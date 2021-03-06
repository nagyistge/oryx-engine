package org.jodaengine.resource;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.jodaengine.IdentityServiceImpl;
import org.jodaengine.exception.JodaEngineException;
import org.jodaengine.exception.ResourceNotAvailableException;

/**
 * Implementation of {@link IdentityBuilder} Interface.
 */
public class IdentityBuilderImpl implements IdentityBuilder {

    /** The identity service. */
    private IdentityServiceImpl identityService;

    // private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Default Constructor.
     * 
     * @param identityServiceImpl
     *            - the IdentityServiceImpl where to build the organization structure on
     */
    public IdentityBuilderImpl(@Nonnull IdentityServiceImpl identityServiceImpl) {

        identityService = identityServiceImpl;
    }

    // -------- Participant Builder Methods -----------

    @Override
    public AbstractParticipant createParticipant(String participantName) {

        Participant participant = new Participant(participantName);

        identityService.getParticipantImpls().put(participant.getID(), participant);

        return participant;
    }

    @Override
    public IdentityBuilder deleteParticipant(UUID participantId)
    throws ResourceNotAvailableException {

        Participant participantImpl = Participant.asParticipantImpl(participantId);

        for (Role roleImpl : participantImpl.getMyRoles()) {
            roleImpl.getParticipants().remove(participantImpl);
        }

        for (Position positionImpl : participantImpl.getMyPositions()) {
            positionImpl.setPositionHolder(null);
        }

        identityService.getParticipantImpls().remove(participantImpl.getID());
        return this;
    }

    @Override
    public IdentityBuilder participantOccupiesPosition(UUID participantId, UUID positionId)
    throws ResourceNotAvailableException {

        Position positionImpl = Position.asPositionImpl(positionId);
        Participant participantImpl = Participant.asParticipantImpl(participantId);

        Participant oldParticiant = (Participant) positionImpl.getPositionHolder();
        if (oldParticiant != null) {
            if (!oldParticiant.equals(participantImpl)) {
                oldParticiant.getMyPositions().remove(positionImpl);
            }
        }

        positionImpl.setPositionHolder(participantImpl);
        participantImpl.getMyPositions().add(positionImpl);

        return this;
    }

    @Override
    public IdentityBuilder participantDoesNotOccupyPosition(UUID participantId, UUID positionId)
    throws ResourceNotAvailableException {

        Position positionImpl = Position.asPositionImpl(positionId);
        Participant participantImpl = Participant.asParticipantImpl(participantId);

        positionImpl.belongstoOrganization(null);
        participantImpl.getMyPositions().remove(positionImpl);
        return this;
    }

    @Override
    public IdentityBuilder participantHasCapability(UUID participantId, AbstractCapability capability) {

        // TODO
        return null;
    }

    @Override
    public IdentityBuilder participantBelongsToRole(UUID participantId, UUID roleId)
    throws ResourceNotAvailableException {

        Role roleImpl = Role.asRoleImpl(roleId);
        Participant participantImpl = Participant.asParticipantImpl(participantId);

        roleImpl.getParticipants().add(participantImpl);
        participantImpl.getMyRoles().add(roleImpl);

        return this;
    }

    @Override
    public IdentityBuilder participantDoesNotBelongToRole(UUID participantId, UUID roleId)
    throws ResourceNotAvailableException {

        Role roleImpl = Role.asRoleImpl(roleId);
        Participant participantImpl = Participant.asParticipantImpl(participantId);

        roleImpl.getParticipants().remove(participantImpl);
        participantImpl.getMyRoles().remove(roleImpl);
        return this;
    }

    // -------- Capability Builder Methods ------------

    @Override
    public AbstractCapability createCapability(String capabilityId) {

        // TODO
        // hier könnte man das FlyWeight-Pattern verwenden ...
        // Capability capability = new CapabilityImpl(capabilityId);

        // return capability;
        return null; 
    }

    // -------- OrganizationUnit Builder Methods ------

    @Override
    public OrganizationUnit createOrganizationUnit(String organizationUnitName) {

        OrganizationUnit organizationUnitImpl = new OrganizationUnit(organizationUnitName);

        identityService.getOrganizationUnitImpls().put(organizationUnitImpl.getID(), organizationUnitImpl);

        return organizationUnitImpl;
    }

    @Override
    public IdentityBuilder deleteOrganizationUnit(UUID organizationUnitId)
    throws ResourceNotAvailableException {

        OrganizationUnit organizationUnitImpl = OrganizationUnit.asOrganizationUnitImpl(organizationUnitId);

        for (OrganizationUnit childOrganizationUnitImpl : organizationUnitImpl.getChildOrganisationUnits()) {
            childOrganizationUnitImpl.setSuperOrganizationUnit(null);
        }

        for (Position positionImpl : organizationUnitImpl.getPositions()) {
            positionImpl.belongstoOrganization(null);
        }

        identityService.getOrganizationUnitImpls().remove(organizationUnitImpl.getID());
        return this;
    }

