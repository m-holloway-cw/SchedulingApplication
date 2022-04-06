package controllers;

import c195.Appointment;
import c195.Contact;
import c195.Customer;
import c195.Main;
import c195.User;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class for appointment manipulation.
 *
 * @author Michael Holloway
 */
public class AppointmentController implements Initializable {

    @FXML
    private Label errorLabel;

    //fxml components for the add tab
    @FXML
    private TextField appointmentIdNew;
    @FXML
    private TextField titleNew;
    @FXML
    private TextField typeNew;
    @FXML
    private TextField descriptionNew;
    @FXML
    private TextField locationNew;
    @FXML
    private DatePicker startDateNew;
    @FXML
    private ComboBox startTimeNew;
    @FXML
    private DatePicker endDateNew;
    @FXML
    private ComboBox endTimeNew;
    @FXML
    private ComboBox contactNew;
    @FXML
    private ComboBox customerIdNew;
    @FXML
    private ComboBox userIdNew;

    //fxml components for update/delete tab
    @FXML
    private ComboBox editChoice;
    @FXML
    private TextField appointmentIdEdit;
    @FXML
    private TextField titleEdit;
    @FXML
    private TextField typeEdit;
    @FXML
    private TextField descriptionEdit;
    @FXML
    private TextField locationEdit;
    @FXML
    private DatePicker startDateEdit;
    @FXML
    private ComboBox startTimeEdit;
    @FXML
    private DatePicker endDateEdit;
    @FXML
    private ComboBox endTimeEdit;
    @FXML
    private ComboBox contactEdit;
    @FXML
    private ComboBox customerIdEdit;
    @FXML
    private ComboBox userIdEdit;

