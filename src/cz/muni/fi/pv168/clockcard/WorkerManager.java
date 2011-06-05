
package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Database backend manager for handling Worker class.
 *
 * @author Marek Osvald
 * @version 2011.0522
 */

/* TODO: Consider refactoring with usage of inheritance. */

public class WorkerManager implements IDatabaseManager {
    private static final String DATABASE_PROPERTY_FILE = "src/Database.properties";
    private static final String CLASS_PROPERTY_FILE = "src/Worker.properties";

    private static WorkerManager instance;

    private final Properties databaseProperties = loadProperties(DATABASE_PROPERTY_FILE);
    private final Properties classProperties = loadProperties(CLASS_PROPERTY_FILE);
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
     * Parameterless constructor. Private in order to force creation
     * of WorkerManager solely via getInstance() method.
     */
    private WorkerManager() {
        testingMode = false;
        dataSource = getProductionDataSource();
//        dataSource = getTestingDataSource();
    }

    @Override
    public Worker find(long id) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Worker result = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(classProperties.getProperty("findQuery"));
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
                    //TODO: Log some crap
                }
            }
        }
        
        return result;
    }
    @Override
    public List<Worker> getAll() {
        Connection connection = null;
        Statement statement;
        ResultSet resultSet = null;

        ArrayList<Worker> result = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(classProperties.getProperty("selectAllQuery"));
            result = new ArrayList<Worker>();
            
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
                    //TODO: Log some crap
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
            resultSet = statement.executeQuery(classProperties.getProperty("countQuery"));

            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            result = -1;
            //TODO: handle the exception (logging, error message)
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //TODO: Log some crap
                }
            }
        }
        
        return result;
    }
    @Override
    public boolean getTestingMode() {
        return testingMode;
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
            testingMode = false;
        }
    }
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
    @Override
    public Properties loadProperties(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null.");
        }

        FileInputStream inputStream = null;
        Properties _properties = null;
        
        try {
            inputStream = new FileInputStream(fileName);
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

    /**
     * Returns worker with the selected login from the database or null if the
     * worker is not found.
     *
     * @param login ID of the worker to be found
     * @return worker with the selected ID from the database or null if the
     * worker is not found
     */
    public Worker findByLogin(String login) {
        Connection connection = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        Worker result = null;

        try {
            connection = WorkerManager.getInstance().getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(classProperties.getProperty("findByLoginQuery"));
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.getFetchSize() == 1 && resultSet.next()) {
                result = Worker.loadWorker(resultSet.getLong(1),
                                           resultSet.getString(2),
                                           resultSet.getString(3),
                                           resultSet.getString(4),
                                           resultSet.getString(5),
                                           resultSet.getLong(6),
                                           resultSet.getBoolean(7));
            }else{
            }
        } catch (SQLException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
            //TODO: log.
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    //TODO: log
                }
            }
        }
        return result;
    }

    /**
     * Returns new DataSource representing a connection to the production database.
     *
     * @return new DataSource representing a connection to the production database
     */
    private DataSource getTestingDataSource() {
        BasicDataSource testingDataSource = new BasicDataSource();
        testingDataSource.setDriverClassName(databaseProperties.getProperty("driverName"));
        testingDataSource.setUrl(databaseProperties.getProperty("testDatabase"));
        testingDataSource.setUsername(databaseProperties.getProperty("testLogin"));
        testingDataSource.setPassword(databaseProperties.getProperty("testPassword"));
        return testingDataSource;
    }
    /**
     * Returns new DataSource representing a connection to the production database.
     *
     * @return new DataSource representing a connection to the production database
     */
    private DataSource getProductionDataSource() {
        BasicDataSource productionDataSource = new BasicDataSource();
        productionDataSource.setDriverClassName(databaseProperties.getProperty("driverName"));
        productionDataSource.setUrl(databaseProperties.getProperty("productionDatabase"));
        productionDataSource.setUsername(databaseProperties.getProperty("productionLogin"));
        productionDataSource.setPassword(databaseProperties.getProperty("productionPassword"));
        return productionDataSource;
    }
}
