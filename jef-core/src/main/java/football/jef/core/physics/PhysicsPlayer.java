package football.jef.core.physics;

import org.dyn4j.dynamics.Body;

import football.jef.core.Player;

public class PhysicsPlayer extends Body
{
	private final Player player;

	public PhysicsPlayer(final Player player)
	{
		this.player = player;
	}

	public Player getPlayer()
	{
		return this.player;
	}

}
