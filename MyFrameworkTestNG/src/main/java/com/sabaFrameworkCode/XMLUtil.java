/*
 * XMLUtil.java
 *
 * Created on April 19, 2001, 12:02 PM
 */

package com.sabaFrameworkCode;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

//import org.apache.cocoon.xml.util.XPathAPI;

//import com.saba.exception.SabaRuntimeException;

/**
 * Utilities to deal with XML and DOM.
 */
public class XMLUtil extends Object {

	/* Constants */
	public static final String kOpenStartTag = "<";
	public static final String kOpenEndTag = "</";
	public static final String kCloseTag = ">";

	/**
	 * maps documents to CachedXPathAPI objects
	 */
	public static final java.util.Map mXPathContexts = new Hashtable();
	public static final java.util.Hashtable mDocCache = new Hashtable();

	/**
	 * Creates end tag
	 * 
	 * @param tagName
	 *            the end tag name
	 * @return end tag
	 */
	public static StringBuffer buildEndTag(String tagName) {
		return buildTag("/" + tagName, null);
	}

	/**
	 * Creates a tag
	 * 
	 * @param tagName
	 *            the name fo the tag
	 * @return built tag as a StringBuffer
	 */
	public static StringBuffer buildTag(String tagName) {
		return buildTag(tagName, null);
	}

	/**
	 * Creates a tag
	 * 
	 * @param tagName
	 *            tag
	 * @param attributes
	 *            attributes
	 * @return the tag
	 */
	public static StringBuffer buildTag(String tagName, String[][] attributes) {
		StringBuffer xmlBuf = new StringBuffer(32);
		xmlBuf.append(kOpenStartTag);
		xmlBuf.append(tagName);
		if (attributes != null) {
			int numAttrs = attributes.length;
			for (int i = 0; i < numAttrs; i++) {
				xmlBuf.append(" ");
				xmlBuf.append(attributes[i][0]);
				xmlBuf.append("=\"");
				xmlBuf.append(attributes[i][1]);
				xmlBuf.append("\"");
			}
		}
		xmlBuf.append(kCloseTag);
		return xmlBuf;
	}

	/**
	 * Creates PCData element
	 * 
	 * @param tagName
	 *            The name of the tag for this element
	 * @param attributes
	 *            A two dimensional array of attributes. Each array element is
	 *            name-value pair for attribute.
	 * @param value
	 *            The value for the element.
	 */
	// 2do: make more general:
	// 2do: allow another element instead of value?
	public static StringBuffer buildPCDATAElement(String tagName, String[][] attributes, String value) {
		StringBuffer xmlBuf = new StringBuffer(64);

		// open the element
		xmlBuf.append(buildTag(tagName, attributes));

		// now put in the value
		xmlBuf.append(value);

		// close the element
		xmlBuf.append(buildEndTag(tagName));

		return xmlBuf;
	}

	/**
	 * The prefix of variables in XPath expressions. The method substituteVars
	 * uses this value to locate variables in the XPath expression.
	 */
	public static final String kVarPrefix = "$";

	/**
	 * Returns the text value of a node by combining the text subnodes of the
	 * node n.
	 * 
	 * @return The text value of the node
	 * @param element
	 *            The node whose text value we want to get
	 */
	public static String getTextValue(Node element) {
		if (element == null)
			return "";
		StringBuffer text = new StringBuffer(64);
		switch (element.getNodeType()) {
			case Node.ATTRIBUTE_NODE:
				text.append(((Attr) element).getValue());
				break;
			case Node.ELEMENT_NODE:
				Node n = element.getFirstChild();
				while (n != null) {
					if (n.getNodeType() == Node.CDATA_SECTION_NODE || n.getNodeType() == Node.TEXT_NODE) {
						String data = ((Text) n).getData();
						if (data == null)
							data = "";
						text.append(data);
					}
					n = n.getNextSibling();
				}
				break;
			case Node.TEXT_NODE:
				text.append(((Text) element).getData());
				break;
			default:
				break;
		}
		return text.toString();
	}

