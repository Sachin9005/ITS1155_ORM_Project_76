package lk.ijse.serenity.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.serenity.bo.TherapistBOImpl;
import lk.ijse.serenity.dto.TherapistDTO;
import lk.ijse.serenity.exception.SerenityException;

public class TherapistPanelController {

    private final TherapistBOImpl svc = new TherapistBOImpl();
    private final ObservableList<TherapistDTO> data = FXCollections.observableArrayList();
    @FXML
    private TableView<TherapistDTO> therapistTable;
    @FXML
    private TableColumn<TherapistDTO, Long> colId;
    @FXML
    private TableColumn<TherapistDTO, String> colName, colSpec, colEmail, colPhone, colAvail;
    @FXML
    private TableColumn<TherapistDTO, Void> colActions;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private TextField fName, fSpec, fEmail, fPhone, fAvail, fQual;

    private TherapistDTO editingTherapist = null;

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
        data.setAll(svc.getAllTherapists());
    }

    @FXML
    private void openAddDialog() {
        editingTherapist = null;
        formTitle.setText("Add New Therapist");
        clearForm();
        showForm(true);
    }

    private void openEditDialog(TherapistDTO t) {
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
                TherapistDTO newTherapist = TherapistDTO.builder()
                        .name(fName.getText())
                        .specialization(fSpec.getText())
                        .email(fEmail.getText())
                        .phone(fPhone.getText())
                        .availability(fAvail.getText())
                        .qualification(fQual.getText())
                        .build();
                boolean isSave = svc.saveTherapist(newTherapist);
                if (isSave) {
                    refresh();
                    showForm(false);
                    new Alert(Alert.AlertType.INFORMATION, "Success Save Therapist").showAndWait();
                } else {
                    formError.setText("Failed to save therapist. Please try again.");
                }

            } else {
                editingTherapist.setName(fName.getText());
                editingTherapist.setSpecialization(fSpec.getText());
                editingTherapist.setEmail(fEmail.getText());
                editingTherapist.setPhone(fPhone.getText());
                editingTherapist.setAvailability(fAvail.getText());
                editingTherapist.setQualification(fQual.getText());
                boolean isUpdate = svc.updateTherapist(editingTherapist);
                if (isUpdate) {
                    refresh();
                    showForm(false);
                    new Alert(Alert.AlertType.INFORMATION, "Success Update Therapist").showAndWait();
                } else {
                    formError.setText("Failed to update therapist. Please try again.");
                }
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deleteTherapist(TherapistDTO t) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete therapist: " + t.getName() + "?", ButtonType.YES, ButtonType.NO).showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                boolean isDeleted = svc.deleteTherapist(t);
                if (isDeleted) {
                    refresh();
                    new Alert(Alert.AlertType.INFORMATION, "Therapist deleted successfully.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete therapist. Please try again.").showAndWait();
                }
            } catch (SerenityException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete therapist. Please try again.").showAndWait();
            }
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
