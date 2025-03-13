package jef.actions.pathfinding;

import java.util.Arrays;

import jef.core.PlayerState;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;

public class PlayerSteps
{
	private PlayerState [] steps;
	private int startOffset;
	private double timerInterval;
	
	public PlayerSteps(PlayerState startingState, int capacity, double timerInterval)
	{
		steps = new PlayerState[capacity];
		this.timerInterval = timerInterval;
		reset(startingState);
	}

	public int getCapacity()
	{
		return this.steps.length;
	}
	
	public PlayerState getState(int offset)
	{
		int index = getIndex(offset);
		return steps[index];
	}

	public void reset(PlayerState startingState)
	{
		Arrays.fill(steps, null);
		steps[0] = startingState;
		startOffset = 0;

		PlayerTracker tracker = new PlayerTracker(startingState, timerInterval);
		for (int i = 1; i < this.steps.length; i++)
		{
			Steering steering = Steering.getInstance();
			steering.next(tracker);
			tracker.advance();
			steps[i] = tracker.getState();
		}
	}
	
	public void advance()
	{
		PlayerTracker tracker = new PlayerTracker(this.steps[getIndex(this.steps.length - 1)], timerInterval);
		Steering steering = Steering.getInstance();
		steering.next(tracker);
		tracker.advance();
		
		startOffset += 1;
		steps[getIndex(this.steps.length - 1)] = tracker.getState();
	}

	private int getIndex(int offset)
	{
		return (this.startOffset + offset) % this.steps.length;
	}
}
