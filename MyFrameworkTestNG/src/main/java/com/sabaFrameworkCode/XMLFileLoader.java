/*
 * XMLFileLoader.java
 *
 * Created on May 17, 2001, 10:55 AM
 */

package com.sabaFrameworkCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class for loading XML DOcuments
 */
public class XMLFileLoader extends Object {

	/** Creates new XMLFileLoader */

	public XMLFileLoader() {
	}

	/**
	 * Loads an XML document
	 * 
	 * @param path
	 *            the file path
	 * @return the Document
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document loadFile(String path) throws FileNotFoundException, SAXException, IOException {
		// this factory is not thread safe
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(path);
			FileReader reader = new FileReader(f);
			InputSource is = new InputSource(reader);
			return builder.parse(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns an empty Document
	 * 
	 * @return
	 */
	public Document getEmptyDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}