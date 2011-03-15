package de.hpi.oryxengine.exception;

/**
 * The Class DefinitionNotFoundException.
 */
public class DefinitionNotFoundException extends OryxEngineException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EXCEPTION_MESSAGE = "Process definition "
        + "with given UUID not found in repository.";
 
    /**
     * Default Constructor.
     */
    public DefinitionNotFoundException() {

        super(DEFAULT_EXCEPTION_MESSAGE);
    }
}
