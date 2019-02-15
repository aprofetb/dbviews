package org.dbviews.api.vo.exporters;

import java.io.IOException;

import java.sql.Connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

import org.dbviews.api.database.Connector;
import org.dbviews.api.vo.Header;
import org.dbviews.api.vo.Item;
import org.dbviews.api.vo.Item.RowWriter;
import org.dbviews.model.DbvConnection;

public class HtmlExporter extends Exporter {
  private final static Logger logger = Logger.getLogger(HtmlExporter.class.getName());

  public HtmlExporter(Appendable writer) {
    super(writer);
  }

  @Override
  public Appendable writeItems(Collection<Item> items) throws IOException {
    Map<DbvConnection, Connection> conMap = new HashMap<>();
    try {
      writer.append("<table border='1' style='border:none'>");
      for (final Item item : items) {
        int exp = 0;
        for (Header header : item.getHeaders())
          if (header.isExportable())
            exp++;
        writer.append(String.format("<tr><th align='left' colspan='%d' style='border:none;font-size:18pt'>", exp));
        writer.append(StringEscapeUtils.escapeHtml(item.getLabel()));
        writer.append("</th></tr>");

        writer.append("<tr>");
        for (Header header : item.getHeaders()) {
          if (!header.isExportable())
            continue;
          writer.append(String.format("<th align='%s' valign='%s' style='color:white;background-color:#3366FF'>",
                                      header.getAlign(), header.getVAlign()));
          writer.append(StringEscapeUtils.escapeHtml(header.getColumnName()));
          writer.append("</th>");
        }
        writer.append("</tr>");

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
      writer.append("</table>");
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
    writer.append("<tr>");
    for (Header header : item.getHeaders()) {
      if (!header.isExportable())
        continue;
      Object value = row.get(header.getId());
      /* writer.append(String.format("<td align='%s' valign='%s' style='color:black;background-color:#FFFFCC'>",
                                  header.getAlign(), header.getVAlign())); */
      writer.append("<td>");
      //writer.append(header.getType() == Type.Html ? value : StringEscapeUtils.escapeHtml(value.toString()));
      if (value != null)
        writer.append(StringEscapeUtils.escapeHtml(value.toString()));
      writer.append("</td>");
    }
    writer.append("</tr>");
    return writer;
  }
}
