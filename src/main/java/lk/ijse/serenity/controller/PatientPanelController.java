package lk.ijse.serenity.controller;

import com.serenity.entity.Patient;
import com.serenity.exception.SerenityException;
import com.serenity.bo.PatientBO;
import com.serenity.util.AlertHelper;
import com.serenity.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientPanelController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final PatientBO svc = PatientBO.getInstance();
    private final ObservableList<Patient> data = FXCollections.observableArrayList();
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, Long> colId;
    @FXML
    private TableColumn<Patient, String> colName, colEmail, colPhone, colDob, colRegDate;
    @FXML
    private TableColumn<Patient, Void> colActions;
    @FXML
    private TextField searchField;
    @FXML
    private Label countLabel;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private TextField fName, fEmail, fPhone, fAddress, fEmergency;
    @FXML
    private DatePicker fDob;
    @FXML
    private TextArea fMedical;
    private Patient editingPatient = null;

    @FXML
    public void initialize() {
        setupColumns();
        refresh();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDob.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateOfBirth() != null ? d.getValue().getDateOfBirth().format(FMT) : "—"));
        colRegDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getRegistrationDate() != null ? d.getValue().getRegistrationDate().format(FMT) : "—"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button delBtn = new Button("🗑 Delete");
            private final HBox box = new HBox(6, editBtn, delBtn);

            {
                editBtn.getStyleClass().addAll("btn-warning", "btn-small");
                delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                editBtn.setOnAction(e -> openEditDialog(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> deletePatient(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        patientTable.setItems(data);
        patientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refresh() {
        List<Patient> all = svc.findAll();
        data.setAll(all);
        countLabel.setText(all.size() + " patients");
    }

    @FXML
    private void handleSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            refresh();
            return;
        }
        List<Patient> results = svc.search(kw);
        data.setAll(results);
        countLabel.setText(results.size() + " results");
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refresh();
    }

    @FXML
    private void openAddDialog() {
        editingPatient = null;
        formTitle.setText("Add New Patient");
        clearForm();
        showForm(true);
    }

    private void openEditDialog(Patient p) {
        editingPatient = p;
        formTitle.setText("Edit Patient — " + p.getName());
        fName.setText(p.getName());
        fEmail.setText(p.getEmail());
        fPhone.setText(p.getPhone());
        fAddress.setText(p.getAddress() != null ? p.getAddress() : "");
        fEmergency.setText(p.getEmergencyContact() != null ? p.getEmergencyContact() : "");
        fMedical.setText(p.getMedicalHistory() != null ? p.getMedicalHistory() : "");
        fDob.setValue(p.getDateOfBirth());
        showForm(true);
    }

    @FXML
    private void savePatient() {
        formError.setText("");
        try {
            if (editingPatient == null) {
                svc.register(fName.getText(), fEmail.getText(), fPhone.getText(),
                        fAddress.getText(), fDob.getValue(), fMedical.getText(), fEmergency.getText());
                AlertHelper.showSuccess("Saved", "Patient registered successfully.");
            } else {
                editingPatient.setName(fName.getText());
                editingPatient.setEmail(fEmail.getText());
                editingPatient.setPhone(fPhone.getText());
                editingPatient.setAddress(fAddress.getText());
                editingPatient.setEmergencyContact(fEmergency.getText());
                editingPatient.setMedicalHistory(fMedical.getText());
                editingPatient.setDateOfBirth(fDob.getValue());
                svc.update(editingPatient);
                AlertHelper.showSuccess("Updated", "Patient record updated.");
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deletePatient(Patient p) {
        if (AlertHelper.confirm("Delete Patient",
                "Delete '" + p.getName() + "'?\nThis will also remove their sessions and payments.")) {
            svc.delete(p);
            refresh();
        }
    }

    @FXML
    private void validateEmail() {
        Validator.applyEmailStyle(fEmail);
    }

    @FXML
    private void validatePhone() {
        Validator.applyPhoneStyle(fPhone);
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
        fName.clear();
        fEmail.clear();
        fPhone.clear();
        fAddress.clear();
        fEmergency.clear();
        fMedical.clear();
        fDob.setValue(null);
        fEmail.setStyle("");
        fPhone.setStyle("");
        formError.setText("");
    }
}
