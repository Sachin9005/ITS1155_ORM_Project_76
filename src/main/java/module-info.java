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

    // Open root package to FX
    opens lk.ijse.serenity to javafx.fxml;
    exports lk.ijse.serenity;

    // Open controller package to FX
    opens lk.ijse.serenity.controller to javafx.fxml;
    exports lk.ijse.serenity.controller;

    // Open entity package to Hibernate for reflection
    opens lk.ijse.serenity.entity to org.hibernate.orm.core, javafx.base;
    exports lk.ijse.serenity.entity;

    // Open DTO package to javafx.base for PropertyValueFactory
    opens lk.ijse.serenity.dto to javafx.base;
    exports lk.ijse.serenity.dto;

    // Export remaining packages
    exports lk.ijse.serenity.bo;
    exports lk.ijse.serenity.dao;
    exports lk.ijse.serenity.config;
    exports lk.ijse.serenity.exception;
    exports lk.ijse.serenity.util;
}