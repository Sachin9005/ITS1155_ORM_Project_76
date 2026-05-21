package lk.ijse.serenity.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.serenity.bo.UserBOImpl;
import lk.ijse.serenity.dto.UserDTO;
import lk.ijse.serenity.exception.SerenityException;
import lk.ijse.serenity.util.Validator;

public class SettingsPanelController {

    @FXML
    private Label lblName, lblRole, lblEmail, lblCurrentUsername;
    @FXML
    private TextField fNewUsername;
    @FXML
    private Label usernameError, passwordError;

    @FXML
    private PasswordField fOldPass, fNewPass, fConfirmPass;
    @FXML
    private TextField fOldPassVisible, fNewPassVisible;
    @FXML
    private Button toggleOld, toggleNew;

    private boolean oldVisible = false;
    private boolean newVisible = false;

    UserBOImpl userBO = new UserBOImpl();

    @FXML
    public void initialize() {
        UserDTO u = userBO.getCurrentUser();
        lblName.setText(u.getFullName() != null ? u.getFullName() : "—");
        lblRole.setText(u.getRole().name());
        lblEmail.setText(u.getEmail() != null ? u.getEmail() : "—");
        lblCurrentUsername.setText(u.getUsername());

        // Sync visible <-> masked password fields
        fOldPassVisible.textProperty().bindBidirectional(fOldPass.textProperty());
        fNewPassVisible.textProperty().bindBidirectional(fNewPass.textProperty());
    }

    @FXML
    private void toggleOldPass() {
        oldVisible = !oldVisible;
        fOldPassVisible.setVisible(oldVisible);
        fOldPassVisible.setManaged(oldVisible);
        fOldPass.setVisible(!oldVisible);
        fOldPass.setManaged(!oldVisible);
        toggleOld.setText(oldVisible ? "🙈" : "👁");
    }

    @FXML
    private void toggleNewPass() {
        newVisible = !newVisible;
        fNewPassVisible.setVisible(newVisible);
        fNewPassVisible.setManaged(newVisible);
        fNewPass.setVisible(!newVisible);
        fNewPass.setManaged(!newVisible);
        toggleNew.setText(newVisible ? "🙈" : "👁");
    }

    @FXML
    private void changeUsername() {
        usernameError.setText("");
        String newUser = fNewUsername.getText().trim();
        try {
            if (!Validator.isValidUsername(newUser)) {
                usernameError.setText("❌ Username must be 4-30 chars, letters/digits/underscore only.");
                return;
            }
            userBO.changeUsername(newUser);
            lblCurrentUsername.setText(newUser);
            fNewUsername.clear();
            new Alert(Alert.AlertType.INFORMATION, "Username changed successfully!").showAndWait();
        } catch (SerenityException e) {
            usernameError.setText("❌ " + e.getMessage());
        }
    }

    @FXML
    private void changePassword() {
        passwordError.setText("");
        String oldPass = fOldPass.getText();
        String newPass = fNewPass.getText();
        String confirmPass = fConfirmPass.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            passwordError.setText("❌ All password fields are required.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            passwordError.setText("❌ New passwords do not match.");
            return;
        }
        try {
            Validator.requireValidPassword(newPass);
            userBO.changePassword(oldPass, newPass);
            fOldPass.clear();
            fNewPass.clear();
            new Alert(Alert.AlertType.INFORMATION, "Password changed successfully!").showAndWait();
        } catch (SerenityException e) {
            passwordError.setText("❌ " + e.getMessage());
        }
    }
}
