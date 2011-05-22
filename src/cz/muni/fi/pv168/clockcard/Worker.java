package cz.muni.fi.pv168.clockcard;

import java.sql.*;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

/**
 * Class that represents worker who is using the ClockCard system.
 *
 * @author Marek Osvald
 * @version 2011.0522
 */

public class Worker implements IDatabaseStoreable {
    private static final String CLASS_PROPERTY_FILE = "src/Worker.properties";

    private static final Properties classProperties = WorkerManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
    
    private Long id;
    private String name;
    private String surname;
    private String login;
    private String password;
    private Shift currentShift = null;
    private boolean suspended = false;
        
    /**
     * Static constructor used for constructing a Worker that has been previously saved to the database.
     *
     * @param id worker's unique ID
     * @param name worker's name
     * @param surname worker's surname
     * @param login worker's login
     * @param password worker's password
     * @param currentShift date and time of worker's pending shift
     * @param suspended whether worker is suspended or not
     * @return newly created worker
     */
    public static Worker loadWorker(long id, String name, String surname, String login, String password, long currentShift, boolean suspended) {
        if (id < 1) {
            throw new IllegalArgumentException("Worker's ID must be greater than zero.");
        }

        return new Worker(id, name, surname, login, password, currentShift, suspended);
    }
    /**
     * Resets password of the selected worker to the default value.
     *
     * @param worker worker whose password is being reset
     */
    public static void resetForgottenPassword(Worker worker) {
        worker.password = classProperties.getProperty("defaultPassword");
    }

