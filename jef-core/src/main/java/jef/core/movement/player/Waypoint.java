package jef.core.movement.player;

import jef.core.movement.Location;

public class Waypoint
{
	public enum DestinationAction { instant, fastStop, normalStop, slowStop, noStop, rounded}
	
	private Location destination;
	private double maxSpeed;
	private double minTurnSpeed;
	private DestinationAction destinationAction;
	
	public Waypoint()
	{
	}

	public Waypoint(Location destination, double minTurnSpeed, double maxSpeed, DestinationAction destinationAction)
	{
		super();
		this.destination = destination;
		this.minTurnSpeed = minTurnSpeed;
		this.maxSpeed = maxSpeed;
		this.destinationAction = destinationAction;
	}

	public Waypoint(Location destination, double maxSpeed, DestinationAction destinationAction)
	{
		super();
		this.destination = destination;
		this.minTurnSpeed = maxSpeed;
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

	protected double getMinTurnSpeed()
	{
		return this.minTurnSpeed;
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

	@Override
	public String toString()
	{
		return String.format("%s %3.1f %s", this.destination, this.maxSpeed, this.destinationAction);
	}

}
