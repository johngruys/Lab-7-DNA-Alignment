/**
 * To test with JUnit, add JUnit to your project. To do this, go to
 * Project->Properties. Select "Java Build Path". Select the "Libraries"
 * tab and "Add Library". Select JUnit, then JUnit 4.
 */

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
  @Test
  public void test() {
    defaultJudgeTest();
    customJudgeTest();
    empties();
    singletons();

    smallTest();
    emptyTest();
    stringLengthTest();
    markerTest();
    randomTest();
  }

  // My tests

  @Test
  public void emptyTest() {
    SequenceAligner sa;
    Result result;
    sa = new SequenceAligner("", "");
    result = sa.getResult(0, 0);
    assertNotNull(result);
    assertEquals(0, result.getScore());
  }

  @Test
  public void smallTest() {
    SequenceAligner sa = new SequenceAligner("ACACCC", "GCCTCGA");
    Result result = sa.getResult(6, 7);
    assertNotNull(result);
    assertEquals(-1, result.getScore());
    assertEquals(Direction.DIAGONAL, result.getParent());

    Result result2 = sa.getResult(6, 6);
    assertNotNull(result2);
    assertEquals(0, result2.getScore());

    Result result3 = sa.getResult(5, 4);
    assertNotNull(result3);
    assertEquals(-1, result3.getScore());
  }

  @Test
  public void stringLengthTest() {
    SequenceAligner sa = new SequenceAligner("ACACCC", "GCCTCGA");
    String x = sa.getAlignedX();
    String y = sa.getAlignedY();
    assertEquals(x.length(), y.length());
  }

  @Test
  public void markerTest() {
    SequenceAligner sa = new SequenceAligner("ACACCC", "GCCTCGA");

    Result result = sa.getResult(6, 7);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(5, 6);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(5, 5);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(4, 4);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(4, 3);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(3, 2);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(2, 2);
    assertNotNull(result);
    assertTrue(result.onPath());

    result = sa.getResult(1, 1);
    assertNotNull(result);
    assertTrue(result.onPath());
  }


  @Test
  public void randomTest() {
    SequenceAligner sa = new SequenceAligner(10);

    // Check string length
    assertTrue(sa.isAligned());
    int lenX = sa.getX().length();
    int lenY = sa.getY().length();


    Result result = sa.getResult(lenX, lenY);
    assertTrue(result.onPath());

    result = sa.getResult(0,0);
    assertTrue(result.onPath());
    assertEquals(0, result.getScore());
    assertEquals(Direction.NONE, result.getParent());

  }





  // Default Tests

  @Test
  public void defaultJudgeTest() {
    Judge judge = new Judge();
    assertEquals(2, judge.score('A',  'A'));
    assertEquals(2, judge.score("A",  "A"));
  }

  @Test
  public void customJudgeTest() {
    Judge judge = new Judge(3, -3, -2);
    assertEquals(3, judge.score('A',  'A'));
    assertEquals(3, judge.score("A",  "A"));
  }

  /**********************************************
   * Testing SequenceAligner.fillCache()
   **********************************************/
  @Test
  public void empties() {
    SequenceAligner sa;
    Result result;
    sa = new SequenceAligner("", "");
    result = sa.getResult(0, 0);
    assertNotNull(result);
    assertEquals(0, result.getScore());
    assertEquals(Direction.NONE, result.getParent());
  }

  @Test
  public void singletons() {
    SequenceAligner sa;
    Result result;
    sa = new SequenceAligner("A", "A");
    result = sa.getResult(0, 0);
    assertNotNull(result);
    assertEquals(0, result.getScore());
    assertEquals(Direction.NONE, result.getParent());
    result = sa.getResult(0, 1);
    assertNotNull(result);
    assertEquals(-1, result.getScore());
    assertEquals(Direction.LEFT, result.getParent());
    result = sa.getResult(1, 0);
    assertNotNull(result);
    assertEquals(-1, result.getScore());
    assertEquals(Direction.UP, result.getParent());
    result = sa.getResult(1, 1);
    assertNotNull(result);
    assertEquals(2, result.getScore());
    assertEquals(Direction.DIAGONAL, result.getParent());
  }

  /**********************************************
   * Testing SequenceAligner.traceback()
   **********************************************/
  @Test
  public void simpleAlignment() {
    SequenceAligner sa;
    sa = new SequenceAligner("ACGT", "ACGT");
    assertTrue(sa.isAligned());
    assertEquals("ACGT", sa.getAlignedX());
    assertEquals("ACGT", sa.getAlignedY());
  }

}