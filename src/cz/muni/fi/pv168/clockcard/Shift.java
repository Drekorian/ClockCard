/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.clockcard;

import java.util.List;

/**
 *
 * @author Marek Osvald
 */

public class Shift {

    public static Shift find(Long id) {
        return new Shift();
    }

    static List<Shift> findByWorker(Long id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public long getID() {
        //TODO: implement;
        return 0;
    }
}
