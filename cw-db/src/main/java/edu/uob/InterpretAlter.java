package edu.uob;

import java.io.*;
import java.util.ArrayList;

public class InterpretAlter extends DBcmd{
    public InterpretAlter(){

    }

    public void doInterpret() throws Exception {
    //"ALTER TABLE " <TableName> " " <AlterationType> " " <AttributeName>
    //<AlterationType> ::= "ADD" | "DROP"
        String tableName = getTableName().get(0);
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        File tableFile = newTableFile(tableName, databaseUsing);
        BufferedReader buffReader = createTableReader(tableFile);
        String attributeName = getAttributeName().get(0);
        ArrayList<String> tableLine = readTableInLine(buffReader);
        ArrayList<ArrayList<String>> tableContent = readTableToList(tableLine);
        int rowNum = tableLine.size();
        if(getAlterationType().equals("ADD")) tableContent = addAttribute(rowNum, tableContent, attributeName);
        if(getAlterationType().equals("DROP")) tableContent = dropAttribute(rowNum, tableContent, attributeName);
        writeToFile(tableFile, rowNum, tableContent);
    }

    public ArrayList<ArrayList<String>> dropAttribute(int rowNum, ArrayList<ArrayList<String>> tableContent, String attributeName) throws Exception {
        int columnNum = tableContent.get(0).size();
        //System.out.println("1: "+tableContent);
        int indexOfAttrib = 0;
        if(attributeName.equals("id")){
            throw new Exception("Cannot drop the id column.");
        }
        boolean flag = false;
        for(int i = 0; i < columnNum; i++) {
            if (tableContent.get(0).get(i).equals(attributeName)) {
                indexOfAttrib = i;
                flag = true;
            }
        }
        if(!flag){
            throw new Exception("Cannot drop the attribute that doesn't exit.");
        }
        for(int j = 0; j < rowNum; j++){
            if(indexOfAttrib < tableContent.get(j).size()){
                tableContent.get(j).remove(indexOfAttrib);
            }
        }
        return tableContent;
    }

    public ArrayList<ArrayList<String>> addAttribute(int rowNum, ArrayList<ArrayList<String>> tableContent, String attributeName){
        //System.out.println("reach add");
        tableContent.get(0).add(attributeName);
        for(int i = 1; i < rowNum; i++){
            tableContent.get(i).add(" ");
        }
        return tableContent;
    }
}
