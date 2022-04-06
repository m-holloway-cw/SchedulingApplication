package controllers;

import c195.Appointment;
import c195.Main;
import c195.User;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

/**
 * FXML Controller class for view appointment statistics.
 *
 * @author Michael Holloway
 */
public class ApptStatsController implements Initializable {

    private static ObservableList<Map<String, Object>> data = FXCollections.<Map<String, Object>>observableArrayList();
    private static ObservableList<Map<String, Object>> userData = FXCollections.<Map<String, Object>>observableArrayList();

    @FXML
    Label nowLabel;

    @FXML
    private TableView statsTableView;

    @FXML
    private TableColumn<Map, String> typeColumn;

    @FXML
    private TableColumn<Map, Integer> monthAmountColumn;

    @FXML
    private TableView userTableView;

    @FXML
    private TableColumn<Map, String> userColumn;

    @FXML
    private TableColumn<Map, Integer> userAmountColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getData();

        setupTableView();

        statsTableView.getItems().addAll(data);
        userTableView.getItems().addAll(userData);
    }
    
    /**
     * Method to set the table cell settings.
     */
    public void setupTableView() {
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        typeColumn.setStyle("-fx-alignment: CENTER");
        monthAmountColumn.setCellValueFactory(new MapValueFactory<>("total"));
        monthAmountColumn.setStyle("-fx-alignment: CENTER");

        userColumn.setCellValueFactory(new MapValueFactory<>("user"));
        userColumn.setStyle("-fx-alignment: CENTER");
        userAmountColumn.setCellValueFactory(new MapValueFactory<>("total"));
        userAmountColumn.setStyle("-fx-alignment: CENTER");
    }

    /*
    ** Lambda interface for getting the totals
    */
    interface Count{
        public long getCount(String type);
    }
    
    /**
     * Method to bring in data from the DataStore.
     * 
     * Lambda expression in use here to simplify obtaining the count 
     * for a particular type. Used in a for loop to search for matches of each 
     * appointment type. The filter in use with the list stream for appointments
     * provides a clean and simple approach to getting required data. 
     */
    public void getData() {
        //fill type data side
        List<String> typesAvailable = new ArrayList();
        for (Appointment appt : Main.store.getAppointments()) {
            if (!typesAvailable.contains(appt.getType()) && LocalDate.now().getMonthValue() == appt.getStart().getMonthValue()) {
                typesAvailable.add(appt.getType());
            }
        }

        for (String typeName : typesAvailable) {
            Map<String, Object> typeMap = new HashMap<>();
            
            //lambda to get count
            Count countLambda = c -> {
                return Main.store.getAppointments().stream().filter(type -> c.equals(type.getType())).count();
            };
            long count = countLambda.getCount(typeName);

            typeMap.put("type", typeName);
            typeMap.put("total", count);
            data.add(typeMap);
        }

        //fill user data side
        List<String> usersPossible = new ArrayList();
        for(User u: Main.store.getUsers()){
            if(!usersPossible.contains(u.getUsername())){
                usersPossible.add(u.getUsername());
            }
        }
        
        for(String username: usersPossible){
            Map<String, Object> userMap = new HashMap<>();
            long count = Main.store.getAppointments().stream().filter(appt -> username.equals(appt.getCreatedBy())).count();
            userMap.put("user", username);
            userMap.put("total", count);
            userData.add(userMap);
        }
        
        //set current month and year
        String current = LocalDate.now().getMonth() + ", " + LocalDate.now().getYear();
        nowLabel.setText(current);
    }

}
