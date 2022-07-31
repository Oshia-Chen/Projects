package edu.uob;

import java.io.*;
import java.util.ArrayList;

public class InterpretInsert extends DBcmd{
    public InterpretInsert(){

    }

    public void doInterpret() throws Exception {
    //"INSERT INTO " <TableName> " VALUES(" <ValueList> ")"
        String tableName = getTableName().get(0);
        String databaseUsing = getDatabaseUsing();
        checkDatabaseUsing(databaseUsing);
        File tableFile = newTableFile(tableName, databaseUsing);
        BufferedReader buffReader = createTableReader(tableFile);
        ArrayList<String> tableLine = readTableInLine(buffReader);
        ArrayList<String> valueList = replaceRetainString();
        String[] firstLine = tableLine.get(0).split("\t");
        int columnOfTable = firstLine.length - 1;
        for (String value : firstLine) {
            if (value.equals("")) {
                columnOfTable--;
            }
        }
        if(valueList.size() != columnOfTable){
            throw new Exception("The number of inserting value doesn't equal to the number of table's column.");
        }
        int newID = calculateNewID(tableLine);
        tableLine = insertToTable(newID, tableLine, valueList);
        writeToFile(tableFile, tableLine);
    }

    public int calculateNewID(ArrayList<String> tableLine) {
        int newID = 1;
        if(tableLine.size() > 1) {
            String tmp = tableLine.get(tableLine.size() - 1);
            String[] lastLine = tmp.split("\t");
            newID = Integer.parseInt(lastLine[0]) + 1;
        }
        return newID;
    }

    public ArrayList<String> insertToTable(int newID, ArrayList<String> tableLine, ArrayList<String> valueList){
        StringBuilder valueInserting = new StringBuilder();
        valueInserting.append(newID).append("\t");
        for (String s : valueList) {
            valueInserting.append(s).append("\t");
        }
        tableLine.add(String.valueOf(valueInserting));
        return tableLine;
    }

    public void writeToFile(File tableFile, ArrayList<String> tableLine) throws IOException {
        BufferedWriter buffWriter = createTableWriter(tableFile);
        for (String s : tableLine) {
            buffWriter.write(String.valueOf(s));
            buffWriter.write("\n");
        }
        buffWriter.flush();
        buffWriter.close();
        setResult(" ");
    }
}
