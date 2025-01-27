package jef.core.pathfinding;

import jef.core.Field;
import jef.core.Performance;
import jef.core.Player;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.PlayerTracker;

public class InterceptPlayer extends AbstractPathfinder
{
	private Player player;
	private Player target;

	private PlayerTracker playerTracker;
	private LineSegment targetSegment;

	public InterceptPlayer(Player player, Player target)
	{
		super();
		this.player = player;
		this.target = target;

		this.playerTracker = new PlayerTracker(this.player, Performance.frameInterval);
		this.targetSegment = new LineSegment(this.target.getLoc(),
				this.target.getLoc().add(this.target.getLV().add(Field.FIELD_TOTAL_LENGTH)))
				.restrictToBetweenEndZones();
	}

	@Override
	public boolean calculate()
	{
		long nanos = System.nanoTime();
		
		

		return false;
	}

}
