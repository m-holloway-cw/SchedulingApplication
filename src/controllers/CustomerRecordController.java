package controllers;

import c195.Appointment;
import c195.Country;
import c195.Customer;
import c195.FirstLevelDivision;
import c195.Main;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class to view and alter customer records.
 *
 * @author Michael Holloway
 */
public class CustomerRecordController implements Initializable {

    @FXML
    private Label errorLabel;

    //fxml components for table of customers tab
    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> customerAddressColumn;

    @FXML
    private TableColumn<Customer, String> customerPostalColumn;

    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;

    @FXML
    private TableColumn<Customer, String> customerDivIdColumn;

    //fxml components for add new tab
    @FXML
    private TextField customerIdNew;
    @FXML
    private TextField nameNew;
    @FXML
    private TextField phoneNumberNew;
    @FXML
    private TextField addressNew;
    @FXML
    private TextField postalCodeNew;
    @FXML
    private ComboBox chooseCountryNew;
    @FXML
    private ComboBox chooseFirstLevelNew;

    //fxml components for edit new tab
    @FXML
    private ComboBox chooseCustomerEdit;
    @FXML
    private TextField customerIdEdit;
    @FXML
    private TextField nameEdit;
    @FXML
    private TextField phoneNumberEdit;
    @FXML
    private TextField addressEdit;
    @FXML
    private TextField postalCodeEdit;
    @FXML
    private ComboBox chooseCountryEdit;
    @FXML
    private ComboBox chooseFirstLevelEdit;

