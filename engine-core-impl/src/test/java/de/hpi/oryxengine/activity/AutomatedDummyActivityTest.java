package de.hpi.oryxengine.activity;

import static org.testng.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.hpi.oryxengine.activity.impl.AutomatedDummyActivity;
import de.hpi.oryxengine.process.instance.ProcessInstance;
import de.hpi.oryxengine.process.instance.ProcessInstanceImpl;
import de.hpi.oryxengine.process.structure.NodeImpl;

/**
 * The test for the automated dummy activity.
 */
public class AutomatedDummyActivityTest {

    /** A temporary print stream. */
    private PrintStream tmp;

    /** The byte array output stream. */
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    /** A dummy string. */
    private String s = "I'm dumb";

    /** The dummy activity. */
    private AutomatedDummyActivity a;

    /** The process instance. */
    private ProcessInstance processInstance;

    /**
     * Set up.
     * TODO: more JavaDoc description
     * @throws Exception
     *             the exception
     */
    @BeforeTest
    public void setUp()
    throws Exception {

        tmp = System.out;
        System.setOut(new PrintStream(out));
        a = new AutomatedDummyActivity(s);
        processInstance = new ProcessInstanceImpl(new NodeImpl(a));
    }

    /**
     * Test activity initialization.
     * The activity should not be null if it was instantiated correctly.
     */
    @Test
    public void testActivityInitialization() {

        assertNotNull(a, "It should not be null since it should be instantiated correctly");
    }

    // @Test
    // public void testStateAfterActivityInitalization(){
    // assertEquals("It should have the state Initialized", State.INIT,
    // a.getState());
    // }

    /**
     * Test execute output.
     * If the activity is executed it should print out the given String.
     */
    @Test
    public void testExecuteOutput() {

        a.execute(processInstance);
        assertTrue(out.toString().indexOf(s) != -1, "It should print out the given string when executed");
    }

    /**
     * Test state after execution.
     * After execution the activity should be in state TERMINTAED
     */
    @Test
    public void testStateAfterExecution() {

        a.execute(processInstance);
        assertEquals(a.getState(), ExecutionState.COMPLETED, "It should have the state Completed");
    }

    /**
     * Tear down.
     */
    @AfterTest
    public void tearDown() {

        System.setOut(tmp);
    }
}
