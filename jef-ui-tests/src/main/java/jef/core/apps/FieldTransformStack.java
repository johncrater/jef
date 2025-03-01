package jef.core.apps;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import jef.core.Conversions;
import jef.core.Field;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.ui.swt.utils.TransformStack;

public class FieldTransformStack extends TransformStack
{
	private boolean disposeGC = false;
	private Rectangle clientArea;
	private double zoomFactor = 1.0;
	private double scale;
	private Location midfield;
	
	public FieldTransformStack(Canvas canvas, Location midfield, double zoomFactor)
	{
		this(canvas, new GC(canvas.getDisplay()), midfield, zoomFactor);
		this.disposeGC = true;
	}
	
	public FieldTransformStack(Canvas canvas, GC gc, Location midfield, double zoomFactor)
	{
		super(gc);
		
		this.clientArea = canvas.getClientArea();
		this.midfield = midfield;
		this.zoomFactor = zoomFactor;
		
		final float scaleX = (float) (clientArea.width / Conversions.yardsToInches(Field.FIELD_TOTAL_LENGTH));
		final float scaleY = (float) (clientArea.height / Conversions.yardsToInches(Field.FIELD_TOTAL_WIDTH));
		this.scale = Math.min(scaleX, scaleY) * (float)zoomFactor;

		this.scale((float)scale, (float)scale);

		Location upperLeft = midfield.subtract(Field.MIDFIELD.divide(zoomFactor));
		Point pt = new Point((int)Conversions.yardsToInches(upperLeft.getX()), (int)Conversions.yardsToInches(upperLeft.getY()));
		this.translate(-pt.x, -pt.y);

		this.set();
	}

	public void translate(Location loc)
	{
		Point pt = this.locationToScreen(loc);
		this.translate(pt.x, pt.y);
	}
	
	public Location getUpperLeft()
	{
		return Field.MIDFIELD.subtract(getMidfield().multiply(getZoomFactor())).divide(2);
//		upperLeft = new DefaultLocation(upperLeft.getX() / zoomFactor, upperLeft.getY() / zoomFactor);
//		return upperLeft;
	}
	
	public Rectangle getClientArea()
	{
		return this.clientArea;
	}

	public double getZoomFactor()
	{
		return this.zoomFactor;
	}

	public double getScale()
	{
		return this.scale;
	}

	public Location getMidfield()
	{
		return this.midfield;
	}

	public Point locationToScreen(Location location)
	{
		location = location.add(getUpperLeft());
		return new Point(yardsToPixels(location.getX()), yardsToPixels(location.getY()));
	}
	
	public Location screenToLocation(Point p)
	{
		return new DefaultLocation(Conversions.inchesToYards(p.x / getScale()), Conversions.inchesToYards(p.y / getScale())).subtract(this.getUpperLeft());
	}
	
	public int yardsToPixels(double yards)
	{
		return (int)(Conversions.yardsToInches(yards));
	}
	
	@Override
	public void close() throws Exception
	{
		super.close();
		if (this.disposeGC)
			this.getGC().dispose();
	}

}