    private static int customerIndex;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCustomerTable();
        ObservableList<Customer> ob = FXCollections.observableArrayList();
        for (Customer c : Main.store.getCustomers()) {
            ob.add(c);
        }
        customerTable.setItems(ob);
        fillCustomerBox();
        fillCountryBoxes();
        customerIdNew.setText(String.valueOf(Main.store.getNextCustomerIdNumber()));
    }

    /**
     * Takes information and adds a new customer object to the database.
     */
    @FXML
    public void createNewCustomer() {
        int id = Integer.parseInt(customerIdNew.getText());
        boolean errorPresent = false;
        String errorMessage = "";

        nameNew.selectAll();
        nameNew.copy();
        String name = nameNew.getText();
        if (name.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing name field";
        }

        phoneNumberNew.selectAll();
        phoneNumberNew.copy();
        String phone = phoneNumberNew.getText();
        if (phone.isBlank() || !phone.replace("-", "").matches("\\d+")) {
            errorPresent = true;
            errorMessage = "Phone number must contain only numbers a dashes";
        }

        addressNew.selectAll();
        addressNew.copy();
        String address = addressNew.getText();
        if (address.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing address field";
        }

        postalCodeNew.selectAll();
        postalCodeNew.copy();
        String postal = postalCodeNew.getText();
        if (postal.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing postal field";
        }

        int divId = 0;
        try {
            String div = chooseFirstLevelNew.getValue().toString();
            for (FirstLevelDivision f : Main.store.getFirstLevels()) {
                if (f.getName().equals(div)) {
                    divId = f.getId();
                }
            }
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing division field";
        }

        if (!errorPresent) {
            Customer newCustomer = new Customer(id, name, address, postal, phone, divId);
            Main.store.addCustomer(newCustomer);
            errorLabel.setText("Added new customer successfully");
        } else {
            errorLabel.setText(errorMessage);
        }
    }

    /**
     * Sets the desired country value and populates the FLD box with appropriate values.
     */
    @FXML
    public void setCountryChosenNew() {
        String text = chooseCountryNew.getValue().toString();
        for (Country c : Main.store.getCountries()) {
            if (c.getName().equals(text)) {
                ObservableList<String> divList = FXCollections.observableArrayList();
                for (FirstLevelDivision f : c.getDivList()) {
                    divList.add(f.getName());
                }
                chooseFirstLevelNew.setItems(divList);
                chooseFirstLevelNew.setDisable(false);
            }
        }

    }

    /**
     * Deletes the selected customer record from the database.
     * Checks for attached appointments prior to deletion.
     */
    @FXML
    public void deleteSelectedCustomer() {
        //check for appointments attached to customer
        //if found deny
        customerIdEdit.selectAll();
        customerIdEdit.copy();
        int id = Integer.parseInt(customerIdEdit.getText());
        boolean apptFound = false;
        StringBuilder sb = new StringBuilder();
        sb.append("Attached appointment ID's found: [");
        for (Appointment a : Main.store.getAppointments()) {
            if (a.getCustomerId() == id) {
                apptFound = true;
                sb.append(a.getId()).append("],[");
            }
        }
        sb.delete(sb.lastIndexOf("["), sb.length());

        if (!apptFound) {
            //alert to confirm
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

            nameEdit.selectAll();
            nameEdit.copy();
            String name = nameEdit.getText();
            confirmation.setContentText("Are you sure you want to delete this customer?\nID: " + id + "\nName: " + name);
            Optional<ButtonType> choice = confirmation.showAndWait();
            if (choice.get() == ButtonType.OK) {
                Main.store.deleteCustomer(id);
                errorLabel.setText("Deleted customer id: " + id);
                //reload data
                fillCustomerBox();
            } else {
                //cancel
            }
        } else {
            Alert failed = new Alert(Alert.AlertType.ERROR);
            failed.setContentText("Unable to delete customer.\nDelete attached appointments and retry:\n" + sb.toString());
            errorLabel.setText("Attached appointments found");
        }
    }

    /**
     * Sends updated customer object to database for replacement.
     */
    @FXML
    public void saveChangedCustomer() {

        boolean errorPresent = false;
        String errorMessage = "";

        Customer current = Main.store.getCustomers().get(customerIndex);

        nameEdit.selectAll();
        nameEdit.copy();
        String name = nameEdit.getText();
        if (name.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing name field";
        }
        current.setName(name);

        phoneNumberEdit.selectAll();
        phoneNumberEdit.copy();
        String phone = phoneNumberEdit.getText();
        if (phone.isBlank() || !phone.replace("-", "").matches("\\d+")) {
            errorPresent = true;
            errorMessage = "Phone number must contain only numbers a dashes";
        }
        current.setPhone(phone);

        addressEdit.selectAll();
        addressEdit.copy();
        String address = addressEdit.getText();
        if (address.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing address field";
        }
        current.setAddress(address);

        postalCodeEdit.selectAll();
        postalCodeEdit.copy();
        String postal = postalCodeEdit.getText();
        if (postal.isBlank()) {
            errorPresent = true;
            errorMessage = "Missing postal field";
        }
        current.setPostal(postal);

        int div = 0;
        try {
            String firstLevelText = chooseFirstLevelEdit.getValue().toString();

            for (FirstLevelDivision f : Main.store.getFirstLevels()) {
                if (f.getName().equals(firstLevelText)) {
                    div = f.getId();
                }
            }
            current.setDivisionId(div);
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing division field";
        }

        if (!errorPresent) {
            //commit changes to mysql db
            Main.store.updateCustomer(customerIndex, current);
            errorLabel.setText("Updated customer record successfully");
        } else {
            errorLabel.setText(errorMessage);
        }
    }

    /**
     * Method for UI to fill the customer combobox with options.
     */ 
    public void fillCustomerBox() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Customer c : Main.store.getCustomers()) {
            ob.add(c.getId() + ". " + c.getName());
        }
        chooseCustomerEdit.setItems(ob);
    }

    /**
     *  Grabs selected customer and fills fields with related data.
     */
    @FXML
    public void getCustomerToEdit() {
        String choice = chooseCustomerEdit.getValue().toString();
        for (Customer c : Main.store.getCustomers()) {
            if ((c.getId() + ". " + c.getName()).equals(choice)) {
                //fill out data fields
                customerIdEdit.setText(String.valueOf(c.getId()));
                nameEdit.setText(c.getName());
                phoneNumberEdit.setText(c.getPhone());
                addressEdit.setText(c.getAddress());
                postalCodeEdit.setText(c.getPostal());
                FirstLevelDivision firstLevel = c.getFirstLevel();
                Country country = c.getCountry(firstLevel.getCountryId());
                chooseCountryEdit.setValue(country.getName());
                chooseFirstLevelEdit.setValue(firstLevel.getName());
                fillFirstLevelBoxes(country);
                customerIndex = Main.store.getCustomers().indexOf(c);
            }
        }
    }

    /**
     * Sets the desired country value and populates the FLD box with appropriate values.
     */
    @FXML
    public void setCountryChosenEdit() {
        String text = chooseCountryEdit.getValue().toString();
        for (Country c : Main.store.getCountries()) {
            if (c.getName().equals(text)) {
                ObservableList<String> divList = FXCollections.observableArrayList();
                for (FirstLevelDivision f : c.getDivList()) {
                    divList.add(f.getName());
                }
                chooseFirstLevelEdit.setItems(divList);
                chooseFirstLevelEdit.setDisable(false);
            }
        }

    }
    
    /**
     * Method for UI to setup table cells.
     */
    public void setupCustomerTable() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setStyle("-fx-alignment: CENTER");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerNameColumn.setStyle("-fx-alignment: CENTER");
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerAddressColumn.setStyle("-fx-alignment: CENTER");
        customerPostalColumn.setCellValueFactory(new PropertyValueFactory<>("postal"));
        customerPostalColumn.setStyle("-fx-alignment: CENTER");
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerPhoneColumn.setStyle("-fx-alignment: CENTER");
        customerDivIdColumn.setCellValueFactory(new PropertyValueFactory<>("firstLevelData"));
        customerDivIdColumn.setStyle("-fx-alignment: CENTER");
    }

    /**
     * Method for UI to fill the country comboboxes with choices.
     */
    public void fillCountryBoxes() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Country c : Main.store.getCountries()) {
            ob.add(c.getName());
        }
        chooseCountryNew.setItems(ob);
        chooseCountryEdit.setItems(ob);
    }

    /**
     * Method for UI to fill the FLD comboboxes with options given a country.
     * 
     * @param country Associated country to find FLDs for
     */
    public void fillFirstLevelBoxes(Country country) {
        ObservableList ob = FXCollections.observableArrayList();
        for (FirstLevelDivision f : Main.store.getFirstLevels()) {
            if (f.getCountryId() == country.getId()) {
                ob.add(f.getName());
            }
        }
        chooseFirstLevelEdit.setItems(ob);
    }
}
