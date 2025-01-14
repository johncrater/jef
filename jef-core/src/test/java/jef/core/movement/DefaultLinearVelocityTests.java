package jef.core.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jef.core.geometry.Vector;

class DefaultLinearVelocityTests
{

	@Test
	void testDefaultLinearVelocity()
	{
		DefaultLinearVelocity lv = new DefaultLinearVelocity();
		
		assertEquals(0, lv.getAzimuth());
		assertEquals(0, lv.getElevation());
		assertEquals(0, lv.getSpeed());

		assertEquals(0, lv.getX());
		assertEquals(0, lv.getY());
		assertEquals(0, lv.getZ());
	}

	@Test
	void testDefaultLinearVelocityDoubleDoubleDouble()
	{
		DefaultLinearVelocity lv1 = new DefaultLinearVelocity(2 * Math.PI / 3, Math.PI / 5, 5);
		DefaultLinearVelocity lv2 = new DefaultLinearVelocity(lv1.getAzimuth(), lv1.getElevation(), lv1.getSpeed());

		assertEquals(round(Math.PI / 5), round(lv1.getElevation()));
		assertEquals(round(2 * Math.PI / 3), round(lv1.getAzimuth()));
		assertEquals(round(5), round(lv1.getSpeed()));
		
		assertEquals(round(lv1.getX()), round(lv2.getX()));
		assertEquals(round(lv1.getY()), round(lv2.getY()));
		assertEquals(round(lv1.getZ()), round(lv2.getZ()));
		
		assertEquals(round(lv1.getAzimuth()), round(lv2.getAzimuth()));
		assertEquals(round(lv1.getElevation()), round(lv2.getElevation()));
		assertEquals(round(lv1.getSpeed()), round(lv2.getSpeed()));
	}

	@Test
	void testDefaultLinearVelocityVector()
	{
		Vector v1 = Vector.fromCartesianCoordinates(1, 2, 3);
		DefaultLinearVelocity lv1 = new DefaultLinearVelocity(v1);
		
		assertEquals(round(v1.getX()), round(lv1.getX()));
		assertEquals(round(v1.getY()), round(lv1.getY()));
		assertEquals(round(v1.getZ()), round(lv1.getZ()));
		
		assertEquals(round(v1.getAzimuth()), round(lv1.getAzimuth()));
		assertEquals(round(v1.getElevation()), round(lv1.getElevation()));
		assertEquals(round(v1.getDistance()), round(lv1.getSpeed()));
	}

	@Test
	void testAddDouble()
	{
		DefaultLinearVelocity lv1 = new DefaultLinearVelocity(Math.PI / 2, Math.PI / 4, 10);
		LinearVelocity lv2 = lv1.add(8);
		
		assertEquals(18, lv2.getSpeed());
	}

	@Test
	void testAddLinearVelocity()
	{
		DefaultLinearVelocity lv1 = new DefaultLinearVelocity(Math.PI / 2, Math.PI / 4, 10);
		LinearVelocity lv2 = lv1.add(new DefaultLinearVelocity(Math.PI / 2, Math.PI / 4, 10));
		
		assertEquals(round(lv1.getAzimuth()), round(lv2.getAzimuth()));
		assertEquals(round(lv1.getElevation()), round(lv2.getElevation()));
		assertEquals(round(lv2.getSpeed()), 20);
	}

	@Test
	void testCloseEnoughTo()
	{
		DefaultLinearVelocity lv1 = new DefaultLinearVelocity(Math.PI / 2, Math.PI / 4, 10);
		DefaultLinearVelocity lv2 = new DefaultLinearVelocity(Math.PI / 2, Math.PI / 4, 10.001);
		
		assertTrue(lv1.closeEnoughTo(lv2));
	}

	@Test
	void testDotProduct()
	{
		fail("Not yet implemented");
	}

	@Test
	void testEqualsObject()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetAzimuth()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetElevation()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetSpeed()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetX()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetXYSpeed()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetXZSpeed()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetY()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetYZSpeed()
	{
		fail("Not yet implemented");
	}

	@Test
	void testGetZ()
	{
		fail("Not yet implemented");
	}

	@Test
	void testIsNotMoving()
	{
		fail("Not yet implemented");
	}

	@Test
	void testMovingLeft()
	{
		fail("Not yet implemented");
	}

	@Test
	void testMovingRight()
	{
		fail("Not yet implemented");
	}

	@Test
	void testMultiply()
	{
		fail("Not yet implemented");
	}

	@Test
	void testNegate()
	{
		fail("Not yet implemented");
	}

	@Test
	void testNewFrom()
	{
		fail("Not yet implemented");
	}

	@Test
	void testNormalize()
	{
		fail("Not yet implemented");
	}

	@Test
	void testSubtract()
	{
		fail("Not yet implemented");
	}

	@Test
	void testToVector()
	{
		fail("Not yet implemented");
	}


	private double round(double x)
	{
		return Math.round(1000.0 * x) / 1000.0;
	}
}
