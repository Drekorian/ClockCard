package cz.muni.fi.pv168.clockcard;

/**
 * Abstract class representing a class that can be stored in the database.
 * @see IDatabaseManager
 *
 * @author Marek Osvald
 * @version 2011.0518
 */

public abstract class ADatabaseStoreable {
    public abstract boolean save();
    public abstract boolean destroy();
}
