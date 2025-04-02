package jef.movement.player;

public interface ISteering
{
	public static ISteering getInstance()
	{
		return new DefaultSteering();
	}
	
	public static ISteering getInstance(int options)
	{
		return new DefaultSteering();
	}
	
	boolean next(PlayerTracker tracker);
	int calculateTicks(PlayerTracker tracker);
}