package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents worker's supervisor in the ClockCard system.
 *
 * @author Marek Osvald
 * @version 2011.0604
 */

public class Supervisor implements IPropertyBased {
    private static final String CLASS_PROPERTY_FILE = "src/Supervisor.properties";

    private static final Logger LOGGER = Logger.getLogger(Supervisor.class.getName());

    private static Supervisor instance = null;

    private final Properties CLASS_PROPERTIES = loadProperties(CLASS_PROPERTY_FILE);

    /**
     * Returns a singleton instance.
     *
     * @return singleton instance
     */
    public static Supervisor getInstance() {
        if (instance == null) {
            instance = new Supervisor();
        }
        
        return instance;
    }

    /**
     * Parameterless constructor. Forces class to be singleton.
     */
    private Supervisor() {
        LOGGER.log(Level.FINEST, CLASS_PROPERTIES.getProperty("log.newInstance"));
    }

    @Override
    public Properties loadProperties(String filename) {
        FileInputStream inputStream = null;
        Properties properties = null;

        try {
            inputStream = new FileInputStream(filename);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load Supervisor properties", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.propertyCloseFail"), ex);
                }
            }
        }

        if (properties != null) {
            return properties;
        }

        return new Properties();
    }

    /**
     * Compares the given parameter with supervisory password.
     *
     * @param password password to authenticate with
     * @return true is the passwords match, false otherwise
     */
    public boolean authenticate(String password) {
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        
        boolean result = CLASS_PROPERTIES.getProperty("password").equals(password);

        if (result) {
            LOGGER.log(Level.FINEST, CLASS_PROPERTIES.getProperty("log.correctAuthentication"));
        } else {
            LOGGER.log(Level.INFO, "{0} {1}.", new Object[]{ CLASS_PROPERTIES.getProperty("log.incorrectPassword"), password });
        }
        
        return result;
    }
    /**
     * Returns all shifts from the database.
     *
     * @return all shifts from the database
     */
    public List<Shift> getAllShifts() {
        return ShiftManager.getInstance().getAll();
    }
    /**
     * Returns all workers from the database.
     * 
     * @return all workers from the database
     */
    public List<Worker> getAllWorkers() {
        return WorkerManager.getInstance().getAll();
    }
    /**
     * Returns all shifts from last month from the database.
     *
     * @return all shifts from last month from the database.
     */
    public List<Shift> getLastMonthShifts() {
        Calendar now = new GregorianCalendar();

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) - 1;
        int daysInMonth = now.getMaximum(Calendar.DAY_OF_MONTH);
        
        return getShiftsByMonth(new GregorianCalendar(year, month, 1), new GregorianCalendar(year, month, daysInMonth));
    }
    /**
     * Returns all shifts from current month from the database
     *
     * @return all shifts from current month from the database
     */
    public List<Shift> getCurrentMonthShifts() {
        Calendar now = new GregorianCalendar();

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int daysInMonth = now.getMaximum(Calendar.DAY_OF_MONTH);

        return getShiftsByMonth(new GregorianCalendar(year, month, 1), new GregorianCalendar(year, month, daysInMonth));
    }

    /**
     * Retuns shifts from specified time interval from the database.
     *
     * @param start start of an interval
     * @param end end of an interval
     * @return list of all shoft from the interval
     */
    private List<Shift> getShiftsByMonth(Calendar start, Calendar end) {
        return ShiftManager.getInstance().findStartBetween(
                new Timestamp(start.getTimeInMillis()),
                new Timestamp(end.getTimeInMillis())
            );
    }
}
