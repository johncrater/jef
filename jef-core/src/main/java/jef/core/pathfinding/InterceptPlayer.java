package jef.core.pathfinding;

import jef.core.Field;
import jef.core.Player;
import jef.core.geometry.Vector;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class InterceptPlayer implements Pathfinder
{
	private final Player player;
	private final Player target;
	private final Direction direction;

	public InterceptPlayer(final Player player, final Player target, final Direction direction)
	{
		this.player = player;
		this.target = target;
		this.direction = direction;
	}

	@Override
	public Path findPath()
	{
		if (target.getLV().isNotMoving())
			return new DefaultPath(new Waypoint(target.getLoc(), player.getMaxSpeed(), DestinationAction.noStop));

		Vector st = target.getLoc().toVector(); // target starting point
		Vector si = player.getLoc().toVector(); // interceptor starting point
		Vector vt = target.getLV().toVector(); // target linear velocity
		double vi = player.getMaxSpeed();  // interceptor max speed
//		double vi = player.getLV().getSpeed(); // interceptor max speed

		Location interceptionPoint = this.getInterceptionPoint(st, si, vt, vi);

		if (interceptionPoint.getY() >= Field.NORTH_SIDELINE_Y)
		{
			System.out.print("NORTH - ");
			interceptionPoint = this.getInterceptionPoint(st, si,
					Vector.fromPolarCoordinates(st.xyAngle(
							Vector.fromCartesianCoordinates(interceptionPoint.getX(), Field.NORTH_SIDELINE_Y, 0)),
							vt.getElevation(), vt.getDistance()),
					vi);
		}
		else if (interceptionPoint.getY() <= Field.SOUTH_SIDELINE_Y)
		{
			interceptionPoint = this.getInterceptionPoint(st, si,
					Vector.fromPolarCoordinates(st.xyAngle(
							Vector.fromCartesianCoordinates(interceptionPoint.getX(), Field.SOUTH_SIDELINE_Y, 0)),
							vt.getElevation(), vt.getDistance()),
					vi);
			System.out.print("SOUTH - ");
		}
		else
		{
			System.out.print("        ");
			System.out.print("      ");
		}
		
		if (interceptionPoint.getX() >= Field.EAST_END_ZONE_X)
			interceptionPoint = new DefaultLocation(Field.EAST_END_ZONE_X - 1, target.getLoc().getY(), 0);
		else if (interceptionPoint.getX() <= Field.WEST_END_ZONE_X)
			interceptionPoint = new DefaultLocation(Field.WEST_END_ZONE_X + 1, target.getLoc().getY(), 0);

		System.out.println(interceptionPoint + " - " + player.getLV());

		return new DefaultPath(new Waypoint(interceptionPoint, player.getMaxSpeed(), DestinationAction.noStop));
	}

	@Override
	public Path findPath(double maximumTimeToSpend)
	{
		return findPath();
	}

	private Location getInterceptionPoint(Vector st, Vector si, Vector vt, double vi)
	{
		// 0 = (vt^2 - vi^2) * t^2 + 2 * (st - si) * vt * t + (st - si)^2;

		double a = vt.dot(vt) - Math.pow(vi, 2);
		if (a == 0)
			return target.getLoc();

		double b = 2 * st.subtract(si).dot(vt);
		double c = st.subtract(si).dot(st.subtract(si));

		double tplus = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
		double tminus = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);

		// if there are no real roots, try this...
		if (Double.isNaN(tplus) || Double.isNaN(tminus))
		{
			double r = Math.sqrt(c / a);
			double theta = Math.acos(-b / (2 * Math.sqrt(a * c)));
			tplus = r * Math.cos(theta) + Math.sin(theta);
			tminus = r * Math.cos(theta) - Math.sin(theta);
			
			System.out.print("Nan - ");
		}
		else
		{
			System.out.print("      ");
		}

		Vector xplus = st.add(vt.multiply(tplus));
		Vector xminus = st.add(vt.multiply(tminus));

		Location interceptionPoint = null;
		if (direction == Direction.east)
			interceptionPoint = xminus.getX() > xplus.getX() ? new DefaultLocation(xminus.getX(), xminus.getY())
					: new DefaultLocation(xplus.getX(), xplus.getY());
		else
			interceptionPoint = xminus.getX() < xplus.getX() ? new DefaultLocation(xminus.getX(), xminus.getY())
					: new DefaultLocation(xplus.getX(), xplus.getY());

		return interceptionPoint;
	}
}
