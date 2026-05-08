package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public abstract class CrudDAO<T> {

    protected final Class<T> entityClass;

    protected CrudDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        Transaction tx = null;
        try (Session session = FactoryConfiguration.getSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new SerenityException("Save failed: " + e.getMessage(), e);
        }
    }

    public T update(T entity) {
        Transaction tx = null;
        try (Session session = FactoryConfiguration.getSession()) {
            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new SerenityException("Update failed: " + e.getMessage(), e);
        }
    }

    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = FactoryConfiguration.getSession()) {
            tx = session.beginTransaction();
            T managed = session.merge(entity);
            session.remove(managed);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new SerenityException("Delete failed: " + e.getMessage(), e);
        }
    }

    public Optional<T> findById(Long id) {
        try (Session session = FactoryConfiguration.getSession()) {
            return Optional.ofNullable(session.get(entityClass, id));
        }
    }

    public List<T> findAll() {
        try (Session session = FactoryConfiguration.getSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    protected Session openSession() {
        return FactoryConfiguration.getSession();
    }
}
