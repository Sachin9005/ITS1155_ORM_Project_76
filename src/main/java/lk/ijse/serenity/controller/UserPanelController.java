package lk.ijse.serenity.controller;

import com.serenity.bo.UserBO;
import com.serenity.dao.UserDAO;
import com.serenity.entity.User;
import com.serenity.exception.DuplicateRegistrationException;
import com.serenity.exception.SerenityException;
import com.serenity.util.AlertHelper;
import com.serenity.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class UserPanelController {

    private final UserDAO userDAO = new UserDAO();
    private final UserBO auth = UserBO.getInstance();
    private final ObservableList<User> data = FXCollections.observableArrayList();
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Long> colId;
    @FXML
    private TableColumn<User, String> colUsername, colFullName, colEmail, colRole;
    @FXML
    private TableColumn<User, Void> colActions;
    @FXML
    private VBox formCard;
    @FXML
    private Label formTitle, formError;
    @FXML
    private TextField fUsername, fFullName, fEmail;
    @FXML
    private PasswordField fPassword, fConfirm;
    @FXML
    private ComboBox<User.Role> fRole;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getRole().name()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("🗑 Delete");

            {
                delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                delBtn.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                User u = getTableView().getItems().get(getIndex());
                // Cannot delete yourself
                delBtn.setDisable(u.getId().equals(auth.getCurrentUser().getId()));
                setGraphic(delBtn);
            }
        });

        fRole.setItems(FXCollections.observableArrayList(User.Role.values()));
        fRole.setValue(User.Role.RECEPTIONIST);

        userTable.setItems(data);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refresh();
    }

    private void refresh() {
        data.setAll(userDAO.findAll());
    }

    @FXML
    private void openAddDialog() {
        formTitle.setText("Create New User");
        clearForm();
        showForm(true);
    }

    @FXML
    private void saveUser() {
        formError.setText("");
        String username = fUsername.getText().trim();
        String pass = fPassword.getText();
        String confirm = fConfirm.getText();

        try {
            if (!Validator.isValidUsername(username)) {
                formError.setText("❌ Username must be 4-30 chars, alphanumeric/underscore.");
                return;
            }
            if (!pass.equals(confirm)) {
                formError.setText("❌ Passwords do not match.");
                return;
            }
            Validator.requireValidPassword(pass);
            if (fEmail.getText().length() > 0) Validator.requireValidEmail(fEmail.getText());
            if (userDAO.existsByUsername(username)) {
                throw new DuplicateRegistrationException("Username", username);
            }
            User u = new User(username, auth.hashPassword(pass),
                    fRole.getValue(), fFullName.getText(), fEmail.getText());
            userDAO.save(u);
            AlertHelper.showSuccess("User Created", "Account '" + username + "' created.");
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deleteUser(User u) {
        if (AlertHelper.confirm("Delete User",
                "Delete user '" + u.getUsername() + "'? This cannot be undone.")) {
            userDAO.delete(u);
            refresh();
        }
    }

    @FXML
    private void validateEmail() {
        Validator.applyEmailStyle(fEmail);
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
        fUsername.clear();
        fFullName.clear();
        fEmail.clear();
        fPassword.clear();
        fConfirm.clear();
        fRole.setValue(User.Role.RECEPTIONIST);
        fEmail.setStyle("");
        formError.setText("");
    }
}
