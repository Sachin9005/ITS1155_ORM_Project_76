package lk.ijse.serenity.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.serenity.bo.PatientBOImpl;
import lk.ijse.serenity.bo.PaymentBOImpl;
import lk.ijse.serenity.bo.TherapySessionBOImpl;
import lk.ijse.serenity.dto.PatientDTO;
import lk.ijse.serenity.dto.PaymentDTO;
import lk.ijse.serenity.dto.TherapySessionDTO;
import lk.ijse.serenity.entity.Payment;
import lk.ijse.serenity.exception.SerenityException;
import lk.ijse.serenity.util.Converter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentPanelController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final PaymentBOImpl paymentSvc = new PaymentBOImpl();
    private final PatientBOImpl patientSvc = new PatientBOImpl();
    private final TherapySessionBOImpl sessSvc = new TherapySessionBOImpl();

    private final ObservableList<PaymentDTO> data = FXCollections.observableArrayList();
    @FXML
    private TableView<PaymentDTO> paymentTable;
    @FXML
    private TableColumn<PaymentDTO, String> colInvoice, colPatient, colAmount,
            colMethod, colDate, colStatus;
    @FXML
    private TableColumn<PaymentDTO, Void> colActions;
    @FXML
    private Label lblRevenue, lblCount, lblPending;
    @FXML
    private VBox formCard;
    @FXML
    private Label formError;
    @FXML
    private ComboBox<PatientDTO> fPatient;
    @FXML
    private ComboBox<TherapySessionDTO> fSession;
    @FXML
    private ComboBox<Payment.PaymentMethod> fMethod;
    @FXML
    private TextField fAmount;

    @FXML
    public void initialize() {
        setupColumns();
        setupForm();
        refresh();
    }

    private void setupColumns() {
        colInvoice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getInvoiceNumber()));
        colPatient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPatient().getName()));
        colAmount.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("LKR %,.2f", d.getValue().getAmount())));
        colMethod.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPaymentMethod() != null ? d.getValue().getPaymentMethod().name() : "—"));
        colDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPaymentDate().format(DATE_FMT)));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button printBtn = new Button("🖨 Invoice");
            private final Button refundBtn = new Button("↩ Refund");
            private final HBox box = new HBox(6, printBtn, refundBtn);

            {
                printBtn.getStyleClass().addAll("btn-secondary", "btn-small");
                refundBtn.getStyleClass().addAll("btn-danger", "btn-small");
                printBtn.setOnAction(e -> printInvoice(getTableView().getItems().get(getIndex())));
                refundBtn.setOnAction(e -> refund(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                PaymentDTO p = getTableView().getItems().get(getIndex());
                refundBtn.setDisable(p.getStatus() != Payment.Status.COMPLETED);
                setGraphic(box);
            }
        });

        paymentTable.setItems(data);
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupForm() {
        fPatient.setItems(FXCollections.observableArrayList(patientSvc.getAllPatients()));
        fMethod.setItems(FXCollections.observableArrayList(Payment.PaymentMethod.values()));
        fMethod.setValue(Payment.PaymentMethod.CASH);

        // Custom display for sessions
        fSession.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(TherapySessionDTO s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText(null);
                else setText(s.getTherapyProgram().getName() + " — "
                        + s.getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                        + " [" + s.getStatus() + "]");
            }
        });
        fSession.setButtonCell(new ListCell<>() {
            protected void updateItem(TherapySessionDTO s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText("Select session");
                else setText(s.getTherapyProgram().getName() + " — "
                        + s.getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
            }
        });
    }

    @FXML
    private void loadPatientSessions() {
        PatientDTO p = fPatient.getValue();
        if (p == null) return;
        List<TherapySessionDTO> sessions = sessSvc.findByPatient(p.getId());
        fSession.setItems(FXCollections.observableArrayList(sessions));
        // Auto-fill amount from program fee if session selected
        fSession.setOnAction(e -> {
            TherapySessionDTO s = fSession.getValue();
            if (s != null && s.getTherapyProgram().getFee() != null) {
                fAmount.setText(s.getTherapyProgram().getFee().toPlainString());
            }
        });
    }

    private void refresh() {
        List<PaymentDTO> all = paymentSvc.findAll();
        data.setAll(all);
        lblRevenue.setText(String.format("%,.2f", paymentSvc.totalRevenue()));
        lblCount.setText(String.valueOf(all.size()));
        lblPending.setText(String.valueOf(paymentSvc.countPending()));
    }

    @FXML
    private void openPaymentDialog() {
        fPatient.setValue(null);
        fSession.getItems().clear();
        fAmount.clear();
        fMethod.setValue(Payment.PaymentMethod.CASH);
        formError.setText("");
        formCard.setVisible(true);
        formCard.setManaged(true);
    }

    @FXML
    private void processPayment() {
        formError.setText("");
        try {
            if (fPatient.getValue() == null) {
                formError.setText("❌ Select a patient.");
                return;
            }
            if (fSession.getValue() == null) {
                formError.setText("❌ Select a session.");
                return;
            }
            if (fMethod.getValue() == null) {
                formError.setText("❌ Select a payment method.");
                return;
            }
            BigDecimal amount;
            try {
                amount = new BigDecimal(fAmount.getText().trim());
            } catch (NumberFormatException ex) {
                formError.setText("❌ Amount must be a valid number.");
                return;
            }
            PaymentDTO p = PaymentDTO.builder()
                    .patient(Converter.toPatientEntity(fPatient.getValue()))
                    .therapySession(Converter.toTherapySessionEntity(fSession.getValue()))
                    .paymentMethod(fMethod.getValue())
                    .amount(amount)
                    .build();
            boolean isSaved = paymentSvc.savePayment(p);
            if(!isSaved){
                throw new SerenityException("Failed to save payment. Please try again.");
            }
            new Alert(Alert.AlertType.INFORMATION, "Payment processed successfully!").showAndWait();
            formCard.setVisible(false);
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void printInvoice(PaymentDTO p) {
        String invoice =
                "========================================\n" +
                        "  SERENITY MENTAL HEALTH THERAPY CENTER\n" +
                        "========================================\n" +
                        "Invoice No : " + p.getInvoiceNumber() + "\n" +
                        "Date       : " + p.getPaymentDate().format(DATE_FMT) + "\n" +
                        "Patient    : " + p.getPatient().getName() + "\n" +
                        "Method     : " + (p.getPaymentMethod() != null ? p.getPaymentMethod() : "—") + "\n" +
                        "----------------------------------------\n" +
                        "Amount     : LKR " + String.format("%,.2f", p.getAmount()) + "\n" +
                        "Status     : " + p.getStatus() + "\n" +
                        "========================================\n" +
                        "       Thank you for choosing Serenity\n" +
                        "========================================";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice — " + p.getInvoiceNumber());
        alert.setHeaderText(null);
        TextArea ta = new TextArea(invoice);
        ta.setEditable(false);
        ta.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");
        ta.setPrefSize(420, 280);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }

    private void refund(PaymentDTO p) {

        if (new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to refund " + p.getInvoiceNumber() + " this payment?").showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            boolean isSaved = paymentSvc.savePayment(p);
            if(!isSaved){
                new Alert(Alert.AlertType.ERROR, "Failed to process refund. Please try again.").showAndWait();
            }
            refresh();
            new Alert(Alert.AlertType.INFORMATION, "Payment refunded successfully!").showAndWait();
        }
    }

    @FXML
    private void closeForm() {
        formCard.setVisible(false);
        formCard.setManaged(false);
    }
}
