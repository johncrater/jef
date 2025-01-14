package jef.core.geometry;

import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class Line
{
	private Location loc1;
	private Location loc2;

	public Line(Location loc1, Location loc2)
	{
		super();
		this.loc1 = loc1;
		this.loc2 = loc2;
	}

	public Location getLoc1()
	{
		return this.loc1;
	}

	public Location getLoc2()
	{
		return this.loc2;
	}

	public double getLength()
	{
		return loc1.distanceBetween(loc2);
	}

	public LinearVelocity getDirection()
	{
		return new DefaultLinearVelocity(loc2.toVector().subtract(loc1.toVector()));
	}

	public Location getPoint(double ratio)
	{
		return new DefaultLocation(loc1.toVector().add(Vector.fromCartesianCoordinates(loc2.getX() - loc1.getX(),
				loc2.getY() - loc1.getY(), loc2.getZ() - loc1.getZ()).multiply(ratio)));
	}
	
	public Line getPerpendicularLine(Location point)
	{
		double angle = loc1.angleTo(loc2);
		Vector v = Vector.fromPolarCoordinates(angle, 0, 1);
		return new Line(point, new DefaultLocation(v.orthogonal2D().add(point.toVector())));
	}
	
	public Vector toQuadratic()
	{
        double a = 1.0; // You can choose any value for 'a' except 0
        double b = (loc2.getY() - loc1.getY() - a * (loc2.getX() * loc2.getX() - loc1.getX() * loc1.getX())) / (loc2.getX() - loc1.getX());
        double c = loc1.getY() - a * loc1.getX() * loc1.getX() - b * loc1.getX();
        
        return Vector.fromCartesianCoordinates(a, b, c);
    }
	
	public Location intersects(Line line)
	{
		Vector thisQ = toQuadratic();
		Vector thatQ = line.toQuadratic();
		
        double det = thisQ.getX() * thatQ.getY() - thatQ.getX() * thisQ.getY();

        if (det == 0) 
            // Lines are parallel or coincident
            return null;
        
        double x = (thisQ.getY() * thatQ.getZ() - thatQ.getY() * thisQ.getZ()) / det;
        double y = (thatQ.getX() * thisQ.getZ() - thisQ.getX() * thatQ.getZ()) / det;
        return new DefaultLocation(x, y, 0);
	}

	public Location intersects(Plane plane)
	{
		double denom = getDirection().dotProduct(plane.getPlaneNormal());

		if (Math.abs(denom) < 1e-6)
			return null;

		Vector planeV = plane.getLoc().toVector();
		double t = planeV.subtract(loc1.toVector()).dot(plane.getPlaneNormal().toVector()) / denom;
		return loc1.add(getDirection().multiply(t));
	}
}
