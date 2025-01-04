package jef.core.steering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jef.core.Location;
import jef.core.units.DefaultLocation;

public class DefaultPath implements Iterable<Waypoint>, Path
{
	private List<Waypoint> waypoints = new ArrayList<>();
	private Location destination = new DefaultLocation();
	
	public DefaultPath()
	{
	}

	@Override
	public void addWaypoint(Waypoint waypoint)
	{
		this.waypoints.add(waypoint);
		updateDestination();
	}

	@Override
	public List<Waypoint> getWaypoints()
	{
		return Collections.unmodifiableList(waypoints);
	}
	
	@Override
	public void clear()
	{
		this.waypoints.clear();
	}

	public Iterator<Waypoint> iterator()
	{
		return this.waypoints.iterator();
	}

	@Override
	public Waypoint getCurrentWaypoint()
	{
		return this.waypoints.getFirst();
	}
	
	@Override
	public Location getDestination()
	{
		return this.destination;
	}
	
	@Override
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
