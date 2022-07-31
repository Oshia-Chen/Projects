package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class ExtendedCommandTests {

    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    // Test to spawn a new server and send a simple "look" command
    @Test
    void testLookingAroundStartLocation() {
        String response = server.handleCommand("player A: look !").toLowerCase();
        assertTrue(response.contains("log cabin in the woods"), "Did not see description of room in response to look");
        assertTrue(response.contains("a bottle of magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("razor sharp axe"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("silver coin"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("locked wooden trapdoor in the floor"), "Did not see description of furniture in response to look");
    }

    @Test
    void testCommands() {
        //in the cabin.
        String response = server.handleCommand("player A: get axe and coin .").toLowerCase();
        assertTrue(response.equalsIgnoreCase("you can only get one artefact at a time."), "Cannot pick up this item.");
        String response1 = server.handleCommand("player A: get a coin ~").toLowerCase();
        assertTrue(response1.contains("coin"), "Cannot pick up this item.");
        String response2 = server.handleCommand("player A: get axe").toLowerCase();
        assertTrue(response2.contains("axe"), "Cannot pick up this item.");
        String response3 = server.handleCommand("player A: inv").toLowerCase();
        assertTrue(response3.contains("axe"), "Did not see the artefact in the inventory.");
        assertTrue(response3.contains("coin"), "Did not see the artefact in the inventory.");

        // in the forest.
        server.handleCommand("player B: goto forest");
        String response4 = server.handleCommand("player A: goto forest ");
        assertTrue(response4.contains("deep dark forest"), "Did not see description of room in response to goto");
        assertTrue(response4.contains("rusty old key"), "Did not see description of room in response to goto");
        assertTrue(response4.contains("tall pine tree"), "Did not see description of room in response to goto");

        String response5 = server.handleCommand("player A: cutdown tree with axe \n").toLowerCase();
        assertTrue(response5.contains("you cut down the tree with the axe"), "Cannot do this action.");

        String response6 = server.handleCommand("player A: LOOK").toLowerCase();
        assertTrue(response6.contains("log"), "Did not see the artefact of room in response to look");
        server.handleCommand("player A: get log");
        String response7 = server.handleCommand("player A: INVENTORY").toLowerCase();
        assertTrue(response7.contains("log"), "Did not see the artefact in the inventory.");
        String response8 = server.handleCommand("player A: get key").toLowerCase();
        assertTrue(response8.contains("key"), "Cannot pick up this item.");
        server.handleCommand("player A: drop log");
        server.handleCommand("player A: INVENTORY");
        server.handleCommand("player A: LOOK");
        server.handleCommand("player A: get a log");

        // back to the cabin.
        String response9 = server.handleCommand("player A: goto cabin").toLowerCase();
        assertTrue(response9.contains("log cabin in the woods"), "Did not see description of room in response to goto");
        String response10 = server.handleCommand("player A: use key unlock the trapdoor").toLowerCase();
        assertTrue(response10.contains("you unlock the door and see steps leading down into a cellar"), "Did not see description in response to open trapdoor.");
        String response11 = server.handleCommand("player A: LOOK").toLowerCase();
        assertTrue(response11.contains("cellar"), "Did not see the path in response to look");

        // goto the cellar.
        String response12 = server.handleCommand("player A: goto cellar").toLowerCase();
        assertTrue(response12.contains("dusty cellar"), "Did not see description of room in response to goto.");
        assertTrue(response12.contains("angry looking elf"), "Did not see description of character in response to goto.");
        String response13 = server.handleCommand("player A: fight ELF ").toLowerCase();
        assertTrue(response13.contains("you attack the elf, but he fights back and you lose some health"), "Did not see description in response to fight elf.");
        String response14 = server.handleCommand("player A: pay ELF a coin").toLowerCase();
        assertTrue(response14.contains("you pay the elf your silver coin and he produces a shovel"), "Cannot do this action.");
        server.handleCommand("player A: get shovel");
        String response15 = server.handleCommand("player A: inventory").toLowerCase();
        assertTrue(response15.contains("shovel"), "Did not see the artefact in the inventory.");

        // goto the riverbank.
        server.handleCommand("player A: goto cabin");
        server.handleCommand("player A: goto forest");
        String response16 = server.handleCommand("player A: goto riverbank").toLowerCase();
        assertTrue(response16.contains("grassy riverbank"), "Did not see description of room in response to goto.");
        assertTrue(response16.contains("old brass horn"), "Did not see description of artefacts in response to goto.");
        assertTrue(response16.contains("fast flowing river"), "Did not see description of furniture in response to goto.");
        String response17 = server.handleCommand("player A: bridge log").toLowerCase();
        assertTrue(response17.contains("you bridge the river with the log and can now reach the other side"), "Did not see description in response to bridge log.");
        server.handleCommand("player A: get horn");
        String response18 = server.handleCommand("player A: LOOK").toLowerCase();
        assertTrue(response18.contains("clearing"), "Did not see the path in response to look");


        // goto the clearing.
        String response19 = server.handleCommand("player A: goto clearing   ").toLowerCase();
        assertTrue(response19.contains("clearing in the woods"), "Did not see description of room in response to goto.");
        assertTrue(response19.contains("it looks like the soil has been recently disturbed"), "Did not see description of furniture in response to goto.");

        String response20 = server.handleCommand("player A: dig ground with shovel").toLowerCase();
        assertTrue(response20.contains("you dig into the soft ground and unearth a pot of gold !!!"), "Did not see description in response to bridge log.");
        String response21 = server.handleCommand("player A: LOOK").toLowerCase();
        assertTrue(response21.contains("hole"), "Did not see the hole in response to look");
        assertTrue(response21.contains("gold"), "Did not see the gold in response to look");
        // you can not get furniture like hole or river. so the inventory will not contain it.
        server.handleCommand("player A: get hole");
        server.handleCommand("player A: get gold");
        String response22 = server.handleCommand("player A: inventory").toLowerCase();
        //you can not get hole, so inventory will not contain the hole.
        assertFalse(response22.contains("hole"), "Did not see the artefact in the inventory.");
        assertTrue(response22.contains("gold"), "Did not see the artefact in the inventory.");

        //back to riverbank.
        String response23 = server.handleCommand("player A: goto riverbank").toLowerCase();
        assertTrue(response23.contains("grassy riverbank"), "Did not see description of room in response to goto.");
        String response24 = server.handleCommand("player A: blow the horn").toLowerCase();
        assertTrue(response24.contains("you blow the horn and as if by magic, a lumberjack appears !"), "Did not see description in response to blow horn.");
        String response25 = server.handleCommand("player A: LOOK").toLowerCase();
        assertTrue(response25.contains("a burly wood cutter"), "Did not see the character in response to look");

        //back to cabin.
        server.handleCommand("player A: goto forest");
        String response26 = server.handleCommand("player A: goto cabin").toLowerCase();
        assertTrue(response26.contains("log cabin in the woods"), "Did not see description of room in response to goto");

    }

    @Test
    void testInvalidCommands() {
        //in the cabin.
        String response;
        server.handleCommand("player A: get a coin");
        //cannot do two actions at a time.
        response = server.handleCommand("player A: look get axe").toLowerCase();
        assertTrue(response.contains("you can only do one action at a time."), "No action can be executed.");
        server.handleCommand("player A: get axe !");

        //in the forest.
        server.handleCommand("player A: goto forest");
        //cannot do two actions at a time.
        response = server.handleCommand("player A: goto forest and drop coin").toLowerCase();
        assertTrue(response.contains("you can only do one action at a time."), "No action can be executed.");
        //when only one action is valid, that action should be executed.
        response = server.handleCommand("player A: drink potion cutdown tree with axe  and fight elf").toLowerCase();
        assertTrue(response.contains("you cut down the tree with the axe"), "Did not see description of room in response to goto");

        //back to the cabin.
        server.handleCommand("player A: goto cabin");
        server.handleCommand("player A: get potion");

        //goto the riverbank.
        server.handleCommand("player A: goto forest");
        server.handleCommand("player A: goto riverbank");
        server.handleCommand("player A: get horn");
        //when user tries to do two valid action at a time.
        response = server.handleCommand("player A: blow horn and drink potion").toLowerCase();
        assertTrue(response.contains("you can only do one action at a time."), "Should not do more than one valid action at a time.");
        //when user tries to do two valid action and one invalid at a time, the result should be the same.
        response = server.handleCommand("player A: blow horn and drink potion and chop the tree").toLowerCase();
        assertTrue(response.contains("you can only do one action at a time."), "Should not do more than one valid action at a time.");
        // but when user tries to do one valid and one invalid action, the valid action should be executed.
        response = server.handleCommand("player A: drink potion and chop the tree").toLowerCase();
        assertTrue(response.contains("you drink the potion and your health improves"), "Should not do more than one valid action at a time.");
        response = server.handleCommand("player A: see HEALTH").toLowerCase();
        assertTrue(response.contains("3"), "Health level is not correct.");

    }

}