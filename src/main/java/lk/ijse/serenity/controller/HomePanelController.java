package lk.ijse.serenity.controller;

import com.serenity.entity.*;
import com.serenity.bo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomePanelController {

    private final PatientBO patientSvc = PatientBO.getInstance();
    private final TherapistBO therapistSvc = TherapistBO.getInstance();
    private final TherapySessionBO sessSvc = TherapySessionBO.getInstance();
    private final PaymentBO paymentSvc = PaymentBO.getInstance();
    private final TherapyProgramBO progSvc = TherapyProgramBO.getInstance();
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label statPatients, statTherapists, statSessions, statRevenue;
    @FXML
    private TableView<TherapySession> recentSessionsTable;
    @FXML
    private TableColumn<TherapySession, String> colSPatient, colSTherapist, colSProgram, colSDate, colSStatus;
    @FXML
    private VBox programsList;
    @FXML
    private TableView<Patient> allProgramsTable;
    @FXML
    private TableColumn<Patient, String> colApName, colApEmail, colApPhone;

    @FXML
    public void initialize() {
        String user = UserBO.getInstance().getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome back, " + user + " · " + LocalDate.now());

        loadStats();
        setupRecentSessions();
        loadPrograms();
        loadAllProgramsQuery();
    }

    private void loadStats() {
        statPatients.setText(String.valueOf(patientSvc.findAll().size()));
        statTherapists.setText(String.valueOf(therapistSvc.findAll().size()));

        long todaySessions = sessSvc.findAll().stream()
                .filter(s -> s.getScheduledAt().toLocalDate().equals(LocalDate.now()))
                .count();
        statSessions.setText(String.valueOf(todaySessions));

        BigDecimal rev = paymentSvc.totalRevenue();
        statRevenue.setText(String.format("%,.0f", rev));
    }

    private void setupRecentSessions() {
        colSPatient.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getPatient().getName()));
        colSTherapist.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getTherapist().getName()));
        colSProgram.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getTherapyProgram().getName()));
        colSDate.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))));
        colSStatus.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().name()));

        List<TherapySession> all = sessSvc.findAll();
        List<TherapySession> recent = all.stream().limit(10).toList();
        recentSessionsTable.setItems(FXCollections.observableArrayList(recent));
        recentSessionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadPrograms() {
        programsList.getChildren().clear();
        for (TherapyProgram p : progSvc.findAll()) {
            VBox card = new VBox(2);
            card.setStyle("-fx-background-color:#f0f9fa;-fx-background-radius:8;-fx-padding:8 12;");
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-weight:bold;-fx-font-size:12px;-fx-text-fill:#1a3a3c;");
            Label info = new Label(p.getProgramId() + "  ·  " + p.getDuration()
                    + "  ·  LKR " + String.format("%,.0f", p.getFee()));
            info.setStyle("-fx-font-size:11px;-fx-text-fill:#7a8a95;");
            card.getChildren().addAll(name, info);
            programsList.getChildren().add(card);
        }
    }

    private void loadAllProgramsQuery() {
        colApName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colApEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colApPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        List<Patient> result = patientSvc.findPatientsEnrolledInAllPrograms();
        allProgramsTable.setItems(FXCollections.observableArrayList(result));
        allProgramsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (result.isEmpty()) {
            allProgramsTable.setPlaceholder(
                    new Label("No patients are enrolled in every therapy program yet."));
        }
    }
}