	/**
	 * Returns the text value of a node that can be accessed from the node n
	 * through the XPath expression path.
	 * 
	 * @param n
	 *            The root node
	 * @param path
	 *            The XPath expression
	 * @throws SAXException
	 *             If there is an error selecting the node
	 * @return The text value of the node found. If no node is found, then the
	 *         empty string is returned
	 * @deprecated use getTextValue(Node,String,XPathCache) instead
	 */
	public static String getTextValue(Node n, String path) throws SAXException {
		try {
			if (n == null)
				return "";
			if (path.equals(""))
				path = ".";
			org.jaxen.XPath xpath = XPathCache.createJaxenXPath(path);
			return getTextValueJaxen(n, xpath);
		} catch (JaxenException e) {
			e.printStackTrace();
			throw new SAXException(e.toString());
		}
	}

	/**
	 * Returns the text value of a node that can be accessed from the node n
	 * through the XPath expression path.
	 * 
	 * @param n
	 *            The root node
	 * @param path
	 *            The XPath expression
	 * @throws SAXException
	 *             If there is an error selecting the node
	 * @return The text value of the node found. If no node is found, then the
	 *         empty string is returned
	 * @deprecated use getTextValue(Node,String,XPathCache) instead
	 */
	public static String getTextValueWithBR(Node n, String path) throws SAXException {
		try {
			if (n == null)
				return "";
			if (path.equals(""))
				path = ".";
			org.jaxen.XPath xpath = XPathCache.createJaxenXPath(path);
			return getTextValueJaxenWithBR(n, xpath);
		} catch (JaxenException e) {
			e.printStackTrace();
			throw new SAXException(e.toString());
		}
	}

	/**
	 * Returns the text value of a node that can be accessed from the node n
	 * through the XPath expression path.
	 * 
	 * @param n
	 *            The root node
	 * @param path
	 *            The XPath expression
	 * @param cache
	 *            the XPath cache to use
	 * @throws SAXException
	 *             If there is an error selecting the node
	 * @return The text value of the node found. If no node is found, then the
	 *         empty string is returned
	 */
	public static String getTextValue(Node n, String path, XPathCache cache) throws SAXException {
		try {
			if (n == null)
				return "";
			if (path.equals(""))
				path = ".";
			org.jaxen.XPath xpath = cache.getJaxenXPath(path);
			if (xpath == null) {
				xpath = XPathCache.createJaxenXPath(path);
				cache.setJaxenXPath(path, xpath);
			}
			return getTextValueJaxen(n, xpath);
		} catch (JaxenException e) {
			e.printStackTrace();
			throw new SAXException(e.toString());
		}
	}

	/**
	 * Returns the text value of a node that can be accessed from the node n
	 * through the XPath expression path.
	 * 
	 * @param n
	 *            The root node
	 * @param path
	 *            The XPath expression
	 * @throws SAXException
	 *             If there is an error selecting the node
	 * @return The text value of the node found. If no node is found, then the
	 *         empty string is returned
	 */
	public static String[] getTextValues(Node n, String path) throws SAXException {
		String[] xxx = new String[0];
		if (n == null)
			return xxx;
		if (path.equals(""))
			path = ".";

		NodeList nl = null;
		try {
			nl = XPathAPI.selectNodeList(n, path);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nl == null)
			return xxx;

		int length = nl.getLength();
		String temp[] = new String[length];
		for (int i = 0; i < length; i++) {
			Node node = nl.item(i);
			temp[i] = getTextValue(node);
		}
		return temp;
	}

	private static String getTextValueJaxen(Node n, org.jaxen.XPath xpath) throws JaxenException {
		StringBuffer buffer = new StringBuffer(64);
		List nl = xpath.selectNodes(n);
		if (nl == null)
			return "";
		Iterator iter = nl.iterator();
		while (iter.hasNext()) {
			Node node = (Node) iter.next();
			buffer.append(getTextValue(node));
		}
		return buffer.toString();
	}

