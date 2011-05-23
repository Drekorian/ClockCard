package cz.muni.fi.pv168.clockcard;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

public class WorkerManagerTest {
    private static final String DATABASE_PROPERTY_FILE = "src/Database.properties";
    private static final String CLASS_PROPERTY_FILE = "src/Worker.properties";

    public static final String[] NAMES = {"Radek", "Jirina", "Vit", "Dita", "Petr", "Zdenek", "Ludmila", "Alena", "Barbora", "Vladan" };
    public static final String[] SURNAMES = {"Houha", "Flekova", "Pech", "Knourkova", "Pour", "Polacek", "Urvalkova", "Kahankova", "Boryskova", "Ferus" };
    public static final String[] LOGINS = {"rhouha", "jflekova", "vpech", "dknourkova", "ppour", "zpolacek", "lurvalkova", "akahankova", "bboryskova", "vferus" };
    public static final String[] PASSWORDS = {"wolverine", "kopretina", "saddam", "milacek", "sylva", "obelix", "kotatko", "inuyasha", "kreslo", "vladivostok" };
    public static final Long[] CURRENT_SHIFTS = {null, null, Long.valueOf(123), null, null, Long.valueOf(456), null, Long.valueOf(789), null, null };
    public static final boolean[] SUSPENSIONS = {false, false, false, false, false, false, false, true, false, true };

    public WorkerManagerTest() {
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
    }

    @After
    public void tearDown() {
        WorkerManager.getInstance().testingOff();
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
            assertEquals(worker, WorkerManager.getInstance().find(i + 1));
        }
    }

    @Test
    public void testGetAll() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table");
        }
        List<Worker> originalWorkers = new ArrayList<Worker>();

        for(int i = 0; i < LOGINS.length; i++) {
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

        List<Worker> dbWorkers = new ArrayList<Worker>();
        dbWorkers.addAll(WorkerManager.getInstance().getAll());
        assertTrue(dbWorkers.equals(originalWorkers));
    }

    @Test
    public void testCount() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        long count = 0;
        count = WorkerManager.getInstance().count();
        assertEquals("Amount of workers do not match.", LOGINS.length, count);
    }

    @Test
    public void testTestingOn() {
        assertTrue("Testing mode should be on", WorkerManager.getInstance().getTestingMode());
        DataSource testingDataSource = WorkerManager.getInstance().getDataSource();
        WorkerManager.getInstance().testingOn();
        assertTrue("Testing mode should be on", WorkerManager.getInstance().getTestingMode());
        assertSame("DataSource should have stayed the same.", testingDataSource, WorkerManager.getInstance().getDataSource());
        WorkerManager.getInstance().testingOff();
        assertFalse("Testing mode should be off", WorkerManager.getInstance().getTestingMode());
        DataSource productionDataSource = WorkerManager.getInstance().getDataSource();
        assertNotSame("DataSource should have changed from testing to production.", productionDataSource, testingDataSource);
        WorkerManager.getInstance().testingOn();
        assertTrue("Testing mode should be on", WorkerManager.getInstance().getTestingMode());
        assertNotSame("DataSource should have changed from production to testing.", productionDataSource, testingDataSource);
    }

    @Test
    public void testTestingOff() {
        assertTrue("Testing mode should be on.", WorkerManager.getInstance().getTestingMode());
        DataSource testingDataSource = WorkerManager.getInstance().getDataSource();
        WorkerManager.getInstance().testingOff();
        assertFalse("Testing mode should be off.", WorkerManager.getInstance().getTestingMode());
        assertNotSame("DataSource should have changed from testing to production.", testingDataSource, WorkerManager.getInstance().getDataSource());
        DataSource productionDataSource = WorkerManager.getInstance().getDataSource();
        WorkerManager.getInstance().testingOff();
        assertFalse("Testing mode should be off.", WorkerManager.getInstance().getTestingMode());
        assertSame("DataSource should have stayed the same.", productionDataSource, WorkerManager.getInstance().getDataSource());
    }

    @Test
    public void testLoadProperties() {
        Properties databaseProperties = WorkerManager.getInstance().loadProperties(DATABASE_PROPERTY_FILE);
        assertNotNull("Database properties should have been loaded.", databaseProperties);
        Properties classProperties = WorkerManager.getInstance().loadProperties(CLASS_PROPERTY_FILE);
        assertNotNull("Class properties should have been loaded.", classProperties);
    }

    public static void resetTable() throws SQLException {
        Connection connection = WorkerManager.getInstance().getDataSource().getConnection();
        Statement statement;

        statement = connection.createStatement();
        try {
            statement.executeUpdate("DROP TABLE APP.workers");
        } catch (SQLException ex) {
            if (!ex.getSQLState().equals("42Y55")) {
                throw ex;
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

        for (int i = 0; i < NAMES.length; i++) {
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
    public void testFindByLogin() {
        try {
            resetTable();
        } catch (SQLException ex) {
            fail("Unable to reset table.");
        }

        for (int i = 0; i < LOGINS.length; i++) {
            Worker originalWorker = new Worker(NAMES[i], SURNAMES[i], LOGINS[i]);
            Worker dbWorker = WorkerManager.getInstance().findByLogin(LOGINS[i]);
            assertEquals(originalWorker, dbWorker);
        }
    }
}
