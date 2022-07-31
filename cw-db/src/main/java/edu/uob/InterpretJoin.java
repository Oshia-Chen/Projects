package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class InterpretJoin extends DBcmd{
    public InterpretJoin(){

    }

    public void doInterpret() throws Exception {
    //"JOIN " <TableName> " AND " <TableName> " ON " <AttributeName> " AND " <AttributeName>
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        if(getTableName().size() < 2){
            throw new Exception("Missed the table name.");
        }
        String tableName1 = getTableName().get(0);
        String tableName2 = getTableName().get(1);
        File table1 = newTableFile(tableName1, databaseUsing);
        File table2 = newTableFile(tableName2, databaseUsing);
        BufferedReader reader1 = createTableReader(table1);
        BufferedReader reader2 = createTableReader(table2);
        ArrayList<String> tableLine1 = readTableInLine(reader1);
        ArrayList<ArrayList<String>> tableContent1 = readTableToList(tableLine1);
        ArrayList<String> tableLine2 = readTableInLine(reader2);
        ArrayList<ArrayList<String>> tableContent2 = readTableToList(tableLine2);
        int IndexOfAttrib1 = searchAttrib(tableContent1, 0);
        int IndexOfAttrib2 = searchAttrib(tableContent2, 1);
        ArrayList<ArrayList<String>> outputResult = joinTwoTable(IndexOfAttrib1, IndexOfAttrib2, tableContent1, tableContent2);
        addColumnName(IndexOfAttrib2, tableContent2, outputResult);
        replaceID(outputResult);
        outputForClient(outputResult);
    }

    public int searchAttrib(ArrayList<ArrayList<String>> tableContent, int index) throws Exception {
        int IndexOfAttrib = 0;
        boolean flag = false;
        ArrayList<String> attribNameList = getAttributeName();
        String attributeName = attribNameList.get(index);
        if(attribNameList.size() < 2){
            throw new Exception("Missed the attribute name.");
        }
        for(int i = 0; i < tableContent.get(0).size(); i++){
            //System.out.println(tableContent.get(0).get(i));
            if(tableContent.get(0).get(i).equals(attributeName)){
                IndexOfAttrib = i;
                flag = true;
            }
        }
        if(!flag){
            throw new Exception("The attribute doesn't exit in the table.");
        }
        return IndexOfAttrib;
    }

    public void replaceID(ArrayList<ArrayList<String>> outputResult){
        int newID = 1;
        for (int i = 1; i < outputResult.size(); i++) {
            outputResult.get(i).set(0, String.valueOf(newID));
            newID++;
        }
    }

    public void addColumnName(int IndexOfAttrib, ArrayList<ArrayList<String>> tableContent, ArrayList<ArrayList<String>> outputResult){
        for(int i = 1; i < tableContent.get(0).size(); i++){
            if(i != IndexOfAttrib){
                outputResult.get(0).add(tableContent.get(0).get(i));
            }
        }
    }

    public ArrayList<ArrayList<String>> joinTwoTable(int IndexOfAttrib1, int IndexOfAttrib2, ArrayList<ArrayList<String>> tableContent1, ArrayList<ArrayList<String>> tableContent2){
        ArrayList<ArrayList<String>> outputResult = new ArrayList<>(tableContent1);
        for(int i = 1; i < tableContent1.size(); i++){
            String value1 = tableContent1.get(i).get(IndexOfAttrib1);
            for(int j = 1; j < tableContent2.size(); j++){
                String value2 = tableContent2.get(j).get(IndexOfAttrib2);
                if(value1.equals(value2)){
                    int table2Col = tableContent2.get(j).size();
                    for(int k = 1; k < table2Col; k++){
                        if(k != IndexOfAttrib2){
                            outputResult.get(i).add(tableContent2.get(j).get(k));
                        }
                    }
                }
            }
        }
        for (ArrayList<String> strings : outputResult) {
            strings.remove(IndexOfAttrib1);
        }
        return outputResult;
    }

    public void outputForClient(ArrayList<ArrayList<String>> outputResult){
        StringBuilder outputTable = new StringBuilder();
        for (ArrayList<String> strings : outputResult) {
            for (String string : strings) {
                outputTable.append(string).append("\t");
            }
            outputTable.append("\n");
        }
        setResult(String.valueOf(outputTable));
    }
}
