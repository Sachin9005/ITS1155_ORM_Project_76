package lk.ijse.serenity.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.serenity.bo.PatientBOImpl;
import lk.ijse.serenity.dto.PatientDTO;
import lk.ijse.serenity.exception.SerenityException;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientPanelController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final PatientBOImpl svc = new PatientBOImpl();

    private final ObservableList<PatientDTO> data = FXCollections.observableArrayList();
    @FXML
    private TableView<PatientDTO> patientTable;
    @FXML
    private TableColumn<PatientDTO, Long> colId;
    @FXML
    private TableColumn<PatientDTO, String> colName, colEmail, colPhone, colDob, colRegDate;
    @FXML
    private TableColumn<PatientDTO, Void> colActions;
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

    private PatientDTO editingPatient = null;

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
                d.getValue().getDob() != null ? d.getValue().getDob().format(FMT) : "—"));
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
        List<PatientDTO> all = svc.getAllPatients();
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
        List<PatientDTO> results = svc.search(kw);
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

    private void openEditDialog(PatientDTO p) {
        editingPatient = p;
        formTitle.setText("Edit Patient — " + p.getName());
        fName.setText(p.getName());
        fEmail.setText(p.getEmail());
        fPhone.setText(p.getPhone());
        fAddress.setText(p.getAddress() != null ? p.getAddress() : "");
        fEmergency.setText(p.getEmergencyContact() != null ? p.getEmergencyContact() : "");
        fDob.setValue(p.getDob());
        showForm(true);
    }

    @FXML
    private void savePatient() {
        formError.setText("");
        try {
            if (editingPatient == null) {
                svc.savePatient(PatientDTO.builder()
                        .name(fName.getText().trim())
                        .email(fEmail.getText().trim())
                        .phone(fPhone.getText().trim())
                        .address(fAddress.getText().trim())
                        .emergencyContact(fEmergency.getText().trim())
                        .dob(fDob.getValue())
                        .build());
                new Alert(Alert.AlertType.INFORMATION, "Patient added successfully!").showAndWait();
            } else {
                editingPatient.setName(fName.getText());
                editingPatient.setEmail(fEmail.getText());
                editingPatient.setPhone(fPhone.getText());
                editingPatient.setAddress(fAddress.getText());
                editingPatient.setEmergencyContact(fEmergency.getText());
                editingPatient.setDob(fDob.getValue());
                svc.updatePatient(editingPatient);
                new Alert(Alert.AlertType.INFORMATION, "Patient updated successfully!").showAndWait();
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deletePatient(PatientDTO p) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Delete Patient").showAndWait().get() == ButtonType.OK) {
            svc.deletePatient(p);;
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
