package c195;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Implementation of the DataStore interface. Used to manipulate and retrieve
 * data during run-time.
 *
 * @author Michael Holloway
 */
public class SQLDataStore implements DataStore {

    private static String url = "jdbc:mysql://wgudb.ucertify.com:3306/WJ06px6";
    private static String user = "U06px6";
    private static String pass = "53688835654";

    private static int nextAppointmentIdNumber;

    private static int nextCustomerIdNumber;

    private final SQL sqlData;

    private User currentUser;

    /**
     * Initial constructor class. Only meant to be used once during run-time.
     *
     * @param sql the SQL object created from data
     */
    public SQLDataStore(SQL sql) {
        this.sqlData = sql;
        //set our index numbers
        nextAppointmentIdNumber = 0;
        for (Appointment a : this.sqlData.appointmentList) {
            if (a.getId() > nextAppointmentIdNumber) {
                nextAppointmentIdNumber = a.getId();
            }
        }

        nextCustomerIdNumber = 0;
        for (Customer c : this.sqlData.customerList) {
            if (c.getId() > nextCustomerIdNumber) {
                nextCustomerIdNumber = c.getId();
            }
        }
    }

    /**
     * Method to get the next available appointment number. Initial setup puts
     * this as the next number after the previous highest.
     *
     * @return integer value to be assigned to the next new appointment
     */
    @Override
    public int getNextAppointmentIdNumber() {
        nextAppointmentIdNumber++;
        return nextAppointmentIdNumber;
    }

    /**
     * Method to get the next available customer number. Initial setup adds 1 to
     * the previous highest id#.
     *
     * @return next integer value available
     */
    @Override
    public int getNextCustomerIdNumber() {
        nextCustomerIdNumber++;
        return nextCustomerIdNumber;
    }

    /**
     * Method to set the logged in user. Used for UI manipulation and run-time
     * data retrieval.
     *
     * @param user currently logged in user
     */
    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Method to get the currently logged in user.
     *
     * @return user object associated to the logged in user.
     */
    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Method to get a list of all available users. Setup from MySQL database.
     *
     * @return list of all known users
     */
    @Override
    public List<User> getUsers() {
        return this.sqlData.userList;
    }

    /**
     * Method to get a list of all known appointments. Setup from MySQL
     * database.
     *
     * @return list of all known appointments
     */
    @Override
    public List<Appointment> getAppointments() {
        return this.sqlData.appointmentList;
    }

    /**
     * Method to get a list of the appointments this week.
     * Uses the next 7 days as a week.
     * 
     * @return ObservableList of the matching appointments
     */
    @Override
    public ObservableList<Appointment> getWeeklyAppointments() {
        ObservableList<Appointment> weekly = FXCollections.observableArrayList();
        for (Appointment appt : this.sqlData.appointmentList) {
            LocalDateTime start = appt.getStart();

            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

            long diffDays = Duration.between(now, start).toDays();

            if (diffDays < 8 && start.isAfter(now)) {
                weekly.add(appt);
            } else {
                //empty

            }
        }
        return weekly;
    }
    
