package jef.actions.pathfinding.blocking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.PlayerState;
import jef.core.Direction;
import jef.core.events.DebugShape;
import jef.core.events.Messages;

public class BlockersAction
{
	public void move(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		defenders = defenders.stream().filter(d -> d.getSteps() != null).toList();

		defenders.forEach(d -> MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawText("" + (int)d.getSteps().getLast().getX(),
						d.getPlayerState().getLoc().add(0, -1, 0), "#FFFF0000", 12)));
		
		List<? extends DefenderPathfinder> defendersRanking = defenders.stream().sorted((d1, d2) ->
		{
			int multiplier = runner.getDirection() == Direction.west ? -1 : 1;
			int ranking = Double.compare(multiplier * d1.getSteps().getLast().getX(),
					multiplier * d2.getSteps().getLast().getX());
			
			
			return ranking;
		}).toList();

		if (defendersRanking.size() > 0)
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawCircle(defendersRanking.getFirst().getPlayerState().getLoc(), "#FFFFFF00", 1));

		Map<DefenderPathfinder, SortedSet<BlockerInterceptRating>> blockersList = new HashMap<>();
		for (BlockerPathfinder blocker : blockers)
		{
			for (DefenderPathfinder d : defendersRanking)
			{
				SortedSet<BlockerInterceptRating> ss = blockersList.get(d);
				if (ss == null)
				{
					ss = new TreeSet<BlockerInterceptRating>();
					blockersList.put(d, ss);
				}

				DefaultInterceptPlayer dip = new DefaultInterceptPlayer(blocker.getPlayerState(), null, d);
				
				int ticks = Integer.MAX_VALUE;
				boolean targetReached = dip.calculate(runner, defenders, blockers, deltaNanos);
				if (targetReached)
					ticks = dip.getSteps().size();

				ss.add(new BlockerInterceptRating(dip, ticks, d.getPlayerState().getLoc().distanceBetween(blocker.getPlayerState().getLoc())));
			}
		}

		for (DefenderPathfinder dr : defendersRanking)
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
			
			bir.bpf.setPath(bir.getBlocker().getPath());
			bir.bpf.calculate(runner, defendersRanking, blockers, deltaNanos);
		}
	}

	private class BlockerInterceptRating implements Comparable<BlockerInterceptRating>
	{
		private DefaultInterceptPlayer bpf;
		private int steps;
		private double distance;

		public BlockerInterceptRating(DefaultInterceptPlayer bpf, int steps, double distance)
		{
			super();
			this.bpf = bpf;
			this.steps = steps;
			this.distance = distance;
		}

		public DefaultInterceptPlayer getBlocker()
		{
			return this.bpf;
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
				ret = bpf.getPlayerState().getPlayer().getPlayerID().compareTo(o.bpf.getPlayerState().getPlayer().getPlayerID());
			
			return ret;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(bpf);
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
			return Objects.equals(this.bpf, other.bpf);
		}

		@Override
		public String toString()
		{
			return "BlockerInterceptRating [bpf=" + this.bpf + ", steps=" + this.steps + ", distance=" + this.distance
					+ "]";
		}
	}
}
