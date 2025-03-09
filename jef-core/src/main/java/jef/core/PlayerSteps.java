package jef.core;

import java.util.Arrays;

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
		steps[0] = startingState;
		this.timerInterval = timerInterval;
	}

	public int getCapacity()
	{
		return this.steps.length;
	}
	
	public PlayerState getState(int offset)
	{
		int index = getIndex(offset);
		PlayerState ret = steps[index];
		if (ret == null)
			advanceTo(offset);
		
		return steps[index];
	}

	public void reset(PlayerState startingState)
	{
		Arrays.fill(steps, null);
		steps[0] = startingState;
		startOffset = 0;
	}
	
	public void advance()
	{
		if (steps[getIndex(1)] == null)
			advanceTo(1);

		this.steps[getIndex(0)] = null;
		this.startOffset += 1;
	}

	private void advanceTo(int offset)
	{
		int startingOffset = 0;
		PlayerState lastState = null;
		for (int i = 0; i <= offset; i++)
		{
			startingOffset = i;
			if (steps[getIndex(i)] != null)
			{
				lastState = steps[getIndex(i)];
			}
			else
			{
				break;
			}
		}
		
		assert lastState != null;
		
		PlayerTracker tracker = new PlayerTracker(lastState, timerInterval);
		for (int i = startingOffset; i <= offset; i++)
		{
			Steering steering = Steering.getInstance();
			steering.next(tracker);
			tracker.advance();
			steps[getIndex(i)] = tracker.getState();
		}
	}
	
	private int getIndex(int offset)
	{
		return (this.startOffset + offset) % this.steps.length;
	}
}
