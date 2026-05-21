package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.Patient;
import lk.ijse.serenity.entity.Therapist;
import lk.ijse.serenity.entity.TherapyProgram;
import lk.ijse.serenity.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class TherapySessionDAOImpl extends CrudDAOImpl<TherapySession>{
    public TherapySessionDAOImpl() { super(TherapySession.class); }

    @Override
    public boolean save(TherapySession entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // Re-attach the associated entities so Hibernate treats them as managed
            entity.setPatient(session.getReference(Patient.class, entity.getPatient().getId()));
            entity.setTherapist(session.getReference(Therapist.class, entity.getTherapist().getId()));
            entity.setTherapyProgram(session.getReference(TherapyProgram.class, entity.getTherapyProgram().getId()));
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public boolean hasConflict(Long therapistId, LocalDateTime proposedTime, Long excludeSessionId) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {

            LocalDateTime start = proposedTime.minusHours(1);
            LocalDateTime end = proposedTime.plusHours(1);

            String hql = """
            SELECT COUNT(s) FROM TherapySession s 
            WHERE s.therapist.id = :therapistId 
              AND s.scheduledAt BETWEEN :start AND :end 
              AND s.status <> 'CANCELLED'
            """;

            if (excludeSessionId != null) {
                hql += " AND s.id <> :excludeId";
            }

            var query = session.createQuery(hql, Long.class)
                    .setParameter("therapistId", therapistId)
                    .setParameter("start", start)
                    .setParameter("end", end);

            if (excludeSessionId != null) {
                query.setParameter("excludeId", excludeSessionId);
            }

            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public List<TherapySession> findByPatient(Long patientId) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            return session.createQuery(
                            "SELECT s FROM TherapySession s " +
                                    "JOIN FETCH s.therapist JOIN FETCH s.therapyProgram " +
                                    "WHERE s.patient.id = :pid ORDER BY s.scheduledAt DESC", TherapySession.class)
                    .setParameter("pid", patientId).list();
        }
    }

    public List<TherapySession> findByTherapist(Long therapistId) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            return session.createQuery(
                            "SELECT s FROM TherapySession s " +
                                    "JOIN FETCH s.patient JOIN FETCH s.therapyProgram " +
                                    "WHERE s.therapist.id = :tid ORDER BY s.scheduledAt", TherapySession.class)
                    .setParameter("tid", therapistId).list();
        }
    }

    public List<TherapySession> findAll() {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            return session.createQuery(
                    "SELECT s FROM TherapySession s " +
                            "JOIN FETCH s.patient JOIN FETCH s.therapist JOIN FETCH s.therapyProgram " +
                            "ORDER BY s.scheduledAt DESC", TherapySession.class).list();
        }
    }
}
