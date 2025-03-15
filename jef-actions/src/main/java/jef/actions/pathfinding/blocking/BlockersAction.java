package jef.actions.pathfinding.blocking;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.IPlayerSteps;
import jef.actions.pathfinding.PlayerStates;
import jef.actions.pathfinding.PlayerSteps;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.DebugShape;
import jef.core.events.Messages;

public class BlockersAction
{
	private PlayerStates playerStates;
	private Player runner;
	private Collection<Player> defenders;
	private Collection<Player> blockers;
	private Direction direction;

	
	public BlockersAction(PlayerStates playerStates, Player runner, Collection<Player> defenders,
			Collection<Player> blockers, Direction direction)
	{
		super();
		this.playerStates = playerStates;
		this.runner = runner;
		this.defenders = defenders;
		this.blockers = blockers;
		this.direction = direction;
	}

	public List<PlayerSteps> move()
	{
		defenders.forEach(d -> MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawText("" + (int)playerStates.getSteps(d).getLast().getLoc().getX(),
				playerStates.getSteps(d).getLast().getLoc().add(0, -1, 0), "#FFFF0000", 12)));
		
		List<Player> defendersRanking = defenders.stream().sorted((d1, d2) ->
		{
			int multiplier = direction == Direction.west ? -1 : 1;
			int ranking = Double.compare(multiplier * playerStates.getSteps(d1).getLast().getLoc().getX(),
					multiplier * playerStates.getSteps(d2).getLast().getLoc().getX());
			
			return ranking;
		}).toList();

		if (defendersRanking.size() > 0)
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawCircle(playerStates.getState(defendersRanking.getFirst()).getLoc(), "#FFFFFF00", 1));

		Map<Player, SortedSet<BlockerInterceptRating>> blockersList = new HashMap<>();
		for (Player blocker : blockers)
		{
			for (Player d : defendersRanking)
			{
				SortedSet<BlockerInterceptRating> ss = blockersList.get(d);
				if (ss == null)
				{
					ss = new TreeSet<BlockerInterceptRating>();
					blockersList.put(d, ss);
				}

				DefaultInterceptPlayer dip = new DefaultInterceptPlayer(playerStates, blocker, direction, d);
				
				int ticks = Integer.MAX_VALUE;
				dip.calculate();
				IPlayerSteps blockerSteps = dip.getSteps();
				if (blockerSteps.hasReachedDestination())
					ticks = blockerSteps.getDestinationReachedSteps();
				
				ss.add(new BlockerInterceptRating(dip, ticks, playerStates.getState(d).getLoc().distanceBetween(playerStates.getState(blocker).getLoc())));
			}
		}

		for (Player dr : defendersRanking)
		{
			SortedSet<BlockerInterceptRating> birss = blockersList.get(dr);
			if (birss.size() == 0)
				continue;

			BlockerInterceptRating bir = birss.getFirst();
			
			for (SortedSet<BlockerInterceptRating> ss : blockersList.values())
			{
				for (BlockerInterceptRating birTmp : ss)
				{
					if (birTmp.getBlocker().getPlayerState().equals(bir.getBlocker().getPlayerState()))
					{
						ss.remove(birTmp);
						break;
					}
				}
			}
			
//			bir.blocker.setPath(playerStates.getState(bir.getBlocker().getPlayer()).getPath());
//			bir.blocker.calculate(runner, defendersRanking, blockers);
		}
		
		return null;
	}

	private class BlockerInterceptRating implements Comparable<BlockerInterceptRating>
	{
		private DefaultInterceptPlayer blocker;
		private int steps;
		private double distance;

		public BlockerInterceptRating(DefaultInterceptPlayer blocker, int steps, double distance)
		{
			super();
			this.blocker = blocker;
			this.steps = steps;
			this.distance = distance;
		}

		public DefaultInterceptPlayer getBlocker()
		{
			return this.blocker;
		}

		public int getSteps()
		{
			return this.steps;
		}

		@Override
		public int compareTo(BlockerInterceptRating o)
		{
			
			int ret = Integer.compare(steps, o.getSteps());
			if (ret == 0)
				ret = Double.compare(this.distance, o.distance);
				
			if (ret == 0)
				ret = blocker.getPlayerState().getPlayer().getPlayerID().compareTo(o.blocker.getPlayerState().getPlayer().getPlayerID());
			
			return ret;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(blocker);
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BlockerInterceptRating other = (BlockerInterceptRating) obj;
			return Objects.equals(this.blocker, other.blocker);
		}

		@Override
		public String toString()
		{
			return "BlockerInterceptRating [bpf=" + this.blocker + ", steps=" + this.steps + ", distance=" + this.distance
					+ "]";
		}
	}
}
