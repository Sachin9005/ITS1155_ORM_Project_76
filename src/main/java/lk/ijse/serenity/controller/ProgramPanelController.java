package lk.ijse.serenity.controller;

import com.serenity.entity.TherapyProgram;
import com.serenity.exception.SerenityException;
import com.serenity.bo.TherapyProgramBO;
import com.serenity.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class ProgramPanelController {

    private final TherapyProgramBO svc = TherapyProgramBO.getInstance();
    private final ObservableList<TherapyProgram> data = FXCollections.observableArrayList();
    @FXML
    private TableView<TherapyProgram> programTable;
    @FXML
    private TableColumn<TherapyProgram, String> colPid, colName, colDuration, colFee, colDesc;
    @FXML
    private TableColumn<TherapyProgram, Void> colActions;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private TextField fPid, fName, fDuration, fFee, fDesc;
    private TherapyProgram editing = null;

    @FXML
    public void initialize() {
        colPid.setCellValueFactory(new PropertyValueFactory<>("programId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colFee.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getFee() != null
                        ? String.format("LKR %,.2f", d.getValue().getFee()) : "—"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button delBtn = new Button("🗑 Delete");
            private final HBox box = new HBox(6, editBtn, delBtn);

            {
                editBtn.getStyleClass().addAll("btn-warning", "btn-small");
                delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                editBtn.setOnAction(e -> openEdit(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> delete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        programTable.setItems(data);
        programTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refresh();
    }

    private void refresh() {
        data.setAll(svc.findAll());
    }

    @FXML
    private void openAddDialog() {
        editing = null;
        formTitle.setText("Add New Therapy Program");
        clearForm();
        fPid.setDisable(false);
        showForm(true);
    }

    private void openEdit(TherapyProgram p) {
        editing = p;
        formTitle.setText("Edit — " + p.getName());
        fPid.setText(p.getProgramId());
        fPid.setDisable(true); // ProgramID is immutable
        fName.setText(p.getName());
        fDuration.setText(p.getDuration() != null ? p.getDuration() : "");
        fFee.setText(p.getFee() != null ? p.getFee().toPlainString() : "");
        fDesc.setText(p.getDescription() != null ? p.getDescription() : "");
        showForm(true);
    }

    @FXML
    private void saveProgram() {
        formError.setText("");
        try {
            BigDecimal fee;
            try {
                fee = new BigDecimal(fFee.getText().trim());
            } catch (NumberFormatException ex) {
                formError.setText("❌ Fee must be a valid number.");
                return;
            }
            if (editing == null) {
                svc.create(fPid.getText(), fName.getText(), fDuration.getText(), fee, fDesc.getText());
                AlertHelper.showSuccess("Saved", "Therapy program created successfully.");
            } else {
                editing.setName(fName.getText());
                editing.setDuration(fDuration.getText());
                editing.setFee(fee);
                editing.setDescription(fDesc.getText());
                svc.update(editing);
                AlertHelper.showSuccess("Updated", "Program updated.");
            }
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void delete(TherapyProgram p) {
        if (AlertHelper.confirm("Delete Program",
                "Delete '" + p.getName() + "'? This cannot be undone.")) {
            try {
                svc.delete(p);
                refresh();
            } catch (SerenityException e) {
                AlertHelper.showError("Delete Failed", e.getMessage());
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
        fPid.clear();
        fName.clear();
        fDuration.clear();
        fFee.clear();
        fDesc.clear();
        fPid.setDisable(false);
        formError.setText("");
    }
}
