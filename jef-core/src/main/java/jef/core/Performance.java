package jef.core;

public class Performance
{
	private static int desiredFrameRate = 24;
	public static long frameNanos = 1000000000L / desiredFrameRate;
	public static double frameInterval = 1.0 / desiredFrameRate;

	private int COUNTS_TO_KEEP = 1000;
	
	private long [] times = new long[COUNTS_TO_KEEP];

	private long startTime;
	private int currentIndex = 0;
	private int tickCount = 0;
	private long totalTime = 0;

	public synchronized void beginCycle()
	{
		startTime = System.nanoTime();
		this.tickCount += 1;
	}

	public int getTickCount()
	{
		return this.tickCount;
	}

	public synchronized void endCycle()
	{
		long previousTime = times[currentIndex];
		long time = System.nanoTime() - startTime;
		currentIndex = (currentIndex + 1) % COUNTS_TO_KEEP;
		times[currentIndex] = time;

		totalTime += time - previousTime;
	}

	public synchronized double getFrameRate()
	{
		return 1000000 * (double)times.length / (double)totalTime;
	}
	
	public synchronized double getAvgTime()
	{
		return ((double)totalTime / (double)times.length);
	}
	
	public static final Performance cycleTime = new Performance();
	public static final Performance drawTime = new Performance();
	public static final Performance processTime = new Performance();
}