package jef.core.ui.swt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

import jef.core.Location;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.movement.player.Waypoint;

public class DebugMessageHandler implements Telegraph
{
//	private static final Color evasionCommonBoundsColor = new Color(0xff, 0x00, 0xff);
//	private static final Color evasionBoundsColor = new Color(0xff, 0xff, 0x00);

	public static Color interceptorColor = new Color(0xff, 0x00, 0x00);
	public static Color runnerColor = new Color(0x00, 0xff, 0x00);
	public static Color blockerColor = new Color(0x00, 0x00, 0xff);

	public static Color runnerInterceptorColor = new Color(0xff, 0xff, 0x00);
	public static Color blockerInterceptorColor = new Color(0xff, 0x00, 0xff);

	private Map<String, Color> colorMap = new HashMap<>();

	private List<DebugShape> debugShapes = new ArrayList<>();

	public DebugMessageHandler()
	{
		MessageManager.getInstance().addListener(this, Messages.drawDebugShape);
	}

	public void clear()
	{
		this.debugShapes.clear();
	}

	@Override
	public boolean handleMessage(Telegram msg)
	{
		this.debugShapes.add((DebugShape) msg.extraInfo);
		return true;
	}

	public void draw(GC gc)
	{
		for (DebugShape debugShape : this.debugShapes)
		{
			Color foregroundColor = this.colorMap.get(debugShape.foregroundRGBA);
			if (foregroundColor == null && debugShape.foregroundRGBA != null)
			{
				foregroundColor = UIUtils.colorStringToColor(debugShape.foregroundRGBA);
				this.colorMap.put(debugShape.foregroundRGBA, foregroundColor);
				gc.setForeground(foregroundColor);
			}

			Color backgroundColor = this.colorMap.get(debugShape.backgroundRGBA);
			if (backgroundColor == null && debugShape.backgroundRGBA != null)
			{
				backgroundColor = UIUtils.colorStringToColor(debugShape.backgroundRGBA);
				this.colorMap.put(debugShape.backgroundRGBA, backgroundColor);
				gc.setBackground(backgroundColor);
			}

			if (foregroundColor != null)
				gc.setForeground(foregroundColor);

			if (backgroundColor != null)
				gc.setBackground(backgroundColor);

			double lineWidth = debugShape.lineWidth;
			double radius = debugShape.radius;

			try (TransformStack ts = new TransformStack(gc))
			{
				float scale = ts.getXScale();
				lineWidth = lineWidth / scale;
				radius = radius / scale;
			}
			catch (Exception e)
			{
			}

			gc.setLineWidth((int)Math.round(lineWidth));
			gc.setLineStyle(getLineTypeNumber(debugShape.lineType));

			if (debugShape.location != null)
			{
				if (debugShape.text != null)
				{
					gc.drawString(debugShape.text, UIUtils.yardsToPixels(debugShape.location.getX()),
							UIUtils.yardsToPixels(debugShape.location.getY()), true);
				}
				else
				{
					if (backgroundColor != null)
						UIUtils.fillCircle(gc, debugShape.location, UIUtils.yardsToPixels(radius));
	
					if (foregroundColor != null)
						UIUtils.drawCircle(gc, debugShape.location, UIUtils.yardsToPixels(radius));
	
					if (debugShape.linearVelocity != null)
					{
						if (foregroundColor != null)
							gc.drawLine(UIUtils.yardsToPixels(debugShape.location.getX()),
									UIUtils.yardsToPixels(debugShape.location.getY()),
									UIUtils.yardsToPixels(debugShape.linearVelocity.getX()),
									UIUtils.yardsToPixels(debugShape.linearVelocity.getY()));
					}
				}
			}

			if (debugShape.lineSegment != null)
			{
				if (backgroundColor != null)
				{
					UIUtils.fillCircle(gc, debugShape.lineSegment.getLoc1(), UIUtils.yardsToPixels(radius));
					UIUtils.fillCircle(gc, debugShape.lineSegment.getLoc2(), UIUtils.yardsToPixels(radius));
				}

				if (foregroundColor != null)
				{
						gc.drawLine(UIUtils.yardsToPixels(debugShape.lineSegment.getLoc1().getX()),
								UIUtils.yardsToPixels(debugShape.lineSegment.getLoc1().getY()),
								UIUtils.yardsToPixels(debugShape.lineSegment.getLoc2().getX()),
								UIUtils.yardsToPixels(debugShape.lineSegment.getLoc2().getY()));
				}
			}
			
			if (debugShape.path != null)
			{
				Location prev = debugShape.location;
				for (Waypoint wp : debugShape.path)
				{
					gc.drawLine(UIUtils.yardsToPixels(prev.getX()),
							UIUtils.yardsToPixels(prev.getY()),
							UIUtils.yardsToPixels(wp.getDestination().getX()),
							UIUtils.yardsToPixels(wp.getDestination().getY()));
					prev = wp.getDestination();
				}
			}
		}
	}

	private int getLineTypeNumber(DebugShape.LineType lineType)
	{
		return switch (lineType)
		{
			case dash -> SWT.LINE_DASH;
			case dashdot -> SWT.LINE_DASHDOT;
			case dashdotdot -> SWT.LINE_DASHDOTDOT;
			case dot -> SWT.LINE_DOT;
			case solid -> SWT.LINE_SOLID;
			default -> SWT.LINE_SOLID;
		};
	}
}
