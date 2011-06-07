package cz.muni.fi.pv168.clockcard;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing methods for ShiftManager class.
 * 
 * @author Marek Osvald
 * @version 2011.0525
 */

public class ShiftManagerTest {
    public static final String DATABASE_PROPERTY_FILE = "src/Database.properties";
    public static final String CLASS_PROPERTY_FILE = "src/Shift.properties";

    public static final long[] WORKER_IDS = { 1, 2, 3, 3, 4, 4, 4, 6, 6, 7 };
    public static final Calendar[] SHIFT_STARTS = { new GregorianCalendar(2011, 5, 15, 16, 1, 5),
                                                    new GregorianCalendar(2011, 5, 17, 9, 30, 4),
                                                    new GregorianCalendar(2011, 5, 18, 9, 28, 26),
                                                    new GregorianCalendar(2011, 5, 19, 9, 44, 12),
                                                    new GregorianCalendar(2011, 5, 15, 16, 1, 5),
                                                    new GregorianCalendar(2011, 5, 16, 23, 2, 54),
                                                    new GregorianCalendar(2011, 5, 17, 22, 59, 58),
                                                    new GregorianCalendar(2011, 5, 22, 4, 15, 42),
                                                    new GregorianCalendar(2011, 5, 23, 4, 16, 3),
                                                    new GregorianCalendar(2011, 5, 25, 4, 14, 1) };

    public static final Calendar[] SHIFT_ENDS = { new GregorianCalendar(2011, 5, 15, 22, 2, 16),
                                                  new GregorianCalendar(2011, 5, 17, 13, 34, 22),
                                                  new GregorianCalendar(2011, 5, 18, 13, 31, 5),
                                                  new GregorianCalendar(2011, 5, 19, 13, 46, 58),
                                                  new GregorianCalendar(2011, 5, 16, 0, 3, 18),
                                                  new GregorianCalendar(2011, 5, 17, 11, 4, 55),
                                                  new GregorianCalendar(2011, 5, 18, 10, 59, 0),
                                                  new GregorianCalendar(2011, 5, 23, 10, 45, 34),
                                                  new GregorianCalendar(2011, 5, 24, 10, 48, 32),
                                                  new GregorianCalendar(2011, 5, 26, 10, 47, 51) };
    public static final Calendar[] LAST_BREAK_STARTS = { null, null, null, null, null, null, null, null, null, null};
    public static final long[] TOTAL_BREAK_TIMES = { 0, 0, 0, 0, 870000, 1799949, 1831354, 0, 0, 0};

