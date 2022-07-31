package edu.uob;

import java.util.ArrayList;

public class Location extends GameEntity{
    private final ArrayList<String> pathsCanGo;
    private ArrayList<Artefact> artefactList;
    private ArrayList<Furniture> furnitureList;
    private ArrayList<Character> characterList;
    private final ArrayList<Location> locationList;
    private final ArrayList<Player> playerList;


    public Location(String name, String description){
        super(name, description);
        pathsCanGo = new ArrayList<>();
        artefactList = new ArrayList<>();
        furnitureList = new ArrayList<>();
        characterList = new ArrayList<>();
        playerList = new ArrayList<>();
        locationList = new ArrayList<>();
    }

    public ArrayList<String> getPaths() {
        return pathsCanGo;
    }

    public void addPath(String paths) {
        if(searchPath(paths)){
            throw new RuntimeException("The path has already exist.");
        }
        pathsCanGo.add(paths);
    }

    public void removePath(String paths) {
        if(pathsCanGo.size() == 0){
            return;
        }
        pathsCanGo.remove(paths);
    }

    public Boolean searchPath(String name){
        for (String destination : pathsCanGo) {
            if (destination.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addArtefact (Artefact artefact) throws Exception {
        if(searchArtefactName(artefact.getName())){
            throw new Exception("The Artefact has existed at this location.");
        }
        artefactList.add(artefact);
    }


    public void setArtefactList(ArrayList<Artefact> list) {
        artefactList = list;
    }

    public ArrayList<Artefact> getArtefactList() {
        return artefactList;
    }

    public void setFurnitureList(ArrayList<Furniture> furniture) {
        furnitureList = furniture;
    }

    public ArrayList<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public void addFurniture (Furniture furniture) throws Exception {
        if(searchFurnitureName(furniture.getName())){
            throw new Exception("The Furniture has existed at this location.");
        }
        furnitureList.add(furniture);
    }

    public void addCharacter (Character character) throws Exception {
        if(searchCharacterName(character.getName())){
            throw new Exception("The Character has existed at this location.");
        }
        characterList.add(character);
    }


    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
    }

    public ArrayList<Player> getPlayer() {
        return playerList;
    }

    public ArrayList<Character> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(ArrayList<Character> characters) {
        characterList = characters;
    }

    public Boolean searchArtefactName(String name){
        for (Artefact artefact : artefactList) {
            if (artefact.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Boolean searchFurnitureName(String name){
        for (Furniture furniture : furnitureList) {
            if (furniture.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Boolean searchCharacterName(String name){
        for (Character character : characterList) {
            if (character.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public int searchArtefactIndex(String name){
        for (int i = 0; i < artefactList.size(); i++) {
            if (artefactList.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public int searchFurnitureIndex(String name){
        for (int i = 0; i < furnitureList.size(); i++) {
            if (furnitureList.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public int searchCharacterIndex(String name){
        for (int i = 0; i < characterList.size(); i++) {
            if (characterList.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

}

