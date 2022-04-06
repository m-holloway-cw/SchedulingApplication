package c195;

import java.time.LocalDateTime;


/**
 * Appointment class to hold all data relevant to this application.
 * Provides getters/setters for all private data.
 * 
 * @author Michael Holloway
 */
public class Appointment {

    private int id;
    private String title;
    private String desc;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String createdBy;
    private int customerId;
    private int userId;
    private int contactId;
    
    /**
     * Main constructor for the application class. 
     * 
     * @param id appointment identification number
     * @param title string title of the appointment
     * @param desc string description of the appointment
     * @param location string location assigned
     * @param type string type of appointment being created
     * @param start date and time the appointment begins
     * @param end date and time the appointment ends
     * @param createdBy name of user who made this appointment
     * @param customerId id of the attached customer
     * @param userId id of the attached user
     * @param contactId is of the attached contact
     */
    public Appointment(int id, String title, String desc, String location, String type, 
            LocalDateTime start, LocalDateTime end, String createdBy, int customerId, int userId, int contactId) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createdBy = createdBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }
    
    /**
     * Method to get the appointment Id 
     * 
     * @return integer id of this appointment
     */
    public int getId(){
        return this.id;
    }
    
    /**
     * Method to get the title
     * 
     * @return String attached title given to this appointment
     */
    public String getTitle(){
        return this.title;
    }
    
    /**
     * Method to get the description 
     * 
     * @return string description of this appointment
     */
    public String getDesc(){
        return this.desc;
    }
    
    /**
     * Method to get the location assigned
     * 
     * @return string location assigned to this appointment
     */
    public String getLocation(){
        return this.location;
    }
    
    /**
     * Method to get the type assigned
     * 
     * @return string type assigned to this appointment
     */
    public String getType(){
        return this.type;
    }
    
    /**
     * Method to get the starting date/time
     * 
     * @return LocalDateTime start value
     */
    public LocalDateTime getStart(){
        return this.start;
    }
    
    /**
     * Method to get the ending date/time
     * 
     * @return LocalDateTime end value
     */
    public LocalDateTime getEnd(){
        return this.end;
    }
    
    /**
     * Method to get the user who made this appointment. 
     * 
     * @return username 
     */
    public String getCreatedBy(){
        return this.createdBy;
    }
    
    /**
     * Method to get the attached customer
     * 
     * @return id of the attached customer
     */
    public int getCustomerId(){
        return this.customerId;
    }
    
    /**
     * Method to get the attached user
     * 
     * @return id of the attached user
     */
    public int getUserId(){
        return this.userId;
    }
    
    /**
     * Method to get the attached contact
     * 
     * @return id of the attached contact
     */
    public int getContactId(){
        return this.contactId;
    }
    
    /**
     * Method to get the name of this contact
     * 
     * @return string version of this contact based on id
     */
    public String getContactName(){
        for(Contact contact: Main.store.getContacts()){
            if(contact.getId() == this.getContactId()){
                return contact.getName();
            }
        }
        return  "";
    }


    /**
     * Method to set the title.
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method to set the description.
     * 
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Method to set the location.
     * 
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Method to set the type.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method to set the starting localdatetime.
     * 
     * @param start the start to set
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Method to set the ending localdatetime.
     * 
     * @param end the end to set
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }


    /**
     * Method to set the attached customer by id.
     * 
     * @param customerId the customerId to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Method to set the attached user by id.
     * 
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Method to set the attached contact by id.
     * 
     * @param contactId the contactId to set
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
}
