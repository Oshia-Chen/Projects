package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;


public class SQLParser {
    private int commandSize;
    private String databaseUsing;
    private String directoryPath;
    private DBcmd interpreter;
    private ArrayList<String> valuesList;
    private ArrayList<String> stringList;
    private ArrayList<String> attributeList;

    public boolean parser(String command, String databaseName, String databasePath) throws Exception {
        if(command.contains("'")){
           retainString(command);
        }
        String[] commands = deleteSpaceOfCommand(command);
        commandSize = commands.length;
        if(commandSize < 1){
            return false;
        }
        directoryPath = databasePath;
        databaseUsing = databaseName;
        valuesList = new ArrayList<>();
        attributeList = new ArrayList<>();
        interpreter = new DBcmd();
        return checkCommand(commands);
    }
    
    public String[] deleteSpaceOfCommand(String command){
        String[] commands = command.split(" ");
        ArrayList<String> newCommands = new ArrayList<>();
        for (String value : commands) {
            if (!value.equals("") && !value.equals(" ")) {
                newCommands.add(value);
            }
        }
        int commandSize = newCommands.size();
        String[] finalCommand =  new String[commandSize];
        for (int i = 0; i < newCommands.size(); i++) {
                finalCommand[i] = newCommands.get(i);
            }
        return finalCommand;
    }

    public void retainString(String command){
        stringList = new ArrayList<>();
        int lenOfCmd = command.length();
        ArrayList<Integer> stringIndex = new ArrayList<>();
        for(int i = 0; i < lenOfCmd; i++){
            if(command.charAt(i) == '\''){
                stringIndex.add(i);
            }
        }
        int lenOfIndex = stringIndex.size();
        if(lenOfIndex % 2 != 0){
            return;
        }
        for(int j = 0; j+1 < lenOfIndex; j+= 2){
            stringList.add(command.substring(stringIndex.get(j)+1, stringIndex.get(j+1)));
        }
    }


    public boolean checkCommand(String[] command) throws Exception {
        //<CommandType> ";"
        if(command[commandSize - 1].endsWith(";")){
            int length = command[commandSize - 1].length();
            if(length != 1){
                command[commandSize - 1] = command[commandSize - 1].substring(0,length-1);
            }
            else{
                commandSize = commandSize - 1;
            }
        }
        else{
            return false;
        }
        return checkCommandType(command);
    }


