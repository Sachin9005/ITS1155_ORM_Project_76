package lk.ijse.serenity.config;

import lk.ijse.serenity.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private static SessionFactory sessionFactory;
    private FactoryConfiguration() {
        try {
            Properties props = new Properties();
            InputStream is = FactoryConfiguration.class.getClassLoader()
                    .getResourceAsStream("hibernate.properties");
            if (is != null) {
                props.load(is);
                is.close();
            } else {
                System.out.println("hibernate.properties not found on classpath!");
            }
            Configuration cfg = new Configuration().setProperties(props);

            cfg.addAnnotatedClass(Patient.class);
            cfg.addAnnotatedClass(Therapist.class);
            cfg.addAnnotatedClass(TherapyProgram.class);
            cfg.addAnnotatedClass(TherapySession.class);
            cfg.addAnnotatedClass(Payment.class);
            cfg.addAnnotatedClass(User.class);

            sessionFactory = cfg.buildSessionFactory();
        }catch (Exception e){
            System.out.println("Unable to load hibernate configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static FactoryConfiguration getInstance() {
        return factoryConfiguration == null ? factoryConfiguration = new FactoryConfiguration() : factoryConfiguration;
    }

    public Session getSession(){
        //1st level cashing
        return sessionFactory.openSession();
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}