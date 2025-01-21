package jef.core;

import java.util.List;

public interface PlayerInfo
{
	public List<PlayerPosition> getPositions();
	public PlayerPosition getPrimaryPosition();
	public PlayerPosition getSecondaryPosition();
	public PlayerPosition getTertiaryPosition();
	
	int getAge();
	void setAge(int age);

	String getFirstName();
	void setFirstName(String firstName);

	String getLastName();
	void setLastName(String lastName);

	int getHeight();
	void setHeight(int height);

	int getNumber();
	void setNumber(int number);

	String getPlayerID();

	int getWeight();
	void setWeight(int weight);







}