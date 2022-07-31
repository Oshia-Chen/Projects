package edu.uob;

import java.io.File;

public class InterpretUse extends DBcmd{

    public InterpretUse(){

    }

    public void doInterpret() throws Exception {
        //"USE " <DatabaseName>
        String databaseDirectory = getDirectoryPath();
        String databaseName =  databaseDirectory + File.separator + getDatabaseUsing();
        File databaseUsing = new File(databaseName);
        if(databaseUsing.exists() && databaseUsing.isDirectory()){
            setResult(" ");
        }else{
            throw new Exception ("The database is not exist.");
        }
    }

}
