package org.jodaengine.rest.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jodaengine.JodaEngineServices;
import org.jodaengine.RepositoryService;
import org.jodaengine.deployment.Deployment;
import org.jodaengine.deployment.DeploymentBuilder;
import org.jodaengine.deployment.importer.archive.DarImporter;
import org.jodaengine.deployment.importer.definition.BpmnXmlImporter;
import org.jodaengine.deployment.importer.definition.bpmn.BpmnXmlParseListener;
import org.jodaengine.ext.service.ExtensionService;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.definition.ProcessDefinitionID;
import org.jodaengine.rest.forms.FileUploadForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API providing an interface for the {@link RepositoryService}. It can be used to deploy process definitions.
 */
@Path("/repository")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class RepositoryWebService {
    // TODO implements RepositoryService

    private static final String XML_START = "<?xml";
    private static final String XML_END = "</definitions>";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DeploymentBuilder deploymentBuilder;
    private RepositoryService repositoryService;
    private ExtensionService extensionService;

    /**
     * Instantiates a new deployer web service. Initializes the Deployment builder.
     * 
     * @param engineServices
     *            the engine services
     */
    public RepositoryWebService(JodaEngineServices engineServices) {

        this.repositoryService = engineServices.getRepositoryService();
        this.deploymentBuilder = this.repositoryService.getDeploymentBuilder();
        this.extensionService = engineServices.getExtensionService();
    }

    // return status codes
    /**
     * Gets the process definitions via REST API.
     * 
     * @return the process definitions
     */
    @Path("/process-definitions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProcessDefinition> getProcessDefinitions() {

        return this.repositoryService.getProcessDefinitions();
    }
    
    /**
     * Activates a process definition.
     */
    @Path("/process-definitions/{definitionId}/activate")
    @POST
    public void activateProcessDefinition(@PathParam("definitionId") String definitionID) {
        this.repositoryService.activateProcessDefinition(ProcessDefinitionID.fromString(definitionID));
    }

    /**
     * Deploy a definition from an uploaded xml.
     * 
     * @param file
     *            the xml representation
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Path("/process-definitions/deploy")
    @POST
    public void deployDefinitionFromXML(String file)
    throws IOException {

        //
        // get the xml process definition
        //
        int xmlStart = file.indexOf(XML_START);
        // it is the end so we need to add back the length of the found element
        int xmlEnd = file.indexOf(XML_END) + XML_END.length();
        String xmlContent = file.substring(xmlStart, xmlEnd);
        logger.debug("Parsing process definition: {}", xmlContent);

        //
        // get parse listener
        //
        List<BpmnXmlParseListener> listeners = this.extensionService.getExtensions(BpmnXmlParseListener.class);
        BpmnXmlParseListener[] listenersArray = listeners.toArray(new BpmnXmlParseListener[listeners.size()]);

        //
        // construct bpmn xml importer with all provided listeners
        //
        BpmnXmlImporter importer = new BpmnXmlImporter(xmlContent, listenersArray);

        //
        // deploys the process definition
        //
        ProcessDefinition definition = importer.createProcessDefinition();

        this.deploymentBuilder.addProcessDefinition(definition);
        this.repositoryService.deployInNewScope(this.deploymentBuilder.buildDeployment());
    }

    /**
     * Deploys an archive in .dar-format. It uses the {@link DarImporter}. See this class to check, what parts of an
     * archive are actually imported.
     * 
     * @param file
     *            the file
     * @throws IOException 
     */
    @Path("/deployments")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void deployDarFile(@MultipartForm FileUploadForm form) throws IOException {
        
        byte[] fileData = form.getFileData();
        // write the input data into a temporary file
        File tempFile = File.createTempFile(String.valueOf(Arrays.hashCode(fileData)), null);
        FileOutputStream output = new FileOutputStream(tempFile);
        output.write(fileData);
        output.close();
        DarImporter importer = this.repositoryService.getNewDarImporter();
        Deployment deployment = importer.importDarFile(tempFile);
        this.repositoryService.deployInNewScope(deployment);
    }
}
