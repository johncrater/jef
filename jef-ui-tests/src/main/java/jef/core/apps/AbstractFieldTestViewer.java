package jef.core.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import jef.core.Conversions;
import jef.core.Field;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.Players;
import jef.core.ui.swt.utils.DebugMessageHandler;
import jef.core.ui.swt.utils.TransformStack;

public abstract class AbstractFieldTestViewer implements Runnable
{
	private Location mouseLocation;

	// graphics elements
	private final Shell shell;
	private Canvas canvas;
	private Image field;
	private Image transformedImage;
	private Font systemFont;

	// field scaling and centering
	private Location midfieldLocation = Field.MIDFIELD;
	private float scaleAdjustment = 1.0f;

	// pause 
	private long lastMilliseconds;
	private boolean paused = true;
	private boolean autoPauseActive;

	// options
	private int options;
	public static final int OPTIONS_NONE = 0x00000000;
	public static final int OPTIONS_SHOW_MOUSE_LOCATION = 0x00000001;
	public static final int OPTIONS_SHOW_DEBUG_SHAPES = 0x00000002;
	public static final int OPTIONS_SHOW_PLAYERS = 0x00000004;
	public static final int OPTIONS_SHOW_PERFORMANCE = 0x00000008;

	// performance display
	double cycleRate = Performance.cycleTime.getFrameRate();
	double cycleTimePerFrame = Performance.cycleTime.getAvgTime();
	double processRate = Performance.processTime.getAvgTime();
	double drawRate = Performance.cycleTime.getAvgTime();
	double otherRate = this.cycleTimePerFrame - this.processRate - this.drawRate;
	long refreshCycleCount = System.currentTimeMillis();
	long freeMemory = Runtime.getRuntime().freeMemory();
	long totalMemory = Runtime.getRuntime().totalMemory();
	long maxMemory = Runtime.getRuntime().totalMemory();

	private final DebugMessageHandler debugMessageHandler = new DebugMessageHandler();

	private Players players;
	
