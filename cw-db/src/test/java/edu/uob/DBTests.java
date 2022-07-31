package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;

  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) throws IOException {
    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.
    server = new DBServer(dbDir);
  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCommandIsAnError() {
    //多加一些其他句型的句法錯誤
    assertTrue(server.handleCommand("foo").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE markbook").startsWith("[ERROR]"));
    assertEquals("[ERROR] Invalid query.", server.handleCommand("foo"));
    assertEquals("[ERROR] Invalid query.", server.handleCommand("USE markbook"));

  }

  @Test
  void testValidCommandIsAnOk() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand(" USE  markbook ;").startsWith("[OK]"));
    //assertTrue(server.handleCommand("CREATE table marks ( name , mark , pass );").startsWith("[OK]"));
    assertEquals("[OK]\n" + " ", server.handleCommand("CREATE table marks ( name , mark , pass );"));

  }

  @Test
  void testUseQuery() {
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("CREATE TABLE marks;"));
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE DATABASE test;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE test;").startsWith("[OK]"));
    assertEquals("[ERROR] The database is not exist.", server.handleCommand("USE test1;"));

  }

  @Test
  void testCreateQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("CREATE TABLE marks;"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks;").startsWith("[OK]"));
    assertEquals("[ERROR] The database name already exists.", server.handleCommand("CREATE DATABASE markbook;"));
    assertEquals("[ERROR] The table name already exists.", server.handleCommand("CREATE TABLE marks;"));
  }

  @Test
  void testDropQuery() {
    assertEquals("[ERROR] Use database before dropping any database or table.", server.handleCommand("DROP DATABASE markbook;"));
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    //The database name doesn't exist.
    assertEquals("[ERROR] The database name doesn't exist.", server.handleCommand("DROP DATABASE markbook1;"));
    //The table name doesn't exist in database.
    assertEquals("[ERROR] The table name doesn't exist in database.", server.handleCommand("DROP TABLE programs;"));
    assertTrue(server.handleCommand("CREATE TABLE programs;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DROP TABLE programs;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DROP DATABASE markbook;").startsWith("[OK]"));
  }

  @Test
  void testInsertQuery() {
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);"));
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertEquals("[ERROR] The table name doesn't exist in database.", server.handleCommand("INSERT INTO MARK1 VALUES ('Steve', 65, TRUE);"));
    assertEquals("[ERROR] The number of inserting value doesn't equal to the number of table's column.", server.handleCommand("INSERT INTO MARKS VALUES ('Steve', 65, TRUE, 55);"));
    assertEquals("[ERROR] Invalid query.", server.handleCommand("INSERT INTO MARKS VALUES ('Steve', 65, happy);"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Oliver', 65.5, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Sandy', 70, null);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Winnie', 80, '@@');").startsWith("[OK]"));
  }

  @Test
  void testAlterQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("ALTER TABLE marks ADD number;"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertEquals("[ERROR] Cannot drop the id column.", server.handleCommand("ALTER TABLE marks DROP id;"));
    assertTrue(server.handleCommand("ALTER TABLE marks ADD number;").startsWith("[OK]"));
    assertTrue(server.handleCommand("ALTER TABLE marks DROP number;").startsWith("[OK]"));
    assertEquals("[ERROR] Cannot drop the attribute that doesn't exit.", server.handleCommand("ALTER TABLE marks DROP mark1;"));
    assertTrue(server.handleCommand("ALTER TABLE marks DROP mark;").startsWith("[OK]"));
  }

  @Test
  void testSelectQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("SELECT * FROM marks;"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertEquals("[OK]\n" +
            "id\tname\tpass\t\n" +
            "1\tSteve\tTRUE\t\n" +
            "2\tDave\tTRUE\t\n" +
            "3\tBob\tFALSE\t\n" +
            "4\tClive\tFALSE\t\n", server.handleCommand("SELECT pass, id, name FROM marks;"));

    assertEquals("[ERROR] The attribute doesn't exist in the table.", server.handleCommand("SELECT grade FROM marks;"));
    assertEquals("[ERROR] The table name doesn't exist in database.", server.handleCommand("SELECT * FROM program;"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n", server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;"));

    // The attribute doesn't exist.
    assertEquals("[ERROR] The attribute doesn't exist in the table.", server.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (grade > 35);"));
    assertEquals("[ERROR] The attribute doesn't exist in the table.", server.handleCommand("SELECT * FROM marks WHERE (number == 12) AND (mark > 35);"));
    // Invalid operator.
    assertEquals("[ERROR] Invalid query.", server.handleCommand("SELECT * FROM marks WHERE (pass = TRUE) AND (mark > 35);"));
    assertEquals("[ERROR] Invalid query.", server.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (mark * 35);"));

    // Valid operator.
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n", server.handleCommand("SELECT * FROM marks WHERE (pass == TRUE) AND (mark > 35);"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n", server.handleCommand("SELECT * FROM marks WHERE (pass != FALSE) AND (mark >= 55);"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE (name LIKE 've') AND (mark <= 55);"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE (name LIKE 've') OR (mark < 50);"));

    // Invalid Boolean or NULL comparison.
    assertEquals("[ERROR] NULL or Boolean cannot be compared by size.", server.handleCommand("SELECT * FROM marks WHERE (pass > TRUE) AND (mark > 35);"));
    assertEquals("[ERROR] NULL or Boolean cannot be compared by size.", server.handleCommand("SELECT * FROM marks WHERE (pass == TRUE) AND (mark >= NULL);"));

    //Float number comparison.
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n", server.handleCommand("SELECT * FROM marks WHERE (pass == TRUE) OR (mark > 35.66);"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("SELECT * FROM marks WHERE (pass != NULL) AND (mark > 65.567);"));

    //String comparison.
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE name > 'Bob' ;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE name <= 'Clive' ;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n", server.handleCommand("SELECT * FROM marks WHERE name >= 'Steve' ;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE name < 'Steve' ;"));

    // Select attribute or attributes.
    assertEquals("[OK]\n" +
            "name\t\n" +
            "Dave\t\n" +
            "Bob\t\n" +
            "Clive\t\n", server.handleCommand("SELECT name FROM marks WHERE name < 'Steve';"));

    assertEquals("[OK]\n" +
            "id\tname\t\n" +
            "2\tDave\t\n" +
            "3\tBob\t\n" +
            "4\tClive\t\n", server.handleCommand("SELECT name, id FROM marks WHERE name < 'Steve';"));

    assertEquals("[OK]\n" +
            "id\tname\tpass\t\n" +
            "2\tDave\tTRUE\t\n" +
            "3\tBob\tFALSE\t\n" +
            "4\tClive\tFALSE\t\n", server.handleCommand("SELECT id, name, pass FROM marks WHERE name < 'Steve';"));


    // AND / OR testing.
    assertEquals("[OK]\n" +
            " ", server.handleCommand("select * from  marks where ( ( pass == FALSE )AND(mark > 35) )AND( ( name == 'Clive' ) AND ( id >1 ) ) ;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks WHERE ((pass == FALSE) AND (mark > 35) )OR (name == 'Clive');"));

    assertEquals("[ERROR] Invalid query.", server.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35) )OR (name == 'Clive');"));
  }

  @Test
  void testUpdateQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t38\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 70 WHERE mark <= 55;"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t70\tTRUE\t\n" +
            "3\tBob\t70\tFALSE\t\n" +
            "4\tClive\t70\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 70 WHERE mark >= 65;"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t70\tTRUE\t\n" +
            "2\tDave\t70\tTRUE\t\n" +
            "3\tBob\t70\tFALSE\t\n" +
            "4\tClive\t70\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 60 WHERE mark == 70;"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t60\tTRUE\t\n" +
            "2\tDave\t60\tTRUE\t\n" +
            "3\tBob\t60\tFALSE\t\n" +
            "4\tClive\t60\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Bob';"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t60\tTRUE\t\n" +
            "2\tDave\t60\tTRUE\t\n" +
            "3\tBob\t38\tFALSE\t\n" +
            "4\tClive\t60\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET pass = TRUE WHERE pass == FALSE;"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t60\tTRUE\t\n" +
            "2\tDave\t60\tTRUE\t\n" +
            "3\tBob\t38\tTRUE\t\n" +
            "4\tClive\t60\tTRUE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 40 WHERE (pass == TRUE) and( name == 'Clive');"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t60\tTRUE\t\n" +
            "2\tDave\t60\tTRUE\t\n" +
            "3\tBob\t38\tTRUE\t\n" +
            "4\tClive\t40\tTRUE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("UPDATE marks SET mark = 60 WHERE (mark == 55) OR ( name == 'Bob');"));
    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t60\tTRUE\t\n" +
            "2\tDave\t60\tTRUE\t\n" +
            "3\tBob\t60\tTRUE\t\n" +
            "4\tClive\t40\tTRUE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[ERROR] Cannot set the id column.", server.handleCommand("UPDATE marks SET id = 10 WHERE mark > 33 ;"));
    assertEquals("[ERROR] Different type cannot be compared by size.", server.handleCommand("UPDATE marks SET mark = 100 WHERE pass > 33 ;"));
    assertEquals("[ERROR] NULL or Boolean cannot be compared by size.", server.handleCommand("UPDATE marks SET mark = 100 WHERE pass > null ;"));
  }

  @Test
  void testDeleteQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("DELETE FROM marks WHERE (name == 'Dave') OR (name == 'Bob');"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertEquals("[OK]\n" +
            " ", server.handleCommand("DELETE FROM marks WHERE id == 5;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[ERROR] Different type cannot be compared by size.", server.handleCommand("DELETE FROM marks WHERE name <= 30;"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("DELETE FROM marks WHERE (name == 'Dave') OR (name == 'Bob');"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "4\tClive\t20\tFALSE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("DELETE FROM marks WHERE (mark <= 30) AND (name == 'Clive');"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n", server.handleCommand("SELECT * FROM marks;"));

    assertEquals("[OK]\n" +
            " ", server.handleCommand("DELETE FROM marks WHERE name == 'Steve';"));

    assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n", server.handleCommand("SELECT * FROM marks;"));
  }

  @Test
  void testJoinQuery() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertEquals("[ERROR] Use database before using any table.", server.handleCommand("DELETE FROM marks WHERE (name == 'Dave') OR (name == 'Bob');"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));

    assertTrue(server.handleCommand("CREATE TABLE coursework (task, grade, mark);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ('OXO', 3, 20);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ('DB', 1, 35);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ('OXO', 4, 65);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ('STAG', 2, 55);").startsWith("[OK]"));

    assertEquals("[OK]\n" +
            "id\ttask\tmark\tname\tmark\tpass\t\n" +
            "1\tOXO\t20\tBob\t35\tFALSE\t\n" +
            "2\tDB\t35\tSteve\t65\tTRUE\t\n" +
            "3\tOXO\t65\tClive\t20\tFALSE\t\n" +
            "4\tSTAG\t55\tDave\t55\tTRUE\t\n", server.handleCommand("JOIN coursework AND marks ON grade AND id;"));

    assertEquals("[OK]\n" +
            "id\ttask\tgrade\tname\tpass\t\n" +
            "1\tOXO\t3\tClive\tFALSE\t\n" +
            "2\tDB\t1\tBob\tFALSE\t\n" +
            "3\tOXO\t4\tSteve\tTRUE\t\n" +
            "4\tSTAG\t2\tDave\tTRUE\t\n", server.handleCommand("JOIN coursework AND marks ON mark AND mark;"));

  }
  

  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific usecase (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)

}
