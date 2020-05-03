package com.sabaFrameworkCode;

import static org.testng.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.sabaFrameworkCode.XmlDataMapper;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;;


public class BaseSeleniumTest  
{
  public static DefaultSabaSelenium selenium=null;
  

  static Boolean reusedbrowser=false;
  protected static Map configMap=null;
  
  private static Map guitestMap=null;
  protected static int defaultGuiWaitPeriod = 60;
  protected static Boolean skipOnFail = true;
  
  /**
   * This is invoked by TestNG before a test suite is executed and 
   * is responsible for 
   * # reading the config tags from test XML.
   * # creating the selenium instance as specified in config.
   * # doing other configurations : defaultGUiWaitPeriod,skipOnFail,timeout,change
   * default xpath library(to optimisze IE execution)

   * @param context  the testNG test context of the execution
   */
  @BeforeSuite(alwaysRun = true)
  public void setupBeforeSuite(ITestContext context)
  {
	  String serverHost;
	  int serverPort;
	  String browserStartCommand;
	  String browserURL;
	  WebDriver driver;
	  
	  configMap =  XmlDataMapper.getConfigMap(context);
	  //this reads config map from test xml
	  readConfiguartions(context);
	  //this read config from guitest.properties
	  
	  if(selenium!=null){
		  return;
	  }

	  serverPort = Integer.parseInt((String) configMap.get("port"));
	  browserStartCommand = (String) configMap.get("browser");
	  serverHost =(String) configMap.get("baseURL"); 
	  driver = (WebDriver) configMap.get("driver");
	  
	  
	  if(configMap.get("defaultBrowserURL")!=null){
		  String def = (String) configMap.get("defaultBrowserURL");
		  browserURL =(String) configMap.get(def); 
	  }else{
		  browserURL = (String) configMap.get("browserURL"); 
	  }
	  
	  
	  
	  System.out.println("Selenium Config : selenium - "+serverHost +":"+ serverPort +" ,browser - "+ browserStartCommand+ " ,webSite - "+browserURL);
	///  selenium =new DefaultSabaSelenium(serverHost, serverPort, browserStartCommand, browserURL);
	  
	  selenium =new DefaultSabaSelenium(driver,  browserURL);	
	  selenium.start();
	  selenium.setTimeout(""+defaultGuiWaitPeriod*1000);
	  selenium.useXpathLibrary("javascript-xpath");
	  selenium.windowMaximize();

  }
  private void readConfiguartions(ITestContext context) {
	
	  if(guitestMap==null){
		  readConfigFile();
	  }
	  Iterator it = guitestMap.entrySet().iterator();
	  while (it.hasNext()) {
		  Map.Entry entry = (Map.Entry) it.next();
	      if(entry.getValue()!=null){
	    	  configMap.put(entry.getKey(), entry.getValue());
	      }
	    }
	  
		try{           	
	       	if(configMap.get("isSkipOnFail")!=null)
	       		skipOnFail=Boolean.parseBoolean((String) configMap.get("isSkipOnFail"));
	       	if(configMap.get("defaultGuiWaitPeriod")!=null)
	       		defaultGuiWaitPeriod=Integer.parseInt((String) configMap.get("defaultGuiWaitPeriod"));
System.out.println("-------------->defaultGuiWaitPeriod"+defaultGuiWaitPeriod);
			}catch(Exception ex){
				System.out.println("Error reading isSkipOnFail ,defaultGuiWaitPeriod ");
			}
}
  
  
	private void readConfigFile() {
		guitestMap = new HashMap();
		Properties props=new Properties();
		 InputStream is=null;
	       try {
			String guiTestPropFile="guitest.properties";
			if(System.getProperty("guitestprops")!=null){
				guiTestPropFile=System.getProperty("guitestprops");
			}
	       	is = new FileInputStream(guiTestPropFile);
	          	props.load(is);
	          	Enumeration e = props.propertyNames();
	
			    while (e.hasMoreElements()) {
			      String key = (String) e.nextElement();
			      if(props.getProperty(key)!=null){
					  if(key.equals("browserURL") && props.getProperty(key).contains("${computerName}")){
						System.out.println(key + " browserURL" +  props.getProperty(key));
						String value = props.getProperty(key);
						value= value.replace("${computerName}",getComputerName());
						guitestMap.put(key, value);
					  }else{
				    	  guitestMap.put(key, props.getProperty(key));
					  }
			      }
			    }
				
				
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + e);
				Reporter.log("File not found: " + e);
				
			} catch (IOException e) {
				System.out.println("IO ex: " + e);
				Reporter.log("IO ex: " + e);
			}
			
