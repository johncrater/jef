package jef.core.movement.player;

public interface Steering
{
	public static Steering getInstance()
	{
		return new AdvancedSteering();
	}
	
	public static Steering getInstance(int options)
	{
		return new AdvancedSteering();
	}
	
	boolean next(PlayerTracker tracker);
	int calculateTicks(PlayerTracker tracker);
}