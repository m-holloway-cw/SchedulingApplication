package c195;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold country information.
 * This application does not provide methods to change country data.
 *
 * @author Michael Holloway
 */
public class Country {

    private int id;
    private String name;

    
    private List<FirstLevelDivision> divList= new ArrayList();

    /**
     * Constructor for the country class
     * 
     * @param id identification number assigned 
     * @param name name of this country
     */
    Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Method to add a first-level-division object to this country.
     * Storing FLDs to help with UI elements for customers
     * 
     * @param fld the incoming object
     */
    public void addToDivList(FirstLevelDivision fld){
        this.divList.add(fld);
    }
    
    /**
     * Method to get the list of first-level-divisions attached to this country.
     * 
     * @return list of FLDs
     */
    public List<FirstLevelDivision> getDivList(){
        return this.divList;
    }
    
    /**
     * Method to get the identification number assigned to this country
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }


    /**
     * Method to get the name of this country.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
}
