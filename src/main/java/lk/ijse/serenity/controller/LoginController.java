package lk.ijse.serenity.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.serenity.MainApp;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisible;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginBtn;

    private boolean passwordShown = false;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        // Sync visible <-> masked fields
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());

        // Allow Enter key to trigger login
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
        passwordVisible.setOnAction(e -> handleLogin());
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordShown = !passwordShown;
        passwordVisible.setVisible(passwordShown);
        passwordVisible.setManaged(passwordShown);
        passwordField.setVisible(!passwordShown);
        passwordField.setManaged(!passwordShown);
        togglePasswordBtn.setText(passwordShown ? "🔒" : "👁");
        if (passwordShown) passwordVisible.requestFocus();
        else passwordField.requestFocus();
    }

    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            UserBO.getInstance().login(username, password);
            MainApp.showDashboard();
        } catch (Exception e) {
            errorLabel.setText("❌ ");
            passwordField.clear();
            passwordVisible.clear();
            usernameField.requestFocus();
        } catch (SerenityException e) {
            errorLabel.setText("❌ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("❌ Connection error. Check database settings.");
            e.printStackTrace();
        }
    }
}
