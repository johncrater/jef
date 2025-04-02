package jef.pathfinding;

import jef.movement.ball.FootballState;

public interface IFootballSteps
{

	FootballState getFirst();

	FootballState getLast();

	FootballState getState(int offset);

	FootballState getPerceivedState(int offset);

	int getStepCapacity();

	double getTimerInterval();

}