    /**
     * Method to get a list of the appointments this month.
     * Uses this month's numbers(1-12) to search for a match.
     * 
     * @return ObservableList of the matching appointments
     */
    @Override
    public ObservableList<Appointment> getMonthlyAppointments() {
        ObservableList<Appointment> monthly = FXCollections.observableArrayList();
        for (Appointment appt : this.sqlData.appointmentList) {
            LocalDateTime i = appt.getStart();

            LocalDateTime now = LocalDateTime.now();
            if (now.getMonthValue() == i.getMonthValue() && i.isAfter(now)) {
                monthly.add(appt);
            }
        }
        return monthly;
    }
    
    
    /**
     * Method to manipulate the available time slots. Forces user to pick a
     * starting time within business hours. 8am-9:30pm EST - for starting times
     * This does not account for overlapping appointments, time only.
     *
     * @return list of all times between 8am and 10pm est in 30 min increments
     */
    @Override
    public List<LocalTime> getPossibleStartTimes() {
        List<LocalTime> t = new ArrayList();
        //int j = 0;
        for (int i = 8; i < 22; i++) {
            for (int j = 0; j < 31; j += 30) {
                LocalTime timeSlot = LocalTime.of(i, j);
                LocalDateTime ld = LocalDateTime.of(LocalDate.now(ZoneId.of("America/New_York")), timeSlot);
                LocalDateTime newLD = ld.atZone(ZoneId.of("America/New_York"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
                t.add(newLD.toLocalTime());
            }
        }
        return t;
    }

    /**
     * Method to manipulate possible appointment end times. Designed to force
     * between 8:30am-10pm EST This does not account for overlapping
     * appointments, time only.
     *
     * @return list of times possible
     */
    @Override
    public List<LocalTime> getPossibleEndTimes() {
        List<LocalTime> t = new ArrayList();
        //int j = 0;
        for (int i = 8; i < 23; i++) {
            if (i == 22) {
                LocalTime timeSlot = LocalTime.of(i, 0);
                LocalDateTime ld = LocalDateTime.of(LocalDate.now(ZoneId.of("America/New_York")), timeSlot);
                LocalDateTime newLD = ld.atZone(ZoneId.of("America/New_York"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
                t.add(newLD.toLocalTime());
            } else {
                for (int j = 0; j < 31; j += 30) {
                    LocalTime timeSlot = LocalTime.of(i, j);
                    LocalDateTime ld = LocalDateTime.of(LocalDate.now(ZoneId.of("America/New_York")), timeSlot);
                    LocalDateTime newLD = ld.atZone(ZoneId.of("America/New_York"))
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    t.add(newLD.toLocalTime());
                }
            }
        }
        return t;
    }

    /**
     * Method to get a list of all known contacts. Setup from MySQL database
     *
     * @return list of all known contact.
     */
    @Override
    public List<Contact> getContacts() {
        return this.sqlData.contactList;
    }

    /**
     * Method to get a list of all known customers. Setup from MySQL database
     *
     * @return list of all known customers.
     */
    @Override
    public List<Customer> getCustomers() {
        return this.sqlData.customerList;
    }

    /**
     * Method to get a list of all known countries. Setup from MySQL database
     *
     * @return list of all known countries.
     */
    @Override
    public List<Country> getCountries() {
        return this.sqlData.countryList;
    }

    /**
     * Method to get a list of all known first-level-divisions. Setup from MySQL
     * database
     *
     * @return list of all known first-level-divisions.
     */
    @Override
    public List<FirstLevelDivision> getFirstLevels() {
        return this.sqlData.firstLevelList;
    }

    /**
     * Method to add an appointment to our database. Includes the MySQL
     * transaction and run-time UI changes
     *
     * @param appt appointment object to add
     */
    @Override
    public void addAppointment(Appointment appt) {
        //mysql transaction
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "INSERT INTO appointments VALUES(?, " //id
                    + "?, " //title
                    + "?, " //desc
                    + "?, " //location
                    + "?, " //type
                    + "?, " //start 
                    + "?, " //end 
                    + "?, " //created date
                    + "?, " //created by
                    + "?, " //last update
                    + "?, " //last updated by
                    + "?, " //customer id
                    + "?, " //user id
                    + "?) ";//contact id
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setInt(1, appt.getId());
            stmt.setString(2, appt.getTitle());
            stmt.setString(3, appt.getDesc());
            stmt.setString(4, appt.getLocation());
            stmt.setString(5, appt.getType());

            //set to UTC time
            ZonedDateTime zStart = appt.getStart().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedStart = zStart.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(6, Timestamp.valueOf(convertedStart.toLocalDateTime()));

            ZonedDateTime zEnd = appt.getEnd().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedEnd = zEnd.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(7, Timestamp.valueOf(convertedEnd.toLocalDateTime()));

            ZonedDateTime zNow = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedNow = zNow.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(8, Timestamp.valueOf(convertedNow.toLocalDateTime()));

            String username = "";
            for (User u : Main.store.getUsers()) {
                if (u.getId() == appt.getUserId()) {
                    username = u.getUsername();
                }
            }
            stmt.setString(9, username);
            stmt.setTimestamp(10, Timestamp.valueOf(convertedNow.toLocalDateTime()));
            stmt.setString(11, username);
            stmt.setInt(12, appt.getCustomerId());
            stmt.setInt(13, appt.getUserId());
            stmt.setInt(14, appt.getContactId());

            stmt.executeUpdate();

            //add to runtime list
            this.sqlData.appointmentList.add(appt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to remove an appointment from the database. Includes MySQL
     * transaction and run-time changes.
     *
     * @param id identification number assigned to the appointment
     */
    @Override
    public void deleteAppointment(int id) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "DELETE FROM appointments WHERE Appointment_ID=?";
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setInt(1, id);
            stmt.execute();
            Appointment storeRemove = null;

            for (Appointment toRemove : Main.store.getAppointments()) {
                //id is the appointments id
                if (toRemove.getId() == id) {
                    storeRemove = toRemove;
                }
            }

            this.sqlData.appointmentList.remove(storeRemove);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to update an existing appointment. Includes MySQL and run-time
     * updates.
     *
     * @param index integer spot in the appointment list
     * @param appt the updated appointment object
     * @return success of transaction
     */
    @Override
    public boolean replaceAppointment(int index, Appointment appt) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "UPDATE appointments SET "
                    + "Title=?, " //title
                    + "Description=?, " //desc
                    + "Location=?, " //location
                    + "Type=?, " //type
                    + "Start=?, " //start 
                    + "End=?, " //end 
                    + "Last_Update=?, " //last update
                    + "Last_Updated_By=?, " //last updated by
                    + "Customer_ID=?, " //customer id
                    + "User_ID=?, " //user id
                    + "Contact_ID=? " //contact id
                    + "WHERE Appointment_ID=?";
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setString(1, appt.getTitle());
            stmt.setString(2, appt.getDesc());
            stmt.setString(3, appt.getLocation());
            stmt.setString(4, appt.getType());

            //set to UTC time
            ZonedDateTime zStart = appt.getStart().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedStart = zStart.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(5, Timestamp.valueOf(convertedStart.toLocalDateTime()));

            ZonedDateTime zEnd = appt.getEnd().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedEnd = zEnd.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(6, Timestamp.valueOf(convertedEnd.toLocalDateTime()));

            ZonedDateTime zNow = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedNow = zNow.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(7, Timestamp.valueOf(convertedNow.toLocalDateTime()));

            String username = "";
            for (User u : Main.store.getUsers()) {
                if (u.getId() == appt.getUserId()) {
                    username = u.getUsername();
                }
            }
            stmt.setString(8, username);
            stmt.setInt(9, appt.getCustomerId());
            stmt.setInt(10, appt.getUserId());
            stmt.setInt(11, appt.getContactId());
            stmt.setInt(12, appt.getId());

            stmt.execute();

            //var index is within the store's list
            this.sqlData.appointmentList.set(index, appt);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to add a new customer object to the database. Includes MySQL and
     * run-time transactions.
     *
     * @param newCustomer customer object to add
     */
    @Override
    public void addCustomer(Customer newCustomer) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "INSERT INTO customers VALUES(?, " //id
                    + "?, " //name
                    + "?, " //address
                    + "?, " //postal code
                    + "?, " //phone 
                    + "?, " //created date
                    + "?, " //created by
                    + "?, " //last update
                    + "?, " //last updated by
                    + "?) ";//division id
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setInt(1, newCustomer.getId());
            stmt.setString(2, newCustomer.getName());
            stmt.setString(3, newCustomer.getAddress());
            stmt.setString(4, newCustomer.getPostal());
            stmt.setString(5, newCustomer.getPhone());

            ZonedDateTime zNow = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedNow = zNow.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(6, Timestamp.valueOf(convertedNow.toLocalDateTime()));
            String username = Main.store.getCurrentUser().getUsername();
            stmt.setString(7, username);

            stmt.setTimestamp(8, Timestamp.valueOf(convertedNow.toLocalDateTime()));
            stmt.setString(9, username);

            stmt.setInt(10, newCustomer.getDivisionId());
            stmt.execute();

            this.sqlData.customerList.add(newCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to remove an existing customer. Includes MySQL and run-time
     * removal.
     *
     * @param id id assigned to the customer object
     */
    @Override
    public void deleteCustomer(int id) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "DELETE FROM customers WHERE Customer_ID=?";
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setInt(1, id);
            stmt.execute();
            Customer storeRemove = null;

            for (Customer toRemove : Main.store.getCustomers()) {
                //id is the appointments id
                if (toRemove.getId() == id) {
                    storeRemove = toRemove;
                }

            }

            this.sqlData.customerList.remove(storeRemove);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to update and existing customer. Includes MySQL and run-time
     * updates.
     *
     * @param index indexed location of the old object in the list
     * @param updated customer object to use
     */
    @Override
    public void updateCustomer(int index, Customer updated) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String statement = "UPDATE customers SET "
                    + "Customer_Name=?, "
                    + "Address=?, "
                    + "Postal_Code=?, "
                    + "Phone=?, "
                    + "Create_Date=?, "
                    + "Created_By=?, "
                    + "Last_Update=?, "
                    + "Last_Updated_By=?, "
                    + "Division_ID=? "
                    + "WHERE Customer_ID=?";
            PreparedStatement stmt = con.prepareStatement(statement);

            stmt.setString(1, updated.getName());
            stmt.setString(2, updated.getAddress());
            stmt.setString(3, updated.getPostal());
            stmt.setString(4, updated.getPhone());

            ZonedDateTime zNow = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime convertedNow = zNow.withZoneSameInstant(ZoneOffset.UTC);
            stmt.setTimestamp(5, Timestamp.valueOf(convertedNow.toLocalDateTime()));
            String username = Main.store.getCurrentUser().getUsername();
            stmt.setString(6, username);

            stmt.setTimestamp(7, Timestamp.valueOf(convertedNow.toLocalDateTime()));
            stmt.setString(8, username);

            stmt.setInt(9, updated.getDivisionId());
            stmt.setInt(10, updated.getId());

            stmt.execute();

            //replace in store list
            this.sqlData.customerList.set(index, updated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Static class to hold all SQL related information.
     */
    public static class SQL {

        public List<User> userList = new ArrayList();
        public List<Customer> customerList = new ArrayList();
        public List<Contact> contactList = new ArrayList();
        public List<Appointment> appointmentList = new ArrayList();
        public List<Country> countryList = new ArrayList();
        public List<FirstLevelDivision> firstLevelList = new ArrayList();

    }

    /**
     * Method to get the data from the MySQL database.
     *
     * @return the SQL object created
     */
    public static SQL loadSQL() {

        SQL returnSQL = new SQL();

        try {

            //users
            String find = "select * from users";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement stmt = con.prepareStatement(find);

            ResultSet ap = stmt.executeQuery(find);

            //each user has user_id(int), user_name(string), password(String),
            //create_date(datetime), created_by(String), last update(timestamp), last updated by
            while (ap.next()) {
                int id = ap.getInt(1);
                String username = ap.getString(2);
                String password = ap.getString(3);

                //set default values
                //change on login
                String locationInfo = ZoneId.systemDefault().toString();
                Locale l = Locale.getDefault();
                String languageInfo = l.getDisplayLanguage();
                User newUser = new User(id, username, password, locationInfo, languageInfo);
                returnSQL.userList.add(newUser);
            }

            //contacts
            find = "select * from contacts";
            Class.forName("com.mysql.cj.jdbc.Driver");
            ap = stmt.executeQuery(find);

            //each contact has contact id (int) name (string) email (string)
            while (ap.next()) {
                int id = ap.getInt(1);
                String name = ap.getString(2);
                String email = ap.getString(3);
                Contact newContact = new Contact(id, name, email);
                returnSQL.contactList.add(newContact);
            }

            //customers
            find = "select * from customers";
            Class.forName("com.mysql.cj.jdbc.Driver");
            ap = stmt.executeQuery(find);

            //each customer has id, name, address, postal code, phone, create data, created by, last update, last updated by, division id
            while (ap.next()) {
                int id = ap.getInt(1);
                String name = ap.getString(2);
                String address = ap.getString(3);
                String postal = ap.getString(4);
                String phone = ap.getString(5);
                int divisionId = ap.getInt(10);
                Customer newCustomer = new Customer(id, name, address, postal, phone,
                        divisionId);
                returnSQL.customerList.add(newCustomer);
            }

            //appointments
            find = "select * from appointments";
            Class.forName("com.mysql.cj.jdbc.Driver");
            ap = stmt.executeQuery(find);

            //each appt has
            //id, title, desc, location, type, start, end, created date, created by, last update, last updated by, customer id, user id, contact id
            while (ap.next()) {
                int id = ap.getInt(1);
                String title = ap.getString(2);
                String desc = ap.getString(3);
                String location = ap.getString(4);
                String type = ap.getString(5);

                Timestamp startTime = ap.getTimestamp(6);
                Date startDate = new Date(startTime.getTime());

                SimpleDateFormat utc = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                utc.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));

                SimpleDateFormat local = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                local.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

                Date localStartDate = utc.parse(local.format(startDate));
                LocalDateTime start = LocalDateTime.ofInstant(localStartDate.toInstant(), ZoneId.systemDefault());

                Timestamp endTime = ap.getTimestamp(7);
                Date endDate = new Date(endTime.getTime());
                Date localEndDate = utc.parse(local.format(endDate));
                LocalDateTime end = LocalDateTime.ofInstant(localEndDate.toInstant(), ZoneId.systemDefault());

                String createdBy = ap.getString(9);
                
                int customerId = ap.getInt(12);
                int userId = ap.getInt(13);
                int contactId = ap.getInt(14);

                //deal with it in mysql transactions
                Appointment newAppt = new Appointment(
                        id, title, desc, location, type,
                        start, end, createdBy, customerId,
                        userId, contactId);
                returnSQL.appointmentList.add(newAppt);
            }

            //divisions
            find = "select * from first_level_divisions";
            Class.forName("com.mysql.cj.jdbc.Driver");
            ap = stmt.executeQuery(find);

            //each contact has FLD has name, id (int), create date, created by, last update date, last updated by, country id
            while (ap.next()) {
                int id = ap.getInt(1);
                String name = ap.getString(2);

                int countryId = ap.getInt(7);
                FirstLevelDivision newFLD = new FirstLevelDivision(id, name, countryId);
                returnSQL.firstLevelList.add(newFLD);
            }

            //countries
            find = "select * from countries";
            Class.forName("com.mysql.cj.jdbc.Driver");
            ap = stmt.executeQuery(find);

            //each contact has country has id (int) name, create date, created by, last update date, last updated by
            while (ap.next()) {
                int id = ap.getInt(1);
                String name = ap.getString(2);
                Country newCountry = new Country(id, name);
                //add associated first level divisions to the country
                for (FirstLevelDivision f : returnSQL.firstLevelList) {
                    if (f.getCountryId() == id) {
                        newCountry.addToDivList(f);
                    }
                }

                returnSQL.countryList.add(newCountry);
            }

            //close connect to db
            con.close();
        } catch (IllegalArgumentException il) {
            il.printStackTrace();
        } catch (SQLException sql) {
            sql.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSQL;
    }

    /**
     * Method for writing a log-in attempt to a log file
     *
     * @param username username that was attempting to log-in
     * @param success whether or not the attempt succeeded
     */
    @Override
    public void logAttempt(String username, boolean success) {
        try {
            FileWriter fW = new FileWriter("login_activity.txt", true);
            fW.write("Attempted Login");
            fW.write("\nUsername: " + username);
            if (success) {
                fW.write("\nAttempt: success");
            } else {
                fW.write("\nAttempt: failed");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
            String time = LocalDateTime.now().format(formatter);
            fW.write("\nTimestamp: " + time);

            fW.write("\n-------------------------------");
            fW.write("\n\n");
            fW.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
