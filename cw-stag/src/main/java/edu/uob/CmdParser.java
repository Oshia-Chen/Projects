package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class CmdParser {

    private final Player currentPlayer;
    private final SetGame gameState;
    private final ArrayList<String> result;
    private String finalResult;


    public CmdParser(String command, SetGame gameSet, Player player) throws Exception {
        gameState = gameSet;
        currentPlayer = player;
        result = new ArrayList<>();

        String[] commands = command.strip().split(":");
        if(commands.length != 2){
            throw new Exception("Check your command again, and it should only have one ':'.");
        }
        String[] actions = deleteSpaceOfCommand(commands[1]);
        Location currentPlace = currentPlayer.getLocation();
        if(!isBasicCMD(actions, currentPlace)){
            if(!isExtendedCMD(actions, currentPlace)){
                throw new Exception("Invalid command.");
            }
        }
    }


    private boolean isExtendedCMD(String[] action, Location currentPlace) throws Exception {
        //command MUST contain a trigger word/phrase and AT LEAST ONE subject.
        TreeMap<String, HashSet<GameAction>> actionList = gameState.getActionList();
        //check triggerKey in the command.
        boolean isBasicCmd = false;
        ArrayList<String> triggerKey = checkTrigger(action, actionList,  isBasicCmd);
        //use key string to get its GameAction array.
        ArrayList<Integer> indexList = new ArrayList<>();
        GameAction[] validGameActions = new GameAction[0];
        // When there are more than one trigger keys, we need to check whether only one action is valid. (Cannot exceed one)
        for (String s : triggerKey) {
            int indexOfAction;
            HashSet<GameAction> actionHashSet = actionList.get(s);
            GameAction[] gameActions = actionHashSet.toArray(new GameAction[0]);
            //check command has at least a subject and all subjects in action can be used by player.
            //return index of action in gameActions array, to know which gameAction will be used.
            indexOfAction = checkActionValid(action, gameActions, currentPlace);
            if (indexOfAction >= 0) {
                validGameActions = gameActions;
                indexList.add(indexOfAction);
            }
        }
        if(indexList.size() == 0){
            throw new Exception("There's a invalid subject or not all subjects are available to you now.");
        }
        if(indexList.size() > 1){
            throw new Exception("You can only do one action at a time.");
        }
        GameAction gameAction = validGameActions[indexList.get(0)];
        //check produced item's location. (Maybe in the storeRoom / another location / GameMap(item might be a path)
        if(checkProduced(gameAction, currentPlace)){
            if(checkConsumed(gameAction, currentPlace)) {
                //return action's narration to user
                finalResult = gameAction.getNarration();
                checkPlayerDied(currentPlace);
                return true;
            }
        }
        return false;
    }

    public void checkPlayerDied(Location currentPlace) throws Exception {
        if(currentPlayer.getHealth() == 0) {
            for (int i = 0; i < currentPlayer.getInventory().size(); i++) {
                currentPlace.addArtefact(currentPlayer.getInventory().get(i));
            }
            currentPlace.removePlayer(currentPlayer);
            currentPlayer.setLocation(gameState.getGameMap().get(0));
            currentPlayer.cleanInventory();
            currentPlayer.setFullHealth();
            finalResult = finalResult.concat("\n" + "you died and lost all of your items, you must return to the start of the game");
        }
    }

    public boolean checkConsumed(GameAction gameAction, Location currentPlace) throws Exception {
        // all consumed items might be artefacts/furniture/location/health in other place.
        boolean isProduced = false;
        ArrayList<String> consumedList = gameAction.getAllConsumed();
        for (String itemName : consumedList) {
            if (!IsHealthAndDo(itemName, isProduced)) {
                if (!checkItemLocation(itemName, currentPlace, isProduced)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkProduced(GameAction gameAction, Location currentPlace) throws Exception {
        // all produced items might be artefacts/furniture/path/health in other place.
        boolean isProduced = true;
        ArrayList<String> producedList = gameAction.getAllProduced();
        for (String itemName : producedList) {
            if (!IsHealthAndDo(itemName, isProduced)) {
                if (!checkItemLocation(itemName, currentPlace, isProduced)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean IsHealthAndDo(String itemName, boolean isProduced){
        if(itemName.equalsIgnoreCase("Health")){
            if(isProduced) {
                currentPlayer.producedHealth();
            }else{
                currentPlayer.consumedHealth();
            }
            return true;
        }
        return false;
    }

    public boolean checkItemLocation(String itemName, Location currentPlace, boolean isProduced) throws Exception {
        ArrayList<Location> gameMap = gameState.getGameMap();
        Location storeroom = gameState.getStoreroom();
        if(storeroom == null){
            throw new Exception("Storeroom is not exist.");
        }
        for (Location location : gameMap) {
            //if item is an artefact.
            if (location.searchArtefactName(itemName)) {
                int indexOfItem = location.searchArtefactIndex(itemName);
                //get the item.
                Artefact item = location.getArtefactList().get(indexOfItem);
                if(isProduced) {
                    //add it to current place's artefactList and remove item from its location.
                    currentPlace.addArtefact(item);
                    location.getArtefactList().remove(item);
                }else{
                    location.getArtefactList().remove(item);
                    storeroom.addArtefact(item);
                }
                return true;
            }
            //if item is furniture.
            if (location.searchFurnitureName(itemName)) {
                int indexOfItem = location.searchFurnitureIndex(itemName);
                Furniture item = location.getFurnitureList().get(indexOfItem);
                if(isProduced) {
                    currentPlace.addFurniture(item);
                    location.getFurnitureList().remove(item);
                }else{
                    location.getFurnitureList().remove(item);
                    storeroom.addFurniture(item);
                }
                return true;
            }
            //if item is a character.
            if (location.searchCharacterName(itemName)) {
                int indexOfItem = location.searchCharacterIndex(itemName);
                Character character = location.getCharacterList().get(indexOfItem);
                if(isProduced) {
                    currentPlace.addCharacter(character);
                    location.getCharacterList().remove(character);
                }else{
                    location.getCharacterList().remove(character);
                    storeroom.addCharacter(character);
                }
                return true;
            }
            // if the item is a location, it means to create a new path between current room or remove this location from map to storeroom.
            if(location.getName().equalsIgnoreCase(itemName)){
                //if it is a produced item.
                if(isProduced){
                    // can not produce path to store room!
                    if (itemName.equalsIgnoreCase("storeroom")) {
                        throw new Exception("You cannot add path to storeroom!");
                    }
                    currentPlace.addPath(itemName);
                    return true;
                }
                else{
                    //if it is a consumed item, only remove the path if there is a path from the player's current location to the "target" location
                    if(currentPlace.searchPath(itemName)){
                       currentPlace.removePath(itemName);
                        return true;
                    }
                }
            }
        }
        //if the consumed item is in the player's inventory.
        return checkInventory(isProduced, itemName, storeroom);
    }

    public boolean checkInventory(boolean isProduced, String itemName, Location storeroom) throws Exception {
        if(!isProduced){
            if(currentPlayer.searchArtefactName(itemName)){
                int indexOfItem = currentPlayer.searchArtefactIndex(itemName);
                Artefact consumedItem = currentPlayer.getInventory().get(indexOfItem);
                currentPlayer.getInventory().remove(indexOfItem);
                storeroom.addArtefact(consumedItem);
                return true;
            }
        }
        return false;
    }


    public int checkActionValid(String[] action, GameAction[] gameActions, Location currentPlace){
        int indexOfAction = -1;
        for(int i = 0; i < gameActions.length; i++){
            for (String s : action) {
                if (gameActions[i].searchSubjects(s)) {
                    if (subjectsCanUse(gameActions[i], currentPlace)) {
                        indexOfAction = i;
                    }
                }
            }
        }
        return indexOfAction;
    }

    public boolean subjectsCanUse(GameAction action, Location currentPlace){
        //check whether all subjects in actions are available in player's inventory or current location.
        ArrayList<String> subjectList = action.getAllSubjects();
        for (String subjectName : subjectList) {
            if (!currentPlace.searchArtefactName(subjectName)) {
                if (!currentPlayer.searchArtefactName(subjectName)) {
                    if (!currentPlace.searchFurnitureName(subjectName)) {
                        if (!currentPlace.searchCharacterName(subjectName)) {
                            if(!currentPlace.getName().equalsIgnoreCase(subjectName)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    public ArrayList<String> checkTrigger(String[] action, TreeMap<String, HashSet<GameAction>> actionList, boolean isBasicCmd) throws Exception {
        ArrayList<String> triggerKey = new ArrayList<>();
        // find all valid trigger name in the command.
        for (String s : action) {
            if (actionList.containsKey(s)) {
                triggerKey.add(s);
            }
        }
        //make sure at least one trigger phase is valid.
        if(!isBasicCmd && triggerKey.size() == 0) {
            throw new Exception("The action command is invalid.");
        }
        return triggerKey;
    }


    public Boolean isBasicCMD (String[] actions, Location currentPlace) throws Exception {
        String validAction = null;
        String[] basicCmd = {"look", "inventory", "get", "drop", "goto", "health"};
        TreeMap<String, HashSet<GameAction>> actionList = gameState.getActionList();
        //check triggerKey in the command.
        boolean isBasicCmd = true;
        ArrayList<String> triggerKey = checkTrigger(actions, actionList, isBasicCmd);
        int actionCnt = 0;
        for(int i = 0; i < actions.length; i++) {
            if (actions[i].equalsIgnoreCase("inv")) {
                actions[i] = "inventory";
            }
            for (String s : basicCmd) {
                if (actions[i].equalsIgnoreCase(s)) {
                    validAction = actions[i];
                    actionCnt++;
                }
            }
        }
        if(actionCnt > 1){
            throw new Exception("You can only do one action at a time.");
        }
        if(actionCnt == 1 && triggerKey.size() == 0) {
            switch (validAction.toLowerCase()) {
                case "look" -> {
                    doLook(currentPlace);
                    resultToString();
                    return true;
                }
                case "inventory" -> {
                    doInventory();
                    resultToString();
                    return true;
                }
                case "get" -> {
                    doGet(actions, currentPlace);
                    resultToString();
                    return true;
                }
                case "drop" -> {
                    doDrop(actions);
                    resultToString();
                    return true;
                }
                case "goto" -> {
                    doGoto(actions, currentPlace);
                    resultToString();
                    return true;
                }
                case "health" -> {
                    reportHealth();
                    resultToString();
                    return true;
                }
            }
        }
        return false;
    }

    private void reportHealth(){
        int health = currentPlayer.getHealth();
        setResult("Your health level is  "+ health + "\n");
    }

    private void doGoto(String[] action, Location currentPlace) throws Exception {
        //moves the player to a new location (if there is a path to that location).
        String LocationName = null;
        int countOfMatch = 0;
        //search whether the location exist in current location's path.
        for(int i = 1; i < action.length; i++){
            if(currentPlace.searchPath(action[i])){
                LocationName = action[i];
                countOfMatch++;
            }
            if(countOfMatch > 1){
                throw new Exception("You can only goto one location at a time.");
            }
        }
        if(countOfMatch == 0){
            throw new Exception("Your current location doesn't have a path to the destination you want.");
        }
        //find destination's location object from gameMap.
        ArrayList<Location> gameMap = gameState.getGameMap();
        Location location = null;
        for (Location place : gameMap) {
            if (place.getName().equalsIgnoreCase(LocationName)) {
                location = place;
            }
        }
        //System.out.println(location.getDescription());
        if(location == null){
            throw new Exception("The destination doesn't exist in the game map.");
        }
        //remove this player from current location and add it to the Goto location.
        currentPlace.removePlayer(currentPlayer);
        location.addPlayer(currentPlayer);
        //update the player's currentLocation.
        currentPlayer.setLocation(location);
        //give the message to the player, told him that he has been to the new place.
        doLook(location);
        resultToString();
    }

    private void doDrop(String[] action) throws Exception {
        //puts down an artefact from player's inventory and places it into the current location.
        String artefactName = null;
        int countOfMatch = 0;
        //search whether the Artefact exist in player's inventory.
        for(int i = 1; i < action.length; i++){
            if(currentPlayer.searchArtefactName(action[i])){
                artefactName = action[i];
                countOfMatch++;
            }
            if(countOfMatch > 1 || action[i].equalsIgnoreCase("and")){
                throw new Exception("You can only drop one artefact at a time.");
            }
        }
        if(countOfMatch == 0){
            throw new Exception("The artefact doesn't exist in your inventory.");
        }
        //if it is, drop it from the player's inventory and put it at the current location.
        currentPlayer.dropArtefact(artefactName);
        setResult("You dropped a "+ artefactName + "\n");
    }

    private void doGet(String[] action, Location currentPlace) throws Exception {
        //picks up a specified artefact from the current location and adds it into player's inventory.
        String artefactName = null;
        int countOfMatch = 0;
        //search whether the Artefact exist at current location
        for(int i = 1; i < action.length; i++){
            if(currentPlace.searchArtefactName(action[i])){
                artefactName = action[i];
                countOfMatch++;
            }
            if(countOfMatch > 1 || action[i].equalsIgnoreCase("and")){
                throw new Exception("You can only get one artefact at a time.");
            }
        }
        if(countOfMatch == 0){
            throw new Exception("The artefact doesn't exist at this location.");
        }
        //if it is, search its index in the location's artefactList for get and delete it.
        currentPlayer.pickUpArtefact(artefactName);
        setResult("You picked up a "+ artefactName + "\n");
    }

    private void doInventory(){
        setResult("In your inventory, you have: \n");
        ArrayList<Artefact> inventory = currentPlayer.getInventory();
        for (Artefact artefact : inventory) {
            setResult(artefact.getDescription() + "\n");
        }
    }


    public String[] deleteSpaceOfCommand(String command){
        String[] commands = command.split(" ");
        ArrayList<String> newCommands = new ArrayList<>();
        for (String value : commands) {
            if (!value.equals("") && !value.equals(" ")) {
                newCommands.add(value);
            }
        }
        int commandSize = newCommands.size();
        String[] finalCommand =  new String[commandSize];
        for (int i = 0; i < newCommands.size(); i++) {
            finalCommand[i] = newCommands.get(i).toLowerCase();
        }
        return finalCommand;
    }

    public void doLook(Location currentPlace){
        setResult("You are in "+currentPlace.getDescription()+ ".\n");
        setResult("At this location, you can find: \n");
        ArrayList<Artefact> artefactList = currentPlace.getArtefactList();
        ArrayList<Furniture> furnitureList = currentPlace.getFurnitureList();
        ArrayList<Character> characterList = currentPlace.getCharacterList();
        ArrayList<Player> playerList = currentPlace.getPlayer();
        ArrayList<String> PathList = currentPlace.getPaths();
        for (Artefact artefact : artefactList) {
            setResult(artefact.getName() + " : " + artefact.getDescription() + "\n");
        }
        for (Furniture furniture : furnitureList) {
            setResult(furniture.getName() + " : " + furniture.getDescription() + "\n");
        }
        for (Character character : characterList) {
            setResult(character.getName() + " : " + character.getDescription() + "\n");
        }
        for (Player player : playerList) {
            //don't show the current player's name!
            if (!player.getName().equals(currentPlayer.getName())) {
                setResult(player.getDescription() + "\n");
            }
        }
        setResult("These are the locations you can visit: \n");
        for (String s : PathList) {
            setResult(s + "\n");
        }
    }

    public void setResult(String output) {
        result.add(output);
    }


    public void resultToString(){
        StringBuilder finalString = new StringBuilder();
        for (String string : result) {
            finalString.append(string);
        }
        finalResult = String.valueOf(finalString);
    }

    public String getFinalResult() {
        return finalResult;
    }

}



