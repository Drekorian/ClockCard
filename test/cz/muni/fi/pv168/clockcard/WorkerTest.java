package cz.muni.fi.pv168.clockcard;

import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marek Osvald
 */

public class WorkerTest {
    private static final Properties PROPERTIES = Worker.loadProperties();
    private static final String[] NAMES = {"Radek", "Jirina", "Vit", "Dita", "Petr", "Zdenek", "Ludmila", "Alena", "Barbora", "Vladan" };
    private static final String[] SURNAMES = {"Houha", "Flekova", "Pech", "Knourkova", "Pour", "Polacek", "Urvalkova", "Kahankova", "Boryskova", "Ferus" };
    private static final String[] LOGINS = {"rhouha", "jflekova", "vpech", "dknourkova", "ppour", "zpolacek", "lurvalkova", "akahankova", "bboryskova", "vferus" };
    private static final String[] PASSWORDS = {"wolverine", "kopretina", "saddam", "milacek", "sylva", "obelix", "kotatko", "inuyasha", "kreslo", "vladivostok" };
    private static final Long[] CURRENT_SHIFTS = {null, null, Long.valueOf(123), null, null, Long.valueOf(456), null, Long.valueOf(789), null, null };
    private static final boolean[] SUSPENSIONS = {false, false, false, false, false, false, false, true, false, true };
    
    private Worker joe, bill;
    private Exception lastThrownException = null;

