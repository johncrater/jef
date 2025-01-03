package jef.core.steering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jef.core.units.DefaultLocation;
import jef.core.units.Location;

public class Path implements Iterable<Waypoint>
{
	private List<Waypoint> waypoints = new ArrayList<>();
	private Location destination = new DefaultLocation();
	
	public Path()
	{
	}

	public void addWaypoint(Waypoint waypoint)
	{
		this.waypoints.add(waypoint);
		updateDestination();
	}

	public List<Waypoint> getWaypoints()
	{
		return Collections.unmodifiableList(waypoints);
	}
	
	public void clear()
	{
		this.waypoints.clear();
	}

	public Iterator<Waypoint> iterator()
	{
		return this.waypoints.iterator();
	}

	public Waypoint getCurrentWaypoint()
	{
		return this.waypoints.getFirst();
	}
	
	public Location getDestination()
	{
		return this.destination;
	}
	
	public void removeWaypoint(Waypoint waypoint)
	{
		this.waypoints.remove(waypoint);
		updateDestination();
	}

	private void updateDestination()
	{
		if (this.waypoints.size() > 0)
			this.destination = this.waypoints.getLast().getDestination();
	}
}
