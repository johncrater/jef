package jef.core.ui.swt.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

import jef.core.Conversions;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.geometry.LineSegment;


public class TransformStack implements AutoCloseable
{
	private Stack<Transform> stack = new Stack<>();
	private Transform currentTransform;
	private GC gc;
	private Map<String, Color> colors = new HashMap<>();

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
		while (stack.size() > 0)
			pop();

		currentTransform.dispose();
		
		for (Color color : colors.values())
			color.dispose();
	}

	public void getElements(float [] elements)
	{
		this.currentTransform.getElements(elements);
	}
	
	public float getXScale()
	{
		float [] elements = new float[16];
		getElements(elements);
		return elements[0];
	}
	
	public float getYScale()
	{
		float [] elements = new float[16];
		getElements(elements);
		return elements[3];
	}
	
	public float getXTransform()
	{
		float [] elements = new float[16];
		getElements(elements);
		return elements[4];
	}
	
	public float getYTransform()
	{
		float [] elements = new float[16];
		getElements(elements);
		return elements[5];
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
	
	public float transform(float in)
	{
		float [] f = new float[] {in, in};
		transform(f);
		return f[0];
	}
	
	public Location transformToLocation(Point p)
	{
		float [] tmp = new float[2];
		tmp[0] = p.x;
		tmp[1] = p.y;
		this.invert();
		transform(tmp);
		this.invert();
		return new Location(Conversions.inchesToYards(tmp[0]), Conversions.inchesToYards(tmp[1]));
	}
	
	public Point transformToPoint(Location loc)
	{
		Point p = new Point((int)Conversions.yardsToInches(loc.getX()), (int)Conversions.yardsToInches(loc.getY()));
		float [] f = new float[] {p.x, p.y};
		transform(f);
		
		Point ret = new Point(Math.round(f[0]), Math.round(f[1]));
		return ret;
	}
	
	public void translate(float offsetX, float offsetY)
	{
		this.currentTransform.translate(offsetX, offsetY);
	}

	public void translate(Point point)
	{
		this.translate(point.x, point.y);
	}
	
	public void translate(Location location)
	{
		Point p = new Point(yardsToPixels(location.getX()), yardsToPixels(location.getY()));
		this.translate(p.x, p.y);
	}
	
	public void setForeground(Color color)
	{
		this.gc.setForeground(color);
	}
	
	public void setForeground(int systemColor)
	{
		this.gc.setForeground(this.getSystemColor(systemColor));
	}

	public void setForegound(String colorString)
	{
		this.setForeground(getColor(colorString));
	}
	
	public Color getColor(String colorString)
	{
		Color color = this.colors.get(colorString);
		if (color == null)
		{
			color = UIUtils.colorStringToColor(colorString);
			this.colors.put(colorString, color);
		}
	
		return color;
	}
	
	public void setBackground(String colorString)
	{
		this.setBackground(getColor(colorString));
	}
	
	public void setBackground(Color color)
	{
		this.gc.setBackground(color);
	}
	
	public void setBackground(int systemColor)
	{
		this.gc.setBackground(this.getSystemColor(systemColor));
	}
	
	public void setLineWidth(int width)
	{
		this.gc.setLineWidth(width);
	}
	
	public void setLineStyle(int style)
	{
		this.gc.setLineStyle(style);
	}
	
	public Font createFont(FontData fontData)
	{
		return new Font(this.gc.getDevice(), fontData);
	}
	
	public void setFont(Font font)
	{
		this.gc.setFont(font);
	}
	
	public Color getSystemColor(int color)
	{
		return this.gc.getDevice().getSystemColor(color);
	}
	
	public void drawText(String text, Location location, boolean isTransparent)
	{
		Point p = this.locationToPixels(location);
		this.gc.drawText(text, p.x, p.y, isTransparent);
	}
	
	private Point locationToPixels(Location location)
	{
		return new Point(yardsToPixels(location.getX()), yardsToPixels(location.getY()));
	}
	
	public int yardsToPixels(double yards)
	{
		return (int)Math.round(Conversions.yardsToInches(yards));
	}
	
	public double pixelsToYards(int pixels)
	{
		return Conversions.inchesToYards(pixels);
	}
	
	public void fillCircle(Location location, double radiusInYards)
	{
		Point p = this.locationToPixels(location);
		this.gc.fillOval(p.x - yardsToPixels(radiusInYards), p.y - yardsToPixels(radiusInYards),
				yardsToPixels(radiusInYards * 2), yardsToPixels(radiusInYards * 2));
	}
	
	public void drawCircle(Location location, double radiusInYards)
	{
		Point p = this.locationToPixels(location);
		this.gc.drawOval(p.x - yardsToPixels(radiusInYards), p.y - yardsToPixels(radiusInYards),
				yardsToPixels(radiusInYards * 2), yardsToPixels(radiusInYards * 2));
	}
	
	public void drawLine(Location from, Location to)
	{
		Point fromPoint = this.locationToPixels(from);
		Point toPoint = this.locationToPixels(to);
		
		this.gc.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
	}

	public void drawLine(Location from, LinearVelocity lv)
	{
		Point fromPoint = this.locationToPixels(from);
		Point toPoint = this.locationToPixels(new Location().add(lv));
		
		this.gc.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
	}
	
	public void drawLine(LineSegment ls)
	{
		drawLine(ls.getLoc1(), ls.getLoc2());
	}
	
	public void fillPolygon(int [] points)
	{
		this.gc.fillPolygon(points);
	}
	
	public void fillPolygon(Location...locs)
	{
		int [] points = new int[locs.length * 2];
		for (int i = 0; i < locs.length; i++)
		{
			Point p = this.locationToPixels(locs[i]);
			points[i * 2] = p.x;
			points[i * 2 + 1] = p.y;
		}
		
		this.gc.fillPolygon(points);
	}
	
	public Location textExtent(String text)
	{
		Point point = this.gc.textExtent(text);
		return new Location(this.pixelsToYards(point.x), this.pixelsToYards(point.y));
	}
}
