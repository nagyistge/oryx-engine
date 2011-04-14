package de.hpi.oryxengine.repository.importer;

import javax.annotation.Nonnull;

import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.repository.ProcessDefinitionImporter;

/**
 * The {@link RawProcessDefintionImporter} is capable of deploying a raw {@link ProcessDefinition}. The
 * {@link ProcessDefinition} object is not created in this class.
 */
public class RawProcessDefintionImporter implements ProcessDefinitionImporter {

    private ProcessDefinition processDefinition;

    /**
     * Instantiates the {@link RawProcessDefintionImporter}.
     * 
     * @param processDefinition
     *            - the {@link ProcessDefinition} that needs to be imported
     */
    public RawProcessDefintionImporter(@Nonnull ProcessDefinition processDefinition) {

        this.processDefinition = processDefinition;
    }

    @Override
    public ProcessDefinition createProcessDefinition() {

        return processDefinition;
    }
}