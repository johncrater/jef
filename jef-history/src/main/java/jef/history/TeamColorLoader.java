package jef.history;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.input.DOMBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

public class TeamColorLoader
{

	public TeamColorLoader()
	{
		// TODO Auto-generated constructor stub
	}

	private static List<HistoricalLeague> leagues;

	public static void main(String[] args) throws Exception
	{
		leagues = Database.getDefaultDatabase().loadUpEverything();

		File file = new File(
				"uniforms/view-source_https___www.trucolor.net_portfolio_american-football-league-official-colors-1960-through-1969_.html");

		String text = FileUtils.readFileToString(file, Charset.defaultCharset());
		final var doc = createDocument(text);
		Document subDoc = getSubXml(doc);
		List<Element> div0s = XPath.selectNodes(subDoc, "/html/div/div[1][@title]");
		for (Element div0 : div0s)
		{
			String teamName = div0.getAttributeValue("title");
			String[] split = teamName.split("\\(");
			teamName = split[0];

			List<Element> div1s = XPath.selectNodes(div0, "../div[2]/div/div[2]/div/div[1]");
			for (Element div1 : div1s)
			{
				String title = div1.getAttributeValue("title");
				String [] titleSplit = title.split(":");
				String years = titleSplit[0];
		
				int startYear = 0;
				int endYear = 0;
		
				String[] yearsSplit = years.split(" through ");
				if (yearsSplit.length == 1)
				{
					startYear = endYear = Integer.parseInt(years.trim());
				}
				else if (yearsSplit.length == 2)
				{
					startYear = Integer.parseInt(yearsSplit[0].trim());
					endYear = Integer.parseInt(yearsSplit[1].trim());
				}
				else
				{
					System.out.println(yearsSplit);
					throw new RuntimeException();
				}

				StringBuffer query = new StringBuffer();
				query.append("INSERT INTO jfg.historical_teams (color_home_pants, color_home_shirt, color_home_number, color_home_trim, color_away_pants, color_away_shirt, color_away_number, color_away_trim) VALUES (");
				List<Element> div2s = XPath.selectNodes(div1, "../div[2]/div/div/div/div[1]");
				for (Element div2 : div2s)
				{
					String style = div2.getAttributeValue("style");
					String [] styleAttributes = style.trim().split(";");
					String backgroundColor = styleAttributes[0].trim();		
					String color = backgroundColor.split(":")[1].trim();
					System.out.println("\t" + color);
					if ("#FFFFFF".equals(color))
						break;
				}
			}
		}
	}

	private static HistoricalTeam findTeam(String teamName, int year)
	{
		teamName = teamName.toLowerCase();
		for (HistoricalLeague league : leagues)
		{
			for (HistoricalConference conference : league.getConferences(year))
			{
				for (HistoricalDivision division : conference.getAllDivisions())
				{
					for (HistoricalTeam team : division.getAllTeams())
					{
						if (teamName.contains(team.getLocation().toLowerCase())
								&& teamName.contains(team.getNickname().toLowerCase()))
						{
							return team;
						}
					}
				}
			}
		}
		
		System.out.println();
		return null;
	}

	private static Document getSubXml(Document doc) throws Exception
	{
		StringBuilder ret = new StringBuilder();

		List<Element> nodes = XPath.selectNodes(doc, "//span[@class='html-tag']");
		for (Element e : nodes)
		{
			for (Object c : e.getContent())
			{
				if (c instanceof Text t)
				{
					ret.append(t.getText());
				}
				else if (c instanceof Element element)
				{
					ret.append(element.getText());
				}
			}
		}

		String retString = ret.toString();
		retString = retString.replaceAll("&lt;", "<");
		retString = retString.replaceAll("&gt;", ">");
		retString = retString.replaceAll("<strong>", "");
		retString = retString.replaceAll("</strong>", "");
		return createDocument(retString);
	}

	private static Document createDocument(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		xml = xml.replace("&", "&amp;");
		final var domfactory = DocumentBuilderFactory.newInstance();
		domfactory.setValidating(false);
		domfactory.setNamespaceAware(true);
		final var docbuilder = domfactory.newDocumentBuilder();
		return new DOMBuilder().build(docbuilder.parse(new ByteArrayInputStream(xml.getBytes())));
	}

}
