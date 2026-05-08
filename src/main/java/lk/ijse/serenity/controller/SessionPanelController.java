package lk.ijse.serenity.controller;

import com.serenity.entity.*;
import com.serenity.exception.SerenityException;
import com.serenity.bo.*;
import com.serenity.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SessionPanelController {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final TherapySessionBO sessSvc = TherapySessionBO.getInstance();
    private final PatientBO patientSvc = PatientBO.getInstance();
    private final TherapistBO therapistSvc = TherapistBO.getInstance();
    private final TherapyProgramBO progSvc = TherapyProgramBO.getInstance();
    private final ObservableList<TherapySession> allData = FXCollections.observableArrayList();
    private final ObservableList<TherapySession> viewData = FXCollections.observableArrayList();
    @FXML
    private TableView<TherapySession> sessionTable;
    @FXML
    private TableColumn<TherapySession, Long> colId;
    @FXML
    private TableColumn<TherapySession, String> colPatient, colTherapist, colProgram, colDate, colStatus;
    @FXML
    private TableColumn<TherapySession, Void> colActions;
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
    private ComboBox<Patient> fPatient;
    @FXML
    private ComboBox<TherapyProgram> fProgram;
    @FXML
    private ComboBox<Therapist> fTherapist;
    @FXML
    private DatePicker fDate;
    @FXML
    private TextField fTime, fNotes;
    private TherapySession reschedulingSession = null;

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
                TherapySession s = getTableView().getItems().get(getIndex());
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
        fPatient.setItems(FXCollections.observableArrayList(patientSvc.findAll()));
        fProgram.setItems(FXCollections.observableArrayList(progSvc.findAll()));
        fTherapist.setItems(FXCollections.observableArrayList(therapistSvc.findAll()));
    }

    private void refresh() {
        allData.setAll(sessSvc.findAll());
        applyFilter();
    }

    @FXML
    private void applyFilter() {
        String status = statusFilter.getValue();
        String name = patientFilter.getText().toLowerCase().trim();
        List<TherapySession> filtered = allData.stream()
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

    private void openReschedule(TherapySession s) {
        reschedulingSession = s;
        formTitle.setText("Reschedule — " + s.getPatient().getName());
        fPatient.setValue(s.getPatient());
        fPatient.setDisable(true);
        fProgram.setValue(s.getTherapyProgram());
        fProgram.setDisable(true);
        fTherapist.setValue(s.getTherapist());
        fDate.setValue(s.getScheduledAt().toLocalDate());
        fTime.setText(s.getScheduledAt().format(DateTimeFormatter.ofPattern("HH:mm")));
        fNotes.setText(s.getNotes() != null ? s.getNotes() : "");
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
                sessSvc.book(fPatient.getValue(), fTherapist.getValue(),
                        fProgram.getValue(), dt, fNotes.getText());
                AlertHelper.showSuccess("Booked", "Session booked successfully.");
            } else {
                sessSvc.reschedule(reschedulingSession, dt);
                AlertHelper.showSuccess("Rescheduled", "Session has been rescheduled.");
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void cancelSession(TherapySession s) {
        if (AlertHelper.confirm("Cancel Session",
                "Cancel the session for '" + s.getPatient().getName() + "' on "
                        + s.getScheduledAt().format(DT_FMT) + "?")) {
            sessSvc.cancel(s);
            refresh();
        }
    }

    private void completeSession(TherapySession s) {
        if (AlertHelper.confirm("Mark Complete",
                "Mark this session as completed?")) {
            sessSvc.complete(s);
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
