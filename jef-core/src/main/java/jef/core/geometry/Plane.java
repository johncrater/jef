package jef.core.geometry;

import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class Plane
{
	public static final Plane THE_FIELD = new Plane(new DefaultLocation(),
			Vector.fromCartesianCoordinates(0, 0, 1));

	private Location loc;
	private Vector planeNormal;

	public Plane(Location loc, Vector planeNormal)
	{
		super();
		this.loc = loc;
		this.planeNormal = planeNormal;
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public Vector getPlaneNormal()
	{
		return this.planeNormal;
	}

}
