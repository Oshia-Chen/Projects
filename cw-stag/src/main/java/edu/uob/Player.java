package edu.uob;

import java.util.ArrayList;

public class Player extends GameEntity{

    private Location currentLocation;
    private ArrayList<Artefact> inventory;
    private SetGame gameState;
    private int health;

    public Player(String name, String description){
        super(name, description);
        inventory = new ArrayList<>();
        health = 3;
    }

    public void setFullHealth(){
        health = 3;
    }

    public void cleanInventory(){
        for(int i = 0; i < inventory.size(); i++){
            inventory.remove(i);
        }
        inventory = new ArrayList<>();
    }

    public void consumedHealth(){
        if (health > 0) {
            health = health - 1;
        }
    }

    public void producedHealth(){
        if(health < 3){
            health = health + 1;
        }
    }

    public int getHealth(){
        return health;
    }

    public void setFirstLocation(){
        currentLocation = gameState.getFirstLocation();
    }


    public void setLocation(Location location){
        currentLocation = location;
    }

    public Location getLocation(){
        return currentLocation;
    }


    public void pickUpArtefact(String name) throws RuntimeException {
        int indexOfArtefact = currentLocation.searchArtefactIndex(name);
        if(indexOfArtefact == -1){
            throw new RuntimeException("The Artefact doesn't exist in the current location.");
        }
        Artefact newArtefact = currentLocation.getArtefactList().get(indexOfArtefact);
        //add the Artefact picked up by player to player's inventory.
        inventory.add(newArtefact);
        //delete the Artefact picked up by player from the location Artefact List.
        currentLocation.getArtefactList().remove(newArtefact);
    }

    public void dropArtefact(String name) throws Exception {
        if(!searchArtefactName(name)){
            throw new Exception("The Artefact doesn't exist in your inventory.");
        }
        int indexOfArtefact = searchArtefactIndex(name);
        Artefact newArtefact = inventory.get(indexOfArtefact);
        inventory.remove(newArtefact);
        //drop the Artefact and place it into the current location.
        currentLocation.getArtefactList().add(newArtefact);
    }

    public Boolean searchArtefactName(String name){
        //search certain ArtefactName in player's inventory。
        for (Artefact artefact : inventory) {
            if (artefact.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public int searchArtefactIndex(String name){
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public void setGameState(SetGame game){
        gameState = game;
    }

    public ArrayList<Artefact> getInventory(){
        return inventory;
    }

}
