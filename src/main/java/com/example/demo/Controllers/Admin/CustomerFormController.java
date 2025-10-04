package com.example.demo.Controllers.Admin;

import com.example.demo.Controllers.Tables.CustomerTable;
import com.example.demo.Controllers.Customer.Customer;
import com.example.demo.Models.CustomerModel;
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

public class CustomerFormController {
    public MFXTextField txtSearch;
    public TableColumn colCustomerId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colMobileNo;
    public TableColumn colUpdate;
    public TableColumn colDelete;
    public AnchorPane employeePane;
    public MFXButton btnAddCustomer;
    public TableView tblCustomer;

    private ObservableList<CustomerTable> originalCustomerList;
    private FilteredList<CustomerTable> filteredData;

    public void initialize() {
        setCellValuesFactory();
        loadAllCustomers();
        setupSearchFilter();
    }

    private void setCellValuesFactory() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colMobileNo.setCellValueFactory(new PropertyValueFactory<>("contact_Number"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("updateButton"));
        colDelete.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));
    }

    public void loadAllCustomers() {
        originalCustomerList = FXCollections.observableArrayList();
        try {
            List<Customer> allCustomers = CustomerModel.getAllCustomers();

            for (Customer customer : allCustomers) {
                originalCustomerList.add(new CustomerTable(
                        customer.getCusId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getContact_Number()));
            }

            setupButtonActions(allCustomers);

            // Initialize filtered data
            filteredData = new FilteredList<>(originalCustomerList, p -> true);

            // Create sorted list
            SortedList<CustomerTable> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tblCustomer.comparatorProperty());

            tblCustomer.setItems(sortedData);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Failed to load customers: " + e.getMessage()).show();
        }
    }

    private void setupButtonActions(List<Customer> allCustomers) {
        for (int i = 0; i < originalCustomerList.size(); i++) {
            final int index = i;

            originalCustomerList.get(i).getUpdateButton().setOnAction(event -> {
                try {
                    updateCustomer(allCustomers.get(index).getCusId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            originalCustomerList.get(i).getDeleteButton().setOnAction(event -> {
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure to delete this customer?", yes, no).showAndWait();

                if (type.orElse(no) == yes) {
                    String cusId = allCustomers.get(index).getCusId();
                    deleteCustomer(cusId);
                    loadAllCustomers(); // Reload the table after deletion
                }
            });
        }
    }

    private void setupSearchFilter() {
        // Add listener to search text field
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomers(newValue);
        });
    }

    private void filterCustomers(String searchText) {
        if (filteredData == null) return;

        filteredData.setPredicate(customer -> {
            // If filter text is empty, show all customers
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Convert search text to lower case for case-insensitive search
            String lowerCaseFilter = searchText.toLowerCase();

            // Filter by Customer ID
            if (customer.getCusId().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            // Optional: You can also filter by other fields if needed
            // For example, by name:
            // if (customer.getName().toLowerCase().contains(lowerCaseFilter)) {
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
        filterCustomers(searchText);
    }

    // Clear search and show all customers
    public void clearSearch() {
        txtSearch.clear();
        if (filteredData != null) {
            filteredData.setPredicate(null);
        }
    }

    private void updateCustomer(String cusId) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Admin/UpdateCustomerForm.fxml"));
        Parent rootNode = loader.load();

        UpdateCustomerFormController updateCustomerFormController = loader.getController();
        updateCustomerFormController.setCustomerFormController(this);
        updateCustomerFormController.setCusId(cusId);
        updateCustomerFormController.loadCustomerDetails();

        System.out.println("akaya");

        Scene scene = new Scene(rootNode);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Update Customer");
        stage.show();
    }

    private void deleteCustomer(String cusId) {
        boolean isDeleted = CustomerModel.deleteCustomer(cusId);
        if (isDeleted) {
            new Alert(Alert.AlertType.CONFIRMATION, "Customer Deleted").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to delete customer").show();
        }
    }

    public void btnAddCustomerOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/Admin/AddCustomerForm.fxml"));
        Parent rootNode = loader.load();

        AddCustomerFormController addCustomerFormController = loader.getController();
        addCustomerFormController.setCustomerFormController(this);

        Scene scene = new Scene(rootNode);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Add Customer");
        stage.show();
    }
}