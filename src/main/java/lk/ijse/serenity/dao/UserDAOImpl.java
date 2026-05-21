package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class UserDAOImpl extends CrudDAOImpl<User> {

    public Optional<User> findByUsername(String username) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<User> q = session.createQuery(
                    "FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return q.uniqueResultOptional();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByUsername(String username) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username).uniqueResult();
            return count != null && count > 0;
        }
    }
}
