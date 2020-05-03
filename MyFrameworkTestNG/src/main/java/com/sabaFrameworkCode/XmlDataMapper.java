package com.sabaFrameworkCode;

//~--- non-JDK imports --------------------------------------------------------

import java.io.File;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.testng.ITestContext;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * <b>XmlDataMapper</b> - Utility class that imports datasets from testng xml
 * files.<br>
 * Variables needed between tests is also stored here.
 *
 */
public class XmlDataMapper {
    static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static final String CURRENT_DATE = "CURRENT_DATE";
	static final String MINUS        = "-";
	static final String PLUS         = "+";
	static final int logLvl=1;
	private static Random generator = new Random(new Date().getTime()); 
	/**
	 * Stores Variables declared in config tag.
	 */
	public static Map configMap = null;


	private static String currentSuite = "";

	/**
	 * Stores all datatests in DefaultData file.
	 */
	private static Map    defaultDataMap       = null;
	private static String defaultGuiWaitPeriod = "60";
	private static String isSkipOnFail         = "true";

	/**
	 * Stores all datasets in a list for a particular method
	 */
	public static Map methodData              = null;
	public static Map includeDataMapFileNames = new HashMap();
	public static Map includeDataMapAll       = new HashMap();

	/**
	 * Stores Variables needed between tests.
	 */
	public static Map globalMap = new HashMap();

	/**
	 * Stores the path for defaultSuiteFile
	 */
	private static String defaultDataEntityFile;
	private static String defaultDateComponentFile;
	public static String  testConfigurationName;
    private static final String RANDOM = "RANDOM";

	static {
		try {
			defaultDateComponentFile = System.getProperty("defaultComponentPath");
			if(defaultDateComponentFile==null || defaultDateComponentFile.length()==0) {
				defaultDateComponentFile=System.getenv("defaultComponentPath");
			}
			defaultDataEntityFile = System.getProperty("defaultEntityPath");
			if(defaultDataEntityFile==null || defaultDataEntityFile.length()==0) {
				defaultDataEntityFile=System.getenv("defaultEntityPath");
			}
			defaultGuiWaitPeriod = System.getProperty("defaultGuiWaitPeriod");
			if(defaultGuiWaitPeriod==null || defaultGuiWaitPeriod.length()==0) {
				defaultGuiWaitPeriod=System.getenv("defaultGuiWaitPeriod");
			}
			isSkipOnFail = System.getProperty("isSkipOnFail");
			if(isSkipOnFail==null || isSkipOnFail.length()==0) {
				isSkipOnFail=System.getenv("isSkipOnFail");
			}
		} catch (Exception e) {
			System.out.println("Ex: " + e);
			Reporter.log("Ex: " + e);
			Runtime.getRuntime().exit(1);
		}
	}

	/**
	 * Method to get data for a particular test, it deduces the method name and the xml file on its own
	 * @param context
	 *            ItestContext for the particular test.
	 * @return List of dataset maps for a particular test element.
	 */
	public static List getData(ITestContext context) {
		String key      = context.getName();
		String fileName = context.getSuite().getXmlSuite().getFileName();

		if (!context.getSuite().getName().equals(currentSuite)) {
			currentSuite = context.getSuite().getName();

			// System.out.println("testConfigurationName:::::"+testConfigurationName+" "+currentSuite);
			methodData = null;
			configMap  = null;

			// globalMap = null;
		}
		return getData(key, fileName);
	}

	/**
	 * Method to get data for a particular test given the method name, xml file is deduced from the context
	 * @param methodname
	 *            whose datasets are required & context ItestContext for the
	 *            particular test.
	 * @return List of dataset maps for a particular test element.
	 */
	public static List getData(String methodname, ITestContext context) {
		String key      = methodname;
		String fileName = context.getSuite().getXmlSuite().getFileName();

		if (!context.getSuite().getName().equals(currentSuite)) {
			currentSuite = context.getSuite().getName();

			if (testConfigurationName == null) {
				methodData = null;
				configMap  = null;
			}
		}

		return getData(key, fileName);
	}

