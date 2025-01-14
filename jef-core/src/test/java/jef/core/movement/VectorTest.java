package jef.core.movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jef.core.geometry.Vector;

class VectorTest
{

	@Test
	void testFromCartesianCoordinates()
	{
		Vector v1 = Vector.fromCartesianCoordinates(1.1, 2.2, 3.3);
		Vector v2 = Vector.fromPolarCoordinates(v1.getAzimuth(), v1.getElevation(), v1.getDistance());
		Vector v3 = Vector.fromCartesianCoordinates(v2.getX(), v2.getY(), v2.getZ());
		
		assertEquals(1.1, v1.getX());
		assertEquals(2.2, v1.getY());
		assertEquals(3.3, v1.getZ());
		
		assertEquals(round(v1.getX()), round(v2.getX()));
		assertEquals(round(v1.getY()), round(v2.getY()));
		assertEquals(round(v1.getZ()), round(v2.getZ()));
		
		assertEquals(round(v1.getAzimuth()), round(v2.getAzimuth()));
		assertEquals(round(v1.getElevation()), round(v2.getElevation()));
		assertEquals(round(v1.getDistance()), round(v2.getDistance()));

		assertEquals(round(v2.getX()), round(v3.getX()));
		assertEquals(round(v2.getY()), round(v3.getY()));
		assertEquals(round(v2.getZ()), round(v3.getZ()));

		assertEquals(round(v2.getAzimuth()), round(v3.getAzimuth()));
		assertEquals(round(v2.getElevation()), round(v3.getElevation()));
		assertEquals(round(v2.getDistance()), round(v3.getDistance()));

		v1 = Vector.fromCartesianCoordinates(0, 0, 0);
		v2 = Vector.fromPolarCoordinates(v1.getAzimuth(), v1.getElevation(), v1.getDistance());
		v3 = Vector.fromCartesianCoordinates(v2.getX(), v2.getY(), v2.getZ());
		
		assertEquals(0, v1.getX());
		assertEquals(0, v1.getY());
		assertEquals(0, v1.getZ());
		
		assertEquals(round(v1.getX()), round(v2.getX()));
		assertEquals(round(v1.getY()), round(v2.getY()));
		assertEquals(round(v1.getZ()), round(v2.getZ()));
		
		assertEquals(round(v1.getAzimuth()), round(v2.getAzimuth()));
		assertEquals(round(v1.getElevation()), round(v2.getElevation()));
		assertEquals(round(v1.getDistance()), round(v2.getDistance()));

		assertEquals(round(v2.getX()), round(v3.getX()));
		assertEquals(round(v2.getY()), round(v3.getY()));
		assertEquals(round(v2.getZ()), round(v3.getZ()));

		assertEquals(round(v2.getAzimuth()), round(v3.getAzimuth()));
		assertEquals(round(v2.getElevation()), round(v3.getElevation()));
		assertEquals(round(v2.getDistance()), round(v3.getDistance()));
	}

	@Test
	void testFromPolarCoordinates()
	{
		Vector v1 = Vector.fromPolarCoordinates(2 * Math.PI / 3, Math.PI / 5, 5);
		Vector v2 = Vector.fromCartesianCoordinates(v1.getX(), v1.getY(), v1.getZ());
		Vector v3 = Vector.fromPolarCoordinates(v2.getAzimuth(), v2.getElevation(), v2.getDistance());

		assertEquals(round(Math.PI / 5), round(v1.getElevation()));
		assertEquals(round(2 * Math.PI / 3), round(v1.getAzimuth()));
		assertEquals(round(5), round(v1.getDistance()));
		
		assertEquals(round(v1.getX()), round(v2.getX()));
		assertEquals(round(v1.getY()), round(v2.getY()));
		assertEquals(round(v1.getZ()), round(v2.getZ()));
		
		assertEquals(round(v1.getAzimuth()), round(v2.getAzimuth()));
		assertEquals(round(v1.getElevation()), round(v2.getElevation()));
		assertEquals(round(v1.getDistance()), round(v2.getDistance()));

		assertEquals(round(v2.getX()), round(v3.getX()));
		assertEquals(round(v2.getY()), round(v3.getY()));
		assertEquals(round(v2.getZ()), round(v3.getZ()));

		assertEquals(round(v2.getAzimuth()), round(v3.getAzimuth()));
		assertEquals(round(v2.getElevation()), round(v3.getElevation()));
		assertEquals(round(v2.getDistance()), round(v3.getDistance()));

		v1 = Vector.fromPolarCoordinates(0, 0, 0);
		v2 = Vector.fromCartesianCoordinates(v1.getX(), v1.getY(), v1.getZ());
		v3 = Vector.fromPolarCoordinates(v2.getAzimuth(), v2.getElevation(), v2.getDistance());

		assertEquals(round(0), round(v1.getElevation()));
		assertEquals(round(0), round(v1.getAzimuth()));
		assertEquals(round(0), round(v1.getDistance()));
		
		assertEquals(round(v1.getX()), round(v2.getX()));
		assertEquals(round(v1.getY()), round(v2.getY()));
		assertEquals(round(v1.getZ()), round(v2.getZ()));
		
		assertEquals(round(v1.getAzimuth()), round(v2.getAzimuth()));
		assertEquals(round(v1.getElevation()), round(v2.getElevation()));
		assertEquals(round(v1.getDistance()), round(v2.getDistance()));

		assertEquals(round(v2.getX()), round(v3.getX()));
		assertEquals(round(v2.getY()), round(v3.getY()));
		assertEquals(round(v2.getZ()), round(v3.getZ()));

		assertEquals(round(v2.getAzimuth()), round(v3.getAzimuth()));
		assertEquals(round(v2.getElevation()), round(v3.getElevation()));
		assertEquals(round(v2.getDistance()), round(v3.getDistance()));
	}

