import com.lithium.a11y.BaseTest;
import com.google.common.base.Predicate;
import org.apache.commons.validator.routines.UrlValidator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
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
			Reporter.log("src: " + webElements.get(v).getAttribute("src"));
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
		webElements = driver.findElements(By.xpath("//a[@href and contains(@class, 'lia-link-navigation') and not(contains(@class, 'addthis_button_')) and not(contains(@class, 'lia-component-search-action-disable-auto-complete'))]"));

		Map<String, Integer> linkDestinations = new HashMap<>();
		for (int v = 0; v < webElements.size(); v++) {
			if (!webElements.get(v).getAttribute("href").contains("url")) {
				if (linkDestinations.containsKey(webElements.get(v).getAttribute("href"))) {
					linkDestinations.put(webElements.get(v).getAttribute("href"), linkDestinations.get(webElements.get(v).getAttribute("href")) + 1);
				} else {
					linkDestinations.put(webElements.get(v).getAttribute("href"), 1);
				}
			}
		}

		String link = null;
		String link2 = null;
		boolean didFail = false;
		for (Map.Entry<String, Integer> entries : linkDestinations.entrySet()) {

			if (entries.getValue() > 1) {
				link = entries.getKey();
				didFail = true;
				if (link.contains("/t5/"))
					link2 = link.substring(link.indexOf("/t5"), link.length());
				else
					link2 = link.substring(link.lastIndexOf("."), link.length());

				System.out.println(link2);
				Reporter.log(link2);
			}
		}

		if (didFail)
			Assert.fail("Possibility of image link");
	}

	@Test(dataProvider = "urlProvider")
	public void checkSkipNavigationLink(String url) {

		System.out.println(host + url + " : ");
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for links with text "Skip"
		List<WebElement> webElements = driver.findElements(By.partialLinkText("Skip to"));

		System.out.println("Number of skip link: " + webElements.size());

		for (int v = 0; v < webElements.size(); v++) {
			System.out.println("src: " + webElements.get(v).getAttribute("src"));
		}

		Assert.assertTrue(webElements.size() > 0);
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
	public void checkForFontElement(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for font elements
		List<WebElement> webElements = driver.findElements(By.xpath("//font"));

		System.out.println("Number of violations: " + webElements.size());

		for (int v = 0; v < webElements.size(); v++) {
			System.out.println("src: " + webElements.get(v).getAttribute("src"));
			Reporter.log("src: " + webElements.get(v).getAttribute("src"));
		}

		Assert.assertEquals(webElements.size(), 0);
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

		boolean didFail = false;
		for (Map.Entry<String, Integer> entries : elementsWithId.entrySet()) {

			if (entries.getValue() > 1) {
				didFail = true;
				System.out.println("id: " + entries.getKey() + " value: " + entries.getValue());

			}
		}

		if (didFail) Assert.fail("Possible Duplicate ids");
	}

	@Test(dataProvider = "urlProvider")
	public void checkDuplicateLinks(String url) { // Work in Progress

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		List<WebElement> elements;

		// Search for anchor tags that same href value
		elements = driver.findElements(By.xpath("//a[@href and contains(@class, 'lia-link-navigation')]"));

		Map<String, Map<String, String>> linkToTextMap = new HashMap<>();
		Map<String, Map<String, String>> textToLinkMap = new HashMap<>();

		for (int index = 0; index < elements.size(); index++) {

			WebElement element = elements.get(index);
			String text = element.getText();
			String link = element.getAttribute("href");

			Map<String, String> textValues;
			if (!linkToTextMap.containsKey(link)) {
				textValues = new HashMap<>();
				linkToTextMap.put(link, textValues);
			} else {
				textValues = linkToTextMap.get(link);
			}
			textValues.put(text, element.getTagName() + "[text=" + text  +"]");

			Map<String, String> linkValues;
			if (!textToLinkMap.containsKey(text)) {
				linkValues = new HashMap<>();
				textToLinkMap.put(text, linkValues);
			} else {
				linkValues = textToLinkMap.get(text);
			}
			linkValues.put(link, element.getTagName() + "[text=" + text  +"]");
		}

		for (Map.Entry<String, Map<String, String>> entry : linkToTextMap.entrySet()) {
			Map<String, String> texts = entry.getValue();
			if (texts.size() > 1) {
				System.out.println("href: " + entry.getKey() + " || text: " + entry.getValue());
			}
		}

		for (Map.Entry<String, Map<String, String>> entry : textToLinkMap.entrySet()) {
			Map<String, String> links = entry.getValue();
			if (links.size() > 1) {
				System.out.println("text: " + entry.getKey() + " || link: " + entry.getValue());
			}
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
		//List<WebElement> boldElements = driver.findElements(By.xpath("//b"));
		List<WebElement> italicElements = driver.findElements(By.xpath("//i[contains(@class, 'lia-message-stats-icon')]"));

//		System.out.println("Bold elements: " + boldElements.size());
//		for (int instance = 0; instance < boldElements.size(); instance++) {
//
//			System.out.println(boldElements.get(instance).getText());
//		}

		System.out.println("Italic elements: " + italicElements.size());
		for (int instance = 0; instance < italicElements.size(); instance++) {

			System.out.println(italicElements.get(instance).getText());
		}

		//Assert.assertEquals(boldElements.size(), 0);
		Assert.assertEquals(italicElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkEmptyListTags(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		BaseTest.doLoginWithUserAndPasswd(driver);

		// Search for ol and ul elements
		List<WebElement> unorderListElements = driver.findElements(By.xpath("//ul"));
		List<WebElement> orderListElements = driver.findElements(By.xpath("//ol"));

		System.out.print("Empty Unordered List elements: ");
		int noOfEmptyUnorderedElements=0;

		for(WebElement web : unorderListElements ){
			List<WebElement> liList = web.findElements(By.tagName("li"));
			if (liList.size() == 0)
				noOfEmptyUnorderedElements++;
		}
		System.out.println(noOfEmptyUnorderedElements);

		System.out.print("Empty Oordered List elements: ");
		int noOfEmptyOrderedElements=0;
		for(WebElement web : orderListElements ){
			List<WebElement> liList = web.findElements(By.tagName("li"));
			if (liList.size() == 0)
				noOfEmptyOrderedElements++;
		}
		System.out.println(noOfEmptyOrderedElements);

		Assert.assertEquals(noOfEmptyUnorderedElements, 0);
		Assert.assertEquals(noOfEmptyOrderedElements, 0);
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
			List<WebElement> centerElements = driver.findElements(By.tagName("center"));

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
			List<WebElement> iframeElements = driver.findElements(By.xpath("//iframe[not(contains(@width, '%')) and not(contains(@height, '%')) and not(contains(@src, 'https://staticxx.facebook.com/')) and not(contains(@src,'javascript'))]"));

			for (int instance = 0; instance < iframeElements.size(); instance++) {

				System.out.println("src: " + iframeElements.get(instance).getAttribute("src") + " width: " + iframeElements.get(instance).getAttribute("width") + " height: " + iframeElements.get(instance).getAttribute("height"));
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
				System.out.println("Tag: <" + element.getTagName() + "> onclick: " + element.getAttribute("onclick"));
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
				System.out.println("Tag: <" + element.getTagName() + "> text: " + element.getText() + "|| target: " + element.getAttribute("target"));
			}

			Assert.assertEquals(blankTargetLinkElements.size(), 0);
	}

	@Test(dataProvider = "urlProvider")
	public void checkSpanWithRoleImg(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {
			BaseTest.doLoginFromCommunityAdmin(driver);

			// Search for links with empty link text
			List<WebElement> spanImageElements = driver.findElements(By.xpath("//span[contains(@class, 'lia-fa') and not(@aria-label) and not(@role='img')]"));
			List<WebElement> spanElementsWithoutRole = new ArrayList<>();

			System.out.println("Span elements without role: " + spanImageElements.size());
			for (WebElement element: spanImageElements) {
				System.out.println("class: " + element.getAttribute("class"));
			}

			Assert.assertEquals(spanImageElements.size(), 0);
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkSpanWithoutAriaLabel(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {
			BaseTest.doLoginFromCommunityAdmin(driver);

			// Search for links with empty link text
			List<WebElement> postWithMostLikesHeadingElements = driver.findElements(By.xpath("//span[not(@aria-label) and contains(@class, 'lia-fa')]"));
			List<WebElement> spanElementsWithoutRole = new ArrayList<>();

			System.out.println("Headings: " + postWithMostLikesHeadingElements.size());
			for (WebElement element: postWithMostLikesHeadingElements) {
				System.out.println("class: " + element.getAttribute("class"));
			}

			Assert.assertEquals(postWithMostLikesHeadingElements.size(), 0);
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkTableAccessibility(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {
			BaseTest.doLoginWithUserAndPasswd(driver);

			// Search for links with empty link text
			List<WebElement> allTableElementsList = driver.findElements(By.xpath("//table"));
			List<WebElement> liaListSlimTablesList = driver.findElements(By.xpath("//table[@class='lia-list-slim']"));

			System.out.println("All tables: " + allTableElementsList.size());
			for (WebElement element: allTableElementsList) {
				System.out.println("class: " + element.getAttribute("class"));
			}

			System.out.println("Slim tables: " + liaListSlimTablesList.size());
			for (WebElement element: liaListSlimTablesList) {
				System.out.println("class: " + element.getAttribute("class"));
			}

			//Assert.assertEquals(allTableElementsList.size(), 0);
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkDescriptivePageTitle(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {
			BaseTest.doLoginWithUserAndPasswd(driver);

			String pageTitle = driver.getTitle();

			System.out.println("title: " + pageTitle);
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkAbbreviations(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {
			BaseTest.doLoginWithUserAndPasswd(driver);

			List<WebElement> webElements = driver.findElements(By.partialLinkText("(EN)"));
			List<WebElement> webElements2 = driver.findElements(By.xpath("//span[contains(@class, 'lia-breadcrumb-forum')]"));

			if (!webElements.isEmpty())
				System.out.println("text: " + webElements.get(0).getText() + " href: " + webElements.get(0).getAttribute("href"));

			if (!webElements2.isEmpty())
				System.out.println("text: " + webElements2.get(0).getText() + " href: " + webElements2.get(0).getAttribute("href"));

			if (!webElements.isEmpty() || !webElements2.isEmpty())
				Assert.fail("Abbreviations present");
		}
	}

	@Test(dataProvider = "urlProvider")
	public void checkNBSPAndFormLabel(String url) {

		System.out.println(host + url);
		driver.get(host + url);

		new WebDriverWait(driver, 60).until(
				(Predicate<WebDriver>) webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));


		if (driver.getPageSource().contains("An unexpected application exception has occurred.")) {
			Assert.fail("Exception occured for url: " + url);
		}
		else {

			List<WebElement> webElements = driver.findElements(By.xpath("//div[contains(@class,'lia-component-search-widget-spellcheck)']"));

			if (!webElements.isEmpty()) {
				Assert.fail("search field present");
			}
		}
	}
}

class URLDataProvider {

	public static String[][] urlsAsArray() {

		File urlFile = new File(System.getProperty("user.dir") + "/SBB_URLs.txt");
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