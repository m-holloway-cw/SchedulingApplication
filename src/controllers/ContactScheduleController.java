package controllers;

import c195.Appointment;
import c195.Contact;
import c195.Main;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class for viewing contact schedules.
 * No methods provided to alter data, read-only.
 * 
 * @author Michael Holloway
 */
public class ContactScheduleController implements Initializable {

    @FXML
    private ComboBox contactBox;

    @FXML
    private TableView<Appointment> contactTable;

    @FXML
    private TableColumn<Appointment, Integer> apptIdColumn;

    @FXML
    private TableColumn<Appointment, String> apptTitleColumn;

    @FXML
    private TableColumn<Appointment, String> apptDescColumn;

    @FXML
    private TableColumn<Appointment, String> apptLocationColumn;

    @FXML
    private TableColumn<Appointment, String> apptContactColumn;

    @FXML
    private TableColumn<Appointment, String> apptTypeColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> apptStartColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> apptEndColumn;

    @FXML
    private TableColumn<Appointment, Integer> apptCustomerIdColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();

        fillContacts();
    }

    /**
     * Method for the UI to fill the contact combobox.
     */
    public void fillContacts() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Contact c : Main.store.getContacts()) {
            ob.add(c.getName());
        }
        contactBox.setItems(ob);
    }

    /**
     * Method for the UI to force settings for the table cells.
     */
    public void setupTable() {
        apptIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptIdColumn.setStyle("-fx-alignment: CENTER");
        apptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTitleColumn.setStyle("-fx-alignment: CENTER");
        apptDescColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        apptDescColumn.setStyle("-fx-alignment: CENTER");
        apptTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptTypeColumn.setStyle("-fx-alignment: CENTER");
        apptStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptStartColumn.setCellFactory(s -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    setText(time.format(formatter));
                }
            }
        });

        apptStartColumn.setStyle("-fx-alignment: CENTER");
        apptEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptEndColumn.setCellFactory(e -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    setText(time.format(formatter));
                }
            }
        });
        apptEndColumn.setStyle("-fx-alignment: CENTER");
        apptCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptCustomerIdColumn.setStyle("-fx-alignment: CENTER");
    }

    /**
     * Fills data fields, activated by the contact combobox.
     */
    @FXML
    public void chooseContact() {
        String choice = contactBox.getValue().toString();
        ObservableList<Appointment> aList = FXCollections.observableArrayList();
        for (Appointment a : Main.store.getAppointments()) {
            if (a.getContactName().equals(choice)) {
                aList.add(a);
            }
        }
        contactTable.getItems().clear();
        contactTable.getItems().addAll(aList);
    }

}
