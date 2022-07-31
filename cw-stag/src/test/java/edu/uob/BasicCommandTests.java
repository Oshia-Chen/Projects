package edu.uob;

import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("player A: look").toLowerCase();
    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
//    response = server.handleCommand("player 1:  unlock trapdoor");
//    System.out.println(response);
//    server.handleCommand("player 1:  goto forest");
//    response = server.handleCommand("player 1: look!").toLowerCase();
  }

  @Test
  void testBasicCommands() {
    String response1 = server.handleCommand("player A: goto forest  ").toLowerCase();
    assertTrue(response1.contains("dark forest"), "Did not see description of room in response to goto");
    String response2 = server.handleCommand("player A: LOOK AROUND  ").toLowerCase();
    assertTrue(response2.contains("brass key"), "Did not see description of artifacts in response to look");
    String response3 = server.handleCommand("player A: get a brass key").toLowerCase();
    assertTrue(response3.contains("picked up"), "Cannot pick up this item.");
    assertTrue(response3.contains("key"), "Cannot pick up this item.");
    String response4 = server.handleCommand("player A: inv").toLowerCase();
    assertTrue(response4.contains("key"), "Cannot see this item in inventory.");

    String response5 = server.handleCommand("player B: look").toLowerCase();
    assertTrue(response5.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response5.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response5.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    String response6 = server.handleCommand("player B: goto forest  ").toLowerCase();
    assertTrue(response6.contains("dark forest"), "Did not see description of room in response to goto");
    String response7 = server.handleCommand("\nplayer B: LOOK AROUND\n");
    assertTrue(response7.contains("player A"), "Cannot see any player in this room.");

    String response8 = server.handleCommand("player A: LOOK AROUND");
    assertTrue(response8.contains("player B"), "Cannot see any player in this room.");
    String response9 = server.handleCommand("player A: drop key\n ").toLowerCase();
    assertTrue(response9.contains("dropped"), "Cannot drop this item.");
    assertTrue(response9.contains("key"), "Cannot drop this item.");
    String response10 = server.handleCommand("player A:  look  ").toLowerCase();
    assertTrue(response10.contains("key"), "Cannot see the key you dropped.");
  }

  @Test
  void testCustomActions() {
    String response1 = server.handleCommand("player A: goto forest").toLowerCase();
    assertTrue(response1.contains("dark forest"), "Did not see description of room in response to goto.");
    String response2 = server.handleCommand("player A: get a brass key").toLowerCase();
    assertTrue(response2.contains("key"), "Cannot pick up this item.");
    String response3 = server.handleCommand("player A: goto cabin").toLowerCase();
    assertTrue(response3.contains("empty room"), "Did not see description of room in response to goto.");
    String response4 = server.handleCommand("player A:  open trapdoor with key").toLowerCase();
    assertTrue(response4.contains("you unlock the trapdoor and see steps leading down into a cellar"), "Did not see description in response to open trapdoor.");
    String response5 = server.handleCommand("player A: goto cellar").toLowerCase();
    assertTrue(response5.contains("dusty cellar"), "Did not see description of room in response to goto.");
    String response6 = server.handleCommand("player A: fight ELF ").toLowerCase();
    assertTrue(response6.contains("you attack the elf, but he fights back and you lose some health"), "Did not see description in response to fight elf.");

    server.handleCommand("player A: fight ELF ");
    //let user died and sent him back to the cabin (first location).
    String response7 = server.handleCommand("player A: fight ELF ");
    assertTrue(response7.contains("you died"), "Health level is not correct.");
    //user has restored to the full health level.
    String response8 = server.handleCommand("player A: HEALTH ");
    assertTrue(response8.contains("3"), "Health level didn't restore to the full level.");
    //user was sent back to the first location.
    String response9 = server.handleCommand("player A: look around ");
    assertTrue(response9.contains("empty room"), "Did not see description of room in response to look.");

    String response10 = server.handleCommand("player A: chop tree").toLowerCase();
    assertTrue(response10.equalsIgnoreCase("there's a invalid subject or not all subjects are available to you now."), "Did not see description of room in response to goto.");

    String response11 = server.handleCommand("player A: drink potion").toLowerCase();
    assertTrue(response11.equalsIgnoreCase("you drink the potion and your health improves"), "Did not see description of drink potion.");
    String response12 = server.handleCommand("player A: health").toLowerCase();
    assertTrue(response12.contains("3"), "Health level didn't increase.");
    String response13 = server.handleCommand("player A: look around ");
    // The potion shouldn't appear in this location
    assertFalse(response13.contains("potion"), "The potion shouldn't appear in this location now.");
    String response14 = server.handleCommand("player A: go somewhere ").toLowerCase();
    assertTrue(response14.contains("the action command is invalid"), "This action should be invalid");
  }

}
