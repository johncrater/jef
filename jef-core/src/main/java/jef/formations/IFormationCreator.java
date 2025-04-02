package jef.formations;

import jef.geometry.Direction;
import jef.geometry.Location;

public interface IFormationCreator
{
	public String getName();
	public UnitType getUnitType();
	public IFormation createFormation(Location scrimmage, Direction direction);

}