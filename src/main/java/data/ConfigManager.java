package data;

import encryption.EncryptConfigsXml;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.xpath.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ConfigManager {
    private Document doc;
    private XPath xPath;
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public ConfigManager() throws ParserConfigurationException, IOException, SAXException, NoSuchAlgorithmException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(new File("configs/config.xml"));

        XPathFactory xPathfactory = XPathFactory.newInstance();
        xPath = xPathfactory.newXPath();

        this.secretKey = EncryptConfigsXml.generateSecretKey();
        this.ivParameterSpec = EncryptConfigsXml.generateIvParameterSpec();
    }

    public String getDriverClass() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/DRIVER_CLASS");
        return expr.evaluate(doc);
    }

    public String getConnectionURL() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/CONNECTION_URL");
        return expr.evaluate(doc);
    }

    public String getUndertowHost() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/SERVER/UNDERTOW_HOST");
        return expr.evaluate(doc);
    }

    public String getUndertowPort() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/SERVER/UNDERTOW_PORT");
        return expr.evaluate(doc);
    }

    public String getBasePathUrl() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/API/BASE_PATH");
        return expr.evaluate(doc);
    }

    public String getDatabaseName() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/DATABASE_NAME");
        Node databaseNameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedDbName = null;
        if (isEncrypted((Element) databaseNameNode)) {
            // If encrypted, decrypt the value
            String encryptedDbName = databaseNameNode.getTextContent();
            decryptedDbName = EncryptConfigsXml.decrypt(encryptedDbName, secretKey, ivParameterSpec);
        } else {
            // If not encrypted, return the clear-text value
            decryptedDbName = databaseNameNode.getTextContent();
        }
        return decryptedDbName;
    }

    public String getUsername() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/USERNAME");
        Node usernameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedUsername = null;
        if (isEncrypted((Element) usernameNode)) {
            // If encrypted, decrypt the value
            String encryptedUsername = usernameNode.getTextContent();
            decryptedUsername = EncryptConfigsXml.decrypt(encryptedUsername, secretKey, ivParameterSpec);
        } else {
            // If not encrypted, return the clear-text value
            decryptedUsername = usernameNode.getTextContent();
        }
        return decryptedUsername;
    }

    public String getPassword() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/PASSWORD");
        Node passwordNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedPassword = null;
        if (isEncrypted((Element) passwordNode)) {
            // If encrypted, decrypt the value
            String encryptedPassword = passwordNode.getTextContent();
            decryptedPassword = EncryptConfigsXml.decrypt(encryptedPassword, secretKey, ivParameterSpec);
        } else {
            // If not encrypted, return the clear-text value
            decryptedPassword = passwordNode.getTextContent();
        }
        return decryptedPassword;
    }

    // Checking for the Encryption Status
    private boolean isEncrypted(Element element) {
        String typeAttribute = element.getAttribute("TYPE");
        return "ENCRYPTED".equalsIgnoreCase(typeAttribute);
    }
}