	private static String getTextValueJaxenWithBR(Node n, org.jaxen.XPath xpath) throws JaxenException {
		StringBuffer buffer = new StringBuffer(64);
		List nl = xpath.selectNodes(n);
		if (nl == null)
			return "";
		Iterator iter = nl.iterator();
		while (iter.hasNext()) {
			Node node = (Node) iter.next();
			buffer.append(getTextValue(node));
			Node brNode = node.getNextSibling();
			while (brNode != null && brNode.getNodeName().equalsIgnoreCase("br")) {
				buffer.append("\n");
				brNode = brNode.getNextSibling();
			}
		}
		return buffer.toString();
	}

	/**
	 * Same as getTextValue(Node n, String path) except it returns a default
	 * value if the node is null or if the text value of the node is empty
	 * string.
	 * 
	 * @param n
	 *            the node
	 * @param path
	 *            the xpath
	 * @param defaultValue
	 *            the default value
	 */
	public static String getTextValue(Node n, String path, String defaultValue) throws SAXException {
		String value = getTextValue(n, path);
		if (value == null || value.equals(""))
			return defaultValue;
		return value;
	}

	/**
	 * Sets the text value of a node. Note: All child nodes of the node are
	 * replaced by the text node containing the text value.
	 * 
	 * @param n
	 *            The node whose text value we want to set
	 * @param value
	 *            The text value.
	 */
	public static void setTextValue(Node n, String value) {
		NodeList nl = n.getChildNodes();
		Document d = n.getOwnerDocument();
		Text text = d.createTextNode(value);
		if (nl.getLength() != 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				n.removeChild(nl.item(0));
			}
		}
		n.appendChild(text);
	}

	/**
	 * Returns a node that can be accessed from the root node through the XPath
	 * path.
	 * 
	 * @param root
	 *            The root node
	 * @param path
	 *            An XPath expression
	 * @throws SAXException
	 *             If an error occurs during the traversal
	 * @return The node or null if no node matches the path
	 */
	public static Node getNode(Node root, String path) throws SAXException {
		try {
			Node n = getNodeJaxen(root, XPathCache.createJaxenXPath(path));
			return n;
		} catch (JaxenException e) {
			e.printStackTrace();
			throw new SAXException(e.toString());
		}
	}

	/**
	 * Returns a node that can be accessed from the root node through the XPath
	 * path.
	 * 
	 * @param root
	 *            The root node
	 * @param path
	 *            An XPath expression
	 * @param cache
	 *            the Xpath cache
	 * @throws SAXException
	 *             If an error occurs during the traversal
	 * @return The node or null if no node matches the path
	 */
	public static Node getNode(Node root, String path, XPathCache cache) throws SAXException {
		try {
			org.jaxen.XPath xpath = cache.getJaxenXPath(path);
			return getNodeJaxen(root, xpath);
		} catch (JaxenException e) {
			e.printStackTrace();
			throw new SAXException(e.toString());
		}
	}

	private static Node getNodeJaxen(Node root, org.jaxen.XPath xpath) throws JaxenException {
		return (Node) xpath.selectSingleNode(root);
	}

	/**
	 * Returns a node that can be accessed from the root node through the XPath
	 * path.
	 * 
	 * @param root
	 *            The root node
	 * @param path
	 *            An XPath expression
	 * @throws SAXException
	 *             If an error occurs during the traversal
	 * @return The node or null if no node matches the path
	 * @deprecated do not use this method
	 */
	public static NodeList getNodesDeprecated(Node root, String path) throws SAXException {
		try {
			if (root == null)
				return null;
			NodeList nl = getXPathAPI(root).selectNodeList(root, path);
			return nl;
		} catch (javax.xml.transform.TransformerException ex) {
			ex.printStackTrace();
			throw new SAXException("");
		}
	}

	/**
	 * Substitutes variables in an XPath expression. The variables are
	 * identified by '$'+ a number (e.g., $1, $2, etc.). Variables are replaced
	 * by the values stored in the binds array. E.g., The $1 variable is
	 * replaced by the value at binds[0], and so on.
	 * 
	 * @param path
	 *            The string to replace the variables in
	 * @param binds
	 *            The values of the variabels
	 * @return The string where all vars are substituted with their values.
	 */
	public static String substituteVars(String path, String[] binds) {
		StringBuffer b = new StringBuffer(path);
		for (int i = 0; i < binds.length; i++) {
			replaceVar(b, kVarPrefix + (i + 1), binds[i]);
		}
		return b.toString();
	}

	private static void replaceVar(StringBuffer b, String var, String value) {
		String s = b.toString();
		Stack idxStack = new Stack();
		int pos = 0;
		int varLength = var.length();
		while ((pos = s.indexOf(var, pos)) != -1) {
			idxStack.push(Integer.valueOf((pos)));
			pos += varLength;
		}
		while (!idxStack.isEmpty()) {
			pos = ((Integer) idxStack.pop()).intValue();
			b.replace(pos, pos + varLength, value);
		}
	}

	/**
	 * Returns direct children of n by the given name
	 * 
	 * @param n
	 *            the node
	 * @param tagName
	 *            the tagname
	 * @return List of Nodes
	 */
	public static List getStrictChildNodes(Node n, String tagName) {
		ArrayList result = new ArrayList();
		Node child = n.getFirstChild();
		while (child != null) {
			String name = child.getNodeName();
			if (name.compareTo(tagName) == 0) {
				result.add(child);
			}
			child = child.getNextSibling();
		}
		return result;
	}

	/**
	 * Returns the first direct childnode of n by the given name
	 * 
	 * @param n
	 *            the node
	 * @param tagName
	 *            the tagname
	 * @return the first direct childnode of n by the given name
	 */
	public static Node getStrictChildNode(Node n, String tagName) {
		Node child = n.getFirstChild();
		while (child != null) {
			String name = child.getNodeName();
			if (name.compareTo(tagName) == 0)
				return (Element) child;
			child = child.getNextSibling();
		}
		return null;
	}

	/**
	 * Returns the descendant of node
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the xpath to evaluate
	 * @return the node matching the xpath expression
	 * @deprecated use getChildNode(Node,String,XPathCache) instead
	 */
	public static Node getChildNode(Node node, String path) {
		Node n = null;
		try {
			n = getNode(node, path);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return n;
	}

	/**
	 * Returns the descendant of node
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the xpath to evaluate
	 * @param cache
	 *            the XPathCache
	 * @return the node matching the xpath expression
	 */
	public static Node getChildNode(Node node, String path, XPathCache cache) {
		Node n = null;
		try {
			n = getNode(node, path, cache);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return n;
	}

	/**
	 * Returns node value as bool
	 * 
	 * @param node
	 *            the Node
	 * @param path
	 *            the Xpath
	 * @param defaultValue
	 *            the default value
	 * @return true if the node located from the parent node has a "true" value
	 * @throws SAXException
	 */
	public static boolean getBooleanValue(Node node, String path, boolean defaultValue) throws SAXException {
		if (node == null)
			return defaultValue;
		String text = getTextValue(node, path);
		if (text == null)
			return defaultValue;
		text = text.trim();
		if (text.equals("true"))
			return true;
		if (text.equals("false"))
			return false;
		return defaultValue;
	}

	/**
	 * Returns node value as int
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the xpath
	 * @param defaultValue
	 *            the default value
	 * @return the found node's text content as int
	 * @throws SAXException
	 */
	public static int getIntValue(Node node, String path, int defaultValue) throws SAXException {
		if (node == null)
			return defaultValue;
		String text = getTextValue(node, path);
		if (text == null)
			return defaultValue;
		text = text.trim();
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Returns node value as float
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the xpath
	 * @param defaultValue
	 *            the default value
	 * @return the found node's text content as float
	 * @throws SAXException
	 */
	public static float getFloatValue(Node node, String path, float defaultValue) throws SAXException {
		if (node == null)
			return defaultValue;
		String text = getTextValue(node, path);
		if (text == null)
			return defaultValue;
		text = text.trim();
		try {
			return Float.parseFloat(text);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * List Of Nodes matching path from the parent node
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the path
	 * @return List of Node
	 */
	public static List getListOfNodes(Node node, String path) {
		List nl = null;
		if (node == null)
			return new ArrayList();
		if (isSimpleXPath(path)) {
			nl = new ArrayList();
			if (".".equals(path)) {
				nl.add(node);
				return nl;
			}
			Node child = (Node) node.getFirstChild();
			while (child != null) {
				if (!(child instanceof Element)) {
					child = child.getNextSibling();
					continue;
				}
				String txt = ((Element) child).getTagName();
				if (path.equals(txt)) {
					nl.add(child);
				}
				child = child.getNextSibling();
			}
			return nl;
		}
		try {
			nl = XPathCache.createJaxenXPath(path).selectNodes(node);
		} catch (JaxenException ex) {
			ex.printStackTrace();
		}
		return nl;
	}

	/**
	 * List Of Nodes matching path from the parent node
	 * 
	 * @param node
	 *            the parent node
	 * @param path
	 *            the path
	 * @param cache
	 *            the XPathCache to use
	 * @return List of Node
	 */
	public static List getListOfNodes(Node node, String path, XPathCache cache) {
		List nl = null;
		if (node == null)
			return new ArrayList();
		try {
			org.jaxen.XPath xpath = cache.getJaxenXPath(path);
			nl = xpath.selectNodes(node);
		} catch (JaxenException ex) {
			ex.printStackTrace();
		}
		return nl;
	}

	private static boolean isSimpleXPath(String path) {
		char[] specials = new char[] { '@', '/', '[', ':' };
		for (int i = 0; i < specials.length; i++) {
			if (path.indexOf(specials[i]) != -1)
				return false;
		}
		return true;
	}

	/**
	 * Gets list of nodes from parent; xpath is composed of root and path
	 * combination
	 * 
	 * @param node
	 *            parent node
	 * @param path
	 *            xpath component
	 * @param root
	 *            root xpath component
	 * @return list of nodes from parent
	 */
	public static List getListOfNodes(Node node, String path, String root) {
		String completePath = computePath(path, root);
		List nl = null;
		nl = getListOfNodes(node, completePath);
		return nl;
	}

	/**
	 * Evaluates a boolean XPath expression
	 * 
	 * @param node
	 *            the node to evalute
	 * @param path
	 *            the XPath expression
	 * @return the result of the xpath expr
	 */
	public static boolean evaluateXPath(Node node, String path) {
		try {
			XPath xpath = XPathCache.createJaxenXPath(path);
			Object result = xpath.evaluate(node);
			if (result == null)
				return false;
			if (result instanceof List) {
				List list = (List) result;
				if (list.size() == 0)
					return false;
			} else if (result instanceof Boolean) {
				Boolean bool = (Boolean) result;
				return bool.booleanValue();
			}
			return true;
		} catch (JaxenException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Serializes a node to a writer
	 * 
	 * @param node
	 *            the node
	 * @param writer
	 *            the writer
	 */
	public static void serialize(Node node, Writer writer) {
		try {
			javax.xml.transform.TransformerFactory factory = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = factory.newTransformer();
			javax.xml.transform.Source source = new javax.xml.transform.dom.DOMSource(node);
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(writer);
			transformer.transform(source, result);
		} catch (javax.xml.transform.TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (javax.xml.transform.TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (javax.xml.transform.TransformerException e) {
			e.printStackTrace();
		}
	}

	private static String computePath(String path, String rootPath) {
		String completePath = "";
		String separator = "";
		// the root path is the first part, and the path is the second part.
		// if neither root or path ends or starts with / then we need to insert
		// a separator
		if (!rootPath.endsWith("/") && !path.startsWith("/")) {
			separator = "/";
		}
		// if root ends and path starts with / then we need to get rid of one of
		// the / chars
		if (rootPath.endsWith("/") && path.startsWith("/")) {
			path = path.substring(1);
		}
		completePath = rootPath + separator + path;
		if (completePath.startsWith("/"))
			completePath = "." + completePath;
		else
			completePath = "./" + completePath;
		// if both the path and rootpath were empty, then just use the current
		// node selector
		if (completePath.equals("./"))
			completePath = ".";
		return completePath;
	}

	private static CachedXPathAPI getXPathAPI(Node n) {
		Document d = n.getOwnerDocument();
		CachedXPathAPI api = null;
		if (isDocInCache(d)) {
			api = (CachedXPathAPI) mXPathContexts.get(d);
		}
		if (api == null) {
			api = new CachedXPathAPI();
			if (isDocInCache(d)) {
				mXPathContexts.put(d, api);
			}
		}
		return api;
	}

	/**
	 * Marks a document as "cachable". Any XPath related calls will check if the
	 * document is cachable. If it is, then the XPathAPI will be cached for the
	 * document, so subsequent calls will be able to reuse the same
	 * CachedXPathAPI. Important: You must call removeDocFromCache once you no
	 * longer need the XPathAPI. Typically you would write: <code>
	 * Document doc;
	 * try {
	 *    addDocToCache(doc);
	 *    ... call XMLUtil methods
	 * }
	 * finally {
	 * 	  removeDocFromCache(doc);
	 * }
	 * </code>
	 * 
	 * @param doc
	 *            the document to cache
	 * @deprecated shouldn't need to use this, we have a fast enough XPath
	 *             processor
	 */
	public static void addDocToCache(Document doc) {
		// removing: we should no longer use getNodeList functions. They are the
		// only ones requiring caching a document.
	}

	/**
	 * Removes a previously cachable document and its XPathAPI object from the
	 * cache.
	 * 
	 * @param doc
	 *            the document to remove from cache
	 * @see addDocToCache
	 * @deprecated shouldn't need to use this, we have a fast enough XPath
	 *             processor
	 */
	public static void removeDocFromCache(Document doc) {

	}

	/**
	 * @param doc
	 * @return
	 * @deprecated shouldn't need to use this, we have a fast enough XPath
	 *             processor
	 */
	private static boolean isDocInCache(Document doc) {
		boolean inCache = (doc != null);
		if (!inCache)
			return false;
		return mDocCache.containsKey(doc);
	}

	/**
	 * Convenience method for creating an element with optional text child.
	 * Optionally, the created element is added as a child to a parent element
	 * 
	 * @param document
	 *            the document to use as the factory
	 * @param parent
	 *            the parent. If not null, the created element is added as a
	 *            child of this parent
	 * @param tagName
	 *            the name of the tag
	 * @param value
	 *            optional text content of the tag. If null, no text is added.
	 * @return the created element
	 */
	public static Element createElement(Document document, Element parent, String tagName, String value) {
		Element e = document.createElement(tagName);
		if (parent != null) {
			parent.appendChild(e);
		}
		if (value != null) {
			Text txt = document.createTextNode(value);
			e.appendChild(txt);
		}
		return e;
	}

	/**
	 * Convenience method for creating an element with optional text child.
	 * Optionally, the created element is added as a child to a parent element
	 * 
	 * @param document
	 *            the document to use as the factory
	 * @param parent
	 *            the parent. If not null, the created element is added as a
	 *            child of this parent
	 * @param ns
	 *            the namespace url
	 * @param tagName
	 *            the name of the tag
	 * @param value
	 *            optional text content of the tag. If null, no text is added.
	 * @return the created element
	 */
	public static Element createElementNS(Document document, Element parent, String ns, String tagName, String value) {
		Element e = document.createElementNS(ns, tagName);
		if (parent != null) {
			parent.appendChild(e);
		}
		if (value != null) {
			Text txt = document.createTextNode(value);
			e.appendChild(txt);
		}
		return e;
	}

	/**
	 * This is an utility method which replaces special chars with XML entity so
	 * that the XML document can be parsed
	 * 
	 * @param theValue
	 *            the string value which needs to be replaced with XML entity
	 * @return the value with XML competible string
	 */
	public static String translateSpecialCharsForXML(String theValue) {

		theValue = theValue.replaceAll("&", "&amp;");
		theValue = theValue.replaceAll(">", "&gt;");
		theValue = theValue.replaceAll("<", "&lt;");
		theValue = theValue.replaceAll("\"", "&quot;");

		return theValue;
	}
}
