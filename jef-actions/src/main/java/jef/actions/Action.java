package jef.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jef.IPlayers;
import jef.core.Player;
import jef.core.movement.player.Path;

public class Action
{
	private List<Action> actions = new ArrayList<>();
	private IPlayers players;
	private Player player;
	private boolean isComplete;
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public Path process()
	{
		for (Action action : actions)
		{
			if (action.isComplete())
				continue;
			
			return action.process();
		}
		
		return null;
	}
	
	public void addAction(Action action)
	{
		this.actions.add(action);
	}
	
	public Collection<Action> getActions()
	{
		return Collections.unmodifiableCollection(actions);
	}
	
	public IPlayers getPlayers()
	{
		return this.players;
	}

	public boolean isComplete()
	{
		return this.isComplete;
	}
	
	public void markCompleted()
	{
		this.isComplete = true;
	}
}
