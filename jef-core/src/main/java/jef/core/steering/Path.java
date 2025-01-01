package jef.core.steering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Path implements Iterable<Waypoint>
{
	private List<Waypoint> waypoints = new ArrayList<>();
	
	public Path()
	{
	}

	public void addWaypoint(Waypoint waypoint)
	{
		this.waypoints.add(waypoint);
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


}
