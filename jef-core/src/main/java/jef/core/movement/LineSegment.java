package jef.core.movement;

import com.synerset.unitility.unitsystem.common.Angle;
import jef.core.Conversions;

public class LineSegment
{
	public final Location a;
	public final Location b;

	public LineSegment(final Location a, final Location b)
	{
		this.a = a;
		this.b = b;
	}

	public Location bisect()
	{
		return this.bisect(.5f);
	}

	public Location bisect(final float multiplier)
	{
		return new DefaultLocation(this.a.getX() + ((this.b.getX() - this.a.getX()) * multiplier),
				this.a.getY() + ((this.b.getY() - this.a.getY()) * multiplier));
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof final LineSegment ls))
			return false;

		if ((this.a.equals(ls.a) && this.b.equals(ls.b)) || (this.a.equals(ls.b) && this.b.equals(ls.a)))
			return true;

		return false;
	}

	public double getAngle()
	{
		return a.angleTo(b);
	}

	public LineSegment getPerpendicularSegment(final Location loc)
	{
		double a = this.getAngle();
		a -= Math.PI / 2;

		return new LineSegment(loc.add(new DefaultLinearVelocity(0, a, 500)), loc.add(new DefaultLinearVelocity(0, a, 500))).limitToPlayableArea();
	}

	public double getSlope()
	{
		return (this.a.getY() - this.b.getY()) / (this.a.getX() - this.b.getX());
	}

	public double getYIntercept()
	{
		return this.a.getY() - (this.getSlope() * this.a.getX());
	}

	public LineSegment limitToPlayableArea()
	{
		// the bounds of the line segment should be contained within the playing field
		// make the length long enough to easily exit the field of play
		final var northBoundary = calculateIntersection(this, Field.n.northEndZone);
		final var southBoundary = calculateIntersection(this, LineSegment.southEndZone);
		final var eastBoundary = calculateIntersection(this, LineSegment.eastSideline);
		final var westBoundary = calculateIntersection(this, LineSegment.westSideline);

		// two and only two of these should be non null
		var l1 = northBoundary;
		if (l1 == null)
		{
			l1 = southBoundary;
			if (l1 == null)
			{
				l1 = eastBoundary;
				if (l1 == null)
					l1 = westBoundary;
			}
		}

		var l2 = northBoundary;
		if ((l2 == null) || (l2 == l1))
		{
			l2 = southBoundary;
			if ((l2 == null) || (l2 == l1))
			{
				l2 = eastBoundary;
				if ((l2 == null) || (l2 == l1))
					l2 = westBoundary;
			}
		}

		if ((l1 == null) || (l2 == null))
			return null;

		return new LineSegment(l1, l2);
	}

	@Override
	public String toString()
	{
		return this.a.toString() + " - " + this.b.toString();
	}

	public Angle calculateDeviantion(Location location)
	{
		return Conversions.normalizeAngleInDegrees(this.a.getXYArcTangent(location).minus(a.getXYArcTangent(b)));
	}

	public boolean locationIsToTheLeft(Location location)
	{
		Angle a = calculateDeviantion(location);
		return a.getInDegrees() >= 0 && a.getInDegrees() <= 180;
	}

	public boolean locationIsToTheRight(Location location)
	{
		Angle a = calculateDeviantion(location);
		return a.getInDegrees() <= 0 && a.getInDegrees() >= -180;
	}
}
