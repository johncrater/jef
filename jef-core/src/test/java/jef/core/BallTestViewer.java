package jef.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.synerset.unitility.unitsystem.common.Angle;

import jef.core.physics.ball.BallPhysics;
import jef.core.units.DefaultAngularVelocity;
import jef.core.units.DefaultLinearVelocity;
import jef.core.units.DefaultLocation;
import jef.core.units.Location;

public class BallTestViewer implements Runnable
{
	private static final double totalLength = Conversions.yardsToInches(125);
	private static final double totalWidth = Conversions.yardsToInches(54 + 5);
	private static final double totalHeight = Conversions.yardsToInches(80);
	private static final double screenLength = 1300D;
	private static final double screenHeight = 600D;

	private static final Color fieldGreen = new Color(70, 137, 68);
	private static final Color white = new Color(255, 255, 255);
	private static final Color yellow = new Color(255, 255, 0);
	private static final Color black = new Color(0, 0, 0);
	private static final Color red = new Color(255, 0, 0);

	private static Shell shell;
	private static Canvas canvasXY;
	private static Canvas canvasXZ;

	private static Image footballBig;
	private static Image football;
	private static Image footballSmall;

	public static FontData playerFontData = new FontData("Courier New", 16, SWT.BOLD);
	public static Font trackingFont;

	public static Football ball;

	private static long hangTime = 0;
	private static double ballDistance;
	private static double ballHeight;
	private static boolean hangTimeRunning = false;
	private static List<DefaultLocation> path = new ArrayList<>();

