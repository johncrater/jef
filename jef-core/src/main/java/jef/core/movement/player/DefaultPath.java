package jef.core.movement.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;

public class DefaultPath implements Iterable<Waypoint>, Path
{
	private List<Waypoint> waypoints = new ArrayList<>();
	private Location destination = new DefaultLocation();
	
	public DefaultPath(Path path)
	{
		this.waypoints = path.getWaypoints();
		this.destination = path.getDestination();
	}
	
	public DefaultPath(Waypoint...waypoints)
	{
		for (Waypoint wp : waypoints)
			addWaypoint(wp);
	}
	
	private void addWaypoint(Waypoint waypoint)
	{
		this.waypoints.add(waypoint);
		updateDestination();
	}

	@Override
	public List<Waypoint> getWaypoints()
	{
		return new ArrayList<Waypoint>(waypoints);
	}
	
	public Iterator<Waypoint> iterator()
	{
		return this.waypoints.iterator();
	}

	@Override
	public Waypoint getCurrentWaypoint()
	{
		return this.waypoints.size() > 0 ? this.waypoints.getFirst() : null;
	}
	
	@Override
	public Location getDestination()
	{
		return this.destination;
	}
	
	private void updateDestination()
	{
		if (this.waypoints.size() > 0)
			this.destination = this.waypoints.getLast().getDestination();
	}
}
