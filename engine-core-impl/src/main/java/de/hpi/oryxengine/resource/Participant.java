package de.hpi.oryxengine.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.exception.DalmatinaRuntimeException;
import de.hpi.oryxengine.resource.worklist.AbstractWorklist;
import de.hpi.oryxengine.resource.worklist.ParticipantWorklist;
import de.hpi.oryxengine.resource.worklist.WorklistItem;
import de.hpi.oryxengine.resource.worklist.WorklistItemState;

/**
 * A Participants is a resource which a task can be assigned to.
 * 
 * For example, a clerk, but even a manager, etc.
 * 
 * @author Gerardo Navarro Suarez
 */
@XmlRootElement
public class Participant extends AbstractParticipant {

    @XmlTransient
    private Set<Position> myPositions;

    @XmlTransient
    private Set<AbstractCapability> myCapabilities;

    @XmlTransient
    private Set<Role> myRoles;
    
    /**
     * Hidden jaxb constructor.
     */
    protected Participant() { }
    
    /**
     * Instantiates a new {@link Participant}.
     * 
     * @param participantName
     *            the name of the {@link Participant}
     */
    public Participant(String participantName) {

        super(participantName);
    }

    @Override
    public Set<AbstractPosition> getMyPositionsImmutable() {

        Set<AbstractPosition> setToReturn = new HashSet<AbstractPosition>(getMyPositions());
        return Collections.unmodifiableSet(setToReturn);
    }

    /**
     * Returns a Set of all PositionImpl of this Participant.
     * 
     * @return a Set of all PositionImpl of this Participant
     */
    protected Set<Position> getMyPositions() {

        if (myPositions == null) {
            myPositions = new HashSet<Position>();
        }
        return myPositions;
    }

    @Override
    public Set<AbstractCapability> getMyCapabilities() {

        // TODO hier muss noch was gemacht werden

        if (myCapabilities == null) {
            myCapabilities = new HashSet<AbstractCapability>();
        }
        return myCapabilities;
    }

    @Override
    public Set<AbstractRole> getMyRolesImmutable() {

        Set<AbstractRole> setToReturn = new HashSet<AbstractRole>(getMyRoles());
        return Collections.unmodifiableSet(setToReturn);
    }

    /**
     * Returns a Set of all RoleImpl of this Participant.
     * 
     * @return a Set of all RoleImpl of this Participant
     */
    protected Set<Role> getMyRoles() {

        if (myRoles == null) {
            myRoles = new HashSet<Role>();
        }
        return myRoles;
    }

    @Override
    public AbstractWorklist getWorklist() {

        if (worklist == null) {
            worklist = new ParticipantWorklist(this);
        }
        return worklist;
    }

    /**
     * Translates a Participant into a corresponding ParticipantImpl object.
     * 
     * Furthermore some constrains are checked.
     * 
     * @param participantID
     *            - a Participant object
     * @return participantImpl - the casted Participant object
     */
    public static Participant asParticipantImpl(UUID participantID) {

        if (participantID == null) {
            throw new DalmatinaRuntimeException("The Participant parameter is null.");
        }

        Participant participantImpl = (Participant) ServiceFactory.getIdentityService().getParticipant(participantID);

        if (participantImpl == null) {
            throw new DalmatinaRuntimeException("There exists no Participant with the id " + participantID + ".");
        }
        return participantImpl;
    }

    @Override
    public List<WorklistItem> getWorklistItemsCurrentlyInWork() {
        List<WorklistItem> inWork = new ArrayList<WorklistItem>();
        if (this.worklist != null) {
            for (WorklistItem w : this.worklist) {
                if (w.getStatus() == WorklistItemState.EXECUTING) {
                    inWork.add(w);
                }
            }
        }
        return inWork;
    }
}
