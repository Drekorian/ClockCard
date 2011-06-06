package cz.muni.fi.pv168.clockcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database backend manager for handling Shift class.
 *
 * @author David Stein
 * @author Marek Osvald
 * @version 2011.0604
 */

public class ShiftManager extends ADatabaseManager {
    private static final String CLASS_PROPERTY_FILE = "src/Shift.properties";
    private static final Logger LOGGER = Logger.getLogger(ShiftManager.class.getName());

    private static ShiftManager instance;

    private final Properties CLASS_PROPERTIES = loadProperties(CLASS_PROPERTY_FILE);

    /**
     * Returns the sole instance of ShiftManager in the program. Provided that
     * instance has not been created yet, creates one.
     *
     * @return sole ShiftManager instance in the program
     */
    public static ShiftManager getInstance() {
        if (instance == null) {
            instance = new ShiftManager();
        }

        return instance;
    }

    /**
     * Parameterless constructor. Private in order to force creating
     * of ShiftManager solely via getInstance() method.
     */
    private ShiftManager() {
        LOGGER.finest(CLASS_PROPERTIES.getProperty("log.newInstance"));
    }

    @Override
    public Shift find(long id) {
        Connection connection = null;
        List<QueryParameter> params;
        ResultSet resultSet;
        Shift result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        try {
            params = new ArrayList<QueryParameter>();
            params.add(new QueryParameter(QueryParameter.LONG, id));
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findQuery"), params);

            if (resultSet != null && resultSet.getFetchSize() == 1 && resultSet.next()) {
                Calendar shiftStart = null, shiftEnd = null, lastBreak = null;

                shiftStart = new GregorianCalendar();
                shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());

                if (resultSet.getTimestamp("SHIFT_END") != null) {
                    shiftEnd   = new GregorianCalendar();
                    shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                }
                
                if (resultSet.getTimestamp("LAST_BREAK") != null) {
                    lastBreak  = new GregorianCalendar();
                    shiftEnd.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());
                }

                result = Shift.loadShift(resultSet.getLong("ID"),
                                         resultSet.getLong("WORKER_ID"),
                                         shiftStart,
                                         shiftEnd,
                                         lastBreak,
                                         resultSet.getLong("TOTAL_BREAK_TIME"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.findFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0}: ({1}|{2}) {3}", new Object[]{ CLASS_PROPERTIES.getProperty("log.findSuccess"), result.getWorkerID(), result.getID(), result.getStart() });
        }

        return result;
    }
    @Override
    public List<Shift> getAll() {
        Connection connection = null;
        ResultSet resultSet;
        ArrayList<Shift> result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        try {
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("selectAllQuery"));
            result = new ArrayList<Shift>();

            if (resultSet != null) {
                while (resultSet.next()) {
                    Calendar shiftStart = null, shiftEnd = null, lastBreak = null;

                    shiftStart = new GregorianCalendar();
                    shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());

                    if (resultSet.getTimestamp("SHIFT_END") != null) {
                        shiftEnd   = new GregorianCalendar();
                        shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                    }

                    if (resultSet.getTimestamp("LAST_BREAK") != null) {
                        lastBreak  = new GregorianCalendar();
                        lastBreak.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());
                    }

                    Shift shift = Shift.loadShift(resultSet.getLong("ID"),
                                                  resultSet.getLong("WORKER_ID"),
                                                  shiftStart,
                                                  shiftEnd,
                                                  lastBreak,
                                                  resultSet.getLong("TOTAL_BREAK_TIME"));
                    result.add(shift);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.getAllFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0} [{1}]", new Object[]{ CLASS_PROPERTIES.getProperty("log.getAllSuccess"), result.size() });
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return null;
    }
    @Override
    public long count() {
        Connection connection = null;
        ResultSet resultSet;
        long result = -1;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return -1;
        }

        try {
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("countQuery"));
            if (resultSet != null && resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.countFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result > 0) {
            LOGGER.log(Level.FINEST, "{0} [{1}]", new Object[]{ CLASS_PROPERTIES.getProperty("log.countSuccess"), result });
        }
        
        return result;
    }

    /**
     * Retrieves all shifts assgined to the worker with given unique ID. Provided
     * that worker's ID is less or equal to zero, throw IllegalArgumentException.
     *
     * @param workerID unique ID of the worker
     *
     * @return shifts assigned to worker with given unique ID
     */
    public List<Shift> findByWorkerID(long workerID) {
        if (workerID < 1) {
            throw new IllegalArgumentException("Worker's ID must be greater than zero.");
        }

        Connection connection = null;
        List<QueryParameter> params;
        ResultSet resultSet;
        ArrayList<Shift> result = null;
        
        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        try {
            params = new ArrayList<QueryParameter>();
            params.add(new QueryParameter(QueryParameter.LONG, workerID));
            result = new ArrayList<Shift>();
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findByWorkerIDQuery"), params);

            if (resultSet != null) {
                while (resultSet.next()) {
                    Calendar shiftStart = null, shiftEnd = null, lastBreak = null;

                    shiftStart = new GregorianCalendar();
                    shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());

                    if (resultSet.getTimestamp("SHIFT_END") != null) {
                        shiftEnd = new GregorianCalendar();
                        shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                    }

                    if (resultSet.getTimestamp("LAST_BREAK") != null) {
                        lastBreak = new GregorianCalendar();
                        lastBreak.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());
                    }

                    Shift shift = Shift.loadShift(resultSet.getLong("ID"),
                                                  resultSet.getLong("WORKER_ID"),
                                                  shiftStart,
                                                  shiftEnd,
                                                  lastBreak,
                                                  resultSet.getLong("TOTAL_BREAK_TIME"));
                    result.add(shift);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.findByWorkerIDFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }
        
        if (result != null) {
            LOGGER.log(Level.FINEST, "{0}: [{1}]", new Object[]{ CLASS_PROPERTIES.getProperty("log.findSuccess"), result.size() });
        }

        return null;
    }
    /**
     * TOOD: JAVADOC
     *
     * @param begin
     * @param end
     * @param workerID
     * @return
     */
    public List<Shift> findStartBetween(Timestamp begin, Timestamp end, Long workerID) {
        Connection connection = null;
        List<QueryParameter> params;
        ResultSet resultSet;
        ArrayList<Shift> result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        try {
            params = new ArrayList<QueryParameter>();
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, begin));
            params.add(new QueryParameter(QueryParameter.TIMESTAMP, end));

            if (workerID == null) {
                resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findStartBetweenQuery"), params);
            } else {
                params.add(new QueryParameter(QueryParameter.LONG, workerID));
                resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findStartBetweenQueryID"), params);
            }

            if (resultSet != null) {
                result = new ArrayList<Shift>();
                while (resultSet.next()) {
                    Calendar shiftStart = null, shiftEnd = null, lastBreak = null;

                    shiftStart = new GregorianCalendar();
                    shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());

                    if (resultSet.getTimestamp("SHIFT_END") != null) {
                        shiftEnd   = new GregorianCalendar();
                        shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                    }

                    if (resultSet.getTimestamp("LAST_BREAK") != null) {
                        lastBreak  = new GregorianCalendar();
                        lastBreak.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());
                    }

                    Shift shift = Shift.loadShift(resultSet.getLong("ID"),
                                                  resultSet.getLong("WORKER_ID"),
                                                  shiftStart,
                                                  shiftEnd,
                                                  lastBreak,
                                                  resultSet.getLong("TOTAL_BREAK_TIME"));
                    result.add(shift);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.findStartBetweenFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0}: [{1}]", new Object[]{ CLASS_PROPERTIES.getProperty("log.findStartBetweenSuccess"), result.size() });
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return null;
    }
    /**
     * Returns a list of shifts that start between given parameters.
     *
     * @param begin begin of the interval
     * @param end end of the interval
     * @return list of shifts that start between given parameters
     */
    public List<Shift> findStartBetween(Timestamp begin, Timestamp end) {
        return findStartBetween(begin, end, null);
    }
}
