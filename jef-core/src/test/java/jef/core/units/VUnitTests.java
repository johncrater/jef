package jef.core.units;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synerset.unitility.unitsystem.common.Velocity;

class VUnitTests
{

	@Test
	void test()
	{
		Velocity inYardsPerSecond = Velocity.of(30, VUnits.YPS);
		Velocity inKilometersPerHour = Velocity.ofKilometersPerHour(inYardsPerSecond.getInKilometersPerHours());
		inYardsPerSecond = Velocity.of(inKilometersPerHour.getInUnit(VUnits.YPS), VUnits.YPS);
		
		assertEquals(30, inYardsPerSecond.getInUnit(VUnits.YPS));
	}

}
