package cz.muni.fi.pv168.clockcard;

import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Interface implemented by database managers.
 *
 * @author Marek Osvald
 * @version 2011.0518
 */

public interface IDatabaseManager {
    /**
     * Returns list of all records mapped via ORM
     * 
     * @return list of all records mapped via ORM
     */
    List<? extends ADatabaseStoreable> getAll();
    /**
     * Finds database storeable object and returns it.
     * 
     * @return provided that object with selected ID exists an database storeable object, null otherwise
     */
    ADatabaseStoreable find(long id);
    /**
     * Counts all records in the table.
     *
     * @return total of the records
     */
    long count();
    /**
     * Turns on the testing mode.
     */
    void testingOn();
    /**
     * Turns off the testing mode.
     */
    void testingOff();
    /**
     * TODO: JAVADOC ME!
     *
     * @return
     */
    DataSource getDataSource();

    /**
     * Retrieves properties from database manager property file.
     *
     * @return database manager properties
     */
    Properties loadProperties();
}
