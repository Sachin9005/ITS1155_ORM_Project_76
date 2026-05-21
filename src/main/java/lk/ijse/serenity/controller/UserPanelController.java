package lk.ijse.serenity.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import lk.ijse.serenity.bo.UserBOImpl;
import lk.ijse.serenity.dto.UserDTO;
import lk.ijse.serenity.entity.User;
import lk.ijse.serenity.exception.DuplicateRegistrationException;
import lk.ijse.serenity.exception.SerenityException;
import lk.ijse.serenity.util.Validator;

public class UserPanelController {

    private final UserBOImpl userBO = new UserBOImpl();
    private final ObservableList<UserDTO> data = FXCollections.observableArrayList();
    @FXML
    private TableView<UserDTO> userTable;
    @FXML
    private TableColumn<UserDTO, Long> colId;
    @FXML
    private TableColumn<UserDTO, String> colUsername, colFullName, colEmail, colRole;
    @FXML
    private TableColumn<UserDTO, Void> colActions;
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
                UserDTO u = getTableView().getItems().get(getIndex());
                // Cannot delete yourself
                delBtn.setDisable(u.getId().equals(userBO.getCurrentUser().getId()));
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
        data.setAll(userBO.getAllUsers());
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
            if (userBO.existsByUsername(username)) {
                throw new DuplicateRegistrationException("Username", username);
            }
            UserDTO newUser = UserDTO.builder()
                    .username(username)
                    .passwordHash(userBO.hashPassword(pass))
                    .role(fRole.getValue())
                    .fullName(fFullName.getText().trim())
                    .email(fEmail.getText().trim())
                    .build();
            boolean isSaved = userBO.saveUser(newUser);
            if (!isSaved) {
                throw new SerenityException("Failed to save user. Please try again.");
            }
            new Alert(Alert.AlertType.INFORMATION, "User created successfully!").showAndWait();
            closeForm();
            refresh();
        } catch (SerenityException e) {
            formError.setText("❌ " + e.getMessage());
        }
    }

    private void deleteUser(UserDTO u) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Delete user '" + u.getUsername() + "'? This cannot be undone.")
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean isDeleted = userBO.deleteUser(u);
            if (!isDeleted) {
                throw new SerenityException("Failed to delete user. Please try again.");
            }
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