	/**
	 * Method to get data for a particular test, given the method and filename
	 * @param testname
	 *            whose datasets are required & xml filename where the test is
	 *            written.
	 * @return List of dataset maps for a particular test element.
	 */
	private static List getData(String methodname, String fileName) {
		String key = methodname;

		if ((methodData == null) || (methodData.get(methodname) == null)) {
			readDataForSuite(fileName);
		}

		// includeMapFill(key,testConfigurationName);
		fillGlobalValues(key);

		return (List) methodData.get(key);
	}


	/**
	 * This method fills the return List of maps with the externalised variable
	 * values.
	 *
	 * @param key
	 *            Test Name for which the datasets are to be returned
	 */
	private static void fillGlobalValues(String key) {
		List data = new ArrayList();

		if (methodData.get(key) != null) {
			data = (List) methodData.get(key);
		}

		for (int i = 0; i < data.size(); i++) {
			Map mp = (Map) data.get(i);

			mp = replaceGlobalRef(mp);
		}
	}

	/**
	 * This method reads the global map and replaces all the references in the map that has been provided.
	 * @param dataMap Map in which all the global references needs to be replaced
	 * @return
	 */
	private static Map replaceGlobalRef(Map dataMap) {
		if (dataMap == null) {
			return dataMap;
		}

		Iterator it = dataMap.keySet().iterator();

		while (it.hasNext()) {
			String mapkey = (String) it.next();

			// System.out.println(dataMap.get(mapkey)+""dataMap.get(mapkey).getClass());
			if ((dataMap.get(mapkey) != null) && (dataMap.get(mapkey) instanceof java.lang.String)) {
				String stringval = (String) dataMap.get(mapkey);

				if ((stringval.length() > 0) && (stringval.charAt(0) == '$')
						&& (stringval.charAt(stringval.length() - 1) == '$')) {
					String key = stringval.substring(1, stringval.length() - 1);
					Object o   = null;

					if (key.indexOf("DefaultData") == 0) {
						o = getDefaultReference(key);
					} else if ((globalMap != null) && (globalMap.get(key) != null)) {
						o = globalMap.get(key);
					} else {

						// error
					}

					dataMap.put(mapkey, o);
				}
			} else if ((dataMap.get(mapkey) != null) && (dataMap.get(mapkey) instanceof Map)) {
				Map innerDataMap = replaceGlobalRef((Map) dataMap.get(mapkey));

				dataMap.put(mapkey, innerDataMap);
			} else{
			    dataMap.put(mapkey, dataMap.get(mapkey));
			}
		}

		return dataMap;
	}

	/**
	 * Reads the reference to a default data entity or component file deduces upto which level the inheritance <br>
	 * goes and replaces the reference with the corresponding value.
	 * @param key The key of the referenced value
	 * @return value The value corresponding to the referencing to default entity or component.
	 */
	private static Object getDefaultReference(String key) {
		String[] searchKeys = key.split("\\.");
		Object   o          = defaultDataMap.get(searchKeys[0]);

		for (int k = 1; k < searchKeys.length; k++) {

			// System.out.println(searchKeys[k]+"====="+((Map)o).get(searchKeys[k]));
			if (o instanceof Map) {
				o = ((Map) o).get(searchKeys[k]);
			} else if (o instanceof String) {
				String val = (String) o;

				if ((val.charAt(0) == '$') && (val.charAt(val.length() - 1) == '$')) {
					String key1 = val.substring(1, val.length() - 1);

					o = getDefaultReference(key1);

					if (o instanceof Map) {
						o = ((Map) o).get(searchKeys[k]);
					} else {

						// error
					}
				} else {

					// error
				}
			} else {

				// error
			}
		}

		if (o instanceof Map) {
			o = replaceGlobalRef((Map) o);
		}

		return o;

	}

	/**
	 * This method is used to externalize variables so that the scope of the
	 * variable is the entire test.
	 *
	 * @param key Key with which the data is to be shared
	 * @param value the value to be shared
	 */
	public static void shareData(String key, Object value) {
		if (globalMap == null) {
			globalMap = new HashMap();
		}

		globalMap.put(key, value);
	}

