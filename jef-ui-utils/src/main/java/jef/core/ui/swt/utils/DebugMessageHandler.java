package jef.core.ui.swt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

import jef.core.Conversions;
import jef.core.Field;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;

public class DebugMessageHandler implements Telegraph
{
//	private static final Color evasionCommonBoundsColor = new Color(0xff, 0x00, 0xff);
//	private static final Color evasionBoundsColor = new Color(0xff, 0xff, 0x00);

	public static Color interceptorColor = new Color(0xff, 0x00, 0x00);
	public static Color runnerColor = new Color(0x00, 0xff, 0x00);
	public static Color blockerColor = new Color(0x00, 0x00, 0xff);

	public static Color runnerInterceptorColor = new Color(0xff, 0xff, 0x00);
	public static Color blockerInterceptorColor = new Color(0xff, 0x00, 0xff);

	private Color defaultColor = new Color(0, 0, 0);
	private Map<Integer, Color> colors = new HashMap<>();
	private Map<Integer, List<Location>> locationsMap = new HashMap<>();
	private Map<Integer, List<LineSegment>> segmentsMap = new HashMap<>();
	private Map<String, Color> colorMap = new HashMap<>();

	private List<DebugShape> debugShapes = new ArrayList<>();

	public DebugMessageHandler()
	{
		MessageManager.getInstance().addListener(this, Messages.drawDebugShape);

		colors.put(Messages.drawIntercepterDestination, interceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawIntercepterDestination);

		colors.put(Messages.drawIntercepterPath, interceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawIntercepterPath);

		colors.put(Messages.drawRunnerDestination, runnerColor);
		MessageManager.getInstance().addListener(this, Messages.drawRunnerDestination);

		colors.put(Messages.drawRunnerPath, runnerColor);
		MessageManager.getInstance().addListener(this, Messages.drawRunnerPath);

		colors.put(Messages.drawBlockerDestination, blockerColor);
		MessageManager.getInstance().addListener(this, Messages.drawBlockerDestination);

		colors.put(Messages.drawBlockerPath, blockerColor);
		MessageManager.getInstance().addListener(this, Messages.drawBlockerPath);

		colors.put(Messages.drawRunnerIntercepterBoundingSegments, runnerInterceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawRunnerIntercepterBoundingSegments);

		colors.put(Messages.drawBlockerIntercepterBoundingSegments, blockerInterceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawBlockerIntercepterBoundingSegments);
	}

	public void clear()
	{
		this.locationsMap.clear();
		this.segmentsMap.clear();
		this.debugShapes.clear();
	}

	@Override
	public boolean handleMessage(Telegram msg)
	{
		if (msg.extraInfo instanceof Location)
		{
			List<Location> locList = locationsMap.get(msg.message);
			if (locList == null)
			{
				locList = new ArrayList<>();
				locationsMap.put(msg.message, locList);
			}

			locList.add((Location) msg.extraInfo);
		}
		else if (msg.extraInfo instanceof LineSegment)
		{
			List<LineSegment> segList = segmentsMap.get(msg.message);
			if (segList == null)
			{
				segList = new ArrayList<>();
				segmentsMap.put(msg.message, segList);
			}

			segList.add((LineSegment) msg.extraInfo);
		}
		else if (msg.extraInfo instanceof DebugShape)
		{
			this.debugShapes.add((DebugShape) msg.extraInfo);
		}

		return true;
	}

	public void draw(GC gc)
	{
		for (Integer message : segmentsMap.keySet())
		{
			Color color = colors.getOrDefault(message, defaultColor);
			gc.setForeground(color);
			gc.setLineWidth(3);

			List<LineSegment> segments = segmentsMap.get(message);
			if (segments == null)
				continue;

			for (LineSegment segment : segments)
			{
				gc.drawLine(UIUtils.yardsToPixels(segment.getLoc1().getX()),
						UIUtils.yardsToPixels(Field.FIELD_TOTAL_WIDTH - segment.getLoc1().getY()),
						UIUtils.yardsToPixels(segment.getLoc2().getX()),
						UIUtils.yardsToPixels(Field.FIELD_TOTAL_WIDTH - segment.getLoc2().getY()));
			}
		}

		for (Integer message : locationsMap.keySet())
		{
			Color color = colors.getOrDefault(message, defaultColor);
			gc.setForeground(color);
			gc.setBackground(color);
			gc.setLineWidth(1);

			List<Location> locations = locationsMap.get(message);
			if (locations == null)
				continue;

			for (Location location : locations)
			{
				UIUtils.fillCircle(gc, location, 10);
			}
		}

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

			if (debugShape.lineWidth > 0)
				gc.setLineWidth(debugShape.lineWidth);

			if (debugShape.location != null)
			{
				if (backgroundColor != null)
					UIUtils.fillCircle(gc, debugShape.location, UIUtils.yardsToPixels(debugShape.radius));

				if (foregroundColor != null)
					UIUtils.drawCircle(gc, debugShape.location, UIUtils.yardsToPixels(debugShape.radius));

				if (debugShape.linearVelocity != null)
				{
					if (foregroundColor != null)
						gc.drawLine(UIUtils.yardsToPixels(debugShape.location.getX()),
								UIUtils.yardsToPixels(debugShape.location.getY()),
								UIUtils.yardsToPixels(debugShape.linearVelocity.getX()),
								UIUtils.yardsToPixels(debugShape.linearVelocity.getY()));
				}
			}

			if (debugShape.lineSegment != null)
			{
				if (backgroundColor != null)
				{
					UIUtils.fillCircle(gc, debugShape.lineSegment.getLoc1(), UIUtils.yardsToPixels(debugShape.radius));
					UIUtils.fillCircle(gc, debugShape.lineSegment.getLoc2(), UIUtils.yardsToPixels(debugShape.radius));
				}

				if (foregroundColor != null)
					gc.drawLine(UIUtils.yardsToPixels(debugShape.lineSegment.getLoc1().getX()),
							UIUtils.yardsToPixels(debugShape.lineSegment.getLoc1().getY()),
							UIUtils.yardsToPixels(debugShape.lineSegment.getLoc2().getX()),
							UIUtils.yardsToPixels(debugShape.lineSegment.getLoc2().getY()));
			}
		}
	}
}
