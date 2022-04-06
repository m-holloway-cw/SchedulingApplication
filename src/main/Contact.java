package c195;

/**
 * Class to hold all relevant information on a contact.
 * This application does not provide the ability to change contact information.
 * 
 * @author Michael Holloway
 */
public class Contact {
    private int id;
    private String name;
    private String email;
    
    /**
     * Constructor for the contact class.
     * 
     * @param id identification number given to this contact
     * @param name name of the contact
     * @param email email for the contact
     */
    public Contact(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    /**
     * Method to get the id number of this contact.
     * 
     * @return integer id number
     */
    public int getId(){
        return this.id;
    }
    
    /**
     * Method to get the name of this contact.
     * 
     * @return string name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Method to get the email for this contact.
     * 
     * @return string email
     */
    public String getEmail(){
        return this.email;
    }
    
}
