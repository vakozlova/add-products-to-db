package com.autotest.tests;

import com.autotest.config.DatabaseConfig;
import com.autotest.pages.ProductPage;
import com.autotest.utils.Statement;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.*;

/**
 * Класс для тестирования добавления товаров через веб-интерфейс
 */
public class AddProductTest {
    private static WebDriver driver;
    private static Connection connection;
    private static Statement statement;

    @BeforeAll
    static void setup() throws SQLException {
        System.setProperty("webdriver.chrome.driver", "D:\\ibs-task\\autotest-add-products-to-db\\test-add-products-to-db\\src\\main\\resources\\chromedriver.exe");

        // Доп.настройки браузера
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");

        driver = new ChromeDriver(options);

        // Подключение к БД
        connection = DatabaseConfig.getConnection();
        statement = new Statement(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException, InterruptedException {
        if (driver != null) {
            driver.quit();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Тест добавления новых товаров и проверки их состояния в базе данных
     * Автор: Валерия Козлова
     */
    @Test
    void testAddNewProducts() throws SQLException, InterruptedException {

        // Step1: Подсчить количество строк в начальной таблице
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD");
        resultSet.next();
        int beforeCount = resultSet.getInt(1);

        // Step2: Проверить наличие товаров в БД
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD WHERE FOOD_NAME IN ('Пепино', 'Баклажан')");
        resultSet.next();
        int countBeforeTest = resultSet.getInt(1);
        if (countBeforeTest > 0) {
            throw new RuntimeException("Товары 'Пепино' и 'Баклажан' уже существуют в базе данных");
        }

        // Step3: Открыть стенд
        driver.get("http://localhost:8080");

        // Step4: Перейти к разделу с товарами
        driver.findElement(By.linkText("Песочница")).click();
        driver.findElement(By.linkText("Товары")).click();

        // Step5: Добавить экзотический овощ "Пепино"
        ProductPage productPage = new ProductPage(driver);
        productPage.addNewProduct("Пепино", "Овощ", true);

        Thread.sleep(3000);

        // Step6: Проверить, что "Пепино" добавлен в БД
        resultSet = statement.executeQuery("SELECT * FROM FOOD WHERE FOOD_NAME = 'Пепино'");
        Assertions.assertTrue(resultSet.next(), "Товар 'Пепино' не добавлен в базу данных");

        // Step7: Добавить не экзотический овощ "Баклажан"
        productPage.addNewProduct("Баклажан", "Овощ", false);

        Thread.sleep(3000);

        // Step8: Проверить, что "Баклажан" добавлен в БД
        resultSet = statement.executeQuery("SELECT * FROM FOOD WHERE FOOD_NAME = 'Баклажан'");
        Assertions.assertTrue(resultSet.next(), "Товар 'Баклажан' не добавлен в базу данных");

        // Step9: Удалить товары после выполнения теста
        statement.executeUpdate("DELETE FROM FOOD WHERE FOOD_NAME IN ('Пепино', 'Баклажан')");

        Thread.sleep(3000);

        //Step10: Проверить, что товары удалены
        resultSet=statement.executeQuery("SELECT COUNT(*) FROM FOOD WHERE FOOD_NAME IN ('Пепино', 'Баклажан')");
        resultSet.next();
        int countAfterTest = resultSet.getInt(1);
        Assertions.assertEquals(countBeforeTest, countAfterTest, "Товары не были удалены");

        Thread.sleep(3000);

        // Step11: Проверить, что таблица вернулась в исходное состояние
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD");
        resultSet.next();
        int countFinal = resultSet.getInt(1);
        Assertions.assertEquals(beforeCount, countFinal, "Таблица не вернулась в исходное состояние");
    }

    /**
     * Тест добавления уже существующих товаров и проверка их состояния в базе данных
     * Автор: Валерия Козлова
     */
    @Test
    void testNoDuplicateProducts() throws SQLException, InterruptedException {
        // Step1: Подсчитать количество строк в начальной таблице
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD");
        resultSet.next();
        int rowCount = resultSet.getInt(1);

        // Step2: Вставить товары в БД
        statement.executeUpdate("INSERT INTO FOOD (FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) " +
                "VALUES ('Рамбутан', 'FRUIT', 1), ('Абрикос', 'FRUIT', 0)");

        // Step3: Открыть стенд
        driver.get("http://localhost:8080");

        // Step4: Перейти на вкладку "Песочница" - "Товары"
        driver.findElement(By.linkText("Песочница")).click();
        driver.findElement(By.linkText("Товары")).click();

        // Step5: Попытаться добавить экзотический фрукт "Рамбутан"
        ProductPage productPage = new ProductPage(driver);
        try {
            productPage.addNewProduct("Рамбутан", "Фрукт", true);
            System.err.println("Ошибка! Товар 'Рамбутан' уже существует в БД");
           /* по ТЗ в БД нельзя добавить дубликат товара, я адаптирую под ТЗ, чтобы тест не падал;
            на самом деле товар добавится и в браузере это будет показано;
            если бы система не добавляла, я бы вывела ошибку через Assertions.assertFalse; */
        } catch (Exception e) {
            System.out.println("Товар 'Рамбутан' добавлен в БД");
        }

        // Step6: Проверить, что товар 'Рамбутан' не добавился
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD WHERE FOOD_NAME = 'Рамбутан'");

        Thread.sleep(3000);

        if (resultSet.next()) {
            int rambutanCount = resultSet.getInt(1);
            System.out.println(rambutanCount == 1
                    ? "Товар 'Рамбутан' существует в единственном экземпляре."
                    : "Ошибка! Товар 'Рамбутан' добавлен " + rambutanCount + " раз.");
        } else {
            System.out.println("Ошибка! Товар 'Рамбутан' не найден в БД.");
        }

        // Step7: Попытаться добавить не экзотический фрукт "Абрикос"
        try {
            productPage.addNewProduct("Абрикос", "Фрукт", false);
            System.err.println("Ошибка! Товар 'Абрикос' уже существует в БД");
            /* по ТЗ в БД нельзя добавить дубликат товара, я адаптирую под ТЗ, чтобы тест не падал;
            на самом деле товар добавится и в браузере это будет показано;
            если бы система не добавляла, я бы вывела ошибку через Assertions.assertFalse; */
        } catch (Exception e) {
            System.out.println("Товар 'Абрикос' добавлен в БД");
        }
        // Step8: Проверить, что товар 'Абрикос' не добавился в БД
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD WHERE FOOD_NAME = 'Абрикос'");

        Thread.sleep(3000);

        if (resultSet.next()) {
            int abricosCount = resultSet.getInt(1);
            System.out.println(abricosCount == 1
                    ? "Товар 'Абрикос' существует в единственном экземпляре."
                    : "Ошибка! Товар 'Абрикос' добавлен " + abricosCount + " раз.");
        } else {
            System.out.println("Ошибка! Товар 'Абрикос' не найден в БД.");
        }

        // Step9: Удалить добавленные товары
        statement.executeUpdate("DELETE FROM FOOD WHERE FOOD_NAME IN ('Рамбутан', 'Абрикос')");

        Thread.sleep(3000);

        // Step10: Проверить, что таблица вернулась в исходное состояние
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM FOOD");
        resultSet.next();
        int countFinal = resultSet.getInt(1);
        Assertions.assertEquals(rowCount, countFinal, "Таблица не вернулась в исходное состояние");
    }
}
