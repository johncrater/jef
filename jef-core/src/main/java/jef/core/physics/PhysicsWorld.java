package jef.core.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.dyn4j.dynamics.Body;

import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import jef.core.Football;

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
	private HashSet<PhysicsPlayer> players = new HashSet<>();

	public PhysicsWorld()
	{
		this.xyWorld = new PhysicsWorldXY();
		this.yzWorld = new PhysicsWorldYZ();
	}

	public void addPlayer(PhysicsPlayer player)
	{
		this.players.add(player);
		xyWorld.addPlayer(player);
	}
	
	public Body getFloor()
	{
		return this.yzWorld.getFloor();
	}
	
	public List<PhysicsPlayer> getPlayers()
	{
		return Collections.unmodifiableList(new ArrayList<>(this.players));
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
		if (this.physicsBall != null)
			this.physicsBall.beforeUpdate(timeInterval);
		
		this.getPlayers().forEach(p -> p.beforeUpdate(timeInterval));

		this.xyWorld.updatev(timeInterval);
		this.yzWorld.updatev(timeInterval);

		if (this.physicsBall != null)
			this.physicsBall.afterUpdate(timeInterval);

		this.getPlayers().forEach(p -> p.afterUpdate(timeInterval));
	}
}
