package jef.history;

import java.io.File;
import java.util.HashMap;

public class RenameUniformFiles
{
	private static HashMap<String, String> conversionMap = new HashMap<>();

	public static void main(String[] args)
	{
		conversionMap.put("ChiBears", "chi");
		conversionMap.put("ChiCardinals", "crd");
		conversionMap.put("Cleveland", "cle");
		conversionMap.put("Detroit", "det");
		conversionMap.put("GreenBay", "gnb");
		conversionMap.put("LosAngeles", "ram");
		conversionMap.put("NYGiants", "nyg");
		conversionMap.put("NYYanks", "nyy");
		conversionMap.put("Philadelphia", "phi");
		conversionMap.put("Pittsburgh", "pit");
		conversionMap.put("SanFrancisco", "sfo");
		conversionMap.put("Washington", "was");
		conversionMap.put("Chicago", "chi");
		conversionMap.put("Dallas", "dal");
		conversionMap.put("DalTexans", "dtx");
		conversionMap.put("Baltimore", "clt");
		conversionMap.put("Boston", "nwe");
		conversionMap.put("Buffalo", "buf");
		conversionMap.put("DalCowboys", "_DalCowboys");
		conversionMap.put("Denver", "den");
		conversionMap.put("Houston", "oti");
		conversionMap.put("LAChargers", "sdg");
		conversionMap.put("LARams", "ram");
		conversionMap.put("NYTitans", "nyj");
		conversionMap.put("Oakland", "rai");
		conversionMap.put("StLouis", "crd");
		conversionMap.put("Minnesota", "min");
		conversionMap.put("SanDiego", "sdg");
		conversionMap.put("KansasCity", "kan");
		conversionMap.put("NYJets", "nyj");
		conversionMap.put("Atlanta", "atl");
		conversionMap.put("Miami", "mia");
		conversionMap.put("NewOrleans", "nor");
		conversionMap.put("Cincinnati", "cin");
		conversionMap.put("NewEngland", "nwe");
		conversionMap.put("Seattle", "sea");
		conversionMap.put("TampaBay", "tam");
		conversionMap.put("LARaiders", "rai");
		conversionMap.put("Indianapolis", "clt");
		conversionMap.put("Phoenix", "crd");
		conversionMap.put("Carolina", "car");
		conversionMap.put("Jacksonville", "jax");
		conversionMap.put("Tennessee", "oti");
		conversionMap.put("LasVegas", "rai");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");
		conversionMap.put("", "");

		File dir = new File("uniforms");
		for (File file : dir.listFiles((d, name) -> name.endsWith(".png")))
		{
			String [] fileNameSplit = file.getName().split("_|\\.");
			if (fileNameSplit.length > 1)
			{
				String teamAbbr = convertUniformAbbrToTeamAbbr(fileNameSplit[1]);
				File renamedFile = new File(file.getParent(), teamAbbr.toUpperCase() + "-" + fileNameSplit[0] + ".png");
				System.out.println("Renaming " + file.getName() + " to " + renamedFile.getName());
				file.renameTo(renamedFile);
			}
		}
	}

	private static String convertUniformAbbrToTeamAbbr(String uniformAbbr)
	{
		String ret = conversionMap.get(uniformAbbr);
		if (ret == null)
			throw new IllegalArgumentException(uniformAbbr);
		
		return ret;
	}

}
