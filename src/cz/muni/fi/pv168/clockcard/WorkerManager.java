package cz.muni.fi.pv168.clockcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database backend manager for handling Worker class.
 *
 * @author Marek Osvald
 * @version 2011.0522
 */

public class WorkerManager extends ADatabaseManager {
    private static final String CLASS_PROPERTY_FILE = "src/Worker.properties";
    private static final Logger LOGGER = Logger.getLogger(WorkerManager.class.getName());

    private static WorkerManager instance;
 
    private final Properties CLASS_PROPERTIES = loadProperties(CLASS_PROPERTY_FILE);

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
        LOGGER.finest(CLASS_PROPERTIES.getProperty("log.newInstance"));
    }

    @Override
    public Worker find(long id) {
        Connection connection = null;
        List<QueryParameter> params;
        ResultSet resultSet;
        Worker result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }        

        params = new ArrayList<QueryParameter>();
        params.add(new QueryParameter(QueryParameter.LONG, id));
        
        try {
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findQuery"), params);
            if (resultSet != null && resultSet.getFetchSize() == 1 && resultSet.next()) {
                result = Worker.loadWorker(resultSet.getLong("ID"),
                                           resultSet.getString("NAME"),
                                           resultSet.getString("SURNAME"),
                                           resultSet.getString("LOGIN"),
                                           resultSet.getString("PASSWORD"),
                                           resultSet.getLong("CURRENT_SHIFT"),
                                           resultSet.getBoolean("SUSPENDED"));
                }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.findFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0}: {1} {2}", new Object[]{ CLASS_PROPERTIES.getProperty("log.findSuccess"), result.getName(), result.getSurname() });
        }
        
        return result;
    }
    @Override
    public List<Worker> getAll() {
        Connection connection = null;
        ResultSet resultSet;
        List<Worker> result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        try {
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("selectAllQuery"));
            result = new ArrayList<Worker>();
            
            while (resultSet != null && resultSet.next()) {
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
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.getAllFailed"), ex);
        } finally {
            terminateConnection(connection);
        }
        
        if (result != null) {
            return Collections.unmodifiableList(result);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0} [{1}]", new Object[]{ CLASS_PROPERTIES.getProperty("log.getAllSuccess"), result.size() });
        }

        return null;
    }
    @Override
    public long count() {
        Connection connection = null;
        ResultSet resultSet;
        int result = 0;

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
            result = -1;
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
     * Returns worker with the selected login from the database or null if the
     * worker is not found.
     *
     * @param login ID of the worker to be found
     * @return worker with the selected ID from the database or null if the
     * worker is not found
     */
    public Worker findByLogin(String login) {
        Connection connection = null;
        List<QueryParameter> params = new ArrayList<QueryParameter>();
        ResultSet resultSet;
        Worker result = null;

        if ((connection = openConnection()) == null) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.connectionFailed"));
            return null;
        }

        params = new ArrayList<QueryParameter>();
        params.add(new QueryParameter(QueryParameter.STRING, login));

        try {
            resultSet = executeQuery(connection, CLASS_PROPERTIES.getProperty("findByLoginQuery"), params);

            if (resultSet != null && resultSet.getFetchSize() == 1 && resultSet.next()) {
                result = Worker.loadWorker(resultSet.getLong(1),
                                           resultSet.getString(2),
                                           resultSet.getString(3),
                                           resultSet.getString(4),
                                           resultSet.getString(5),
                                           resultSet.getLong(6),
                                           resultSet.getBoolean(7));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, CLASS_PROPERTIES.getProperty("log.findByLoginFailed"), ex);
        } finally {
            terminateConnection(connection);
        }

        if (result != null) {
            LOGGER.log(Level.FINEST, "{0}: {1} {2}", new Object[]{ CLASS_PROPERTIES.getProperty("log.findByLoginSuccess"), result.getName(), result.getSurname() });
        }
        
        return result;
    }
}
