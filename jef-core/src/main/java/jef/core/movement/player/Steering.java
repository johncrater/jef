package jef.core.movement.player;

public interface Steering
{
	public static Steering getInstance()
	{
		return new DefaultSteering();
	}
	
	public static Steering getInstance(int options)
	{
		return new DefaultSteering();
	}
	
	boolean next(PlayerTracker tracker);

}