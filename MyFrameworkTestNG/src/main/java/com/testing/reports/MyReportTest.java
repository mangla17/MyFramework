package com.testing.reports;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class MyReportTest {
	
	  ExtentReports  report;
	  ExtentTest test;
	
	
	@BeforeClass
	public void testBeforeClass(){
		System.out.println("Beforeclass method starts");
		report = new ExtentReports(System.getProperty("user.dir")+"\\ExtentReportResults.html" , true);
		
		System.out.println("Beffore class method end");
	}
	
	@Test
	public void testFirst(){
		System.out.println("First method starts");
		test = report.startTest("TestPass");
		test.log(LogStatus.PASS, "testFirst");
	}
	
	@Test
	public void testSecond(){
		System.out.println("Second method starts");
		test = report.startTest("testPass");
		test.log(LogStatus.PASS, "testSecond");
	}
	
	@AfterClass
	public void testAfterClass(){
		System.out.println("AfterClass method");
		
		report.endTest(test);
		report.flush();
	}
	
	

}
