package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CrudDAOImpl<T> {

    private Class<T> entityClass;

    public CrudDAOImpl() {}

    public CrudDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean save(T entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return  true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return  false;
        }finally {
            session.close();
        }
    }

    public boolean update(T entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }finally {
            session.close();
        }
    }
    public boolean delete(T entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // Merge to re-attach detached entity, then remove
            T merged = session.merge(entity);
            session.remove(merged);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return  false;
        }finally {
            session.close();
        }
    }

    public List<T> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            if (entityClass == null) {
                throw new RuntimeException("Entity class not set for CrudDAOImpl.getAll(). " +
                        "Call setEntityClass() in the subclass constructor.");
            }
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            session.close();
        }
    }
}
