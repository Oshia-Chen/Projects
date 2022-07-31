package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class SetGame {
    private final ArrayList<Location> gameMap;
    private final TreeMap<String, HashSet<GameAction>> actionList;
    private Location firstLocation;


    public SetGame(File entitiesFile, File actionsFile) throws Exception {
        gameMap = new ArrayList<>();
        actionList = new TreeMap<>();
        buildEntities(entitiesFile);
        loadActions(actionsFile);
        //currentPlayer = new Player(commands[0], "A player : "+ commands[0] +" in the game\n", game.getFirstLocation(), game);
    }

    public void buildEntities(File entitiesFile) throws Exception {
        Parser parser = new Parser();
        FileReader reader = null;
        try {
            reader = new FileReader(entitiesFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        // The locations will always be in the first subgraph, the second is paths.
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        if (locations == null || locations.size() == 0) {
            throw new Exception("The locations is empty.");
        }

        int roomNo = 0;
        while (roomNo < locations.size()) {
            Graph Location = locations.get(roomNo);
            Node locationDetails = Location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String Description = locationDetails.getAttribute("description");
            Location location = new Location(locationName, Description);
            gameMap.add(location);
            if (roomNo == 0) {
                firstLocation = location;
            }
            roomNo++;
            for (int i = 0; i < Location.getSubgraphs().size(); i++) {
                Graph Subgraph = Location.getSubgraphs().get(i);
                switch (Subgraph.getId().getId()) {
                    case "artefacts" -> {
                        ArrayList<Artefact> artefactList = setArtefactList(Subgraph);
                        location.setArtefactList(artefactList);
                    }
                    case "furniture" -> {
                        ArrayList<Furniture> furnitureList = setFurnitureList(Subgraph);
                        location.setFurnitureList(furnitureList);
                    }
                    case "characters" -> {
                        ArrayList<Character> characterList = setCharacterList(Subgraph);
                        location.setCharacterList(characterList);
                    }
                }
            }
        }
        setPaths(sections);
    }

    public ArrayList<Artefact> setArtefactList(Graph object){
        ArrayList<Artefact> artefactList = new ArrayList<>();
        for(int i = 0; i < object.getNodes(false).size(); i++){
            //把 artefacts 的內容物抓出來，ex. potion [description="Magic potion"];
            Node artefactDetail = object.getNodes(false).get(i);
            //抓 artefacts 的名字，ex. potion 。
            String artefactName = artefactDetail.getId().getId();
            //抓 artefacts 的 description，ex. Magic potion 。
            String Description = artefactDetail.getAttribute("description");
            Artefact artefact = new Artefact(artefactName, Description);
            artefactList.add(artefact);
        }
       return  artefactList;
    }

    public ArrayList<Furniture> setFurnitureList(Graph object){
        ArrayList<Furniture> furnitureList = new ArrayList<>();
        for(int i = 0; i < object.getNodes(false).size(); i++){
            //把 artefacts 的內容物抓出來，ex. potion [description="Magic potion"];
            Node furnitureDetail = object.getNodes(false).get(i);
            //抓 artefacts 的名字，ex. potion 。
            String furnitureName = furnitureDetail.getId().getId();
            //抓 artefacts 的 description，ex. Magic potion 。
            String Description = furnitureDetail.getAttribute("description");
            Furniture furniture = new Furniture(furnitureName, Description);
            furnitureList.add(furniture);
        }
        return furnitureList;
    }

    public ArrayList<Character> setCharacterList(Graph object){
        ArrayList<Character> characterList = new ArrayList<>();
        for(int i = 0; i < object.getNodes(false).size(); i++){
            //把 artefacts 的內容物抓出來，ex. potion [description="Magic potion"];
            Node characterDetail = object.getNodes(false).get(i);
            //抓 artefacts 的名字，ex. potion 。
            String characterName = characterDetail.getId().getId();
            //抓 artefacts 的 description，ex. Magic potion 。
            String Description = characterDetail.getAttribute("description");
            Character character = new Character(characterName, Description);
            characterList.add(character);
        }
        return characterList;
    }

    public void setPaths(ArrayList<Graph> sections) throws RuntimeException {
        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();
        for (Edge path : paths) {
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            if(fromName.equalsIgnoreCase("storeroom")){
                throw new RuntimeException("Shouldn't add any path to the storeroom.");
            }
            for (Location location : gameMap) {
                if (location.getName().equals(fromName)) {
                    Node toLocation = path.getTarget().getNode();
                    String toName = toLocation.getId().getId();
                    if (toName.equalsIgnoreCase("storeroom")) {
                        throw new RuntimeException("Shouldn't add any path to the storeroom.");
                    }
                    location.addPath(toName);
                    break;
                }
            }
        }
    }


    public void loadActions(File actionsFile) throws Exception {
        //抓出 trigger keyword 當作 key 後，後面的內容都包成 object。
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        // Get the action (only the odd items are actually actions - 1, 3, 5 etc.)
        for(int i = 1; i < actions.getLength(); i+=2){
            Element action = (Element)actions.item(i);
            Element triggers = (Element)action.getElementsByTagName("triggers").item(0);
            ArrayList<String> triggerPhraseList = new ArrayList<>();
            // Get all the trigger phrases
            for(int j = 0; j < triggers.getElementsByTagName("keyword").getLength(); j++){
                String triggerPhrase = triggers.getElementsByTagName("keyword").item(j).getTextContent();
                triggerPhraseList.add(triggerPhrase);
            }
            GameAction object = new GameAction();
            entitiesToObject("subjects", action, object);
            entitiesToObject("consumed", action, object);
            entitiesToObject("produced", action, object);
            String narration = action.getElementsByTagName("narration").item(0).getTextContent();
            object.addNarration(narration);
            //System.out.println(object.getAllSubjects());
            int index = 0;
            while(index < triggerPhraseList.size()){
                String key = triggerPhraseList.get(index);
                HashSet<GameAction> actionHashSet = new HashSet<>();
                if(actionList.containsKey(key)){
                    actionHashSet = actionList.get(key);
                }
                actionHashSet.add(object);
                actionList.put(key, actionHashSet);
                //System.out.println(triggerPhraseList.get(index));
                index++;
            }
        }
    }

    public void entitiesToObject(String entityName, Element action, GameAction object){
        Element objectList = (Element)action.getElementsByTagName(entityName).item(0);
        for(int i = 0; i < objectList.getElementsByTagName("entity").getLength(); i++){
            String Phrase = objectList.getElementsByTagName("entity").item(i).getTextContent();
            switch (entityName) {
                case "subjects" -> object.addSubjects(Phrase);
                case "consumed" -> object.addConsumed(Phrase);
                case "produced" -> object.addProduced(Phrase);
            }
        }
    }


    public TreeMap<String, HashSet<GameAction>> getActionList(){
        return actionList;
    }

    public ArrayList<Location> getGameMap(){
        return gameMap;
    }

    public Location getStoreroom(){
        for (Location location : gameMap) {
            if (location.getName().equalsIgnoreCase("storeroom")) {
                return location;
            }
        }
        return null;
    }

    public Location getFirstLocation(){
        return firstLocation;
    }

}


