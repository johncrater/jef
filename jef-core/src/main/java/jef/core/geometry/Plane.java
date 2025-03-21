package jef.core.geometry;

import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.LinearVelocity;
import jef.core.Location;

public class Plane
{
	public static final Plane THE_FIELD = new Plane(new Location(),
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
