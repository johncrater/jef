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

import jef.core.Field;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;

public class DebugMessageHandler implements Telegraph
{
	private static final Color evasionCommonBoundsColor = new Color(0xff, 0x00, 0xff);
	private static final Color evasionBoundsColor = new Color(0xff, 0xff, 0x00);
	public static Color interceptorColor = new Color(0xff, 0x00, 0x00);
	public static Color runnerColor = new Color(0x00, 0xff, 0x00);
	public static Color blockerColor = new Color(0x00, 0x00, 0xff);
	
	private Color defaultColor = new Color(0, 0, 0);
	private Map<Integer, Color> colors = new HashMap<>();
	private Map<Integer, List<Location>> locationsMap = new HashMap<>();
	private Map<Integer, List<LineSegment>> segmentsMap = new HashMap<>();

	public DebugMessageHandler()
	{
		colors.put(Messages.drawIntercepterDestination, interceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawIntercepterDestination);

		colors.put(Messages.drawIntercepterPath, interceptorColor);
		MessageManager.getInstance().addListener(this, Messages.drawIntercepterPath);

		colors.put(Messages.drawRunnerDestination, runnerColor);
		MessageManager.getInstance().addListener(this, Messages.drawRunnerDestination);

		colors.put(Messages.drawRunnerPath, runnerColor);
		MessageManager.getInstance().addListener(this, Messages.drawRunnerPath);

		colors.put(Messages.drawEvasionBoundingSegments, evasionBoundsColor);
		MessageManager.getInstance().addListener(this, Messages.drawEvasionBoundingSegments);

		colors.put(Messages.drawEvasionIntersections, evasionBoundsColor);
		MessageManager.getInstance().addListener(this, Messages.drawEvasionIntersections);

		colors.put(Messages.drawEvasionCommonReachableLines, evasionCommonBoundsColor);
		MessageManager.getInstance().addListener(this, Messages.drawEvasionCommonReachableLines);

		colors.put(Messages.drawEvasionCommonReachableLocations, evasionCommonBoundsColor);
		MessageManager.getInstance().addListener(this, Messages.drawEvasionCommonReachableLocations);

		colors.put(Messages.drawBlockerDestination, blockerColor);
		MessageManager.getInstance().addListener(this, Messages.drawBlockerDestination);

		colors.put(Messages.drawBlockerPath, blockerColor);
		MessageManager.getInstance().addListener(this, Messages.drawBlockerPath);

		MessageManager.getInstance().addListener(this, Messages.drawEvasionIntercepterReachableLocations);
		colors.put(Messages.drawEvasionIntercepterReachableLocations, new Color(0x80, 0x00, 0x00));

	}

	public void clear()
	{
		this.locationsMap.clear();
		this.segmentsMap.clear();
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
	}
}
