package jef.pathfinding.collisions;

import jef.movement.player.PlayerState;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.PathfindingState;

public class BlockingResolver<T extends IPathfinderPlayer> extends CollisionResolverBase<T>
{
//	private static final double MAXIMUM_BLOCKING_ANGLE = Math.PI / 2;
//	private static final double MAXIMUM_BLOCKING_DISTANCE = 1;

	public BlockingResolver(PathfindingState<T> pathfindingState, PlayerState blocker, PlayerState defender)
	{
		super(pathfindingState, blocker, defender);
	}

	@Override
	public void resolveCollision()
	{
//		double distance = getBlocker().getLoc().distanceBetween(getDefender().getLoc());
//		double angle = Conversions
//				.normalizeAngle(getDefender().getLV().getAzimuth() - getDefender().getLoc().angleTo(getBlocker().getLoc()));

//		double distancePct = Math.clamp(0, (MAXIMUM_BLOCKING_DISTANCE - distance) / MAXIMUM_BLOCKING_DISTANCE, 1.0);
//		double tackleAnglePct = Math.clamp(0, (MAXIMUM_BLOCKING_ANGLE - Math.abs(angle)) / MAXIMUM_BLOCKING_ANGLE, 1.0);

		super.resolveCollision();

//		double pct = distancePct * tackleAnglePct;
//		if (Randomizer.nextDouble() < pct)
//		{
//			this.setPlayerState1(getBlocker().newFrom(null, null, null, null, Posture.fallingDown));
//			this.setPlayerState2(getDefender().newFrom(null, null, null, null, Posture.fallingDown));
//		}
	}

	public PlayerState getBlocker()
	{
		return this.getPlayerState1();
	}

	public PlayerState getDefender()
	{
		return this.getPlayerState2();
	}

}
