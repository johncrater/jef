package jef.pathfinding.blocking;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.IPlayers;
import jef.Players.PlayerSteps;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.movement.player.Path;
import jef.pathfinding.DefaultInterceptPlayer;

public class BlockersAction
{
	private IPlayers players;
//	private Player runner;
	private Collection<Player> defenders;
	private Collection<Player> blockers;
	private Direction direction;
	private Map<Player, Path> newPaths = new HashMap<>();
	
	public BlockersAction(IPlayers players, Player runner, Collection<Player> defenders,
			Collection<Player> blockers, Direction direction)
	{
		super();
		this.players = players;
//		this.runner = runner;
		this.defenders = defenders;
		this.blockers = blockers;
		this.direction = direction;
	}

	public Path getPath(Player player)
	{
		return this.newPaths.get(player);
	}
	
	public void move()
	{
		defenders.forEach(d -> MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawText("" + (int)players.getSteps(d).getLast().getLoc().getX(),
				players.getSteps(d).getLast().getLoc().add(0, -1, 0), "#FFFF0000", 12)));
		
		List<Player> defendersRanking = defenders.stream().sorted((d1, d2) ->
		{
			int multiplier = direction == Direction.west ? -1 : 1;
			int ranking = Double.compare(multiplier * players.getSteps(d1).getLast().getLoc().getX(),
					multiplier * players.getSteps(d2).getLast().getLoc().getX());
			
			return ranking;
		}).toList();

		if (defendersRanking.size() > 0)
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawCircle(players.getState(defendersRanking.getFirst()).getLoc(), "#FFFFFF00", 1));

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

				DefaultInterceptPlayer dip = new DefaultInterceptPlayer(players, blocker, direction, d);
				
				int ticks = Integer.MAX_VALUE;
				Path path = dip.calculatePath();
				PlayerSteps blockerSteps = this.players.createSteps(players.getState(blocker), path);
				if (blockerSteps.hasReachedDestination())
					ticks = blockerSteps.getDestinationReachedSteps();
				
				ss.add(new BlockerInterceptRating(dip, ticks, players.getState(d).getLoc().distanceBetween(players.getState(blocker).getLoc())));
			}
		}

		for (Player dr : defendersRanking)
		{
			SortedSet<BlockerInterceptRating> birss = blockersList.get(dr);
			if (birss == null || birss.size() == 0)
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
			
			Path path = bir.blocker.calculatePath();
			this.newPaths.put(bir.blocker.getPlayer(), path);
		}
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

	public Collection<Player> getBlockers()
	{
		return this.blockers;
	}
}
