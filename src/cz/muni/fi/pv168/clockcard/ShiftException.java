package cz.muni.fi.pv168.clockcard;

/**
 * Exception class that is being thrown when manipulation with Shift's class
 * database backend failed.
 *
 * @author David Stein
 * @version 2011.0518
 */

public class ShiftException extends Exception{
    public ShiftException(Throwable cause) {
        super(cause);
    }

    public ShiftException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShiftException(String message) {
        super(message);
    }

    public ShiftException() {
    }
}