    @Override
    public IdentityBuilder subOrganizationUnitOf(UUID subOrganizationUnitId, UUID superOrganizationUnitId)
    throws JodaEngineException {

        OrganizationUnit organizationUnitImpl = OrganizationUnit.asOrganizationUnitImpl(subOrganizationUnitId);
        OrganizationUnit superOrganizationUnitImpl = OrganizationUnit.asOrganizationUnitImpl(superOrganizationUnitId);

        if (organizationUnitImpl.equals(superOrganizationUnitImpl)) {
            throw new JodaEngineException("The OrganizationUnit cannot be the superior of yourself.");
        }

        organizationUnitImpl.setSuperOrganizationUnit(superOrganizationUnitImpl);

        superOrganizationUnitImpl.getChildOrganisationUnits().add(organizationUnitImpl);

        return this;
    }

    @Override
    public IdentityBuilder organizationUnitOffersPosition(UUID organizationUnitId, UUID positionId)
    throws ResourceNotAvailableException {

        Position positionImpl = Position.asPositionImpl(positionId);
        OrganizationUnit organizationUnitImpl = OrganizationUnit.asOrganizationUnitImpl(organizationUnitId);

        OrganizationUnit oldOrganizationUnit = (OrganizationUnit) positionImpl.belongstoOrganization();
        if (oldOrganizationUnit != null) {
            if (!oldOrganizationUnit.equals(organizationUnitImpl)) {
                oldOrganizationUnit.getPositions().remove(positionImpl);
            }
        }

        positionImpl.belongstoOrganization(organizationUnitImpl);
        organizationUnitImpl.getPositions().add(positionImpl);

        return this;
    }

    @Override
    public IdentityBuilder organizationUnitDoesNotOfferPosition(UUID organizationUnitId, UUID positionId)
    throws ResourceNotAvailableException {

        Position positionImpl = Position.asPositionImpl(positionId);
        OrganizationUnit organizationUnitImpl = OrganizationUnit.asOrganizationUnitImpl(organizationUnitId);

        positionImpl.belongstoOrganization(null);
        organizationUnitImpl.getPositions().remove(positionImpl);

        return this;
    }

    // -------- Position Builder Methods --------------

    @Override
    public Position createPosition(String positionName) {

        Position positionImpl = new Position(positionName);

        identityService.getPositionImpls().put(positionImpl.getID(), positionImpl);

        return positionImpl;
    }

    @Override
    public IdentityBuilder positionReportsToSuperior(UUID positionId, UUID superiorPositionId)
    throws JodaEngineException {

        Position positionImpl = Position.asPositionImpl(positionId);
        Position superiorPositionImpl = Position.asPositionImpl(superiorPositionId);

        if (positionImpl.equals(superiorPositionImpl)) {
            throw new JodaEngineException("The Position '" + positionImpl.getID()
                + "' cannot be the superior of yourself.");
        }

        positionImpl.setSuperiorPosition(superiorPositionImpl);

        superiorPositionImpl.getSubordinatePositions().add(positionImpl);

        return this;
    }

    @Override
    public IdentityBuilder deletePosition(UUID positionId)
    throws JodaEngineException {

        Position positionImpl = Position.asPositionImpl(positionId);

        identityService.getPositionImpls().remove(positionImpl.getID());

        for (Position subordinatePosition : positionImpl.getSubordinatePositions()) {
            subordinatePosition.setSuperiorPosition(null);
        }

        return this;
    }

    // -------- Role Builder Methods ------------------

    @Override
    public Role createRole(String roleName) {

        Role roleImpl = new Role(roleName);

        identityService.getRoleImpls().put(roleImpl.getID(), roleImpl);

        return roleImpl;
    }

    @Override
    public IdentityBuilder deleteRole(UUID roleId)
    throws JodaEngineException {

        Role roleImpl = Role.asRoleImpl(roleId);

        for (Participant participantImpl : roleImpl.getParticipants()) {
            participantImpl.getMyRoles().remove(roleImpl);
        }

        identityService.getRoleImpls().remove(roleImpl.getID());
        return this;
    }

    @Override
    public IdentityBuilder subRoleOf(UUID subRole, UUID superRole) {

        // TODO @Gerardo - bin mir nicht sicher ob wir überhaupt Oberrollen brauchen; sollte nochmal
        // diskutiert werden
        return null;
    }
}