    public WorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        joe = new Worker("Joe", "Smith", "joe.smith");
        bill = new Worker("Bill", "Newman", "bill.newman");
        lastThrownException = null;
    }

    @After
    public void tearDown() {
        joe = null;
        bill = null;
        lastThrownException = null;
    }

    
    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(PROPERTIES.getProperty("testDatabase"),
                                                     PROPERTIES.getProperty("testLogin"),
                                                     PROPERTIES.getProperty("testPassword"));
        } catch (SQLException e) {
            fail("Unable to establish connection.");
        }
        return connection;
    }
    private void resetTable() throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate("DROP TABLE APP.workers");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("42Y55")) {
                throw e;
            }
        }
        statement.close();

        statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE APP.workers(ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                              + "NAME VARCHAR(30),"
                              + "SURNAME VARCHAR(30),"
                              + "LOGIN VARCHAR(30) not null,"
                              + "PASSWORD VARCHAR(30) not null,"
                              + "CURRENT_SHIFT INTEGER,"
                              + "SUSPENDED SMALLINT)");
        statement.close();

        for (int i = 0; i < 10; i++) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO APP.workers (name, surname, login, password, current_shift, suspended) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, NAMES[i]);
            preparedStatement.setString(2, SURNAMES[i]);
            preparedStatement.setString(3, LOGINS[i]);
            preparedStatement.setString(4, PASSWORDS[i]);
            if (CURRENT_SHIFTS[i] != null) {
                preparedStatement.setLong(5, CURRENT_SHIFTS[i]);
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }
            preparedStatement.setBoolean(6, SUSPENSIONS[i]);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        connection.close();
    }

    @Test
    public void testResetForgottenPassword() {
        String defaultPassword = PROPERTIES.getProperty("defaultPassword");
        String newPassword = "SomeSecretPassword";

        assertFalse("Joe should not be suspended.", joe.isSuspended());

        if (newPassword.equals(defaultPassword)) {
            newPassword += ":)";
        }

        assertTrue("Joe's password should be default password from the property file.",
                   joe.authenticate(PROPERTIES.getProperty("defaultPassword")));
        joe.setPassword(newPassword);
        assertFalse("Joe's password should differ from the default password",
                    joe.authenticate(PROPERTIES.getProperty(defaultPassword)));
        assertTrue("Joe's password should be the new specified ", joe.authenticate(newPassword));
    }

    @Test
    public void testAll() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }

        List<Worker> originalWorkers = new ArrayList<Worker>();

        for(int i = 0; i < 10; i++) {
            Worker worker = new Worker(NAMES[i], SURNAMES[i], LOGINS[i]);
            worker.setPassword(PASSWORDS[i]);
            if (SUSPENSIONS[i]) {
                try {
                    worker.suspend();
                } catch (WorkerException ex) {
                    fail("Worker suspension failed");
                }
            }

            originalWorkers.add(worker);
        }

        ArrayList<Worker> dbWorkers = new ArrayList<Worker>();
        try {
            Worker.testingOn();
            dbWorkers.addAll(Worker.all());
            Worker.testingOff();
        } catch (SQLException ex) {
            fail("Workers retrieval caused an unexpected WorkerException.");
        }

        assertTrue(dbWorkers.equals(originalWorkers));
    }

    @Test
    public void testCount() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        int count = 0;
        try {
            Worker.testingOn();
            count = Worker.count();
            Worker.testingOff();
        } catch (SQLException ex) {
            fail("Unable to count workers.");
        }
        assertEquals("Amount of workers do not match.", LOGINS.length, count);
    }

    @Test
    public void testFind() {
        try {
            resetTable();
        } catch (SQLException e) {
            fail("Unable to reset table.");
        }

        for (int i = 0; i < LOGINS.length; i++) {
            Worker worker = new Worker(NAMES[i], SURNAMES[i], LOGINS[i]);
            worker.setPassword(PASSWORDS[i]);
            if (SUSPENSIONS[i]) {
                try {
                    worker.suspend();
                } catch (WorkerException ex) {
                    fail("Unable to suspend worker.");
                }
            }
            try {
                Worker.testingOn();
                assertEquals(worker, Worker.find(i+1));
                Worker.testingOff();
            } catch (SQLException e) {
                fail("Unable to retrieve worker.");
            }
        }
    }

    @Test
    public void testStartShift() {
        assertFalse("Joe should not be suspended.", joe.isSuspended());
        try {
            joe.startShift();
            assertNull("Unexpected WorkerException thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }

        Shift joeShift = joe.getCurrentShift();
        assertNotNull("Joe should have a pending shift.", joeShift);

        try {
            joe.startShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        lastThrownException = null;
        assertTrue("Joe's shift should not have changed.", joeShift == joe.getCurrentShift());


        assertNull("Bill should not have a pending shift.", bill.getCurrentShift());
        try {
            bill.suspend();
            assertNull("Unexpected WorkerException thrown.", lastThrownException);
            assertTrue("Bill should be suspended.", bill.isSuspended());
            bill.startShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        lastThrownException = null;

        assertNull("Bill should not have a pending shift.", bill.getCurrentShift());
    }

    @Test
    public void testEndShift() {
        assertNull("Joe should not have a pending shift.", joe.getCurrentShift());
        try {
            joe.startShift();
            assertNotNull("Joe should have a pending shift.", joe.getCurrentShift());
            joe.endShift();
            assertNull("Joe should not have a pending shift.", joe.getCurrentShift());
        } catch (WorkerException e) {
            fail("Joe should be able to start and end a shift.");
         }


        assertNull("Bill should not have a pending shift", bill.getCurrentShift());
        try {
            bill.endShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        lastThrownException = null;

        assertNull("Joe should not have a pending shift.", joe.getCurrentShift());
    }

    @Test
    public void testSuspend() {
        assertFalse("Joe should not be suspended.", joe.isSuspended());
        try {
            joe.suspend();
        } catch (WorkerException e) {
            fail("Joe should be able to get suspended.");
        }
        assertTrue("Joe should be suspended.", joe.isSuspended());

        try {
            joe.suspend();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        lastThrownException = null;

        assertTrue("Joe should be suspended", joe.isSuspended());
    }

    @Test
    public void testUnsuspend() {
        assertFalse("Joe should not be suspended.", joe.isSuspended());
        try {
            joe.suspend();
            joe.unsuspend();
        } catch (WorkerException e) {
            fail("Joe should be able to get suspended and then unsuspended.");
        }
        assertFalse("Joe should not be suspended.", joe.isSuspended());

        try {
            joe.unsuspend();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        assertFalse("Joe should not be suspended.", joe.isSuspended());
    }

    @Test
    public void testSave() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }

        Worker newWorker = new Worker("John", "Doe", "jdoe");
        Worker.testingOn();
        try {
            assertTrue("Saving John Doe to the database failed.", newWorker.save());
        } catch (SQLException ex) {
            fail("Unable to save John Doe to the database.");
        }
        
        Worker firstWorker = null;
        try {
            firstWorker = Worker.find(1);
        } catch (SQLException ex) {
            fail("Unable to retrieve first worker from the database.");
        }
        assertFalse("First user should not have login 'bigboss'", firstWorker.getLogin().equals("bigboss"));
        firstWorker.setPassword("boss");
        try {
            assertTrue("Updating of the first worker failed.", firstWorker.save());
        } catch (SQLException ex) {
            System.out.println(ex);
            fail("Unable to update first worker in the database.");
        }
        Worker.testingOff();
    }

    @Test
    public void testDestroy() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        Worker.testingOn();

        Worker newWorker = new Worker("John", "Doe", "jdoe");
        try {
            assertFalse("Destroy should return false if the worker has not been saved yet.", newWorker.destroy());
        } catch (SQLException ex) {
            fail("Failed to recognize that worker has not been saved yet.");
        }

        try {
            Worker firstWorker = Worker.find(1);
            assertTrue("Database should be in the default state.", Worker.count() == LOGINS.length);
            assertTrue("Deleting the first Worker from the database failed.", firstWorker.destroy());
            assertTrue("Database should contain one less record than it's default state.", Worker.count() == LOGINS.length - 1);
            assertNull("First worker should not be in the database.", Worker.find(1));
        }  catch (SQLException ex) {
            fail("Unable to retrieve the first worker in the database.");
        }

        Worker.testingOff();
    }
}
