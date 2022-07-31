package edu.uob;

import java.io.File;

public class InterpretDrop extends DBcmd{
    public InterpretDrop(){

    }

    public void doInterpret() throws Exception {
        //"DROP " <Structure> " " <StructureName>
        //"DATABASE" | "TABLE"
        String databaseUsing = getDatabaseUsing();
        if(databaseUsing == null){
            throw new Exception("Use database before dropping any database or table.");
        }
        if(getStructure().equalsIgnoreCase("DATABASE")){
            String structureName = getStructureName();
            String databaseDirectory = getDirectoryPath();
            String databaseName = databaseDirectory + File.separator + structureName;
            File databaseDropping = new File(databaseName);
            if(databaseDropping.exists()){
                databaseDropping.delete();
            } else {
                throw new Exception("The database name doesn't exist.");
            }
            if(databaseUsing.equals(structureName)){
                setDatabaseUsing(null);
            }
        }
        if(getStructure().equalsIgnoreCase("TABLE")){
            String structureName = getStructureName();
            File tableDropping = newTableFile(structureName, databaseUsing);
            if(tableDropping.exists()){
                tableDropping.delete();
            } else {
                throw new Exception("The table name doesn't exist in database.");
            }
        }
        setResult(" ");
    }
}
