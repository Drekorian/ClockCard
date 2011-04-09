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

        List<Worker> list =  Worker.all();
        Iterator<Worker> iterator = list.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getName());
        }
    }


}
