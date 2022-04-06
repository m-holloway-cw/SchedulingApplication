package controllers;

import c195.Main;
import c195.User;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class for the initial log-in page.
 *
 * @author Michael Holloway
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label locationLabel;


    /**
     * Initializes the controller class.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
        String locationInfo = ZoneId.systemDefault().toString();
        locationLabel.setText(locationInfo);

    }

    /**
     * Begins the process of logging in.
     */
    @FXML
    public void submitLogin() {
        usernameTextField.selectAll();
        usernameTextField.copy();
        String username = usernameTextField.getText();
        passwordTextField.selectAll();
        passwordTextField.copy();
        String password = passwordTextField.getText();
        Locale l = Locale.getDefault();
        String language = l.getDisplayLanguage();
        if (username.isBlank() || password.isBlank()) {
            if (language.equalsIgnoreCase("français")) {
                errorLabel.setText("Nom d'utilisateur ou mot de passe manquant");
            } else {
                errorLabel.setText("Missing username or password");
            }           
        } else {
            checkLogin(username, password);
        }
    }

    /**
     * Checks given username + password against database.If successful, sends
 the user to the home page.
     * @param username string username entered into textfield
     * @param password string password entered into textfield
     */
    public void checkLogin(String username, String password) {
        List<User> users = Main.store.getUsers();
        Locale l = Locale.getDefault();
        String language = l.getDisplayLanguage();
        boolean success = false;
        for (User u : users) {
            if (username.equals(u.getUsername()) && password.equals(u.getPassword())) {
                //send to homepage of application
                try {
                    //set values for this user
                    String locationInfo = ZoneId.systemDefault().toString();
                    u.setLocationInfo(locationInfo);

                    u.setLanguageInfo(language);

                    //set current user in the store
                    Main.store.setCurrentUser(u);

                    //flag for log method
                    success = true;

                    Stage primary = (Stage) usernameTextField.getScene().getWindow();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/Home.fxml"));
                    Parent addScreen = loader.load();
                    Scene scene = new Scene(addScreen);
                    primary.setScene(scene);
                    primary.show();

                } catch (IOException ie) {
                    if (language.equalsIgnoreCase("français")) {
                        success = false;
                        errorLabel.setText("Une erreur s'est produite lors de l'ouverture de l'écran d'accueil");
                    } else {
                        success = false;
                        errorLabel.setText("Error trying to open main menu");
                    }
                }
            }
        }
        //if username/password are correct log as true
        //if are not correct, log as false, show error message
        if (success) {
            Main.store.logAttempt(username, true);
        } else {
            if (language.equalsIgnoreCase("français")) {
                //log data for this transcation
                Main.store.logAttempt(username, false);
                errorLabel.setText("L'identifiant ou le mot de passe est incorrect");
            } else {
                //log data for this transcation
                Main.store.logAttempt(username, false);
                errorLabel.setText("Username or password is incorrect");
            }
        }
    }

}
