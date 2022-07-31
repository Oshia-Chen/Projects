package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class InterpretUpdate extends DBcmd{
    public InterpretUpdate(){

    }

    public void doInterpret() throws Exception {
    //"UPDATE " <TableName> " SET " <NameValueList> " WHERE " <Condition>
        String tableName = getTableName().get(0);
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        File tableFile = newTableFile(tableName, databaseUsing);
        BufferedReader buffReader = createTableReader(tableFile);
        int attribListSize = getAttributeName().size();
        ArrayList<String> valueLists = replaceString();
        String attribNameToSet = getAttributeName().get(attribListSize-1);
        int valueListSize = getValueList().size();
        String ValueToSet = valueLists.get(valueListSize-1);
        if(attribNameToSet.equals("id")){
            throw new Exception("Cannot set the id column.");
        }
        //read table by line.
        ArrayList<String> tableLine = readTableInLine(buffReader);
        ArrayList<ArrayList<String>> tableContent = readTableToList(tableLine);
        int indexOfSetAttrib = findIndexOfAttrib(tableContent, attribNameToSet);
        ArrayList<Integer> finalResult = matchAllCondition(tableContent, valueLists);
        if(finalResult.size() != 0) {
            tableContent = updatingTable(indexOfSetAttrib, ValueToSet, finalResult, tableContent);
            int rowNum = tableLine.size();
            writeToFile(tableFile, rowNum, tableContent);
        }
        setResult(" ");
    }


    public ArrayList<ArrayList<String>> updatingTable(int indexOfSetAttrib, String ValueToSet, ArrayList<Integer> finalResult, ArrayList<ArrayList<String>> tableContent){
        for (Integer index : finalResult) {
            tableContent.get(index).set(indexOfSetAttrib, ValueToSet);
        }
        return tableContent;
    }

    public ArrayList<String> replaceString() {
        int indexOfStrList = 0;
        ArrayList<String> valueList = getValueList();
        if(getStringList() != null){
            ArrayList<String> stringList = getStringList();
            String firstString = stringList.get(0);
            stringList.remove(0);
            stringList.add(firstString);
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.get(i).contains("'")) {
                    valueList.set(i, stringList.get(indexOfStrList));
                    indexOfStrList++;
                }
            }
        }
        return valueList;
    }

}
