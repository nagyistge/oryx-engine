package org.jodaengine.eventmanagement;

import java.util.HashSet;
import java.util.Set;

import org.jodaengine.eventmanagement.adapter.CorrelationAdapter;
import org.jodaengine.eventmanagement.adapter.InboundAdapter;
import org.jodaengine.eventmanagement.adapter.InboundPullAdapter;
import org.jodaengine.eventmanagement.adapter.error.ErrorAdapter;
import org.jodaengine.eventmanagement.adapter.error.ErrorAdapterConfiguration;
import org.jodaengine.eventmanagement.registration.ProcessIntermediateEvent;
import org.jodaengine.eventmanagement.registration.ProcessStartEvent;
import org.jodaengine.eventmanagement.timing.TimingManager;
import org.jodaengine.eventmanagement.timing.TimingManagerImpl;

public class EventManager implements EventRegistrar, AdapterRegistrar {

    private final CorrelationManager correlationManagerNullObject = new ErrorCorrelationService();
    
    private Set<CorrelationAdapter> eventAdapters;
    
    private TimingManagerImpl timingManager;
    
    private ErrorAdapter errorAdapter;
    
    public EventManager() {

        this.errorAdapter = new ErrorAdapter(new ErrorAdapterConfiguration());
        this.timingManager = new TimingManagerImpl(errorAdapter);
    }

    @Override
    public void registerStartEvent(ProcessStartEvent startEvent) {

        // Delegate the work of registering the adapter to the configuration
        CorrelationAdapter eventAdapter = startEvent.getAdapterConfiguration().registerAdapter(this, correlationManagerNullObject);
        
    }

    /**
     * Checks if the Event is already registers and adds the eventAdapter if necessary.
     * 
     * @param eventAdapter
     *            - the {@link CorrelationAdapter eventAdapter} that should be added.
     */
    private void addEventAdapterToEvent(CorrelationAdapter eventAdapter) {

        if (!getEventAdapters().contains(eventAdapter)) {
            getEventAdapters().add(eventAdapter);
        }
    }

    @Override
    public String registerIntermediateEvent(ProcessIntermediateEvent event) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InboundAdapter registerInboundAdapter(InboundAdapter inboundAdapter) {

        addEventAdapterToEvent(inboundAdapter);
        
        return inboundAdapter;
    }

    @Override
    public InboundPullAdapter registerInboundPullAdapter(InboundPullAdapter inboundPullAdapter) {

        timingManager.registerJobForInboundPullAdapter(inboundPullAdapter);
        addEventAdapterToEvent(inboundPullAdapter);
        
        return inboundPullAdapter;
    }

    @Override
    public TimingManager getTimer() {

        return timingManager;
    }

    /**
     * Retrieves the {@link CorrelationAdapter eventAdapters}
     * @return a set containing the currently registered {@link CorrelationAdapter eventAdapters}
     */
    public Set<CorrelationAdapter> getEventAdapters() {
    
        if (eventAdapters == null) {
            this.eventAdapters = new HashSet<CorrelationAdapter>();
        }
        return eventAdapters;
    }
}