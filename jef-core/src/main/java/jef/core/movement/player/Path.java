package jef.core.movement.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import jef.core.Location;

public class Path implements Iterable<Waypoint>
{
	private List<Waypoint> waypoints = new ArrayList<>();
	private Location destination = new Location();
	
	public Path(Path path)
	{
		this.waypoints = path.getWaypoints();
		this.destination = path.getDestination();
	}
	
	public Path(Waypoint...waypoints)
	{
		for (Waypoint wp : waypoints)
			addWaypoint(wp);
	}
	
	private void addWaypoint(Waypoint waypoint)
	{
		this.waypoints.add(waypoint);
		updateDestination();
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
		return this.waypoints.size() > 0 ? this.waypoints.getFirst() : null;
	}
	
	
	public Location getDestination()
	{
		return this.destination;
	}
	
	private void updateDestination()
	{
		if (this.waypoints.size() > 0)
			this.destination = this.waypoints.getLast().getDestination();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(destination, waypoints);
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
		return Objects.equals(this.destination, other.destination) && Objects.equals(this.waypoints, other.waypoints);
	}

	@Override
	public String toString()
	{
		return "Path [waypoints=" + this.waypoints + "]";
	}
}
