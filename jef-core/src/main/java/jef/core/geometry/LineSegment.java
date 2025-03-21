package jef.core.geometry;

import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.Field;
import jef.core.LinearVelocity;
import jef.core.Location;

public class LineSegment
{
	private Location loc1;
	private Location loc2;

	public LineSegment(Location loc1, Location loc2)
	{
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
		return new LinearVelocity(loc2.toVector().subtract(loc1.toVector()));
	}

	public Location getPoint(double ratio)
	{
		return new Location(loc1.toVector().add(Vector.fromCartesianCoordinates(loc2.getX() - loc1.getX(),
				loc2.getY() - loc1.getY(), loc2.getZ() - loc1.getZ()).multiply(ratio)));
	}

	/**
	 * @param point
	 * @param length
	 * @return a line segment of length "length", perpendicular to this line segment
	 *         and centered on point
	 */
	public LineSegment getPerpendicularLine(Location point, double length)
	{
		double angle = loc1.angleTo(loc2);
		Vector v = Vector.fromPolarCoordinates(angle, 0, length);

		Vector orthogonal = v.orthogonal2D();
		Location orthogonalEndPoint = new Location(orthogonal);
		Location vertex = new Location();

		// move relative to point
		orthogonalEndPoint = orthogonalEndPoint.add(point);
		vertex = vertex.add(point);

		// slide segment so point is center point of new segment
		LineSegment seg = new LineSegment(vertex, orthogonalEndPoint);
		Location bisection = seg.getPoint(.5);

		Location offset = orthogonalEndPoint.subtract(bisection);
		return new LineSegment(vertex.subtract(offset), bisection);
	}

	public LineSegment addLength(double length)
	{
		return new LineSegment(this.getLoc1(),
				this.getLoc2().add(new LinearVelocity(this.getLoc1(), this.getLoc2()).newFrom(null, null, length)));
	}

//	public Vector toQuadratic()
//	{
//		double a = 1.0; // You can choose any value for 'a' except 0
//		double b = 0;
//		double denom = (loc2.getX() - loc1.getX());
//		if (denom != 0)
//			b = (loc2.getY() - loc1.getY() - a * (loc2.getX() * loc2.getX() - loc1.getX() * loc1.getX()))
//					/ (loc2.getX() - loc1.getX());
//
//		double c = loc1.getY() - a * loc1.getX() * loc1.getX() - b * loc1.getX();
//
//		return Vector.fromCartesianCoordinates(a, b, c);
//	}
//
	public double getXYSlope()
	{
		return (this.loc2.getY() - this.loc1.getY()) / (this.loc2.getX() - this.loc1.getX());
	}

	public double getYIntercept()
	{
		return this.loc1.getY() - (this.getXYSlope() * this.loc1.getX());
	}

	public double getXIntercept()
	{
		if (Double.isInfinite(getXYSlope()))
			return loc1.getX();

		return -1 * getYIntercept() / getXYSlope();
	}

	public Vector toStandardForm()
	{
		return Vector.fromCartesianCoordinates(-getXYSlope(), 1, -getYIntercept());
	}

	public Location xyIntersection(LineSegment line)
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
		if ((getXYSlope() - line.getXYSlope()) == 0
				|| (Double.isInfinite(getXYSlope()) && Double.isInfinite(line.getXYSlope())))
			return null;

//		Vector l1 = toStandardForm();
//		Vector l2 = line.toStandardForm();
//		
//		double x = (l1.getY() * l2.getZ() - l2.getY() * l1.getZ()) / (l1.getX() * l2.getY() - l2.getX() * l1.getY());
//		double y = (l1.getZ() * l2.getX() - l2.getZ() * l1.getX()) / (l1.getX() * l2.getY() - l1.getY() * l2.getX());
		double x = Double.NaN;
		double y = Double.NaN;
		if (Double.isInfinite(getXYSlope()))
		{
			x = getXIntercept();
			y = line.getXYSlope() * x + line.getYIntercept();
		}
		else if (Double.isInfinite(line.getXYSlope()))
		{
			x = line.getXIntercept();
			y = getXYSlope() * x + getYIntercept();
		}
		else
		{
			x = (line.getYIntercept() - getYIntercept()) / (getXYSlope() - line.getXYSlope());
			y = line.getXYSlope() * x + line.getYIntercept();
		}

		Location intersection = new Location(x, y, 0);

		if (intersects(intersection) && line.intersects(intersection))
			return intersection;

