package com.example.demo.Controllers.Admin;

import com.example.demo.Controllers.Tables.EmployeeTable;
import com.example.demo.Controllers.Employee.Employee;
import com.example.demo.Models.Users;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeFormController {
    public MFXTextField txtSearch;
    public MFXButton btnSalary;
    public MFXButton btnAttendance;
    public MFXButton btnAddEmployee;
    public TableColumn colEmployeeId;
    public TableColumn colFirstName;
    public TableColumn colLastName;
    public TableColumn colMobileNo;
    public TableColumn colUpdate;
    public TableColumn colDelete;
    public TableView tblEmployee;
    public AnchorPane employeePane;
    public TableColumn colUserName;
    public TableColumn colPassword;

    private ObservableList<EmployeeTable> originalEmployeeList;
    private FilteredList<EmployeeTable> filteredData;

    public void initialize() {
        setCellValuesFactory();
        loadAllEmployees();
        setupSearchFilter();
    }

    private void setCellValuesFactory() {
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("empId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colMobileNo.setCellValueFactory(new PropertyValueFactory<>("contact_Number"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("updateButton"));
        colDelete.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));
    }

    public void loadAllEmployees() {
        originalEmployeeList = FXCollections.observableArrayList();
        try {
            List<Employee> allEmployees = Users.getAllEmployees();

            for (Employee employee : allEmployees) {
                originalEmployeeList.add(new EmployeeTable(
                        employee.getEmpId(),
                        employee.getUserName(),
                        employee.getPassword(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getContact_Number()));
            }

            setupButtonActions(allEmployees);

            // Initialize filtered data
            filteredData = new FilteredList<>(originalEmployeeList, p -> true);

            // Create sorted list
            SortedList<EmployeeTable> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tblEmployee.comparatorProperty());

            tblEmployee.setItems(sortedData);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Failed to load employees: " + e.getMessage()).show();
        }
    }

    private void setupButtonActions(List<Employee> allEmployees) {
        for (int i = 0; i < originalEmployeeList.size(); i++) {
            final int index = i;

            originalEmployeeList.get(i).getUpdateButton().setOnAction(event -> {
                try {
                    updateEmployee(allEmployees.get(index).getEmpId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            originalEmployeeList.get(i).getDeleteButton().setOnAction(event -> {
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure to delete this employee?", yes, no).showAndWait();

                if (type.orElse(no) == yes) {
                    String empId = allEmployees.get(index).getEmpId();
                    deleteEmployee(empId);
                    loadAllEmployees(); // Reload the table after deletion
                }
            });
        }
    }

    private void setupSearchFilter() {
        // Add listener to search text field
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees(newValue);
        });
    }

    private void filterEmployees(String searchText) {
        if (filteredData == null) return;

        filteredData.setPredicate(employee -> {
            // If filter text is empty, show all employees
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Convert search text to lower case for case-insensitive search
            String lowerCaseFilter = searchText.toLowerCase();

            // Filter by Employee ID
            if (employee.getEmpId().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            // Optional: You can also filter by other fields if needed
            // For example, by first name:
            // if (employee.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
            //     return true;
            // }

            return false; // Does not match
        });
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        // This method will be called when Enter is pressed in the search field
        // The filtering is already handled by the textProperty listener
        // You can add additional logic here if needed
        String searchText = txtSearch.getText();
        filterEmployees(searchText);
    }

    // Clear search and show all employees
    public void clearSearch() {
        txtSearch.clear();
        if (filteredData != null) {
            filteredData.setPredicate(null);
        }
    }

    private void updateEmployee(String empId) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Admin/UpdateEmployeeForm.fxml"));
        Parent rootNode = loader.load();

        UpdateEmployeeFormController updateEmployeeFormController = loader.getController();
        updateEmployeeFormController.setEmployeeFormController(this);
        updateEmployeeFormController.setEmployeeId(empId);
        updateEmployeeFormController.loadEmployeeDetails();

        Scene scene = new Scene(rootNode);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Update Employee");
        stage.show();
    }

    private void deleteEmployee(String empId) {
        boolean isDeleted = Users.deleteEmployee(empId);
        if (isDeleted) {
            new Alert(Alert.AlertType.CONFIRMATION, "Employee Deleted").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to delete employee").show();
        }
    }

    public void btnAddEmployeeOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Admin/addEmployeeForm.fxml"));
        Parent rootNode = loader.load();

        AddEmployeeFormController addEmployeeFormController = loader.getController();
        addEmployeeFormController.setCustomerFormController(this);

        Scene scene = new Scene(rootNode);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Add Employee");
        stage.show();
    }

    public void btnAttendanceOnAction(ActionEvent actionEvent) {
        // Implement attendance functionality
    }

    public void btnSalaryOnAction(ActionEvent actionEvent) {
        // Implement salary functionality
    }
}