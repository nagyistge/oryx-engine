package org.jodaengine.process.definition;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jodaengine.eventmanagement.subscription.ProcessStartEvent;
import org.jodaengine.exception.IllegalStarteventException;
import org.jodaengine.process.activation.ProcessDefinitionDeActivationPattern;
import org.jodaengine.process.activation.pattern.NullProcessDefinitionActivationPattern;
import org.jodaengine.process.instantiation.StartInstantiationPattern;
import org.jodaengine.process.instantiation.pattern.StartNullInstantiationPattern;
import org.jodaengine.process.structure.Node;

/**
 * The Class ProcessDefinitionImpl. A process definition consists of a list of start nodes that, as we have a tree
 * structure of nodes, reference all the nodes of the definition transitively.
 */
public class BpmnProcessDefinition extends AbstractProcessDefinition {

    @JsonIgnore
    private Map<ProcessStartEvent, Node> startTriggers;

    /**
     * Instantiates a new {@link ProcessDefinition}. The name is the ID of the {@link ProcessDefinition}.
     * 
     * @param id
     *            - the internal of the {@link ProcessDefinition}
     * @param name
     *            - the name of the {@link ProcessDefinition}
     * @param description
     *            - the description of the {@link ProcessDefinition}
     * @param startNodes
     *            - the initial nodes that refer to the whole node-tree
     */
    public BpmnProcessDefinition(ProcessDefinitionID id, String name, String description, List<Node> startNodes) {

        this(id, name, description, startNodes, new StartNullInstantiationPattern(),
            new NullProcessDefinitionActivationPattern());
    }

//    /**
//     * Hidden constructor.
//     */
//    protected BpmnProcessDefinition() {
//
//    }

    /**
     * Instantiates a new process definition. A UUID is generated randomly.
     * 
     * @param id
     *            - the internal of the {@link ProcessDefinition}
     * @param name
     *            - the name of the {@link ProcessDefinition}
     * @param description
     *            - the description of the {@link ProcessDefinition}
     * @param startNodes
     *            - the initial nodes that refer to the whole node-tree
     */
    public BpmnProcessDefinition(ProcessDefinitionID id,
                                 String name,
                                 String description,
                                 List<Node> startNodes,
                                 StartInstantiationPattern startInstantiationPattern,
                                 ProcessDefinitionDeActivationPattern startActivationPattern) {

        super(id, name, description, startNodes, startInstantiationPattern, startActivationPattern);
    }
    
    /**
     * Hidden constructor for the deserialzation of rest-easy.
     */
    protected BpmnProcessDefinition() {
        super(null, null, null, null, null, null);
    }

    @Override
    public Map<ProcessStartEvent, Node> getStartTriggers() {

        return startTriggers;
    }

    @Override
    public void addStartTrigger(ProcessStartEvent event, Node node)
    throws IllegalStarteventException {

        if (startNodes.contains(node)) {
            startTriggers.put(event, node);
        } else {
            throw new IllegalStarteventException();
        }

    }
}
