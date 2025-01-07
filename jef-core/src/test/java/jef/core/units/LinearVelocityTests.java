package jef.core.units;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;

import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;

class LinearVelocityTests
{

	@Test
	void testWithinEpsilon()
	{
		assertTrue(LinearVelocity.equals(LinearVelocity.EPSILON, LinearVelocity.EPSILON));
		assertTrue(LinearVelocity.equals(0, LinearVelocity.EPSILON - .00000001));
		assertFalse(LinearVelocity.equals(0, LinearVelocity.EPSILON + .00000001));

		assertTrue(LinearVelocity.equals(-LinearVelocity.EPSILON, -LinearVelocity.EPSILON));
		assertFalse(LinearVelocity.equals(-LinearVelocity.EPSILON - .00000001, 0));
		assertTrue(LinearVelocity.equals(-LinearVelocity.EPSILON + .00000001, 0));
	}

	@Test
	void testLinearVelocityVector3D()
	{
		// elevation and azimuth are reversed in Vector3D constructor from LV
		Vector3D vector = new Vector3D(Math.PI / 2, Math.PI / 4);
		LinearVelocity lv = new DefaultLinearVelocity(vector);

		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.PI / 2, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(1, lv.getSpeed()));

		vector = new Vector3D(Math.cos(Math.toRadians(37)), Math.sin(Math.toRadians(37)), 0);
		lv = new DefaultLinearVelocity(vector);

		assertTrue(LinearVelocity.equals(0, Math.toDegrees(lv.getElevation())));
		assertTrue(LinearVelocity.equals(37, Math.toDegrees(lv.getAzimuth())));
		assertTrue(LinearVelocity.equals(1, lv.getSpeed()));

		vector = new Vector3D(Math.cos(Math.toRadians(37)), 0, Math.sin(Math.toRadians(37)));
		lv = new DefaultLinearVelocity(vector);

		assertTrue(LinearVelocity.equals(37, Math.toDegrees(lv.getElevation())));
		assertTrue(LinearVelocity.equals(0, Math.toDegrees(lv.getAzimuth())));
		assertTrue(LinearVelocity.equals(1, lv.getSpeed()));

		vector = new Vector3D(0, Math.cos(Math.toRadians(37)), Math.sin(Math.toRadians(37)));
		lv = new DefaultLinearVelocity(vector);

		assertTrue(LinearVelocity.equals(37, Math.toDegrees(lv.getElevation())));
		assertTrue(LinearVelocity.equals(90, Math.toDegrees(lv.getAzimuth())));
		assertTrue(LinearVelocity.equals(1, lv.getSpeed()));

		double radians = Math.toRadians(37);
		double x = Math.cos(radians);
		double y = Math.sin(radians);
		double z = Math.sin(radians) / Math.cos(radians);
		
		double magnitude = Math.sqrt(x * x + y * y + z * z);
		
		vector = new Vector3D(x * 1 / magnitude, y * 1 / magnitude, z * 1 / magnitude);
		
		lv = new DefaultLinearVelocity(vector);

		assertTrue(LinearVelocity.equals(37, Math.toDegrees(lv.getElevation())));
		assertTrue(LinearVelocity.equals(37, Math.toDegrees(lv.getAzimuth())));
		assertTrue(LinearVelocity.equals(1, lv.getSpeed()));
	}

	@Test
	void testLinearVelocityDoubleDoubleDouble()
	{
		LinearVelocity lv = new DefaultLinearVelocity(0, 0, 0);
		assertTrue(LinearVelocity.equals(0, lv.getElevation()));
		assertTrue(LinearVelocity.equals(0, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(0, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, Math.PI / 4, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(7 * Math.PI / 8, Math.PI / 4, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 8, lv.getElevation()));
		assertTrue(LinearVelocity.equals(-3 * Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, 5 * Math.PI / 4, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(-3 * Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, Math.PI / 4, -10);
		assertTrue(LinearVelocity.equals(-Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(-3 * Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(15 * Math.PI + Math.PI / 4, Math.PI / 4, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(-3 * Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(16 * Math.PI + Math.PI / 4, Math.PI / 4, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, 26 * Math.PI, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(0, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, 27 * Math.PI, 10);
		assertTrue(LinearVelocity.equals(Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.PI, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));

		lv = new DefaultLinearVelocity(Math.PI / 4, Math.PI, -10);
		assertTrue(LinearVelocity.equals(-Math.PI / 4, lv.getElevation()));
		assertTrue(LinearVelocity.equals(0, lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(10, lv.getSpeed()));
	}

	@Test
	void testAddDouble()
	{
		LinearVelocity lv = new DefaultLinearVelocity(Math.toRadians(37), Math.toRadians(37), 4);
		lv = lv.add(3.5);
		
		assertTrue(LinearVelocity.equals(Math.toRadians(37), lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.toRadians(37), lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(7.5, lv.getSpeed()));

		lv = lv.add(-3.5);

		assertTrue(LinearVelocity.equals(Math.toRadians(37), lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.toRadians(37), lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(4.0, lv.getSpeed()));

		lv = lv.add(-7);

		assertTrue(LinearVelocity.equals(Math.toRadians(-37), lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.toRadians(-143), lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(3.0, lv.getSpeed()));

		lv = lv.add(-lv.getSpeed());

		assertTrue(LinearVelocity.equals(Math.toRadians(-37), lv.getElevation()));
		assertTrue(LinearVelocity.equals(Math.toRadians(-143), lv.getAzimuth()));
		assertTrue(LinearVelocity.equals(0, lv.getSpeed()));

	}

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
//	void testGetSpeed()
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
//	void testGetXYSpeed()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetXZSpeed()
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
//	void testGetYZSpeed()
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
