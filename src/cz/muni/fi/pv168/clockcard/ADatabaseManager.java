package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Interface implemented by database managers.
 *
 * @author Marek Osvald
 * @version 2011.0604
 */

public abstract class ADatabaseManager implements IPropertyBased {
    private static final String DATABASE_PROPERTY_FILE = "src/Database.properties";
    private static final Logger LOGGER = Logger.getLogger(ADatabaseManager.class.getName());

    private final Properties DATABASE_PROPERTIES = loadProperties(DATABASE_PROPERTY_FILE);

    private DataSource dataSource;
    private boolean testingMode = false;

    /**
     * Non-parametric constructor. Sets manager's data source to production mode.
     */
    public ADatabaseManager() {
        dataSource = getProductionDataSource();
    }

    @Override
    public Properties loadProperties(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null.");
        }

        FileInputStream inputStream = null;
        Properties properties = null;

        try {
            inputStream = new FileInputStream(fileName);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, DATABASE_PROPERTIES.getProperty("log.propertyLoadFail"), ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, DATABASE_PROPERTIES.getProperty("log.propertyCloseFail"), ex);
                }
            }
        }

        if (properties != null) {
            return properties;
        }

        return new Properties();
    }

    /**
     * Returns list of all records mapped via ORM
     * 
     * @return list of all records mapped via ORM
     */
    public abstract List<? extends IDatabaseStoreable> getAll();
    /**
     * Finds database storeable object in the database and returns it.
     * 
     * @return provided that object with selected ID exists an database storeable object, null otherwise
     */
    public abstract IDatabaseStoreable find(long id);
    /**
     * Counts all records in the table.
     *
     * @return total of the records
     */
    public abstract long count();

    /**
     * Returns whether testing mode is on.
     *
     * @return true when testing mode is on, false otherwise.
     */
    public boolean getTestingMode() {
        return testingMode;
    }
    /**
     * Turns on the testing mode.
     */
    public void testingOn() {
        if (!testingMode) {
            dataSource = getTestingDataSource();
            testingMode = true;
            LOGGER.log(Level.CONFIG, DATABASE_PROPERTIES.getProperty("log.testingOn"));
        }
    }
    /**
     * Turns off the testing mode.
     */
    void testingOff() {
        if (testingMode) {
            dataSource = getProductionDataSource();
            testingMode = false;
            LOGGER.log(Level.CONFIG, DATABASE_PROPERTIES.getProperty("log.testingOff"));
        }
    }
    /**
     * Returns manager's data source.
     *
     * @return manager's data source
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Returns new DataSource representing a connection to the testing database.
     *
     * @return new DataSource representing a connection to the testing database
     */
    private DataSource getTestingDataSource() {
        BasicDataSource testingDataSource = new BasicDataSource();
        testingDataSource.setDriverClassName(DATABASE_PROPERTIES.getProperty("driverName"));
        testingDataSource.setUrl(DATABASE_PROPERTIES.getProperty("testDatabase"));
        testingDataSource.setUsername(DATABASE_PROPERTIES.getProperty("testLogin"));
        testingDataSource.setPassword(DATABASE_PROPERTIES.getProperty("testPassword"));
        return testingDataSource;
    }
    /**
     * Returns new DataSource representing a connection to the production database.
     *
     * @return new DataSource representing a connection to the production database
     */
    private DataSource getProductionDataSource() {
        BasicDataSource testingDataSource = new BasicDataSource();
        testingDataSource.setDriverClassName(DATABASE_PROPERTIES.getProperty("driverName"));
        testingDataSource.setUrl(DATABASE_PROPERTIES.getProperty("productionDatabase"));
        testingDataSource.setUsername(DATABASE_PROPERTIES.getProperty("productionLogin"));
        testingDataSource.setPassword(DATABASE_PROPERTIES.getProperty("productionPassword"));
        return testingDataSource;
    }

    /**
     * TODO: javadoc
     * @param preparedStatement
     * @param params
     * @throws SQLException
     */
    private void processQueryParameters(PreparedStatement preparedStatement, List<QueryParameter> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            switch (params.get(i).getType()) {
                case QueryParameter.BOOLEAN:
                    if (params.get(i).getValue() != null) {
                        preparedStatement.setBoolean(i + 1, (Boolean) params.get(i).getValue());
                    } else {
                        preparedStatement.setNull(i + 1, Types.BOOLEAN);
                    }
                    break;
                case QueryParameter.LONG:
                    if (params.get(i).getValue() != null) {
                        preparedStatement.setLong(i + 1, (Long) params.get(i).getValue());
                    } else {
                        preparedStatement.setNull(i + 1, Types.BIGINT);
                    }
                    break;
                case QueryParameter.TIMESTAMP:
                    if (params.get(i).getValue() != null) {
                        preparedStatement.setTimestamp(i + 1, (Timestamp) params.get(i).getValue());
                    } else {
                        preparedStatement.setNull(i + 1, Types.TIMESTAMP);
                    }
                    break;
                case QueryParameter.STRING:
                    if (params.get(i).getValue() != null) {
                        preparedStatement.setString(i + 1, (String) params.get(i).getValue());
                    } else {
                        preparedStatement.setNull(i + 1, Types.VARCHAR);
                    }
                    break;
            }
        }
    }

    /**
     * TODO: Javadoc
     *
     * @param params
     * @return
     */
    protected ResultSet executeQuery(Connection connection, String query, List<QueryParameter> params) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }

        if (query == null || query.equals("")) {
            throw new IllegalArgumentException("query cannot be null");
        }

        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }

        PreparedStatement preparedStatement = null;
        ResultSet result = null;

        
        preparedStatement = connection.prepareStatement(query);

        if (params.size() > 0) {
            processQueryParameters(preparedStatement, params);
        }
            
        result = preparedStatement.executeQuery();

        return result;
    }
    /**
     * TODO: javadoc
     *
     * @param connection
     * @param query
     * @return
     */
    protected ResultSet executeQuery(Connection connection, String query) throws SQLException {
        return executeQuery(connection, query, new ArrayList<QueryParameter>());
    }
    /**
     * TODO: Javadoc
     *
     * @param params
     * @return
     */
    protected int executeUpdate(Connection connection, String query, List<QueryParameter> params, Long key) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }

        if (query == null || query.equals("")) {
            throw new IllegalArgumentException("query cannot be null");
        }

        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }

        PreparedStatement preparedStatement = null;
        int result = -1;

        if (key != null) {
            preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            preparedStatement = connection.prepareStatement(query);
        }

        if (params.size() > 0) {
            processQueryParameters(preparedStatement, params);
        }

        result = preparedStatement.executeUpdate();

        if (key != null) {
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                key = keys.getLong(1);
            }
        }
    
        return result;
    }
    /**
     * TODO: javadoc
     *
     * @param connection
     * @param query
     * @return
     */
    protected int executeUpdate(Connection connection, String query, List<QueryParameter> params) throws SQLException {
        return executeUpdate(connection, query, params, null);
    }

    /**
     * TODO: javadoc
     * @return
     */
    public Connection openConnection() {
        Connection result = null;
        
        try {
            result = getDataSource().getConnection();
            LOGGER.log(Level.FINEST, DATABASE_PROPERTIES.getProperty("log.openConnectionSuccess"));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, DATABASE_PROPERTIES.getProperty("log.openConnectionFailed"), ex);
        }

        return result;
    }
    /**
     * TODO: JAVADOC
     * 
     * @param connection
     */
    public void terminateConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.log(Level.FINEST, DATABASE_PROPERTIES.getProperty("log.closeConnectionSuccess"));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, DATABASE_PROPERTIES.getProperty("log.closeConnectionFailed"), ex);
            }
        }
    }
}
