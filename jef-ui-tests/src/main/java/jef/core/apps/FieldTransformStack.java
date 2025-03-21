package jef.core.apps;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import jef.core.Conversions;
import jef.core.Location;
import jef.core.Field;
import jef.core.Location;
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
		
		final float scaleX = (float) (clientArea.width / Conversions.yardsToInches(Field.DIM_TOTAL_LENGTH));
		final float scaleY = (float) (clientArea.height / Conversions.yardsToInches(Field.DIM_TOTAL_WIDTH));
		this.scale = Math.min(scaleX, scaleY) * (float)zoomFactor;

		this.scale((float)scale, (float)scale);

		Location upperLeft = midfield.subtract(Field.MIDFIELD.divide(zoomFactor));
		Point pt = new Point((int)Conversions.yardsToInches(upperLeft.getX()), (int)Conversions.yardsToInches(upperLeft.getY()));
		this.translate(-pt.x, -pt.y);

		this.set();
	}

	public Location getUpperLeft()
	{
		return Field.MIDFIELD.subtract(getMidfield().multiply(getZoomFactor())).divide(2);
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

	@Override
	public void close() throws Exception
	{
		super.close();
		if (this.disposeGC)
			this.getGC().dispose();
	}

}
