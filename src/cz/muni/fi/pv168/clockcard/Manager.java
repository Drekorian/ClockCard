package cz.muni.fi.pv168.clockcard;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents manager of the ClockCard system.
 *
 * @author Marek Osvald
 * @version 2011.0518
 * 
 * TODO: Refactor - rename to Supervisor in order not to be mistaken with database manager classes
 */

public class Manager {
    private static final String PASSWORD = "";
    //TODO: Load manager password from property file.

    /**
     * Parameterless constructor.
     */
    private Manager() { }

    public List<Worker> getWorkers() {
        //TODO implement (watch out for imports)
        return new ArrayList<Worker>();
    }

    public List<Shift> getShifts() {
        //TODO implement (watch out for imports)
        return new ArrayList<Shift>();
    }

    public Map<Worker, Time> getWorkedTime() {
        //TODO implement (watch out for imports)
        return new HashMap<Worker, Time>();
    }

    /**
     * Creates a new worker and saves him into a database.
     *
     * @param name name of the hired worker
     * @param surname surname of the hired worker
     * @param login login of the hired worker
     */
    public void hireWorker(String name, String surname, String login) {
        //TODO: implement
    }

    /**
     * Deletes a suspended worker from the database.
     */
    public void fireSuspendedWorker() {
        //TODO: implement
    }
}
