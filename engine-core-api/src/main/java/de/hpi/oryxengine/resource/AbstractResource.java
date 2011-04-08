package de.hpi.oryxengine.resource;

import javax.xml.bind.annotation.XmlRootElement;

import de.hpi.oryxengine.resource.worklist.Worklist;
import de.hpi.oryxengine.util.Identifiable;

/**
 * Represents a resource that is part of the enterprise's organization structure.
 * 
 * It is the sup interface of all other organization elements.
 * 
 * @param <R>
 *            - extending Resource
 */
@XmlRootElement
public abstract class AbstractResource<R extends AbstractResource<?>> implements Identifiable {

    /**
     * Returns the type of the {@link AbstractResource}. The type is an element of Enumeration {@link ResourceType}.
     * 
     * @return the type of the {@link AbstractResource}, which is an Element of the Enumeration
     */
    public abstract ResourceType getType();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public abstract String getName();

    /**
     * Sets the name.
     * 
     * @param name
     *            - the name
     * @return the current Resource object
     */
   public abstract R setName(String name);

    /**
     * Gets the object that corresponds to the property id.
     * 
     * @param propertyId
     *            - the property id
     * @return the object corresponding to the property id
     */
   public abstract Object getProperty(String propertyId);

    /**
     * Stores a property that consists of a property id and the corresponding object.
     * 
     * @param propertyId
     *            - the property id
     * @param propertyValue
     *            - the object that is stored to the property id
     * @return the current Resource object
     */
   public abstract R setProperty(String propertyId, Object propertyValue);
    
    /**
     * Retriev
     * 
     * es the resource's worklist. 
     * 
     * @return the worklist of the resource
     */
   public abstract Worklist getWorklist();

}