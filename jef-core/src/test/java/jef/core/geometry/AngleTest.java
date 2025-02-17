package jef.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jef.core.Field;

class AngleTest
{
	@Test
	void testBisect()
	{
		Angle angle = new Angle(Field.MIDFIELD, Vector.fromPolarCoordinates(-Math.PI / 2, 0, 10), Vector.fromPolarCoordinates(Math.PI, 0, 10));
		Vector bisection = angle.bisect();
		assertEquals(round(-3 * Math.PI / 4), round(bisection.getAzimuth()));

		angle = new Angle(Field.MIDFIELD, Vector.fromPolarCoordinates(Math.PI / 2, 0, 10), Vector.fromPolarCoordinates(Math.PI, 0, 10));
		bisection = angle.bisect();
		assertEquals(round(3 * Math.PI / 4), round(bisection.getAzimuth()));

		angle = new Angle(Field.MIDFIELD, Vector.fromPolarCoordinates(Math.PI / 4, 0, 10), Vector.fromPolarCoordinates(Math.PI / 6, 0, 10));
		bisection = angle.bisect();
		assertEquals(round(5 * Math.PI / 24), round(bisection.getAzimuth()));

		angle = new Angle(Field.MIDFIELD, Vector.fromPolarCoordinates(-Math.PI / 4, 0, 10), Vector.fromPolarCoordinates(Math.PI / 6, 0, 10));
		bisection = angle.bisect();
		assertEquals(round(-Math.PI / 24), round(bisection.getAzimuth()));
	}

	
	private double round(double x)
	{
		return Math.round(1000.0 * x) / 1000.0;
	}
}
