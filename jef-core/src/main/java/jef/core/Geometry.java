package jef.core;

import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public class Geometry
{
	public static Coordinate calculateIntersection(LineSegment ls1, LineSegment ls2)
	{
		RobustLineIntersector intersector = new RobustLineIntersector();
		intersector.computeIntersection(ls1.p0, ls1.p1, ls2.p0, ls2.p1);
		int intersectionNum = intersector.getIntersectionNum();
		if (intersectionNum != 1)
			return intersector.getIntersection(0);
		
		return null;
	}
}
