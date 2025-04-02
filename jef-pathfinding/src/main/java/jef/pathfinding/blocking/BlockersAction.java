package jef.pathfinding.blocking;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.geometry.Direction;
import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;
import jef.pathfinding.IPlayerSteps;
import jef.pathfinding.PathfindingMessages;

public class BlockersAction<T extends IPathfinderPlayer>
{
	private IPathfinderState<T> pathfinderState;
	private Collection<T> defenders;
	private Collection<T> blockers;
	private Map<T, Path> newPaths = new HashMap<>();

	public BlockersAction(IPathfinderState<T> pathfinderState, T runner,
			Collection<T> defenders, Collection<T> blockers)
	{
		super();
		this.pathfinderState = pathfinderState;
		this.defenders = defenders;
		this.blockers = blockers;
	}

	public Path getPath(T player)
	{
		return this.newPaths.get(player);
	}

	public void move()
	{
		defenders.forEach(d -> MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
				DebugShape.drawText("" + (int) pathfinderState.getPlayerSteps(d.getId()).getLast().getLoc().getX(),
						pathfinderState.getPlayerSteps(d.getId()).getLast().getLoc().add(0, -1, 0), "#FFFF0000", 12)));

		List<T> defendersRanking = defenders.stream().sorted((d1, d2) ->
		{
			int multiplier = pathfinderState.getFootballState().getCurrentOffenseDirection() == Direction.west ? -1 : 1;
			int ranking = Double.compare(multiplier * pathfinderState.getPlayerSteps(d1.getId()).getLast().getLoc().getX(),
					multiplier * pathfinderState.getPlayerSteps(d2.getId()).getLast().getLoc().getX());

			return ranking;
		}).toList();

		if (defendersRanking.size() > 0)
			MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
					DebugShape.drawCircle(pathfinderState.getPlayerState(defendersRanking.getFirst().getId()).getLoc(), "#FFFFFF00", 1));

		Map<T, SortedSet<BlockerInterceptRating<T>>> blockersList = new HashMap<>();
		for (T blocker : blockers)
		{
			for (T d : defendersRanking)
			{
				SortedSet<BlockerInterceptRating<T>> ss = blockersList.get(d);
				if (ss == null)
				{
					ss = new TreeSet<BlockerInterceptRating<T>>();
					blockersList.put(d, ss);
				}

				DefaultInterceptPlayer<T> dip = new DefaultInterceptPlayer<T>(pathfinderState, blocker, d);

				int ticks = Integer.MAX_VALUE;
				Path path = dip.calculatePath();
				IPlayerSteps blockerSteps = this.pathfinderState.createSteps(pathfinderState.getPlayerState(blocker.getId()), path);
				if (blockerSteps.hasReachedDestination())
					ticks = blockerSteps.getDestinationReachedSteps();

				ss.add(new BlockerInterceptRating<T>(dip, ticks,
						pathfinderState.getPlayerState(d.getId()).getLoc().distanceBetween(pathfinderState.getPlayerState(blocker.getId()).getLoc())));
			}
		}

		for (T dr : defendersRanking)
		{
			SortedSet<BlockerInterceptRating<T>> birss = blockersList.get(dr);
			if (birss == null || birss.size() == 0)
				continue;

			BlockerInterceptRating<T> bir = birss.getFirst();

			for (SortedSet<BlockerInterceptRating<T>> ss : blockersList.values())
			{
				for (BlockerInterceptRating<T> birTmp : ss)
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

	private class BlockerInterceptRating<T2 extends IPathfinderPlayer> implements Comparable<BlockerInterceptRating<T2>>
	{
		private DefaultInterceptPlayer<T2> blocker;
		private int steps;
		private double distance;

		public BlockerInterceptRating(DefaultInterceptPlayer<T2> blocker, int steps, double distance)
		{
			super();
			this.blocker = blocker;
			this.steps = steps;
			this.distance = distance;
		}

		public DefaultInterceptPlayer<T2> getBlocker()
		{
			return this.blocker;
		}

		public int getSteps()
		{
			return this.steps;
		}

		@Override
		public int compareTo(BlockerInterceptRating<T2> o)
		{

			int ret = Integer.compare(steps, o.getSteps());
			if (ret == 0)
				ret = Double.compare(this.distance, o.distance);

			if (ret == 0)
				ret = blocker.getPlayerState().getPlayerId()
						.compareTo(o.blocker.getPlayerState().getPlayerId());

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
			@SuppressWarnings("unchecked")
			BlockerInterceptRating<T2> other = (BlockerInterceptRating<T2>) obj;
			return Objects.equals(this.blocker, other.blocker);
		}

		@Override
		public String toString()
		{
			return "BlockerInterceptRating [bpf=" + this.blocker + ", steps=" + this.steps + ", distance="
					+ this.distance + "]";
		}
	}

	public Collection<T> getBlockers()
	{
		return this.blockers;
	}
}
