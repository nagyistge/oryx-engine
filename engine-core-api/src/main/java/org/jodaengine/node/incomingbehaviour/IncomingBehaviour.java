package org.jodaengine.node.incomingbehaviour;

import java.util.Collection;

import org.jodaengine.process.structure.Node;
import org.jodaengine.process.token.Token;

/**
 * The Interface JoinBehaviour.
 */
public interface IncomingBehaviour {

    /**
     * Join.
     * 
     * @param token
     *            the token for which the join-algorithm is invoked.
     * @return the result of the joining. Usually this list contains one or zero Tokens (Example: And-Join).
     */
    Collection<Token> join(Token token);
    
    /**
     * Checks if the behaviour could perform a join.
     *
     * @param token the token that the join is triggered for.
     * @param node the node
     * @return true, if a join can be performed, e.g. for a BPMN AND-Join, all sibling-instances have to reached the
     * join node as well.
     */
    boolean joinable(Token token, Node node);
}
