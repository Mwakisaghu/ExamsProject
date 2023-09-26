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
    private static Document doc;
    private static XPath xPath;
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public ConfigManager() throws ParserConfigurationException, IOException, SAXException, NoSuchAlgorithmException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(new File("configs/config.xml"));

        XPathFactory xPathfactory = XPathFactory.newInstance();
        xPath = xPathfactory.newXPath();

        // Initialize secretKey and ivParameterSpec
        secretKey = EncryptConfigsXml.generateSecretKey();
        System.out.println("Secret Key generated on !st attempt :" + secretKey);
        ivParameterSpec = EncryptConfigsXml.generateIvParameterSpec();
    }

    // Read config file elements
    public static String getDriverClass() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/DRIVER_CLASS");
        return expr.evaluate(doc);
    }

    public static String getConnectionURL() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/CONNECTION_URL");
        return expr.evaluate(doc);
    }

    public static String getUsername() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/USERNAME");
        return expr.evaluate(doc);
    }

    public static String getPassword() throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/PASSWORD");
        return expr.evaluate(doc);
    }

    // Check and update the username
    public void updateUsername(String newUsername) throws XPathExpressionException, TransformerException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/USERNAME");
        Node usernameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (!isEncrypted((Element) usernameNode)) {
            // If not encrypted, encrypt it and set the attribute to "ENCRYPTED"
            String encryptedUsername = EncryptConfigsXml.encrypt(getUsername(), secretKey, ivParameterSpec);
            usernameNode.setTextContent(encryptedUsername);
            ((Element) usernameNode).setAttribute("TYPE", "ENCRYPTED");
            saveDocument();

            System.out.println(encryptedUsername + ": Encrypted Username");
        } else {
            // If already encrypted, just print it
//            System.out.println("Username (Encrypted): " + usernameNode.getTextContent());

            // Decrypt
            String decryptedUsername = EncryptConfigsXml.decrypt(usernameNode.getTextContent(), secretKey, ivParameterSpec);
            System.out.println("decryptedUsername :" + decryptedUsername);
        }
    }

    // Check and update the password
    public void updatePassword(String newPassword) throws XPathExpressionException, TransformerException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/PASSWORD");
        Node passwordNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (!isEncrypted((Element) passwordNode)) {
            // If not encrypted, encrypt it and set the attribute to "ENCRYPTED"
            String encryptedPassword = EncryptConfigsXml.encrypt(getPassword(), secretKey, ivParameterSpec);
            passwordNode.setTextContent(encryptedPassword);
            ((Element) passwordNode).setAttribute("TYPE", "ENCRYPTED");
            saveDocument();

            System.out.println(encryptedPassword + ": Encrypted Password");
            System.out.println(secretKey + "used for encryption");
        } else {
            // If already encrypted - just print it
//            System.out.println("Username (Encrypted): " + passwordNode.getTextContent());

            // Decrypt the password
            String decryptedPassword = EncryptConfigsXml.decrypt(passwordNode.getTextContent(), secretKey, ivParameterSpec);
            System.out.println(secretKey + ": used for decryption");
            System.out.println("Decrypted Password: " + decryptedPassword);
        }
    }

    // Save and update the document
    private void saveDocument() throws TransformerException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            // Specify the correct file path for saving the updated XML
            StreamResult result = new StreamResult(new File("configs/config.xml"));
            transformer.transform(source, result);
            System.out.println("Success - Update!!");
        } catch (TransformerException e) {
            e.printStackTrace();
            System.err.println("Update was not successful");
        }
    }

    // Checking encryption status
    private boolean isEncrypted(Element element) {
        String typeAttribute = element.getAttribute("TYPE");
        return "ENCRYPTED".equalsIgnoreCase(typeAttribute);
    }
}