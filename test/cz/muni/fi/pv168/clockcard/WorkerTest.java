package cz.muni.fi.pv168.clockcard;

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
    private Worker joe, bill;


    public WorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        joe = new Worker("Joe", "Smith", "joe.smith");
        bill = new Worker("Bill", "Newman", "bill.newman");
    }

    @After
    public void tearDown() {
        joe = null;
        bill = null;
    }

    /**
     * Test of resetForgottenPassword method, of class Worker.
     */
    @Test
    public void testResetForgottenPassword() {
        /* TODO: Load default password from the property file and check whether it differs from "SomeSecretPassword" */

        String newPassword = "SomeSecretPassWord";

        assertTrue("Joe's password should be default password from the property file.", joe.authenticate("")); //CHANGE for value from the property file!
        joe.setPassword(newPassword);
        assertFalse("Joe's password should differ from the default password", joe.authenticate("")); //CHANGE for value from the property file!
        assertTrue("Joe's password should be the new specified ", joe.authenticate(newPassword));
    }
}