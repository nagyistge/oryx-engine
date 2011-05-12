package de.hpi.oryxengine.allocation;

import de.hpi.oryxengine.process.token.Token;

/**
 * Represents a pattern object for distribution of a task into worklists.
 */
public interface Pattern {

    /**
     * Executes the pattern and performs the logic of the distribution.
     * 
     * @param task
     *            - the {@link Task} to distribute
     * @param token
     *            - reference to the {@link Token} in order to have more context information
     * @param worklistService
     *            - reference to the {@link TaskAllocation} in order to operate on worklist queues
     */
    void execute(Token token, TaskAllocation worklistService);
}
