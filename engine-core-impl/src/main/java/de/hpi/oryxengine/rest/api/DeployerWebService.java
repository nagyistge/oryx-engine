package de.hpi.oryxengine.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.activity.impl.AddNumbersAndStoreActivity;
import de.hpi.oryxengine.activity.impl.EndActivity;
import de.hpi.oryxengine.deployment.DeploymentBuilder;
import de.hpi.oryxengine.deployment.importer.RawProcessDefintionImporter;
import de.hpi.oryxengine.exception.IllegalStarteventException;
import de.hpi.oryxengine.process.definition.NodeParameterBuilder;
import de.hpi.oryxengine.process.definition.NodeParameterBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilder;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.routing.behaviour.incoming.impl.SimpleJoinBehaviour;
import de.hpi.oryxengine.routing.behaviour.outgoing.impl.TakeAllSplitBehaviour;

/**
 * API servlet providing an interface for the deployer. It can be used to deploy process definitions.
 */
@Path("/deployer")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DeployerWebService {

    private DeploymentBuilder deploymentBuilder;

    /**
     * Instantiates a new deployer web service. Initializes the Deplyoment builder.
     */
    public DeployerWebService() {

        this.deploymentBuilder = ServiceFactory.getRepositoryService().getDeploymentBuilder();
    }

    /**
     * Deploys an instance, more customizable version to come.
     *
     * @return the string
     */
    @Path("/deploy")
    @GET
    @Produces("text/plain")
    public String deployInstance() {

        ProcessDefinitionBuilder builder = new ProcessBuilderImpl();
        NodeParameterBuilder nodeParamBuilder = new NodeParameterBuilderImpl(new SimpleJoinBehaviour(),
            new TakeAllSplitBehaviour());

        // param.setActivity(AddNumbersAndStoreActivity("result", 1, 1));
        int[] integers = {1, 1};
        nodeParamBuilder.setActivityBlueprintFor(AddNumbersAndStoreActivity.class)
        .addConstructorParameter(String.class, "result").addConstructorParameter(int[].class, integers);
        Node node1 = builder.createStartNode(nodeParamBuilder.buildNodeParameter());

        // param.setActivity(new AddNumbersAndStoreActivity("result", 2, 2));
        Node node2 = builder.createNode(nodeParamBuilder.buildNodeParameter());

        // param.setActivity(new AddNumbersAndStoreActivity("result", 3, 3));
        Node node3 = builder.createNode(nodeParamBuilder.buildNodeParameter());

        // param.setActivity(new AddNumbersAndStoreActivity("result", 4, 4));
        Node node4 = builder.createNode(nodeParamBuilder.buildNodeParameter());

        nodeParamBuilder = new NodeParameterBuilderImpl();
        nodeParamBuilder.setActivityBlueprintFor(EndActivity.class);
        Node endNode = builder.createNode(nodeParamBuilder.buildNodeParameter());

        builder.createTransition(node1, node2).createTransition(node2, node3).createTransition(node3, node4)
        .createTransition(node4, endNode);

        ProcessDefinition definition = null;
        try {
            definition = builder.buildDefinition();
            deploymentBuilder.deployProcessDefinition(new RawProcessDefintionImporter(definition));
        } catch (IllegalStarteventException e) {
            e.printStackTrace();
        }

        return definition.getID().toString();
    }

}
