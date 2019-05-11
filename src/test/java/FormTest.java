
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

interface Classificator {
    void classificate();
}

public class FormTest {

    private static WebDriver driver;
    private static final StringBuffer verificationErrors = new StringBuffer();
    private WebDriverWait waitDriver;
    private WebElement button, birthDate, parentsCheckbox, doctorCheckbox;
    private static Classificator classificatorNoData;

    @BeforeClass
    public static void addChromeDriverUrlProperty() {
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");
    }

    //tworze metode klasyfikatora dla blednych danych aby nie powtarzac kodu w kazdym tescie
    @BeforeClass
    public static void createNoDataClassificator() {
        classificatorNoData = () -> {
            try {
                assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Blad danych", driver.findElement(By.id("returnSt")).getText());
            } catch (Error e) {
                verificationErrors.append(e.toString());
            }
        };
    }


    @Before
    public void init() throws Exception {
        driver = new ChromeDriver();
        waitDriver = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://lamp.ii.us.edu.pl/~mtdyd/zawody/");
        driver.findElement(By.id("inputEmail3")).sendKeys("Imie");
        driver.findElement(By.id("inputPassword3")).sendKeys("Nazwisko");
        birthDate = driver.findElement(By.id("dataU"));
        button = driver.findElement(By.className("btn-default"));
        parentsCheckbox = driver.findElement(By.id("rodzice"));
        doctorCheckbox = driver.findElement(By.id("lekarz"));
    }

    @Test
    public void testUnder10() throws Exception {
        Classificator classificator = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Brak kwalifikacji", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[4];
        Arrays.fill(classificators, classificator);
        fillFormFromLoop(2009, 2019, classificators);
    }


    @Test
    public void testBOMBELEK() throws Exception {
        Classificator classificatorBOMBELEK = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Skrzat", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[] {classificatorNoData, classificatorNoData,  classificatorBOMBELEK, classificatorNoData};
        fillFormFromLoop(2007, 2008, classificators);
    }


    @Test
    public void testYoungster() throws Exception {
        Classificator classificatorYoungster = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Mlodzik", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[] {classificatorNoData, classificatorNoData, classificatorYoungster, classificatorNoData};
        fillFormFromLoop(2005, 2006, classificators);
    }


    @Test
    public void testJunior() throws Exception {
        Classificator classificatorJunior = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Junior", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[] {classificatorNoData, classificatorNoData, classificatorJunior, classificatorNoData};
        fillFormFromLoop(2001, 2004, classificators);
    }

    @Test
    public void testAdult() throws Exception {
        Classificator classificatorAdult = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Dorosly", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[4];
        Arrays.fill(classificators, classificatorAdult);
        fillFormFromArray(new int[] {1954, 2000}, classificators);
    }

    @Test
    public void testSenior() throws Exception {
        Classificator classificatorSenior = new Classificator() {
            @Override
            public void classificate() {
                try {
                    assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii Senior", driver.findElement(By.id("returnSt")).getText());
                } catch (Error e) {
                    verificationErrors.append(e.toString());
                }
            }
        };
        Classificator[] classificators = new Classificator[] {classificatorNoData, classificatorNoData, classificatorSenior, classificatorSenior};
        fillFormFromArray(new int[] {1953}, classificators);
    }

    @Test
    public void checkDate() throws Exception {
        birthDate.sendKeys("bledna data");
        if (!parentsCheckbox.isSelected()) {
            parentsCheckbox.click();
        }
        if (!doctorCheckbox.isSelected()) {
            doctorCheckbox.click();
        }
        button.click();
        closeAlert();
        classificatorNoData.classificate();
        birthDate.clear();
        birthDate.sendKeys("1995-01-01");
        button.click();
        closeAlert();
        classificatorNoData.classificate();
    }




    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private void fillFormFromLoop(int start, int end, Classificator[] classificators) {
        for(int year = start; year<=end; year++){
            fillForm(year, classificators);
        }
    }
    // Dla doroslych sprawdzam tylko date koncowa i poaczatkowa dlatego tablica
    private void fillFormFromArray(int[] years, Classificator[] classificators){
        for(int index = 0; index < years.length; index++) {
            fillForm(years[index], classificators);
        }
    }

    // Wypelnianie formularza
    private void fillForm(int year, Classificator[] classificators) {
        birthDate.clear();
        birthDate.sendKeys("01-07-" + year);
        button.click();
        closeAlert();
        classificators[0].classificate();
        if(!parentsCheckbox.isSelected()){
            parentsCheckbox.click();
        }
        button.click();
        closeAlert();
        classificators[1].classificate();
        if(!doctorCheckbox.isSelected()){
            doctorCheckbox.click();
        }
        button.click();
        closeAlert();
        classificators[2].classificate();
        if(parentsCheckbox.isSelected()){
            parentsCheckbox.click();
        }
        button.click();
        closeAlert();
        classificators[3].classificate();
        if(doctorCheckbox.isSelected()){
            doctorCheckbox.click();
        }
    }

    public void closeAlert() {
        Alert alert = waitDriver.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        alert.dismiss();
    }
}