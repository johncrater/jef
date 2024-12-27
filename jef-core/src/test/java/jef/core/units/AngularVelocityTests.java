package jef.core.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AngularVelocityTests
{

	@Test
	void testIsCloseToZero()
	{
		AngularVelocity av = new AngularVelocity();
		assertTrue(av.isCloseToZero());
		
		av = new AngularVelocity(0, .01);
		assertFalse(av.isCloseToZero());
		
		av = new AngularVelocity(0, .009);
		assertTrue(av.isCloseToZero());
		
		av = new AngularVelocity(0, .011);
		assertFalse(av.isCloseToZero());
	}

	@Test
	void testAdjust()
	{
		AngularVelocity av = new AngularVelocity();
		av = av.adjust(Math.PI, Math.PI);
		assertEquals(Math.PI, av.getCurrentAngleInRadians());
		assertEquals(Math.PI, av.getRadiansPerSecond());
		
		av = av.adjust(Math.PI / 2, Math.PI / 2);
		assertEquals(-Math.PI / 2, av.getCurrentAngleInRadians());
		assertEquals(-Math.PI / 2, av.getRadiansPerSecond());
		
		av = av.adjust(Math.PI * 2, Math.PI * 2);
		assertEquals(-Math.PI / 2, av.getCurrentAngleInRadians());
		assertEquals(-Math.PI / 2, av.getRadiansPerSecond());
		
		av = av.adjust(-Math.PI / 2, -Math.PI / 2);
		assertEquals(-Math.PI, av.getCurrentAngleInRadians());
		assertEquals(-Math.PI, av.getRadiansPerSecond());
	}

	@Test
	void testMultiply()
	{
		AngularVelocity av = new AngularVelocity();
		av = av.multiply(1);
		assertEquals(0, av.getCurrentAngleInRadians());
		assertEquals(0, av.getRadiansPerSecond());

		av = new AngularVelocity(Math.PI, Math.PI);
		av = av.multiply(1.7);
		assertEquals(Math.PI, av.getCurrentAngleInRadians());
		assertEquals(-.3 * Math.PI, av.getRadiansPerSecond());
	
	}

}
