package lk.ijse.serenity.controller;

import com.serenity.bo.UserBO;
import com.serenity.entity.User;
import com.serenity.exception.SerenityException;
import com.serenity.util.AlertHelper;
import com.serenity.util.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    public void initialize() {
        User u = UserBO.getInstance().getCurrentUser();
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
            UserBO.getInstance().changeUsername(newUser);
            lblCurrentUsername.setText(newUser);
            fNewUsername.clear();
            AlertHelper.showSuccess("Username Updated", "Your username has been changed to: " + newUser);
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
            UserBO.getInstance().changePassword(oldPass, newPass);
            fOldPass.clear();
            fNewPass.clear();
            fConfirmPass.clear();
            AlertHelper.showSuccess("Password Updated", "Your password has been changed successfully.");
        } catch (SerenityException e) {
            passwordError.setText("❌ " + e.getMessage());
        }
    }
}