			finally{
				try{
				is.close();
				}
				catch (Exception e) {
					e.getMessage();
				}			
			}	
	
	}
	
	  public static Map getConfigMap() {
			return configMap;
		}

	  
/**
   * Responsible for stopping the selenium instance and closing the browser
   * after the test suite has been executed.
   */
  @AfterSuite(alwaysRun = true)
  public void setupAfterSuite()
  {

	  if( XmlDataMapper.testConfigurationName == null ){
			// only if this is not an included test

			 selenium.stop();
			 selenium = null;
	  }
  }
  	/**
  	 * Default implementation of a dataprovidor, which provided the dataset
  	 * key value pairs from the test XML as a map 
  	 * @param context the testNG test context of the execution
  	 * @return
  	 */
	@DataProvider(name = "datatest")
	public Object[][] thisDataProvider(ITestContext context) {
		List datasets = XmlDataMapper.getData(context);
		
		if(datasets.size()==0){
			Object[][] o = new Object[1][1];
			o[0][0]=new HashMap();
			return  o;			
		}
		Object[][] o =  new Object[datasets.size()][1];
		for (int i = 0; i < datasets.size(); i++) {
			o[i][0]=(Map) datasets.get(i);
		}
		return o;
	}
	
	/**
	 * SpeTypes into text boxes
	 * @param locator locator for the element to be typed into
	 * @param text text to be typed
	 */
  public static void suggestType(String locator, String text) {
		
		if(selenium.getEval("navigator.userAgent").contains("Firefox")){
			text = text.replace("y", "");
			
			selenium.typeKeys(locator, text, "Couldn't type in the given field");
		}else{
			
			String key=text;
			if(text.length()>1){
				selenium.type(locator, text.substring(0, text.length()-1), "Couldn't type in the selected locator");
				key=text.substring(text.length()-1, text.length());
			}
			selenium.keyDown(locator, key);
			selenium.type(locator, text, "Couldn't type in the selected locator");
			selenium.keyUp(locator, key);
		}
	}
  
  /**
   * Used to wait until a particular element appears on the page.
   * @param locator locator of the element that we need to wait for
   * @param errorMsg error message to be displayed
   * @throws Exception
   */
  public static void waitForElementPresent(String locator, String errorMsg) throws Exception{

	  waitForElementPresent(locator,errorMsg, defaultGuiWaitPeriod);
  } 
  
  /**
   * Used to wait until a particular element appears on the page.
   * @param locator locator of the element that we need to wait for
   * @param errorMsg error message to be displayed
   * @param wait the time in ms to wait for
   * @throws Exception
   */
  public static void waitForElementPresent(String locator,String errorMsg, int wait) throws Exception{
	  selenium.waitForElementPresent(locator, errorMsg, wait);
  }
  
  /**
   * Used to wait until a particular text appears on the page.
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @throws Exception
   */
  public static void waitForTextPresent(String text ,String errorMsg) throws Exception{

	  waitForTextPresent(text,errorMsg, defaultGuiWaitPeriod);
  }
  
  /**
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @param wait the time in ms to wait for
   * @throws Exception
   */
  public static void waitForTextPresent(String text, String errorMsg, int wait) throws Exception{
		selenium.waitForTextPresent(text, errorMsg, wait);
  }
 
  /**
   * Used to wait until a particular text disappears from the page.
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @throws Exception
   */
  public static void waitForElementNotPresent(String locator, String errorMsg) throws Exception{

	  waitForElementNotPresent(locator,errorMsg, defaultGuiWaitPeriod);
  } 
  
  /**
   * Used to wait untill a particular text disappears from the page.
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @param wait the time in ms to wait for
   * @throws Exception
   */
  public static void waitForElementNotPresent(String locator,String errorMsg, int wait) throws Exception{
		selenium.waitForElementNotPresent(locator, errorMsg, wait);
  }
  
  /**
   * Used to wait untill a particular text disappears from the page.
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @param wait the time in ms to wait for
   * @throws Exception
   */
  public static void waitForTextNotPresent(String text ,String errorMsg) throws Exception{

	  waitForTextNotPresent(text,errorMsg, defaultGuiWaitPeriod);
  }
  
  /**
   * Used to wait untill a particular text disappears from the page.
   * @param text text that we need to wait for
   * @param errorMsg error message to be displayed
   * @param wait the time in ms to wait for
   * @throws Exception
   */
  public static void waitForTextNotPresent (String text, String errorMsg, int wait) throws Exception{
		selenium.waitForTextNotPresent(text, errorMsg, wait);
  }
