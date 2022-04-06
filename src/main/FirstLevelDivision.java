package c195;

/**
 * Class to hold first-level-division data.
 * This application does not alter this or related country data.
 * 
 * @author Michael Holloway
 */
public class FirstLevelDivision {

    private String name;
    private int id;
    private int countryId;
    
    /**
     * Constructor for the FirstLevelDivision class
     * 
     * @param id id assigned to this division
     * @param name name given to this division
     * @param countryId associated country id used for indexing
     */
    FirstLevelDivision(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    /**
     * Method to get the name of this FLD.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get the identification number assigned to this FLD.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Method to get the associated country identification number.
     * 
     * @return the countryId
     */
    public int getCountryId() {
        return countryId;
    }
    
}
