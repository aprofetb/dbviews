package org.dbviews.api.vo.exporters;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;

import org.dbviews.api.vo.Item;

public abstract class Exporter {
  protected Appendable writer;

  public Exporter(Appendable writer) {
    this.writer = writer;
  }

  public void setWriter(Appendable writer) {
    this.writer = writer;
  }

  public Appendable getWriter() {
    return writer;
  }

  public abstract Appendable writeItems(Collection<Item> items) throws IOException;

  protected abstract Appendable writeRow(Item item, Map<Integer, Object> row) throws IOException;
}