    public boolean checkCommandType(String[] command) throws Exception {
        //<Use> | <Create> | <Drop> | <Alter> | <Insert> | <Select> | <Update> | <Delete> | <Join>
        if(command[0].equalsIgnoreCase("USE")){
            if(parseUse(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("CREATE")){
            if(parseCreate(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("DROP")){
            if(parseDrop(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("ALTER")){
            if(parseAlter(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("INSERT")){
            if(parseInsert(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("SELECT")){
            if(parseSelect(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("UPDATE")){
            if(parseUpdate(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("DELETE")){
            if(parseDelete(command)){
                return true;
            }
        }
        if(command[0].equalsIgnoreCase("JOIN")) {
            if (parseJoin(command)) {
                return true;
            }
        }
        throw new Exception ("Invalid query.");
    }


    public boolean parseUse(String[] command) throws Exception {
        //"USE " <DatabaseName>
        if(isDatabaseName(command[1])){
            InterpretUse tmp = new InterpretUse();
            tmp.setDatabaseUsing(command[1]);
            tmp.setDirectoryPath(directoryPath);
            databaseUsing = command[1];
            tmp.doInterpret();
            interpreter = tmp;
            return true;
        }
        return false;
    }


    public boolean parseCreate(String[] command) throws Exception {
        //<CreateDatabase> | <CreateTable>
        return parseCreateDatabase(command) || parseCreateTable(command);
    }


    public boolean parseCreateDatabase(String[] command) throws Exception {
        //"CREATE DATABASE " <DatabaseName>
        if(commandSize == 3 && command[1].equalsIgnoreCase("DATABASE")){
            if(isDatabaseName(command[2])){
                InterpretCreate tmp = new InterpretCreate();
                tmp.setDatabaseName(command[2]);
                tmp.setDatabaseUsing(databaseUsing);
                tmp.setDirectoryPath(directoryPath);
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
        }
        return false;
    }


    public boolean parseCreateTable(String[] command) throws Exception {
        //"CREATE TABLE " <TableName> | "CREATE TABLE " <TableName> "(" <AttributeList> ")"
        if(!command[1].equalsIgnoreCase("TABLE")) {
            return false;
        }
        if(commandSize == 3){
            if(isTableName(command[2])){
                InterpretCreate tmp = new InterpretCreate();
                tmp.setDatabaseUsing(databaseUsing);
                tmp.setDirectoryPath(directoryPath);
                tmp.setTableName(command[2]);
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
        }
        String commandString = command[2];
        for(int i = 3; i < commandSize; i++){
            commandString = commandString.concat(command[i]);
        }
        if(!commandString.endsWith(")")){
            return false;
        }
        String[] commands = commandString.split("\\(");
        ArrayList<String> attribList = new ArrayList<>();
        attribList.add(commands[1].substring(0, commands[1].length()-1));
        if(isTableName(commands[0]) && isAttributeList(attribList)){
            InterpretCreate tmp = new InterpretCreate();
            tmp.setDatabaseUsing(databaseUsing);
            tmp.setDirectoryPath(directoryPath);
            tmp.setTableName(command[2]);
            tmp.setAttribList(attributeList);
            tmp.doInterpret();
            interpreter = tmp;
            return true;
       }
        return false;
    }


    public boolean parseDrop(String[] command) throws Exception {
        //"DROP " <Structure> " " <StructureName>
        if(commandSize != 3){
            return false;
        }
        if(isStructure(command[1])){
            if(isPlainText(command[2])){
                InterpretDrop tmp = new InterpretDrop();
                tmp.setDatabaseUsing(databaseUsing);
                tmp.setDirectoryPath(directoryPath);
                tmp.setStructure(command[1]);
                tmp.setStructureName(command[2]);
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
        }
        return false;
    }


    public boolean parseAlter(String[] command) throws Exception {
        //"ALTER TABLE " <TableName> " " <AlterationType> " " <AttributeName>
        if(commandSize != 5 || !command[1].equalsIgnoreCase("TABLE")) {
            return false;
        }
        if(isTableName(command[2])){
            if(isAlterationType(command[3])){
                if(isAttributeName(command[4])){
                    InterpretAlter tmp = new InterpretAlter();
                    for(int i = 0; i < interpreter.getAttributeName().size(); i++){
                        tmp.setAttributeName(interpreter.getAttributeName().get(i));
                    }
                    tmp.setDatabaseUsing(databaseUsing);
                    tmp.setDirectoryPath(directoryPath);
                    tmp.setTableName(command[2]);
                    tmp.setAlterationType(command[3]);
                    //tmp.setAttributeName(command[4]);
                    tmp.doInterpret();
                    interpreter = tmp;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean parseInsert(String[] command) throws Exception {
        //"INSERT INTO " <TableName> " VALUES(" <ValueList> ")"
        if(commandSize < 4 || !command[1].equalsIgnoreCase("INTO")) {
            return false;
        }
        if(!isTableName(command[2])){
            return false;
        }
        String commandString = command[3];
        for(int i = 4; i < commandSize; i++){
            commandString = commandString.concat(command[i]);
        }
        if(!commandString.endsWith(")")){
            return false;
        }
        String[] commands = commandString.split("\\(");
        String values = commands[1].substring(0, commands[1].length()-1);
        if(commands[0].equalsIgnoreCase("VALUES")){
            if(isValueList(values)){
                InterpretInsert tmp = new InterpretInsert();
                for(int i = 0; i < interpreter.getValueList().size(); i++){
                    tmp.setValueList(interpreter.getValueList().get(i));
                }
                tmp.setTableName(command[2]);
                tmp.setDatabaseUsing(databaseUsing);
                tmp.setDirectoryPath(directoryPath);
                tmp.setStringList(stringList);
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
        }
        return false;
    }


    public boolean parseSelect(String[] command) throws Exception {
        // "SELECT " <WildAttribList> " FROM " <TableName> | "SELECT " <WildAttribList> " FROM " <TableName> " WHERE " <Condition>
        int indexOfFrom = 0;
        int indexOfWhere = 0;
        for(int i = 2; i < commandSize; i++){
            if(command[i].equalsIgnoreCase("FROM")){
                indexOfFrom = i;
            }
            if(command[i].equalsIgnoreCase("WHERE")){
                indexOfWhere = i;
            }
        }
        if(indexOfFrom == 0 || indexOfFrom == commandSize-1 || !isTableName(command[indexOfFrom+1])){
            return false;
        }
        ArrayList<String> attribList = new ArrayList<>(Arrays.asList(command).subList(1, indexOfFrom));
        if(isWildAttribList(attribList)){
            InterpretSelect tmp = new InterpretSelect();
            tmp.setDatabaseUsing(databaseUsing);
            tmp.setDirectoryPath(directoryPath);
            tmp.setTableName(command[indexOfFrom+1]);
            tmp.setWildAttribList(attributeList);
            tmp.setStringList(stringList);
            if(indexOfFrom+1 == commandSize-1 && indexOfWhere == 0){
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
            else{
                ArrayList<String> conditionList = new ArrayList<>(Arrays.asList(command).subList(indexOfWhere + 1, commandSize));
                if(isConditionLists(conditionList)){
                    for(int i = 0; i < interpreter.getValueList().size(); i++){
                        tmp.setValueList(interpreter.getValueList().get(i));
                    }
                    for(int i = 0; i < interpreter.getOperatorList().size(); i++){
                        tmp.setOperatorList(interpreter.getOperatorList().get(i));
                    }
                    for(int i = 0; i < interpreter.getCondRelation().size(); i++){
                        tmp.setCondRelation(interpreter.getCondRelation().get(i));
                    }
                    for(int i = 0; i < interpreter.getAttributeName().size(); i++){
                        tmp.setAttributeName(interpreter.getAttributeName().get(i));
                    }
                    tmp.doInterpret();
                    interpreter = tmp;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean parseUpdate(String[] command) throws Exception {
        //"UPDATE " <TableName> " SET " <NameValueList> " WHERE " <Condition>
        if(commandSize < 6){
            return false;
        }
        if(!isTableName(command[1]) || !command[2].equalsIgnoreCase("SET")){
            return false;
        }
        int indexOfWhere = 0;
        for(int i = 3; i < commandSize; i++){
            if(command[i].equalsIgnoreCase("WHERE")){
                indexOfWhere = i;
            }
        }
        if(indexOfWhere == 0 || indexOfWhere == commandSize-1){
            return false;
        }
        ArrayList<String> nameValueList = new ArrayList<>(Arrays.asList(command).subList(3, indexOfWhere));
        ArrayList<String> conditionList = new ArrayList<>(Arrays.asList(command).subList(indexOfWhere + 1, commandSize));
        if(isConditionLists(conditionList)){
            if(isNameValueList(nameValueList)){
                InterpretUpdate tmp = new InterpretUpdate();
                for(int i = 0; i < interpreter.getValueList().size(); i++){
                    tmp.setValueList(interpreter.getValueList().get(i));
                }
                for(int i = 0; i < interpreter.getOperatorList().size(); i++){
                    tmp.setOperatorList(interpreter.getOperatorList().get(i));
                }
                for(int i = 0; i < interpreter.getCondRelation().size(); i++){
                    tmp.setCondRelation(interpreter.getCondRelation().get(i));
                }
                for(int i = 0; i < interpreter.getAttributeName().size(); i++){
                    tmp.setAttributeName(interpreter.getAttributeName().get(i));
                }
                tmp.setDatabaseUsing(databaseUsing);
                tmp.setDirectoryPath(directoryPath);
                tmp.setStringList(stringList);
                tmp.setTableName(command[1]);
                tmp.doInterpret();
                interpreter = tmp;
                return true;
            }
        }
        return false;
    }


    public boolean parseDelete(String[] command) throws Exception {
        //"DELETE FROM " <TableName> " WHERE " <Condition>
        if(commandSize < 5 ||!command[1].equalsIgnoreCase("FROM")){
            return false;
        }
        if(!isTableName(command[2]) || !command[3].equalsIgnoreCase("WHERE")){
            return false;
        }
        ArrayList<String> conditionList = new ArrayList<>(Arrays.asList(command).subList(4, commandSize));
        if(isConditionLists(conditionList)){
            InterpretDelete tmp = new InterpretDelete();
            for(int i = 0; i < interpreter.getValueList().size(); i++){
                tmp.setValueList(interpreter.getValueList().get(i));
            }
            for(int i = 0; i < interpreter.getOperatorList().size(); i++){
                tmp.setOperatorList(interpreter.getOperatorList().get(i));
            }
            for(int i = 0; i < interpreter.getCondRelation().size(); i++){
                tmp.setCondRelation(interpreter.getCondRelation().get(i));
            }
            for(int i = 0; i < interpreter.getAttributeName().size(); i++){
                tmp.setAttributeName(interpreter.getAttributeName().get(i));
            }
            tmp.setDatabaseUsing(databaseUsing);
            tmp.setDirectoryPath(directoryPath);
            tmp.setTableName(command[2]);
            tmp.setStringList(stringList);
            tmp.doInterpret();
            interpreter = tmp;
            return true;
        }
        return false;
    }


    public boolean parseJoin(String[] command) throws Exception {
        //"JOIN " <TableName> " AND " <TableName> " ON " <AttributeName> " AND " <AttributeName>
        if(commandSize != 8){
            return false;
        }
        if(isTableName(command[1]) && isTableName(command[3])){
            String conjunction = command[2];
            conjunction = conjunction.concat(command[4]);
            conjunction = conjunction.concat(command[6]);
            if(conjunction.equalsIgnoreCase("ANDONAND")){
                if(isAttributeName(command[5]) && isAttributeName(command[7])){
                    InterpretJoin tmp = new InterpretJoin();
                    for(int i = 0; i < interpreter.getAttributeName().size(); i++){
                        tmp.setAttributeName(interpreter.getAttributeName().get(i));
                    }
                    tmp.setDatabaseUsing(databaseUsing);
                    tmp.setDirectoryPath(directoryPath);
                    tmp.setTableName(command[1]);
                    tmp.setTableName(command[3]);
                    tmp.doInterpret();
                    interpreter = tmp;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isStructure(String command){
        //"DATABASE" | "TABLE"
        return command.equalsIgnoreCase("DATABASE") || command.equalsIgnoreCase("TABLE");
    }



    public boolean isLetter(Character name){
        if(!Character.isUpperCase(name)){
            return Character.isLowerCase(name);
        }
        return true;
    }


    public boolean isPlainText(String command){
        //<Letter> | <Digit> | <Letter> <PlainText> | <Digit> <PlainText>
        int length = command.length();
        for(int i = 0; i < length; i++){
            if(!isLetter(command.charAt(i))){
                if(!Character.isDigit(command.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isSymbol(Character command){
        String symbols = "/*!@#$%^&*()\"{}_[]|\\?/<>,.+-=:;`~";
        for(int i = 0; i < symbols.length(); i++){
            if(symbols.charAt(i) == command){
                return true;
            }
        }
        return false;
    }


    public boolean isSpace(Character command){
        return command == ' ';
    }


    public boolean isDigitSequence(String command){
        try {
            Integer.parseInt(command);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    public boolean isIntegerLiteral(String command){
        //<DigitSequence> | "-" <DigitSequence> | "+" <DigitSequence>
        if(isDigitSequence(command)){
            return true;
        }
        if(command.charAt(0) == '-' || command.charAt(0) == '+'){
            return isDigitSequence(command.substring(1));
        }
        return false;
    }


    public boolean isFloatLiteral(String command){
        //<DigitSequence> "." <DigitSequence> | "-" <DigitSequence> "." <DigitSequence> | "+" <DigitSequence> "." <DigitSequence>
        if(!command.contains(".")){
            return false;
        }
        String[] tmp = command.split("\\.", 2);
        if(command.charAt(0) == '-' || command.charAt(0) == '+'){
            if(!isDigitSequence(tmp[0].substring(1))){
                return false;
            }
        }
        else{
            if(!isDigitSequence(tmp[0])){
                return false;
            }
        }
        return isDigitSequence(tmp[1]);
    }


    public boolean isBooleanLiteral(String command){
        //"TRUE" | "FALSE"
        if(!command.equalsIgnoreCase("TRUE")){
            return command.equalsIgnoreCase("FALSE");
        }
        return true;
    }


    public boolean isCharLiteral(Character command){
        //<Space> | <Letter> | <Symbol>
        if(!isSpace(command)){
            if(!isLetter(command)){
                return isSymbol(command);
            }
        }
        return true;
    }


    public boolean isStringLiteral(String command){
        //"" | <CharLiteral> | <CharLiteral> <StringLiteral>
        if(!command.equals("")){
            for(int i = 0; i < command.length(); i++){
                if(!isCharLiteral(command.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isValue(String command){
        //"'" <StringLiteral> "'" | <BooleanLiteral> | <FloatLiteral> | <IntegerLiteral> | "NULL"
        if(command.equalsIgnoreCase("NULL")){
            interpreter.setValueList("NULL");
            return true;
        }
        if(command.charAt(0) == '\'' && command.endsWith("'")){
            int index = command.length() - 1;
            String commandOrigin = command;
            command = command.substring(1,index);
            if(isStringLiteral(command)){
                valuesList.add(commandOrigin);
                interpreter.setValueList(commandOrigin);
                return true;
            }
        }
        if(isBooleanLiteral(command)){
            interpreter.setValueList(command);
            valuesList.add(command);
            return true;
        }
        if(isFloatLiteral(command)){
            interpreter.setValueList(command);
            valuesList.add(command);
            return true;
        }
        if(isIntegerLiteral(command)){
            interpreter.setValueList(command);
            valuesList.add(command);
            return true;
        }
        return false;
    }


    public boolean isValueList(String command){
        // <Value> | <Value> "," <ValueList>
        String[] values = command.split(",");
        for (String s : values) {
            if (!isValue(s)) {
                return false;
            }
        }
        return true;
    }


    public boolean isAttributeName(String command){
        if(isPlainText(command)){
            interpreter.setAttributeName(command);
            return true;
        }
        return false;
    }


    public boolean isTableName(String command){
        return isPlainText(command);
    }


    public boolean isDatabaseName(String command){
        return isPlainText(command);
    }


    public boolean isAlterationType(String command){
        //"ADD" | "DROP"
        if(!command.equalsIgnoreCase("ADD")){
            return command.equalsIgnoreCase("DROP");
        }
        return true;
    }


    public boolean isAttributeList(ArrayList<String> command){
        //  <AttributeName> | <AttributeName> "," <AttributeList>
        int size = command.size();
        String attributeString = command.get(0);
        for(int i = 1; i < size; i++){
            attributeString = attributeString.concat(command.get(i));
        }
        String[] attribList = attributeString.split(",");
        attributeList = new ArrayList<>();
        for (String s : attribList) {
            if (!isAttributeName(s)) {
                return false;
            }
            attributeList.add(s);
        }
        return true;
    }


    public boolean isNameValuePair(String command){
        //<AttributeName> "=" <Value>
        if(command.contains("=")){
          String[] commands = command.split("=", 2);
          return isAttributeName(commands[0]) && isValue(commands[1]);
        }
        return false;
    }


    public boolean isNameValueList(ArrayList<String> command){
        //<NameValuePair> | <NameValuePair> "," <NameValueList>
        int size = command.size();
        String nameValueString = command.get(0);
        for(int i = 1; i < size; i++){
            nameValueString = nameValueString.concat(command.get(i));
        }
        String[] listOfNameValue = nameValueString.split(",");
        for (String s : listOfNameValue) {
            if (!isNameValuePair(s)) {
                return false;
            }
        }
        return true;
    }


    public boolean isWildAttribList(ArrayList<String> command){
        //<AttributeList> | "*"
        if(command.size() == 1){
            if(command.get(0).equals("*")){
                attributeList.add("*");
                return true;
            }
        }
        return isAttributeList(command);
    }


    public boolean isConditionLists(ArrayList<String> command){
        //"(" <Condition> ")AND(" <Condition> ")" | "(" <Condition> ")OR(" <Condition> ")" | <AttributeName> <Operator> <Value>
        int size = command.size();
        String conditionString = command.get(0);
        for(int i = 1; i < size; i++){
            conditionString = conditionString.concat(command.get(i));
        }
        return isConditionList(conditionString);
    }

    public boolean isCondition(String command){
        //<AttributeName> <Operator> <Value>
        int length = command.length();
        String[] condition;
        if(command.contains("LIKE")){
            interpreter.setOperatorList("LIKE");
            condition = command.split("LIKE");
            return isAttributeName(condition[0]) && isValue(condition[1]);
        }
        else if(command.contains("like")){
            interpreter.setOperatorList("LIKE");
            condition = command.split("like");
            return isAttributeName(condition[0]) && isValue(condition[1]);
        }
        else{
            for(int i = 0; i < length; i++){
                if(!isLetter(command.charAt(i))){
                    if(i+2 < length) {
                        String doubleOp = command.substring(i, i + 2);
                        if(isOperator(doubleOp)) {
                            interpreter.setOperatorList(doubleOp);
                            condition = command.split(doubleOp);
                            return isAttributeName(condition[0]) && isValue(condition[1]);
                        }
                    }
                    String singleOp = String.valueOf(command.charAt(i));
                    if(isOperator(singleOp)){
                        interpreter.setOperatorList(singleOp);
                        condition = command.split(singleOp);
                        return isAttributeName(condition[0]) && isValue(condition[1]);
                    }
                }
            }
        }
        return false;
    }

    public boolean isConditionList(String conditionString){
        int bracketCount = 1;
        int preConditionIndex = 0;
        int postConditionIndex = 0;
        if(conditionString.charAt(0)=='(' && conditionString.charAt(conditionString.length()-1)==')'){
            for(int i = 1; i < conditionString.length(); i++){
                if(conditionString.charAt(i)=='('){
                    bracketCount++;
                }
                else if(conditionString.charAt(i)==')'){
                    bracketCount--;
                }

                if(bracketCount==0){
                    preConditionIndex = i;
                    postConditionIndex = i + conditionString.substring(i).indexOf('(');

                    String preCondition = conditionString.substring(1, preConditionIndex);
                    String postCondition = conditionString.substring(postConditionIndex+1, conditionString.length()-1);
                    String condition = conditionString.substring(preConditionIndex+1, postConditionIndex).strip();
                    interpreter.setCondRelation(condition);
                    return isConditionList(preCondition) && isConditionList(postCondition);
                }

            }
        }
        else {
            return isCondition(conditionString);
        }
        return false;   // bracketCount != 0
    }


    public boolean isOperator(String command){
        //"==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
        return switch (command) {
            case "==", "LIKE", ">", "<", ">=", "<=", "!=" -> true;
            default -> false;
        };
    }

    public DBcmd getInterpreter() {
        return interpreter;
    }
}
