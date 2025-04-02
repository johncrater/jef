package jef.league.events;

public interface FootballEventBroadcaster
{
	void addFootballListener(FootballEventListener listener);

	void removeFootballListener(FootballEventListener listener);

	void sendEvent(FootballEvent event);
}
