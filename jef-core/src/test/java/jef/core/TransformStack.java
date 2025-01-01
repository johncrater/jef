package jef.core;

import java.util.Stack;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

import com.synerset.unitility.unitsystem.common.Angle;

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

	public Transform getCurrentTrasnform()
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

	public void rotateAroundZ(float x, float y, Angle angle)
	{
		translate(x, y);
		rotate(angle);
	}
	
	public void rotate(Angle angle)
	{
		this.currentTransform.rotate((float) -angle.getInDegrees());
	}

	public void rotate(float angle)
	{
		this.currentTransform.rotate(angle);
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
	
	public void translate(float offsetX, float offsetY)
	{
		this.currentTransform.translate(offsetX, offsetY);
	}

	public void translate(Point point)
	{
		this.translate(point.x, point.y);
	}
}
