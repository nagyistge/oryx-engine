package org.jodaengine.eventmanagement.adapter;

import org.jodaengine.eventmanagement.AdapterEvent;
import org.jodaengine.eventmanagement.processevent.incoming.IncomingStartProcessEvent;
import org.jodaengine.eventmanagement.processevent.incoming.intermediate.IncomingIntermediateProcessEvent;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Testing basic functionality of the CorrelationEventAdapter.
 */
public class CorrelatingEventAdapterTest {

    private AbstractCorrelatingEventAdapter<?> eventAdapter;

    /**
     * Sets the up.
     */
    @BeforeMethod
    public void setUp() {

        this.eventAdapter = new DummyCorrelatingEventAdapter();
    }

    /**
     * Tests the registration of a start event.
     */
    @Test
    public void testRegisteringStartEvent() {

        IncomingStartProcessEvent startEvent = Mockito.mock(IncomingStartProcessEvent.class);
        
        // Registering the startEvent
        eventAdapter.subscribeToStartEvent(startEvent);

        Assert.assertEquals(eventAdapter.getProcessEvents().size(), 1);
        Assert.assertEquals(startEvent, eventAdapter.getProcessEvents().get(0));
    }

    /**
     * Test registering a start event and then correlating it.
     */
    @Test
    public void testRegisterAndCorrelateStartEvent() {

        IncomingStartProcessEvent startEvent = Mockito.mock(IncomingStartProcessEvent.class);
        Mockito.when(startEvent.evaluate(Mockito.any(AdapterEvent.class))).thenReturn(true);

        // At first register and then correlate
        eventAdapter.subscribeToStartEvent(startEvent);
        eventAdapter.correlateAdapterEvent(Mockito.mock(AbstractAdapterEvent.class));

        Mockito.verify(startEvent).trigger();
        Assert.assertEquals(eventAdapter.getUnCorrelatedAdapterEvents().size(), 1);
    }

    /**
     * Test registering an intermediate event.
     */
    @Test
    public void testRegisteringIntermediateEvent() {

        IncomingIntermediateProcessEvent intermediateEvent = Mockito.mock(IncomingIntermediateProcessEvent.class);
        
        // Registering the intermediateEvent
        eventAdapter.subscribeToIncomingIntermediateEvent(intermediateEvent);

        Assert.assertEquals(eventAdapter.getProcessEvents().size(), 1);
        Assert.assertEquals(intermediateEvent, eventAdapter.getProcessEvents().get(0));
    }

    /**
     * Test registering and correlating an intermediate event.
     */
    @Test
    public void testRegisterAndCorrelateIntermediateEvent() {

        IncomingIntermediateProcessEvent intermediateEvent = Mockito.mock(IncomingIntermediateProcessEvent.class);
        Mockito.when(intermediateEvent.evaluate(Mockito.any(AdapterEvent.class))).thenReturn(true);

        // At first register and then correlate
        eventAdapter.subscribeToIncomingIntermediateEvent(intermediateEvent);
        eventAdapter.correlateAdapterEvent(Mockito.mock(AbstractAdapterEvent.class));

        Mockito.verify(intermediateEvent).trigger();
        Assert.assertEquals(eventAdapter.getUnCorrelatedAdapterEvents().size(), 1);
    }
}
