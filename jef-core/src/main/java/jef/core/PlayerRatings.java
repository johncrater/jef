package jef.core;

public interface PlayerRatings
{

	int getCsvAccuracy();

	int getCsvControl();

	int getCsvCoverage();

	int getCsvDiscipline();

	int getCsvDistance();

	int getCsvDurability();

	int getCsvFgAccuracy();

	int getCsvFgPower();

	int getCsvHands();

	int getCsvKickReturns();

	int getCsvMobility();

	int getCsvPassBlocking();

	int getCsvPassRush();

	int getCsvPower();

	int getCsvPuntAccuracy();

	int getCsvPuntPower();

	int getCsvPuntReturns();

	int getCsvQuickness();

	int getCsvReceiving();

	int getCsvRedZone();

	int getCsvRunBlocking();

	int getCsvRunDefense();

	int getCsvSpeed();

	int getCsvTackling();

	int getCsvTurnovers();

	int getCsvVision();

	int getRatingBlocking();

	int getRatingPassBlocking();

	int getRatingPassDefense();

	int getRatingPassRush();

	int getRatingTackles();

	void setRatingTackles(final int ratingTackles);

	void setRatingPassRush(final int ratingPassRush);

	void setRatingPassDefense(final int ratingPassDefense);

	void setRatingPassBlocking(final int ratingPassBlocking);

	void setRatingBlocking(final int ratingBlocking);

	void setCsvVision(final int csvVision);

	void setCsvTurnovers(final int csvTurnovers);

	void setCsvTackling(final int csvTackling);

	void setCsvSpeed(final int csvSpeed);

	void setCsvRunDefense(final int csvRunDefense);

	void setCsvRunBlocking(final int csvRunBlocking);

	void setCsvRedZone(final int csvRedZone);

	void setCsvReceiving(final int csvReceiving);

	void setCsvQuickness(final int csvQuickness);

	void setCsvPuntReturns(final int csvPuntReturns);

	void setCsvPuntPower(final int csvPuntPower);

	void setCsvPuntAccuracy(final int csvPuntAccuracy);

	void setCsvPower(final int csvPower);

	void setCsvPassRush(final int csvPassRush);

	void setCsvPassBlocking(final int csvPassBlocking);

	void setCsvMobility(final int csvMobility);

	void setCsvKickReturns(final int csvKickReturns);

	void setCsvHands(final int csvHands);

	void setCsvFgPower(final int csvFgPower);

	void setCsvFgAccuracy(final int csvFgAccuracy);

	void setCsvDurability(final int csvDurability);

	void setCsvDistance(final int csvDistance);

	void setCsvDiscipline(final int csvDiscipline);

	void setCsvCoverage(final int csvCoverage);

	void setCsvControl(final int csvControl);

	void setCsvAccuracy(final int csvAccuracy);

}