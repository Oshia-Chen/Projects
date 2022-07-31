package edu.uob;

import java.io.*;

public class InterpretCreate extends DBcmd {

    public InterpretCreate(){

    }

    public void doInterpret() throws Exception {
        String databaseDirectory = getDirectoryPath();
        if(getDatabaseName() != null && getTableName().size() == 0) {
            createDatabase(databaseDirectory);
        }
        if (getTableName().size() != 0) {
            createTable(databaseDirectory);
        }
        setResult(" ");
    }
    public void createDatabase(String databaseDirectory) throws Exception {
        //"CREATE DATABASE " <DatabaseName>
        String directoryName = getDatabaseName();
        String databaseName = databaseDirectory + File.separator + directoryName;
        File databaseCreating = new File(databaseName);
        if (databaseCreating.exists()) {
            throw new Exception("The database name already exists.");
        } else {
            databaseCreating.mkdir();
        }
    }

    public void createTable(String databaseDirectory) throws Exception {
        //CREATE TABLE " <TableName> | "CREATE TABLE " <TableName> "(" <AttributeList> ")"
        String fileName = getTableName().get(0);
        String directoryName = getDatabaseUsing();
        checkDatabaseUsing(directoryName);
        String pathOfTable = databaseDirectory + File.separator + directoryName + File.separator + fileName;
        File tableCreating = new File(pathOfTable);
        //System.out.println("table creating reach");
        if (tableCreating.exists()) {
            throw new Exception("The table name already exists.");
        } else {
            try {
                tableCreating.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Unsuccessful in creating a table.");
            }
        }
        if (getAttribList() != null) {
            FileWriter writer = new FileWriter(tableCreating);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            StringBuilder column = new StringBuilder();
            column.append("id").append("\t");
            for (int i = 0; i < getAttribList().size(); i++) {
                column.append(getAttribList().get(i)).append("\t");
            }
            column.append("\n");
            buffWriter.write(String.valueOf(column));
            buffWriter.flush();
            buffWriter.close();
        }
    }

}
