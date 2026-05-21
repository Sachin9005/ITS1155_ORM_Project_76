package lk.ijse.serenity.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import lk.ijse.serenity.bo.PaymentBOImpl;
import lk.ijse.serenity.bo.TherapistBOImpl;
import lk.ijse.serenity.bo.TherapyProgramBOImpl;
import lk.ijse.serenity.bo.TherapySessionBOImpl;
import lk.ijse.serenity.dto.TherapistDTO;
import lk.ijse.serenity.dto.TherapyProgramDTO;
import lk.ijse.serenity.dto.TherapySessionDTO;
import lk.ijse.serenity.entity.TherapySession;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportPanelController {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final TherapySessionBOImpl sessSvc = new TherapySessionBOImpl();
    private final TherapistBOImpl therapistSvc = new TherapistBOImpl();
    private final TherapyProgramBOImpl progSvc = new TherapyProgramBOImpl();
    private final PaymentBOImpl paymentSvc = new PaymentBOImpl();
    private final ObservableList<TherapySessionDTO> allSessions = FXCollections.observableArrayList();
    private final ObservableList<TherapySessionDTO> historyData = FXCollections.observableArrayList();
    // KPI labels
    @FXML
    private Label kpiSessions, kpiCompleted, kpiCancelled, kpiRevenue;
    // Therapist performance table
    @FXML
    private TableView<TherapistDTO> therapistTable;
    @FXML
    private TableColumn<TherapistDTO, String> colTName, colTSpec, colTTotal, colTDone;
    // Program enrollment panel
    @FXML
    private VBox programStats;
    // Patient history table
    @FXML
    private TableView<TherapySessionDTO> historyTable;
    @FXML
    private TableColumn<TherapySessionDTO, String> colHPatient, colHProgram, colHTherapist, colHDate, colHStatus;
    @FXML
    private TextField searchPatient;

    @FXML
    public void initialize() {
        loadKpis();
        setupTherapistTable();
        loadProgramStats();
        setupHistoryTable();
    }

    // ── KPIs ──────────────────────────────────────────────────────────────────

    private void loadKpis() {
        List<TherapySessionDTO> all = sessSvc.getAllSessions();
        allSessions.setAll(all);

        kpiSessions.setText(String.valueOf(all.size()));

        long completed = all.stream()
                .filter(s -> s.getStatus() == TherapySession.Status.COMPLETED).count();
        kpiCompleted.setText(String.valueOf(completed));

        long cancelled = all.stream()
                .filter(s -> s.getStatus() == TherapySession.Status.CANCELLED).count();
        kpiCancelled.setText(String.valueOf(cancelled));

        kpiRevenue.setText(String.format("%,.2f", paymentSvc.totalRevenue()));
    }

    // ── Therapist Performance ─────────────────────────────────────────────────

    private void setupTherapistTable() {
        // Group sessions by therapist for counts
        Map<Long, Long> totalMap = allSessions.stream()
                .collect(Collectors.groupingBy(s -> s.getTherapist().getId(), Collectors.counting()));
        Map<Long, Long> doneMap = allSessions.stream()
                .filter(s -> s.getStatus() == TherapySession.Status.COMPLETED)
                .collect(Collectors.groupingBy(s -> s.getTherapist().getId(), Collectors.counting()));

        colTName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colTSpec.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSpecialization() != null ? d.getValue().getSpecialization() : "—"));
        colTTotal.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(totalMap.getOrDefault(d.getValue().getId(), 0L))));
        colTDone.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(doneMap.getOrDefault(d.getValue().getId(), 0L))));

        therapistTable.setItems(FXCollections.observableArrayList(therapistSvc.getAllTherapists()));
        therapistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Program Enrollment Stats ───────────────────────────────────────────────

    private void loadProgramStats() {
        programStats.getChildren().clear();

        Map<String, Long> countByProgram = allSessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getTherapyProgram().getName(), Collectors.counting()));

        long maxCount = countByProgram.values().stream().mapToLong(Long::longValue).max().orElse(1L);

        for (TherapyProgramDTO p : progSvc.getAllTherapyPrograms()) {
            long count = countByProgram.getOrDefault(p.getName(), 0L);
            double pct = maxCount > 0 ? (count * 100.0 / maxCount) : 0;

            VBox row = new VBox(4);
            HBox header = new HBox();
            Label nameLabel = new Label(p.getName());
            nameLabel.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:#1a3a3c;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);
            Label countLabel = new Label(count + " sessions");
            countLabel.setStyle("-fx-font-size:11px;-fx-text-fill:#7a8a95;");
            header.getChildren().addAll(nameLabel, countLabel);

            // Progress bar
            ProgressBar bar = new ProgressBar(pct / 100.0);
            bar.setPrefWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-accent:#1a6b72;-fx-pref-height:8px;");

            row.getChildren().addAll(header, bar);
            programStats.getChildren().add(row);
        }

        if (progSvc.getAllTherapyPrograms().isEmpty()) {
            Label empty = new Label("No programs found.");
            empty.setStyle("-fx-text-fill:#7a8a95;-fx-font-size:12px;");
            programStats.getChildren().add(empty);
        }
    }

    // ── Patient Therapy History (Read-Only) ───────────────────────────────────

    private void setupHistoryTable() {
        colHPatient.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPatient().getName()));
        colHProgram.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTherapyProgram().getName()));
        colHTherapist.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTherapist().getName()));
        colHDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getScheduledAt().format(DT_FMT)));
        colHStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus().name()));

        historyData.setAll(allSessions);
        historyTable.setItems(historyData);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setEditable(false);
    }

    @FXML
    private void filterHistory() {
        String kw = searchPatient.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            historyData.setAll(allSessions);
        } else {
            historyData.setAll(allSessions.stream()
                    .filter(s -> s.getPatient().getName().toLowerCase().contains(kw))
                    .collect(Collectors.toList()));
        }
    }
}