	/**
	 * Reads the Testng xml file and fills the data set into a map.
	 *
	 * @param Filename Canonical filename with path of the testng xml file.
	 * @return Map Containing all the datasets in a xml file.
	 */
	public static void readDataForSuite(String Filename) {
		Map     tests           = new HashMap();
		HashMap includedTestMap = null;

		if (testConfigurationName != null) {
			includedTestMap = (HashMap) includeDataMapAll.get(testConfigurationName);
		}

		try {
			Node suiteRoot = getRootNode(new File(Filename));

			if (suiteRoot.getNodeName().equals("testFile")) {
				suiteRoot = (Node) XMLUtil.getStrictChildNodes(suiteRoot, "suite").get(0);
			}

			if (configMap == null) {
				readConfig(suiteRoot);
			}

			List     testNodes = XMLUtil.getListOfNodes(suiteRoot, "//suite/test");
			Iterator testIter  = testNodes.iterator();

			while (testIter.hasNext()) {
				Node   testNode = (Node) testIter.next();
				String testName = getAttributeValue(testNode.getAttributes(), "name");
				String testRef  = getAttributeValue(testNode.getAttributes(), "ref");

				if (testRef != null) {
					includeDataMapFileNames.put(testName, testRef);
					includeTest(testNode, testName, testRef);

					continue;
				}

				HashMap includedTest = null;
				boolean overwrite    = false;

				if ((includedTestMap != null) && (includedTestMap.get(testName) != null)) {
					includedTest = (HashMap) includedTestMap.get(testName);

					if (includedTestMap.containsKey(testName + "===inheritdatafromParent===")) {
						overwrite = true;
					}
				}

				List    datasetNodes   = XMLUtil.getStrictChildNodes(testNode, "dataset");
				boolean hasDatasetNode = !datasetNodes.isEmpty()
				? true
						: false;
				HashMap dataSets       = new HashMap();

				List     dataSetList = new ArrayList();

				if (hasDatasetNode &&!overwrite) {

					List    dataSetsOrderedList = new ArrayList();
					fillDataSet(datasetNodes, includedTest, testName, dataSets, dataSetsOrderedList);

					for(int k=0;k<dataSetsOrderedList.size();k++){
						dataSetList.add(dataSets.get(dataSetsOrderedList.get(k)));
					}

				} else if (overwrite) {
					dataSets = includedTest;

					Iterator itDataset   = dataSets.values().iterator();
					while (itDataset.hasNext()) {
						dataSetList.add(itDataset.next());
					}

				}



				tests.put(getAttributeValue(testNode.getAttributes(), "name"), dataSetList);

				List configNodes = XMLUtil.getListOfNodes(suiteRoot, "//testFile/suite/test");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		methodData = tests;
	}

	private static void includeTest(Node testNode, String testName, String testRef) {

		// List configNodes = XMLUtil.getListOfNodes(testNode, "//incltest/");
		List<Node> inclTestNodes = XMLUtil.getStrictChildNodes(testNode, "incltest");
		HashMap    includeMap    = new HashMap();

		for (int i = 0; i < inclTestNodes.size(); i++) {
			String  inclTestName   = getAttributeValue(inclTestNodes.get(i).getAttributes(), "name");
			String  inclInherit    = getAttributeValue(inclTestNodes.get(i).getAttributes(), "inheritdata");
			List    datasetNodes   = XMLUtil.getStrictChildNodes(inclTestNodes.get(i), "dataset");
			boolean hasDatasetNode = !datasetNodes.isEmpty()
			? true
					: false;
			HashMap dataSets       = new HashMap();

			if (hasDatasetNode) {
				List dataSetOrderedList = new ArrayList();
				fillDataSet(datasetNodes, null, testName, dataSets, dataSetOrderedList);
			}

			includeMap.put(inclTestName, dataSets);

			if ((inclInherit != null) && inclInherit.equalsIgnoreCase("false")) {
				includeMap.put(inclTestName + "===inheritdatafromParent===", true);
			}
		}

		includeDataMapAll.put(testName, includeMap);
	}

	private static void fillDataSet(List datasetNodes, HashMap includedTest, String testName, HashMap dataSets, List dataSetsOrderedList) {
		Iterator datasetIter = datasetNodes.iterator();

		while (datasetIter.hasNext()) {
			Map     dataMap        = new HashMap();
			Node    dataSetNode    = (Node) datasetIter.next();
			String  dataSetName    = getAttributeValue(dataSetNode.getAttributes(), "name");
			HashMap includedKeyVal = new HashMap();

			if ((includedTest != null) && includedTest.containsKey(dataSetName)) {
				includedKeyVal = (HashMap) includedTest.get(dataSetName);
				includedTest.put(dataSetName,null);
			}

			String dataSetRef = getAttributeValue(dataSetNode.getAttributes(), "datasetRef");
			//if (dataSetRef != null) {

				if (defaultDataMap == null) {
					defaultDataMap = new DefaultDataMapper().fillDefaultDataEntityMap(defaultDataEntityFile,
							defaultDateComponentFile);
				}
                if (dataSetRef != null) {
				Object o = getDefaultReference(dataSetRef);

				if (o == null) {

					// Error
				} else if (o instanceof String) {

					// error
				} else if (o instanceof Map) {
					Map      tempMap = (Map) o;
					Iterator it      = tempMap.keySet().iterator();

					while (it.hasNext()) {
						String keyTempMap = (String) it.next();

						dataMap.put(keyTempMap, tempMap.get(keyTempMap));
						System.out.println(keyTempMap + " == " + tempMap.get(keyTempMap));
					}
				}
			}

			List    dataNodes   = XMLUtil.getStrictChildNodes(dataSetNode, "data");
			boolean hasDataNode = !dataNodes.isEmpty()
			? true
					: false;

			if (hasDataNode) {
				Iterator dataIter = dataNodes.iterator();

				while (dataIter.hasNext()) {
					Node dataNode = (Node) dataIter.next();

					// fill value of dataNode into the datamap for a test
					dataMap = fillValueOfDataNode(dataMap, dataNode, testName);
				}

				Iterator keysIt = includedKeyVal.keySet().iterator();

				while (keysIt.hasNext()) {
					String key   = (String) keysIt.next();

					dataMap.put(key, includedKeyVal.get(key));
				}
			}

			if (dataSetName == null) {    // this has been done so that we can still support the prev version where datasetname is not mandatory
				int randomNo = (int) (Math.random() * 10);

				dataSetName = "datasetNm" + randomNo;
			}


			dataSetsOrderedList.add(dataSetName);
			dataSets.put(dataSetName, dataMap);
		}

		if ((includedTest != null) ) {

			Iterator keysIt = includedTest.keySet().iterator();

			while (keysIt.hasNext()) {
				String key   = (String) keysIt.next();
				if(includedTest.get(key)!=null){

					dataSetsOrderedList.add(key);
					dataSets.put(key, includedTest.get(key));
				}
			}

		}
	}

	/**
	 * Get the root node of an xml file
	 *
	 * @param xmlFile
	 *            the File
	 * @return Node the root node of xmlFile
	 * @throws Exception
	 */
	public static Node getRootNode(File xmlFile) throws Exception {
		Node                   node    = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder        builder;
		Document               doc;

		try {
			builder = factory.newDocumentBuilder();
			doc     = builder.parse(xmlFile);

			XMLProperties props = new XMLProperties(doc.getDocumentElement());

			node = props.getRootPropertyNode();
		} catch (Exception e) {
			Reporter.log("Failed to get root node for " + xmlFile + " due to " + e);
		}

		return node;
	}

	/**
	 * Get the value of an attribute element
	 *
	 * @param attrs
	 *            NamedNodeMap of attributes for an element
	 * @param attrName
	 *            The name of the attribute
	 * @return String The value of the attribute or null if the attribute was
	 *         not present
	 */
	public static String getAttributeValue(NamedNodeMap attrs, String attrName) {
		String attrValue = null;
		Node   nodeAttr  = attrs.getNamedItem(attrName);

		if (nodeAttr != null) {
			attrValue = nodeAttr.getNodeValue();
		}

		return attrValue;
	}


	/**
	 * Sets the config variables of the suite to be executed Config variables of
	 * included suites are overriden by the parent suite.
	 *
	 * @param suiteRoot
	 *            the root note of the suite
	 * @throws Exception
	 */
	private static void readConfig(Node suiteRoot) {
		try {
			if(configMap==null){
				configMap = new HashMap();
			}
			Node    configNode  = XMLUtil.getNode(suiteRoot, "//config");
			List    dataNodes   = XMLUtil.getStrictChildNodes(configNode, "data");

			boolean hasDataNode = !dataNodes.isEmpty()
			? true
					: false;

			if (hasDataNode) {
				Iterator dataIter = dataNodes.iterator();

				while (dataIter.hasNext()) {
					Node         dataNode  = (Node) dataIter.next();
					NamedNodeMap dataAttrs = dataNode.getAttributes();
					String       value     = getAttributeValue(dataAttrs, "value");

					configMap.put(getAttributeValue(dataAttrs, "key"), handleSpecialValues(value));

					// System.out.println(getAttributeValue(dataAttrs,
					// "key")+"==="+ handleSpecialValues(value));
				}
			}

			NodeList    configNodes   = configNode.getChildNodes();
			for(int i=0;i<configNodes.getLength();i++){

				Node currnode = configNodes.item(i);
				if(currnode.getNodeType()==1 && !currnode.getNodeName().equalsIgnoreCase("data")){
					System.out.println(currnode.getNodeType()+" "+currnode.getNodeName());
					String key = currnode.getNodeName();
					String value = currnode.getTextContent();

					configMap.put(key, value);
				}
			}


		} catch (Exception se) {
			Reporter.log("Problem while setting suite config" + se);
		}
	}

	public static Map getConfigMap(ITestContext context) {
		if (configMap == null) {
			configMap = new HashMap();

			try {
				Node suiteRoot = getRootNode(new File(context.getSuite().getXmlSuite().getFileName()));

				if (suiteRoot.getNodeName().equals("testFile")) {
					suiteRoot = (Node) XMLUtil.getStrictChildNodes(suiteRoot, "suite").get(0);
				}

				readConfig(suiteRoot);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return configMap;
	}

	private static Map fillValueOfDataNode(Map dataMap, Node dataNode, String testConfigName) {
		NamedNodeMap dataAttrs = dataNode.getAttributes();

		// code for includemap filling starts here
		String testName = getAttributeValue(dataAttrs, "test");

		if (testName != null) {
			String dataSetName = getAttributeValue(dataAttrs, "dataset");
			String key         = getAttributeValue(dataAttrs, "key");
			String values      = getAttributeValue(dataAttrs, "value");
			String reference   = getAttributeValue(dataAttrs, "ref");

			if (includeDataMapAll == null) {
				includeDataMapAll = new HashMap();
			}

			HashMap dataSet   = new HashMap();
			HashMap keyValMap = new HashMap();

			if (includeDataMapAll.containsKey(testConfigName + "." + testName)) {
				dataSet = (HashMap) includeDataMapAll.get(testConfigName + "." + testName);

				if (dataSet.containsKey(dataSetName)) {
					keyValMap = (HashMap) dataSet.get(dataSetName);
				}
			}

			if (values != null) {
				keyValMap.put(key, values);
			} else {
				keyValMap.put(key, "$" + reference + "$");
			}

			dataSet.put(dataSetName, keyValMap);
			includeDataMapAll.put(testConfigName + "." + testName, dataSet);
		}

		// code for includemap ends here
		if (dataAttrs.getNamedItem("value") != null) {
			String value = getAttributeValue(dataAttrs, "value");

			dataMap.put(getAttributeValue(dataAttrs, "key"), handleSpecialValues(value, getAttributeValue(dataAttrs, "type")));
		} else if (dataAttrs.getNamedItem("ref") != null) {
			String ref = getAttributeValue(dataAttrs, "ref");

			dataMap.put(getAttributeValue(dataAttrs, "key"), "$" + ref + "$");

			if ((ref.indexOf("DefaultDataEntity.") == 0) || (ref.indexOf("DefaultDataFunctionality.") == 0)) {
				if (defaultDataMap == null) {
					defaultDataMap = new DefaultDataMapper().fillDefaultDataEntityMap(defaultDataEntityFile,
							defaultDateComponentFile);
				}
			}
		} else {
			Map  innerDataMap = new HashMap();
			List values       = XMLUtil.getStrictChildNodes(dataNode, "value");

			if (values.size() > 0) {
				List innerDataNodes = XMLUtil.getStrictChildNodes((Node) values.get(0), "data");

				for (int i = 0; i < innerDataNodes.size(); i++) {
					Node         curr      = (Node) innerDataNodes.get(i);
					NamedNodeMap currattrs = curr.getAttributes();

					fillValueOfDataNode(innerDataMap, curr, testConfigName);
				}
			}

			dataMap.put(getAttributeValue(dataAttrs, "key"), innerDataMap);
		}

		return dataMap;
	}


        protected static String handleSpecialValues(String value) {
            return handleSpecialValues(value, null).toString();
        }

	
	protected static Object handleSpecialValues(String value, String type) {
	    Object result = null;

		// handling for dates
		if (value.trim().startsWith(CURRENT_DATE)) {
			value = value.trim();

			int      days = 0;
			Calendar cal  = new GregorianCalendar();

			try {

				// Check if it's CURRENT_DATE+x
				if (value.indexOf(PLUS) > -1) {
					days = Integer.parseInt(value.substring(value.indexOf(PLUS) + 1));
					cal.add(Calendar.DATE, days);
				}

				// Check if it's CURRENT_DATE-y
				if (value.indexOf(MINUS) > -1) {
					days = Integer.parseInt(value.substring(value.indexOf(MINUS) + 1));
					cal.add(Calendar.DATE, -days);
				}
			} catch (NumberFormatException mfe) {
				Reporter.log("Invalid format for number: " + days + ". Defaulting to system date.");
			}

			// Otherwise default to the current date
			// Make a string of format mm/dd/yyyy and replace value with it
			value = (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.DATE)) + "/" + (cal.get(Calendar.YEAR));
		}
		else if(value.trim().startsWith(RANDOM)) {
			/*
            If it is just RANDOM, return a 5 digit random value
            RANDOM^x(4) will create a string starting with characters x, followed by a random number of length<=4
            RANDOM$x(4) will create a string starting with a random number of length<=4, followed by characters x
            RANDOM$x will create a string starting with a random number of length<=5, followed by characters x
            RANDOM^x$y(5) will create a string starting with characters x, followed by a <=5 character random number, followed by characters y
			 */
			final int maxFiveDigitNumber = 99999;
			int randomValue = generator.nextInt(maxFiveDigitNumber);
			if(value.indexOf("^")!=-1 && value.indexOf("(")==-1 && value.indexOf("$")==-1){
				String tokens[] = value.split("\\^");
				value = tokens[1];
				value += Integer.toString(randomValue);
			}
			else if(value.trim().equals(RANDOM) || value.indexOf("(")==-1) { //Default to 5 digit random number
				value=Integer.toString(randomValue);
			}
			//if(value.indexOf("^")>-1)

		}
		
		if (type != null) {
		    if ("boolean".equalsIgnoreCase(type)) {
		        result = new Boolean(value);
		    } else if ("date".equalsIgnoreCase(type)) {
		        try {
                    result = new Timestamp(dateFormat.parse(value).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Incorrect data for type 'date'. Recived value =" + value);
                }
            } else if ("time".equalsIgnoreCase(type)) {
                try {
                    result = new Time(timeFormat.parse(value).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Incorrect data for type 'time'. Recived value =" + value);
                }
		    } else if ("integer".equalsIgnoreCase(type)) {
		        result = new Integer(value);
		    } else if ("float".equalsIgnoreCase(type)) {
		        result = new BigDecimal(value);
		    } else if ("string".equalsIgnoreCase(type)) {
		        result = value;
		    }
		}
		return result == null ? value : result;
	}
	/**
	 * Default time for which wait for element has to be done.
	 * @return
	 */
	public static String getDefaultGuiWaitPeriod() {
		return defaultGuiWaitPeriod;
	}
	/**
	 * Check if all tests to be skipped on failure.
	 * @return
	 */
	public static String getSkipOnFail() {
		return isSkipOnFail;
	}
	/**
	 * set the test configuration name
	 * @param name The name to be set for the configuration
	 */
	public static void setTestConfigurationName(String name) {
		testConfigurationName = name;
	}
	/**
	 * called after the test suite is run to remove all the included data
	 * @param testName Test Name for which the data is to be removed
	 */
	public static void deleteIncludedData(String testName) {
		includeDataMapAll.remove(testName);
	}
	/**
	 * Get the name of the included file.
	 * @param context test context
	 * @return Name of the include file
	 */
	public static String getIncludedFileName(ITestContext context) {
		return (String) includeDataMapFileNames.get(context.getName());
	}

}
