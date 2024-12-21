package football.jef.core.physics;

import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import football.jef.core.Football;

public class PhysicsWorld
{
	// yards per tick per tick
	public static final Velocity gravity = Velocity.ofMetersPerSecond(-9.8);
	public static final Density densityOfAir = Density.ofKilogramPerCubicMeter(1.225);
	public static final Density fieldDensity = Density.ofKilogramPerCubicMeter(1600);
	public static final double coefficientOfFriction = .4;
	public static final double coefficientOfRestitution = .6;

	private final PhysicsWorldXY xyWorld;
	private final PhysicsWorldYZ yzWorld;
	private PhysicsBall physicsBall;

	public PhysicsWorld()
	{
		this.xyWorld = new PhysicsWorldXY();
		this.yzWorld = new PhysicsWorldYZ();
	}

	public void addBall(final Football ball)
	{
		this.physicsBall = new PhysicsBall(ball);
		this.xyWorld.addBall(this.physicsBall.getXyBall());
		this.yzWorld.addBall(this.physicsBall.getYzBall());
	}

	public PhysicsBall getPhysicsBall()
	{
		return this.physicsBall;
	}

	public PhysicsWorldXY getXyWorld()
	{
		return this.xyWorld;
	}

	public PhysicsWorldYZ getYzWorld()
	{
		return this.yzWorld;
	}

	public void update(final double timeInterval)
	{
		this.physicsBall.beforeUpdate(timeInterval);
		this.xyWorld.updatev(timeInterval);
		this.yzWorld.updatev(timeInterval);
		this.physicsBall.afterUpdate(timeInterval);
	}
}
