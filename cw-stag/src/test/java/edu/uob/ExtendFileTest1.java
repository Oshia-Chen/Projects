package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ExtendedFileTest {

    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    @Test
    void ExtendedActionTest() {
        String response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("log cabin "));
        assertTrue(response1.contains(" magic potion"));
        assertTrue(response1.contains("axe"));
        assertTrue(response1.contains("coin"));
        assertTrue(response1.contains("locked wooden trapdoor "));

        response1 = server.handleCommand("annie: get coin and axe").toLowerCase();
        assertTrue(response1.contains("you can only get one artefact at a time."));

        response1 = server.handleCommand("annie: get coin and coke").toLowerCase();
        assertTrue(response1.contains("picked up a coin"));

        response1 = server.handleCommand("annie: get  axe").toLowerCase();
        response1 = server.handleCommand("annie: cut tree").toLowerCase();
        //subject does not match
        assertTrue(response1.contains("there's a invalid subject or not all subjects are available to you now."));
        response1 = server.handleCommand("annie: goto fore").toLowerCase();
        assertTrue(response1.contains("your current location doesn't have a path to the destination you want."));
        //only one destination valid
        response1 = server.handleCommand("annie: goto forest and cabin").toLowerCase();
        assertTrue(response1.contains("deep dark forest"));
        assertTrue(response1.contains("key"));
        assertTrue(response1.contains("tree"));
        assertFalse(response1.contains("log"));

        response1 = server.handleCommand("annie: get key").toLowerCase();
        response1 = server.handleCommand("annie: inv").toLowerCase();
        assertTrue(response1.contains("key"));
        assertTrue(response1.contains("coin"));
        assertTrue(response1.contains("axe"));
        response1 = server.handleCommand("annie: cut cutdown tree").toLowerCase();
        assertTrue(response1.contains("you can only do one action at a time."));

        response1 = server.handleCommand("annie: cut tree with axe tree").toLowerCase();
        assertTrue(response1.contains("cut down the tree with the axe"));

        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("log"));
        assertFalse(response1.contains("tree"));

        response1 = server.handleCommand("annie: get log").toLowerCase();
        response1 = server.handleCommand("annie: inventory").toLowerCase();
        assertTrue(response1.contains("log"));
        assertTrue(response1.contains("axe"));

        response1 = server.handleCommand("annie: river bridge and drink potion").toLowerCase();
        assertTrue(response1.contains("there's a invalid subject or not all subjects are available to you now."));

        response1 = server.handleCommand("annie: goto riverbank cabin").toLowerCase();
        assertTrue(response1.contains("you can only goto one location at a time."));

        response1 = server.handleCommand("annie: goto riverbank ").toLowerCase();
        assertTrue(response1.contains("grassy"));
        assertTrue(response1.contains("river"));
        assertTrue(response1.contains("horn"));
        assertFalse(response1.contains("clearing"));

        response1 = server.handleCommand("annie: river bridge").toLowerCase();
        response1 = server.handleCommand("annie: look").toLowerCase();

        assertTrue(response1.contains("clearing"));

        response1 = server.handleCommand("annie: inventory").toLowerCase();
        assertFalse(response1.contains("log"));



        response1 = server.handleCommand("annie: drop key and a big axe").toLowerCase();
        assertTrue(response1.contains("you can only drop one artefact at a time."));

        response1 = server.handleCommand("annie: drop  a big axe").toLowerCase();
        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("clearing"));
        assertTrue(response1.contains("axe"));
        assertTrue(response1.contains("horn"));
        //lumberjack can appear multiple times
        response1 = server.handleCommand("annie: blow horn").toLowerCase();
        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("cutter"));

        response1 = server.handleCommand("annie: get horn").toLowerCase();
        response1 = server.handleCommand("annie: goto forest").toLowerCase();
        response1 = server.handleCommand("annie: blow horn").toLowerCase();
        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("cutter"));
        //lumberjack disappear in the riverbank
        response1 = server.handleCommand("annie: goto riverbank ").toLowerCase();
        assertFalse(response1.contains("cutter"));

        response1 = server.handleCommand("annie: goto forest").toLowerCase();
        response1 = server.handleCommand("annie: goto cabin").toLowerCase();
        response1 = server.handleCommand("annie: get potion").toLowerCase();

        response1 = server.handleCommand("annie: door open horn blow").toLowerCase();
        assertTrue(response1.contains("a lumberjack appears"));

        response1 = server.handleCommand("annie: open trapdoor").toLowerCase();
        assertTrue(response1.contains("into a cellar"));

        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("cellar"));
        assertTrue(response1.contains("forest"));
        assertTrue(response1.contains("cutter"));

        response1 = server.handleCommand("annie: goto cellar").toLowerCase();
        assertTrue(response1.contains("elf"));

        response1 = server.handleCommand("annie: hit attack elf").toLowerCase();
        assertTrue(response1.contains("you can only do one action at a time."));

        response1 = server.handleCommand("annie: attack elf").toLowerCase();
        response1 = server.handleCommand("annie: health").toLowerCase();
        assertTrue(response1.contains("2"));

        response1 = server.handleCommand("annie: coin pay elf").toLowerCase();
        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("shovel"));
        response1 = server.handleCommand("annie: get shovel").toLowerCase();
        response1 = server.handleCommand("annie: goto cabin").toLowerCase();
        response1 = server.handleCommand("annie: goto forest").toLowerCase();
        response1 = server.handleCommand("annie: goto riverbank").toLowerCase();
        response1 = server.handleCommand("annie: goto clearing").toLowerCase();

        response1 = server.handleCommand("annie: shovel ground dig").toLowerCase();

        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("hole"));
        assertTrue(response1.contains("gold"));

        response1 = server.handleCommand("annie: drop horn and coke").toLowerCase();

        //multiple player
        response1 = server.handleCommand("anita: goto forest").toLowerCase();
        response1 = server.handleCommand("anita: goto riverbank").toLowerCase();
        response1 = server.handleCommand("anita: goto clearing").toLowerCase();
        assertTrue(response1.contains("annie"));
        assertTrue(response1.contains("horn"));

        response1 = server.handleCommand("anita: get horn and gold").toLowerCase();
        assertTrue(response1.contains("you can only get one artefact at a time."));
        response1 = server.handleCommand("anita: get horn ").toLowerCase();
        response1 = server.handleCommand("anita: inv ").toLowerCase();
        assertTrue(response1.contains("horn"));
        response1 = server.handleCommand("anita: blow horn").toLowerCase();
        response1 = server.handleCommand("anita: look").toLowerCase();
        assertTrue(response1.contains("cutter"));
        assertFalse(response1.contains("horn"));
        response1 = server.handleCommand("annie: look").toLowerCase();
        assertTrue(response1.contains("anita"));
        assertTrue(response1.contains("cutter"));
    }
}
