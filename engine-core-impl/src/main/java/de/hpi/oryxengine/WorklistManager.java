package de.hpi.oryxengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JSpinner.ListEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.allocation.Pattern;
import de.hpi.oryxengine.allocation.Task;
import de.hpi.oryxengine.allocation.TaskAllocation;
import de.hpi.oryxengine.allocation.TaskDistribution;
import de.hpi.oryxengine.exception.DalmatinaException;
import de.hpi.oryxengine.exception.DalmatinaRuntimeException;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.resource.Resource;
import de.hpi.oryxengine.resource.worklist.WorklistItem;

/**
 * The implementation of the WorklistManager.
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
    public void addWorklistItem(WorklistItem worklistItem, Resource<?> resourceToFillIn) {

        resourceToFillIn.getWorklist().addWorklistItem(worklistItem);
    }
    

    @Override
    public void addWorklistItem(WorklistItem worklistItem, Set<Resource<?>> resourcesToFillIn) {

        Resource<?>[] resourcesToFillInArray = (Resource<?>[]) resourcesToFillIn
        .toArray(new Resource<?>[resourcesToFillIn.size()]);

        for (int i = 0; i < resourcesToFillInArray.length; i++) {
            Resource<?> resourceToFillIn = resourcesToFillInArray[i];
            addWorklistItem(worklistItem, resourceToFillIn);
        }
    }

    @Override
    public void distribute(Task task, Token token) {

        Pattern pushPattern = task.getAllocationStrategies().getPushPattern();

        pushPattern.execute(task, token, this);
    }

    @Override
    public @Nullable
    WorklistItem getWorklistItem(@Nonnull Resource<?> resource, @Nonnull UUID worklistItemId) {

        for (final WorklistItem item : resource.getWorklist()) {
            if (worklistItemId.equals(item.getID())) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Map<Resource<?>, List<WorklistItem>> getWorklistItems(List<Resource<?>> resources) {

        Map<Resource<?>, List<WorklistItem>> result = new HashMap<Resource<?>, List<WorklistItem>>();

        for (Resource<?> r : resources) {
            result.put(r, getWorklistItems(r));
        }

        return result;
    }

    @Override
    public void claimWorklistItemBy(WorklistItem worklistItem, Resource<?> resource) {

        Set<Resource<?>> resourcesToNotify = new HashSet<Resource<?>>();
        resourcesToNotify.add(resource);
        resourcesToNotify.addAll(worklistItem.getAssignedResources());
        for (Resource<?> resourceToNotify : resourcesToNotify) {
            resourceToNotify.getWorklist().itemIsAllocatedBy(worklistItem, resource);
        }
    }

    @Override
    public void abortWorklistItemBy(WorklistItem worklistItem, Resource<?> resource) {

    }

    @Override
    public void completeWorklistItemBy(WorklistItem worklistItem, Resource<?> resource) {

        resource.getWorklist().itemIsCompleted(worklistItem);

        try {

            worklistItem.getCorrespondingToken().resume();

        } catch (DalmatinaException e) {

            // TODO Logger message
            throw new DalmatinaRuntimeException(e.getMessage());
        }
    }

    @Override
    public List<WorklistItem> getWorklistItems(@Nonnull Resource<?> resource) {

        return resource.getWorklist().getWorklistItems();
    }

    @Override
    public void beginWorklistItemBy(WorklistItem worklistItem, Resource<?> resource) {

        resource.getWorklist().itemIsStarted(worklistItem);
    }

    @Override
    public int size(List<Resource<?>> resources) {
        Map<Resource<?>, List<WorklistItem>> items = getWorklistItems(resources);
        return items.size();
    }
}
