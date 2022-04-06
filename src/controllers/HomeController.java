package controllers;

import c195.Appointment;
import c195.Main;
import c195.User;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class for the home page of the application.
 *
 * @author Michael Holloway
 */
public class HomeController implements Initializable {

    @FXML
    Label usernameLabel;

    @FXML
    Label upcomingApptLabel;

    @FXML
    private TableView<Appointment> weeklyTable;

    @FXML
    private TableColumn<Appointment, Integer> weeklyApptIdColumn;

    @FXML
    private TableColumn<Appointment, String> weeklyApptTitleColumn;

    @FXML
    private TableColumn<Appointment, String> weeklyApptDescColumn;

    @FXML
    private TableColumn<Appointment, String> weeklyApptLocationColumn;

    @FXML
    private TableColumn<Appointment, String> weeklyApptContactColumn;

    @FXML
    private TableColumn<Appointment, String> weeklyApptTypeColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> weeklyApptStartColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> weeklyApptEndColumn;

    @FXML
    private TableColumn<Appointment, Integer> weeklyApptCustomerIdColumn;

    @FXML
    private TableView<Appointment> monthlyTable;

    @FXML
    private TableColumn<Appointment, Integer> monthlyApptIdColumn;

    @FXML
    private TableColumn<Appointment, String> monthlyApptTitleColumn;

    @FXML
    private TableColumn<Appointment, String> monthlyApptDescColumn;

    @FXML
    private TableColumn<Appointment, String> monthlyApptLocationColumn;

    @FXML
    private TableColumn<Appointment, String> monthlyApptContactColumn;

    @FXML
    private TableColumn<Appointment, String> monthlyApptTypeColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> monthlyApptStartColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> monthlyApptEndColumn;

    @FXML
    private TableColumn<Appointment, Integer> monthlyApptCustomerIdColumn;

    private User currentUser;

