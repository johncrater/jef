package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.Location;

public class UIUtils
{
	public static Color colorStringToColor(String colorString)
	{
		if (colorString == null)
			return new Color(0, 0, 0);

		int colorInt = Integer.parseInt(colorString.substring(1), 16);
		return UIUtils.colorIntToColor(colorInt);
	}

	public static String colorToString(Color c)
	{
		return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
	}

	public static Color colorIntToColor(int color)
	{
		color = color & 0x00ffffff;
		final int r = color >> 16;
		final int g = color >> 8 & 0x000000FF;
		final int b = color & 0x000000FF;

		return new Color(r, g, b);
	}

	public static void drawEquilateralTriangle(final GC gc, final Location loc, final float sideLength, final double a,
			final boolean fill)
	{
		final var distanceToVertex = (float)Math.abs(Math.cos(60) * sideLength / 2);
		final var p1 = loc.add(new DefaultLinearVelocity(a, 0, distanceToVertex));
		final var p2 = loc.add(new DefaultLinearVelocity(a + Math.PI * 4.0 / 6.0, 0, distanceToVertex));
		final var p3 = loc.add(new DefaultLinearVelocity(a + Math.PI * 8.0 / 6.0, 0, distanceToVertex));

		int [] coordinates = new int[]
				{ yardsToPixels(p1.getX()), yardsToPixels(p1.getY()), yardsToPixels(p2.getX()), yardsToPixels(p2.getY()), yardsToPixels(p3.getX()), yardsToPixels(p3.getY()) };
		if (fill)
			gc.fillPolygon(coordinates);
		else
			gc.drawPolygon(coordinates);
	}

	public static void drawX(final GC gc, final Location l, final int length)
	{
		gc.drawLine(yardsToPixels(l.getX()) - length / 2, yardsToPixels(l.getY()) - length / 2, yardsToPixels(l.getX()) + length / 2,
				yardsToPixels(l.getY()) + length / 2);
		gc.drawLine(yardsToPixels(l.getX()) - length / 2, yardsToPixels(l.getY()) + length / 2, yardsToPixels(l.getX()) + length / 2,
				yardsToPixels(l.getY()) - length / 2);
	}

	public static void fillCircle(final GC gc, final Location l, final int radius)
	{
		gc.fillOval(yardsToPixels(l.getX()) - radius, yardsToPixels(l.getY()) - radius, radius * 2, radius * 2);
	}

	public static int yardsToPixels(double yards)
	{
		return (int)Math.round(yards * 36.0);
	}
	
}
