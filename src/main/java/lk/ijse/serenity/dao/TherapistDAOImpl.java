package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.Therapist;
import org.hibernate.Session;

import java.util.List;

public class TherapistDAOImpl extends  CrudDAOImpl<Therapist>{

    public List<Therapist> findAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                    "SELECT DISTINCT t FROM Therapist t LEFT JOIN FETCH t.therapyPrograms", Therapist.class).list();
        } finally {
            session.close();
        }
    }

}
