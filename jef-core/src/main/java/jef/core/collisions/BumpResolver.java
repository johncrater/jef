package jef.core.collisions;

import jef.core.Randomizer;
import jef.core.movement.Location;
import jef.core.movement.player.Steerable;

public class BumpResolver implements CollisionResolver
{
	private static final double MAXIMUM_BUMP_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_BUMP_DISTANCE = .5;
	
	private Steerable player1;
	private Steerable player2;
		
	public BumpResolver(Steerable player1, Steerable player2, Location location)
	{
		super();
		this.player1 = player1;
		this.player2 = player2;
	}

	@Override
	public void resolveCollision()
	{
		double locAngle = player1.getLoc().angleTo(player2.getLoc());
		double lv1Angle = player1.getLV().getAzimuth();
		double lv2Angle = player2.getLV().getAzimuth();
		
		if (Math.abs(locAngle - lv1Angle) > MAXIMUM_BUMP_ANGLE && Math.abs(locAngle - lv2Angle) > MAXIMUM_BUMP_ANGLE)
			return;
			
		double distance = player1.getLoc().distanceBetween(player2.getLoc());
		if (Randomizer.nextDouble() < (MAXIMUM_BUMP_DISTANCE - distance))
			CollisionResolution.resolveCollision(player1, player2);
	}

}
