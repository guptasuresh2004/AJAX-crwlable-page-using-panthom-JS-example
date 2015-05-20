package com.citrixosd.utils;

import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import java.util.concurrent.Callable;
/**
* it generates Page HTML source  at server side after executing all page Java Script. It uses panthomJS Driver
* to generate HTML source 
*/
public class HTMLSnippetHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			HTMLSnippetHelper.class);

	/**
	 * @return PhantomJSDriver
	 */
	private PhantomJSDriver getPhantomJSDriver() {
		PhantomJSDriver driver = null;
		try {

			DesiredCapabilities dcaps = new DesiredCapabilities();
			dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
				"--web-security=false",
					"--ssl-protocol=any",
					"--ignore-ssl-errors=true",
					"--webdriver-loglevel=NONE"
			});
			dcaps.setJavascriptEnabled(true);
			//TODO-Path of the driver file should be changed as per the file location  
			dcaps.setCapability(
			PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"D:\\CQ-server\\author\\driver\\phantomjs.exe");

			driver = new PhantomJSDriver(dcaps);

			return driver;
		} catch (Exception e) {
			LOGGER.info("There is some problem while creating pantamJS driver" + e.getMessage());
		}
		return null;
	}

	/**
	 * @param url
	 * @return
	 */
	public String getHTMLSnippet(final String url) {
		String htmlstring = "";
		if (url == null || url.isEmpty()) {
			return null;
		}
		LOGGER.info("creating snapshot for url" + url);
		final WebDriver  driver = this.getPhantomJSDriver();
		try {

			driver.get(url);//creating HTML SNAP
			String pageTitle = driver.getTitle();
			LOGGER.info("pageTitle" + pageTitle);
			
			//handling for CQ author server to login page 
			if (pageTitle != null && pageTitle.contains("CQ5 - Sign In")) {
				driver.findElement(By.id("input-username")).sendKeys("admin");
				driver.findElement(By.id("input-password")).sendKeys("admin");
				driver.findElement(By.id("input-submit")).submit();
			}
			
			// timed out after 15 seconds - process should not take more than 15 minutes 
			//Auto kill and release resources after 15 seconds
			
			TimeLimiter limiter = new SimpleTimeLimiter();
			 htmlstring = limiter.callWithTimeout(new Callable<String>() {
			    public String call() {
			        return driver.getPageSource();
			    }
			  }, 15, TimeUnit.SECONDS, false);
			
			LOGGER.info("Page HTML is: " + htmlstring);

		} catch (Exception e) {
			LOGGER.info("There is some problem while creating html sanpshot" + e.getMessage());
			e.printStackTrace();

		} finally {
			if (driver != null) driver.quit(); // clean the driver and release resources  
		}

		return htmlstring;

	}

	public static void main(String[] args) {
        // Main method testing 
		HTMLSnippetHelper HTMSnippetHelper = new HTMLSnippetHelper();
		String html = HTMSnippetHelper.getHTMLSnippet("http://localhost:4502/content/g2m-blueprint/en_GB/meeting/how-it-works/?cat=22");
		LOGGER.info("Page HTML is in main: " + html);
		System.out.println("Page HTML is: " + html);
	}
	/**
	 * @param req
	 * @return
	 */
	public static String getURL(HttpServletRequest req) {

		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp
		String servletPath = req.getServletPath(); // /servlet/MyServlet
		String pathInfo = req.getPathInfo(); // /a/b;c=123
		String queryString = req.getQueryString(); // d=789

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append(servletPath);

		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}
	/**
	 * @param url
	 * @return
	 */
	public static String rewriteQueryString(String url) {

		String prittyURL = null;
		prittyURL = url.replaceFirst("\\?_escaped_fragment_=", "#!");
		return prittyURL;

	}
}
