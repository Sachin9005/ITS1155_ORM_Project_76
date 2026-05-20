package lk.ijse.serenity.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lk.ijse.serenity.MainApp;
import lk.ijse.serenity.bo.UserBOImpl;
import lk.ijse.serenity.entity.User;

import java.util.List;

public class DashboardController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Label footerName;
    @FXML
    private Label footerRole;

    @FXML
    private Button navDashboard;
    @FXML
    private Button navPatients;
    @FXML
    private Button navTherapists;
    @FXML
    private Button navPrograms;
    @FXML
    private Button navSessions;
    @FXML
    private Button navPayments;
    @FXML
    private Button navReports;
    @FXML
    private Button navUsers;
    @FXML
    private Button navSettings;
    @FXML
    private Label navAdminSection;

    private List<Button> allNavBtns;

    UserBOImpl  userBOImpl =  new UserBOImpl();

    @FXML
    public void initialize() {
        User user = userBOImpl.getCurrentUser();
        footerName.setText(user.getFullName());
        footerRole.setText(user.getRole().name());

        allNavBtns = List.of(navDashboard, navPatients, navTherapists,
                navPrograms, navSessions, navPayments, navReports, navUsers, navSettings);

        // Hide admin-only items for receptionist
        boolean isAdmin = userBOImpl.isAdmin();
        navReports.setVisible(true);
        navUsers.setVisible(isAdmin);
        navAdminSection.setVisible(isAdmin);

        showDashboardPanel();
    }

    private void setActive(Button active) {
        allNavBtns.forEach(b -> {
            b.getStyleClass().remove("nav-btn-active");
            if (!b.getStyleClass().contains("nav-btn")) b.getStyleClass().add("nav-btn");
        });
        active.getStyleClass().add("nav-btn-active");
    }

    private void loadPanel(String fxml) {
        try {
            Node panel = FXMLLoader.load(
                    getClass().getResource("/fxml/" + fxml));
            contentArea.getChildren().setAll(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDashboardPanel() {
        setActive(navDashboard);
        loadPanel("HomePanel.fxml");
    }

    @FXML
    public void showPatients() {
        setActive(navPatients);
        loadPanel("PatientPanel.fxml");
    }

    @FXML
    public void showTherapists() {
        setActive(navTherapists);
        loadPanel("TherapistPanel.fxml");
    }

    @FXML
    public void showPrograms() {
        setActive(navPrograms);
        loadPanel("ProgramPanel.fxml");
    }

    @FXML
    public void showSessions() {
        setActive(navSessions);
        loadPanel("SessionPanel.fxml");
    }

    @FXML
    public void showPayments() {
        setActive(navPayments);
        loadPanel("PaymentPanel.fxml");
    }

    @FXML
    public void showReports() {
        setActive(navReports);
        loadPanel("ReportPanel.fxml");
    }

    @FXML
    public void showUsers() {
        setActive(navUsers);
        loadPanel("UserPanel.fxml");
    }

    @FXML
    public void showSettings() {
        setActive(navSettings);
        loadPanel("SettingsPanel.fxml");
    }

    @FXML
    public void handleLogout() {
        userBOImpl.logout();
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