/**
 * Used to delay the execution (sleep) for the default specified wait period.
 */
  public void genericSleep() {

			 try {Thread.sleep(defaultGuiWaitPeriod);} catch (Exception e) {}
  }
  
  @BeforeTest
  public void  runBeforeTest(ITestContext c){
	  if(skipOnFail){
		  System.out.println("====");
		  Map<String, ISuiteResult> results = c.getSuite().getResults();
		  
		  Iterator it = results.keySet().iterator();
		  while(it.hasNext()){
			  Object key = it.next();
			  ISuiteResult suiteRes = results.get(key);
					if(suiteRes.getTestContext().getFailedTests().size()>0){	
						throw new SkipException("skip");
					}
		  }
	  }
	 
  }
  
  /**
   * 
    * Verify expected exceptions appear as popups when the test case 
    * is executed. The expected exceptions must be passed as expectedException1,
    * expectedException2 ... . The test method must call this method at the 
    * point where the exceptions are expected. 
    * @param data Map containing the data passed to the test case from xml
   * @throws Exception
   */
	public void verifyExpectedExceptionsPopup(Map data) {
		
		String exception="";
		for(int i=1;i<10;i++){
			if(data.get("expectedException"+i)!=null && !data.get("expectedException"+i).equals("")){				
				
				exception = (String) data.get("expectedException"+i);
				if(selenium.getAlert().equals(exception))	
					System.out.println("exception verified : "+ exception);
				else
					fail("expected exception missing : "+ exception);
			}else{
				break;
			}
		}
		
	}
	
   /**
    * Verify expected exceptions appear on the page when the test case 
    * is executed. The expected exceptions must be passed as expectedException1,
    * expectedException2 ... . The test method must call this method at the 
    * point where the exceptions are expected. 
    * @param data Map containing the data passed to the test case from xml
    * @throws Exception
    */
	@Deprecated
   public void verifyExpectedExceptions(Map data) throws Exception
   {
		
		String exception="";
		for(int i=1;i<10;i++){
			if(data.get("expectedException"+i)!=null && !data.get("expectedException"+i).equals("")){				
				
				exception = (String) data.get("expectedException"+i);
				waitForTextPresent(exception,"exception not displayed");
				if(selenium.isTextPresent(exception))	
					System.out.println("exception verified : "+ exception);
				else
					fail("expected exception missing : "+ exception);
			}else{
				break;
			}
		}
		
	}

   /**
    * Verify expected exceptions appear on the page when the test case 
    * is executed. The expected exceptions must be passed as expectedException1,
    * expectedException2 ... . The test method must call this method at the 
    * point where the exceptions are expected. 
    * @param data Map containing the data passed to the test case from xml
    * @throws Exception
    */
   
    public void verifyExpectedExceptionsPage(Map data) throws Exception
    {
 		
 		String exception="";
 		for(int i=1;i<10;i++){
 			if(data.get("expectedException"+i)!=null && !data.get("expectedException"+i).equals("")){				
 				
 				exception = (String) data.get("expectedException"+i);
 				waitForTextPresent(exception,"exception not displayed");
 				if(selenium.isTextPresent(exception))	
 					System.out.println("exception verified : "+ exception);
 				else
 					fail("expected exception missing : "+ exception);
 			}else{
 				break;
 			}
 		}
 		
 	}
    
	
	/**
	 * Selects and transfers control to the topmost browser popup window open.
	 * To be used when a popup window is opened and commands are to be 
	 * executed in the new window.
	 */
	public static void selectPopUp() throws Exception                 
	  {
	      String str[]=selenium.getAllWindowNames();
	      for (int i=0;i<str.length;i++)
	    	  System.out.println(str[i]);
	      for (int i=str.length-1;i>=0;i--)
	      {
	    	  if(!str[i].equals("selenium_main_app_window")&& !str[i].equals("null"))//
              {
	    		  selenium.selectWindow(str[i]);
                  System.out.println("selecting window: "+str[i]);
                  return;           
              }
	      }
	  }

	
	/**
	 * Selects and transfers control to the main browser window.
	 * Used after a popup window is closed and commands are to be executed 
	 * in the main window.
	 */
	public static void selectMainWindow()  throws Exception              
	  {
	    	selenium.selectWindow(null); 
	  }
	
	/**
     * Wait for the page to load in browser.
     * The timeout period is the time specified in apitest.properties 
     */
	public void waitForPageToLoad()  throws Exception    
    {
        selenium.waitForPageToLoad(""+defaultGuiWaitPeriod*1000);          
    }

	/**
     * Wait for the page to load in browser.
     * The timeout period is the time specified in apitest.properties 
	 * @param errorMsg TODO
     */
	public void waitForPageToLoad(String errorMsg)  throws Exception    
    {
        selenium.waitForPageToLoad(""+defaultGuiWaitPeriod*1000, "Could Not load page");          
    }

	public String getComputerName() {
		String computerName="";
		try{
		  computerName=InetAddress.getLocalHost().getHostName();
		  System.out.println(computerName);
		  return computerName;
		}catch (Exception e){
		  System.out.println("Exception caught ="+e.getMessage());
		}
		return computerName;
	 }
	
	public static void log(Object log){
		if(log!=null){
			System.out.println(log.toString());
			Reporter.log(log.toString());
		}
	}
		
	/**
	 *@param locator Locator of Text Field
	 *@param value Value which is to be type
	 *@param randomNo Random Number(optional)
	 *
	 *@return Returns a String which is typed in text field
	 */
	public String type(String locator, String value, String randomNo){
		if(value!=null){
			if(randomNo!=null && !value.equals(""))
				value=value+randomNo;
			selenium.type(locator, value);
			selenium.fireEvent(locator, "blur");
		}
		return value;
	}
	
