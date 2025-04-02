package jef.core.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

public class FootballEventManager implements FootballEventListener, FootballEventBroadcaster
{
	private static HashSet<FootballEventManager> globalManagers = new HashSet<>();

	public static void report()
	{
		for (final FootballEventManager manager : FootballEventManager.globalManagers)
		{
			for (final FootballEventListener listener : manager.listeners.keySet())
				System.out.println("Listener: " + manager + " -> " + listener);

			for (final FootballEventBroadcaster broadcaster : manager.broadcasters.keySet())
				if (manager.getClass().equals(FootballEventBroadcaster.class) && (manager.declaringClass != null))
					System.out.println("Broadcasters: " + manager.declaringClass.getName() + " -> "
							+ broadcaster.getClass().getName());
				else
					System.out.println("Broadcasters: " + manager.getClass().getName() + " -> "
							+ broadcaster.getClass().getName());
		}
	}

	private final WeakHashMap<FootballEventListener, Object> listeners = new WeakHashMap<>();
	private final Map<FootballEventBroadcaster, FootballEventListener> broadcasters = new HashMap<>();
	private Class<? extends Object> declaringClass;

	public FootballEventManager(final Object owner)
	{
		// used by consumers
		this.declaringClass = owner.getClass();
//		globalManagers.add(this);
	}

	protected FootballEventManager()
	{
		// used by producers
//		globalManagers.add(this);
	}

	@Override
	public void addFootballListener(final FootballEventListener listener)
	{
		// used by consumers
		this.listeners.put(listener, null);
	}

	@Override
	public void handleFootballEvent(final FootballEvent event)
	{
		final Class<?> declaringClass = this.getClass().getDeclaringClass();
		for (final Class<?> iface : declaringClass.getInterfaces())
			if (iface.equals(FootballEventListener.class))
			{
				final var bbListener = FootballEventListener.class.cast(this);
				bbListener.handleFootballEvent(event);
			}
	}

	public void listen(final FootballEventBroadcaster broadcaster, final FootballEventListener listener)
	{
		final var oldListener = this.broadcasters.remove(broadcaster);
		if (oldListener != null)
			broadcaster.removeFootballListener(oldListener);

		this.broadcasters.put(broadcaster, listener);
		broadcaster.addFootballListener(listener);
	}

	@Override
	public void removeFootballListener(final FootballEventListener listener)
	{
		this.listeners.remove(listener);
	}

	@Override
	public void sendEvent(final FootballEvent event)
	{
		if (this.listeners.size() == 0)
			return;

		System.out.println("Sending Event: " + event);

		final var startTime = System.currentTimeMillis();
		for (final FootballEventListener listener : this.listeners.keySet())
		{
			System.out.print("\tElapsed Time: ");

			final var startTime2 = System.currentTimeMillis();
			listener.handleFootballEvent(event);
			final var endTime2 = System.currentTimeMillis();
			System.out.println("" + (endTime2 - startTime2) + " - " + this + " - " + listener);
		}

		final var endTime = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (endTime - startTime));
	}

	public void stopListening()
	{
		for (final FootballEventBroadcaster broadcaster : this.broadcasters.keySet())
			broadcaster.removeFootballListener(this.broadcasters.get(broadcaster));

		this.broadcasters.clear();
	}

	public void stopListening(final FootballEventBroadcaster broadcaster)
	{
		if (broadcaster != null)
		{
			final var listener = this.broadcasters.remove(broadcaster);
			if (listener != null)
				broadcaster.removeFootballListener(listener);
		}
	}

	protected Collection<FootballEventListener> getListeners()
	{
		return this.listeners.keySet();
	}

}