	@Test
	void testFromVector()
	{
		Vector v = Vector.fromVector(Vector.fromCartesianCoordinates(1.1, 2.2, 3.3));
		assertEquals(1.1, v.getX());
		assertEquals(2.2, v.getY());
		assertEquals(3.3, v.getZ());
	}

	@Test
	void testNormalize()
	{
		Vector v = Vector.fromCartesianCoordinates(23, 37, 24);
		v = v.normalize();
		assertEquals(1.0, v.getDistance());
	}

	@Test
	void testAddVector()
	{
		Vector v1 = Vector.fromCartesianCoordinates(23, 37, 24);
		Vector v2 = Vector.fromCartesianCoordinates(27, 45, 89);
		
		Vector v3 = v1.add(v2);
		assertEquals(50, v3.getX());
		assertEquals(82, v3.getY());
		assertEquals(113, v3.getZ());
	}

	@Test
	void testSubtract()
	{
		Vector v1 = Vector.fromCartesianCoordinates(23, 37, 24);
		Vector v2 = Vector.fromCartesianCoordinates(27, 45, 89);
		
		Vector v3 = v1.subtract(v2);
		assertEquals(-4, v3.getX());
		assertEquals(-8, v3.getY());
		assertEquals(-65, v3.getZ());
	}

	@Test
	void testAddDoubleDoubleDouble()
	{
		Vector v1 = Vector.fromCartesianCoordinates(23, 37, 24);
		
		Vector v2 = v1.add(27, 45, 89);
		assertEquals(50, v2.getX());
		assertEquals(82, v2.getY());
		assertEquals(113, v2.getZ());
	}

	@Test
	void testNegate()
	{
		Vector v1 = Vector.fromCartesianCoordinates(23, 37, 24);
		Vector v2 = v1.negate();
		assertEquals(-23, v2.getX());
		assertEquals(-37, v2.getY());
		assertEquals(-24, v2.getZ());
	}

	@Test
	void testGetElevation()
	{
		Vector v1 = Vector.fromPolarCoordinates(Math.PI /2, Math.PI / 4, 5);
		assertEquals(round(Math.PI / 4), round(v1.getElevation()));
	}

	@Test
	void testGetAzimuth()
	{
		Vector v1 = Vector.fromPolarCoordinates(Math.PI /2, Math.PI / 4, 5);
		assertEquals(Math.PI / 2, v1.getAzimuth());
	}

	@Test
	void testGetDistance()
	{
		Vector v1 = Vector.fromPolarCoordinates(Math.PI /2, Math.PI / 4, 5);
		assertEquals(5, v1.getDistance());
	}

	@Test
	void testMultiply()
	{
		Vector v1 = Vector.fromCartesianCoordinates(23, 37, 24);
		Vector v2 = v1.multiply(10);
		assertEquals(230, v2.getX());
		assertEquals(370, v2.getY());
		assertEquals(240, v2.getZ());
	}

	@Test
	void testDistanceBetween()
	{
		Vector v1 = Vector.fromCartesianCoordinates(1, 1, 0);
		Vector v2 = Vector.fromCartesianCoordinates(-1, -1, 0);
		assertEquals(2 * Math.sqrt(2), v1.distanceBetween(v2));
	}

	@Test
	void testXyAngle()
	{
		Vector v1 = Vector.fromCartesianCoordinates(3, 2, 1);
		Vector v2 = Vector.fromCartesianCoordinates(-7, 6, -5);
		
		assertEquals(2.761086276477428, v1.xyAngle(v2));
	}

	@Test
	void testOrthogonal2D()
	{
		Vector v1 = Vector.fromPolarCoordinates(Math.PI / 4, Math.PI / 4, 1);
		Vector v2 = v1.orthogonal2D();
		assertEquals(3 * Math.PI / 4, v2.getAzimuth());
	}

	@Test
	void testDot()
	{
		Vector v1 = Vector.fromCartesianCoordinates(1, 2, 1);
		Vector v2 = Vector.fromCartesianCoordinates(3, 2, 1);
		
		double dot = v1.dot(v2);
		assertEquals(8, dot);
	}

	@Test
	void testEqualsObject()
	{
		Vector v1 = Vector.fromCartesianCoordinates(1, 2, 1);
		Vector v2 = Vector.fromCartesianCoordinates(1, 2, 1);
		
		assertTrue(v1.equals(v2));
	}

	private double round(double x)
	{
		return Math.round(1000.0 * x) / 1000.0;
	}
}
