package org.jodaengine.node.activity.bpmn;

import org.jodaengine.ServiceFactory;
import org.jodaengine.eventmanagement.EventSubscriptionManager;
import org.jodaengine.eventmanagement.adapter.manual.ManualTriggeringAdapter;
import org.jodaengine.eventmanagement.processevent.incoming.TriggeringBehaviour;
import org.jodaengine.eventmanagement.processevent.incoming.intermediate.IncomingIntermediateProcessEvent;
import org.jodaengine.eventmanagement.subscription.IncomingProcessEvent;
import org.jodaengine.eventmanagement.subscription.processevent.intermediate.ProcessIntermediateManualTriggeringEvent;
import org.jodaengine.node.activity.AbstractActivity;
import org.jodaengine.process.token.AbstractToken;
import org.jodaengine.process.token.Token;

/**
 * This {@link BpmnManualTriggeringIntermediateEventActivity} is an activity that registers a {@link IncomingProcessEvent} that
 * should be triggered manually.
 */
public class BpmnManualTriggeringIntermediateEventActivity extends AbstractActivity implements
BpmnEventBasedGatewayEvent {

    private String name;

    /**
     * Default Constructor.
     * 
     * @param name
     *            - the name of the {@link ManualTriggeringAdapter}
     */
    public BpmnManualTriggeringIntermediateEventActivity(String name) {

        this.name = name;
    }

    @Override
    protected void executeIntern(AbstractToken token) {

        EventSubscriptionManager eventManager = ServiceFactory.getCorrelationService();

        IncomingIntermediateProcessEvent processEvent = createProcessIntermediateEvent(token);

        eventManager.registerIncomingIntermediateEvent(processEvent);

        token.suspend();
    }

    
    //
    // ==== Interface that represents that this event can also be used by other nodes like event-based Gateway ====
    //
    @Override
    public IncomingIntermediateProcessEvent createProcessIntermediateEvent(Token token) {

        return new ProcessIntermediateManualTriggeringEvent(name, token);
    }

    @Override
    public IncomingIntermediateProcessEvent createProcessIntermediateEventForEventGroup(Token token, TriggeringBehaviour eventGroup) {

        return new ProcessIntermediateManualTriggeringEvent(name, token, eventGroup);
    }
}
