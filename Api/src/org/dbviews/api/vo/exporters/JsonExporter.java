package org.dbviews.api.vo.exporters;

import java.io.IOException;
import java.io.Writer;

import java.sql.Connection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import org.dbviews.api.database.Connector;
import org.dbviews.api.vo.Header;
import org.dbviews.api.vo.Item;
import org.dbviews.api.vo.Item.RowWriter;
import org.dbviews.model.DbvConnection;

public class JsonExporter extends Exporter {
  private final static Logger logger = Logger.getLogger(JsonExporter.class.getName());

  protected boolean rowsWithColumnName;
  protected boolean skipPropertiesWithNullValue;
  protected ObjectMapper objectMapper;

  public JsonExporter(Appendable writer) {
    this(writer, false, false);
  }

  public JsonExporter(Appendable writer, boolean rowsWithColumnName, boolean skipPropertiesWithNullValue) {
    super(writer);
    this.rowsWithColumnName = rowsWithColumnName;
    this.skipPropertiesWithNullValue = skipPropertiesWithNullValue;
    JsonFactory jsonFactory = new JsonFactory();
    jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    objectMapper = new ObjectMapper(jsonFactory);
  }

  @Override
  public Appendable writeItems(Collection<Item> items) throws IOException {
    Map<DbvConnection, Connection> conMap = new HashMap<>();
    try {
      writer.append("{\"items\":[");
      boolean firstItem = true;
      for (final Item item : items) {
        if (!firstItem)
          writer.append(",");
        writer.append("{");
        if (!rowsWithColumnName) {
          writer.append("\"headers\":");
          writeValue(writer, item.getHeaders());
          writer.append(",");
        }
        writer.append("\"rows\":[");
        if (item.isRowsFetched()) {
          boolean firstRow = true;
          for (Map<Integer, Object> row : item.getRows()) {
            if (!firstRow)
              writer.append(",");
            writeRow(item, row);
            firstRow = false;
          }
        } else {
          DbvConnection dbvConn = item.getDbvConnection();
          Connection con = conMap.get(dbvConn);
          if (con == null) {
            con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
            con.setReadOnly(true);
            conMap.put(dbvConn, con);
          }
          final boolean[] firstRow = { true };
          item.fetchRows(1, Integer.MAX_VALUE - 1, con, new RowWriter() {
            @Override
            public void write(Map<Integer, Object> row) throws IOException {
              if (!firstRow[0]) {
                writer.append(",");
              } else {
                firstRow[0] = false;
              }
              writeRow(item, row);
            }
          });
        }
        writer.append("]}");
        firstItem = false;
      }
      writer.append("]}");
    } catch (Exception e) {
      e.printStackTrace();
      logger.severe(e.getMessage());
    } finally {
      for (Connection con : conMap.values())
        Connector.relres(con);
    }
    return writer;
  }

  @Override
  protected Appendable writeRow(Item item, Map<Integer, Object> row) throws IOException {
    Map rowToWrite = row;
    if (rowsWithColumnName) {
      Map<String, Object> rowWithColumnName = new HashMap<>(row.size());
      List<Header> headers = item.getHeaders();
      for (Map.Entry<Integer, Object> e : row.entrySet()) {
        if (e.getValue() == null && skipPropertiesWithNullValue)
          continue;
        Integer propIndex = e.getKey();
        Header header = headers.get(propIndex - 1);
        rowWithColumnName.put(header.getColumnName(), e.getValue());
      }
      rowToWrite = rowWithColumnName;
    } else if (skipPropertiesWithNullValue) {
      row.values().removeAll(Collections.singleton(null));
    }
    writeValue(writer, rowToWrite);
    return writer;
  }

  protected Appendable writeValue(Appendable writer, Object value) throws IOException {
    if (writer instanceof Writer) {
      objectMapper.writeValue((Writer) writer, value);
    } else {
      writer.append(objectMapper.writeValueAsString(value));
    }
    return writer;
  }
}
