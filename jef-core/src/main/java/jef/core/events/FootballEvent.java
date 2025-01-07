package jef.core.events;

public class FootballEvent
{
	private final Object source;

	public FootballEvent(final Object source)
	{
		this.source = source;
	}

	public Object getSource()
	{
		return this.source;
	}
}
