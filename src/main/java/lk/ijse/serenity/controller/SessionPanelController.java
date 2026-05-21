package lk.ijse.serenity.controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.serenity.bo.PatientBOImpl;
import lk.ijse.serenity.bo.TherapistBOImpl;
import lk.ijse.serenity.bo.TherapyProgramBOImpl;
import lk.ijse.serenity.bo.TherapySessionBOImpl;
import lk.ijse.serenity.dto.PatientDTO;
import lk.ijse.serenity.dto.TherapistDTO;
import lk.ijse.serenity.dto.TherapyProgramDTO;
import lk.ijse.serenity.dto.TherapySessionDTO;
import lk.ijse.serenity.entity.TherapySession;
import lk.ijse.serenity.exception.SerenityException;
import lk.ijse.serenity.util.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SessionPanelController {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final TherapySessionBOImpl sessSvc = new TherapySessionBOImpl();
    private final PatientBOImpl patientSvc = new PatientBOImpl();
    private final TherapistBOImpl therapistSvc = new TherapistBOImpl();
    private final TherapyProgramBOImpl progSvc = new TherapyProgramBOImpl();
    private final ObservableList<TherapySessionDTO> allData = FXCollections.observableArrayList();
    private final ObservableList<TherapySessionDTO> viewData = FXCollections.observableArrayList();
    @FXML
    private TableView<TherapySessionDTO> sessionTable;
    @FXML
    private TableColumn<TherapySessionDTO, Long> colId;
    @FXML
    private TableColumn<TherapySessionDTO, String> colPatient, colTherapist, colProgram, colDate, colStatus;
    @FXML
    private TableColumn<TherapySessionDTO, Void> colActions;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TextField patientFilter;
    @FXML
    private Label countLabel;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private ComboBox<PatientDTO> fPatient;
    @FXML
    private ComboBox<TherapyProgramDTO> fProgram;
    @FXML
    private ComboBox<TherapistDTO> fTherapist;
    @FXML
    private DatePicker fDate;
    @FXML
    private TextField fTime, fNotes;

    private TherapySessionDTO reschedulingSession = null;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadComboBoxes();
        refresh();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleLongProperty(
                d.getValue().getId()).asObject());
        colPatient.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPatient().getName()));
        colTherapist.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTherapist().getName()));
        colProgram.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTherapyProgram().getName()));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getScheduledAt().format(DT_FMT)));
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus().name()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button reschedBtn = new Button("📅 Reschedule");
            private final Button cancelBtn = new Button("✕ Cancel");
            private final Button doneBtn = new Button("✔ Complete");
            private final HBox box = new HBox(5, reschedBtn, cancelBtn, doneBtn);

            {
                reschedBtn.getStyleClass().addAll("btn-warning", "btn-small");
                cancelBtn.getStyleClass().addAll("btn-danger", "btn-small");
                doneBtn.getStyleClass().addAll("btn-secondary", "btn-small");
                reschedBtn.setOnAction(e -> openReschedule(getTableView().getItems().get(getIndex())));
                cancelBtn.setOnAction(e -> cancelSession(getTableView().getItems().get(getIndex())));
                doneBtn.setOnAction(e -> completeSession(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                TherapySessionDTO s = getTableView().getItems().get(getIndex());
                boolean active = s.getStatus() == TherapySession.Status.SCHEDULED
                        || s.getStatus() == TherapySession.Status.RESCHEDULED;
                reschedBtn.setDisable(!active);
                cancelBtn.setDisable(!active);
                doneBtn.setDisable(!active);
                setGraphic(box);
            }
        });

        sessionTable.setItems(viewData);
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "ALL", "SCHEDULED", "COMPLETED", "CANCELLED", "RESCHEDULED"));
        statusFilter.setValue("ALL");
    }

    private void loadComboBoxes() {
        fPatient.setItems(FXCollections.observableArrayList(patientSvc.getAllPatients()));
        fProgram.setItems(FXCollections.observableArrayList(progSvc.getAllTherapyPrograms()));
        fTherapist.setItems(FXCollections.observableArrayList(therapistSvc.getAllTherapists()));
    }

    private void refresh() {
        allData.setAll(sessSvc.getAllSessions());
        applyFilter();
    }

    @FXML
    private void applyFilter() {
        String status = statusFilter.getValue();
        String name = patientFilter.getText().toLowerCase().trim();
        List<TherapySessionDTO> filtered = allData.stream()
                .filter(s -> status == null || status.equals("ALL") || s.getStatus().name().equals(status))
                .filter(s -> name.isEmpty() || s.getPatient().getName().toLowerCase().contains(name))
                .collect(Collectors.toList());
        viewData.setAll(filtered);
        countLabel.setText(filtered.size() + " sessions");
    }

    @FXML
    private void clearFilter() {
        statusFilter.setValue("ALL");
        patientFilter.clear();
        applyFilter();
    }

    @FXML
    private void openBookDialog() {
        reschedulingSession = null;
        formTitle.setText("Book New Session");
        clearForm();
        showForm(true);
    }

    private void openReschedule(TherapySessionDTO s) {
        reschedulingSession = s;
        formTitle.setText("Reschedule — " + s.getPatient().getName());

        fPatient.setValue(Converter.toPatientDTO(s.getPatient()));
        fPatient.setDisable(true);
        fProgram.setValue(Converter.toTherapyProgramDTO(s.getTherapyProgram()));
        fProgram.setDisable(true);
        fTherapist.setValue(Converter.toTherapistDTO(s.getTherapist()));
        fDate.setValue(s.getScheduledAt().toLocalDate());
        fTime.setText(s.getScheduledAt().format(DateTimeFormatter.ofPattern("HH:mm")));
        showForm(true);
    }

    @FXML
    private void saveSession() {
        formError.setText("");
        try {
            // Parse time
            String timeStr = fTime.getText().trim();
            if (!timeStr.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
                formError.setText("❌ Time must be in HH:MM format (e.g. 09:30)");
                return;
            }
            LocalDate date = fDate.getValue();
            if (date == null) {
                formError.setText("❌ Please select a date.");
                return;
            }
            LocalDateTime dt = LocalDateTime.of(date, LocalTime.parse(timeStr));

            if (reschedulingSession == null) {
                // New booking
                if (fPatient.getValue() == null) {
                    formError.setText("❌ Select a patient.");
                    return;
                }
                if (fProgram.getValue() == null) {
                    formError.setText("❌ Select a program.");
                    return;
                }
                if (fTherapist.getValue() == null) {
                    formError.setText("❌ Select a therapist.");
                    return;
                }
                TherapySessionDTO s = TherapySessionDTO.builder()
                        .patient(Converter.toPatientEntity(fPatient.getValue()))
                        .therapyProgram(Converter.toTherapyProgramEntity(fProgram.getValue()))
                        .therapist(Converter.toTherapistEntity(fTherapist.getValue()))
                        .scheduledAt(dt)
                        .build();
                boolean isBooked = sessSvc.book(s);
                if (!isBooked) {
                    throw new SerenityException("Scheduling conflict: Therapist or patient is not available at this time.");
                }
                new Alert(Alert.AlertType.INFORMATION, "Session booked successfully!").showAndWait();
            } else {
                boolean isRescheduled = sessSvc.reschedule(reschedulingSession, dt);
                if (!isRescheduled) {
                    throw new SerenityException("Scheduling conflict: Therapist or patient is not available at this time.");
                }
                new Alert(Alert.AlertType.INFORMATION, "Session rescheduled successfully!").showAndWait();
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void cancelSession(TherapySessionDTO s) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this session?").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean isCanceled = sessSvc.cancel(s);
            if (!isCanceled) {
                new Alert(Alert.AlertType.ERROR, "Failed to cancel session. It may have already been completed or canceled.").showAndWait();
                return;
            }
            refresh();
        }
    }

    private void completeSession(TherapySessionDTO s) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Mark this session as completed?").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean isCompleted = sessSvc.complete(s);
            if (!isCompleted) {
                new Alert(Alert.AlertType.ERROR, "Failed to complete session.").showAndWait();
                return;
            }
            refresh();
        }
    }

    @FXML
    private void closeForm() {
        showForm(false);
        clearForm();
    }

    private void showForm(boolean show) {
        formCard.setVisible(show);
        formCard.setManaged(show);
    }

    private void clearForm() {
        fPatient.setValue(null);
        fPatient.setDisable(false);
        fProgram.setValue(null);
        fProgram.setDisable(false);
        fTherapist.setValue(null);
        fDate.setValue(null);
        fTime.clear();
        fNotes.clear();
        formError.setText("");
        reschedulingSession = null;
    }
}
