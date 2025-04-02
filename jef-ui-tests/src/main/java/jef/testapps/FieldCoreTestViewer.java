package jef.testapps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.formations.Performance;
import jef.geometry.Field;
import jef.geometry.LineSegment;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.DebugShape;
import jef.movement.player.PlayerState;
import jef.pathfinding.PathfindingMessages;
import jef.pathfinding.PathfindingState;

public class FieldCoreTestViewer extends AbstractFieldTestViewer<TestViewerPlayer>
{
	public static void main(String[] args)
	{
		new FieldCoreTestViewer().messageLoop();
	}

	public FieldCoreTestViewer()
	{
		super("Field Core Test Viewer", OPTIONS_SHOW_MOUSE_LOCATION | OPTIONS_SHOW_DEBUG_SHAPES | OPTIONS_SHOW_PERFORMANCE);
	}

	@Override
	protected PathfindingState<TestViewerPlayer> createTestState()
	{
		return new PathfindingState<TestViewerPlayer>(Performance.frameInterval);
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

				try (FieldTransformStack ts = new FieldTransformStack(canvas, getMidfieldLocation(),
						getScaleAdjustment()))
				{
					final Point p = new Point(e.x, e.y);
					final Location loc = ts.transformToLocation(p);

					if ((e.stateMask & SWT.CONTROL) == 0)
					{
						getDebugMessageHandler().clear();
						MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
								DebugShape.fillLocation(loc, "#FF000000"));
						MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
								DebugShape.drawLineSegment(new LineSegment(anchorPoint, loc), "#FF000000"));

						StringBuilder builder = new StringBuilder();
						builder.append("        Location: ").append(loc).append("\n");
						builder.append("Linear  Velocity: ").append(new LinearVelocity(anchorPoint, loc)).append("\n");
						
						MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape, DebugShape
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
	protected void drawTransformedCanvas(FieldTransformStack ts)
	{
		super.drawTransformedCanvas(ts);
		ts.fillCircle(anchorPoint, .5);
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
