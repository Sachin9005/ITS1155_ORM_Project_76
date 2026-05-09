module lk.ijse.serenity {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    requires org.hibernate.orm.core;
    requires jbcrypt;
    requires jakarta.persistence;
    requires java.sql;
    requires java.naming;
    requires static lombok;

    opens lk.ijse.serenity to javafx.fxml;
    exports lk.ijse.serenity;
}