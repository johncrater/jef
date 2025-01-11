package jef.core.pathfinding;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
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
		// 0 = (vt^2 - vi^2) * t^2 + 2 * (st - si) * vt * t + (st - si)2;
		Vector2D st = target.getLoc().toVector2D();
		Vector2D si = player.getLoc().toVector2D();
		Vector2D vt = target.getLV().toVector2D();
		double vi = player.getLV().getSpeed();
		
		double a = vt.dotProduct(vt) * Math.pow(vi,  2);
		double b = 2 * st.subtract(si).dotProduct(vt);
		double c = st.subtract(si).dotProduct(st.subtract(si));
		
		double tplus = (-b + Math.sqrt(4 * a * c)) / 2 * a;
		double tminus = (-b - Math.sqrt(4 * a * c)) / 2 * a;
		
		Vector2D xplus = st.add(vt.scalarMultiply(tplus));
		Vector2D xminus = st.add(vt.scalarMultiply(tminus));

		Location interceptionPoint = null;
		if (direction == Direction.east)
			interceptionPoint = xminus.getX() < xplus.getX() ? new DefaultLocation(xminus.getX(), xminus.getY()) : new DefaultLocation(xplus.getX(), xplus.getY());
		else
			interceptionPoint = xminus.getX() > xplus.getX() ? new DefaultLocation(xminus.getX(), xminus.getY()) : new DefaultLocation(xplus.getX(), xplus.getY());
			
		return new DefaultPath(new Waypoint(interceptionPoint, player.getMaxSpeed(), DestinationAction.noStop));
	}

	@Override
	public Path findPath(double maximumTimeToSpend)
	{
		return findPath();
	}

}
