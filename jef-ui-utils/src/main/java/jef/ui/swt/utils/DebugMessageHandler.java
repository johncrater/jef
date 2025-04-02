package jef.ui.swt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

import jef.geometry.Location;
import jef.movement.DebugShape;
import jef.movement.player.Waypoint;
import jef.pathfinding.PathfindingMessages;

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
		MessageManager.getInstance().addListener(this, PathfindingMessages.drawDebugShape);
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

	public void draw(TransformStack ts)
	{
		for (DebugShape debugShape : this.debugShapes)
		{
			Color foregroundColor = this.colorMap.get(debugShape.foregroundRGBA);
			if (foregroundColor == null && debugShape.foregroundRGBA != null)
			{
				foregroundColor = UIUtils.colorStringToColor(debugShape.foregroundRGBA);
				this.colorMap.put(debugShape.foregroundRGBA, foregroundColor);
				ts.setForeground(foregroundColor);
			}

			Color backgroundColor = this.colorMap.get(debugShape.backgroundRGBA);
			if (backgroundColor == null && debugShape.backgroundRGBA != null)
			{
				backgroundColor = UIUtils.colorStringToColor(debugShape.backgroundRGBA);
				this.colorMap.put(debugShape.backgroundRGBA, backgroundColor);
				ts.setBackground(backgroundColor);
			}

			if (foregroundColor != null)
				ts.setForeground(foregroundColor);

			if (backgroundColor != null)
				ts.setBackground(backgroundColor);

			double lineWidth = debugShape.lineWidth;
			double radius = debugShape.radius;

			float scale = ts.getXScale();
			lineWidth = lineWidth / scale;
			radius = radius / scale;

			ts.setLineWidth((int)Math.round(lineWidth));
			ts.setLineStyle(getLineTypeNumber(debugShape.lineType));

			if (debugShape.location != null)
			{
				if (debugShape.text != null)
				{
					FontData standardFontData = new FontData("Courier New", debugShape.fontSize, SWT.BOLD);
					Font standardFont = ts.createFont(standardFontData);
					ts.setFont(standardFont);
					ts.setForeground(ts.getSystemColor(SWT.COLOR_YELLOW));
					ts.setBackground(ts.getSystemColor(SWT.COLOR_BLACK));
					ts.drawText(debugShape.text, debugShape.location, true);
					standardFont.dispose();
				}
				else
				{
					if (backgroundColor != null)
						ts.fillCircle(debugShape.location, radius);
	
					if (foregroundColor != null)
						ts.drawCircle(debugShape.location, radius);
	
					if (debugShape.linearVelocity != null)
					{
						if (foregroundColor != null)
							ts.drawLine(debugShape.location, debugShape.linearVelocity);
					}
				}
			}

			if (debugShape.lineSegment != null)
			{
				if (backgroundColor != null)
				{
					ts.fillCircle(debugShape.lineSegment.getLoc1(), radius);
					ts.fillCircle(debugShape.lineSegment.getLoc2(), radius);
				}

				if (foregroundColor != null)
				{
						ts.drawLine(debugShape.lineSegment);
				}
			}
			
			if (debugShape.path != null)
			{
				Location prev = debugShape.location;
				for (Waypoint wp : debugShape.path)
				{
					ts.drawLine(prev, wp.getWaypointDestination());
					prev = wp.getWaypointDestination();
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
