package c195;

/**
 *Class for holding customer information.
 * 
 * @author Michael Holloway
 */
public class Customer {

    private int id;
    private String name;
    private String address;
    private String postal;
    private String phone;

    private int divisionId;
    
    public Customer(int id, String name, String address, String postal, String phone, 
            int divisionId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the postal
     */
    public String getPostal() {
        return postal;
    }

    /**
     * @param postal the postal to set
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * @return the divisionId
     */
    public int getDivisionId() {
        return this.divisionId;
    }

    
    /**
     * @param divisionId the divisionId to set
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
    
    public Country getCountry(int countryId){
        for (Country c : Main.store.getCountries()){
            if(c.getId() == countryId){
                return c;
            }
        }
        return null;
    }
    
    public FirstLevelDivision getFirstLevel(){
        for(FirstLevelDivision fld: Main.store.getFirstLevels()){
            if(fld.getId() == this.divisionId){
                return fld;
            }
        }
        return null;
    }
    
    public String getFirstLevelData(){
        for(FirstLevelDivision fld: Main.store.getFirstLevels()){
            if(fld.getId() == this.divisionId){
                return fld.getName();
            }
        }
        return null;
    }
    
}
