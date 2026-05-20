package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.Patient;
import org.hibernate.Session;

import java.util.List;

public class PatientDAOImpl extends  CrudDAOImpl<Patient>{
    public List<Patient> findPatientsEnrolledInAllPrograms() {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            String hql =
                    "SELECT DISTINCT p FROM Patient p " +
                            "WHERE NOT EXISTS (" +
                            "  SELECT tp FROM TherapyProgram tp " +
                            "  WHERE NOT EXISTS (" +
                            "    SELECT s FROM TherapySession s " +
                            "    WHERE s.patient = p AND s.therapyProgram = tp" +
                            "  )" +
                            ")";
            return session.createQuery(hql, Patient.class).list();
        }
    }

    public List<Patient> search(String keyword) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            String kw = "%" + keyword.toLowerCase() + "%";
            return session.createQuery(
                            "FROM Patient p WHERE LOWER(p.name) LIKE :kw OR LOWER(p.email) LIKE :kw " +
                                    "OR p.phone LIKE :kw", Patient.class)
                    .setParameter("kw", kw).list();
        }
    }
}
