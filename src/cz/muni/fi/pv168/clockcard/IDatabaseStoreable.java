package cz.muni.fi.pv168.clockcard;

/**
 * Interface implemented by classes that can be stored in the database.
 * @see IDatabaseManager
 *
 * @author Marek Osvald
 * @version 2011.0522
 */

public interface IDatabaseStoreable {
    /**
     * Saves an database storeable object to the database.
     * 
     * @return true if object was success saved, false otherwise.
     */
    boolean save();
    /**
     * Deletes an database storeable object from the database.
     * 
     * @return true if object was successfully deleted, false otherwise.
     */
    boolean destroy();
}
