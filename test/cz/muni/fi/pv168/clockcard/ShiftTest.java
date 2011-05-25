package cz.muni.fi.pv168.clockcard;

import java.util.List;
import java.util.Properties;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing methods for Shift class.
 *
 * @author Marek Osvald
 * @version 2011.0525
 */

public class ShiftTest {
    private static final String CLASS_PROPERTY_FILE = "src/Shift.properties";
    private static final Properties PROPERTIES = ShiftManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);

    private Exception lastThrownException = null;

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
        WorkerManager.getInstance().testingOn();
        ShiftManager.getInstance().testingOn();

        lastThrownException = null;
    }
    @After
    public void tearDown() {
        WorkerManager.getInstance().testingOff();
        ShiftManager.getInstance().testingOff();

        lastThrownException = null;
    }
    
    @Test
    public void testSave() {
        try {
            WorkerManagerTest.resetTable();
            ShiftManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset tables.");
        }

        Worker firstWorker = WorkerManager.getInstance().find(1);
        Shift newShift = new Shift(firstWorker.getID());
        assertNull("Newly created shift should not have an ID", newShift.getID());
        newShift.save();
        assertNotNull(newShift.getID());

        Shift shift = newShift;
        Calendar oldEnd = shift.getEnd();
        shift.setEnd(new GregorianCalendar());
        shift.save();
        assertFalse("End time should have been updated.", shift.getEnd().equals(oldEnd));
    }
    @Test
    public void testDestroy() {
        Shift newShift = new Shift(1l);
        assertNull("New shift should not have an ID.", newShift.getID());
        assertFalse("Unsaved shift should have returned false.", newShift.destroy());

        try {
            ShiftManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        assertTrue("Database should be in the default state.", ShiftManager.getInstance().count() == ShiftManagerTest.SHIFT_STARTS.length);
        List<Shift> originalShifts = new ArrayList<Shift>();
        for (int i = 0; i < ShiftManagerTest.SHIFT_STARTS.length; i++) {
            Shift shift = new Shift(ShiftManagerTest.WORKER_IDS[i]);
            shift.setStart(ShiftManagerTest.SHIFT_STARTS[i]);

            if (ShiftManagerTest.LAST_BREAK_STARTS[i] != null) {
                shift.setLastBreakStart(ShiftManagerTest.LAST_BREAK_STARTS[i]);
            }

            if (ShiftManagerTest.TOTAL_BREAK_TIMES[i] > 0) {
                shift.setTotalBreakTime(ShiftManagerTest.TOTAL_BREAK_TIMES[i]);
            }

            if (ShiftManagerTest.SHIFT_ENDS[i] != null) {
                shift.setEnd(ShiftManagerTest.SHIFT_ENDS[i]);
            }

            originalShifts.add(shift);
        }

        assertEquals("Database should be in the default state.", originalShifts, ShiftManager.getInstance().getAll());

        Shift firstShift = ShiftManager.getInstance().find(1);
        firstShift.destroy();
        assertTrue("There should be one less records in the table.", ShiftManager.getInstance().count() == ShiftManagerTest.SHIFT_STARTS.length - 1);
        assertNull("Shift with ID 1 should not be in the table.", ShiftManager.getInstance().find(1));
    }
    @Test
    public void testAddBreakTime() {
        try {
            WorkerManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        Worker firstWorker = WorkerManager.getInstance().find(1);
        Shift shift = new Shift(firstWorker.getID());

        try {
            shift.addBreakTime(0);
            assertNotNull("IllegalArgumentException should have been thrown.", lastThrownException);
        } catch (IllegalArgumentException ex) {
            lastThrownException =  ex;
        }
        lastThrownException = null;

        long oldBreakTime = shift.getTotalBreakTime();
        shift.addBreakTime(1000);
        assertTrue("Thousands milliseconds should have been added to the totalBreakTime", shift.getTotalBreakTime() == oldBreakTime + 1000);
    }
}