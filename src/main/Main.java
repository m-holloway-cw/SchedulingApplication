//Javadoc comment files located in zipped javadoc file attached
package c195;


import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The application provides an interface to interact with and update
 * appointments and customer records stored in a MySQL database.
 *
 * @author Michael Holloway
 */
public class Main extends Application {

    public static DataStore store;


    /**
     * Starting point for the JavaFX application
     *
     * @param stage the FXML stage produced by JavaFX
     * @throws Exception initial failure flag if something fails on start
     */
    @Override
    public void start(Stage stage) throws Exception {
        //setup language information for login page
        ResourceBundle bundle = ResourceBundle.getBundle("resources.language", Locale.getDefault());
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"), bundle);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Entry point for the JVM
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLDataStore.SQL sql = SQLDataStore.loadSQL();
        store = new SQLDataStore(sql);
        launch(args);
    }

}