	@SuppressWarnings("deprecation")
	public AbstractFieldTestViewer(final String title, int options)
	{
		this.shell = new Shell();
		this.shell.setMaximized(true);
		this.shell.setText(title);
		
		this.options = options;

		this.shell.setLayout(new GridLayout(1, false));

		try
		{
			final FileInputStream fis = new FileInputStream(new File(title + ".props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			this.getShell().setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getShell().addListener(SWT.Close, e ->
		{
			final Rectangle bounds = this.getShell().getBounds();
			final Properties properties = new Properties();
			properties.put("bounds.x", "" + bounds.x);
			properties.put("bounds.y", "" + bounds.y);
			properties.put("bounds.width", "" + bounds.width);
			properties.put("bounds.height", "" + bounds.height);

			try
			{
				properties.save(new FileOutputStream(new File(title + ".props")), "");
			}
			catch (final FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		this.systemFont = shell.getDisplay().getSystemFont();
	}

	public boolean isAutoPauseActive()
	{
		return this.autoPauseActive;
	}

	public boolean isPaused()
	{
		return this.paused;
	}

	public void messageLoop()
	{
		this.lastMilliseconds = System.currentTimeMillis();

		this.players = this.createPlayers();
		this.createButtons();
		this.createCanvas();

		this.shell.open();

		this.run();

		Performance.cycleTime.beginCycle();
		while (!this.shell.isDisposed())
		{
			try
			{
				if (!this.shell.getDisplay().readAndDispatch())
				{
					this.shell.getDisplay().sleep();
				}
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	@Override
	public void run()
	{
		final double interval = (System.currentTimeMillis() - this.lastMilliseconds) / 1000.0;
		if (interval < Performance.frameInterval)
		{
			this.getShell().getDisplay().asyncExec(this);
			return;
		}

		Performance.cycleTime.endCycle();
		Performance.cycleTime.beginCycle();
		Performance.processTime.beginCycle();

		if (!this.paused)
		{
			if (this.autoPauseActive)
			{
				this.paused = true;
			}

			try
			{
				this.debugMessageHandler.clear();

				this.process();

				Performance.processTime.endCycle();
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		this.getShell().getDisplay().asyncExec(this);
	}

	public void setAutoPauseActive(final boolean autoPauseActive)
	{
		this.autoPauseActive = autoPauseActive;
	}

	public void setPaused(final boolean pause)
	{
		this.paused = pause;
	}

	protected Composite createButtons()
	{
		final Composite buttonRow = new Composite(this.getShell(), SWT.NONE);
		buttonRow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		buttonRow.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Composite c2 = new Composite(buttonRow, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Button autoPauseButton = new Button(c2, SWT.PUSH);
		autoPauseButton.setText("Auto Pause: Off");
		autoPauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				AbstractFieldTestViewer.this.setAutoPauseActive(!AbstractFieldTestViewer.this.isAutoPauseActive());
				autoPauseButton.setText(
						AbstractFieldTestViewer.this.isAutoPauseActive() ? "Auto Pause: On" : "Auto Pause: Off");
			}
		});

		final Button pauseButton = new Button(c2, SWT.PUSH);
		pauseButton.setText("Un Pause");
		pauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				AbstractFieldTestViewer.this.setPaused(!AbstractFieldTestViewer.this.isPaused());
				pauseButton.setText(AbstractFieldTestViewer.this.isPaused() ? "Un Pause" : "Pause");
			}
		});

		return buttonRow;
	}

	protected void createCanvas()
	{
		this.canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED);
		this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		this.canvas.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		this.canvas.layout(true);

		this.field = new Image(this.shell.getDisplay(), this.getClass().getResourceAsStream("/field-4500x2124.png"));

		this.canvas.addControlListener(new ControlListener()
		{

			@Override
			public void controlMoved(final ControlEvent e)
			{
			}

			@Override
			public void controlResized(final ControlEvent e)
			{
				transformedImage = null;
			}
		});

		this.canvas.addPaintListener(e ->
		{
			Performance.drawTime.beginCycle();

			try (TransformStack ts = new TransformStack(e.gc))
			{
				this.drawPreTransformedCanvas(ts);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			try (FieldTransformStack ts = new FieldTransformStack(this.canvas, e.gc, this.midfieldLocation,
					this.scaleAdjustment))
			{
				this.drawTransformedCanvas(ts);
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}

			try (TransformStack ts = new TransformStack(e.gc))
			{
				this.drawPostTransformedCanvas(ts);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			Performance.drawTime.endCycle();
		});

		this.canvas.addMouseMoveListener(e ->
		{
			try (FieldTransformStack ts = new FieldTransformStack(AbstractFieldTestViewer.this.canvas,
					AbstractFieldTestViewer.this.midfieldLocation, AbstractFieldTestViewer.this.scaleAdjustment))
			{
				final Point p = new Point(e.x, e.y);
				this.mouseLocation = ts.transformToLocation(p);
			}
			catch (final Exception e1)
			{
			}
		});

		this.canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				try (FieldTransformStack ts = new FieldTransformStack(AbstractFieldTestViewer.this.canvas,
						AbstractFieldTestViewer.this.midfieldLocation, AbstractFieldTestViewer.this.scaleAdjustment))
				{
					if ((e.stateMask & SWT.CONTROL) != 0)
					{
						final Point p = new Point(e.x, e.y);
						AbstractFieldTestViewer.this.midfieldLocation = ts.transformToLocation(p);
						transformedImage = null;
					}
				}
				catch (final Exception e1)
				{
				}
			}

		});

		this.canvas.addMouseWheelListener(e ->
		{
			if ((e.stateMask & SWT.CONTROL) != 0)
			{
				this.scaleAdjustment = (float) Math.max(.25, this.scaleAdjustment + (Math.signum(e.count) * .25));
				this.transformedImage = null;
			}
		});

	}

	protected void updateFieldImage(FieldTransformStack fts)
	{
		Image tmp = canvas.getBackgroundImage();

		final var bounds = new Rectangle(0, 0,
				(int) Conversions.yardsToInches(Field.DIM_TOTAL_LENGTH * scaleAdjustment),
				(int) Conversions.yardsToInches(Field.DIM_TOTAL_WIDTH * scaleAdjustment));
		final var fieldImage = new Image(getShell().getDisplay(), bounds);
		final var gc = new GC(fieldImage);
		gc.setAdvanced(true);
		gc.setTransform(fts.getCurrentTransform());
		gc.drawImage(field, 0, 0);
		gc.dispose();

		canvas.setBackgroundImage(fieldImage);
		this.transformedImage = fieldImage;

		if (tmp != null)
			tmp.dispose();
	}

	protected abstract Players createPlayers();

	protected abstract void drawPlayer(FieldTransformStack ts, PlayerState playerState);

	protected void drawPostTransformedCanvas(TransformStack fts)
	{
		if ((options & OPTIONS_SHOW_MOUSE_LOCATION) != 0)
		{
			drawMouseLocation(fts);
		}

		if ((options & OPTIONS_SHOW_PERFORMANCE) != 0)
		{
			this.drawPerformance(fts);
		}
	}

	protected void drawMouseLocation(TransformStack ts)
	{
		ts.setBackground(SWT.COLOR_BLACK);
		ts.setForeground(SWT.COLOR_YELLOW);
		
		if (mouseLocation != null)
		{
			String msg = this.mouseLocation.toString();
			ts.setFont(systemFont);
			Location textExtent = ts.textExtent(msg);
			ts.drawText(msg, ts.transformToLocation(new Point(canvas.getClientArea().width, canvas.getClientArea().height)).subtract(textExtent.add(1, 1, 0)), false);
		}
	}
	
	protected void drawPreTransformedCanvas(final TransformStack ts)
	{

	}

	protected void drawTransformedCanvas(final FieldTransformStack ts)
	{
		if (transformedImage == null)
			this.updateFieldImage(ts);

		if ((options & OPTIONS_SHOW_PLAYERS) != 0)
		{
			for (final Player player : this.players.getPlayers())
			{
				this.drawPlayer(ts, this.players.getState(player));
			}
		}
		
		if ((options & OPTIONS_SHOW_DEBUG_SHAPES) != 0)
		{
			this.debugMessageHandler.draw(ts);
		}
	}

	protected Canvas getCanvas()
	{
		return this.canvas;
	}

	protected DebugMessageHandler getDebugMessageHandler()
	{
		return this.debugMessageHandler;
	}

	protected Location getMidfieldLocation()
	{
		return this.midfieldLocation;
	}

	protected Players getPlayers()
	{
		return this.players;
	}

	protected float getScaleAdjustment()
	{
		return this.scaleAdjustment;
	}

	protected Shell getShell()
	{
		return this.shell;
	}

	protected abstract void process();

	protected void drawPerformance(final TransformStack fts)
	{
		fts.setBackground(SWT.COLOR_BLACK);
		fts.setForeground(SWT.COLOR_YELLOW);
	
		final long current = System.currentTimeMillis();
		if ((current - this.refreshCycleCount) > 1000)
		{
			this.cycleRate = Performance.cycleTime.getFrameRate();
			this.cycleTimePerFrame = Performance.cycleTime.getAvgTime();
			if (this.cycleTimePerFrame == 0)
				return;
	
			this.processRate = Performance.processTime.getAvgTime();
			this.drawRate = Performance.drawTime.getAvgTime();
			this.otherRate = this.cycleTimePerFrame - this.processRate - this.drawRate;
			this.refreshCycleCount = current;
	
			this.freeMemory = Runtime.getRuntime().freeMemory();
			this.totalMemory = Runtime.getRuntime().totalMemory();
			this.maxMemory = Runtime.getRuntime().totalMemory();
		}
	
		final StringBuilder msg = new StringBuilder();
		msg.append(String.format("Tick Count  : %d\n", Performance.processTime.getTickCount()));
		msg.append(String.format("Frame Rate  : %.1f fps\n", this.cycleRate));
		msg.append(String.format("Process Rate: %.1f%% (%.1f ns)\n", (this.processRate * 100) / this.cycleTimePerFrame,
				this.processRate));
		msg.append(String.format("Draw Rate   : %.1f%% (%.1f ns)\n", (this.drawRate * 100) / this.cycleTimePerFrame,
				this.drawRate));
		msg.append(String.format("Other Rate  : %.1f%%\n", (this.otherRate * 100) / this.cycleTimePerFrame));
		msg.append("\n");
		msg.append(String.format("Max Memory  : %d MB\n", this.maxMemory / 1000000));
		msg.append(String.format("Total Memory: %d MB\n", this.totalMemory / 1000000));
		msg.append(String.format("Free Memory : %d MB \n", this.freeMemory / 1000000));
		msg.append("\n");
	
		fts.setFont(systemFont);
		fts.drawText(msg.toString(), new Location(1, 1), false);
	}
}
