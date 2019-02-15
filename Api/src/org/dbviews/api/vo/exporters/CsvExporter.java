package org.dbviews.api.vo.exporters;

import java.io.IOException;

import java.sql.Connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import org.dbviews.api.database.Connector;
import org.dbviews.api.vo.Header;
import org.dbviews.api.vo.Item;
import org.dbviews.api.vo.Item.RowWriter;
import org.dbviews.model.DbvConnection;

public class CsvExporter extends Exporter {
  private final static Logger logger = Logger.getLogger(CsvExporter.class.getName());

  public CsvExporter(Appendable writer) {
    super(writer);
  }

  @Override
  public Appendable writeItems(Collection<Item> items) throws IOException {
    Map<DbvConnection, Connection> conMap = new HashMap<>();
    try {
      for (final Item item : items) {
        String separator = StringUtils.defaultIfEmpty(item.getCsvSeparator(), "|");
        boolean addSeparator = false;
        for (Header header : item.getHeaders()) {
          if (!header.isExportable())
            continue;
          if (addSeparator) {
            writer.append(separator);
          } else {
            addSeparator = true;
          }
          writer.append(StringEscapeUtils.escapeCsv(header.getColumnName()));
        }
        writer.append("\r\n");

        if (item.isRowsFetched()) {
          for (Map<Integer, Object> row : item.getRows()) {
            writeRow(item, row);
          }
        } else {
          DbvConnection dbvConn = item.getDbvConnection();
          Connection con = conMap.get(dbvConn);
          if (con == null) {
            con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
            con.setReadOnly(true);
            conMap.put(dbvConn, con);
          }
          item.fetchRows(1, Integer.MAX_VALUE - 1, con, new RowWriter() {
            @Override
            public void write(Map<Integer, Object> row) throws IOException {
              writeRow(item, row);
            }
          });
        }
      }
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
    String separator = StringUtils.defaultIfEmpty(item.getCsvSeparator(), "|");
    boolean addSeparator = false;
    for (Header header : item.getHeaders()) {
      if (!header.isExportable())
        continue;
      if (addSeparator) {
        writer.append(separator);
      } else {
        addSeparator = true;
      }
      Object value = row.get(header.getId());
      if (value != null)
        writer.append(StringEscapeUtils.escapeCsv(value.toString()));
    }
    writer.append("\r\n");
    return writer;
  }
}
