package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CrudDAOImpl<T> {

    public boolean save(T entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            return  true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
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
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
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
            session.delete(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return  false;
        }finally {
            session.close();
        }
    }

    public List<T> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            List<T> list = session.createQuery("from T").list();
            transaction.commit();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            session.close();
        }

    }
}
