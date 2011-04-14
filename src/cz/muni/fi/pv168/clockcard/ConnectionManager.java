/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.*;
import org.apache.commons.pool.impl.GenericObjectPool;
/**
 *
 * @author Fires
 */
public class ConnectionManager {

    private static Connection connection = null;
    public static DataSource ds = null;
    private static GenericObjectPool _pool = null;

    public ConnectionManager(DBConfiguration config) {
        this.connectToDb(config);
    }

    public static Connection getConnection() throws SQLException {
        if(ConnectionManager.connection==null){
               ConnectionManager cm = new ConnectionManager(new DBConfiguration(false));
                try {
                    ConnectionManager.connection = ConnectionManager.ds.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(TestPool.class.getName()).log(Level.SEVERE, null, ex);
               }
        }
       return ConnectionManager.connection;
    }


    @Override
    protected void finalize()
    {
        try {
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }

    private void connectToDb(DBConfiguration config) {
        try {
            java.lang.Class.forName(config.getDriverName()).newInstance();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        ConnectionManager.ds = this.setupDataSource(
                    config.getDbUrl(),
                    config.getUser(),
                    config.getPassword(),
                    config.getDbPoolMinSize(),
                    config.getDbPoolMaxSize());
    }

    private DataSource setupDataSource(String dbUrl, String user, String password, int dbPoolMinSize, int dbPoolMaxSize) {

        GenericObjectPool connectionPool = new GenericObjectPool(null);
        connectionPool.setMinIdle( dbPoolMinSize );
        connectionPool.setMaxActive( dbPoolMaxSize );

        ConnectionManager._pool = connectionPool;

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbUrl,user, password);

        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(
                connectionFactory, connectionPool, null, null, false, true);

        PoolingDataSource dataSource =
                new PoolingDataSource(connectionPool);

        return dataSource;
    }
    
}
