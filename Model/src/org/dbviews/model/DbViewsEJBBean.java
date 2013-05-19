package org.dbviews.model;

import java.util.List;

import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.ServerSession;

@Stateless(name="DbViewsEJB", mappedName = "DbViewsEJB")
public class DbViewsEJBBean
  implements DbViewsEJB
{
  @Resource
  SessionContext sessionContext;
  @PersistenceContext(unitName = "Model")
  private EntityManager em;

  public DbViewsEJBBean()
  {
  }

  public Object queryByRange(String jpqlStmt, int firstResult, int maxResults)
  {
    Query query = em.createQuery(jpqlStmt);
    if (firstResult > 0)
    {
      query = query.setFirstResult(firstResult);
    }
    if (maxResults > 0)
    {
      query = query.setMaxResults(maxResults);
    }
    return query.getResultList();
  }

  public DbvView persistDbvView(DbvView dbvView)
  {
    em.persist(dbvView);
    return dbvView;
  }

  public DbvView mergeDbvView(DbvView dbvView)
  {
    return em.merge(dbvView);
  }

  public void removeDbvView(DbvView dbvView)
  {
    dbvView = em.find(DbvView.class, dbvView.getId());
    em.remove(dbvView);
  }

  /** <code>select o from DbvView o</code> */
  public List<DbvView> getDbvViewFindAll()
  {
    return em.createNamedQuery("DbvView.findAll").getResultList();
  }

  public DbvConnection persistDbvConnection(DbvConnection dbvConnection)
  {
    em.persist(dbvConnection);
    return dbvConnection;
  }

  public DbvConnection mergeDbvConnection(DbvConnection dbvConnection)
  {
    return em.merge(dbvConnection);
  }

  public void removeDbvConnection(DbvConnection dbvConnection)
  {
    dbvConnection = em.find(DbvConnection.class, dbvConnection.getId());
    em.remove(dbvConnection);
  }

  /** <code>select o from DbvConnection o</code> */
  public List<DbvConnection> getDbvConnectionFindAll()
  {
    return em.createNamedQuery("DbvConnection.findAll").getResultList();
  }

  public DbvTable persistDbvTable(DbvTable dbvTable)
  {
    em.persist(dbvTable);
    return dbvTable;
  }

  public DbvTable mergeDbvTable(DbvTable dbvTable)
  {
    return em.merge(dbvTable);
  }

  public void removeDbvTable(DbvTable dbvTable)
  {
    dbvTable = em.find(DbvTable.class, dbvTable.getId());
    em.remove(dbvTable);
  }

  /** <code>select o from DbvTable o</code> */
  public List<DbvTable> getDbvTableFindAll()
  {
    return em.createNamedQuery("DbvTable.findAll").getResultList();
  }

  public DbvGraph persistDbvGraph(DbvGraph dbvGraph)
  {
    em.persist(dbvGraph);
    return dbvGraph;
  }

  public DbvGraph mergeDbvGraph(DbvGraph dbvGraph)
  {
    return em.merge(dbvGraph);
  }

  public void removeDbvGraph(DbvGraph dbvGraph)
  {
    dbvGraph = em.find(DbvGraph.class, dbvGraph.getId());
    em.remove(dbvGraph);
  }

  /** <code>select o from DbvGraph o</code> */
  public List<DbvGraph> getDbvGraphFindAll()
  {
    return em.createNamedQuery("DbvGraph.findAll").getResultList();
  }

  /** <code>select o from DbvConnection o where o.id = :id</code> */
  public DbvConnection getDbvConnectionFindById(int id)
  {
    try
    {
      return (DbvConnection)em.createNamedQuery("DbvConnection.findById").setParameter("id", id).setMaxResults(1).getSingleResult();
    }
    catch (NoResultException e)
    {
      return null;
    }
  }

  /** <code>select o from DbvView o where o.id = :id</code> */
  public DbvView getDbvViewFindById(int id)
  {
    try
    {
      return (DbvView)em.createNamedQuery("DbvView.findById").setParameter("id", id).setMaxResults(1).getSingleResult();
    }
    catch (NoResultException e)
    {
      return null;
    }
  }

  /** <code>select o from DbvTable o where o.id = :id</code> */
  public DbvTable getDbvTableFindById(int id)
  {
    try
    {
      return (DbvTable)em.createNamedQuery("DbvTable.findById").setParameter("id", id).setMaxResults(1).getSingleResult();
    }
    catch (NoResultException e)
    {
      return null;
    }
  }

  public void clearCache(Class... classes)
  {
    JpaEntityManager jpaEm = (JpaEntityManager)em.getDelegate();
    ServerSession ss = jpaEm.getServerSession();
    IdentityMapAccessor ima = ss.getIdentityMapAccessor();
    if (classes.length == 0)
    {
      ima.initializeAllIdentityMaps();
      return;
    }
    for (Class c : classes)
      ima.initializeIdentityMap(c);
  }

  /** <code>select o from DbvGraph o where o.id = :id</code> */
  public DbvGraph getDbvGraphFindById(int id)
  {
    try
    {
      return (DbvGraph)em.createNamedQuery("DbvGraph.findById").setParameter("id", id).setMaxResults(1).getSingleResult();
    }
    catch (NoResultException e)
    {
      return null;
    }
  }
}
