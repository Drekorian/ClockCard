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
 */

public class Manager extends aPerson {

    /**
     * Parametric constructor.
     * 
     * @param name name of the manager
     * @param surname surname of the manager
     * @param login login of the manager
     */
    Manager(String name, String surname, String login) {
        super(name, surname, login);
    }

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
}
