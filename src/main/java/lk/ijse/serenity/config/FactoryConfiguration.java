package lk.ijse.serenity.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private static SessionFactory sessionFactory;
    private FactoryConfiguration() {
        try {
            Properties props = new Properties();
            props.load(Files.newInputStream(Paths.get("hibernate.properties")));
            Configuration cfg = new Configuration().setProperties(props);

            sessionFactory = cfg.buildSessionFactory();
        }catch (Exception ignored){
            System.out.println("Unable to load hibernate.properties");
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