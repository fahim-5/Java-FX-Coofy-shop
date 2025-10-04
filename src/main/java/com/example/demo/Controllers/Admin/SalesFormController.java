package com.example.demo.Controllers.Admin;

import com.example.demo.Controllers.Sale.Sale;
import com.example.demo.Models.SalesModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class SalesFormController {

    public AnchorPane salesPane;
    public TableView tableSales;
    public TableColumn colSaleID;
    public TableColumn colCustomerName;
    public TableColumn colContactNo;
    public TableColumn colTotal;
    public TableColumn colDate;
    public MFXTextField txtSearch;

    private ObservableList<Sale> originalSalesList;
    private FilteredList<Sale> filteredData;

    public void initialize() {
        setCellValuesFactory();
        loadAllSales();
        setupSearchFilter();
    }

    private void setCellValuesFactory() {
        colSaleID.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        colContactNo.setCellValueFactory(new PropertyValueFactory<>("cusContact"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    public void loadAllSales() {
        originalSalesList = FXCollections.observableArrayList();
        try {
            List<Sale> allSales = SalesModel.getAllSales();
            originalSalesList.addAll(allSales);

            // Initialize filtered data
            filteredData = new FilteredList<>(originalSalesList, p -> true);

            // Create sorted list
            SortedList<Sale> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableSales.comparatorProperty());

            tableSales.setItems(sortedData);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load sales: " + e.getMessage()).show();
        }
    }

    private void setupSearchFilter() {
        // Add listener to search text field for real-time filtering
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSales(newValue);
        });
    }

    private void filterSales(String searchText) {
        if (filteredData == null) return;

        filteredData.setPredicate(sale -> {
            // If filter text is empty, show all sales
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Convert search text to lower case for case-insensitive search
            String lowerCaseFilter = searchText.toLowerCase();

            // Filter by Sale ID
            if (sale.getSaleId().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            // Optional: You can also filter by other fields if needed
            // For example, by customer name:
            // if (sale.getCusName().toLowerCase().contains(lowerCaseFilter)) {
            //     return true;
            // }
            // Or by contact number:
            // if (sale.getCusContact().toLowerCase().contains(lowerCaseFilter)) {
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
        filterSales(searchText);
    }

    // Clear search and show all sales
    public void clearSearch() {
        txtSearch.clear();
        if (filteredData != null) {
            filteredData.setPredicate(null);
        }
    }
}