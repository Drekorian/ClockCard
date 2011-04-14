/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fires
 */
public class TestPool {

    public static void main(String[] args) throws SQLException{
        System.out.println("zakladam smenu 1");
        Shift smena = new Shift(7, new GregorianCalendar());
            smena.save();

            smena.setTotalBreakTime(100);
            smena.setEnd(new GregorianCalendar());
            smena.setLastBreakStart(new GregorianCalendar());
            smena.save();
        System.out.println("zakladam smenu 2");
        Shift smena2 = new Shift(8, new GregorianCalendar());
            smena2.save();
        System.out.println("zakladam smenu 3");
        Shift smena3 = new Shift(8, new GregorianCalendar());
            smena3.save();


      System.out.println("vypisuju smeny");
       List<Shift> seznam = ShiftManager.getAll();
       Iterator<Shift> itr = seznam.iterator();

       while(itr.hasNext()){
            Shift sh = itr.next();
             System.out.println(sh.getId());
       }

       System.out.println("vypisuju pocet smen");
       System.out.println(ShiftManager.getCount());




    }


}
