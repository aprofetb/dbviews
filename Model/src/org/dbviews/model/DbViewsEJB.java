package org.dbviews.model;

import java.util.List;

import javax.ejb.Local;

@Local
public interface DbViewsEJB
{
  Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

  DbvView persistDbvView(DbvView dbvView);

  DbvView mergeDbvView(DbvView dbvView);

  void removeDbvView(DbvView dbvView);

  List<DbvView> getDbvViewFindAll();

  DbvConnection persistDbvConnection(DbvConnection dbvConnection);

  DbvConnection mergeDbvConnection(DbvConnection dbvConnection);

  void removeDbvConnection(DbvConnection dbvConnection);

  List<DbvConnection> getDbvConnectionFindAll();

  DbvTable persistDbvTable(DbvTable dbvTable);

  DbvTable mergeDbvTable(DbvTable dbvTable);

  void removeDbvTable(DbvTable dbvTable);

  List<DbvTable> getDbvTableFindAll();

  DbvGraph persistDbvGraph(DbvGraph dbvGraph);

  DbvGraph mergeDbvGraph(DbvGraph dbvGraph);

  void removeDbvGraph(DbvGraph dbvGraph);

  List<DbvGraph> getDbvGraphFindAll();

  DbvConnection getDbvConnectionFindById(int id);

  DbvView getDbvViewFindById(int id);

  DbvTable getDbvTableFindById(int id);

  void clearCache(Class... classes);

  DbvGraph getDbvGraphFindById(int id);

  DbvHtmlBlock getDbvHtmlBlockFindById(int id);
}
