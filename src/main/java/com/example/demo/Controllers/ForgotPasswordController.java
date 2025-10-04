package com.example.demo.Controllers;

import com.example.demo.Models.Users;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ForgotPasswordController implements Initializable {

    @FXML public MFXTextField txtUsername;
    @FXML public MFXTextField txtNewPassword;
    @FXML public MFXTextField txtConfirmPassword;
    @FXML public MFXButton btnResetPassword;
    @FXML public MFXButton btnBackToLogin;
    @FXML public Label lblError;
    @FXML public Label lblUsernameStatus;

    private String userType;
    private String username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFastEventHandlers();
    }

    private void setupFastEventHandlers() {
        // Fast username verification with debouncing
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                fastVerifyUsername(newValue);
            } else if (newValue.isEmpty()) {
                Platform.runLater(() -> {
                    lblUsernameStatus.setText("");
                    lblUsernameStatus.getStyleClass().removeAll("status-success", "status-error");
                });
            }
        });

        // Fast password validation
        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                fastValidatePasswordMatch();
            }
        });
    }

    private void fastVerifyUsername(String username) {
        CompletableFuture.supplyAsync(() -> {
            boolean isAdmin = Users.checkAdminUserName(username);
            boolean isEmployee = Users.checkEmployeeUserName(username);

            if (isAdmin) return "admin";
            if (isEmployee) return "employee";
            return "not_found";

        }).thenAccept(result -> {
            Platform.runLater(() -> {
                switch (result) {
                    case "admin":
                        userType = "admin";
                        this.username = username;
                        lblUsernameStatus.setText("✓ Admin account verified");
                        lblUsernameStatus.getStyleClass().setAll("status-text", "status-success");
                        break;
                    case "employee":
                        userType = "employee";
                        this.username = username;
                        lblUsernameStatus.setText("✓ Employee account verified");
                        lblUsernameStatus.getStyleClass().setAll("status-text", "status-success");
                        break;
                    default:
                        lblUsernameStatus.setText("✗ Username not found");
                        lblUsernameStatus.getStyleClass().setAll("status-text", "status-error");
                        userType = null;
                }
                lblError.setText("");
            });
        });
    }

    @FXML
    public void handleResetPassword(ActionEvent actionEvent) {
        if (fastValidateInputs()) {
            fastResetPassword();
        }
    }

    private boolean fastValidateInputs() {
        // Quick validation
        if (userType == null) {
            showQuickError("Please verify your username first");
            txtUsername.requestFocus();
            return false;
        }

        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showQuickError("Please fill all password fields");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showQuickError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            showQuickError("Password must be at least 6 characters");
            txtNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void fastValidatePasswordMatch() {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (!confirmPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                showQuickError("Passwords do not match");
            } else if (newPassword.length() >= 6) {
                showQuickSuccess("Passwords match ✓");
            }
        }
    }

    private void fastResetPassword() {
        btnResetPassword.setDisable(true);
        btnResetPassword.setText("Resetting...");

        CompletableFuture.supplyAsync(() -> {
            try {
                if ("admin".equals(userType)) {
                    return Users.resetAdminPassword(username, txtNewPassword.getText());
                } else if ("employee".equals(userType)) {
                    return Users.resetEmployeePassword(username, txtNewPassword.getText());
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).thenAccept(success -> {
            Platform.runLater(() -> {
                btnResetPassword.setDisable(false);
                btnResetPassword.setText("Reset Password");

                if (success) {
                    showQuickSuccess("Password reset successfully!");
                    showSuccessAlert();
                    fastNavigateToLogin();
                } else {
                    showQuickError("Failed to reset password");
                }
            });
        });
    }

    private void showQuickError(String message) {
        lblError.setText(message);
        lblError.getStyleClass().setAll("error-text", "status-error");
    }

    private void showQuickSuccess(String message) {
        lblError.setText(message);
        lblError.getStyleClass().setAll("error-text", "status-success");
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Password reset successfully!\nYou can now login with your new password.");
        alert.showAndWait();
    }

    @FXML
    public void handleBackToLogin(ActionEvent actionEvent) {
        fastNavigateToLogin();
    }

    private void fastNavigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);

            Stage currentStage = (Stage) btnBackToLogin.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEnterKey(ActionEvent actionEvent) {
        handleResetPassword(actionEvent);
    }
}