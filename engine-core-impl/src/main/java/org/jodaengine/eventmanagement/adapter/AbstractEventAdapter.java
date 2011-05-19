package org.jodaengine.eventmanagement.adapter;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract super adapter defining helper functions and a general structure.
 * 
 * @author Jan Rehwaldt
 * @param <Configuration>
 *            the adapter's configuration
 */
public abstract class AbstractEventAdapter<Configuration extends AdapterConfiguration> implements
CorrelationAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Configuration configuration;

    /**
     * Default constructor.
     * 
     * @param configuration
     *            the adapter's configuration
     */
    public AbstractEventAdapter(@Nonnull Configuration configuration) {

        this.configuration = configuration;

        logger.info("Initializing {} with config: {}", getClass().getSimpleName(), this.configuration);
    }

    @Override
    public final EventType getAdapterType() {

        return this.configuration.getEventType();
    }

    @Override
    public final @Nonnull
    Configuration getConfiguration() {

        return this.configuration;
    }

//    /**
//     * Correlation method, which calls the underlying {@link CorrelationManager}.
//     * 
//     * @see CorrelationManager
//     * @see EventManager
//     * @param adapterEvent
//     *            the event that should be correlated
//     */
//    protected final void correlateAdapterEvent(@Nonnull AdapterEvent adapterEvent) {
//
//        correlate(adapterEvent);
//        logger.info("Correlating {} for {}", adapterEvent, getClass().getSimpleName());
//    }
}
