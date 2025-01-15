package jef.core.geometry;

import jef.core.Field;
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
		double b = 0;
		double denom = (loc2.getX() - loc1.getX());
		if (denom != 0)
			b = (loc2.getY() - loc1.getY() - a * (loc2.getX() * loc2.getX() - loc1.getX() * loc1.getX()))
					/ (loc2.getX() - loc1.getX());

		double c = loc1.getY() - a * loc1.getX() * loc1.getX() - b * loc1.getX();

		return Vector.fromCartesianCoordinates(a, b, c);
	}

	public double getSlope()
	{
		return (this.loc1.getY() - this.loc2.getY()) / (this.loc1.getX() - this.loc2.getX());
	}

	public double getYIntercept()
	{
		return this.loc1.getY() - (this.getSlope() * this.loc1.getX());
	}

	public double getXIntercept()
	{
		if (Double.isInfinite(getSlope()))
			return loc1.getX();
		
		return -1 * getYIntercept() / getSlope();
	}

	public Vector toStandardForm()
	{
		return Vector.fromCartesianCoordinates(-getSlope(), 1, -getYIntercept());
	}
	
	public Location xyIntersection(Line line)
	{
		if (loc1.equals(line.loc1))
			return loc1;
		
		if (loc1.equals(line.loc2))
			return loc1;
		
		if (loc2.equals(line.loc2))
			return loc2;
		
		if (loc2.equals(line.loc1))
			return loc2;
		
		// parallel?
		if ((getSlope() - line.getSlope()) == 0
				|| (Double.isInfinite(getSlope()) && Double.isInfinite(line.getSlope())))
			return null;

//		Vector l1 = toStandardForm();
//		Vector l2 = line.toStandardForm();
//		
//		double x = (l1.getY() * l2.getZ() - l2.getY() * l1.getZ()) / (l1.getX() * l2.getY() - l2.getX() * l1.getY());
//		double y = (l1.getZ() * l2.getX() - l2.getZ() * l1.getX()) / (l1.getX() * l2.getY() - l1.getY() * l2.getX());
		double x = Double.NaN;
		double y = Double.NaN;
		if (Double.isInfinite(getSlope()))
		{
			x = getXIntercept();
			y = line.getSlope() * x + line.getYIntercept();
		}
		else if (Double.isInfinite(line.getSlope()))
		{
			x = line.getXIntercept();
			y = getSlope() * x + getYIntercept();
		}
		else
		{
			x = (line.getYIntercept() - getYIntercept()) / (getSlope() - line.getSlope());
			y = getSlope() * x + getYIntercept();
		}

		return new DefaultLocation(x, y, 0);
	}

	public boolean intersects(Location loc)
	{
		if (loc.getX() > Math.max(loc1.getX(), loc2.getX()))
			return false;
		
		if (loc.getX() < Math.min(loc1.getX(), loc2.getX()))
			return false;

		if (loc.getY() > Math.max(loc1.getY(), loc2.getY()))
			return false;
		
		if (loc.getY() < Math.min(loc1.getY(), loc2.getY()))
			return false;

		if (loc.getZ() > Math.max(loc1.getZ(), loc2.getZ()))
			return false;
		
		if (loc.getZ() < Math.min(loc1.getZ(), loc2.getZ()))
			return false;

		return true;
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

	public Line restrictToPlayableArea()
	{
		Location l1 = null;

		Location l = this.xyIntersection(Field.EAST_END_ZONE);
		if (l != null && l.isInPlayableArea())
			l1 = l;

		l = this.xyIntersection(Field.WEST_END_ZONE);
		if (l != null && l.isInPlayableArea())
		{
			if (l1 == null)
				l1 = l;
			else
				return new Line(l1, l);
		}

		l = this.xyIntersection(Field.NORTH_SIDELINE);
		if (l != null && l.isInPlayableArea())
		{
			if (l1 == null)
				l1 = l;
			else
				return new Line(l1, l);
		}

		l = this.xyIntersection(Field.SOUTH_SIDELINE);
		if (l != null && l.isInPlayableArea())
		{
			if (l1 == null)
				l1 = l;
			else
				return new Line(l1, l);
		}

		return null;
	}

	@Override
	public String toString()
	{
		return this.loc1.toString() + " - " + loc2.toString();
	}

}
