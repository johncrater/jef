package jef.history;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.commons.io.FileUtils;

public class UniformImageDownloader
{

	public UniformImageDownloader()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		String [] teams = new String [] 
				{
//						"Baltimore",
//						"ChiBears",
//						"ChiCardinals",
//						"Cleveland",
//						"Detroit",
//						"GreenBay",
//						"LosAngeles",
//						"NYGiants",
//						"NYYanks", 
//						"Philadelphia",
//						"Pittsburgh",
//						"SanFrancisco",
//						"Washington",
//						"Dallas",
//						"DalCowboys",
//						"StLouis",
//						"Minnesota",
//						"Atlanta",
//						"NewOrleans",
//						"NewEngland",
//						"Seattle",
//						"TampaBay",
//						"Indianapolis",
//						"LARaiders",
						"Phoenix",
//						"Carolina",
//						"Jacksonville",
//						"Tennessee",
//						
//						
//						
//						"Boston",
//						"Buffalo",
//						"DalTexans",
//						"Denver",
//						"Houston",
//						"LAChargers",
//						"NYTitans",
//						"Oakland",
//						"SanDiego",
//						"KansasCity",
//						"NYJets",
//						"Miami",
//						"Cincinnati",
//						
//						
//						"ofc",
						"LasVegas",
						"Chicago",
						"LARams",
				};
		
		String url = "https://www.gridiron-uniforms.com/GUD/images/%d_%s.png";
		
		for (int i = 1951; i < 2024; i++)
		{
			for (String city : teams)
			{
				String teamUrl = String.format(url, i, city);

				try
				{
					byte [] image = loadLogo(teamUrl);
					final var file = new File("uniforms/" + teamUrl.substring(teamUrl.lastIndexOf("/") + 1));
					FileUtils.writeByteArrayToFile(file, image);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static final HttpClient client = HttpClient.newHttpClient();

	private static byte[] loadLogo(final String url) throws IOException, InterruptedException
	{
		final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		final HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
		return response.body();
	}

}