    private static String createdUser;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

//load data into the combo boxes
        fillEditAppointments();
        fillNewContacts();
        fillNewCustomers();
        fillNewUsers();
        fillTimeBoxes();
        appointmentIdNew.setText(String.valueOf(Main.store.getNextAppointmentIdNumber()));
        createdUser = "";
    }

    /**
     * Method for the UI to send a new appointment to the database.
     */
    @FXML
    public void createNewAppointment() {
        boolean errorPresent = false;
        String errorMessage = "";

        int id = Integer.parseInt(getText(appointmentIdNew));
        String title = getText(titleNew);
        if (title.isBlank()) {
            errorPresent = true;
            errorMessage = "Title cannot be blank";
        }

        String desc = getText(descriptionNew);
        if (desc.isBlank()) {
            errorPresent = true;
            errorMessage = "Description cannot be blank";
        }
        
        String location = getText(locationNew);
        if (location.isBlank()) {
            errorPresent = true;
            errorMessage = "Location cannot be blank";
        }
        
        String type = getText(typeNew);
        if (type.isBlank()) {
            errorPresent = true;
            errorMessage = "Type cannot be blank";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu");

        LocalTime startTime = null;
        try {
            startTime = LocalTime.parse(startTimeNew.getValue().toString());
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing starting time";
        }

        LocalDate startDate = null;
        try {
            startDate = startDateNew.getValue();
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing starting Date";
        }

        try {
            startDateNew.getEditor().selectAll();
            startDateNew.getEditor().copy();
            startDate = LocalDate.parse(startDateNew.getEditor().getText(), formatter);
        } catch (DateTimeParseException e) {
            errorPresent = true;
            errorMessage = "Incorrect starting date format, must be month/day/year";
        }

        LocalDateTime start = LocalDateTime.of(startDate, startTime);

        LocalTime endTime = null;
        try {
            endTime = LocalTime.parse(endTimeNew.getValue().toString());
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing ending time";
        }

        LocalDate endDate = null;
        try {
            endDate = endDateNew.getValue();
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing end date";
        }
        try {
            endDateNew.getEditor().selectAll();
            endDateNew.getEditor().copy();
            endDate = LocalDate.parse(endDateNew.getEditor().getText(), formatter);
        } catch (Exception e) {
            errorPresent = true;
            errorMessage = "Incorrect ending date format, must be month/day/year";
        }

        LocalDateTime end = LocalDateTime.of(endDate, endTime);

        String createdBy = Main.store.getCurrentUser().getUsername();
        
        int customerId = 0;
        try {
            String fullCustomerId = customerIdNew.getValue().toString();
            fullCustomerId = fullCustomerId.substring(0, fullCustomerId.indexOf("."));
            customerId = Integer.parseInt(fullCustomerId);
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing the Customer ID Field";
        }

        int userId = 0;
        try {
            String fullUserId = userIdNew.getValue().toString();
            fullUserId = fullUserId.substring(0, fullUserId.indexOf("."));
            userId = Integer.parseInt(fullUserId);
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing the User ID Field";
        }

        int contactId = 0;
        try {
            String fullContactId = contactNew.getValue().toString();
            fullContactId = fullContactId.substring(0, fullContactId.indexOf("."));
            contactId = Integer.parseInt(fullContactId);
        } catch (NullPointerException ne) {
            errorPresent = true;
            errorMessage = "Missing the Contact ID Field";
        }

        //check for errors
        //make sure end is after start and after current time
        if (end.isBefore(start) && start.isAfter(LocalDateTime.now())) {
            errorMessage = "Error: ending date/time must be after starting date/time";
            errorPresent = true;
        }

        //make sure attached customer does not have overlapping appointments
        StringBuilder sb = new StringBuilder();
        sb.append("Overlapping appointment ID's found: [");
        boolean attachedApptFlag = false;
        for (Appointment appt : Main.store.getAppointments()) {
            if (appt.getCustomerId() == customerId) {
                //overlap exists if the start of any appointment is before the end of proposed appt
                //AND if the end of the same appointment is after the start of proposed appt
                if (appt.getStart().plusSeconds(1).isBefore(end) && appt.getEnd().minusSeconds(1).isAfter(start)) {
                    attachedApptFlag = true;
                    sb.append(appt.getId()).append("],[");

                }
            }
        }
        if (attachedApptFlag) {
            sb.delete(sb.lastIndexOf("["), sb.length());
            //error label of sb
            errorLabel.setText(sb.toString());
        } else {
            if (!errorPresent) {
                Appointment newAppt = new Appointment(
                        id, title, desc, location, type,
                        start, end, createdBy, customerId,
                        userId, contactId);

                Main.store.addAppointment(newAppt);
                errorLabel.setText("Appointment created successfully");
            } else {
                errorLabel.setText(errorMessage);
            }
            fillEditAppointments();
        }
    }

    /**
     * Removes the selected appointment from the database.
     */
    @FXML
    public void deleteSelectedAppointment() {
        //alert to confirm
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        appointmentIdEdit.selectAll();
        appointmentIdEdit.copy();
        int id = Integer.parseInt(appointmentIdEdit.getText());
        typeEdit.selectAll();
        typeEdit.copy();
        String type = typeEdit.getText();
        confirmation.setContentText("Are you sure you want to delete this appointment?\nID: " + id + "\nType: " + type);
        Optional<ButtonType> choice = confirmation.showAndWait();
        if (choice.get() == ButtonType.OK) {
            Main.store.deleteAppointment(id);
            errorLabel.setText("Deleted appointment id: " + id);
        } else {
            //cancel
        }
        fillEditAppointments();
    }

    /**
     * Gets the selected appointment and fills all related data fields
     */
    @FXML
    public void getAppointmentToEdit() {
        try {
            String choice = editChoice.getValue().toString();
            for (Appointment a : Main.store.getAppointments()) {
                if ((a.getId() + ". " + a.getTitle()).equals(choice)) {
                    appointmentIdEdit.setText(String.valueOf(a.getId()));
                    titleEdit.setText(a.getTitle());
                    typeEdit.setText(a.getType());
                    descriptionEdit.setText((a.getDesc()));
                    locationEdit.setText(a.getLocation());

                    LocalDateTime startDate = a.getStart();

                    LocalDateTime endDate = a.getEnd();

                    startDateEdit.setValue(startDate.toLocalDate());

                    endDateEdit.setValue(endDate.toLocalDate());

                    startTimeEdit.setValue(startDate.toLocalTime());
                    endTimeEdit.setValue(endDate.toLocalTime());
                    
                    createdUser = a.getCreatedBy();

                    contactEdit.setValue(a.getContactId() + ". " + a.getContactName());
                    String customerName = "";
                    for (Customer c : Main.store.getCustomers()) {
                        if (c.getId() == a.getCustomerId()) {
                            customerName = c.getName();
                        }
                    }
                    customerIdEdit.setValue(a.getCustomerId() + ". " + customerName);
                    String userName = "";
                    for (User u : Main.store.getUsers()) {
                        if (u.getId() == a.getUserId()) {
                            userName = u.getUsername();
                        }
                    }
                    userIdEdit.setValue(a.getUserId() + ". " + userName);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /*
    ** Interface to handle a lambda expression
     */
    interface Index {

        public int getIndex(int id);
    }

    /**
     * Updates the chosen appointment.
     *
     * Lambda expression implemented in the saveChangedAppointment method to
     * retrieve the index of the old appointment object. In use here to extrude
     * the required value without implementing a full independent method.
     */
    @FXML
    public void saveChangedAppointment() {
        int apptIndex;
        boolean errorPresent = false;
        String errorMessage = "";
        appointmentIdEdit.selectAll();
        appointmentIdEdit.copy();
        int apptId = Integer.parseInt(appointmentIdEdit.getText());

        //lambda to get index
        Index i = id -> {
            for (Appointment a : Main.store.getAppointments()) {
                if (a.getId() == id) {
                    return Main.store.getAppointments().indexOf(a);
                }
            }
            return -1;
        };
        apptIndex = i.getIndex(apptId);

        if (apptIndex > -1) {
            //check for errors/blank fields along the way          
            String title = getText(titleEdit);
            if (title.isBlank()) {
                errorPresent = true;
                errorMessage = "Missing title field";
            }

            String desc = getText(descriptionEdit);
            if (desc.isBlank()) {
                errorPresent = true;
                errorMessage = "Missing description field";
            }

            String location = getText(locationEdit);
            if (location.isBlank()) {
                errorPresent = true;
                errorMessage = "Missing location field";
            }

            String type = getText(typeEdit);
            if (type.isBlank()) {
                errorPresent = true;
                errorMessage = "Missing title field";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu");

            LocalTime startTime = null;
            try {
                startTime = LocalTime.parse(startTimeEdit.getValue().toString());
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing start time";
            }

            LocalDate startDate = null;
            try {
                startDate = startDateEdit.getValue();
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing end date";
            }
            try {
                startDateEdit.getEditor().selectAll();
                startDateEdit.getEditor().copy();
                startDate = LocalDate.parse(startDateEdit.getEditor().getText(), formatter);
            } catch (Exception e) {
                errorPresent = true;
                errorMessage = "Incorrect starting date format, must be month/day/year";
            }

            LocalDateTime start = LocalDateTime.of(startDate, startTime);

            LocalTime endTime = null;

            try {
                endTime = LocalTime.parse(endTimeEdit.getValue().toString());
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing end time";
            }

            LocalDate endDate = null;
            try {
                endDate = endDateEdit.getValue();
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing end date";
            }
            try {
                endDateEdit.getEditor().selectAll();
                endDateEdit.getEditor().copy();
                endDate = LocalDate.parse(endDateEdit.getEditor().getText(), formatter);
            } catch (Exception e) {
                e.printStackTrace();
                errorPresent = true;
                errorMessage = "Incorrect ending date format, must be month/day/year";
            }
            LocalDateTime end = LocalDateTime.of(endDate, endTime);

            int customerId = 0;
            try {
                String fullCustomerId = customerIdEdit.getValue().toString();
                fullCustomerId = fullCustomerId.substring(0, fullCustomerId.indexOf("."));
                customerId = Integer.parseInt(fullCustomerId);
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing the Customer ID Field";
            }

            int userId = 0;
            try {
                String fullUserId = userIdEdit.getValue().toString();
                fullUserId = fullUserId.substring(0, fullUserId.indexOf("."));
                userId = Integer.parseInt(fullUserId);
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing the User ID Field";
            }

            int contactId = 0;
            try {
                String fullContactId = contactEdit.getValue().toString();
                fullContactId = fullContactId.substring(0, fullContactId.indexOf("."));
                contactId = Integer.parseInt(fullContactId);
            } catch (NullPointerException ne) {
                errorPresent = true;
                errorMessage = "Missing the Contact ID Field";
            }
            //check for errors
            //make sure end is after start and after current time
            if (end.isBefore(start) && start.isAfter(LocalDateTime.now())) {
                errorMessage = "Error: starting time must be before ending time";
                errorPresent = true;
            }

            //make sure attached customer does not have overlapping appointments
            StringBuilder sb = new StringBuilder();
            sb.append("Attached appointment ID's found: [");
            boolean attachedApptFlag = false;
            for (Appointment appt : Main.store.getAppointments()) {
                if (appt.getId() != apptId) {
                    if (appt.getCustomerId() == customerId) {
                        //overlap exists if the start of any appointment is before the end of proposed appt
                        //AND if the end of the same appointment is after the start of proposed appt
                        if (appt.getStart().plusSeconds(1).isBefore(end) && appt.getEnd().minusSeconds(1).isAfter(start)) {
                            attachedApptFlag = true;
                            sb.append(appt.getId()).append("],[");
                        }
                    }
                }
            }
            if (attachedApptFlag) {
                sb.delete(sb.lastIndexOf("["), sb.length());
                errorLabel.setText(sb.toString());
            } else {
                if (!errorPresent) {
                    Appointment apptReplacement = new Appointment(
                            apptId, title, desc, location, type,
                            start, end, createdUser, customerId,
                            userId, contactId);
                    if (Main.store.replaceAppointment(apptIndex, apptReplacement)) {
                        errorLabel.setText("Edit to appointment successful");
                    } else {
                        errorLabel.setText("Error occured editing appointment, check length of text");
                    }
                } else {
                    errorLabel.setText(errorMessage);
                }
            }
        } else {
            errorLabel.setText("Error occured trying to find the appointment");
        }
        fillEditAppointments();
    }

    /**
     * Method for the UI to fill the edit appointment combo box.
     * 
     */
    public void fillEditAppointments() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Appointment a : Main.store.getAppointments()) {
            ob.add(a.getId() + ". " + a.getTitle());
        }
        editChoice.getItems().clear();
        editChoice.setItems(ob);
    }

    /**
     * Method for the UI to fill the contact comboboxes.
     */
    public void fillNewContacts() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Contact c : Main.store.getContacts()) {
            ob.add(c.getId() + ". " + c.getName());
        }
        contactNew.setItems(ob);
        contactEdit.setItems(ob);
    }

    /**
     * Method for the UI to fill the customer comboboxes.
     */
    public void fillNewCustomers() {
        ObservableList ob = FXCollections.observableArrayList();
        for (Customer c : Main.store.getCustomers()) {
            ob.add(c.getId() + ". " + c.getName());
        }
        customerIdNew.setItems(ob);
        customerIdEdit.setItems(ob);
    }

    /**
     * Method for the UI to fill the user comboboxes.
     */
    public void fillNewUsers() {
        ObservableList ob = FXCollections.observableArrayList();
        for (User u : Main.store.getUsers()) {
            ob.add(u.getId() + ". " + u.getUsername());
        }
        userIdNew.setItems(ob);
        userIdEdit.setItems(ob);
    }

    /**
     * Method for the UI to fill the time boxes.
     * Implemented using time bounds to prevent scheduling outside business hours.
     */
    public void fillTimeBoxes() {
        try {
            startTimeNew.setItems(FXCollections.observableArrayList(Main.store.getPossibleStartTimes()));
            startTimeEdit.setItems(FXCollections.observableArrayList(Main.store.getPossibleStartTimes()));
            endTimeNew.setItems(FXCollections.observableArrayList(Main.store.getPossibleEndTimes()));
            endTimeEdit.setItems(FXCollections.observableArrayList(Main.store.getPossibleEndTimes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to extract the string from a text field.
     * @param tf the Textfield from UI
     * @return a string of the text within the textfield
    */
    public String getText(TextField tf) {
        tf.selectAll();
        tf.copy();
        return tf.getText();
    }

}
