package com.sabaFrameworkCode;


import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;


public class DefaultSabaSelenium extends WebDriverBackedSelenium {
    public static Map<String, String> constantsMap;

    static {
        constantsMap = ConstantsMap.getMap();
    }

  /*  public DefaultSabaSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL) {
      	super(serverHost, serverPort, browserStartCommand, browserURL);
    	
    	
    }*/

    public DefaultSabaSelenium(WebDriver driver, String url) {
    	   	  
    	   	 super(driver, url);
    	   // 	Selenium selenium = new WebDriverBackedSelenium(driver, "http://www.yoursite.com");
    }
    
    // Overridden methods from Selenium.java

    /**
     *
     * @param answer
     * @param errorMsg
     */
    public void answerOnNextPrompt(String answer, String errorMsg) {
        try {
        	
            super.answerOnNextPrompt(answer);
        } catch (Exception e) {
            handleError(null, e, errorMsg);
        }
    }

    /**
     *
     * @param locator
     * @param errorMsg
     */
    public void check(String locator, String errorMsg) {
        try {
            super.check(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    public void chooseCancelOnNextConfirmation(String errorMsg) {
        try {
            super.chooseCancelOnNextConfirmation();
        } catch (Exception e) {
            handleError(null, e, errorMsg);
        }
    }

    public void chooseOkOnNextConfirmation(String errorMsg) {
        try {
            super.chooseOkOnNextConfirmation();
        } catch (Exception e) {
            handleError(null, e, errorMsg);
        }
    }

    public void click(String locator, String errorMsg) {
        try {
            super.click(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    private void handleError(String locator, Exception e, String errorMsg) {
    	String errorMsgLocator=errorMsg;
        if (constantsMap.get(locator) != null) {
        	errorMsgLocator = errorMsg + "locator: " + constantsMap.get(locator);
        } else if (locator != null) {
        	errorMsgLocator = errorMsg + "locator: " + locator;
        }

        BaseSeleniumTest.log(errorMsgLocator);
        if(e!=null){ 
        	e.printStackTrace();
        	throw new AssertionError(errorMsg);
        }else{
        	throw new AssertionError(errorMsg);
        }
        
       
    }

    public void doubleClick(String locator, String errorMsg) {
        try {
            super.doubleClick(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    /**
     *
     * @param fromLocator
     * @param toLocator
     * @param errorMsg
     */
    public void dragAndDropToObject(String fromLocator, String toLocator, String errorMsg) {
        try {
            super.dragAndDropToObject(fromLocator, toLocator);
        } catch (Exception e) {
            handleError(fromLocator, e, errorMsg);
        }
    }

    public String getAttribute(String locator, String errorMsg) {
        String confirmation = "";

        try {
            confirmation = super.getAttribute(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return confirmation;
    }

    public String getConfirmation(String errorMsg) {
        String confirmation = "";

        try {
            confirmation = super.getConfirmation();
        } catch (Exception e) {
            handleError(null, e, errorMsg);
        }

        return confirmation;
    }

    public String getSelectedLabel(String locator, String errorMsg) {
        String selectedLabel = "";

        try {
            selectedLabel = super.getSelectedLabel(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedLabel;
    }

    public String getSelectedValue(String locator, String errorMsg) {
        String selectedValue = "";

        try {
            selectedValue = super.getSelectedValue(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public String getText(String locator, String errorMsg) {
        String selectedValue = "";

        try {
            selectedValue = super.getText(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public String getValue(String locator, String errorMsg) {
        String selectedValue = "";

        try {
            selectedValue = super.getValue(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public Number getXpathCount(String locator, String errorMsg) {
        Number selectedValue = null;

        try {
            selectedValue = super.getXpathCount(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public boolean isAlertPresent(String errorMsg) {
        boolean selectedValue = false;

        try {
            selectedValue = super.isAlertPresent();
        } catch (Exception e) {
            handleError(null, e, errorMsg);
        }

        return selectedValue;
    }

    public boolean isChecked(String locator, String errorMsg) {
        boolean selectedValue = false;

        try {
            selectedValue = super.isChecked(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public boolean isEditable(String locator, String errorMsg) {
        boolean selectedValue = false;

        try {
            selectedValue = super.isEditable(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    public boolean isElementPresent(String locator, String errorMsg) {
        boolean selectedValue = false;

        try {
            selectedValue = super.isElementPresent(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }

        return selectedValue;
    }

    /**
     *
     * @param locator
     * @param stringToType
     * @param errorMsg
     */
    public void keyPress(String locator, String stringToType, String errorMsg) {
        try {
            super.keyPress(locator, stringToType);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    public void select(String locator, String label, String errorMsg) {
        try {
            super.select(locator, label);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    public void type(String locator, String arg1, String errorMsg) {
        try {
            super.type(locator, arg1);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    public void typeKeys(String locator, String arg1, String errorMsg) {
        try {
            super.typeKeys(locator, arg1);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    public void uncheck(String locator, String errorMsg) {
        try {
            super.uncheck(locator);
        } catch (Exception e) {
            handleError(locator, e, errorMsg);
        }
    }

    /**
     *
     * @param windowId
     * @param timeout
     * @param errorMsg
     */
    public void waitForPopUp(String windowId, String timeout, String errorMsg) {
        super.waitForPopUp(windowId, timeout);
    }

    public void waitForPageToLoad(String timeout, String errorMsg) {
   
    	        super.waitForPageToLoad(timeout);
         

    }
    

    
    /**
     * @param text text that we need to wait for
     * @param errorMsg error message to be displayed
     * @param wait the time in ms to wait for
     * @throws Exception
     */
    public void waitForTextPresent(String text, String errorMsg, int wait) throws Exception{
  		for (int second = 0;; second++) {
  			if (second >= wait) handleError(null,null, errorMsg);
  			try { if (super.isTextPresent(text)) break; } catch (Exception e) {}
  			Thread.sleep(1000);
  		}
    }
    
    
    /**
     * Used to wait until a particular element appears on the page.
     * @param locator locator of the element that we need to wait for
     * @param errorMsg error message to be displayed
     * @param wait the time in ms to wait for
     * @throws Exception
     */
    public void waitForElementPresent(String locator,String errorMsg, int wait) throws Exception{
  		for (int second = 0;; second++) {
			if (second >= wait) handleError(locator,null, errorMsg);
  			try { if (super.isElementPresent(locator)) break; } catch (Exception e) {}
  			Thread.sleep(1000);
  		}
    }

    /**
     * Used to wait untill a particular text disappears from the page.
     * @param text text that we need to wait for
     * @param errorMsg error message to be displayed
     * @param wait the time in ms to wait for
     * @throws Exception
     */
    public void waitForElementNotPresent(String locator,String errorMsg, int wait) throws Exception{
  		for (int second = 0;; second++) {
  			if (second >= wait) handleError(locator,null, errorMsg);
  			try { if (!super.isElementPresent(locator)) break; } catch (Exception e) {}
  			Thread.sleep(1000);
  		}
    }
    /**
     * Used to wait untill a particular text disappears from the page.
     * @param text text that we need to wait for
     * @param errorMsg error message to be displayed
     * @param wait the time in ms to wait for
     * @throws Exception
     */

	public void waitForTextNotPresent(String text, String errorMsg, int wait) throws Exception {
		for (int second = 0;; second++) {
  			if (second >= wait) handleError(null,null, errorMsg);
  			try { if (!super.isTextPresent(text)) break; } catch (Exception e) {}
  			Thread.sleep(1000);
  		}
	}

}
