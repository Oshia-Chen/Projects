package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class InterpretSelect extends DBcmd{
    public InterpretSelect(){

    }

    public void doInterpret() throws Exception {
    //"SELECT " <WildAttribList> " FROM " <TableName> | "SELECT " <WildAttribList> " FROM " <TableName> " WHERE " <Condition>
        String tableName = getTableName().get(0);
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        File tableFile = newTableFile(tableName, databaseUsing);
        BufferedReader buffReader = createTableReader(tableFile);
        //read table by line.
        ArrayList<String> tableLine = readTableInLine(buffReader);
        ArrayList<ArrayList<String>> tableContent = readTableToList(tableLine);
        ArrayList<Integer> finalResult;
        if(getWildAttribList().size() != 0 && !getWildAttribList().get(0).equals("*") && getValueList().size() != 0){
            ArrayList<String> tmpArray = getAttributeName();
            int remove = 0;
            for(int i = 0; i < getWildAttribList().size(); i++){
                tmpArray.remove(i - remove);
                remove++;
            }
        }
        ArrayList<String> valueList = replaceRetainString();
        if(valueList.size() != 0) {
            finalResult = matchAllCondition(tableContent, valueList);
            // set value to table content
            if (finalResult != null) {
                ArrayList<ArrayList<String>> outputResult = outputAfterSelect(tableContent, finalResult);
                if(outputResult == null){
                    setResult(" ");
                    return;
                }
                outputForClient(outputResult);
            } else {
                setResult(" ");
            }
        }else{
            ArrayList<ArrayList<String>> outputResult = outputAfterSelect(tableContent, null);
            outputForClient(outputResult);
        }
    }

    public ArrayList<ArrayList<String>> outputAfterSelect(ArrayList<ArrayList<String>> tableContent, ArrayList<Integer> finalMatch) throws Exception {
        ArrayList<ArrayList<String>> outputResult = new ArrayList<>();
        ArrayList<Integer> selectAttribIndex = new ArrayList<>();
        ArrayList<String> wildAttribList = getWildAttribList();
        if(wildAttribList.get(0).equals("*")){
            for(int i = 0; i < tableContent.get(0).size(); i++){
                selectAttribIndex.add(i);
            }
        }else {
            for (String s : wildAttribList) {
                int index = findIndexOfAttrib(tableContent, s);
                selectAttribIndex.add(index);
            }
        }
        Collections.sort(selectAttribIndex);
        if(finalMatch == null){
            for (int i = 0; i < tableContent.size(); i++) {
                outputResult.add(new ArrayList<>());
                for (int j = 0; j < selectAttribIndex.size(); j++) {
                    int columnIndex = selectAttribIndex.get(j);
                    String result = tableContent.get(i).get(columnIndex);
                    outputResult.get(i).add(result);
                }
            }
        }else if(finalMatch.size() != 0 && !finalMatch.equals("")){
            outputResult.add(new ArrayList<>());
            for (Integer attribIndex : selectAttribIndex) {
                String columnName = tableContent.get(0).get(attribIndex);
                outputResult.get(0).add(columnName);
            }
            for (int i = 0; i < finalMatch.size(); i++) {
                int rowIndex = finalMatch.get(i);
                outputResult.add(new ArrayList<>());
                for (int columnIndex : selectAttribIndex) {
                    String result = tableContent.get(rowIndex).get(columnIndex);
                    outputResult.get(i + 1).add(result);
                }
            }
        }
        else{
            outputResult = null;
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
