package cz.muni.fi.pv168.clockcard;

/**
 * Exception being raised when illegal operation is ran on Shift object.
 *
 * @author David Stein
 * @author Marek Osvald
 * @version 2011.0522
 */

public class ShiftException extends Exception{
    public ShiftException() { }
    
    public ShiftException(Throwable cause) {
        super(cause);
    }

    public ShiftException(String message) {
        super(message);
    }
    
    public ShiftException(String message, Throwable cause) {
        super(message, cause);
    }
}
