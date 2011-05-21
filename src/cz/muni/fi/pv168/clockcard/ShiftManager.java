package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Database backend manager for handling Shift class.
 *
 * @author David Stein
 * @version 2011.0518
 */

public class ShiftManager implements IDatabaseManager {
    private static final String PROPERTY_FILE = "src/Shift.properties";

    private static ShiftManager instance;

    private final Properties properties = loadProperties();
    private boolean testingMode;
    private DataSource dataSource;

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
     * Parameterless constructor.
     * Private in order to force creation of ShiftManager solely via getInstance()
     * method.
     */
    private ShiftManager() {
        testingMode = false;
        dataSource = getProductionDataSource();
    }

    public long count() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet;
        long result = 0;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(properties.getProperty("countQuery"));
            resultSet.next();
            result = resultSet.getInt(1);
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

    public ADatabaseStoreable find(long id) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Shift result = null;

        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement(properties.getProperty("findQuery"));
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.getFetchSize() == 1 && resultSet.next()) {
                Calendar shiftStart, shiftEnd, lastBreak;

                shiftStart = new GregorianCalendar();
                shiftEnd   = new GregorianCalendar();
                lastBreak  = new GregorianCalendar();

                shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());
                shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                lastBreak.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());

                result = Shift.loadShift(resultSet.getLong("ID"),
                                         resultSet.getLong("WORKER_ID"),
                                         shiftStart,
                                         shiftEnd,
                                         lastBreak,
                                         resultSet.getLong("TOTAL_BREAK_TIME"));
            }
        } catch (SQLException ex) {
            //TODO Log exception
        } finally {
            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException ex) {
//                    //Log an exception
//                }
            }
        }
        
        return result;
    }

    public List<Shift> getAll() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet;
        ArrayList<ADatabaseStoreable> result = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(properties.getProperty("getAllQuery"));
            result = new ArrayList<ADatabaseStoreable>();

            while (resultSet.next()) {
                Calendar shiftStart, shiftEnd, lastBreak;

                shiftStart = new GregorianCalendar();
                shiftEnd   = new GregorianCalendar();
                lastBreak  = new GregorianCalendar();

                shiftStart.setTimeInMillis(resultSet.getTimestamp("SHIFT_START").getTime());
                shiftEnd.setTimeInMillis(resultSet.getTimestamp("SHIFT_END").getTime());
                lastBreak.setTimeInMillis(resultSet.getTimestamp("LAST_BREAK").getTime());
                
                Shift shift = Shift.loadShift(resultSet.getLong("ID"),
                                              resultSet.getLong("WORKER_ID"),
                                              shiftStart,
                                              shiftEnd,
                                              lastBreak,
                                              resultSet.getLong("TOTAL_BREAK_TIME"));
                result.add(shift);
            }
        } catch (SQLException ex) {
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

    public Properties loadProperties() {
        FileInputStream inputStream = null;
        Properties _properties = null;

        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            _properties = new Properties();
            _properties.load(inputStream);
        } catch (IOException e) {
            //TODO: LOG fatal error, Property file not found.
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    //TODO: Log error
                }
            }
        }

        if (_properties != null) {
            return _properties;
        }

        return new Properties();
    }   

    public void testingOff() {
        if (testingMode) {
            dataSource = getProductionDataSource();
            testingMode = false;
        }

    }

    public void testingOn() {
        if (!testingMode) {
            dataSource = getTestingDataSource();
            testingMode = true;
        }
    }

    /**
     * Method to get all shift what are assigned to worker.
     * @param workerid
     * @return List<Shift> list of shifts
     * @throws SQLException
     */
    public List<Shift> findByWorkerID(long workerid) {
        Connection connection = null;
        PreparedStatement statement;
        ResultSet resultSet;
        ArrayList<Shift> result = null;

        try {
            connection = ConnectionManager.getConnection();
            result = new ArrayList<Shift>();
            statement = connection.prepareStatement(properties.getProperty("findByWorkerIDQuery"));
            statement.setLong(1, workerid);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Calendar shiftStart, shiftEnd, lastBreak;
                
                shiftStart = new GregorianCalendar();
                shiftEnd = new GregorianCalendar();
                lastBreak = new GregorianCalendar();

                shiftStart.setTimeInMillis(resultSet.getTimestamp("shift_start").getTime());
                shiftEnd.setTimeInMillis(resultSet.getTimestamp("shift_end").getTime());
                lastBreak.setTimeInMillis(resultSet.getTimestamp("last_break").getTime());

                Shift shift = Shift.loadShift(resultSet.getLong("ID"),
                                              resultSet.getLong("WORKER_ID"),
                                              shiftStart,
                                              shiftEnd,
                                              lastBreak,
                                              resultSet.getLong("TOTAL_BREAK_TIME"));
                result.add(shift);
            }
        } catch (SQLException ex) {
            //TODO: log an exception
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                //TOOD: log an exception
            }
        }

        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return null;
    }

    private DataSource getTestingDataSource() {
        BasicDataSource testingDataSource = new BasicDataSource();
        testingDataSource.setDriverClassName(properties.getProperty("driverName"));
        testingDataSource.setUrl(properties.getProperty("testURL"));
        testingDataSource.setUsername(properties.getProperty("testLogin"));
        testingDataSource.setPassword(properties.getProperty("testPassword"));
        return testingDataSource;
    }

    private DataSource getProductionDataSource() {
        BasicDataSource productionDataSource = new BasicDataSource();
        productionDataSource.setDriverClassName(properties.getProperty("driverName"));
        productionDataSource.setUrl(properties.getProperty("productionURL"));
        productionDataSource.setUsername(properties.getProperty("productionLogin"));
        productionDataSource.setPassword(properties.getProperty("productionPassword"));
        return productionDataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