	public static void main(final String[] args) throws IOException
	{
		BallTestViewer.createShell();

		BallTestViewer.footballBig = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-1125x672.png"));
		BallTestViewer.football = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-68x68.png"));
		BallTestViewer.footballSmall = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-34x34.png"));

		BallTestViewer.ball = new TestBall();
		BallTestViewer.ball.setLocation(10.0, 27.0, 0.0);

		final Composite c = new Composite(BallTestViewer.shell, SWT.NONE);
		final GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 30;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		c.setLayoutData(gd);

		c.setLayout(new FillLayout());

		Label l = new Label(c, SWT.None);
		l.setText("Phi");
		Text phi = new Text(c, SWT.None);
		phi.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Theta");
		Text theta = new Text(c, SWT.NONE);
		theta.setText("-90");
		
		l = new Label(c, SWT.NONE);
		l.setText("Speed");
		Text speed = new Text(c, SWT.NONE);
		speed.setText("25");
		
		l = new Label(c, SWT.NONE);
		l.setText("Omega");
		Text omega = new Text(c, SWT.NONE);
		omega.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Height");
		Text height = new Text(c, SWT.NONE);
		height.setText("1.0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Azimuth");
		Text azimuth = new Text(c, SWT.NONE);
		azimuth.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("YardLine");
		Text yardLine = new Text(c, SWT.NONE);
		yardLine.setText("50");
		
		Button b = new Button(c, SWT.PUSH);
		b.setText("Go!");
		b.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				double thetaInDegrees = Double.parseDouble(theta.getText());
				double speedInYPS = Double.parseDouble(speed.getText());
				double azimuthInRadians = Double.parseDouble(azimuth.getText());
				BallTestViewer.ball.setLinearVelocity(new DefaultLinearVelocity(Math.toRadians(thetaInDegrees), azimuthInRadians, speedInYPS));

				double phiInDegrees = Double.parseDouble(phi.getText());
				double omegaInDegreesPerSecond = Double.parseDouble(omega.getText());
				BallTestViewer.ball.setAngularVelocity(new DefaultAngularVelocity(Math.toRadians(phiInDegrees), Math.toRadians(omegaInDegreesPerSecond)));

				double heightInYards =  Double.parseDouble(height.getText());
				double yardLineValue =  Double.parseDouble(yardLine.getText()) + 10;
				
				BallTestViewer.ball.setLocation(yardLineValue, 27.0, heightInYards);
				BallTestViewer.path.clear();
			}
			
		});

		BallTestViewer.createCanvasXY(BallTestViewer.ball);
		BallTestViewer.createCanvasXZ(BallTestViewer.ball);

		BallTestViewer.trackingFont = new Font(BallTestViewer.shell.getDisplay(), BallTestViewer.playerFontData);

		BallTestViewer.shell.open();
		new BallTestViewer().run();

		while (!BallTestViewer.shell.isDisposed())
			try
			{
				if (!BallTestViewer.shell.getDisplay().readAndDispatch())
					BallTestViewer.shell.getDisplay().sleep();
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}

	}

	private static void createCanvasXY(final Football ball)
	{
		BallTestViewer.canvasXY = new Canvas(BallTestViewer.shell, SWT.DOUBLE_BUFFERED);
		final GridData gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = (int) BallTestViewer.screenHeight;
		BallTestViewer.canvasXY.setLayoutData(gd);

		BallTestViewer.canvasXY.addPaintListener(e ->
		{
			final int offset = 20;

			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.scale((float) (BallTestViewer.canvasXY.getClientArea().height / BallTestViewer.totalLength),
						(float) (BallTestViewer.canvasXY.getClientArea().height / BallTestViewer.totalLength));
				ts.set();

				e.gc.setBackground(BallTestViewer.fieldGreen);
				e.gc.setForeground(BallTestViewer.white);
				e.gc.setLineWidth(4);
				e.gc.fillRectangle(offset, offset, (int) BallTestViewer.totalWidth, (int) BallTestViewer.totalLength);

				for (int i = 10; i < 120; i += 10)
					e.gc.drawLine(offset, 3 * offset + (int) Conversions.yardsToInches(i),
							offset + (int) BallTestViewer.totalLength, offset + (int) Conversions.yardsToInches(i));

				final DefaultLocation location = ball.getLocation();
				final Point xy = new Point((int) Conversions.yardsToInches(location.getY()),
						(int) Conversions.yardsToInches(location.getX()));

				ts.translate(xy.x + offset, xy.y + 3 * offset);
				ts.rotate(90);
				ts.set();
				e.gc.drawImage(BallTestViewer.football, 0 - 34, 0 - 34);
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private static void createCanvasXZ(final Football ball)
	{
		BallTestViewer.canvasXZ = new Canvas(BallTestViewer.shell, SWT.DOUBLE_BUFFERED);
		final GridData gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = 1300;
		BallTestViewer.canvasXZ.setLayoutData(gd);

		BallTestViewer.canvasXZ.setBackground(BallTestViewer.black);
		BallTestViewer.canvasXZ.addPaintListener(e ->
		{
			final int xOffset = 20;
			final int yOffset = 1000;

			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.setElements(1, 0, 0, -1, 0, 0);
				ts.scale((float) (BallTestViewer.screenLength / BallTestViewer.totalLength),
						(float) (BallTestViewer.screenLength / BallTestViewer.totalLength));
//				ts.scale(.5f, .5f);
				ts.translate(xOffset, (float) -BallTestViewer.totalWidth - yOffset);
				ts.set();

				e.gc.setForeground(BallTestViewer.yellow);
				for (int i = 0; i < 11; i++)
				{
					e.gc.drawRectangle((int) Conversions.yardsToInches(i * 10), 0, (int) Conversions.yardsToInches(10),
							(int) BallTestViewer.totalHeight);
					e.gc.drawRectangle(0, (int) Conversions.yardsToInches(i * 10), (int) BallTestViewer.totalLength,
							(int) Conversions.yardsToInches(10));
				}

				final DefaultLocation location = ball.getLocation();

				Point xy = BallTestViewer.toXZPoint(location);

				try (TransformStack ts2 = new TransformStack(e.gc))
				{
					ts2.translate(xy.x, xy.y);
					ts2.rotate(Angle.ofRadians(ball.getAngularVelocity().getOrientation() * -1));
					ts2.set();
					e.gc.drawImage(BallTestViewer.footballSmall, -17, -17);
				}

				e.gc.setBackground(BallTestViewer.white);
				for (final DefaultLocation l : BallTestViewer.path)
					e.gc.fillOval((int) Conversions.yardsToInches(l.getX()), (int) Conversions.yardsToInches(l.getZ()),
							7, 7);
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			final DefaultLocation location = ball.getLocation();

			final StringBuilder text = new StringBuilder();
			text.append(
					String.format("Location  : %.2f, %.2f, %.2f\n", location.getX(), location.getY(), location.getZ()));
			text.append(String.format("Velocity  : %s\n", ball.getLinearVelocity()));
			text.append(String.format("Angular   : %.0f\u00B0, %.2f r/s\n",
					Math.toDegrees(ball.getAngularVelocity().getOrientation()),
					ball.getAngularVelocity().getRotation()));
			text.append(String.format("Hang Time : %.2f\n", BallTestViewer.hangTime / 1000.0));
			text.append(String.format("Distance  : %.0f\n", BallTestViewer.ballDistance));
			text.append(String.format("Height    : %.0f\n", BallTestViewer.ballHeight));

			e.gc.setFont(BallTestViewer.trackingFont);
			e.gc.setBackground(BallTestViewer.black);
			e.gc.drawText(text.toString(), 0, 0);
			
			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.translate(canvasXZ.getBounds().width - 400, canvasXZ.getBounds().y + 200);
				ts.scale(.15f, .15f);
				ts.rotate(Angle.ofRadians(Math.PI / 2.0 + ball.getAngularVelocity().getOrientation()));
				ts.set();
				
				e.gc.drawImage(BallTestViewer.footballBig, (int)(-footballBig.getImageData().width / 2.0) + 1, (int)(-footballBig.getImageData().height / 2.0) + 1);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			

		});
		
		Canvas canvasFootball = new Canvas(canvasXZ, SWT.NONE);
		canvasFootball.setBounds(canvasXZ.getBounds().width - 200, canvasXZ.getBounds().y, 200, 200);
		canvasFootball.addPaintListener(e -> 
		{
			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.scale(.15f, .15f);
				ts.set();
				
				e.gc.drawImage(footballBig, 0, 0);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private static void createShell()
	{
		BallTestViewer.shell = new Shell();
		BallTestViewer.shell.setMaximized(true);
		BallTestViewer.shell.setText("Ball Test Viewer");
		BallTestViewer.shell.setLayout(new GridLayout(2, false));
		BallTestViewer.shell.setBackground(new Color(0, 0, 0));

		try
		{
			final FileInputStream fis = new FileInputStream(new File("BallTestViewer.props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			BallTestViewer.shell.setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BallTestViewer.shell.addListener(SWT.Close, e ->
		{
			final Rectangle bounds = BallTestViewer.shell.getBounds();
			final Properties properties = new Properties();
			properties.put("bounds.x", "" + bounds.x);
			properties.put("bounds.y", "" + bounds.y);
			properties.put("bounds.width", "" + bounds.width);
			properties.put("bounds.height", "" + bounds.height);

			try
			{
				properties.save(new FileOutputStream(new File("BallTestViewer.props")), "");
			}
			catch (final FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private static Point toXZPoint(final Location location)
	{
		return new Point((int) Conversions.yardsToInches(location.getX()),
				(int) Conversions.yardsToInches(location.getZ()));
	}

	private long lastMilliseconds = System.currentTimeMillis();

	@Override
	public void run()
	{
		final long interval = System.currentTimeMillis() - this.lastMilliseconds;
		if (interval < 25)
		{
			BallTestViewer.shell.getDisplay().asyncExec(this);
			return;
		}

		try
		{
			new BallPhysics(ball).update(.04f);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BallTestViewer.canvasXY.redraw();
		BallTestViewer.canvasXZ.redraw();

		this.lastMilliseconds = System.currentTimeMillis();

		if (BallTestViewer.hangTimeRunning)
		{
			if (BallTestViewer.ball.getLocation().getZ() < 1)
				BallTestViewer.hangTimeRunning = false;

			BallTestViewer.ballDistance = Math.max(BallTestViewer.ball.getLocation().getX() - 10,
					BallTestViewer.ballDistance);
			BallTestViewer.ballHeight = Math.max(BallTestViewer.ball.getLocation().getZ() - 0,
					BallTestViewer.ballHeight);

			BallTestViewer.hangTime += interval;
		}

		if (!BallTestViewer.ball.getLinearVelocity().isNotMoving())
			BallTestViewer.path.add(BallTestViewer.ball.getLocation());

		BallTestViewer.shell.getDisplay().asyncExec(this);
	}

}
