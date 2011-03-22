package de.hpi.oryxengine.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.activity.impl.AddNumbersAndStoreActivity;
import de.hpi.oryxengine.activity.impl.PrintingVariableActivity;
import de.hpi.oryxengine.correlation.adapter.AdapterTypes;
import de.hpi.oryxengine.correlation.adapter.mail.MailAdapterConfiguration;
import de.hpi.oryxengine.correlation.adapter.mail.MailAdapterEvent;
import de.hpi.oryxengine.correlation.registration.EventCondition;
import de.hpi.oryxengine.correlation.registration.EventConditionImpl;
import de.hpi.oryxengine.correlation.registration.StartEvent;
import de.hpi.oryxengine.correlation.registration.StartEventImpl;
import de.hpi.oryxengine.deploy.Deployer;
import de.hpi.oryxengine.navigator.NavigatorImpl;
import de.hpi.oryxengine.plugin.navigator.NavigatorListenerLogger;
import de.hpi.oryxengine.process.definition.ProcessBuilder;
import de.hpi.oryxengine.process.definition.ProcessBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.definition.StartNodeParameter;
import de.hpi.oryxengine.process.definition.StartNodeParameterImpl;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.routing.behaviour.incoming.impl.SimpleJoinBehaviour;
import de.hpi.oryxengine.routing.behaviour.outgoing.impl.TakeAllSplitBehaviour;

/**
 * The Class ExampleMailStartProcess. This is an example process that is started by an arriving email.
 */
public class ExampleMailStartProcess {

    /**
     * The main method. Run the sh*t.
     *
     * @param args the arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args)
    throws InterruptedException {

        UUID processID = UUID.randomUUID();
        
        // the main
        NavigatorImpl navigator = new NavigatorImpl();
        navigator.registerPlugin(NavigatorListenerLogger.getInstance());

        Deployer deployer = ServiceFactory.getDeplyomentService();

        ProcessBuilder builder = new ProcessBuilderImpl();
        StartNodeParameter param = new StartNodeParameterImpl();
        param.setActivity(new AddNumbersAndStoreActivity("result", 1, 1));
        param.setIncomingBehaviour(new SimpleJoinBehaviour());
        param.setOutgoingBehaviour(new TakeAllSplitBehaviour());

        // Create a mail adapater event here. Could create a builder for this later.
        MailAdapterConfiguration config = MailAdapterConfiguration.dalmatinaGoogleConfiguration();
        EventCondition subjectCondition = null;
        try {
            subjectCondition = new EventConditionImpl(MailAdapterEvent.class.getMethod("getMessageTopic"),
                "Hallo");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        List<EventCondition> conditions = new ArrayList<EventCondition>();
        conditions.add(subjectCondition);
        
        StartEvent event = new StartEventImpl(AdapterTypes.Mail, config, conditions, processID);
        param.setStartEvent(event);

        Node node1 = builder.createStartNode(param);
        
        param.setActivity(new PrintingVariableActivity("result"));

        Node node2 = builder.createNode(param);
        
        builder.createTransition(node1, node2).setDescription("description").setID(processID);

        ProcessDefinition def = builder.buildDefinition();
        deployer.deploy(def, navigator);

        navigator.start();

        // Thread.sleep(SLEEP_TIME);

        // navigator.stop();
    }

}