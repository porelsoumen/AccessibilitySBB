import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BaseTest {

    private static Properties properties;
    public static String ENV;
    public static String USER;
    public static String PASS;
    private static WebDriver driver;

    static{
        try {
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/configs/env.config");
            properties = new Properties();
            properties.load(reader);

            ENV = properties.getProperty("server");
            USER = properties.getProperty("user");
            PASS = properties.getProperty("pass");

            System.setProperty("webdriver.chrome.driver", "/Users/soumen.porel/Downloads/osx/chromedriver");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getAllURLsFromFile() {

        ArrayList<String> urls = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/URLs.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));


            String url;

            while ((url = br.readLine()) != null) {

                urls.add(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

    public static void doLoginWithUserAndPasswd(WebDriver driver) {

        if (driver.findElements(By.id("email")).size() > 0 && driver.findElements(By.id("mainform")).size() > 0){
            driver.findElement(By.id("email")).sendKeys(BaseTest.USER);
            driver.findElement(By.id("pwd")).sendKeys(BaseTest.PASS);
            driver.findElement(By.name("LOGIN")).click();

            new WebDriverWait(driver, 40).until(ExpectedConditions.visibilityOf(driver.findElement(By.linkText("ADATesting"))));
        }
    }

    public static void doLoginFromCommunityAdmin() {

        //Reuse from mineraloil-lia
    }

    public static void checkForAttributeinHTML(WebDriver driver, String attr, boolean shouldExist) {

        // Search for elements having attribute attr
        List<WebElement> webElements;
        webElements = driver.findElements(By.xpath("//*[@" + attr + "]"));

        for (int instance=0 ; instance < webElements.size(); instance++) {
            System.out.println("Tagname: " + webElements.get(instance).getTagName() + " " + attr + ": " + webElements.get(instance).getAttribute(attr));
        }

        if (shouldExist) {
            Assert.assertTrue(webElements.size() > 0);
        }
        else
        {
            Assert.assertEquals(webElements.size(), 0);
        }
    }

    public static WebDriver getDriver() {
        if (driver == null)
            driver = new ChromeDriver();

        return driver;
    }
}
