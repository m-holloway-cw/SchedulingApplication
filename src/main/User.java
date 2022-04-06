package c195;


/**
 * Class to hold the user object 
 * 
 * @author Michael Holloway
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String locationInfo;
    private String langaugeInfo;

    /**
     * Constructor for the User object
     * 
     * @param id identification number for this object
     * @param username name used to log-in
     * @param password password used to log-in
     * @param locationInfo data used to determine user's location at log-in
     * @param languageInfo used to translate if needed
     */
    public User(int id, String username, String password,  String locationInfo, String languageInfo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.locationInfo = locationInfo;
        this.langaugeInfo = languageInfo;
    }

    /**
     * Method to get the identification number assigned to this user.
     * 
     * @return integer id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Method to get the username assigned to this user.
     * 
     * @return string name
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Method to get the password assigned to this user.
     * Only meant to be used for logging in.
     * 
     * @return string password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Method to get the location of this use.
     * 
     * @return string representation of the location
     */
    public String getLocationInfo() {
        return this.locationInfo;
    }

    /**
     * Method to set this user's location.
     * Used on log-in for configuration purposes.
     * 
     * @param location string representation of the user's location
     */
    public void setLocationInfo(String location) {
        this.locationInfo = location;
    }

    /**
     * Method to get the user's language.
     * Set by PC settings.
     * 
     * @return string representation of the language in use
     */
    public String getLanguageInfo() {
        return this.langaugeInfo;
    }

    /**
     * Method to set the language for this user.
     * 
     * @param language string representation of the language in use
     */
    public void setLanguageInfo(String language) {
        this.langaugeInfo = language;
    }

}
