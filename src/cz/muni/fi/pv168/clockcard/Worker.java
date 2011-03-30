package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents worker using the ClockCard system.
 *
 * @author Marek Osvald
 */

public class Worker extends APerson {
    private Shift currentShift = null;
    private boolean suspended = false;

    /**
     * Returns lists of all workers in the database.
     *
     * @return list of all workers in the database.
     */
    public static List<Worker> all() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/clockcard", "root", "root");
        Statement statement = connection.createStatement();
        boolean execute = statement.execute("SELECT * FROM workers");

        //TODO: get rid of the throws

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
     * Parametric constructor.
     * 
     * @param name name of the worker
     * @param surname surname of the worker
     * @param login login of the worker
     */
    Worker(String name, String surname, String login) {
       super(name, surname, login);
    }
    /**
     * Returns current pending shift.
     *
     * @return current pending shift
     */
    public Shift getCurrentShift() {
        return currentShift;
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
        return new ArrayList<Shift>() {};
    }

/*    private Statement getStatement() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/clockcard", "root", "root");
        return connection.createStatement();
    }*/
}
