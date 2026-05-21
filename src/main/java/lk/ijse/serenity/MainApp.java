package lk.ijse.serenity;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.serenity.bo.TherapyProgramBOImpl;
import lk.ijse.serenity.bo.UserBOImpl;
import lk.ijse.serenity.config.FactoryConfiguration;

public class MainApp extends Application {
    public static Stage primaryStage;
    private UserBOImpl userBOImpl = new UserBOImpl();
    private TherapyProgramBOImpl therapyProgramBOImpl = new TherapyProgramBOImpl();

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                MainApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
    }

    public static void showDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/DashboardView.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                MainApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(750);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Initialise Hibernate & seed data
        FactoryConfiguration.getInstance().getSession();
        userBOImpl.ensureDefaultAdmin();
        therapyProgramBOImpl.seedDefaultPrograms();

        showLogin();

        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.show();
    }

    @Override
    public void stop() {
        FactoryConfiguration.shutdown();
    }
}