//#########################################################################################################################################
	/**
	 *@param locator Locator of Text Field
	 *@param value Value which is to be type
	 *
	 *@return Returns a String which is typed in text field
	 */
	public String type(String locator, String value){
		return type(locator, value, null);
	}
	
//#########################################################################################################################################
	/**
	 *@param actual Actual Value
	 *@param expected Expected Value
	 *
	 */
	public void checkEqual(Object actual, Object expected){
		if(expected!=null)
			Assert.assertEquals(actual, expected);
	}
//#########################################################################################################################################
	/**
	 *@param selectLocator Locator of Drop Down List
	 *@param option Label String from Drop Down List
	 *
	 */
	public void select(String selectLocator, String option){
		if(option!=null && !option.equals(""))
			selenium.select(selectLocator, "label="+option);
	}
//#########################################################################################################################################
	/**
	 *@param locator Locator of CheckBox
	 *@param check Boolean Value to check or uncheck CheckBox
	 *
	 */
	public void checkUncheck(String locator, Boolean check){
		if(check!=null && check.booleanValue() && !selenium.isChecked(locator))
			selenium.click(locator);
		else if(check!=null && !check.booleanValue() && selenium.isChecked(locator))
			selenium.click(locator);
	}

//#########################################################################################################################################

	/**
	 *@param locator Locator of CheckBox
	 *@param check Boolean Value to check or uncheck CheckBox
	 *
	 */
	public void waitForCondition(String script){
		selenium.waitForCondition(script, ""+defaultGuiWaitPeriod*1000);
	}

//#########################################################################################################################################
	
	/**
	 *@param locator Locator of CheckBox
	 *@param check Boolean Value to check or uncheck CheckBox
	 *
	 */
	public void clickOnWDKLink(String key, String value){
		//String vijay = selenium.getEval("window.document.getElementsByTagName(\"a\")[0].getAttribute(\"onclick\")");
		
		String script = "var linkObjs = window.document.getElementsByTagName(\"a\")" +
				"var success = false;" +
				"for(var i = 0; i < linkObjs.length; i++){" +
					"if(linkObjs[i].getAttribute(\""+key+"\") == \""+value+"\"){" +
						"linkObjs[i].onclick();" +
						"success = true;" +
						"break;" +
					"}" +
				"}" +
				"success;";
		
		Assert.assertTrue(selenium.getEval(script).equalsIgnoreCase("true"), "Element not found");
	}

