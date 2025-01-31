package jef.core.movement.player;

import jef.core.PlayerPosition;
import jef.core.movement.DefaultMoveable;
import jef.core.movement.Posture;

public class DefaultSteerable extends DefaultMoveable implements Steerable
{
	private SpeedMatrix speedMatrix;
	private double desiredSpeed;
	private Path path;
	private Posture posture;
	private double accelerationCoefficient;
	private PlayerPosition currentPosition;

	public DefaultSteerable()
	{
		super();
	}

	public DefaultSteerable(final Steerable s)
	{
		super(s);
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

	public void setSpeedMatrix(SpeedMatrix speedMatrix)
	{
		this.speedMatrix = speedMatrix;
	}

	public void setDesiredSpeed(double desiredSpeed)
	{
		this.desiredSpeed = desiredSpeed;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	public void setPosture(Posture posture)
	{
		this.posture = posture;
	}

	public void setAccelerationCoefficient(double accelerationCoefficient)
	{
		this.accelerationCoefficient = accelerationCoefficient;
	}

	public void setCurrentPosition(PlayerPosition currentPosition)
	{
		this.currentPosition = currentPosition;
	}

	@Override
	public PlayerPosition getCurrentPosition()
	{
		return this.currentPosition;
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
