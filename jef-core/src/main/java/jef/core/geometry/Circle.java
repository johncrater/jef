package jef.core.geometry;

import jef.core.Location;

public class Circle
{
	private Location loc;
	private double radius;

	public Circle(Location loc, double radius)
	{
		super();
		this.loc = loc;
		this.radius = radius;
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public double getRadius()
	{
		return this.radius;
	}

	public LineSegment intersects(Circle c)
	{
		double d = loc.distanceBetween(c.loc);
		if (d > radius + c.radius)
			return null;
		
		if (d < Math.abs(radius - c.radius))
			return null;
		
		if (d == 0 && radius == c.radius)
			return null;
		
		double a = ((radius * radius) - (c.radius * c.radius) + (d * d)) / (2 * d);
		double h = Math.sqrt((radius * radius) - (a * a));
		
		double x0 = loc.getX();
		double y0 = loc.getY();
		double x1 = c.loc.getX();
		double y1 = c.loc.getY();

		double x2 = x0 + a * (x1 - x0) / d;
		double y2 = y0 + a * (y1 - y0) / d;   
		
		double x3 = x2 + h * (y1 - y0) / d;
		double y3 = y2 - h * (x1 - x0) / d;
		double x4 = x2 - h * (y1 - y0) / d;
		double y4 = y2 + h * (x1 - x0) / d;
		
		Location l3 = new Location(x3, y3);
		Location l4 = new Location(x4, y4);
		
		if (l3.compareTo(l4) <= 0)
			return new LineSegment(l3, l4);
		else
			return new LineSegment(l4, l3);
	}
}
