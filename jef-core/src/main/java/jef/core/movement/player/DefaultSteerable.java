package jef.core.movement.player;

import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.movement.DefaultMoveable;
import jef.core.movement.Posture;

public class DefaultSteerable extends DefaultMoveable implements Steerable
{
	private Player player;
	private SpeedMatrix speedMatrix;
	private double desiredSpeed;
	private Path path;
	private Posture posture;
	private double accelerationCoefficient;
	private PlayerPosition currentPosition;

	public DefaultSteerable(Player player)
	{
		super(player.getLV(), player.getLoc(), player.getAV());
		this.player = player;
		this.speedMatrix = player.getSpeedMatrix();
		this.desiredSpeed = player.getMaxSpeed();
		this.path = player.getPath();
		this.posture = player.getPosture();
		this.accelerationCoefficient = player.getAccelerationCoefficient();
		this.currentPosition = player.getCurrentPosition();
	}

	public DefaultSteerable(final Player player, Path path)
	{
		this(player);
		this.path = new DefaultPath(path);
	}

	public DefaultSteerable(final Steerable s)
	{
		super(s);
		this.player = s.getPlayer();
		this.accelerationCoefficient = s.getAccelerationCoefficient();
		this.desiredSpeed = s.getDesiredSpeed();
		this.path = new DefaultPath(s.getPath());
		this.posture = s.getPosture();
		this.speedMatrix = s.getSpeedMatrix();
		this.currentPosition = s.getCurrentPosition();
	}

	public DefaultSteerable(final Steerable s, Path path)
	{
		this(s);
		this.path = new DefaultPath(path);
	}

	@Override
	public PlayerPosition getCurrentPosition()
	{
		return this.currentPosition;
	}

	@Override
	public Player getPlayer()
	{
		return player;
	}

	@Override
	public double getAccelerationCoefficient()
	{
		return this.accelerationCoefficient;
	}

	@Override
	public double getDesiredSpeed()
	{
		return this.desiredSpeed;
	}

	@Override
	public double getMaxSpeed()
	{
		return this.speedMatrix.getSprintingSpeed();
	}

	@Override
	public Path getPath()
	{
		return this.path;
	}

	@Override
	public Posture getPosture()
	{
		return this.posture;
	}

	@Override
	public SpeedMatrix getSpeedMatrix()
	{
		return this.speedMatrix;
	}

}
