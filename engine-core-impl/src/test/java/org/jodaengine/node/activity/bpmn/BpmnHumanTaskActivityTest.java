package org.jodaengine.node.activity.bpmn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.jodaengine.IdentityServiceImpl;
import org.jodaengine.ServiceFactory;
import org.jodaengine.navigator.NavigatorImplMock;
import org.jodaengine.node.incomingbehaviour.SimpleJoinBehaviour;
import org.jodaengine.node.outgoingbehaviour.TakeAllSplitBehaviour;
import org.jodaengine.process.instance.ProcessInstanceImpl;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.structure.NodeImpl;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.TokenImpl;
import org.jodaengine.resource.AbstractParticipant;
import org.jodaengine.resource.AbstractResource;
import org.jodaengine.resource.IdentityBuilder;
import org.jodaengine.resource.allocation.pattern.AllocateSinglePattern;
import org.jodaengine.resource.allocation.pattern.ConcreteResourcePattern;
import org.jodaengine.resource.worklist.AbstractWorklistItem;
import org.jodaengine.util.mock.MockUtils;
import org.jodaengine.util.testing.AbstractJodaEngineTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The test for the {@link BpmnHumanTaskActivity}.
 */

public class BpmnHumanTaskActivityTest extends AbstractJodaEngineTest {

    private ConcreteResourcePattern pattern = null;
    private AbstractResource<?> resource = null;

    private BpmnHumanTaskActivityMock humanTask = null;

    private Token token = null;

    /**
     * Set up.
     * 
     * @throws Exception
     *             the exception
     */
    @BeforeMethod
    public void setUp()
    throws Exception {

        // Prepare the organization structure

        IdentityBuilder identityBuilder = new IdentityServiceImpl().getIdentityBuilder();
        AbstractParticipant participant = identityBuilder.createParticipant("jannik");
        participant.setName("Jannik Streek");

        resource = participant;

        // Define the task
        String subject = "Jannik, get Gerardo a cup of coffee!";
        String description = "You know what I mean.";

        pattern = new ConcreteResourcePattern(subject, description, null, participant);

        humanTask = new BpmnHumanTaskActivityMock(pattern, new AllocateSinglePattern());

        Node node = new NodeImpl(humanTask, new SimpleJoinBehaviour(), new TakeAllSplitBehaviour());
        token = new TokenImpl(node, new ProcessInstanceImpl(MockUtils.mockProcessDefinition()), new NavigatorImplMock());
    }

    /**
     * Tear down.
     */
    @AfterMethod
    public void tearDown() {

        // Reseting the Worklist Manager after the test case
        // ServiceFactoryForTesting.clearWorklistManager();
    }

    /**
     * Test activity initialization. The activity should not be null if it was instantiated correctly.
     */
    @Test
    public void testActivityInitialization() {

        assertNotNull(humanTask, "It should not be null since it should be instantiated correctly");
    }

    /**
     * Test that the humanTask pushes a task item into the Jannik's worklist.
     */
    @Test
    public void testJannikHasWorklistItem() {

        humanTask.execute(token);

        int worklistSize = ServiceFactory.getWorklistService().getWorklistItems(resource).size();
        String failureMessage = "Jannik should now have 1 item in his worklist, but there are " + worklistSize
            + " item(s) in the worklist.";
        assertTrue(worklistSize == 1, failureMessage);

        AbstractWorklistItem worklistItem = ServiceFactory.getWorklistService().getWorklistItems(resource).get(0);
        assertEquals(worklistItem.getSubject(), pattern.getItemSubject());
        assertEquals(worklistItem.getDescription(), pattern.getItemDescription());
    }

    /**
     * Test that the ids of the created worklist items are stored in the context during execution.
     */
    @Test
    public void testItemIdInContextStorage() {

        humanTask.execute(token);

        List<UUID> savedItemIDs = (List<UUID>) token.getInternalVariable(humanTask.getInternaIdentifier(token));

        assertNotNull(savedItemIDs, "The variable should be initialized");
        assertEquals(savedItemIDs.size(), 1, "There should be one saved item ID");

        UUID itemID = savedItemIDs.get(0);

        AbstractWorklistItem item = ServiceFactory.getWorklistService().getWorklistItems(resource).get(0);
        assertEquals(itemID, item.getID(), "The saved ID should be the ID of the created worklist item");
    }

    /**
     * Test that the list of item IDs is removed from the context after the activity has been resumed.
     * @throws InterruptedException 
     */
    @Test
    public void testItemIdRemovedFromContext() throws InterruptedException {

        humanTask.execute(token);
        AbstractWorklistItem item = ServiceFactory.getWorklistService().getWorklistItems(resource).get(0);
        ServiceFactory.getWorklistQueue().beginWorklistItemBy(item, resource);
        ServiceFactory.getWorklistQueue().completeWorklistItemBy(item, resource);

        List<UUID> savedItemIDs = (List<UUID>) token.getInternalVariable(humanTask.getInternaIdentifier(token));

        Assert.assertNull(savedItemIDs, "The variable should not exist any longer.");
    }
}
