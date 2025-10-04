package com.example.demo.Controllers;

import com.example.demo.Controllers.Admin.Admin;
import com.example.demo.Models.Users;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Login {

    // FXML Injections
    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button loginButton;

    @FXML
    public Label error_msg;

    // Constants
    private static final String ADMIN_DASHBOARD_PATH = "/view/Admin/DashBoardControl.fxml";
    private static final String EMPLOYEE_DASHBOARD_PATH = "/view/Employee/DashBoardControl.fxml";
    private static final String FORGOT_PASSWORD_PATH = "/view/forgot_password.fxml";

    // Login result constants
    private static final int LOGIN_SUCCESS = 1;
    private static final int INCORRECT_PASSWORD = 2;
    private static final int USER_NOT_FOUND = 0;

    /**
     * Handles the login button action
     */
    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (!validateInputs(username, password)) {
            return;
        }

        try {
            attemptLogin(username, password);
        } catch (Exception e) {
            handleLoginError(e);
        }
    }

    /**
     * Validates username and password inputs
     */
    private boolean validateInputs(String username, String password) {
        clearError();

        if (username.isEmpty()) {
            showError("Please enter username");
            usernameField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Attempts to login user (admin first, then employee)
     */
    private void attemptLogin(String username, String password) throws IOException {
        int loginResult = Users.checkAdminLogin(username, password);

        switch (loginResult) {
            case LOGIN_SUCCESS:
                handleAdminLoginSuccess(username, password);
                break;

            case INCORRECT_PASSWORD:
                showError("Incorrect admin password");
                break;

            case USER_NOT_FOUND:
                attemptEmployeeLogin(username, password);
                break;

            default:
                showError("Login failed. Please try again.");
        }
    }

    /**
     * Handles successful admin login
     */
    private void handleAdminLoginSuccess(String username, String password) throws IOException {
        System.out.println("Admin Login Successful");

        // Create admin instance (if needed for session)
        Admin admin = new Admin(username, password);

        // Load admin dashboard
        loadDashboard(ADMIN_DASHBOARD_PATH, "Admin Dashboard");

        closeLoginWindow();
    }

    /**
     * Attempts employee login if admin login fails
     */
    private void attemptEmployeeLogin(String username, String password) throws IOException {
        int employeeResult = Users.checkEmployeeLogin(username, password);

        switch (employeeResult) {
            case LOGIN_SUCCESS:
                handleEmployeeLoginSuccess(username);
                break;

            case INCORRECT_PASSWORD:
                showError("Incorrect employee password");
                break;

            default:
                showError("User not found");
        }
    }

    /**
     * Handles successful employee login
     */
    private void handleEmployeeLoginSuccess(String username) throws IOException {
        System.out.println("Employee Login Successful");

        // Set username for session
        Users.username = username;

        // Load employee dashboard
        loadDashboard(EMPLOYEE_DASHBOARD_PATH, "Employee Dashboard");

        closeLoginWindow();
    }

    /**
     * Loads the appropriate dashboard based on user type
     */
    private void loadDashboard(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        applyTheme(scene);

        Stage stage = createStage(scene, title);
        stage.show();
    }

    /**
     * Applies MaterialFX theme to the scene
     */
    private void applyTheme(Scene scene) {
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
    }

    /**
     * Creates and configures a new stage
     */
    private Stage createStage(Scene scene, String title) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.centerOnScreen();
        stage.setMaximized(true); // Optional: start maximized

        // Set minimum window size
        stage.setMinWidth(1024);
        stage.setMinHeight(768);

        return stage;
    }

    /**
     * Closes the login window
     */
    private void closeLoginWindow() {
        Window window = usernameField.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).close();
        }
    }

    /**
     * Handles forgot password action
     */
    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            navigateToForgotPassword();
        } catch (IOException e) {
            showError("Unable to load password reset page");
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the forgot password screen
     */
    private void navigateToForgotPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FORGOT_PASSWORD_PATH));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        applyTheme(scene);

        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.setScene(scene);
        currentStage.setTitle("Reset Password");
        currentStage.centerOnScreen();
    }

    /**
     * Displays error message in the UI
     */
    private void showError(String message) {
        error_msg.setText(message);
        System.out.println("Login Error: " + message);
    }

    /**
     * Clears any existing error messages
     */
    private void clearError() {
        error_msg.setText("");
    }

    /**
     * Handles unexpected login errors
     */
    private void handleLoginError(Exception e) {
        String errorMessage = "An unexpected error occurred during login";
        showError(errorMessage);

        // Log the full error for debugging
        e.printStackTrace();

        // Show alert for critical errors
        showAlert(Alert.AlertType.ERROR, "Login Error",
                "Please contact system administrator if this persists.");
    }

    /**
     * Utility method to show alerts
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Optional: Handle Enter key press for login
     */
    public void handleEnterKey(ActionEvent actionEvent) {
        handleLogin(actionEvent);
    }

    /**
     * Optional: Initialize method for any setup
     */
    @FXML
    public void initialize() {
        setupEventHandlers();
    }

    /**
     * Sets up additional event handlers
     */
    private void setupEventHandlers() {
        // Clear error when user starts typing
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!error_msg.getText().isEmpty()) {
                clearError();
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!error_msg.getText().isEmpty()) {
                clearError();
            }
        });
    }
}