package cz.muni.fi.pv168.clockcard;

import java.util.Properties;

/**
 * Interface implemented by classes that can use properties for storing configuration.
 * @see Supervisor, ADatabaseManager
 * 
 * @author Marek Osvald
 * @version 2011.0604
 */

public interface IPropertyBased {
    /**
     * Retrieves properties from given property file.
     *
     * @param fileName path and filename of the property file
     * @return properties loaded from the property file
     */
    Properties loadProperties(String filename);
}
