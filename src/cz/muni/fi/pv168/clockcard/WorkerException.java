package cz.muni.fi.pv168.clockcard;

/**
 * Exception raised when illegal operation is ran on Worker object.
 *
 * @author Marek Osvald
 */
class WorkerException extends Exception {
    public WorkerException() {
    }
    
    public WorkerException(Throwable cause) {
        super(cause);
    }

    public WorkerException(String message) {
        super(message);
    }

    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
