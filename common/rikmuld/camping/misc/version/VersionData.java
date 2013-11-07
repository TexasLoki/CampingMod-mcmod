package rikmuld.camping.misc.version;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rikmuld.camping.core.lib.ModInfo;
import rikmuld.camping.core.register.ModLogger;

public class VersionData  implements Runnable {

	private static VersionData instance = new VersionData();
	
	Document doc;
	
	boolean checked = false;
	int check = 0;

	public static String NEW_VERSION = "Not Found";

	public void GetXmlFile()
	{
		try
		{
			URL url = new URL("http://rikmuld.com/assets/files/version.xml");
			URLConnection conn = url.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(conn.getInputStream());
		}
		catch(IOException | ParserConfigurationException | SAXException e)
		{
			ModLogger.log(Level.WARNING, "Whooops, something whent wrong while cheking the version! "+Integer.toString(2-check)+((2-check==1)? " attempt":" attempts")+" left!");
		}
	}

	public void CheckVersion()
	{
		GetXmlFile();
		if(doc!=null)
		{
			NodeList Version = doc.getElementsByTagName("Version");

			Node NewestVersion = Version.item(0);

			String NewVersion = NewestVersion.getTextContent();

			if(!NewVersion.equals(ModInfo.MOD_VERSION))
			{
				this.NEW_VERSION = NewVersion;
			}
			
			checked = true;
		}
	}

	@Override
	public void run()
	{		
		while(checked==false)
		{
			CheckVersion();
			check++;
			if(checked==false)
			{
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			if(check>=3)
			{
				break;
			}
		}
	}

	public void execute()
	{
		new Thread(instance).start();
	}
}