package org.example;

import io.undertow.Undertow;
import org.xml.sax.SAXException;
import queries.QueryManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static path.PathHandler.createPathHandler;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, XPathExpressionException, NoSuchPaddingException, IllegalBlockSizeException, ParserConfigurationException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException, SAXException {

        // Initialising the query manager class
        QueryManager queryManager = new QueryManager();

        // Creating an Undertow server and set the root handler
        Undertow server = Undertow.builder()
                .addHttpListener(7080, "localhost")
                .setHandler(createPathHandler(QueryManager.getConnection()))
                .build();

        server.start();
    }
}

