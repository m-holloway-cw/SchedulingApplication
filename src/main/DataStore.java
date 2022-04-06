package c195;

import java.time.LocalTime;
import java.util.List;
import javafx.collections.ObservableList;

/**
 * Interface class for the run-time database.
 * The database stores all methods for data manipulation and retrieval.
 * 
 * @author Michael Holloway
 */
public interface DataStore {
    
    int getNextAppointmentIdNumber();
    
    int getNextCustomerIdNumber();
    
    void setCurrentUser(User user);
    
    User getCurrentUser();
    
    List<User> getUsers();
    
    List<Appointment> getAppointments();
    
    ObservableList<Appointment> getWeeklyAppointments();
    
    ObservableList<Appointment> getMonthlyAppointments();
    
    List<LocalTime> getPossibleStartTimes();
    
    List<LocalTime> getPossibleEndTimes();
    
    List<Contact> getContacts();
    
    List<Customer> getCustomers();
    
    List<Country> getCountries();
    
    List<FirstLevelDivision> getFirstLevels();
    
    void addAppointment(Appointment appt);
    
    void deleteAppointment(int id);
    
    boolean replaceAppointment(int index, Appointment replacement);
    
    void addCustomer(Customer newCustomer);
    
    void deleteCustomer(int id);
    
    void updateCustomer(int index, Customer updated);
    
    void logAttempt(String username, boolean success);
    
}
