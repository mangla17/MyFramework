/*
 * Created on May 6, 2003
 *
 */
package com.sabaFrameworkCode;

import java.util.Hashtable;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;

/** Represents a cache for XPath objects. This API
 * has methods for caching Jaxen XPath objects. 
 * @author sszego
 * May 6, 2003
 */
public class XPathCache {

	private Hashtable mCache = new Hashtable(10);
	/**
	 * Sets the JaxenXpath object for a give xpath string
	 * @param path the xpath string
	 * @param xpath the Jaxen XPath instance
	 */
	public void setJaxenXPath(String path, org.jaxen.XPath xpath) {
		mCache.put(path,xpath);
	}
	/**
	 * Returns the Jaxen XPath for a give xpath string
	 * @param path the XPtath string
	 * @return the Jaxen Xpath instance
	 * @throws JaxenException
	 */
	public org.jaxen.XPath getJaxenXPath(String path) throws JaxenException {
		org.jaxen.XPath xpath = (org.jaxen.XPath)mCache.get(path);
		if (xpath == null) {
			xpath = XPathCache.createJaxenXPath(path);
			mCache.put(path,xpath);
		}
		return xpath;
	}
	/**
	 * Creates a new Jaxen XPath oject
	 * @param path the xpath string
	 * @return Jaxen XPath
	 * @throws JaxenException
	 */
	public static org.jaxen.XPath createJaxenXPath(String path) throws JaxenException {
		DOMXPath xp = new DOMXPath(path);
		xp.addNamespace("wdk","http://www.saba.com/XML/WDK");
		xp.addNamespace("wdktags","http://www.saba.com/XML/WDK/taglib");
		xp.addNamespace("xsp","http://www.apache.org/1999/XSP/Core");
		xp.addNamespace("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		xp.addNamespace("xsl","http://www.w3.org/1999/XSL/Transform");
		return xp;
		
	}
}
