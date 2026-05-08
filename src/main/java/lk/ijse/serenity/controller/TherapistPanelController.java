package lk.ijse.serenity.controller;

import com.serenity.entity.Therapist;
import com.serenity.exception.SerenityException;
import com.serenity.bo.TherapistBO;
import com.serenity.util.AlertHelper;
import com.serenity.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TherapistPanelController {

    private final TherapistBO svc = TherapistBO.getInstance();
    private final ObservableList<Therapist> data = FXCollections.observableArrayList();
    @FXML
    private TableView<Therapist> therapistTable;
    @FXML
    private TableColumn<Therapist, Long> colId;
    @FXML
    private TableColumn<Therapist, String> colName, colSpec, colEmail, colPhone, colAvail;
    @FXML
    private TableColumn<Therapist, Void> colActions;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private TextField fName, fSpec, fEmail, fPhone, fAvail, fQual;
    private Therapist editingTherapist = null;

    @FXML
    public void initialize() {
        setupColumns();
        refresh();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAvail.setCellValueFactory(new PropertyValueFactory<>("availability"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button delBtn = new Button("🗑 Delete");
            private final HBox box = new HBox(6, editBtn, delBtn);

            {
                editBtn.getStyleClass().addAll("btn-warning", "btn-small");
                delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                editBtn.setOnAction(e -> openEditDialog(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> deleteTherapist(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        therapistTable.setItems(data);
        therapistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refresh() {
        data.setAll(svc.findAll());
    }

    @FXML
    private void openAddDialog() {
        editingTherapist = null;
        formTitle.setText("Add New Therapist");
        clearForm();
        showForm(true);
    }

    private void openEditDialog(Therapist t) {
        editingTherapist = t;
        formTitle.setText("Edit Therapist — " + t.getName());
        fName.setText(t.getName());
        fSpec.setText(t.getSpecialization() != null ? t.getSpecialization() : "");
        fEmail.setText(t.getEmail());
        fPhone.setText(t.getPhone());
        fAvail.setText(t.getAvailability() != null ? t.getAvailability() : "");
        fQual.setText(t.getQualification() != null ? t.getQualification() : "");
        showForm(true);
    }

    @FXML
    private void saveTherapist() {
        formError.setText("");
        try {
            if (editingTherapist == null) {
                svc.add(fName.getText(), fSpec.getText(), fEmail.getText(),
                        fPhone.getText(), fAvail.getText(), fQual.getText());
                AlertHelper.showSuccess("Saved", "Therapist added successfully.");
            } else {
                editingTherapist.setName(fName.getText());
                editingTherapist.setSpecialization(fSpec.getText());
                editingTherapist.setEmail(fEmail.getText());
                editingTherapist.setPhone(fPhone.getText());
                editingTherapist.setAvailability(fAvail.getText());
                editingTherapist.setQualification(fQual.getText());
                svc.update(editingTherapist);
                AlertHelper.showSuccess("Updated", "Therapist updated.");
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deleteTherapist(Therapist t) {
        if (AlertHelper.confirm("Delete Therapist",
                "Delete '" + t.getName() + "'? This will remove their session history too.")) {
            svc.delete(t);
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
        fSpec.clear();
        fEmail.clear();
        fPhone.clear();
        fAvail.clear();
        fQual.clear();
        fEmail.setStyle("");
        fPhone.setStyle("");
        formError.setText("");
    }
}
