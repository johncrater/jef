package jef.core.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.movement.DUnits;

public class DUnitsTests
{
	@Test
	void test1()
	{
		Distance inYards = Distance.of(30, DUnits.YARD);
		double meters = inYards.getInMeters();
		
		assertEquals(27.432, meters);
	}

	@Test
	void test2()
	{
		Distance inMeters = Distance.ofMeters(27.432);
		double yards = inMeters.getInUnit(DUnits.YARD);
		
		assertEquals(30, yards);
	}
}
