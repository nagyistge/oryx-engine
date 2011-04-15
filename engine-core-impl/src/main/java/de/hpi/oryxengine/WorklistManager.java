package de.hpi.oryxengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.allocation.Pattern;
import de.hpi.oryxengine.allocation.Task;
import de.hpi.oryxengine.allocation.TaskAllocation;
import de.hpi.oryxengine.allocation.TaskDistribution;
import de.hpi.oryxengine.exception.DalmatinaException;
import de.hpi.oryxengine.exception.DalmatinaRuntimeException;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.resource.AbstractResource;
import de.hpi.oryxengine.resource.worklist.AbstractWorklistItem;

/**
 * The implementation of the WorklistManager. It manages the worklists of all resources in the system.
 */
public class WorklistManager implements WorklistService, TaskDistribution, TaskAllocation, Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void start() {

        logger.info("Starting the correlation manager");
    }

    @Override
    public void stop() {

        logger.info("Stopping the correlation manager");
    }

    @Override
    public void addWorklistItem(AbstractWorklistItem worklistItem, AbstractResource<?> resourceToFillIn) {

        // The worklistItem is added to the worklist of a certain resource
        resourceToFillIn.getWorklist().addWorklistItem(worklistItem);
    }
    

    @Override
    public void addWorklistItem(AbstractWorklistItem worklistItem, Set<AbstractResource<?>> resourcesToFillIn) {

        // Copying the set because it is modified during iteration. If it is not done there would be a
        // ConcurrentModificationException.
        AbstractResource<?>[] resourcesToFillInArray = (AbstractResource<?>[]) resourcesToFillIn
        .toArray(new AbstractResource<?>[resourcesToFillIn.size()]);

        for (int i = 0; i < resourcesToFillInArray.length; i++) {
            AbstractResource<?> resourceToFillIn = resourcesToFillInArray[i];
            addWorklistItem(worklistItem, resourceToFillIn);
        }
    }

    @Override
    public void distribute(Task task, Token token) {

        // Delegate the strategy of task distribution to the specific push pattern.
        Pattern pushPattern = task.getAllocationStrategies().getPushPattern();

        pushPattern.execute(task, token, this);
    }

    @Override
    public @Nullable AbstractWorklistItem getWorklistItem(@Nonnull AbstractResource<?> resource, @Nonnull UUID worklistItemId) {

        for (final AbstractWorklistItem item : resource.getWorklist()) {
            if (worklistItemId.equals(item.getID())) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Map<AbstractResource<?>, List<AbstractWorklistItem>> getWorklistItems(Set<? extends AbstractResource<?>> resources) {

        Map<AbstractResource<?>, List<AbstractWorklistItem>> result = new HashMap<AbstractResource<?>, List<AbstractWorklistItem>>();

        for (AbstractResource<?> r : resources) {
            result.put(r, getWorklistItems(r));
        }

        return result;
    }

    @Override
    public void claimWorklistItemBy(AbstractWorklistItem worklistItem, AbstractResource<?> resource) {

        // Defining which resources' worklists should be notified when the worklist item is claimed
        Set<AbstractResource<?>> resourcesToNotify = new HashSet<AbstractResource<?>>();
        resourcesToNotify.add(resource);
        resourcesToNotify.addAll(worklistItem.getAssignedResources());
        
        // Notifying the worklist of each resource. Each worklist implements another behavior 
        for (AbstractResource<?> resourceToNotify : resourcesToNotify) {
            resourceToNotify.getWorklist().itemIsAllocatedBy(worklistItem, resource);
        }
    }

    @Override
    public void abortWorklistItemBy(AbstractWorklistItem worklistItem, AbstractResource<?> resource) {

        // TODO Hier muss noch was gemacht werden.
    }

    @Override
    public void completeWorklistItemBy(AbstractWorklistItem worklistItem, AbstractResource<?> resource) {

        // Others resources' worklists don't need to be notifyed because there is only one resource that has this worklistItem
        resource.getWorklist().itemIsCompleted(worklistItem);

        // Resuming the token
        try {

            worklistItem.getCorrespondingToken().resume();

        } catch (DalmatinaException e) {

            logger.error("The token didn't resumed properly.", e);
            throw new DalmatinaRuntimeException(e.getMessage());
        }
    }

    @Override
    public List<AbstractWorklistItem> getWorklistItems(@Nonnull AbstractResource<?> resource) {

        return resource.getWorklist().getWorklistItems();
    }

    @Override
    public void beginWorklistItemBy(AbstractWorklistItem worklistItem, AbstractResource<?> resource) {

        resource.getWorklist().itemIsStarted(worklistItem);
    }

    @Override
    public int size(Set<? extends AbstractResource<?>> resources) {
        Map<AbstractResource<?>, List<AbstractWorklistItem>> items = getWorklistItems(resources);
        return items.size();
    }
}
