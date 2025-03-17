package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import jef.core.LinearVelocity;
import jef.core.Field;
import jef.core.Location;

public class UIUtils
{
	public static Color colorStringToColor(String colorString)
	{
		if (colorString == null)
			return new Color(0, 0, 0, 0);

		int colorInt = Integer.parseInt(colorString.substring(1, 3), 16) << 8;
		colorInt = colorInt + Integer.parseInt(colorString.substring(3, 5), 16) << 8;
		colorInt = colorInt + Integer.parseInt(colorString.substring(5, 7), 16) << 8;
		colorInt = colorInt + Integer.parseInt(colorString.substring(7, 9), 16);
		return UIUtils.colorIntToColor(colorInt);
	}

	public static String colorToString(Color c)
	{
		return String.format("#%02X%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public static Color colorIntToColor(int color)
	{
		final int r = color >> 24 & 0x000000FF;
		final int g = color >> 16 & 0x000000FF;
		final int b = color >> 8 & 0x000000FF;
		final int alpha = color & 0x000000FF;

		return new Color(r, g, b, alpha);
	}

	public static void drawEquilateralTriangle(final GC gc, final Location loc, final float sideLength, final double a,
			final boolean fill)
	{
		final var distanceToVertex = (float)Math.abs(Math.cos(60) * sideLength / 2);
		final var p1 = loc.add(new LinearVelocity(a, 0, distanceToVertex));
		final var p2 = loc.add(new LinearVelocity(a + Math.PI * 4.0 / 6.0, 0, distanceToVertex));
		final var p3 = loc.add(new LinearVelocity(a + Math.PI * 8.0 / 6.0, 0, distanceToVertex));

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

	public static void drawCircle(final GC gc, final Location l, final int radius)
	{
		gc.drawOval(yardsToPixels(l.getX()) - radius, yardsToPixels(l.getY()) - radius, radius * 2, radius * 2);
	}

	public static int yardsToPixels(double yards)
	{
		return (int)Math.round(yards * 36.0);
	}

	public static double pixelsToYards(int pixels)
	{
		return pixels / 36.0;
	}
	
	public static Point locationToPoint(Location loc)
	{
		return new Point(yardsToPixels(loc.getX()), yardsToPixels(loc.getY()));
	}
}
