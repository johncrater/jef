package jef.core.pathfinding.blocking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.DefaultPlayer;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.pathfinding.DefaultInterceptPlayer;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class BlockersAction
{
	public void move(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		defenders.forEach(d -> 
				MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawText("" + (int)d.getSteps().getLast().getX(),
						d.getPlayer().getLoc().add(0, -1, 0), "#FFFF0000", 12)));
		
		List<? extends DefenderPathfinder> defendersRanking = defenders.stream().sorted((d1, d2) ->
		{
			int multiplier = runner.getDirection() == Direction.west ? -1 : 1;
			return Double.compare(multiplier * d1.getSteps().getLast().getX(),
					multiplier * d2.getSteps().getLast().getX());
		}).toList();

		MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
				DebugShape.drawCircle(defendersRanking.getFirst().getPlayer().getLoc(), "#FFFFFF00", 1));

		Map<DefenderPathfinder, SortedSet<BlockerInterceptRating>> blockersList = new HashMap<>();
		blockers.forEach(pf ->
		{
			defendersRanking.forEach(d ->
			{
				SortedSet<BlockerInterceptRating> ss = blockersList.get(d);
				if (ss == null)
				{
					ss = new TreeSet<BlockerInterceptRating>();
					blockersList.put(d, ss);
				}

				DefaultInterceptPlayer dip = new DefaultInterceptPlayer(pf.getPlayer(), null, d);
				dip.calculate(runner, defenders, blockers, deltaNanos);
				int ticks = dip.getSteps().size();
				ss.add(new BlockerInterceptRating(dip, ticks));
			});
		});

		defendersRanking.forEach(dr ->
		{
			SortedSet<BlockerInterceptRating> birss = blockersList.get(dr);
			if (birss.size() == 0)
				return;

			BlockerInterceptRating bir = birss.getFirst();

			DefaultPlayer player = (DefaultPlayer) bir.getBlocker().getPlayer();
			player.setPath(bir.getBlocker().getPath());

			blockersList.values().forEach(ss -> ss.remove(bir));
		});
	}

	private class BlockerInterceptRating implements Comparable<BlockerInterceptRating>
	{
		private DefaultInterceptPlayer bpf;
		private int steps;

		public BlockerInterceptRating(DefaultInterceptPlayer bpf, int steps)
		{
			super();
			this.bpf = bpf;
			this.steps = steps;
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
			return Integer.compare(steps, o.getSteps());
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
	}
}
