package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Database backend manager for handling Worker class.
 *
 * @author Marek Osvald
 * @version 2011.0518
 */

public class WorkerManager implements IDatabaseManager {
    private static final String PROPERTY_FILE = "src/Worker.properties";

    private static WorkerManager instance;

    private final Properties properties = loadProperties();
    private boolean testingMode;
    private DataSource dataSource;

    /**
     * Returns the sole instance of WorkerManager in the program. Provided that
     * instance has not been created yet, creates one.
     *
     * @return sole WorkerManager instance in the program
     */
    public static WorkerManager getInstance() {
        if (instance == null) {
            instance = new WorkerManager();
        }

        return instance;
    }

    /**
     * Parameterless constructor.
     * Private in order to force creation of WorkerManager solely via getInstance()
     * method.
     */
    private WorkerManager() {
        testingMode = false;
        dataSource = getProductionDataSource();
    }

    @Override
    public ADatabaseStoreable find(long id) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Worker result = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(properties.getProperty("findQuery"));
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.getFetchSize() == 1 && resultSet.next()) {
                result = Worker.loadWorker(resultSet.getLong("ID"),
                                           resultSet.getString("NAME"),
                                           resultSet.getString("SURNAME"),
                                           resultSet.getString("LOGIN"),
                                           resultSet.getString("PASSWORD"),
                                           resultSet.getLong("CURRENT_SHIFT"),
                                           resultSet.getBoolean("SUSPENDED"));
            }
        } catch (SQLException ex) {
            //TODO: Handle an exception (logging, error message)
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //TODO: Handle exception (logging, error message,...)
                }
            }
        }
        
        return result;
    }

    @Override
    public List<? extends ADatabaseStoreable> getAll() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet = null;

        ArrayList<ADatabaseStoreable> result = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(properties.getProperty("selectAllQuery"));
            result = new ArrayList<ADatabaseStoreable>();
            
            while (resultSet.next()) {
                Worker worker = Worker.loadWorker(resultSet.getLong("ID"),
                                                  resultSet.getString("NAME"),
                                                  resultSet.getString("SURNAME"),
                                                  resultSet.getString("LOGIN"),
                                                  resultSet.getString("PASSWORD"),
                                                  resultSet.getLong("CURRENT_SHIFT"),
                                                  resultSet.getBoolean("SUSPENDED"));
                result.add(worker);
            }
        } catch (SQLException ex) {
            //TODO: Handle the Exception (logging, error message)
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //TODO: Handle the exception
                }
            }
        }
        
        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        return null;
    }

    @Override
    public long count() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet = null;
        int result = 0;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeQuery(properties.getProperty("countQuery"));

            resultSet.next();
            result = resultSet.getInt(1);
        } catch (SQLException ex) {
            result = -1;
            //TODO: handle the exception (logging, error message)
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //TODO: Handle the exception (logging, error message)
                }
            }
        }
        
        return result;
    }

    @Override
    public void testingOn() {
        if (!testingMode) {
            dataSource = getTestingDataSource();
            testingMode = true;
        }
    }

    @Override
    public void testingOff() {
        if (testingMode) {
            dataSource = getProductionDataSource();
            testingMode = true;
        }
    }

    @Override
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

    @Override
    public DataSource getDataSource() {
        return dataSource;
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
}
