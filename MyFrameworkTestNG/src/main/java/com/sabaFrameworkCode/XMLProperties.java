/*
 * XMLProperties.java
 *
 * Created on April 19, 2001, 10:01 AM
 */

package com.sabaFrameworkCode;

import org.w3c.dom.*; //import com.saba.util.*;
import org.xml.sax.SAXException;

import java.lang.Exception.*;
import java.util.Hashtable;

/**
 * The superclass of all XMLProperty files.
 */
public class XMLProperties extends Object implements java.io.Serializable {
	Node mRootProperty = null;
	/**
	 * Cache the properties for faster access. Need to be hashtable because we
	 * need to support concurrent access
	 */
	Hashtable mStringProperties = new Hashtable(89);
	Hashtable mSubProperties = new Hashtable(89);

	/**
	 * Constructs a new XMLProperties object
	 */
	public XMLProperties() {
		Document doc = (new XMLFileLoader()).getEmptyDocument();
		mRootProperty = (Node) doc.getDocumentElement();
	}

	/**
	 * Constructs a new XMLProperties object with the Node as its root node
	 * 
	 * @param node
	 *            the rootNode
	 */
	public XMLProperties(Node node) {
		mRootProperty = node;
	}

	/**
	 * Returns the node representing the root of all properties
	 * 
	 * @return the root node.
	 */
	public Node getRootPropertyNode() {
		return mRootProperty;
	}

	/**
	 * An XMLProperty object has an underlying Node that contains all properties
	 * 
	 * @param rootProperty
	 *            The underlying node containing all properties
	 */
	public void setRootPropertyNode(Node rootProperty) {
		mRootProperty = rootProperty;
	}

	/**
	 * Returns the text value of the node that is retrieved by following the
	 * XPath from the root node.
	 * 
	 * @param path
	 *            The XPath to follow from the root node
	 * @throws SabaException
	 *             if following the XPath expression causes an error
	 * @return the text value of the selected node
	 */
	public String getPropertyText(String path) {
		try {
			String value = (String) mStringProperties.get(path);
			if (value == null) {
				value = XMLUtil.getTextValue(mRootProperty, path);
				if (value != null)
					mStringProperties.put(path, value);
			}
			return value;
		} catch (SAXException saxEx) {
			throw new RuntimeException(saxEx);
		}
	}

	/**
	 * Sets the property to the text value
	 * 
	 * @param path
	 *            The path to select the property
	 * @param value
	 *            The new text value
	 * @throws SabaException
	 *             If an error occurs
	 */
	public void setPropertyText(String path, String value) {
		try {
			Node n = XMLUtil.getNode(mRootProperty, path);
			if (n == null) {
				throw new UnsupportedOperationException("Cannot set a non-existent node!");
			} else {
				XMLUtil.setTextValue(n, value);
			}
		} catch (SAXException saxEx) {
			throw new RuntimeException(saxEx);
		}
	}

	/**
	 * Returns a subtree of this property as an XMLProperties object
	 * 
	 * @return the XMLProperty object corresponding to the subtree
	 * @param path
	 *            The path to select the subtree
	 * @throws SabaException
	 *             If an error occurs
	 */
	public XMLProperties getSubXMLProperties(String path) {
		try {
			XMLProperties props = (XMLProperties) mSubProperties.get(path);
			if (props == null) {
				Node n = XMLUtil.getNode(mRootProperty, path);
				if (n == null) {
					return null;
				}
				props = new XMLProperties(n);
				mSubProperties.put(path, props);
			}
			return props;
		} catch (SAXException saxEx) {
			throw new RuntimeException(saxEx);
		}
	}
}