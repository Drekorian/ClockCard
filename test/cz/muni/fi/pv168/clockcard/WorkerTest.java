package cz.muni.fi.pv168.clockcard;

import java.util.List;
import java.util.Properties;
import java.sql.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing methods for Worker class.
 * 
 * @author Marek Osvald
 * @version 2011.0525
 */

public class WorkerTest {
    private static final String CLASS_PROPERTY_FILE = "src/Worker.properties";
    private static final Properties PROPERTIES = WorkerManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
    
    private Worker joe;
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
        WorkerManager.getInstance().testingOn();

        joe = new Worker("Joe", "Smith", "joe.smith");
        lastThrownException = null;
    }
    @After
    public void tearDown() {
        WorkerManager.getInstance().testingOff();

        joe = null;
        lastThrownException = null;
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
    public void testStartShift() {
        assertFalse("Joe should not be suspended.", joe.isSuspended());
        assertNull("Joe should not have an ID", joe.getID());
        try {
            joe.startShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;
        assertNull("Joe should not have a pending shift.", joe.getCurrentShift());

        try {
            WorkerManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }

        Worker firstWorker = WorkerManager.getInstance().find(1);
        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());
        assertFalse("First worker should not be suspended.", firstWorker.isSuspended());

        try {
            firstWorker.suspend();
        } catch (WorkerException ex) {
            fail("First worker should be able to get suspended.");
        }
        
        assertTrue("First worker should be suspended.", firstWorker.isSuspended());
        try {
            firstWorker.startShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());

        try {
            firstWorker.unsuspend();
        } catch (WorkerException ex) {
            fail("First worker should be able to get unsuspended.");
        }
        assertFalse("First worker should not be suspended.", firstWorker.isSuspended());

        try {
            firstWorker.startShift();
            assertNull("First worker should be able to start a shift.", null);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }

        assertNotNull("FirstWorker should have a pending shift.", firstWorker.getCurrentShift());
        assertNotNull("First worker's current shift should have a start date and time.", firstWorker.getCurrentShift().getStart());

        Shift firstShift = firstWorker.getCurrentShift();
        try {
            firstWorker.startShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException e) {
            lastThrownException = e;
        }
        lastThrownException = null;
        assertTrue("First worker's shift should not have changed.", firstShift == firstWorker.getCurrentShift());
    }
    @Test
    public void testEndShift() {
        assertNull("Joe should not have a pending shift", joe.getCurrentShift());
        try {
            joe.endShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;

        try {
            WorkerManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        Worker firstWorker = WorkerManager.getInstance().find(1);
        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());
        try {
            firstWorker.endShift();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;

        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());
        try {
            firstWorker.startShift();
            assertNull("Unexpected WorkerException thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        Shift firstShift = firstWorker.getCurrentShift();
        assertNotNull("First worker should have a pending shift.", firstShift);

        try {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                fail("Sleeping for a millisecond interrupted.");
            }

            firstWorker.endShift();
            assertNull("Unexpected WorkerException thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());
        assertNotNull("Ended shift should have data and time.", firstShift.getEnd());
    }
    @Test
    public void testStartBreak() {
        assertNull("Joe should be unsaved.", joe.getID());
        try {
            joe.startBreak();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;

        try {
            WorkerManagerTest.resetTable();
            ShiftManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        Worker firstWorker = WorkerManager.getInstance().find(1);
        assertFalse("First worker should not be suspended.", firstWorker.isSuspended());

        try {
            firstWorker.suspend();
        } catch (WorkerException ex) {
            fail("First worker should be able to get suspended.");
        }
        assertTrue("First worker should be suspended.", firstWorker.isSuspended());

        try {
            firstWorker.startBreak();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;

        try {
            firstWorker.unsuspend();
            assertNull("Unexpected WorkerException thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        assertFalse("First worker should not be suspended.", firstWorker.isSuspended());

        assertNull("First worker should not have a pending shift.", firstWorker.getCurrentShift());
        try {
            firstWorker.startBreak();
            assertNotNull("WorkerException should have been thrown.", lastThrownException);
        } catch (WorkerException ex) {
            lastThrownException = ex;
        }
        lastThrownException = null;
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
            WorkerManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }

        assertTrue("Saving of Joe Smith to the database failed.", joe.save());
        assertNotNull("Joe should have a new ID.", joe.getID());
        
        Worker firstWorker = WorkerManager.getInstance().find(1);
        assertFalse("First user should not have login 'bigboss'", firstWorker.getLogin().equals("bigboss"));
        firstWorker.setPassword("boss");
        assertTrue("Updating of the first worker failed.", firstWorker.save());
    }
    @Test
    public void testDestroy() {
        try {
            WorkerManagerTest.resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        assertFalse("Destroy should return false if the worker has not been saved yet.", joe.destroy());
        Worker firstWorker = WorkerManager.getInstance().find(1);
        assertTrue("Table should be in the default state.", WorkerManager.getInstance().count() == WorkerManagerTest.LOGINS.length);
        List<Worker> originalWorkers = new ArrayList<Worker>();
        for (int i = 0; i < WorkerManagerTest.LOGINS.length; i++) {
            originalWorkers.add(new Worker(WorkerManagerTest.NAMES[i], WorkerManagerTest.SURNAMES[i], WorkerManagerTest.LOGINS[i]));
        }
        assertEquals("Table should be in the default state.", originalWorkers, WorkerManager.getInstance().getAll());
        assertTrue("Deleting the first Worker from the database failed.", firstWorker.destroy());
        assertTrue("Database should contain one less record than it's default state.", WorkerManager.getInstance().count() == WorkerManagerTest.LOGINS.length - 1);
        assertNull("First worker should not be in the database.", WorkerManager.getInstance().find(1));
    }
}
