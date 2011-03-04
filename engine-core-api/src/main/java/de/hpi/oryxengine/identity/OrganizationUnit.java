package de.hpi.oryxengine.identity;

import java.util.List;

/**
 * An Organization Unit is a functional grouping of positions. Common examples might include departments like
 * Marketing, Sales, Human Resource, etc. .
 * 
 * @author Gery
 */
public interface OrganizationUnit extends Resource<OrganizationUnit> {

    OrganizationUnit getSuperOrganizationUnit();
    OrganizationUnit setSuperOrganizationUnit(OrganizationUnit organizationUnit);
    
    /**
     * 
     * @return read-only list
     */
    List<Position> getPositions();
//    OrganizationUnit addPosition(Position position);
}