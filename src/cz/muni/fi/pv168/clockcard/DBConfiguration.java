package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Purpose of DBConfiguration class?
 * TODO: Documentation? Is this ours or 3rd party code?
 *
 * @author David Stein
 * @version 2011.0518
 */

public class DBConfiguration {
    private static final String PROPERTIES_FILE = "src/Worker.properties";

    private String driverName;
    private String user;
    private String password;
    private String dbUrl;
    private int dbPoolMinSize;
    private int dbPoolMaxSize;
    private boolean productionMode;

    public DBConfiguration(boolean productionMode) {
        this.productionMode = productionMode;
        this.setUp();
    }

    public DBConfiguration() {
        this(false);
    }
    
    public final void setUp(){
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(DBConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.driverName = properties.getProperty("driverName");
        this.dbPoolMinSize = Integer.parseInt(properties.getProperty("dbPoolMinSize"));
        this.dbPoolMaxSize = Integer.parseInt(properties.getProperty("dbPoolMaxSize"));

        if (this.productionMode) {
            this.dbUrl = properties.getProperty("productionDatabase");
            this.user = properties.getProperty("productionLogin");
            this.password = properties.getProperty("productionPassword");
        } else {
            this.dbUrl = properties.getProperty("testDatabase");
            this.user = properties.getProperty("testLogin");
            this.password = properties.getProperty("testPassword");
        }
    }

    public int getDbPoolMaxSize() {
        return dbPoolMaxSize;
    }

    public int getDbPoolMinSize() {
        return dbPoolMinSize;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }
}
