/**
 * Copyright (C) 2015 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

import com.google.common.base.Predicate;
import org.apache.commons.validator.routines.UrlValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IdentifyA11yViolationsSBB {


	private static String host;
	private static ArrayList<String> urls;
	private static WebDriver driver;

	//private static final URL scriptUrl = IdentifyA11yViolationsSBB.class.getResource("/axe.min.js");

	/**
	 * Instantiate the WebDriver and navigate to the test site
	 */
	@BeforeTest
	public void setUp() {

		urls = BaseTest.getAllURLsFromFile();
		host = BaseTest.ENV;

		driver = BaseTest.getDriver();
	}

	@DataProvider
	public static Object[][] urlProvider() {

		return URLDataProvider.urlsAsArray();
	}

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@AfterClass
	public static void tearDown() {
		driver.quit();
	}


	@Test(dataProvider = "urlProvider")
	public void checkAltTextsForImages(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		List<WebElement> webElements;

		// Search for images that either don't have an alt attribute or the alt attribute is empty
		webElements = driver.findElements(By.xpath("//img[not(@alt) or @alt='']"));

		System.out.println("Number of violations: " + webElements.size());

		for (int v = 0; v < webElements.size(); v++) {
			System.out.println("src: " + webElements.get(v).getAttribute("src"));

		}
		Assert.assertEquals(webElements.size(), 0);
	}

	public void findMissingImageAlts(String url) {

		System.out.println(url);
//		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
//				.options("{ runOnly: {type: 'rule', values: ['image-alt'] } }")
//				.analyze();
//
//		JSONArray violations = responseJSON.getJSONArray("violations");
//
//		if (violations.length() == 0) {
//			System.out.println("No violations found");
//			assertTrue("No violations found", true);
//		} else {
//			System.out.println("Violations found");
//			AXE.writeResults(testName.getMethodName(), responseJSON);
//			//System.out.println(responseJSON);
//			//System.out.println(url);
//			//assertTrue(AXE.report(violations), false);
//		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkImageLinks(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		List<WebElement> webElements;

		// Search for anchor tags that same href value
		webElements = driver.findElements(By.xpath("//a[@href and contains(@class, 'lia-link-navigation') and not(contains(@class, 'addthis_button_'))] "));

		Map<String, Integer> linkDestinations = new HashMap<>();
		for (int v = 0; v < webElements.size(); v++) {
			if (linkDestinations.containsKey(webElements.get(v).getAttribute("href"))) {
				linkDestinations.put(webElements.get(v).getAttribute("href"), linkDestinations.get(webElements.get(v).getAttribute("href")) + 1);
			} else {
				linkDestinations.put(webElements.get(v).getAttribute("href"), 1);
			}
		}

		String link = null;
		String link2 = null;
		for (Map.Entry<String, Integer> entries : linkDestinations.entrySet()) {

			if (entries.getValue() > 1) {
				link = entries.getKey();

				if (link.contains("/t5/"))
					link2 = link.substring(link.indexOf("/t5"), link.length());
				else
					link2 = link.substring(link.lastIndexOf("."), link.length());

				System.out.println(link2);
			}
			Assert.assertTrue(entries.getValue() == 0);
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkSkipNavigationLink(String url) {

		System.out.println(host + url + " : ");
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for text "Skip" in the page source
		System.out.println(driver.getPageSource().contains("Skip") ? "This url has a Skip Navigation Link" : "This url does not have a Skip Navigation Link");

		Assert.assertTrue(driver.getPageSource().contains("Skip"));
	}

	@Test(dataProvider = "urlProvider")
	public void checkForBorderInHTML(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for elements with border attribute
		BaseTest.checkForAttributeinHTML(driver, "border", false);
	}

	@Test(dataProvider = "urlProvider")
	public void checkForFontAttributeInHTML(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for elements with font-size attribute
		BaseTest.checkForAttributeinHTML(driver, "font-size", false);

	}

	@Test(dataProvider = "urlProvider")
	public void checkForDuplicateId(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		List<WebElement> webElements;

		// Search for elements with duplicate id values
		webElements = driver.findElements(By.xpath("//*[@id]"));

		Map<String, Integer> elementsWithId = new HashMap<>();
		for (int v = 0; v < webElements.size(); v++) {
			if (elementsWithId.containsKey(webElements.get(v).getAttribute("id"))) {
				elementsWithId.put(webElements.get(v).getAttribute("id"), elementsWithId.get(webElements.get(v).getAttribute("id")) + 1);
			} else {
				elementsWithId.put(webElements.get(v).getAttribute("id"), 1);
			}
		}

		for (Map.Entry<String, Integer> entries : elementsWithId.entrySet()) {

			if (entries.getValue() > 1) {

				System.out.println("id: " + entries.getKey());
				Assert.fail();
			}
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkDuplicateLinks(String url) { // Work in Progress

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		List<WebElement> webElements;

		// Search for anchor tags that same href value
		webElements = driver.findElements(By.xpath("//a[@href and contains(@class, 'lia-link-navigation') and not(contains(@class, 'addthis_button_'))]"));

		Map<String, String> linkDestinations = new HashMap<>();
		for (int v = 0; v < webElements.size(); v++) {
			linkDestinations.put(webElements.get(v).getAttribute("href"), linkDestinations.get(webElements.get(v).getText()));
		}

		Map<String, Integer> sameHrefDiffText = new HashMap<>();
		Map<String, Integer> diffHrefSameText = new HashMap<>();

		for (String href : linkDestinations.keySet()) {
			if (sameHrefDiffText.containsKey(href))
				sameHrefDiffText.put(href, sameHrefDiffText.get(href) + 1);
			else
				sameHrefDiffText.put(href, 1);
		}

		for (String text : linkDestinations.values()) {
			if (diffHrefSameText.containsKey(text))
				diffHrefSameText.put(text, diffHrefSameText.get(text) + 1);
			else
				diffHrefSameText.put(text, 1);
		}

	}

	@Test(dataProvider = "urlProvider")
	public void checkBoldItalicInHTML(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for elements using bold or italic tag
		List<WebElement> boldElements = driver.findElements(By.xpath("//b"));
		List<WebElement> italicElements = driver.findElements(By.xpath("//i"));

		System.out.println("Bold elements: " + boldElements.size());
		for (int instance = 0; instance < boldElements.size(); instance++) {

			System.out.println(boldElements.get(instance).getText());
		}

		System.out.println("Italic elements: " + italicElements.size());
		for (int instance = 0; instance < italicElements.size(); instance++) {

			System.out.println(italicElements.get(instance).getText());
		}

		Assert.assertEquals(boldElements.size(), 0);
		Assert.assertEquals(italicElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkEmptyListTags(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for elements using bold or italic tag
		List<WebElement> defListElements = driver.findElements(By.xpath("//dl"));
		List<WebElement> unorderListElements = driver.findElements(By.xpath("//ul"));
		List<WebElement> orderListElements = driver.findElements(By.xpath("//ol"));

		System.out.println("Empty Definition List elements: " + defListElements.size());
		for (int instance = 0; instance < defListElements.size(); instance++) {

			System.out.println(defListElements.get(instance).getText());
		}

		System.out.println("Empty Unordered List elements: " + unorderListElements.size());
		for (int instance = 0; instance < unorderListElements.size(); instance++) {

			System.out.println(unorderListElements.get(instance).getText());
		}

		System.out.println("Empty Ordered List elements: " + orderListElements.size());
		for (int instance = 0; instance < orderListElements.size(); instance++) {

			System.out.println(orderListElements.get(instance).getText());
		}

		Assert.assertEquals(defListElements.size(), 0);
		Assert.assertEquals(unorderListElements.size(), 0);
		Assert.assertEquals(orderListElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkForFontColorAttributeInHTML(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for elements with font-color attribute
		BaseTest.checkForAttributeinHTML(driver, "font-color", false);
	}

	@Test(dataProvider = "urlProvider")
	public void checkIframeWithValidSrcAttr(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for iframe elements without valid src attribute
		List<WebElement> iframeElements = driver.findElements(By.xpath("//iframe[@src]"));
		UrlValidator urlValidator = new UrlValidator();

		for (int instance = 0; instance < iframeElements.size(); instance++) {

			System.out.println(iframeElements.get(instance).getText());
			String iframeSrc = iframeElements.get(instance).getAttribute("src");

			//List iframes with invalid src attribute values
			if (!urlValidator.isValid(iframeSrc)) {
				System.out.println(iframeSrc);
				Assert.fail();
			}
		}

	}

	@Test(dataProvider = "urlProvider")
	public void checkIframeWithEmptyText(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for iframe elements empty text between start and end tags
		List<WebElement> iframeElements = driver.findElements(By.xpath("//iframe"));

		for (int instance = 0; instance < iframeElements.size(); instance++) {

			if (iframeElements.get(instance).getText().isEmpty()) {
				System.out.println("src: " + iframeElements.get(instance).getAttribute("src"));
			}
		}

		Assert.assertEquals(iframeElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkIframeWithEmptyTitle(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for iframe elements without a title attribute
			List<WebElement> iframeElements = driver.findElements(By.xpath("//iframe[not(@title)]"));

			for (int instance = 0; instance < iframeElements.size(); instance++) {

				System.out.println("src: " + iframeElements.get(instance).getAttribute("src"));
			}

			Assert.assertEquals(iframeElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checklangAttr(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for html elements without lang attribute
			List<WebElement> langElements = driver.findElements(By.xpath("//html[not(@lang)]"));

			for (int instance = 0; instance < langElements.size(); instance++) {

				System.out.println("No lang attribute found");
			}

			Assert.assertEquals(langElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkCenterTagInHTML(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for center elements
			List<WebElement> centerElements = driver.findElements(By.xpath("//center"));

			for (int instance = 0; instance < centerElements.size(); instance++) {

				System.out.println("Location: " + centerElements.get(instance).getLocation());
			}

			Assert.assertEquals(centerElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkIframeRelativeSize(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for iframe elements with absolute sizing
			List<WebElement> iframeElements = driver.findElements(By.xpath("//iframe[not(contains(@width, '%')) and not(contains(@height, '%'))]"));

			for (int instance = 0; instance < iframeElements.size(); instance++) {

				System.out.println(iframeElements.get(instance).getText() + iframeElements.get(instance).getAttribute("width"));
			}

			Assert.assertEquals(iframeElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkEmptyLinkText(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for links with empty link text
			List<WebElement> emptyLinkElements = driver.findElements(By.xpath("//a"));

			System.out.println("Empty link text: ");
			for (int instance = 0; instance < emptyLinkElements.size(); instance++) {

				if (emptyLinkElements.get(instance).getText().isEmpty())
					System.out.println("href: " + emptyLinkElements.get(instance).getAttribute("href"));
			}

			Assert.assertEquals(emptyLinkElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkSpanAndDivWithOnClick(String url) {

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for span and div elements with onclick attribute
			List<WebElement> spansWithOnClick = driver.findElements(By.xpath("//span[@onclick]"));
			List<WebElement> divsWithOnClick = driver.findElements(By.xpath("//div[@onclick]"));

			System.out.println("Spans with Onclick: " + spansWithOnClick.size());
			for (WebElement element: spansWithOnClick) {
				System.out.println(element.getText() + " onclick: " + element.getAttribute("onclick"));
			}

			System.out.println("Divs with Onclick: " + divsWithOnClick.size());
			for (WebElement element: divsWithOnClick) {
				System.out.println(element.getText() + " onclick: " + element.getAttribute("onclick"));
			}

			Assert.assertEquals(spansWithOnClick.size(), 0);
			Assert.assertEquals(divsWithOnClick.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkPurposeOfButtons(String url) { // There should be aria-label attribute for actions

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for links with empty link text
			List<WebElement> kudosButtonElements = driver.findElements(By.xpath("//a[@id='kudoEntity']"));

			System.out.println("kudos Button without aria-labels: " + kudosButtonElements.size());
			for (WebElement element: kudosButtonElements) {
				System.out.println("Tag: " + element.getTagName() + " id: " + element.getAttribute("id") + " aria-label: " + element.getAttribute("aria-label"));
			}

			Assert.assertEquals(kudosButtonElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkOnClickAttr(String url) { // We should use onchange attr instead of onclick to allow handling from keyboard

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for links with empty link text
			List<WebElement> onclickAttrElements = driver.findElements(By.xpath("//*[@onclick]"));

			System.out.println("onclick attribute elements: " + onclickAttrElements.size());
			for (WebElement element: onclickAttrElements) {
				System.out.println("Tag: " + element.getTagName() + " onclick: " + element.getAttribute("onclick"));
			}

			Assert.assertEquals(onclickAttrElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkLinkTargetBlank(String url) { //Avoid using links and form actions that pop up additional windows, leaving the window context.

			System.out.println(host + url);
			driver.get(host + url);

			new WebDriverWait(driver, 60).until(
					(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for links with empty link text
			List<WebElement> blankTargetLinkElements = driver.findElements(By.xpath("//a[@target='_blank']"));

			System.out.println("Links with target _blank: " + blankTargetLinkElements.size());
			for (WebElement element: blankTargetLinkElements) {
				System.out.println("Tag: " + element.getTagName() + " text: " + element.getText() + " target: " + element.getAttribute("target"));
			}

			Assert.assertEquals(blankTargetLinkElements.size(), 0);
	}
}

class URLDataProvider {

	public static String[][] urlsAsArray() {

		File urlFile = new File(System.getProperty("user.dir") + "/URLs.txt");
		ArrayList<String> urlList = new ArrayList<>();
		String url;

		String[][] urls;

		try {
			FileInputStream fileInputStream = new FileInputStream(urlFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			while ((url = br.readLine()) != null) {
				urlList.add(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		urls = new String[urlList.size()][1];

		for (int i=0 ; i<urlList.size() ; i++) {

			urls[i][0] = urlList.get(i);
		}

		return urls;
	}
}