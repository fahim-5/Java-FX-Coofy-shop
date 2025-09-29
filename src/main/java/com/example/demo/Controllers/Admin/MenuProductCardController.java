package com.example.demo.Controllers.Admin;

import com.example.demo.Controllers.Product.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale; // Added for professional string manipulation

public class MenuProductCardController {

    public AnchorPane menu_prod_card;
    public ImageView prod_imageview;
    @FXML
    Label prod_name;
    @FXML
    private GridPane menu_gridpane;

    @FXML
    Label prod_price;

    @FXML
    private Spinner<Integer> prod_spinner;

    @FXML
    private Button prod_addbtn;

    @Setter
    MenuController menuController;

    // PROFESSIONAL DESIGN: Store the product name for use in the fallback image logic
    private String currentProductName;

    @FXML
    private void initialize() {
        // Initial factory is just a placeholder; it's properly set in setProductDetails
        prod_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));
        prod_addbtn.setOnAction(event -> {
            btnAddProductOnAction(null);
        });
    }

    @Override
    public String toString() {
        return "MenuProductCardController{" +
                "prod_name=" + prod_name +
                ", prod_price=" + prod_price +
                ", prod_spinner=" + prod_spinner +
                ", prod_addbtn=" + prod_addbtn +
                ", menuController=" + menuController +
                '}';
    }


    /**
     * Sets the product details on the card, including a null-safe check for the image.
     * @param product The Product object containing details.
     * @param menuController The parent controller for communication.
     */
    public void setProductDetails(Product product, MenuController menuController) {
        this.menuController = menuController;

        // Set basic details
        currentProductName = product.getProductName();
        prod_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, product.getStock(), 1));
        prod_name.setText(currentProductName);
        prod_price.setText(String.valueOf(product.getPrice()));

        // --- PRIMARY LOADING: Database byte array (imageData) ---
        byte[] imageData = product.getImage();

        if (imageData != null) {
            // Database data exists: Load the actual product image
            try {
                InputStream is = new ByteArrayInputStream(imageData);
                Image image = new Image(is);
                prod_imageview.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading image from DB for product: " + currentProductName + ". Falling back to resource.");
                e.printStackTrace();
                loadResourceImage(); // Fallback to resource if DB loading fails
            }
        } else {
            // Database data is null: Try to load from application resources
            System.out.println("No image found in DB for product: " + currentProductName + ". Attempting resource load.");
            loadResourceImage();
        }
    }

    /**
     * Helper method to set a product-specific image from resources, or a generic default if that fails.
     */
    private void loadResourceImage() {
        // The image files use PascalCase (e.g., frenchFries.png, appleJuice.png) as per the file tree image.
        // We will try to match the product name to the file name directly (e.g., "French Fries" -> "FrenchFries.png").

        // Remove spaces from the product name to match the file names in your /img/products folder.
        String baseName = currentProductName.replaceAll("\\s+", "");
        String dynamicImagePath = "/img/products/" + baseName + ".png";

        // The file tree shows 'logo.png' in /img/ as a good generic fallback.
        String genericDefaultPath = "/img/logo.png";

        // 1. Try to load the product-specific image from resources
        try (InputStream productStream = getClass().getResourceAsStream(dynamicImagePath)) {
            if (productStream != null) {
                Image productImage = new Image(productStream);
                prod_imageview.setImage(productImage);
                return; // Image loaded successfully
            } else {
                System.err.println("Product-specific resource image not found: " + dynamicImagePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading product-specific image from resource: " + dynamicImagePath + " - " + e.getMessage());
        }

        // 2. If product-specific resource loading failed, load a generic default image
        System.out.println("Trying generic default: " + genericDefaultPath);
        try (InputStream defaultStream = getClass().getResourceAsStream(genericDefaultPath)) {
            if (defaultStream != null) {
                Image defaultImage = new Image(defaultStream);
                prod_imageview.setImage(defaultImage);
            } else {
                System.err.println("CRITICAL: Generic default image resource not found. Path: " + genericDefaultPath);
                prod_imageview.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Failed to load generic default image: " + e.getMessage());
            prod_imageview.setImage(null);
        }
    }


    public void btnAddProductOnAction(ActionEvent actionEvent) {
        String name = prod_name.getText();
        Integer quantity = prod_spinner.getValue();
        double price = Double.parseDouble(prod_price.getText());

        // Only add if a valid quantity (> 0) is selected and the menuController is set
        if (quantity != null && quantity > 0 && menuController != null) {
            menuController.addProductToTable(name, quantity, price);
        }
    }
}