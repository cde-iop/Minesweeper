package dennis.callum.minesweeper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SquareTest {
    private WebDriver driver;
    private WebElement testElement;
    private int[] dimensions = new int[2];
    private GameState game;
    private WebElement gameEle;

    @Before
    public void setup(){
        driver = new FirefoxDriver();
        driver.get("http://minesweeperonline.com/#intermediate-150-night");
        dimensions[0] = 16;
        dimensions[1] = 16;
        game = new GameState(dimensions, driver);
        gameEle = game.getGame();
    }

    @After
    public void tearDown(){
        driver.quit();
    }

    @Test
    public void coordinateCenterSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("10_10"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{10, 10};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }
    @Test
    public void coordinateYEdgeSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("1_10"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{1, 10};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }
    @Test
    public void coordinateXEdgeSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("10_1"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{10, 1};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }

    @Test
    public void coordinateTopLeftCornerSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("1_1"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{1, 1};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }
    @Test
    public void coordinateTopRightCornerSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("1_16"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{1, 16};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }
    @Test
    public void coordinateBottomRightCornerSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("16_16"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{16, 16};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }
    @Test
    public void coordinateLeftRightCornerSquareDimsOver10Test(){
        testElement = gameEle.findElement(By.id("16_1"));
        Square test = new Square(testElement, dimensions);
        int[] expectedResult = new int[]{16, 1};
        Assert.assertArrayEquals(expectedResult, test.getCoordinates());
    }

}