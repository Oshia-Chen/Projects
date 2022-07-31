package edu.uob;

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
final class BasicCommandTests2 {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
//  @Test
//  void testLookingAroundStartLocation() {
//
//    String response = server.handleCommand("Simon gpkpkccccccccc     fffft :look").toLowerCase();
//    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
//    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
//    //assertTrue(response.contains("wooden door"), "Did not see description of furniture in response to look");
//    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
//   // String response = server.handleCommand("Sion gpkpkccccccccc     fffft :look").toLowerCase();
//    String responses = server.handleCommand("Simon:    look").toLowerCase();
//    assertTrue(responses.contains("empty room"), "Did not see description of room in response to look");
//    assertTrue(responses.contains("magic potion"), "Did not see description of artifacts in response to look");
//    //assertTrue(response.contains("wooden door"), "Did not see description of furniture in response to look");
//    assertTrue(responses.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
//
//    String response1 = server.handleCommand("Simon        :    look").toLowerCase();
//    assertTrue(response1.contains("empty room"), "Did not see description of room in response to look");
//    assertTrue(response1.contains("magic potion"), "Did not see description of artifacts in response to look");
//    //assertTrue(response.contains("wooden door"), "Did not see description of furniture in response to look");
//    assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
//  }
//
//
//  @Test
//  void testBasicCommands2(){
//    String re1 = server.handleCommand("player A:  look ").toLowerCase();
//    assertTrue(re1.contains("forest"), "Did not get right path of room in response with look.");
//    assertTrue(re1.contains("magic potion"), "Did not get right description of start point in response.");
//    assertTrue(re1.contains("wooden trapdoor"), "Did not get right description of start point in response.");
//    assertTrue(re1.contains("an empty room"), "Did not get right description of start point in response.");
//    assertFalse(re1.contains("cellar"), "Path of cellar is not open yet");
//    assertFalse(re1.contains("cabin"), "It should show the description");
//
//    re1 = server.handleCommand("player A: goto cellar").toLowerCase();
//    assertTrue(re1.contains("your current location doesn't have a path to the destination you want."), "Did not get right description in response with invalid path .");
//
//    re1 = server.handleCommand("player A: get key").toLowerCase();
//    assertTrue(re1.contains("the artefact doesn't exist at this location."), "Did not get right description in response with invalid artefacts.");
//
//    re1 = server.handleCommand("player A: get a Magic potion").toLowerCase();
//    assertTrue(re1.contains("picked up a potion"), "Successfully picked up the item");
//
//    re1 = server.handleCommand("player A: get potion").toLowerCase();
//    assertTrue(re1.contains("the artefact doesn't exist at this location."), "The item has been picked");
//
//    re1 = server.handleCommand("player A: get trapdoor").toLowerCase();
//    assertTrue(re1.contains("the artefact doesn't exist at this location."), "Furniture can not be picked");
//
//    re1 = server.handleCommand("player A: look").toLowerCase();
//    assertFalse(re1.contains("magic potion"), "potion has been picked ");
//
//    re1 = server.handleCommand("player A: goto forest").toLowerCase();
//    assertFalse(re1.contains("magic potion"), "Did not get right description of forest in response.");
//    assertTrue(re1.contains("brass key"), "Key is in the forest");
//    assertTrue(re1.contains("cabin"), "It is the path of the location");
//
//    re1 = server.handleCommand("player A: drop potion").toLowerCase();
//    re1 = server.handleCommand("player A: get key and potion").toLowerCase();
//    assertTrue(re1.contains("you can only get one artefact at a time."));
//    re1 = server.handleCommand("player A: get key ").toLowerCase();
//    assertTrue(re1.contains("key"), "The item is already picked up by player");
//    re1 = server.handleCommand("player A: get potion ").toLowerCase();
//    re1 = server.handleCommand("player A: drop potion and key ").toLowerCase();
//    assertTrue(re1.contains("you can only drop one artefact at a time."));
//
//    re1 = server.handleCommand("player A: drop jgo ").toLowerCase();
//    assertTrue(re1.contains("the artefact doesn't exist in your inventory."));
//
//
//    re1 = server.handleCommand("player A: look").toLowerCase();
//    assertFalse(re1.contains("brass key"), "The item is already picked up by player");
//
//    re1 = server.handleCommand("player A: inv").toLowerCase();
//    assertTrue(re1.contains("key"), "The item is already picked up by player");
//    assertTrue(re1.contains("potion"), "The item is already picked up by player");
//
//    re1 = server.handleCommand("player A: drop potion").toLowerCase();
//    assertTrue(re1.contains("potion"), "The item is already placed to current location by player");
//
//    re1 = server.handleCommand("player A: look").toLowerCase();
//    assertTrue(re1.contains("magic potion"), "potion has been placed ");
//    //empty command
//    re1 = server.handleCommand("player A: ").toLowerCase();
//    assertTrue(re1.contains("check your command again, and it should only have one ':'."), "potion has been placed ");
//
//  }

