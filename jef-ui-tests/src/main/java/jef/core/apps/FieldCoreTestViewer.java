package jef.core.apps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.AngularVelocity;
import jef.core.Conversions;
import jef.core.Field;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.PlayerState;
import jef.core.Players;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.ui.swt.utils.UIUtils;

public class FieldCoreTestViewer extends AbstractFieldTestViewer
{
	public static void main(String[] args)
	{
		new FieldCoreTestViewer().messageLoop();
	}

	public FieldCoreTestViewer()
	{
		super("Field Core Test Viewer");
	}

	@Override
	protected Players createPlayers()
	{
		return new Players();
	}

	private Location anchorPoint = Field.MIDFIELD;

	@Override
	protected void createCanvas()
	{
		super.createCanvas();

		Canvas canvas = this.getCanvas();
		canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				final Point p = new Point(e.x, e.y);

				try (FieldTransformStack ts = new FieldTransformStack(canvas, getMidfieldLocation(),
						getScaleAdjustment()))
				{
					final Location loc = ts.transformToLocation(p);

					if ((e.stateMask & SWT.CONTROL) == 0)
					{
						getDebugMessageHandler().clear();
						MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
								DebugShape.fillLocation(loc, "#FF000000"));
						MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
								DebugShape.drawLineSegment(new LineSegment(anchorPoint, loc), "#FF000000"));

						StringBuilder builder = new StringBuilder();
						builder.append("        Location: ").append(loc).append("\n");
						builder.append("Linear  Velocity: ").append(new LinearVelocity(anchorPoint, loc)).append("\n");
						
						MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape
								.drawText(builder.toString(), Field.PLAYABLE_AREA_NW_CORNER, "#FFFF0000", 36));
					}
				}
				catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
	}

	@Override
	protected void drawPostTransformedCanvas(GC gc)
	{
		super.drawPostTransformedCanvas(gc);
	}

	@Override
	protected void drawTransformedCanvas(FieldTransformStack ts)
	{
		super.drawTransformedCanvas(ts);

		UIUtils.fillCircle(ts.getGC(), anchorPoint, (int) Conversions.yardsToInches(.5));
	}

	@Override
	protected void drawPlayer(FieldTransformStack ts, PlayerState playerState)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void process()
	{
		// TODO Auto-generated method stub

	}

}
