package football.jef.core.physics;

import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import com.synerset.unitility.unitsystem.common.Angle;
import com.synerset.unitility.unitsystem.common.Velocity;

import football.jef.core.Conversions;
import football.jef.core.Football;
import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.VUnits;

public class PhysicsBall
{
	private final Football football;
	private final PhysicsBallXY xyBall;
	private final PhysicsBallYZ yzBall;
	private double xyVelocityRatio;

	public PhysicsBall(final Football football)
	{
		this.football = football;
		this.xyBall = new PhysicsBallXY(this.football);
		this.yzBall = new PhysicsBallYZ(this.football);
	}

	public void afterUpdate(final double timeInterval)
	{
		final Vector2 newXYLocation = this.xyBall.getTransform().getTranslation();
		final Vector2 newXYLinearVelocity = this.xyBall.getLinearVelocity();

		final Vector2 newYZLocation = this.yzBall.getTransform().getTranslation();
		final Vector2 newYZLinearVelocity = this.yzBall.getLinearVelocity();

		this.football.setLocation(Conversions.metersToYards(newXYLocation.x),
				(Conversions.metersToYards(newXYLocation.y) + Conversions.metersToYards(newYZLocation.x)) / 2,
				Conversions.metersToYards(newYZLocation.y));

		this.football.setLinearVelocity(Conversions.metersToYards(newXYLinearVelocity.y * this.xyVelocityRatio),
				(Conversions.metersToYards(newXYLinearVelocity.y) + Conversions.metersToYards(newYZLinearVelocity.x))
						/ 2,
				Conversions.metersToYards(newYZLinearVelocity.y));

		final double radians = this.football.getAngularVelocity().getRadiansPerSecond() * timeInterval;

		this.football.setAngularVelocity(
				new AngularVelocity((this.football.getAngularVelocity().getCurrentAngleInRadians() + radians) % Math.PI,
						this.football.getAngularVelocity().getRadiansPerSecond()));

		if (this.football.getLinearVelocity().getYZVelocity() == 0)
			this.football.setAngularVelocity(0.0, 0.0);
	}

	public void beforeUpdate(final double timeInterval)
	{
		this.applyDrag(timeInterval);

		final Vector2 vXy = new Vector2(Conversions.yardsToMeters(this.football.getLinearVelocity().getX()),
				Conversions.yardsToMeters(this.football.getLinearVelocity().getY()));
		this.xyBall.setLinearVelocity(vXy);

		final Vector2 lXy = new Vector2(Conversions.yardsToMeters(this.football.getLocation().getX()),
				Conversions.yardsToMeters(this.football.getLocation().getY()));
		Transform t = new Transform();
		t.translate(lXy);
		this.xyBall.setTransform(t);

		final Vector2 vYz = new Vector2(Conversions.yardsToMeters(this.football.getLinearVelocity().getY()),
				Conversions.yardsToMeters(this.football.getLinearVelocity().getZ()));
		this.yzBall.setLinearVelocity(vYz);

		final Vector2 lYz = new Vector2(Conversions.yardsToMeters(this.football.getLocation().getY()),
				Conversions.yardsToMeters(this.football.getLocation().getZ()));
		t = new Transform();
		t.translate(lYz);
		this.yzBall.setTransform(t);
	}

	public Football getFootball()
	{
		return this.football;
	}

	public PhysicsBallXY getXyBall()
	{
		return this.xyBall;
	}

	public PhysicsBallYZ getYzBall()
	{
		return this.yzBall;
	}

	public void kick(final LinearVelocity lv)
	{
		final double yLinearVelocity = lv.getY() == 0 ? 1 : lv.getY();
		final double xLinearVelocity = lv.getX();
		this.xyVelocityRatio = xLinearVelocity / yLinearVelocity;

		this.football.setAngularVelocity(new AngularVelocity(0, -20D));
		this.xyBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getX()), Conversions.yardsToMeters(lv.getY())));
		this.yzBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getY()), Conversions.yardsToMeters(lv.getZ())));
	}

	public void pass(final LinearVelocity lv)
	{
		this.xyBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getX()), Conversions.yardsToMeters(lv.getY())));
		this.yzBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getY()), Conversions.yardsToMeters(lv.getZ())));
	}

	public void punt(final LinearVelocity lv)
	{
		this.xyBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getX()), Conversions.yardsToMeters(lv.getY())));
		this.yzBall.applyForce(new Vector2(Conversions.yardsToMeters(lv.getY()), Conversions.yardsToMeters(lv.getZ())));
	}

	public void pass(double distance, Angle xAngle, double hangTime)
	{
		double z = hangTime / 1.13D * 110;
		double y = distance / hangTime * 6.77 * 1.2; // This formula just seems to work out.
		double x = distance * xAngle.cos();
		
		kick(new LinearVelocity(x, y, z));
	}

	public void kick(double distance, Angle xAngle, double hangTime)
	{
		double z = hangTime / 1.13D * 110;
		double y = distance / hangTime * 6.77 * 1.2; // This formula just seems to work out.
		double x = distance * xAngle.sin();
		
		kick(new LinearVelocity(x, y, z));
	}

	public void punt(double distance, Angle xAngle, double hangTime)
	{
		double z = hangTime / 1.13D * 100;
		double y = distance / hangTime * 6.77 * 13.4 / 14.0; // This formula just seems to work out.
		double x = distance * xAngle.sin();
		
		punt(new LinearVelocity(x, y, z));
	}

	private void applyDrag(final double deltaTime)
	{
		// drag for y
		final var dragForce = this.getYzBall().calculateDragForce();
		if (dragForce.isEqualZero())
			return;

		final Velocity currentVelocity = Velocity.of(this.football.getLinearVelocity().getY(), VUnits.YPS);

		// we include * Constants.TIMER_INTERVAL because drag is a rate of acceleration
		// per second and we deal in ticks
		final Velocity updatedVelocity = Velocity.ofMetersPerSecond(currentVelocity.getInMetersPerSecond()
				- (dragForce.div(PhysicsBallBase.mass.getInKilograms()).getInNewtons() * deltaTime));

		this.yzBall.setLinearDamping(currentVelocity.getInMetersPerSecond() - updatedVelocity.getInMetersPerSecond());
		this.xyBall.setLinearDamping(currentVelocity.getInMetersPerSecond() - updatedVelocity.getInMetersPerSecond());
	}

}