    /**
     * Initializes the controller class.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentUser = Main.store.getCurrentUser();
        usernameLabel.setText("User #" + currentUser.getId() + ": " + currentUser.getUsername());

        setupWeeklyTable();

        weeklyTable.setItems(Main.store.getWeeklyAppointments());

        setupMonthlyTable();
        monthlyTable.setItems(Main.store.getMonthlyAppointments());

        upcomingApptLabel.setText("");
        checkUpcoming();
    }

    /**
     * Logout of application, popup alert confirmation.
     */
    @FXML
    public void logout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setContentText("Are you sure you wish to logout?");
        Optional<ButtonType> choice = confirmation.showAndWait();
        if (choice.get() == ButtonType.OK) {
            try {
                Stage primary = (Stage) usernameLabel.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/Login.fxml"));
                Parent addScreen = loader.load();
                Scene scene = new Scene(addScreen);
                primary.setScene(scene);
                primary.show();
            } catch (IOException io) {
                upcomingApptLabel.setText("Error occured trying to log out");
            }
        } else {
            //cancel
        }
    }

    /**
     * Method for the UI to setup the table cells for the weekly table.
     */
    public void setupWeeklyTable() {
        weeklyApptIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        weeklyApptIdColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        weeklyApptTitleColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptDescColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        weeklyApptDescColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        weeklyApptLocationColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        weeklyApptContactColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        weeklyApptTypeColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        weeklyApptStartColumn.setCellFactory(s -> new TableCell<Appointment, LocalDateTime>() {
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

        weeklyApptStartColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        weeklyApptEndColumn.setCellFactory(e -> new TableCell<Appointment, LocalDateTime>() {
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
        weeklyApptEndColumn.setStyle("-fx-alignment: CENTER");
        weeklyApptCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        weeklyApptCustomerIdColumn.setStyle("-fx-alignment: CENTER");
    }

    /**
     * Method for the UI to setup the table cells for the monthly table.
     */
    public void setupMonthlyTable() {
        monthlyApptIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        monthlyApptIdColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        monthlyApptTitleColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptDescColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        monthlyApptDescColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        monthlyApptLocationColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        monthlyApptContactColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthlyApptTypeColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        monthlyApptStartColumn.setCellFactory(s -> new TableCell<Appointment, LocalDateTime>() {
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

        monthlyApptStartColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        monthlyApptEndColumn.setCellFactory(e -> new TableCell<Appointment, LocalDateTime>() {
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
        monthlyApptEndColumn.setStyle("-fx-alignment: CENTER");
        monthlyApptCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        monthlyApptCustomerIdColumn.setStyle("-fx-alignment: CENTER");
    }


    /**
     * Method to check is the logged-in user has any appointments in the next 15 minutes.
     */
    public void checkUpcoming() {   
        boolean apptFound = false;
        for (Appointment appt : Main.store.getAppointments()) {
            LocalDateTime start = appt.getStart();
            LocalDateTime now = LocalDateTime.now();

            long diffMinutes = Duration.between(now, start).toMinutes();
            
            if (diffMinutes < 15 && start.isAfter(now)) {
                //show alert with appt info
                apptFound = true;
                Alert confirmation = new Alert(Alert.AlertType.WARNING);
                confirmation.setTitle("Warning");
                confirmation.setHeaderText("Appointment in next 15 minutes:");
                LocalDate date = appt.getStart().toLocalDate();

                LocalTime startTime = appt.getStart().toLocalTime();
                LocalTime endTime = appt.getEnd().toLocalTime();

                String content = "ID: " + appt.getId() + " \nDate: " + date + " \nStart time: " + startTime + " \nEnd time: " + endTime;
                confirmation.setContentText(content);
                confirmation.show();
            } 
        }
        if(!apptFound){
                upcomingApptLabel.setText("No appointments found in next 15 minutes");           
        }
    }

    /**
     * Opens the appointment management page in a new window.
     * Holds focus until exited to prevent duplicate openings.
     */
    @FXML
    public void openApptManager() {
        try {

            Stage apptWindow = new Stage();
            apptWindow.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../fxml/Appointment.fxml"));
            Parent addScreen = loader.load();

            Scene scene = new Scene(addScreen);

            apptWindow.setScene(scene);
            apptWindow.showAndWait();

            //after closing get the user object again and refresh tables
            weeklyTable.setItems(Main.store.getWeeklyAppointments());

            monthlyTable.setItems(Main.store.getMonthlyAppointments());
            
        } catch (IOException io) {
            io.printStackTrace();
            upcomingApptLabel.setText("Error occured trying to open the appointment manager");
        }
    }

    /**
     * Opens the customer record manager in a new window.
     * Holds focus to prevent duplicate openings.
     */
    @FXML
    public void openCustomerManager() {
        try {
            Stage custWindow = new Stage();
            custWindow.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/CustomerRecord.fxml"));
            Parent addScreen = loader.load();
            Scene scene = new Scene(addScreen);
            custWindow.setScene(scene);
            custWindow.show();
        } catch (IOException io) {
            upcomingApptLabel.setText("Error occured trying to open the customer manager");
        }
    }

    /**
     * Opens a window to display specific information about appointment totals.
     */
    @FXML
    public void openStats() {
        try {
            Stage primary = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ApptStats.fxml"));
            Parent addScreen = loader.load();
            Scene scene = new Scene(addScreen);
            primary.setScene(scene);
            primary.show();
        } catch (IOException io) {
            io.printStackTrace();
            upcomingApptLabel.setText("Error occured trying to open the appointment stats");
        }
    }

    /**
     * Opens a read-only appointment viewer for all known contacts.
     */
    @FXML
    public void openContactSchedule() {
        try {
            Stage primary = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ContactSchedule.fxml"));
            Parent addScreen = loader.load();
            Scene scene = new Scene(addScreen);
            primary.setScene(scene);
            primary.show();
        } catch (IOException io) {
            io.printStackTrace();
            upcomingApptLabel.setText("Error occured trying to open the contact schedule");
        }
    }
    
}
