package jef.core.steering;

import java.util.Iterator;

import jef.core.MoveableImpl;
import jef.core.Player;
import jef.core.units.LinearVelocity;

public class Steering implements Iterable<Moveable>, Iterator<Moveable>
{
	private Steerable steerable;
	private double timeInterval;
	private LinearVelocity remainingVelocity;
	
	public Steering(Steerable steerable, double timeInterval)
	{
		this.steerable = steerable;
		this.timeInterval = timeInterval;
	}

	@Override
	public Iterator<Moveable> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext()
	{
		return steerable.getPath().getWaypoints().size() > 0;
	}

	@Override
	public Moveable next()
	{
		MoveableImpl moveable = new MoveableImpl(this.steerable);
		this.remainingVelocity = steerable.getLinearVelocity().multiply(timeInterval);
		if (hasNext() == false)
		{
			// coast to a stop
			this.remainingVelocity = this.remainingVelocity.add(Player.normalDecelerationRate * timeInterval);
			final var speed = Math.max(0, remainingVelocity.getXYSpeed());
			moveable.
			turnTracker.adjustSpeed(speed);
		}
		
		return null;
	}

	
}