		return null;

	}

	public boolean intersects(Location loc)
	{
		// project w onto v
		// ((w ⋅ v) / (||v||2)) * v

		Vector wProjectedOnV = project(loc.toVector());

		Vector q = wProjectedOnV.add(loc1.toVector());

		double distance = loc.distanceBetween(new Location(q));
		boolean ret = Location.EPSILON.eqZero(distance);
		if (ret == false)
			return false;

		Vector aq = q.subtract(loc1.toVector());
		Vector qb = loc2.toVector().subtract(q);

		double dotProduct = aq.dot(qb);
		return dotProduct >= 0;

//		Vector v = loc2.toVector().subtract(loc1.toVector());
//		Vector w = loc.toVector().subtract(loc1.toVector());
//		Vector crossProduct = v.crossProduct(w);
//		
//		// project w onto v
//		// ((w ⋅ v) / (||v||2)) * v
//
//		Vector projectionW = crossProduct.multiply(1 / Math.pow(v.getDistance(), 2));
//		return Location.EPSILON.eqZero(projectionW.getDistance() / v.getDistance());
	}

	public Vector project(Vector proj)
	{
		Vector v = loc2.toVector().subtract(loc1.toVector());
		Vector w = proj.subtract(loc1.toVector());
		double scalar = v.dot(w);
		return v.multiply(scalar / Math.pow(v.getDistance(), 2));
	}

	public Location intersects(Plane plane)
	{
		double denom = getDirection().toVector().dot(plane.getPlaneNormal());

		if (Math.abs(denom) < 1e-6)
			return null;

		Vector planeV = plane.getLoc().toVector();
		double t = planeV.subtract(loc1.toVector()).dot(plane.getPlaneNormal()) / denom;
		return loc1.add(getDirection().multiply(t));
	}

	public LineSegment restrictToBetweenEndZones(boolean includeEndZoneLine)
	{
		return restrictToBetweenEndZones(includeEndZoneLine, Location.EPSILON_VALUE);
	}
	/**
	 * @param includeEndZoneLine
	 * @param epsilon
	 * @return a line segment which overlaps the original line segment where the
	 *         starting and ending locations are in bounds and between the end zones
	 *         exclusive
	 */
	public LineSegment restrictToBetweenEndZones(boolean includeEndZoneLine, double epsilon)
	{
		Location l1 = null;

		Location l = this.loc1;
		if (l.isInBounds(epsilon))
			l1 = l;

		l = this.loc2;
		if (l.isInBounds(epsilon) && !l.isInEndZone(null))
		{
			if (l1 == null)
				l1 = l;
			else
				return new LineSegment(l1, l);
		}

		l = this.xyIntersection(Field.GOAL_LINE_EAST.move(includeEndZoneLine ? 0 : -epsilon, 0, 0));
		if (l != null && l.isInBounds(epsilon))
		{
			if (l1 == null)
				l1 = l;
			else
				return new LineSegment(l1, l);
		}

		l = this.xyIntersection(Field.GOAL_LINE_WEST.move(includeEndZoneLine ? 0 : epsilon, 0, 0));
		if (l != null && l.isInBounds(epsilon))
		{
			if (l1 == null)
				l1 = l;
			else
				return new LineSegment(l1, l);
		}

		l = this.xyIntersection(Field.SIDELINE_NORTH.move(0, -epsilon, 0));
		if (l != null && !l.isInEndZone(null))
		{
			if (l1 == null)
				l1 = l;
			else
				return new LineSegment(l1, l);
		}

		l = this.xyIntersection(Field.SIDELINE_SOUTH.move(0, epsilon, 0));
		if (l != null && !l.isInEndZone(null))
		{
			if (l1 == null)
				l1 = l;
			else
				return new LineSegment(l1, l);
		}

		return null;
	}

	/**
	 * @return a line segment overlapping this line segment but restricted to the in
	 *         bounds area between the end lines.
	 */
	public LineSegment restrictToInBounds(double epsilon)
	{
		LineSegment ret = this;
		
		Location l1 = null;

		Location l = this.loc1;
		if (l.isInBounds())
		{
			l1 = l;
			ret = new LineSegment(l1, getLoc2());
		}
		
		l = this.loc2;
		if (l.isInBounds())
		{
			if (l1 == null)
			{
				l1 = l;
				ret = new LineSegment(l1, getLoc2());
			}
			else
				return new LineSegment(l1, l);
		}

		l = ret.xyIntersection(Field.END_LINE_EAST.move(-epsilon, 0, 0));
		if (l != null && l.isInBounds())
		{
			if (l1 == null)
			{
				l1 = l;
				ret = new LineSegment(l1, getLoc2());
			}
			else
				return new LineSegment(l1, l);
		}

		l = ret.xyIntersection(Field.END_LINE_WEST.move(epsilon, 0, 0));
		if (l != null && l.isInBounds())
		{
			if (l1 == null)
			{
				l1 = l;
				ret = new LineSegment(l1, getLoc2());
			}
			else
				return new LineSegment(l1, l);
		}

		l = ret.xyIntersection(Field.EXTENDED_SIDELINE_NORTH.move(0, epsilon, 0));
		if (l != null && l.isInBounds())
		{
			if (l1 == null)
			{
				l1 = l;
				ret = new LineSegment(l1, getLoc2());
			}
			else
				return new LineSegment(l1, l);
		}

		l = ret.xyIntersection(Field.EXTENDED_SIDELINE_SOUTH.move(0, -epsilon, 0));
		if (l != null && l.isInBounds())
		{
			if (l1 == null)
			{
				l1 = l;
				ret = new LineSegment(l1, getLoc2());
			}
			else
				return new LineSegment(l1, l);
		}

		return null;
	}

	public LineSegment move(double x, double y, double z)
	{
		return new LineSegment(this.getLoc1().add(x, y, z), this.getLoc2().add(x, y, z));
	}

	@Override
	public String toString()
	{
		return this.loc1.toString() + " - " + loc2.toString();
	}

}