    /**
     * Parametric constructor. Private in order to prevent misuse.
     *
     * @param id id of the worker
     * @param name name of the worker
     * @param surname surname of the worker
     * @param login login of the worker
     * @param password password of the worker
     * @param currentShift id of the current pending shift
     * @param suspended true when worker is suspended, false otherwise
     */
    private Worker(Long id, String name, String surname, String login, String password, Long currentShift, boolean suspended) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        if (surname == null || surname.equals("")) {
            throw new IllegalArgumentException("Surname cannot be null or empty.");
        }

        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.login = login;
        this.currentShift = (currentShift != null) ? ShiftManager.getInstance().find(currentShift) : null;
        this.suspended = suspended;
    }
    /**
     * Parametric constructor. This constructor is used for objects that have
     * not been serialized into the database yet.
     *
     * @param name worker's name
     * @param surname worker's surname
     * @param login worker's login
     */
    public Worker(String name, String surname, String login) {
        this(null, name, surname, login, classProperties.getProperty("defaultPassword"), null, false);
    }

    @Override
    public boolean save() {
        Connection connection = null;
        PreparedStatement preparedStatement;
        boolean result = false;

        try {
            connection = WorkerManager.getInstance().getDataSource().getConnection();

            if (id == null) {
                preparedStatement = connection.prepareStatement(classProperties.getProperty("saveQuery"), Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(classProperties.getProperty("updateQuery"));
                preparedStatement.setLong(7, id);
            }

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            preparedStatement.setString(3, login);
            preparedStatement.setString(4, password);
            if (currentShift != null) {
                preparedStatement.setLong(5, currentShift.getID());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }
            preparedStatement.setBoolean(6, suspended);

            result = (preparedStatement.executeUpdate() == 1);

            if (id == null) {
                boolean generatedKey = false;
                ResultSet keys = preparedStatement.getGeneratedKeys();
                if (keys.next()){
                    id = keys.getLong(1);
                    generatedKey = true;
                }

                result = result && generatedKey;
            }
        } catch (SQLException ex) {
            //TODO: log error
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //Log error
                }
            }
        }
        return result;
    }
    @Override
    public boolean destroy() {
        if (id == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement preparedStatement;
        boolean result = false;

        try {
            connection = WorkerManager.getInstance().getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(classProperties.getProperty("deleteQuery"));
            preparedStatement.setLong(1, id);
            result = (preparedStatement.executeUpdate() == 1);
        } catch (SQLException ex) {
            //TODO: log
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //log exception
                }
            }
        }

        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Worker other = (Worker) obj;

        if (login == null || other.login == null) {
            return false;
        }

        return this.login.equals(other.login);
    }
    @Override
    public int hashCode() {
        return (this.login != null ? this.login.hashCode() : 0);
    }

    /**
     * Returns worker's unique ID.
     *
     * @return worker's unique ID
     */
    public Long getID() {
        return id;
    }
    /**
     * Returns worker's name.
     *
     * @return worker's name
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
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        this.name = name;
    }
    /**
     * Returns worker's surname.
     *
     * @return worker's surname
     */
    public String getSurname() {
        return surname;
    }
    /**
     * Sets worker's surname.
     *
     * @param worker's surname
     */
    public void setSurname(String surname) {
        if (surname == null || surname.equals(name)) {
            throw new IllegalArgumentException("Surname cannot be null or empty.");
        }

        this.surname = surname;
    }
    /**
     * Returns worker's login.
     *
     * @return worker's login
     */
    public String getLogin() {
        return login;
    }
    /**
     * Sets login of the worker.
     *
     * @param login login of the worker.
     */
    public void setLogin(String login) {
        if (login == null || login.equals("")) {
            throw new IllegalArgumentException("Login cannot be null or empty.");
        }

        this.login = login;
    }
    /**
     * Sets password worker's password.
     *
     * @param password worker's password
     */
    public void setPassword(String password) {
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        this.password = password;
    }
    /**
     * Returns worker's current pending shift.
     *
     * @return worker's current pending shift
     */
    public Shift getCurrentShift() {
        return currentShift;
    }
    /**
     * Returns whether the worker is suspended or not.
     *
     * @return true when worker is suspended, false otherwise
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Verifies given password against worker's one.
     *
     * @param password password to authenticate with
     * @return true when password is correct and worker is not suspended, false otherwise
     */
    public boolean authenticate(String password) {
        if (suspended) {
            return false;
        }

        return this.password.equals(password);
    }
    /**
     * Starts a new shift. Provided that worker is suspended or already has
     * a pending shift throws a WorkerException.
     *
     * @throws WorkerException
     */
    public void startShift() throws WorkerException {
        if (id == null) {
            throw new WorkerException("Unsaved workers cannot start a new Shift.");
        }

        if (suspended) {
            throw new WorkerException("Suspended workers cannot start a new shift.");
        }

        if (currentShift != null) {
            throw new WorkerException("Worker already has a pending shift.");
        }

        currentShift = new Shift(id);
    }
    /**
     * Ends pending shift. Provided that worker is suspended or has not
     * a pending shift, throws a WorkerException.
     *
     * @throws WorkerException
     */
    public void endShift() throws WorkerException {
        if (currentShift == null) {
            throw new WorkerException("Worker has not a pending shift.");
        }

        if (id == null) {
            throw new WorkerException("Unsaved workers cannot start a new Shift.");
        }

        if (currentShift.getLastBreakStart() != null) {
            currentShift.addBreakTime(new GregorianCalendar().getTimeInMillis() - currentShift.getLastBreakStart().getTimeInMillis());
            currentShift.setLastBreakStart(null);
        }

        currentShift.setEnd(new GregorianCalendar());
        currentShift = null;
    }
    /**
     * Starts a break at current shift. Provided that Worker is not saved, is
     * suspended or is already on break throws WorkerException.
     * 
     * @throws WorkerException
     */
    public void startBreak() throws WorkerException {
        if (id == null) {
            throw new WorkerException("Unsaved workers cannot start a break.");
        }

        if (suspended) {
           throw new WorkerException("Suspended workers cannot start a break.");
        }

        if (currentShift == null) {
            throw new WorkerException("Worker with no pedning shift cannot start a break.");
        }

        if (currentShift.getLastBreakStart() != null) {
            throw new WorkerException("Worker already is on break.");
        }

        currentShift.setLastBreakStart(new GregorianCalendar());
    }
    /**
     * Ends a break at current shift. Provided that Worker is not saved, is
     * suspended or worker has no break to end, throws WorkerException.
     * 
     * @throws WorkerException
     */
    public void endBreak() throws WorkerException {
        if (id == null) {
            throw new WorkerException("Unsaved workers cannot end a break.");
        }

        if (suspended) {
            throw new WorkerException("Suspended workers cannost end a break");
        }

        if (currentShift.getLastBreakStart() == null) {
            throw new WorkerException("Worker who is not on break cannot end a break");
        }

        currentShift.addBreakTime(new GregorianCalendar().getTimeInMillis() - currentShift.getLastBreakStart().getTimeInMillis());
        currentShift.setLastBreakStart(null);
    }
    /**
     * Suspends worker. Provided that worker is already suspended, throws
     * WorkerException.
     *
     * @throws WorkerException
     */
    public void suspend() throws WorkerException {
        if (isSuspended()) {
            throw new WorkerException("Worker is already suspended");
        }

        suspended = true;
    }
    /**
     * Cancels previous suspension. Provided that worker is not suspended,
     * throws IllegalArgumentException.
     *
     * @throws WorkerException
     */
    public void unsuspend() throws WorkerException {
        if (!isSuspended()) {
            throw new WorkerException("Worker is not suspended.");
        }

        suspended = false;
    }
    /**
     * Returns all shifts worked by worker.
     *
     * @return list of all shifts worked by worker
     */
    public List<Shift> getShifts() {
        if (id == null) {
            return null;
        }
        
        return ShiftManager.getInstance().findByWorkerID(id);
    }   
}
