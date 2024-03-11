package data;

import encryption.EncryptConfigsXml;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.crypto.*;
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
import java.util.HashMap;
import java.util.Map;

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

        // Retrieving the secret key and IV from the encryption class - if already generated
        this.secretKey = EncryptConfigsXml.getSecretKey();
        this.ivParameterSpec = EncryptConfigsXml.getIvParameterSpec();

        // Check if the secret key and iv are null
        if (this.secretKey == null || this.ivParameterSpec == null) {
            this.secretKey = EncryptConfigsXml.generateSecretKey();
            this.ivParameterSpec = EncryptConfigsXml.generateIvParameterSpec();

            // Saving the generated secret key and IV
            EncryptConfigsXml.setSecretKey(secretKey);
            EncryptConfigsXml.setIvParameterSpec(ivParameterSpec);
        }
        System.out.println("Secret Key (Encryption): " + secretKey);
        System.out.println("IV Parameter (Encryption): " + ivParameterSpec);
    }

    // Retrieving config elements
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
        if (databaseNameNode != null) {
            String encryptedDbName = databaseNameNode.getTextContent();
            if (isEncrypted((Element) databaseNameNode)) {
                // If encrypted, decrypt the value
                decryptedDbName = EncryptConfigsXml.decrypt(encryptedDbName, secretKey, ivParameterSpec);
                System.out.println("Decrypted database name: " + decryptedDbName);
            } else {
                // If not encrypted, return the clear-text value
                decryptedDbName = encryptedDbName;
                System.out.println("Database name retrieved from config XML: " + decryptedDbName);
            }
        }
        return decryptedDbName;
    }

    public String getUsername() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/USERNAME");
        Node usernameNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedUsername = null;
        if (usernameNode != null) {
            String encryptedUsername = usernameNode.getTextContent();
            if (isEncrypted((Element) usernameNode)) {
                // If encrypted, decrypt the value
                decryptedUsername = EncryptConfigsXml.decrypt(encryptedUsername, secretKey, ivParameterSpec);
                System.out.println("Decrypted username: " + decryptedUsername);
            } else {
                // If not encrypted, return the clear-text value
                decryptedUsername = encryptedUsername;
                System.out.println("Username retrieved from config XML: " + decryptedUsername);
            }
        }
        return decryptedUsername;
    }

    public String getPassword() throws XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException {
        XPathExpression expr = xPath.compile("/CONFIG/DB/PASSWORD");
        Node passwordNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String decryptedPassword = null;
        if (passwordNode != null) {
            String encryptedPassword = passwordNode.getTextContent();
            if (isEncrypted((Element) passwordNode)) {
                // If encrypted, decrypt the value
                decryptedPassword = EncryptConfigsXml.decrypt(encryptedPassword, secretKey, ivParameterSpec);
                System.out.println("Decrypted password: " + decryptedPassword);
            } else {
                // If not encrypted, return the clear-text value
                decryptedPassword = encryptedPassword;
                System.out.println("Password retrieved from config XML: " + decryptedPassword);
            }
        }
        return decryptedPassword;
    }

    // Updating encrypted values in the config XML
    public void updateConfig() throws XPathExpressionException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, TransformerException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        updateEncryptedValue("/CONFIG/DB/USERNAME");
        updateEncryptedValue("/CONFIG/DB/PASSWORD");
        updateEncryptedValue("/CONFIG/DB/DATABASE_NAME");

        writeChangesToFile();
    }

    private void updateEncryptedValue(String xpathExpression) throws XPathExpressionException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        XPathExpression expr = xPath.compile(xpathExpression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (node != null && !isEncrypted((Element) node)) {
            String clearTextValue = node.getTextContent();
            String encryptedValue = EncryptConfigsXml.encrypt(clearTextValue, secretKey, ivParameterSpec);
            node.setTextContent(encryptedValue);
            ((Element) node).setAttribute("TYPE", "ENCRYPTED");
            System.out.println("Encrypted value updated for " + xpathExpression);
        }
    }

    // Register a new user with encrypted credentials
    public void registerUser(String username, String password) throws TransformerException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, XPathExpressionException {
        XPathExpression expr = xPath.compile("/CONFIG/USERS");
        Node usersNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        Element userNode = doc.createElement("USER");

        // Encrypt username and password
        String encryptedUsername = EncryptConfigsXml.encrypt(username, secretKey, ivParameterSpec);
        String encryptedPassword = EncryptConfigsXml.encrypt(password, secretKey, ivParameterSpec);

        // Create username element
        Element usernameNode = doc.createElement("USERNAME");
        usernameNode.setAttribute("TYPE", "ENCRYPTED");
        usernameNode.setTextContent(encryptedUsername);

        // Create password element
        Element passwordNode = doc.createElement("PASSWORD");
        passwordNode.setAttribute("TYPE", "ENCRYPTED");
        passwordNode.setTextContent(encryptedPassword);

        // Append username and password elements to user node
        userNode.appendChild(usernameNode);
        userNode.appendChild(passwordNode);

        // Append user node to users node
        usersNode.appendChild(userNode);

        // Write changes to the XML file
        writeChangesToFile();
    }

    // Retrieve decrypted username and password for authentication
    public Map<String, String> getUserCredentials(String username) throws XPathExpressionException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        XPathExpression expr = xPath.compile("/CONFIG/USERS/USER[USERNAME='" + username + "']");
        Node userNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (userNode != null) {
            NodeList childNodes = userNode.getChildNodes();
            String decryptedUsername = null;
            String decryptedPassword = null;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                String nodeName = childNode.getNodeName();
                String nodeValue = childNode.getTextContent();
                if ("USERNAME".equals(nodeName)) {
                    decryptedUsername = EncryptConfigsXml.decrypt(nodeValue, secretKey, ivParameterSpec);
                } else if ("PASSWORD".equals(nodeName)) {
                    decryptedPassword = EncryptConfigsXml.decrypt(nodeValue, secretKey, ivParameterSpec);
                }
            }
            if (decryptedUsername != null && decryptedPassword != null) {
                Map<String, String> credentials = new HashMap<>();
                credentials.put("username", decryptedUsername);
                credentials.put("password", decryptedPassword);
                return credentials;
            }
        }
        return null;
    }

    private void writeChangesToFile() throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("configs/config.xml"));
        transformer.transform(source, result);
    }

    // Checking for the Encryption Status
    private boolean isEncrypted(Element element) {
        String typeAttribute = element.getAttribute("TYPE");
        return "ENCRYPTED".equalsIgnoreCase(typeAttribute);
    }
}
