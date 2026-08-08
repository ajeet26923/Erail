import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class InspectErail {
    public static void main(String[] args) throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://erail.in/");
        Thread.sleep(3000);
        WebElement input = driver.findElement(By.id("txtStationFrom"));
        input.clear();
        input.sendKeys("DEL");
        Thread.sleep(2000);
        WebElement parent = input.findElement(By.xpath(".."));
        System.out.println("PARENT: " + parent.getTagName() + " id=" + parent.getAttribute("id") + " class=" + parent.getAttribute("class"));
        List<WebElement> siblings = parent.findElements(By.xpath("./*"));
        for (int i = 0; i < siblings.size(); i++) {
            WebElement s = siblings.get(i);
            System.out.println("CHILD[" + i + "]: " + s.getTagName() + " id=" + s.getAttribute("id") + " class=" + s.getAttribute("class"));
        }
        List<WebElement> old = driver.findElements(By.xpath("//div[contains(@class,'autocomplete') and not(contains(@class,'autocomplete-w'))]/div"));
        System.out.println("OLD_XPATH_COUNT=" + old.size());
        if (!old.isEmpty()) {
            WebElement ac = old.get(0).findElement(By.xpath(".."));
            System.out.println("AC_PARENT: " + ac.getTagName() + " class=" + ac.getAttribute("class"));
        }
        List<WebElement> css = driver.findElements(By.cssSelector("#txtStationFrom + div > div"));
        System.out.println("CSS_COUNT=" + css.size());
        driver.quit();
    }
}
