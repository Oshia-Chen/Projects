package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class InterpretDelete extends DBcmd{
    public InterpretDelete(){

    }

    public void doInterpret() throws Exception {
    //"DELETE FROM " <TableName> " WHERE " <Condition>
        String tableName = getTableName().get(0);
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        File tableFile = newTableFile(tableName, databaseUsing);
        BufferedReader buffReader = createTableReader(tableFile);
        ArrayList<String> valueLists = replaceRetainString();
        //read table by line.
        ArrayList<String> tableLine = readTableInLine(buffReader);
        ArrayList<ArrayList<String>> tableContent = readTableToList(tableLine);
        ArrayList<Integer> finalResult = matchAllCondition(tableContent, valueLists);
        if(finalResult.size() != 0) {
            updateTable(finalResult, tableContent);
            int rowNum = tableContent.size();
            writeToFile(tableFile, rowNum, tableContent);
        }
        setResult(" ");
    }

    public void updateTable(ArrayList<Integer> finalResult, ArrayList<ArrayList<String>> tableContent){
        int minusIndex = 0;
        for (int index : finalResult) {
            tableContent.remove(index - minusIndex);
            minusIndex++;
        }
    }
}
