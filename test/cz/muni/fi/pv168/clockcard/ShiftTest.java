package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Stein
 */
public class ShiftTest {

    public ShiftTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of save method, of class Shift.
     */
    @Test
    public void testSave() {
        Shift shift = new Shift(10, new GregorianCalendar());
            shift.save();
        System.out.println(shift.getID());
        assertTrue(shift.equals(ShiftManager.getById(shift.getID())));
    }

    /**
     * Test of count method, of class Shift.
     */
    @Test
    public void testCount() throws Exception {
        Connection con = ConnectionManager.getConnection();
        Statement stant = con.createStatement();
         stant.execute("DELETE FROM APP.SHIFTS");
        Shift shift = new Shift(10, new GregorianCalendar());
            shift.save();

         assertTrue(ShiftManager.getCount()==1);
    }

    /**
     * Test of destroy method, of class Shift.
     */
     @Test(expected=ShiftException.class)
     public void testDestroy() throws SQLException, ShiftException{
        try {
            Connection con = ConnectionManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ShiftTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Shift smena = new Shift(10, new GregorianCalendar());
            smena.save();
            smena.destroy();
        ShiftManager.getById(smena.getID());


     }

}