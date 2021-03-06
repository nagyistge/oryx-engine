package org.jodaengine.example;

import org.jodaengine.JodaEngineServices;
import org.jodaengine.bootstrap.JodaEngine;
import org.jodaengine.deployment.DeploymentBuilder;
import org.jodaengine.exception.DefinitionNotActivatedException;
import org.jodaengine.exception.DefinitionNotFoundException;
import org.jodaengine.exception.IllegalStarteventException;
import org.jodaengine.monitor.Monitor;
import org.jodaengine.monitor.MonitorGUI;
import org.jodaengine.navigator.NavigatorImpl;
import org.jodaengine.navigator.schedule.FIFOScheduler;
import org.jodaengine.node.factory.ControlFlowFactory;
import org.jodaengine.node.factory.bpmn.BpmnCustomNodeFactory;
import org.jodaengine.node.factory.bpmn.BpmnNodeFactory;
import org.jodaengine.node.factory.bpmn.BpmnProcessDefinitionModifier;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.definition.ProcessDefinitionID;
import org.jodaengine.process.definition.bpmn.BpmnProcessDefinitionBuilder;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.structure.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SimpleExampleProcess. It really is just a simple example process.
 */
public final class SimpleExampleProcess {

    /** Hidden constructor. */
    private SimpleExampleProcess() {

    }

    /**
     * The Constant INSTANCE_COUNT. Which determines the number of instances which will be run when the main is
     * executed.
     */
    private static final int INSTANCE_COUNT = 1000000;

    private static final int STOPPING_MARK_1 = 234000;
    private static final int STOPPING_MARK_2 = 100000;
    private static final int STOPPING_MARK_3 = 500000;
    private static final int STOPPING_MARK_4 = 800000;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExampleProcess.class);

    /**
     * The main method. It starts a a specified number of instances.
     * 
     * @param args
     *            the arguments
     * @throws IllegalStarteventException
     *             fails
     * @throws DefinitionNotFoundException
     *             fails
     */
    public static void main(String[] args)
    throws IllegalStarteventException, DefinitionNotFoundException, DefinitionNotActivatedException {

        MonitorGUI monitorGUI = MonitorGUI.start(INSTANCE_COUNT);

        Monitor monitor = new Monitor(monitorGUI);

        JodaEngineServices jodaEngineServices = JodaEngine.start();

        // Registering the plugin - kind of a hack
        NavigatorImpl navigator = (NavigatorImpl) jodaEngineServices.getNavigatorService();
        FIFOScheduler scheduler = (FIFOScheduler) navigator.getScheduler();
        scheduler.registerListener(monitor);

        ProcessDefinitionID sampleProcessUUID = deploySampleProcess(jodaEngineServices);
        
        // Activate the process
        jodaEngineServices.getRepositoryService().activateProcessDefinition(sampleProcessUUID);

        // let's generate some load :)
        LOGGER.info("Engine started");
        for (int i = 0; i < INSTANCE_COUNT; i++) {

            AbstractProcessInstance processInstance = jodaEngineServices.getNavigatorService().startProcessInstance(
                sampleProcessUUID);

            if (i == STOPPING_MARK_1 || i == STOPPING_MARK_2 || i == STOPPING_MARK_3 || i == STOPPING_MARK_4) {
                monitor.markSingleInstance(processInstance);
            }

            if (i % INSTANCE_COUNT == 0) {
                LOGGER.debug("Started {} Instances", i);
            }
        }
    }

    /**
     * Deploys the sample process.
     * 
     * @param jodaEngineServices
     *            the joda engine services
     * @return the process definition id
     * @throws IllegalStarteventException
     *             the illegal startevent exception
     */
    private static ProcessDefinitionID deploySampleProcess(JodaEngineServices jodaEngineServices)
    throws IllegalStarteventException {

        DeploymentBuilder deploymentBuilder = jodaEngineServices.getRepositoryService().getDeploymentBuilder();

        ProcessDefinition processDefinition = buildSampleProcessDefinition(BpmnProcessDefinitionBuilder.newBuilder());

        ProcessDefinitionID sampleProcessUUID = processDefinition.getID();
        deploymentBuilder.addProcessDefinition(processDefinition);

        jodaEngineServices.getRepositoryService().deployInNewScope(deploymentBuilder.buildDeployment());

        return sampleProcessUUID;

    }

    /**
     * Builds the {@link ProcessDefinition} for the sample process.
     * <p>
     * The sample process contains: {@link StartEvent} => {@link AutomatedDummyActivity AutomatedDummyActivityNode} =>
     * 
     * @param definitionBuilder
     *            the definition builder
     * @return the process definition
     * @throws IllegalStarteventException
     *             the illegal startevent exception {@link AutomatedDummyActivity AutomatedDummyActivityNode} =>
     *             {@link EndEvent} .
     *             </p>
     */
    private static ProcessDefinition buildSampleProcessDefinition(BpmnProcessDefinitionBuilder definitionBuilder)
    throws IllegalStarteventException {

        String sampleProcessName = "Sample process for load test";
        String sampleProcessDescription = "This process is passed on to the load monitor.";

        Node startNode, automatedDummyNode1, automatedDummyNode2, endNode;

        startNode = BpmnNodeFactory.createBpmnStartEventNode(definitionBuilder);

        automatedDummyNode1 = BpmnCustomNodeFactory.createBpmnPrintingNode(definitionBuilder,
            "AutomatedActivity 1 (Sample Process)");

        automatedDummyNode2 = BpmnCustomNodeFactory.createBpmnPrintingNode(definitionBuilder,
            "AutomatedActivity 2 (Sample Process)");

        endNode = BpmnNodeFactory.createBpmnEndEventNode(definitionBuilder);

        ControlFlowFactory.createControlFlowFromTo(definitionBuilder, startNode, automatedDummyNode1);
        ControlFlowFactory.createControlFlowFromTo(definitionBuilder, automatedDummyNode1, automatedDummyNode2);
        ControlFlowFactory.createControlFlowFromTo(definitionBuilder, automatedDummyNode2, endNode);

        definitionBuilder.setName(sampleProcessName).setDescription(sampleProcessDescription);
        BpmnProcessDefinitionModifier.decorateWithDefaultBpmnInstantiationPattern(definitionBuilder);

        return definitionBuilder.buildDefinition();
    }
}
