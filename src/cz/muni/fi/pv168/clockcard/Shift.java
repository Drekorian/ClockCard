package cz.muni.fi.pv168.clockcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a shift worked by a worker.
 *
 * @author David Stein
 * @author Marek Osvald
 * @version 2011.0604
 */

public class Shift implements IDatabaseStoreable {
    private static final String CLASS_PROPERTY_FILE = "src/Shift.properties";
    private static final Properties CLASS_PROPERTIES = ShiftManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
    private static final Logger LOGGER = Logger.getLogger(Shift.class.getName());

    private Long id;
    private long workerID;
    private Calendar start;
    private Calendar end;
    private Calendar lastBreakStart;
    private long totalBreakTime = 0;

    /**
     * Static constructor used for constructing a Shift that has been previously saved to the database.
     * 
     * @param id shift's unique ID
     * @param workerID of the shift's worker
     * @param start date and time shift's start
     * @param end date and time shift's end
     * @param lastBreakStart date and time of the last break's start
     * @param totalBreakTime total time spent on break in milliseconds
     * @return newly created shift.
     */
    public static Shift loadShift(long id, long workerID, Calendar start, Calendar end, Calendar lastBreakStart, long totalBreakTime) {
        if (id < 1) {
            throw new IllegalArgumentException("ID must not be greater or equal to 1");
        }
        
        if (workerID < 1) {
            throw new IllegalArgumentException("WorkerID must not be greater or equal to 1");
        }

        if (start == null) {
            throw new IllegalArgumentException("Start cannot be null");
        }

        if (totalBreakTime < 0) {
            throw new IllegalArgumentException("TotalBreakTime must not be greater or equal to 1");
        }
        
        LOGGER.log(Level.FINEST, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.loadShift"), workerID, id, start });
        return new Shift(id, workerID, start, end, lastBreakStart, totalBreakTime);
    }
    /**
     * Parametric constructor. Private in order to prevent misuse.
     *
     * @param id unique ID of the shift
     * @param workerId unique ID of the shift's worker
     * @param start date and time of the shift's start
     * @param end date and time of the shift's start
     * @param lastBreakStart date and time of the last break's start
     * @param totalBreakTime total break time on break in milliseconds
     */
    private Shift(Long id, long workerID, Calendar start, Calendar end, Calendar lastBreakStart, long totalBreakTime) {
        if (workerID < 1) {
            throw new IllegalArgumentException("workerID must be greater or equal to 1");
        }

        if (start == null) {
            throw new IllegalArgumentException("Shift start cannot be null.");
        }

        if (totalBreakTime < 0) {
            throw new IllegalArgumentException("Total break time must be greater or equal to zero.");
        }

        this.id = id;
        this.workerID = workerID;
        this.start = start;
        this.end = end;
        this.lastBreakStart = lastBreakStart;
        this.totalBreakTime = totalBreakTime;
    }
    /**
     * Parametric constructor. Creates a shift based on worker's ID and current system date and time.
     * @param workerID unique ID of the worker
     */
    public Shift(long workerID) {
        this(null, workerID, new GregorianCalendar(), null, null, 0);
        LOGGER.log(Level.FINEST, "{0}: (none|{1}) none", new Object[]{ CLASS_PROPERTIES.getProperty("log.newShift"), workerID });
    }

    @Override
    public boolean save() {
        Connection connection = null;
        List<QueryParameter> params = null;
        Long key = null;
        int updatedRows = 0;
        boolean result = false;

        if ((connection = ShiftManager.getInstance().openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return false;
        }

        params = new ArrayList<QueryParameter>();
        params.add(new QueryParameter(QueryParameter.LONG, workerID));
        params.add(new QueryParameter(QueryParameter.TIMESTAMP, new Timestamp(start.getTimeInMillis())));
        if (end != null) {
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, new Timestamp(end.getTimeInMillis())));
        } else {
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, null));
        }

        if (lastBreakStart != null) {
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, new Timestamp(lastBreakStart.getTimeInMillis())));
        } else {
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, null));
        }

        params.add(new QueryParameter(QueryParameter.LONG, totalBreakTime));

        try {
            if (id == null) {
                key = new Long(0);
                updatedRows = ShiftManager.getInstance().executeUpdate(connection, CLASS_PROPERTIES.getProperty("saveQuery"), params, key);
            } else {
                params.add(new QueryParameter(QueryParameter.LONG, id));
                updatedRows = ShiftManager.getInstance().executeUpdate(connection, CLASS_PROPERTIES.getProperty("updateQuery"), params);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.saveFailed"), ex);
        } finally {
            ShiftManager.getInstance().terminateConnection(connection);
        }

        result = (updatedRows == 1);

        if (id == null) {
            if (ShiftManager.getInstance().getLastGeneratedKey() != null &&
                ShiftManager.getInstance().getLastGeneratedKey() > 0) {
                id = ShiftManager.getInstance().getLastGeneratedKey();
                ShiftManager.getInstance().deleteLastGeneratedKey();
            } else {
                result = false;
                LOGGER.log(Level.WARNING, CLASS_PROPERTIES.getProperty("log.keyGenerationFailed"));
            }
        }

        if (result) {
            LOGGER.log(Level.FINEST, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.saveSuccess"), workerID, id, start });
        } else {
            LOGGER.log(Level.WARNING, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.saveFailed"), workerID, id, start });
        }

        return result;
    }
    @Override
    public boolean destroy() {
        if (id == null) {
            return false;
        }

        Connection connection = null;
        List<QueryParameter> params;
        boolean result = false;

        if ((connection = ShiftManager.getInstance().openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return false;
        }

        params = new ArrayList<QueryParameter>();
        params.add(new QueryParameter(QueryParameter.LONG, id));
        try {
            result = (ShiftManager.getInstance().executeUpdate(connection, CLASS_PROPERTIES.getProperty("deleteQuery"), params) == 1);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.destroyFailed"), ex);
        }
        
        ShiftManager.getInstance().terminateConnection(connection);

        if (result) {
            LOGGER.log(Level.FINEST, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.destroySuccess"), workerID, id, start });
        } else {
            LOGGER.log(Level.WARNING, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.destroyFailed"), workerID, id, start });
        }

        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Shift other = (Shift) obj;

        boolean result = true;

        result = result && (workerID == other.workerID);

        if (start != null && other.start != null) {
            result = result && start.equals(other.start);
        }

        if (end != null && other.end != null) {
            result = result && end.equals(other.end);
        }

        if (lastBreakStart != null && other.lastBreakStart != null) {
            result = result && lastBreakStart.equals(other.lastBreakStart);
        }

        result = result && (totalBreakTime == other.totalBreakTime);

        return result;
    }
    @Override
    public int hashCode() {
        return (37 * Long.valueOf(workerID).hashCode() +
                41 * (start != null ? start.hashCode() : 0) +
                61 * (end != null ? end.hashCode() : 0) +
                71 * (lastBreakStart != null ? lastBreakStart.hashCode() : 0) +
                89 * Long.valueOf(totalBreakTime).hashCode());
    }

    /**
     * Returns unique ID of the shift.
     *
     * @return unique ID of the shift
     */
    public Long getID() {
        return id;
    }
    /**
     * Returns unique ID of the shift's worker
     *
     * @return unique ID of the shift's worker
     */
    public long getWorkerID() {
        return workerID;
    }
    /**
     * Returns date and time of shift's start
     * 
     * @return data and time of shift's start
     */
    public Calendar getStart() {
            return start;
        }
    /**
     * Sets date and time of shift's start. Provided that given parameter is null or
     * sets date after the date and time of shift's end, IllegalArgumentException
     * is thrown.
     *
     * @param start date and time of shift's start
     *
     * @throws IllegalArgumentException
     */
    public void setStart(Calendar start) {
        if (start == null) {
            throw new IllegalArgumentException("Start cannot be null");
        }

        if (end != null && end.getTimeInMillis() - start.getTimeInMillis() <= 0) {
            throw new IllegalArgumentException("Start time must take place before the end time.");
        }

        this.start = start;
    }
    /**
     * Returns date and time of the end of the shift.
     *
     * @return date and time of the end of the shift
     */
    public Calendar getEnd() {
        return end;
    }
    /**
     * Sets date and time of shift's end. Provided that given parameter is null or
     * sets date before the date and time of shift's start, IllegalArgumentException
     * is thrown.
     *
     * @param end date and time of shift's end
     *
     * @throws IllegalArgumentException
     */
    public void setEnd(Calendar end) {
        if (end == null) {
            throw new IllegalArgumentException("End cannot be null");
        }

        if (end.getTimeInMillis() - start.getTimeInMillis() <= 0) {
            throw new IllegalArgumentException("End time must take place after the start time.");
        }

        this.end = end;
    }
    /**
     * Returns date and time of lasts shift's break start
     *
     * @return date and time of lasts shift's break start
     */
    public Calendar getLastBreakStart() {
        return lastBreakStart;
    }
    /**
     * Sets start date and time of the last break during shift. Provided that
     * given parameter is null and current shift has no pending shift,
     * IllegalArgumentException is thrown.
     *
     * @param lastBreakStart start date and time of the last break during shift
     * @throws IllegalArgumentException
     */
    public void setLastBreakStart(Calendar lastBreakStart) {
        if (this.lastBreakStart == null && lastBreakStart == null) {
            throw new IllegalArgumentException("There is no pending break to nullify.");
        }
        
        this.lastBreakStart = lastBreakStart;
    }
    /**
     * Returns total break time in milliseconds.
     * 
     * @return total break time in milliseconds
     */
    public long getTotalBreakTime() {
        return totalBreakTime;
    }
    /**
     * Sets total break time in milliseconds. Provided that given attribute is
     * less than zero, IllegalArgumentException is thrown.
     * 
     * @param totalBreakTime total break time in milliseconds
     */
    public void setTotalBreakTime(long totalBreakTime) {
        if (totalBreakTime < 0) {
            throw new IllegalArgumentException("totalBreakTime must be greater or equal to zero.");
        }

        this.totalBreakTime = totalBreakTime;
    }
    /**
     * Adds given parameter to shift's total break time. Provided that given
     * parameter is less or equal to zero IllegalArgumentException is thrown.
     * 
     * @param time time in milliseconds to add to shift's total break time
     */
    public void addBreakTime(long time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Break time in milliseconds mus be greater than zero");
        }

        totalBreakTime += time;
    }
}
