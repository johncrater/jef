package jef.core.events;

import java.util.List;

import jef.core.geometry.LineSegment;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class DebugShape
{
	public Location location;
	public LinearVelocity linearVelocity;
	public LineSegment lineSegment;
	public Location [] locations;
	
	public double radius;
	public String foregroundRGBA;
	public String backgroundRGBA;
	public int lineWidth;
	
	public DebugShape()
	{	
	}
	
	public static DebugShape fillLocation(Location location, String backgroundRGBA)
	{
		DebugShape ret = new DebugShape();
		ret.location = location;
		ret.radius = .25;
		ret.backgroundRGBA = backgroundRGBA;
		return ret;
	}
	
	public static DebugShape drawCircle(Location location, String foregroundRGBA, double radius)
	{
		DebugShape ret = new DebugShape();
		ret.location = location;
		ret.foregroundRGBA = foregroundRGBA;
		ret.radius = radius;
		return ret;
	}
	
	public static DebugShape drawLinearVelocity(LinearVelocity linearVelocity, String foregroundRGBA)
	{
		DebugShape ret = new DebugShape();
		ret.linearVelocity = linearVelocity;
		ret.foregroundRGBA = foregroundRGBA;
		ret.radius = .25;
		ret.backgroundRGBA = foregroundRGBA;
		ret.lineWidth = 3;
		return ret;
	}
	
	public static DebugShape drawLineSegment(LineSegment lineSegment, String foregroundRGBA)
	{
		DebugShape ret = new DebugShape();
		ret.lineSegment = lineSegment;
		ret.foregroundRGBA = foregroundRGBA;
		ret.radius = 0;
		ret.backgroundRGBA = foregroundRGBA;
		ret.lineWidth = 3;
		return ret;
	}
	
	public static DebugShape drawPolygon(String foregroundRGBA, int lineWidth, Location...locations)
	{
		DebugShape ret = new DebugShape();
		ret.locations = locations;
		ret.foregroundRGBA = foregroundRGBA;
		ret.lineWidth = lineWidth;
		return ret;
	}

	
	public static DebugShape fillPolygon(String backgroundRGBA, Location...locations)
	{
		DebugShape ret = new DebugShape();
		ret.locations = locations;
		ret.backgroundRGBA = backgroundRGBA;
		return ret;
	}
}
