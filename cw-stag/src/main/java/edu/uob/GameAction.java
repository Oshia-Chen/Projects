package edu.uob;

import java.util.ArrayList;

public class GameAction {

    private final ArrayList<String> subjectList;
    private final ArrayList<String> consumedList;
    private final ArrayList<String> producedList;
    private String narration;

    public GameAction(){
        subjectList = new ArrayList<>();
        consumedList = new ArrayList<>();
        producedList = new ArrayList<>();
    }
    public void addSubjects(String subject){
        subjectList.add(subject);
    }

    public ArrayList<String> getAllSubjects(){
        return subjectList;
    }

    public Boolean searchSubjects(String subject){
        for (String s : subjectList) {
            if (s.equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }

    public void addConsumed(String consumed){
        consumedList.add(consumed);
    }

    public ArrayList<String> getAllConsumed(){
        return consumedList;
    }

    public void addProduced(String produced){
        producedList.add(produced);
    }

    public ArrayList<String> getAllProduced(){
        return producedList;
    }

    public void addNarration(String string){
        narration = string;
    }

    public String getNarration(){
        return narration;
    }
}

