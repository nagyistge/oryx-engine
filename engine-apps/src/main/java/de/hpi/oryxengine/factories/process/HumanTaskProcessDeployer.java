package de.hpi.oryxengine.factories.process;

import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.IdentityService;
import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.activity.impl.EndActivity;
import de.hpi.oryxengine.activity.impl.HumanTaskActivity;
import de.hpi.oryxengine.activity.impl.NullActivity;
import de.hpi.oryxengine.allocation.Task;
import de.hpi.oryxengine.factories.worklist.TaskFactory;
import de.hpi.oryxengine.loadgenerator.PseudoHumanJob;
import de.hpi.oryxengine.process.definition.NodeParameterBuilder;
import de.hpi.oryxengine.process.definition.NodeParameterBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessBuilderImpl;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.resource.AbstractParticipant;
import de.hpi.oryxengine.resource.AbstractResource;
import de.hpi.oryxengine.resource.IdentityBuilder;
import de.hpi.oryxengine.resource.Participant;
import de.hpi.oryxengine.resource.Role;

/**
 * A factory for creating ExampleProcessToken objects. These objects just have 2 add Number activities.
 */
public class HumanTaskProcessDeployer extends AbstractProcessDeployer {

    private static final String JANNIK = "Jannik";
    private static final String TOBI = "Tobi";
    private static final String LAZY = "lazy guy";
    private static final String ROLE = "DUMMIES";
    private static final String JOBGROUP = "dummy";
    private IdentityBuilder identityBuilder;

    private IdentityService identityService;

    public static final String PARTICIPANT_KEY = "Participant";

    /** an array with the waiting times of the different pseudo humans. */
    public static final int[] WAITING_TIME = {1000, 1000, 1000};

    private Scheduler scheduler;

    /** The node1. */
    private Node node1;

    /** The node2. */
    private Node node2;

    /** The node3. */
    private Node node3;

    /** The start node. */
    private Node startNode;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Role role;

    /**
     * Instantiates a new example process token factory.
     * 
     * @throws SchedulerException
     *             thrown if the scheduler can't work correctly
     */
    public HumanTaskProcessDeployer()
    throws SchedulerException {

        identityService = ServiceFactory.getIdentityService();
        builder = new ProcessBuilderImpl();
        identityBuilder = identityService.getIdentityBuilder();

    }
    
    public void initializeNodes() {
        initializeNodesWithRoleTasks();
    }

    /**
     * Initializes the nodes.
     */
    public void initializeNodesWithDirectAlloc() {

        NodeParameterBuilder nodeParamBuilder = new NodeParameterBuilderImpl();
        nodeParamBuilder.setActivityBlueprintFor(NullActivity.class);
        startNode = builder.createStartNode(nodeParamBuilder.buildNodeParameterAndClear());

        Object[] participants = identityService.getParticipants().toArray();
        // Create the task
        Task task = TaskFactory.createParticipantTask((AbstractResource<?>) participants[0]);

        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, task);
        node1 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        // Create the task
        task = TaskFactory.createParticipantTask((AbstractResource<?>) participants[1]);
        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, task);

        node2 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        // Create the task
        task = TaskFactory.createParticipantTask((AbstractResource<?>) participants[2]);
        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, task);

        node3 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        builder.createTransition(startNode, node1).createTransition(node1, node2).createTransition(node2, node3);

        nodeParamBuilder = new NodeParameterBuilderImpl();
        nodeParamBuilder.setActivityBlueprintFor(EndActivity.class);
        Node endNode = builder.createNode(nodeParamBuilder.buildNodeParameter());
        builder.createTransition(node3, endNode);

    }
    
    public void initializeNodesWithRoleTasks() {
        NodeParameterBuilder nodeParamBuilder = new NodeParameterBuilderImpl();
        nodeParamBuilder.setActivityBlueprintFor(NullActivity.class);
        startNode = builder.createStartNode(nodeParamBuilder.buildNodeParameterAndClear());

        // Create the task
        Task roleTask = TaskFactory.createRoleTask("Do stuff", "Do it cool", role);

        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, roleTask);
        node1 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, roleTask);

        node2 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        nodeParamBuilder.setActivityBlueprintFor(HumanTaskActivity.class).addConstructorParameter(Task.class, roleTask);

        node3 = builder.createNode(nodeParamBuilder.buildNodeParameterAndClear());

        builder.createTransition(startNode, node1).createTransition(node1, node2).createTransition(node2, node3);

        nodeParamBuilder = new NodeParameterBuilderImpl();
        nodeParamBuilder.setActivityBlueprintFor(EndActivity.class);
        Node endNode = builder.createNode(nodeParamBuilder.buildNodeParameter());
        builder.createTransition(node3, endNode);
    }

    /**
     * Creates our dummy participants with a common role. Those are the ones that will claim and complete activity
     * within a time interval that is determined within the schedule dummy participants method.
     */
    public void createAutomatedParticipants() {

        Participant jannik = (Participant) identityBuilder.createParticipant(JANNIK);
        Participant tobi = (Participant) identityBuilder.createParticipant(TOBI);
        Participant lazy = (Participant) identityBuilder.createParticipant(LAZY);
        role = (Role) identityBuilder.createRole(ROLE);
        identityBuilder.participantBelongsToRole(jannik.getID(), role.getID())
        .participantBelongsToRole(tobi.getID(), role.getID()).participantBelongsToRole(lazy.getID(), role.getID());

    }

    /**
     * Schedule the dummy participants with a given time. A quartz scheduler is used to schedule them.
     * 
     * @throws SchedulerException
     *             the scheduler exception
     */
    public void scheduleDummyParticipants()
    throws SchedulerException {

        // Create the quartz scheduler
        final SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory();
        this.scheduler = factory.getScheduler();
        this.scheduler.start();

        // Schedule the jobs of our participants
        Set<AbstractParticipant> participants = ServiceFactory.getIdentityService().getParticipants();
        int i = 0;
        for (AbstractParticipant participant : participants) {

            JobDetail jobDetail = new JobDetail(participant.getName(), JOBGROUP, PseudoHumanJob.class);
            JobDataMap data = jobDetail.getJobDataMap();
            data.put(PARTICIPANT_KEY, participant);

            Trigger trigger = new SimpleTrigger(participant.getID().toString(), -1, WAITING_TIME[i++]);

            try {
                this.scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException se) {
                logger.error("Failed scheduling of event manager job", se);
            }

        }

    }

    @Override
    public void stop() {

        try {
            this.scheduler.shutdown();
        } catch (SchedulerException e) {
            logger.error("Error when shutting down the scheduler of the Human process", e);
        }
    }

    /**
     * Really creates Pseudo Humans. @see scheduleDummyParticipants {@inheritDoc}
     */
    @Override
    public void createPseudoHuman() {

        createAutomatedParticipants();
        try {
            scheduleDummyParticipants();
        } catch (SchedulerException e) {
            logger.error("Scheduler Exception when trying to schedule dummy Participants", e);
        }
    }

}
