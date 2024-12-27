package jef.core.steering;

import jef.core.units.Location;

public class Waypoint
{
	public enum DestinationAction { hardStop, softStop, runThrough, angleForNextWaypoint }
	
	private Location destination;
	private double maxSpeed;
	private DestinationAction destinationAction;
	
	public Waypoint()
	{
	}

	public Waypoint(Location destination, double maxSpeed, DestinationAction destinationAction)
	{
		super();
		this.destination = destination;
		this.maxSpeed = maxSpeed;
		this.destinationAction = destinationAction;
	}

	public Location getDestination()
	{
		return this.destination;
	}

	public void setDestination(Location destination)
	{
		this.destination = destination;
	}

	public double getMaxSpeed()
	{
		return this.maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed)
	{
		this.maxSpeed = maxSpeed;
	}

	public DestinationAction getDestinationAction()
	{
		return this.destinationAction;
	}

	public void setDestinationAction(DestinationAction destinationAction)
	{
		this.destinationAction = destinationAction;
	}

}
