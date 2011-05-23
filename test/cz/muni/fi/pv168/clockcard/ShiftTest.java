//package cz.muni.fi.pv168.clockcard;
//
//import java.util.Properties;
//import java.sql.*;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// * @author Marek Osvald
// */
//
//public class ShiftTest {
//    private static final String CLASS_PROPERTY_FILE = "src/Shift.properties";
//    private static final Properties PROPERTIES = ShiftManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
//
//    private static final long[] WORKER_IDS = { 1, 2, 3, 3, 4, 4, 4, 6, 6, 7 };
//    private static final Calendar[] SHIFT_STARTS = { new GregorianCalendar(2011, 5, 15, 16, 1, 5),
//                                                     new GregorianCalendar(2011, 5, 17, 9, 30, 4),
//                                                     new GregorianCalendar(2011, 5, 18, 9, 28, 26),
//                                                     new GregorianCalendar(2011, 5, 19, 9, 44, 12),
//                                                     new GregorianCalendar(2011, 5, 15, 16, 1, 5),
//                                                     new GregorianCalendar(2011, 5, 16, 23, 2, 54),
//                                                     new GregorianCalendar(2011, 5, 17, 22, 59, 58),
//                                                     new GregorianCalendar(2011, 5, 22, 4, 15, 42),
//                                                     new GregorianCalendar(2011, 5, 23, 4, 16, 3),
//                                                     new GregorianCalendar(2011, 5, 25, 4, 14, 1) };
//
//    private static final Calendar[] SHIFT_ENDS = { new GregorianCalendar(2011, 5, 15, 22, 2, 16),
//                                                   new GregorianCalendar(2011, 5, 17, 13, 34, 22),
//                                                   new GregorianCalendar(2011, 5, 18, 13, 31, 5),
//                                                   new GregorianCalendar(2011, 5, 19, 13, 46, 58),
//                                                   new GregorianCalendar(2011, 5, 16, 0, 3, 18),
//                                                   new GregorianCalendar(2011, 5, 17, 11, 4, 55),
//                                                   new GregorianCalendar(2011, 5, 18, 10, 59, 0),
//                                                   new GregorianCalendar(2011, 5, 19, 10, 45, 34),
//                                                   new GregorianCalendar(2011, 5, 24, 10, 48, 32),
//                                                   new GregorianCalendar(2011, 5, 26, 10, 47, 51) };
//    private static final Calendar[] LAST_BREAK_STARTS = { null, null, null, null, null, null, null, null, null, null};
//    private static final long[] TOTAL_BREAK_TIMES = { 0, 0, 0, 0, 870000, 1799949, 1831354, 0, 0, 0};
//
//    public ShiftTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//        WorkerManager.getInstance().testingOn();
//        ShiftManager.getInstance().testingOn();
//    }
//
//    @After
//    public void tearDown() {
//        WorkerManager.getInstance().testingOff();
//        ShiftManager.getInstance().testingOff();
//    }
//
//    @Test
//    public void testSave() {
//        try {
//            WorkerManagerTest.resetTable();
//            resetTable();
//        } catch (SQLException ex) {
//            fail("Unable to reset table");
//        }
//
//        Worker firstWorker = WorkerManager.getInstance().find(1);
//        Shift newShift = new Shift(firstWorker.getID());
//        newShift.save();
//
//    }
//
//    /**
//     * Test of destroy method, of class Shift.
//     */
//    @Test
//    public void testDestroy() {
//        System.out.println("destroy");
//        Shift instance = null;
//        boolean expResult = false;
//        boolean result = instance.destroy();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of equals method, of class Shift.
//     */
//    @Test
//    public void testEquals() {
//        System.out.println("equals");
//        Object obj = null;
//        Shift instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals(obj);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hashCode method, of class Shift.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        Shift instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getID method, of class Shift.
//     */
//    @Test
//    public void testGetID() {
//        System.out.println("getID");
//        Shift instance = null;
//        long expResult = 0L;
//        long result = instance.getID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getWorkerID method, of class Shift.
//     */
//    @Test
//    public void testGetWorkerID() {
//        System.out.println("getWorkerID");
//        Shift instance = null;
//        long expResult = 0L;
//        long result = instance.getWorkerID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStart method, of class Shift.
//     */
//    @Test
//    public void testGetStart() {
//        System.out.println("getStart");
//        Shift instance = null;
//        Calendar expResult = null;
//        Calendar result = instance.getStart();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setStart method, of class Shift.
//     */
//    @Test
//    public void testSetStart() {
//        System.out.println("setStart");
//        Calendar start = null;
//        Shift instance = null;
//        instance.setStart(start);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEnd method, of class Shift.
//     */
//    @Test
//    public void testGetEnd() {
//        System.out.println("getEnd");
//        Shift instance = null;
//        Calendar expResult = null;
//        Calendar result = instance.getEnd();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEnd method, of class Shift.
//     */
//    @Test
//    public void testSetEnd() {
//        System.out.println("setEnd");
//        Calendar end = null;
//        Shift instance = null;
//        instance.setEnd(end);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLastBreakStart method, of class Shift.
//     */
//    @Test
//    public void testGetLastBreakStart() {
//        System.out.println("getLastBreakStart");
//        Shift instance = null;
//        Calendar expResult = null;
//        Calendar result = instance.getLastBreakStart();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLastBreakStart method, of class Shift.
//     */
//    @Test
//    public void testSetLastBreakStart() {
//        System.out.println("setLastBreakStart");
//        Calendar lastBreakStart = null;
//        Shift instance = null;
//        instance.setLastBreakStart(lastBreakStart);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTotalBreakTime method, of class Shift.
//     */
//    @Test
//    public void testGetTotalBreakTime() {
//        System.out.println("getTotalBreakTime");
//        Shift instance = null;
//        long expResult = 0L;
//        long result = instance.getTotalBreakTime();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTotalBreakTime method, of class Shift.
//     */
//    @Test
//    public void testSetTotalBreakTime() {
//        System.out.println("setTotalBreakTime");
//        long totalBreakTime = 0L;
//        Shift instance = null;
//        instance.setTotalBreakTime(totalBreakTime);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addBreakTime method, of class Shift.
//     */
//    @Test
//    public void testAddBreakTime() {
//        System.out.println("addBreakTime");
//        long time = 0L;
//        Shift instance = null;
//        instance.addBreakTime(time);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public static void resetTable() throws SQLException {
//        Connection connection = ShiftManager.getInstance().getDataSource().getConnection();
//        Statement statement;
//
//        statement = connection.createStatement();
//        try {
//            statement.executeUpdate("DROP TABLE APP.shifts");
//        } catch (SQLException ex) {
//            if (!ex.getSQLState().equals("42Y55")) {
//                throw ex;
//            }
//        }
//        statement.close();
//
//        statement = connection.createStatement();
//        statement.executeUpdate("CREATE TABLE APP.shifts ("
//                             + "ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,7"
//                             + "WORKER_ID INTEGER not null,"
//                             + "SHIFT_START TIMESTAMP,"
//                             + "SHIFT_END TIMESTAMP,"
//                             + "LAST_BREAK TIMESTAMP,"
//                             + "TOTAL_BREAK_TIME TIMESTAMP");
//        statement.close();
//
//        for (int i = 0; i < WORKER_IDS.length; i++) {
//            PreparedStatement preparedStatement = connection.prepareStatement(PROPERTIES.getProperty("saveQuery"));
//            preparedStatement.setLong(1, WORKER_IDS[i]);
//
//            if (SHIFT_STARTS[i] != null) {
//                preparedStatement.setTimestamp(2, new Timestamp(SHIFT_STARTS[i].getTimeInMillis()));
//            } else {
//                preparedStatement.setNull(2, Types.TIMESTAMP);
//            }
//
//            if (SHIFT_ENDS[i] != null) {
//                preparedStatement.setTimestamp(3, new Timestamp(SHIFT_ENDS[i].getTimeInMillis()));
//            } else {
//                preparedStatement.setNull(3, Types.TIMESTAMP);
//            }
//
//            if (LAST_BREAK_STARTS[i] != null) {
//                preparedStatement.setTimestamp(4, new Timestamp(LAST_BREAK_STARTS[i].getTimeInMillis()));
//            } else {
//                preparedStatement.setNull(4, Types.TIMESTAMP);
//            }
//
//            preparedStatement.setLong(5, TOTAL_BREAK_TIMES[i]);
//            preparedStatement.executeUpdate();
//            preparedStatement.close();
//        }
//        connection.close();
//    }
//}