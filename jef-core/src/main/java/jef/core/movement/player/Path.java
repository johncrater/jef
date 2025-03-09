package jef.core.movement.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public String toString()
	{
		return "Path [waypoints=" + this.waypoints + "]";
	}
}
