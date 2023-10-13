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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

    public String getDatabaseName() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/DATABASE_NAME");
        Node databaseNameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedDbName = null;
        if (!isEncrypted((Element) databaseNameNode)) {
            // If not encrypted, return the clear-text value
            String encryptedDbName = EncryptConfigsXml.encrypt(databaseNameNode.getTextContent(), secretKey, ivParameterSpec);
            databaseNameNode.setTextContent(encryptedDbName);
            ((Element) databaseNameNode).setAttribute("TYPE", "ENCRYPTED");
            saveDocument();

            System.out.println(encryptedDbName + ": Updated and Encrypted Db Name");

            decryptedDbName = EncryptConfigsXml.decrypt(encryptedDbName, secretKey, ivParameterSpec);
            System.out.println(decryptedDbName + "--decrypted Db Name");
        }
        return decryptedDbName;
    }

    public String getUsername() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/USERNAME");
        Node usernameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedUsername = null;
        if (!isEncrypted((Element) usernameNode)) {
            // If not encrypted => encrypt username => set the attribute to "ENCRYPTED"
            String encryptedUsername = EncryptConfigsXml.encrypt(usernameNode.getTextContent(), secretKey, ivParameterSpec);
            usernameNode.setTextContent(encryptedUsername);
            ((Element) usernameNode).setAttribute("TYPE", "ENCRYPTED");
            saveDocument();

            System.out.println(encryptedUsername + ": Updated and Encrypted Username");

            decryptedUsername = EncryptConfigsXml.decrypt(encryptedUsername, secretKey, ivParameterSpec);
            System.out.println(decryptedUsername + "--decryptedUsername");
        }
        return decryptedUsername;
    }

    public String getPassword() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/PASSWORD");
        Node passwordNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedPassword = null;
        if (!isEncrypted((Element) passwordNode)) {
            // If not encrypted => encrypt => set the attribute to "ENCRYPTED"
            String encryptedPassword = EncryptConfigsXml.encrypt(passwordNode.getTextContent(), secretKey, ivParameterSpec);
            passwordNode.setTextContent(encryptedPassword);
            ((Element) passwordNode).setAttribute("TYPE", "ENCRYPTED");
            saveDocument();

            System.out.println(encryptedPassword + ": Updated and Encrypted Password");

            decryptedPassword = EncryptConfigsXml.decrypt(encryptedPassword, secretKey, ivParameterSpec);
            System.out.println(decryptedPassword + "--decryptedPassword--");

        }
        return decryptedPassword;
    }

    // Checking for the Encryption Status
    private boolean isEncrypted(Element element) {
        String typeAttribute = element.getAttribute("TYPE");
        return "ENCRYPTED".equalsIgnoreCase(typeAttribute);
    }

    private void saveDocument() throws TransformerException {
        // Writing the updated document back to the XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("configs/config.xml"));
        transformer.transform(source, result);
    }
}