//#########################################################################################################################################
	/**
	 * 
	 * @param arg Variable length argument contains total window count and total null window count
	 * @throws Exception
	 */
	public void waitForPopup(Integer... arg)throws Exception{
		int time=0;
		int totalWin=arg[0];
		int totalNullWinCount=arg.length==2?arg[1]:-1;
		int newTotalNullWinCount=0;
		while(selenium.getAllWindowNames().length!=(totalWin+1) && time++<=20){
			String[] allWinNames=selenium.getAllWindowNames();
			if(totalNullWinCount!=-1){
				newTotalNullWinCount=0;
				for(String str:allWinNames){
					if(str.equals("null"))
						newTotalNullWinCount++;
				}
				if(newTotalNullWinCount==totalNullWinCount-1)
					break;
			}
			Thread.sleep(500);
		}
		int newTotalWin=selenium.getAllWindowNames().length;
		Assert.assertTrue(newTotalWin==totalWin+1 || (totalNullWinCount!=-1 && newTotalNullWinCount==totalNullWinCount-1), "Popup is not opened");
	}
	
//#########################################################################################################################################
	public void clickAndWaitForPopup(String locator)throws Exception{
		clickAndWaitForPopup(locator, true);
	}
//#########################################################################################################################################
	/**
	 * 
	 * @param locator Locator of element where user want to click
	 * @param selectPopup true/false whether to call selectPopUp()
	 * @description Click on the element and waits for the popup to open and also select popup(Optional) 
	 * @throws Exception
	 */
	public void clickAndWaitForPopup(String locator, boolean selectPopup)throws Exception{
		int totalNullWinCount=0;
		String[] allWinNames=selenium.getAllWindowNames();
		int totalWin = allWinNames.length;
		for(String str:allWinNames){
			if(str.equals("null"))
			totalNullWinCount++;
		}
		waitForElementPresent(locator, locator +" not found");
		selenium.click(locator);
		waitForPopup(totalWin,totalNullWinCount);
		if(selectPopup)
			selectPopUp();
	}
//#########################################################################################################################################	
	public void clickAndWaitToClosePopup(String locator)throws Exception{
		clickAndWaitToClosePopup(locator, true);
	}
	/**
	 * Create Selenium Objects Based On Browser
	 * @return
	 */
	//#########################################################################################################################################
	public DefaultSabaSelenium createSeleniumObject()
	{
		String browserURL = (String) getConfigMap().get("browserURL");
    	if (!browserURL.endsWith("/")) {
        	browserURL = browserURL + "/";
    	}
    	
    	WebDriver driver = (WebDriver) getConfigMap().get("driver");
   // 	DefaultSabaSelenium obj= (new DefaultSabaSelenium((String) getConfigMap().get("baseURL"), Integer.parseInt((String) getConfigMap().get("port")), (String) getConfigMap().get("browser"), browserURL));
    	DefaultSabaSelenium obj = (new DefaultSabaSelenium(driver, browserURL));
    	obj.start();
    	return obj;
	}
	/**
	 * Stop Selenium object
	 * @param Obj
	 */
	//#########################################################################################################################################
	public void stopSeleniumObject(DefaultSabaSelenium Obj)
	{
		Obj.stop();
	}
	/**
	 * initialize selenium object
	 * @param Obj
	 */
	//#########################################################################################################################################
	public void initSeleniumObject(DefaultSabaSelenium Obj){
		selenium=Obj;
	}
//#########################################################################################################################################
	/**
	 * 
	 * @param locator Locator of element where user want to click
	 * @param selectPopup true/false whether to call selectPopUp()
	 * @description Click on the element and waits for the popup to close and also select popup(Optional) 
	 * @throws Exception
	 */
	public void clickAndWaitToClosePopup(String locator, boolean selectPopup)throws Exception{
		int totalNullWinCount=0;
		String[] allWinNames=selenium.getAllWindowNames();
		for(String str:allWinNames){
			if(str.equals("null"))
			totalNullWinCount++;
		}
		waitForElementPresent(locator, locator +" not found");
		selenium.click(locator);
		int newTotalNullWinCount = 0 , time = 0;
		do {
			allWinNames=selenium.getAllWindowNames();
			for(String str:allWinNames){
				if(str.equals("null"))
					newTotalNullWinCount++;
			}
			Thread.sleep(500);
		} while (totalNullWinCount != (newTotalNullWinCount - 1) && time++<=20);
		Assert.assertTrue(totalNullWinCount == (newTotalNullWinCount - 1), "Popup is not closed");
		if(selectPopup)
			selectPopUp();
	}
//#########################################################################################################################################
		
}
