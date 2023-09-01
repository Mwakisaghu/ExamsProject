package data;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class ConfigReader {
    private Document d;

    public ConfigReader() {
        try {
            File inputFile = new File("configs/config.xml");

            // Loading the xml config file
            // Create a DocumentBuilder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder= dbf.newDocumentBuilder();

            d = dBuilder.parse(inputFile);
            d.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDriverClass () throws XPathExpressionException {
        XPath xp = XPathFactory.newInstance().newXPath();
        return xp.compile("/CONFIG/DB/DRIVER_CLASS").evaluate(d);
    }

    public String getConnectionURL () throws  XPathExpressionException {
        XPath xp = XPathFactory.newInstance().newXPath();
        return  xp.compile("/CONFIG/DB/CONNECTION_URL").evaluate(d);
    }

    public String getUsername () throws XPathExpressionException {
        XPath xp = XPathFactory.newInstance().newXPath();
        return  xp.compile("/CONFIG/DB/USERNAME").evaluate(d);
    }

    public String getPassword () throws  XPathExpressionException {
        XPath xp = XPathFactory.newInstance().newXPath();
        return  xp.compile("/CONFIG/DB/PASSWORD").evaluate(d);
    }
}
