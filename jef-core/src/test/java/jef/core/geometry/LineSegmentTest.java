package jef.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jef.core.Field;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;

class LineSegmentTest
{

	@Test
	void testLineSegment()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(10, -3, 7), new DefaultLocation(2, 52, 27));

		Assertions.assertEquals(10, ls1.getLoc1().getX());
		Assertions.assertEquals(-3, ls1.getLoc1().getY());
		Assertions.assertEquals(7, ls1.getLoc1().getZ());

		Assertions.assertEquals(2, ls1.getLoc2().getX());
		Assertions.assertEquals(52, ls1.getLoc2().getY());
		Assertions.assertEquals(27, ls1.getLoc2().getZ());
	}

	@Test
	void testGetLength()
	{
		LineSegment ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(2, 0, 0));
		Assertions.assertEquals(2, ls1.getLength());

		ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(0, 2, 0));
		Assertions.assertEquals(2, ls1.getLength());

		ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(0, 0, 2));
		Assertions.assertEquals(2, ls1.getLength());

		ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(2, 2, 2));
		Assertions.assertEquals(Math.sqrt(4 + 4 + 4), ls1.getLength());
	}

	@Test
	void testGetDirection()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(0, 2, 0));
		Assertions.assertEquals(round(Math.PI / 2), round(ls1.getDirection().getAzimuth()));
	}

	private double round(final double x)
	{
		return Math.round(1000.0 * x) / 1000.0;
	}

	@Test
	void testGetPoint()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(2, 2, 2));
		Location loc1 = ls1.getPoint(.5);
		assertEquals(new DefaultLocation(1, 1, 1), loc1);
	}

	@Test
	void testGetPerpendicularLine()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(2, 2, 2));
		LineSegment ls2 = ls1.getPerpendicularLine(ls1.getPoint(.5), ls1.getLength());
		
		assertEquals(round(Math.PI * 3 / 4), round(ls2.getLoc1().angleTo(ls2.getLoc2())));
	}

	@Test
	void testGetSlope()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(0, 0, 0), new DefaultLocation(2, 2, 2));
		double d = ls1.getXYSlope();
		assertEquals(1, d);
	}

	@Test
	void testGetYIntercept()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(-1, -1, -1), new DefaultLocation(3, 3, 3));
		double d = ls1.getYIntercept();
		assertEquals(0, d);
	}

	@Test
	void testGetXIntercept()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(-1, -1, -1), new DefaultLocation(3, 3, 3));
		double d = ls1.getXIntercept();
		assertEquals(0, Math.abs(d));  // -0.0 ??
	}

	@Test
	void testToStandardForm()
	{
		final LineSegment ls1 = new LineSegment(new DefaultLocation(-1, -3, 0), new DefaultLocation(11, 6, 0));
		Vector v1 = ls1.toStandardForm();
		assertEquals(3.0/4.0, ls1.getXYSlope());
		assertEquals(-2.25, ls1.getYIntercept());
	}

	@Test
	void testXyIntersectionLineSegmentBoolean()
	{
		LineSegment ls1 = new LineSegment(new DefaultLocation(-2, 2, 0), new DefaultLocation(6, 10, 0));
		LineSegment ls2 = new LineSegment(new DefaultLocation(0, 8, 0), new DefaultLocation(4, -4, 0));
		Location loc = ls1.xyIntersection(ls2);
		assertTrue(loc.equals(new DefaultLocation(1, 5, 0)));

		ls1 = new LineSegment(new DefaultLocation(2, 6, 0), new DefaultLocation(6, 10, 0));
		ls2 = new LineSegment(new DefaultLocation(0, 8, 0), new DefaultLocation(4, -4, 0));
		loc = ls1.xyIntersection(ls2);
		
		assertNull(loc);
}

	@Test
	void testIntersectsLocation()
	{
		LineSegment ls1 = new LineSegment(new DefaultLocation(-2, 2, 0), new DefaultLocation(6, 10, 0));
		boolean ret = ls1.intersects(new DefaultLocation(5, 9, 0));
		assertTrue(ret);

		ret = ls1.intersects(new DefaultLocation(5, 10, 0));
		assertFalse(ret);
	}

	@Test
	void testIntersectsPlane()
	{
		LineSegment ls1 = new LineSegment(new DefaultLocation(-2, 2, 0), new DefaultLocation(7, 11, 8));
		Plane plane = new Plane(new DefaultLocation(0, 0, 4), Vector.fromCartesianCoordinates(0, 0, 1));
		
		Location loc = ls1.intersects(plane);
		assertEquals(new DefaultLocation(2.5, 6.5, 4), loc);
	}

	@Test
	void testRestrictToBetweenEndZones()
	{
		Location l1 = new DefaultLocation(20, 40);
		Location l2 = new DefaultLocation(30, 50);
		LineSegment ls1 = new LineSegment(l1, l2);
		LineSegment ls2 = ls1.restrictToBetweenEndZones(true);
		
		assertEquals(new DefaultLocation(20, 40), ls2.getLoc1());
		assertEquals(new DefaultLocation(30, 50), ls2.getLoc2());

		l1 = new DefaultLocation(20, -10);
		l2 = new DefaultLocation(20, 100);
		ls1 = new LineSegment(l1, l2);
		ls2 = ls1.restrictToBetweenEndZones(true);
		
		assertEquals(new DefaultLocation(20, Field.FIELD_BORDER_WIDTH), ls2.getLoc2());
		assertEquals(new DefaultLocation(20, Field.FIELD_TOTAL_WIDTH - Field.FIELD_BORDER_WIDTH), ls2.getLoc1());
	}

}
