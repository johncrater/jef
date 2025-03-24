package jef.core.movement.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import jef.core.Location;
import jef.core.movement.player.Waypoint.DestinationAction;

public class Path implements Iterable<Waypoint>
{
	public static Path removeCurrentWaypoint(Path path)
	{
		List<Waypoint> waypoints = path.waypoints;
		if (waypoints.size() > 0)
			waypoints = waypoints.subList(1, waypoints.size());
		
		return new Path(waypoints, path.destinationWaypoint);
	}
	
	private List<Waypoint> waypoints = new ArrayList<>();
	private Waypoint destinationWaypoint;
	
	private Path(List<Waypoint> waypoints, Waypoint destination)
	{
		this.waypoints = waypoints;
		this.destinationWaypoint = destination;
	}
	
	
	public Path(Path path)
	{
		assert path != null;
		this.waypoints = path.waypoints;
		this.destinationWaypoint = path.destinationWaypoint;
	}
	
	public Path(Location destination)
	{
		this(new Waypoint(destination, 0, DestinationAction.fastStop));
	}
	
	public Path(Location destination, double maxSpeed, DestinationAction destinationAction)
	{
		this(new Waypoint(destination, maxSpeed, destinationAction));
	}
	
	public Path(Location destination, double maxSpeed, DestinationAction destinationAction, double minTurnSpeed, Double destinationOrientation)
	{
		this(new Waypoint(destination, maxSpeed, destinationAction, minTurnSpeed, destinationOrientation));
	}
	
	public Double getDestinationOrientation()
	{
		return this.destinationWaypoint.getDestinationOrientation();
	}
	
	public Path(Waypoint...waypoints)
	{
		this(Arrays.asList(waypoints));
	}
	
	public Path(Collection<Waypoint> waypoints)
	{
		assert waypoints.size() > 0;
		
		this.waypoints = new ArrayList<>(waypoints);
		this.destinationWaypoint = this.waypoints.getLast();
	}

	public List<Waypoint> getWaypoints()
	{
		return new ArrayList<Waypoint>(waypoints);
	}
	
	public Iterator<Waypoint> iterator()
	{
		return this.waypoints.iterator();
	}

	public Waypoint getCurrentWaypoint()
	{
		return this.waypoints.size() > 0 ? this.waypoints.getFirst() : this.destinationWaypoint;
	}
	
	public Waypoint getDestinationWaypoint()
	{
		return this.destinationWaypoint;
	}
	
	public Location getDestination()
	{
		return this.getDestinationWaypoint().getWaypointDestination();
	}
	
	public boolean isComplete()
	{
		return this.waypoints.size() == 0;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(waypoints);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		return Objects.equals(this.waypoints, other.waypoints);
	}

	@Override
	public String toString()
	{
		return "Path [waypoints=" + this.waypoints + "]";
	}
}
