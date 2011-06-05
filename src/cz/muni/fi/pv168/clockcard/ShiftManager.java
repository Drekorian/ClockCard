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

    private static ShiftManager instance;

    private final Properties classProperties = loadProperties(CLASS_PROPERTY_FILE);

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
     * of ShiftManager solely via getInstance().
     * method.
     */
    private ShiftManager() {
        testingMode = false;
        dataSource = getProductionDataSource();
    }

    @Override
    public Shift find(long id) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Shift result = null;

        try {
            connection = getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(classProperties.getProperty("findQuery"));
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.getFetchSize() == 1 && resultSet.next()) {
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
            Logger.getLogger(ShiftManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ShiftManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }
    @Override
    public List<Shift> getAll() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet;
        ArrayList<Shift> result = null;
        
        try {
            connection = getDataSource().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(classProperties.getProperty("selectAllQuery"));
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
        } catch (SQLException ex) {
            System.out.println(ex);
            //TODO: log an exception
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                //log an exception
            }
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return null;
    }
  
    public long count() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet;
        long result = 0;

        try {
            connection = getDataSource().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(classProperties.getProperty("countQuery"));
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            //TODO: Log error
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    result = -1;
                    //TODO: Log error
                }
            }
        }
        
        return result;
    }

    /**
     * Retrieves all shifts assgined to the worker with given unique ID. Provided
     * that worker's ID is less or equal to zero, throw IllegalArgumentException.
     *
     * @param workerid unique ID of the worker
     *
     * @return shifts assigned to worker with given unique ID
     */
    public List<Shift> findByWorkerID(long workerid) {
        if (workerid < 1) {
            throw new IllegalArgumentException("Worker's ID must be greater than zero.");
        }

        Connection connection = null;
        PreparedStatement statement;
        ResultSet resultSet;
        ArrayList<Shift> result = null;
        try {
            connection = getDataSource().getConnection();
            result = new ArrayList<Shift>();
            statement = connection.prepareStatement(classProperties.getProperty("findByWorkerIDQuery"));
            statement.setLong(1, workerid);
            resultSet = statement.executeQuery();

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
        } catch (SQLException ex) {
            Logger.getLogger(ShiftManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(ShiftManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (result != null) {
            return Collections.unmodifiableList(result);
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
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<Shift> result = null;

        try {
            connection = getDataSource().getConnection();
            if (workerID == null) {
                preparedStatement = connection.prepareStatement(classProperties.getProperty("findStartBetweenQuery"));
            } else {
                preparedStatement = connection.prepareStatement(classProperties.getProperty("findStartBetweenQueryID"));
            }

            preparedStatement.setTimestamp(1, begin);
            preparedStatement.setTimestamp(2, end);

            if (workerID != null) {
                preparedStatement.setLong(3, workerID);
            }

            resultSet = preparedStatement.executeQuery();
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
        } catch (SQLException ex) {
            System.out.println(ex);
            //TODO: log an exception
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                //log an exception
            }
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return new ArrayList<Shift>();
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
