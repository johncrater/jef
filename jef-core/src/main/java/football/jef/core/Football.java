package football.jef.core;

import com.synerset.unitility.unitsystem.common.Area;
import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.MassUnits;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.Location;

public interface Football
{
	public static final Distance lengthOfTheMajorAxis = Distance.ofInches(11.25f);
	public static final Distance lengthOfTheMinorAxis = Distance.ofInches(6.70f);
	
	public static final double estimatedCoefficientOfFriction = .41f;

	// supposed to be around .8. But this seems to bounce way too high. It may have
	// more to do with
	// the grass field which I have read will result in a .6 to .75 COR.
	// .4 seems more accurate.
	public static final double coefficientOfRestitution = .5f;
	public static final Mass mass = Mass.of(14.5, MassUnits.OUNCE);
	public static final Density density = Density.ofKilogramPerCubicMeter(.1);
	public static final Area areaOfMajorAxis = Area.ofSquareInches(
			((Football.lengthOfTheMajorAxis.getInInches() / 2) * Math.PI * Football.lengthOfTheMinorAxis.getInInches())
					/ 2);
	public static final Area areaOfMinorAxis = Area
			.ofSquareInches(Math.PI * Football.lengthOfTheMinorAxis.getInInches());
	public static final double dragCoefficientSpiral = .19f;
	public static final double dragCoefficientEndOverEnd = (.75f + Football.dragCoefficientSpiral) / 2;

	public Football adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond);
	public Football adjustLinearVelocity(Double x, Double y, Double z);
	public Football adjustLocation(Double x, Double y, Double z);

	public AngularVelocity getAngularVelocity();
	public LinearVelocity getLinearVelocity();
	public Location getLocation();

	public Football setAngularVelocity(AngularVelocity angularVelocity);
	public Football setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);
	public Football setLinearVelocity(LinearVelocity lv);
	public Football setLinearVelocity(Double x, Double y, Double z);
	public Football setLocation(Location location);
	public Football setLocation(Double x, Double y, Double z);
}
