/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Fires
 */
public class Test {
    public static void main(String[] args) throws SQLException {
        /*List<Worker> list = Worker.all();
        Iterator<Worker> iterator = list.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

        Worker work = new Worker("saveTest", "saveTest", "saveTestLogin");
            work.save();
            work.setId(3);
            work.destroy();*/

        Connection c = DriverManager.getConnection("jdbc:derby:db/clockcard", "root", "root");
        c.close();

    }
}
