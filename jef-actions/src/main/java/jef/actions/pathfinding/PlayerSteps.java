package jef.actions.pathfinding;

import java.util.Arrays;

import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;

public class PlayerSteps implements IPlayerSteps
{
	private final PlayerState[] steps;
	private final int stepCapacity;
	private final double timerInterval;
	private int startOffset;
	private int destinationReachedSteps;

	public PlayerSteps(final PlayerState startingState, final int stepCapacity, final double timerInterval,
			final int startOffset)
	{
		this.steps = new PlayerState[stepCapacity];
		this.stepCapacity = stepCapacity;
		this.timerInterval = timerInterval;
		this.startOffset = startOffset;
		this.destinationReachedSteps = -1;
		this.reset(startingState);
	}
	
	public PlayerSteps(IPlayerSteps playerSteps)
	{
		this.steps = new PlayerState[playerSteps.getCapacity()];
		this.stepCapacity = playerSteps.getCapacity();
		this.timerInterval = playerSteps.getTimerInterval();
		this.startOffset = playerSteps.getStartOffset();
		this.destinationReachedSteps = playerSteps.getDestinationReachedSteps();
		
		for (int i = 0; i < playerSteps.getCapacity(); i++)
		{
			this.steps[getIndex(i + this.startOffset)] = playerSteps.getState(i + startOffset);
		}
	}

	public Player getPlayer()
	{
		return steps[0].getPlayer();
	}
	
	public void advance()
	{
		final PlayerTracker tracker = new PlayerTracker(this.steps[this.getIndex(this.steps.length - 1)],
				this.timerInterval);
		final Steering steering = Steering.getInstance();
		final boolean destinationReached = steering.next(tracker);
		this.steps[this.getIndex(0)] = tracker.getState();

		if (destinationReached && (this.destinationReachedSteps == -1))
		{
			this.destinationReachedSteps = this.steps.length - 1;
		}
		else if (this.destinationReachedSteps > 0)
		{
			this.destinationReachedSteps -= 1;
		}

		this.startOffset += 1;
	}

	@Override
	public int getCapacity()
	{
		return this.stepCapacity;
	}

	@Override
	public int getDestinationReachedSteps()
	{
		return this.destinationReachedSteps;
	}

	@Override
	public PlayerState getFirst()
	{
		return this.getState(0);
	}

	@Override
	public PlayerState getLast()
	{
		return this.getState(this.getCapacity() - 1);
	}

	@Override
	public double getTimerInterval()
	{
		return this.timerInterval;
	}

	@Override
	public int getStartOffset()
	{
		return this.startOffset;
	}

	@Override
	public PlayerState getState(final int offset)
	{
		final int index = this.getIndex(offset);
		return this.steps[index];
	}

	@Override
	public boolean hasReachedDestination()
	{
		return this.destinationReachedSteps > -1;
	}

	public void reset(final PlayerState startingState)
	{
		Arrays.fill(this.steps, null);

		this.destinationReachedSteps = -1;
		final Steering steering = Steering.getInstance();
		final PlayerTracker tracker = new PlayerTracker(startingState, this.timerInterval);
		this.steps[this.getIndex(0)] = tracker.getState();
		tracker.advance();
		if (tracker.destinationReached())
		{
			this.destinationReachedSteps = 0;
		}

		for (int i = 1; i < this.steps.length; i++)
		{
			final boolean destinationReached = steering.next(tracker);
			if (destinationReached)
			{
				this.destinationReachedSteps = i;
			}

			tracker.advance();
			this.steps[this.getIndex(i)] = tracker.getState();
		}
	}

	private int getIndex(final int offset)
	{
		return (this.startOffset + offset) % this.stepCapacity;
	}

}