    public ShiftManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }
    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    @Before
    public void setUp() {
        ShiftManager.getInstance().testingOn();
    }
    @After
    public void tearDown() {
        ShiftManager.getInstance().testingOff();
    }

    @Test
    public void testGetInstance() {
        assertNotNull("Method getInstance() should return an instance.", WorkerManager.getInstance());
    }
    @Test
    public void testFind() {
        try {
            resetTable();
        } catch (SQLException e) {
            fail("Unable to reset table.");
        }

        for (int i = 0; i < SHIFT_STARTS.length; i++) {
            Shift shift = new Shift(WORKER_IDS[i]);

            shift.setStart(SHIFT_STARTS[i]);
            shift.setTotalBreakTime(TOTAL_BREAK_TIMES[i]);

            if (LAST_BREAK_STARTS[i] != null) {
                shift.setLastBreakStart(LAST_BREAK_STARTS[i]);
            }

            if (SHIFT_ENDS[i] != null) {
                shift.setEnd(SHIFT_ENDS[i]);
            }
            
            assertEquals(shift, ShiftManager.getInstance().find(i + 1));
        }
    }
    @Test
    public void testGetAll() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }
        
        List<Shift> originalShifts = new ArrayList<Shift>();

        for(int i = 0; i < SHIFT_STARTS.length; i++) {
            Shift shift = new Shift(WORKER_IDS[i]);

            shift.setStart(SHIFT_STARTS[i]);
            shift.setTotalBreakTime(TOTAL_BREAK_TIMES[i]);

            if (SHIFT_ENDS[i] != null) {
                shift.setEnd(SHIFT_ENDS[i]);
            }

            if (LAST_BREAK_STARTS[i] != null ) {
                shift.setLastBreakStart(LAST_BREAK_STARTS[i]);
            }

            originalShifts.add(shift);
        }

        assertEquals(originalShifts, ShiftManager.getInstance().getAll());
    }
    @Test
    public void testCount() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        assertEquals("Amount of shifts do not match.", SHIFT_STARTS.length, ShiftManager.getInstance().count());
    }
    @Test
    public void testTestingModeOn() {
        assertTrue("Testing mode should be on", ShiftManager.getInstance().getTestingMode());
        DataSource testingDataSource = ShiftManager.getInstance().getDataSource();
        ShiftManager.getInstance().testingOn();
        assertTrue("Testing mode should be on", ShiftManager.getInstance().getTestingMode());
        assertSame("DataSource should have stayed the same.", testingDataSource, ShiftManager.getInstance().getDataSource());
        ShiftManager.getInstance().testingOff();
        assertFalse("Testing mode should be off", ShiftManager.getInstance().getTestingMode());
        DataSource productionDataSource = ShiftManager.getInstance().getDataSource();
        assertNotSame("DataSource should have changed from testing to production.", productionDataSource, testingDataSource);
        ShiftManager.getInstance().testingOn();
        assertTrue("Testing mode should be on", ShiftManager.getInstance().getTestingMode());
        assertNotSame("DataSource should have changed from production to testing.", productionDataSource, testingDataSource);
    }
    @Test
    public void testTestingOff() {
        assertTrue("Testing mode should be on.", ShiftManager.getInstance().getTestingMode());
        DataSource testingDataSource = ShiftManager.getInstance().getDataSource();
        ShiftManager.getInstance().testingOff();
        assertFalse("Testing mode should be off.", ShiftManager.getInstance().getTestingMode());
        assertNotSame("DataSource should have changed from testing to production.", testingDataSource, ShiftManager.getInstance().getDataSource());
        DataSource productionDataSource = ShiftManager.getInstance().getDataSource();
        WorkerManager.getInstance().testingOff();
        assertFalse("Testing mode should be off.", ShiftManager.getInstance().getTestingMode());
        assertSame("DataSource should have stayed the same.", productionDataSource, ShiftManager.getInstance().getDataSource());
    }
    @Test
    public void testLoadProperties() {
        Properties databaseProperties = ShiftManager.getInstance().loadProperties(DATABASE_PROPERTY_FILE);
        assertNotNull("Database properties should have been loaded.", databaseProperties);
        Properties classProperties = ShiftManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
        assertNotNull("Class properties should have been loaded.", classProperties);
    }
    @Test
    public void testFindByWorkerID() {
        try {
            resetTable();
        } catch (SQLException e) {
            fail("Unable to reset table.");
        }

        List<Shift> shifts = new ArrayList<Shift>();
        for (int i = 0; i < SHIFT_STARTS.length; i++) {
            Shift shift = new Shift(WORKER_IDS[i]);

            shift.setStart(SHIFT_STARTS[i]);
            shift.setTotalBreakTime(TOTAL_BREAK_TIMES[i]);

            if (LAST_BREAK_STARTS[i] != null) {
                shift.setLastBreakStart(LAST_BREAK_STARTS[i]);
            }

            if (SHIFT_ENDS[i] != null) {
                shift.setEnd(SHIFT_ENDS[i]);
            }
            
            shifts.add(shift);
        }

        Map<Long, List<Shift>> workerShifts = new HashMap<Long, List<Shift>>();

        for (Shift shift: shifts) {
            if (workerShifts.get(shift.getWorkerID()) == null) {
                workerShifts.put(shift.getWorkerID(), new ArrayList<Shift>());
            }
            
            workerShifts.get(shift.getWorkerID()).add(shift);
        }

        for (long workerID: workerShifts.keySet()) {
            assertTrue("Result should contain the same amoun of shifts.", 
                    workerShifts.get(workerID).size() == ShiftManager.getInstance().findByWorkerID(workerID).size());
        }
    }
    @Test
    public void testFindStartBetweeen() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }

        List<Shift> originalShifts = new ArrayList<Shift>();

        for(int i = 0; i < SHIFT_STARTS.length; i++) {
            Shift shift = new Shift(WORKER_IDS[i]);

            shift.setStart(SHIFT_STARTS[i]);
            shift.setTotalBreakTime(TOTAL_BREAK_TIMES[i]);

            if (SHIFT_ENDS[i] != null) {
                shift.setEnd(SHIFT_ENDS[i]);
            }

            if (LAST_BREAK_STARTS[i] != null ) {
                shift.setLastBreakStart(LAST_BREAK_STARTS[i]);
            }

            originalShifts.add(shift);
        }

        assertEquals(originalShifts, ShiftManager.getInstance().findStartBetween(
                new Timestamp(new GregorianCalendar(2011, 5,  1).getTimeInMillis()),
                new Timestamp(new GregorianCalendar(2011, 5, 31).getTimeInMillis())));
    }

    public static void resetTable() throws SQLException {
        Connection connection = ShiftManager.getInstance().getDataSource().getConnection();
        Statement statement;

        statement = connection.createStatement();
        try {
            statement.executeUpdate("DROP TABLE APP.shifts");
        } catch (SQLException ex) {
            if (!ex.getSQLState().equals("42Y55")) {
                throw ex;
            }
        }
        statement.close();
        statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE APP.shifts ("
                             + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                             + "WORKER_ID BIGINT not null,"
                             + "SHIFT_START TIMESTAMP,"
                             + "SHIFT_END TIMESTAMP,"
                             + "LAST_BREAK TIMESTAMP,"
                             + "TOTAL_BREAK_TIME BIGINT)");
        statement.close();

        for (int i = 0; i < WORKER_IDS.length; i++) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO APP.shifts(worker_id, shift_start, shift_end, last_break, total_break_time) values(?, ?, ?, ?, ?)");
            preparedStatement.setLong(1, WORKER_IDS[i]);

            if (SHIFT_STARTS[i] != null) {
                preparedStatement.setTimestamp(2, new Timestamp(SHIFT_STARTS[i].getTimeInMillis()));
            } else {
                preparedStatement.setNull(2, Types.TIMESTAMP);
            }

            if (SHIFT_ENDS[i] != null) {
                preparedStatement.setTimestamp(3, new Timestamp(SHIFT_ENDS[i].getTimeInMillis()));
            } else {
                preparedStatement.setNull(3, Types.TIMESTAMP);
            }

            if (LAST_BREAK_STARTS[i] != null) {
                preparedStatement.setTimestamp(4, new Timestamp(LAST_BREAK_STARTS[i].getTimeInMillis()));
            } else {
                preparedStatement.setNull(4, Types.TIMESTAMP);
            }

            preparedStatement.setLong(5, TOTAL_BREAK_TIMES[i]);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        connection.close();
    }
}