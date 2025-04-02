package jef.formations;

import java.util.Collection;

import jef.geometry.Direction;
import jef.geometry.Location;

public interface IFormation
{
	public UnitType getUnitType();

	public Collection<FormationPosition> getFormationPositions();

	public Collection<String> positionNames();

	public FormationPosition getPosition(String name);

	public boolean isValid();

	public String getName();

	public Location getScrimmage();
	
	public Direction getDirection();
}