package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class ControllerTests {
  OXOModel model;
  OXOController controller;

  // create your standard 3*3 OXO board (where three of the same symbol in a line wins) with the X
  // and O player
  private static OXOModel createStandardModel() {
    OXOModel model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    return model;
  }

  // we make a new board for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
    model = createStandardModel();
    controller = new OXOController(model);
  }

  // here's a basic test for the `controller.handleIncomingCommand` method
  @Test
  void testHandleIncomingCommand() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");

    // A move has been made for A1 (i.e. the [0,0] cell on the board), let's see if that cell is
    // indeed owned by the player
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0));
  }

  // here's a complete game where we find out if someone won
  @Test
  void testBasicWinWithA1A2A3() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");

    // OK, so A1, A2, A3 is a win and that last A3 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
        firstMovingPlayer,
        model.getWinner(),
        "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagonalsWin() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("A1");
    controller.handleIncomingCommand("B1");
    controller.handleIncomingCommand("B2");
    controller.handleIncomingCommand("B3");
    controller.handleIncomingCommand("C3");
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDrawn() throws OXOMoveException {
    controller.addColumn();
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("a4");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b4");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("c4");
    controller.handleIncomingCommand("c3");
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testChangeBoardToDrawn() throws OXOMoveException {
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.removeRow();
    controller.removeColumn();
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testRepeatClaim() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");
    assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("b1"));
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(1, 0));
  }


  @Test
  void testInvalidIdentifierLength() {
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand(""));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("a"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("2"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("110"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("a11"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("234567"));
  }

  @Test
  void testInvalidClaim() {
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("31"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("#1"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("!2"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("aa"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("b*"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("b="));
  }

  @Test
  void testOutsideCellRangeClaim() {
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("z1"));
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("d2"));
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a5"));
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("c4"));
    controller.addRow();
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("d4"));
    controller.addRow();
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("e4"));
  }

  @Test
  void testChangeRowCol() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addRow();
    controller.addRow();
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns());
    assertEquals(5, model.getNumberOfRows());
    controller.handleIncomingCommand("e4");
    controller.handleIncomingCommand("d3");
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(4, 3));
    controller.removeRow();
    controller.removeColumn();
    assertEquals(4, model.getNumberOfColumns());
    assertEquals(5, model.getNumberOfRows());
  }

  @Test
  void testRemoveRowColToOne() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.removeRow();
    controller.removeRow();
    controller.removeColumn();
    controller.removeColumn();
    assertEquals(1, model.getNumberOfRows());
    assertEquals(1, model.getNumberOfColumns());
    controller.removeRow();
    controller.removeColumn();
    assertEquals(1, model.getNumberOfRows());
    assertEquals(1, model.getNumberOfColumns());
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a2"));
    controller.handleIncomingCommand("a1");
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testAddRowOrColOutOfRange(){
    for(int i = 0; i < 8; i++){
      controller.addRow();
      controller.addColumn();
    }
    assertEquals(9, model.getNumberOfRows());
    assertEquals(9, model.getNumberOfColumns());
  }

  @Test
  void ChangeRowOrColAfterWin() throws OXOMoveException {
    testBasicWinWithA1A2A3();
    controller.addRow();
    controller.addColumn();
    assertEquals(3, model.getNumberOfRows());
    assertEquals(3, model.getNumberOfColumns());
    controller.removeRow();
    controller.removeColumn();
    assertEquals(3, model.getNumberOfRows());
    assertEquals(3, model.getNumberOfColumns());
  }

  @Test
  void ChangeRowOrColAfterDrawn() throws OXOMoveException {
    testDrawn();
    controller.addRow();
    controller.addColumn();
    assertEquals(3, model.getNumberOfRows());
    assertEquals(4, model.getNumberOfColumns());
    controller.removeRow();
    controller.removeColumn();
    assertEquals(3, model.getNumberOfRows());
    assertEquals(4, model.getNumberOfColumns());
  }

  @Test
  void testMinimumWinThreshold() {
    for (int i = 0; i < 15; i++) {
      controller.decreaseWinThreshold();
    }
    assertEquals(0, model.getWinThreshold());
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testChangeWinThreshold() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold());
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a2");
    assertEquals(firstMovingPlayer, model.getWinner());
  }

  @Test
  void testChangeThresholdEdgeCase() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    assertNull(model.getWinner());
    controller.decreaseWinThreshold();
    assertEquals(3, model.getWinThreshold());
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testDrawnWithZeroThreshold() throws OXOMoveException {
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();
    assertEquals(0, model.getWinThreshold());
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testClaimAfterWin() throws OXOMoveException {
    testBasicWinWithA1A2A3();
    controller.handleIncomingCommand("b3");
    assertEquals('\0', model.getCellOwner(1,2).getPlayingLetter());
  }

  @Test
  void testAddPlayer() throws OXOMoveException{
    model.addPlayer(new OXOPlayer('@'));
    assertEquals(3, model.getNumberOfPlayers());
    assertEquals('X', model.getPlayerByNumber(0).getPlayingLetter());
    assertEquals('O', model.getPlayerByNumber(1).getPlayingLetter());
    assertEquals('@', model.getPlayerByNumber(2).getPlayingLetter());
    model.addPlayer(new OXOPlayer('$'));
    assertEquals(4, model.getNumberOfPlayers());
    assertEquals('$', model.getPlayerByNumber(3).getPlayingLetter());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b2");
    assertEquals('@', model.getCells().get(0).get(2).getPlayingLetter());
    assertEquals('$', model.getCells().get(1).get(1).getPlayingLetter());
  }

}
