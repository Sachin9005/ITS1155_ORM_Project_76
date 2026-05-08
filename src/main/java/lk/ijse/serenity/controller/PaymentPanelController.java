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

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentPanelController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final PaymentBO paymentSvc = PaymentBO.getInstance();
    private final PatientBO patientSvc = PatientBO.getInstance();
    private final TherapySessionBO sessSvc = TherapySessionBO.getInstance();
    private final ObservableList<Payment> data = FXCollections.observableArrayList();
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, String> colInvoice, colPatient, colAmount,
            colMethod, colDate, colStatus;
    @FXML
    private TableColumn<Payment, Void> colActions;
    @FXML
    private Label lblRevenue, lblCount, lblPending;
    @FXML
    private VBox formCard;
    @FXML
    private Label formError;
    @FXML
    private ComboBox<Patient> fPatient;
    @FXML
    private ComboBox<TherapySession> fSession;
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
                Payment p = getTableView().getItems().get(getIndex());
                refundBtn.setDisable(p.getStatus() != Payment.Status.COMPLETED);
                setGraphic(box);
            }
        });

        paymentTable.setItems(data);
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupForm() {
        fPatient.setItems(FXCollections.observableArrayList(patientSvc.findAll()));
        fMethod.setItems(FXCollections.observableArrayList(Payment.PaymentMethod.values()));
        fMethod.setValue(Payment.PaymentMethod.CASH);

        // Custom display for sessions
        fSession.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TherapySession s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText(null);
                else setText(s.getTherapyProgram().getName() + " — "
                        + s.getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                        + " [" + s.getStatus() + "]");
            }
        });
        fSession.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TherapySession s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText("Select session");
                else setText(s.getTherapyProgram().getName() + " — "
                        + s.getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
            }
        });
    }

    @FXML
    private void loadPatientSessions() {
        Patient p = fPatient.getValue();
        if (p == null) return;
        List<TherapySession> sessions = sessSvc.findByPatient(p.getId());
        fSession.setItems(FXCollections.observableArrayList(sessions));
        // Auto-fill amount from program fee if session selected
        fSession.setOnAction(e -> {
            TherapySession s = fSession.getValue();
            if (s != null && s.getTherapyProgram().getFee() != null) {
                fAmount.setText(s.getTherapyProgram().getFee().toPlainString());
            }
        });
    }

    private void refresh() {
        List<Payment> all = paymentSvc.findAll();
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
            paymentSvc.process(fPatient.getValue(), fSession.getValue(), amount, fMethod.getValue());
            AlertHelper.showSuccess("Payment Processed",
                    "Payment recorded. Invoice will appear in the table.");
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void printInvoice(Payment p) {
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

    private void refund(Payment p) {
        if (AlertHelper.confirm("Refund Payment",
                "Refund invoice " + p.getInvoiceNumber() + " (LKR "
                        + String.format("%,.2f", p.getAmount()) + ")?")) {
            paymentSvc.refund(p);
            refresh();
        }
    }

    @FXML
    private void closeForm() {
        formCard.setVisible(false);
        formCard.setManaged(false);
    }
}
