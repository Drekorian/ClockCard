package cz.muni.fi.pv168.clockcard;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Represents workers supervisor in the ClockCard system.
 *
 * @author Marek Osvald
 * @version 2011.0518
 */

public class Supervisor {
    private static final String CLASS_PROPERTY_FILE = "Supervisor.java";
    private static final Properties PROPERTIES = loadProperties();
    private static final String PASSWORD = PROPERTIES.getProperty("password");

    /**
     * Parameterless constructor. Forces class to be static.
     */
    private Supervisor() { }

    /**
     * Compares the given parameter with supervisory password.
     *
     * @param password password to authenticate with
     * @return true is the passwords match, false otherwise
     */
    public static boolean authenticate(String password) {
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        return PASSWORD.equals(password);
    }

    //public static get

    public static Properties loadProperties() {
        FileInputStream inputStream = null;
        Properties _properties = null;

        try {
            inputStream = new FileInputStream(CLASS_PROPERTY_FILE);
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
}
