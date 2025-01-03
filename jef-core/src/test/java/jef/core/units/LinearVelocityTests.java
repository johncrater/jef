package jef.core.units;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;

import jef.core.LinearVelocity;

class LinearVelocityTests
{

	@Test
	void testWithinEpsilon()
	{
		assertTrue(DefaultLinearVelocity.withinEpsilon(LinearVelocity.EPSILON, LinearVelocity.EPSILON));
		assertTrue(DefaultLinearVelocity.withinEpsilon(0, LinearVelocity.EPSILON - .00000001));
		assertFalse(DefaultLinearVelocity.withinEpsilon(0, LinearVelocity.EPSILON + .00000001));

		assertTrue(DefaultLinearVelocity.withinEpsilon(-LinearVelocity.EPSILON, -LinearVelocity.EPSILON));
		assertFalse(DefaultLinearVelocity.withinEpsilon(-LinearVelocity.EPSILON - .00000001, 0));
		assertTrue(DefaultLinearVelocity.withinEpsilon(-LinearVelocity.EPSILON + .00000001, 0));
	}

	@Test
	void testLinearVelocityVector3D()
	{
		Vector3D vector = new Vector3D(Math.PI / 4, Math.PI / 2);
		LinearVelocity lv = new DefaultLinearVelocity(vector);
		
		assertEquals(Math.PI / 4, lv.getElevation());
		assertEquals(Math.PI / 2, lv.getAzimuth());
		assertEquals(1, lv.getDistance());
	}
//
//	@Test
//	void testLinearVelocityDoubleDoubleDouble()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddDouble()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddDoubleDoubleDouble()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddLinearVelocity()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testEqualsObject()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetElevation()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetAzimuth()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetDistance()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetX()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetXYDistance()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetXZDistance()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetY()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetYZDistance()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetZ()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testIsCloseToZero()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testMovingLeft()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testMovingRight()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testMultiply()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testNormalize()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testSet()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testSubtract()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testToVector3D()
//	{
//		fail("Not yet implemented");
//	}
//
}
