package cz.muni.fi.pv168.clockcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents worker using the ClockCard system.
 *
 * @author Marek Osvald
 */

public class Worker {
    //TODO: Load default from the property file.
    private static final String DEFAULT_PASSWORD = ""; // _DO NOT_ use this value.

    private Long id;
    private String name;
    private String surname;
    private String login;
    private String password;
    private Shift currentShift = null;
    private boolean suspended = false;

    /**
     * Resets the password of the selected worker to the default value.
     *
     * @param worker worker whose password is being reset
     */
    public static void resetForgottenPassword(Worker worker) {
        //worker.password = defaultPassword;
    }
    /**
     * Returns lists of all workers in the database.
     *
     * @return list of all workers in the database.
     */
    public static List<Worker> all() throws SQLException {
        /*Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/clockcard", "root", "root");
        Statement statement = connection.createStatement();
        boolean execute = statement.execute("SELECT * FROM workers");

        //TODO: get rid of the throws
        */
        return new ArrayList<Worker>();
    }
    /**
     * Returns worker with the selected ID from the database or null if the
     * worker is not found.
     * 
     * @param id ID of the worker to be found
     * @return worker with the selected ID from the database or null if the
     * worker is not found
     */
    public static Worker find(long id) {
        //TODO implement
        return new Worker("", "", ""); //_DO NOT_ USE THIS -- JUST A DUMMY OBJECT
    }
    /**
     * Returns total number of workers in the database.
     *
     * @return total number of workers in the database
     */
    public static int count() {
        //TODO implement
        return 0; //_DO NOT_ USE THIS -- JUST A DUMMY VALUE
    }
    /**
     * Deletes matching record from the database.
     */
    public void destroy() {
        //
    }
    /**
     * Parametric constructor.
     * 
     * @param name name of the worker
     * @param surname surname of the worker
     * @param login login of the worker
     */
    Worker(String name, String surname, String login) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = DEFAULT_PASSWORD;
    }
    /**
     * Returns ID of the worker.
     *
     * @return id of the worker, null provided that the worker was not saved to the database yet.
     */
    public Long getID() {
        return id;
    }
    /**
     * Returns name of the worker.
     *
     * @return name of the worker
     */
    public String getName() {
        return name;
    }
    /**
     * Sets name of the worker.
     *
     * @param name name of the worker to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Returns current pending shift.
     *
     * @return current pending shift
     */
    public Shift getCurrentShift() {
        return currentShift;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public void startShift() {
        //TODO implement
    }

    public void endShift() {
        //TODO implement
    }

    public void startBreak() {
        //TODO implement
    }

    public void endBreak() {
        //TODO implement
    }
    /**
     * Returns true when worker is suspended, false otherwise.
     *
     * @return true when worker is suspended, false otherwise
     */
    public boolean isSuspended() {
        return suspended;
    }
    /**
     * Suspends worker.
     *
     * @throws WorkerException
     */
    public void suspend() throws WorkerException {
        /*if (suspended) {
            throw new WorkerException("Worker already suspended.");
        } else {
            suspended = true;
        }*/
    }
    /**
     * Cancels previous suspension.
     *
     * @throws WorkerException
     */
    public void unsuspend() throws WorkerException {
        /*if (!suspended) {
            throw new WorkerException("Worker is not suspended.");
        } else {
            suspended = false;
        }*/
    }
    /**
     * Returns all shifts worked by worker.
     *
     * @return list of all shifts worked by worker
     */
    public List<Shift> getShifts() {
        //TODO implement;
        return new ArrayList<Shift>();
    }
}