package jef.core.ui.swt.utils;


import java.util.Stack;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

import jef.core.Conversions;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;


public class TransformStack implements AutoCloseable
{
	private Stack<Transform> stack = new Stack<>();
	private Transform currentTransform;
	private GC gc;

	public TransformStack(GC gc)
	{
		this.gc = gc;
		this.gc.setAdvanced(true);
		currentTransform = new Transform(gc.getDevice());
		gc.getTransform(currentTransform);
		push();
	}

	public Transform getCurrentTransform()
	{
		return this.currentTransform;
	}
	
	public GC getGC()
	{
		return this.gc;
	}
	
	public void clear()
	{
		currentTransform = new Transform(gc.getDevice());
		gc.setTransform(currentTransform);
	}
	
	@Override
	public void close() throws Exception
	{
		currentTransform.dispose();
		while (stack.size() > 0)
			pop();
	}

	public void getElements(float [] elements)
	{
		this.currentTransform.getElements(elements);
	}
	
	public void identity()
	{
		this.currentTransform.identity();
	}

	public void invert()
	{
		this.currentTransform.invert();
	}
	
	public boolean isIdentity()
	{
		return this.currentTransform.isIdentity();
	}

	public void multiply(Transform t)
	{
		this.currentTransform.multiply(t);
	}

	public void pop()
	{
		currentTransform.dispose();
		currentTransform = stack.pop();
		set();
	}
	
	public void push()
	{
		stack.push(currentTransform);
		currentTransform = new Transform(gc.getDevice());
		gc.getTransform(currentTransform);
	}

	public void rotateAroundZ(float x, float y, double angle)
	{
		translate(x, y);
		rotate(angle);
	}
	
	public void rotate(double angle)
	{
		this.currentTransform.rotate((float)Math.toDegrees(angle));
	}

	public void scale(float scaleX, float scaleY)
	{
		this.currentTransform.scale(scaleX, scaleY);
	}
	
	public void set()
	{
		gc.setTransform(currentTransform);
	}
	
	public void setElements(float m11, float m12, float m21, float m22, float dx, float dy)
	{
		this.currentTransform.setElements(m11, m12, m21, m22, dx, dy);
	}

	public void shear(float shearX, float shearY)
	{
		this.currentTransform.shear(shearX, shearY);
	}

	public void transform(float [] floatArray)
	{
		this.currentTransform.transform(floatArray);
	}
	
	public Point transformToPoint(Point p)
	{
		float [] f = new float[] {p.x, p.y};
		transform(f);
		
		Point ret = new Point((int)f[0], (int)f[1]);
		return ret;
	}
	
	public Location transformToLocation(Point p)
	{
		float [] tmp = new float[2];
		tmp[0] = p.x;
		tmp[1] = p.y;
		this.invert();
		transform(tmp);
		this.invert();
		return new DefaultLocation(Conversions.inchesToYards(tmp[0]), Conversions.inchesToYards(tmp[1]));
	}
	
	public Point transformToPoint(Location loc)
	{
		Point p = new Point(UIUtils.yardsToPixels(loc.getX()), UIUtils.yardsToPixels(loc.getY()));
		return transformToPoint(p);
	}
	
	public void translate(float offsetX, float offsetY)
	{
		this.currentTransform.translate(offsetX, offsetY);
	}

	public void translate(Point point)
	{
		this.translate(point.x, point.y);
	}
}