  @Test
  void testBasicAction(){
    server.handleCommand("player A: get potion");
    server.handleCommand("player A: goto forest");
    server.handleCommand("player A: get key");
    String response1 = server.handleCommand("player A: inv").toLowerCase();
    server.handleCommand("player A: goto cabin");
    response1 = server.handleCommand("player A: drink key unlock potion yayaya").toLowerCase();
    System.out.println(response1);
    assertTrue(response1.contains("you can only do one action at a time."), "Two valid action is invalid command");
    response1 = server.handleCommand("player A: look").toLowerCase();
    assertFalse(response1.contains("cellar"), "invalid path");
    response1 = server.handleCommand("player A: key unlock").toLowerCase();
    assertTrue(response1.contains("unlock the trapdoor and see steps leading down into a cellar"), "valid command");
    response1 = server.handleCommand("player A: look").toLowerCase();
    assertTrue(response1.contains("cellar"), "valid path");
    response1 = server.handleCommand("player A: goto cellar").toLowerCase();
    assertTrue(response1.contains("elf"), "character");
    assertTrue(response1.contains("cabin"), "valid path");
    response1 = server.handleCommand("player A: attack elf").toLowerCase();
    assertTrue(response1.contains("attack the elf, but he fights back and you lose some health"), "valid command");
    response1 = server.handleCommand("player A: health").toLowerCase();
    assertTrue(response1.contains("2"), "valid command");
    response1 = server.handleCommand("player A: drink a big potion key unlock").toLowerCase();
    assertTrue(response1.contains("drink the potion and your health improves"), "only one action is valid ");
    response1 = server.handleCommand("player A: health").toLowerCase();
    assertTrue(response1.contains("3"), "valid command");
    response1 = server.handleCommand("player A: attack  elf").toLowerCase();
    response1 = server.handleCommand("player A: attack  elf").toLowerCase();
    response1 = server.handleCommand("player A: attack  elf").toLowerCase();
    assertTrue(response1.contains("died and lost all of your items, you must return to the start of the game"), "restart");
    response1 = server.handleCommand("player A: look").toLowerCase();
    assertTrue(response1.contains("cellar"), "start point");
    assertTrue(response1.contains("forest"), "start point");
    response1 = server.handleCommand("player A: goto cellar and forest").toLowerCase();
    assertTrue(response1.contains("you can only goto one location at a time."), "invalid command");
    response1 = server.handleCommand("player A: health").toLowerCase();
    assertTrue(response1.contains("3"), "restart should recover the health");
  }

  @Test
  void testMultiplePlayer(){
    String response1 = server.handleCommand("annie: look").toLowerCase();
    response1 = server.handleCommand("anita: look").toLowerCase();
    assertTrue(response1.contains("annie"), "can see other player ");
    response1 = server.handleCommand("annie: look").toLowerCase();
    assertTrue(response1.contains("anita"), "can see other player  ");
    response1 = server.handleCommand("annie: goto forest").toLowerCase();
    response1 = server.handleCommand("annie: get a big key").toLowerCase();
    response1 = server.handleCommand("annie: goto a cabin").toLowerCase();
    response1 = server.handleCommand("annie: get potion").toLowerCase();
    response1 = server.handleCommand("anita: look").toLowerCase();
    assertFalse(response1.contains("potion"), "picked by other player");
    response1 = server.handleCommand("anita: unlock trapdoor").toLowerCase();
    response1 = server.handleCommand("anita: look").toLowerCase();
    assertFalse(response1.contains("cellar"), "no path");
    assertFalse(response1.contains("potion"), "picked by other player");
    response1 = server.handleCommand("annie: unlock trapdoor").toLowerCase();
    response1 = server.handleCommand("annie: goto cellar").toLowerCase();
    response1 = server.handleCommand("anita: goto cellar").toLowerCase();
    response1 = server.handleCommand("anita: look").toLowerCase();
    assertTrue(response1.contains("annie"), "can see other player ");
    response1 = server.handleCommand("annie: hit elf").toLowerCase();
    response1 = server.handleCommand("annie: attack elf").toLowerCase();
    response1 = server.handleCommand("annie: fight elf").toLowerCase();
    response1 = server.handleCommand("annie: health").toLowerCase();
    response1 = server.handleCommand("annie: look").toLowerCase();
    response1 = server.handleCommand("anita: look").toLowerCase();
    assertFalse(response1.contains("annie"), "die would go back to start point");
    assertTrue(response1.contains("potion"), "valid command");
    response1 = server.handleCommand("anita: get potion").toLowerCase();
    response1 = server.handleCommand("anita: inventory").toLowerCase();
    assertTrue(response1.contains("potion"), "valid command");
    response1 = server.handleCommand("anita:     drink a potion  ").toLowerCase();
    response1 = server.handleCommand("anita:     health  ").toLowerCase();
    assertTrue(response1.contains("3"), "valid command");
    response1 = server.handleCommand("anita: chop tree  ").toLowerCase();
    assertTrue(response1.contains("there's a invalid subject or not all subjects are available to you now."), "invalid command");

  }
  // Add more unit tests or integration tests here.

}
