package jef.movement.player;

import java.util.Objects;

import jef.core.Player;
import jef.geometry.AngularVelocity;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;

public class PlayerState
{
	public static enum Posture
	{

		// Player activities effected by Posture include: moving, tackling, blocking,
		// passing, catching (including intercepting), kicking, fumbling

		// normal operation. could be running or just standing. All activities are
		// normal
		upright,

		// falling but won't hit ground necessarily depending on events
		// if a player is stumbling and he exceeds his maximum velocity, he will
		// transition to fallingDown
		// A stumbling player can still move, tackle, block, pass, catch at reduced
		// effectiveness. But they may not kick.
		stumbling,

		// as a result of contact player has lost balance. He will hit the ground
		// momentarily. While fallingDown
		// a player can still move, tackle, block, pass, catch at reduced effectiveness.
		// But they may not kick.
		fallingDown,

		// fallen and not moving. Once a player hits the ground, they cannot move,
		// tackle, block, pass, catch, kick, or anything else other than transition to
		// standingUp. However.
		// they may trip an opponent depending on conditions that may cause him to
		// transition to stumbling.
		onTheGround,

		// a player is transitioning from onTheGround to upright. During this
		// transition, the
		// player cannot move, kick, pass. They may block or tackling can reduced
		// effectiveness.
		standingUp,
		
		// center with hands on the ball on the ground ready to snap
		snapping,

		// standard 3 point stance, right hand on the ground
		threePointStance,
		
		// standard four point stance
		fourPointStance,
		
		// holder for a right footed kicker to the left of the "tee"
		holderLeft,
		
		// holder for a left footed kicker to the right of the "tee"
		holderRight;

		public Posture adjustDown()
		{
			return switch (this)
			{
				case fallingDown -> onTheGround;
				case onTheGround -> onTheGround;
				case standingUp -> onTheGround;
				case stumbling -> fallingDown;
				case upright -> stumbling;
				case snapping -> onTheGround;
				case threePointStance -> onTheGround;
				case fourPointStance -> onTheGround;
				case holderLeft -> onTheGround;
				case holderRight -> onTheGround;
			};
		}

		public Posture adjustUp()
		{
			return switch (this)
			{
				case fallingDown -> onTheGround;
				case onTheGround -> standingUp;
				case standingUp -> upright;
				case stumbling -> upright;
				case upright -> upright;
				case snapping -> standingUp;
				case threePointStance -> standingUp;
				case fourPointStance -> standingUp;
				case holderLeft -> standingUp;
				case holderRight -> standingUp;
			};
		}
	}
	
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private Posture posture;
	private Player player;
	
	public PlayerState(Player player)
	{
		this(player, null, null, null, null);
	}
	
	public PlayerState(Player player, LinearVelocity lv, Location loc, AngularVelocity av, Posture posture)
	{
		assert player != null;
		this.player = player;
		
		if (lv == null)
			lv = new LinearVelocity();
		
		if (loc == null)
			loc = new Location();
		
		if (av == null)
			av = new AngularVelocity();

		if (posture == null)
			posture = Posture.upright;
		
		this.lv = lv;
		this.loc = loc;
		this.av = av;
		this.posture = posture;
	}

	public LinearVelocity getLV()
	{
		return this.lv;
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public AngularVelocity getAV()
	{
		return this.av;
	}

	public Posture getPosture()
	{
		return this.posture;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public PlayerState newFrom(LinearVelocity lv, Location loc, AngularVelocity av, Posture posture)
	{
		if (lv == null)
			lv = this.lv;
		
		if (loc == null)
			loc = this.loc;
		
		if (av == null)
			av = this.av;
		
		if (posture == null)
			posture = this.posture;
		
		return new PlayerState(this.player, lv, loc, av, posture);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(av, loc, lv, player, posture);
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
		PlayerState other = (PlayerState) obj;
		return Objects.equals(this.av, other.av) && Objects.equals(this.loc, other.loc)
				&& Objects.equals(this.lv, other.lv) 
				&& Objects.equals(this.player, other.player) && this.posture == other.posture;
	}

	@Override
	public String toString()
	{
		return this.player + "" + this.lv + ", " + this.loc + ", " + this.av
				+ ", " + this.posture;
	}
	
	
}
