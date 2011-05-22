package cz.muni.fi.pv168.clockcard;

import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Interface implemented by database managers.
 *
 * @author Marek Osvald
 * @version 2011.0522
 */

public interface IDatabaseManager {
    /**
     * Returns list of all records mapped via ORM
     * 
     * @return list of all records mapped via ORM
     */
    List<? extends IDatabaseStoreable> getAll();
    /**
     * Finds database storeable object in the database and returns it.
     * 
     * @return provided that object with selected ID exists an database storeable object, null otherwise
     */
    IDatabaseStoreable find(long id);
    /**
     * Counts all records in the table.
     *
     * @return total of the records
     */
    long count();
    /**
     * Returns whether testing mode is on.
     *
     * @return true when testing mode is on, false otherwise.
     */
    boolean getTestingMode();
    /**
     * Turns on the testing mode.
     */
    void testingOn();
    /**
     * Turns off the testing mode.
     */
    void testingOff();
    /**
     * Returns manager's DataSource.
     *
     * @return manager's DataSource
     */
    DataSource getDataSource();
    /**
     * Retrieves properties from given property file.
     *
     * @param fileName path and filename of the property file
     * @return properties loaded from the property file
     */
    Properties loadProperties(String fileName);
}
