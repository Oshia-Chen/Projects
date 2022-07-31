package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class DBcmd {
    private String databaseUsing;
    private String directoryPath;
    private String tableUsing;
    private String databaseName;
    private String structure;
    private String structureName;
    private String alterationType;
    private String result;
    private ArrayList<String> stringList;
    private final ArrayList<String> valueList;
    private final ArrayList<String> operatorList;
    private final ArrayList<String> conditionRelation;
    private ArrayList<String> attributeList;
    private ArrayList<String> wildAttribList;
    private final ArrayList<String> attributeName;
    private final ArrayList<String> tableName;

    public DBcmd() {
        attributeName = new ArrayList<>();
        tableName = new ArrayList<>();
        wildAttribList = new ArrayList<>();
        attributeList  = new ArrayList<>();
        valueList = new ArrayList<>();
        operatorList = new ArrayList<>();
        conditionRelation  = new ArrayList<>();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String output) {
        result =  output;
    }

    public void setDatabaseName(String name) {
        databaseName = name;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDirectoryPath(String path) {
        directoryPath = path;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setStructure(String command) {
        structure = command;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructureName(String name) {
        structureName = name;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setAlterationType(String type) {
        alterationType = type;
    }

    public String getAlterationType() {
        return alterationType;
    }

    public void setValueList(String value) {
        valueList.add(value);
    }

    public ArrayList<String> getValueList() {
        return valueList;
    }

    public void setCondRelation(String relation) {
        conditionRelation.add(relation);
    }

    public ArrayList<String> getCondRelation() {
        return conditionRelation;
    }

    public void setStringList(ArrayList<String> string) {
        stringList = string;
    }

    public ArrayList<String> getStringList() {
        return stringList;
    }

    public void setTableName(String name) {
        tableName.add(name);
    }

    public ArrayList<String> getTableName() {
        return tableName;
    }

    public void setAttributeName(String name) {
        attributeName.add(name);
    }

    public ArrayList<String> getAttributeName() {
        return attributeName;
    }

    public void setAttribList(ArrayList<String> attribute) {
        attributeList = attribute;
    }

    public ArrayList<String> getAttribList() {
        return attributeList;
    }

    public void setOperatorList(String operator) {
        operatorList.add(operator);
    }

    public ArrayList<String> getOperatorList() {
        return operatorList;
    }

    public void setWildAttribList(ArrayList<String> attribute) {
        wildAttribList = attribute;
    }

    public ArrayList<String> getWildAttribList() {
        return wildAttribList;
    }

    public String getDatabaseUsing() {
        return databaseUsing;
    }

    public void setDatabaseUsing(String name) {
        databaseUsing = name;
    }

    public String getTableUsing() {
        return tableUsing;
    }

    public void setTableUsing(String name) {
        tableUsing = name;
    }

    public void checkDatabaseUsing(String databaseUsing) throws Exception {
        if(databaseUsing == null) {
            throw new Exception("Use database before using any table.");
        }
    }
    public File newTableFile(String tableName, String databaseUsing) throws Exception {
        String databaseDirectory = getDirectoryPath();
        String pathOfTable = databaseDirectory + File.separator + databaseUsing + File.separator +tableName;
        File table = new File(pathOfTable);
        if(!table.exists()){
            throw new Exception("The table name doesn't exist in database.");
        }
        return table;
    }
    public BufferedReader createTableReader(File name) throws FileNotFoundException {
        FileReader reader = new FileReader(name);
        return new BufferedReader(reader);
    }

    public BufferedWriter createTableWriter(File name) throws IOException {
        FileWriter writer = new FileWriter(name);
        return new BufferedWriter(writer);
    }


    public ArrayList<String> readTableInLine(BufferedReader buffReader) throws Exception {
        ArrayList<String> tableLine = new ArrayList<>();
        String tmp = buffReader.readLine();
        while (tmp != null && !tmp.equals("") && !tmp.equals("\t")) {
            tableLine.add(tmp);
            tmp = buffReader.readLine();
        }
        buffReader.close();
        if (tableLine.size() == 0) {
            throw new Exception("The table is empty.");
        }
        return tableLine;
    }

    public ArrayList<ArrayList<String>> readTableToList(ArrayList<String> tableLine){
        int rowNum = tableLine.size();
        ArrayList<ArrayList<String>> tableContent = new ArrayList<>();
        for (int i = 0; i < rowNum; i++) {
            String[] tmpArray = tableLine.get(i).split("\t");
            ArrayList<String> valuesOfLine = new ArrayList<>();
            for (String value : tmpArray) {
                if (!value.equals("") && !value.equals(" ")) {
                    valuesOfLine.add(value);
                }
            }
            tableContent.add(new ArrayList<>());
            for (String s : valuesOfLine) {
                if (s != null) {
                    tableContent.get(i).add(s);
                }
            }
        }
        return tableContent;
    }

    public ArrayList<String> replaceRetainString() {
        int indexOfStrList = 0;
        ArrayList<String> result = getValueList();
        if(getStringList() != null){
            ArrayList<String> stringList = getStringList();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).contains("'")) {
                    result.set(i, stringList.get(indexOfStrList));
                    indexOfStrList++;
                }
            }
        }
        return result;
    }

    public void isBoolOrNull(String command) throws Exception {
        if(!command.equalsIgnoreCase("TRUE")){
            if(!command.equalsIgnoreCase("FALSE")){
                if(!command.equalsIgnoreCase("NULL")){
                    return;
                }
            }
        }
        throw new Exception("NULL or Boolean cannot be compared by size.");
    }

    public void writeToFile(File tableFile, int rowNum, ArrayList<ArrayList<String>> tableContent) throws IOException {
        BufferedWriter buffWriter = createTableWriter(tableFile);
        StringBuilder newTable = new StringBuilder();
        for (int i = 0; i < rowNum; i++) {
            for(int j = 0; j < tableContent.get(i).size(); j++){
                newTable.append(tableContent.get(i).get(j)).append("\t");
            }
            newTable.append("\n");
        }
        buffWriter.write(String.valueOf(newTable));
        buffWriter.flush();
        buffWriter.close();
        setResult(" ");
    }

    public int findIndexOfAttrib(ArrayList<ArrayList<String>> tableContent, String attribName) throws Exception {
        int indexOfAttrib = 0;
        boolean flag = false;
        for(int i = 0; i < tableContent.get(0).size(); i++){
            if(tableContent.get(0).get(i).equalsIgnoreCase(attribName)){
                indexOfAttrib = i;
                flag = true;
            }
        }
        if(!flag){
            throw new Exception("The attribute doesn't exist in the table.");
        }
        return indexOfAttrib;
    }

    public ArrayList<Integer> findMatchLine(int indexOfList, ArrayList<ArrayList<String>> tableContent, ArrayList<String> valueListNoQuote) throws Exception {
        //"==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
        ArrayList<Double> tableToDouble = new ArrayList<>();
        //condition's value, attribute name and operator
        String operator = getOperatorList().get(indexOfList);
        String attribName = getAttributeName().get(indexOfList);
        String value = valueListNoQuote.get(indexOfList);
        Double ValueToDouble = null;
        boolean ListIsNumber = false;
        boolean ValueIsNumber = false;
        //condition's attribute name index of the table
        int indexOfAttrib = findIndexOfAttrib(tableContent, attribName);

        for (int i = 1; i < tableContent.size(); i++) {
            if (Character.isDigit(tableContent.get(i).get(indexOfAttrib).charAt(0))) {
                tableToDouble.add(Double.parseDouble(tableContent.get(i).get(indexOfAttrib)));
                ListIsNumber = true;
            }
        }
        //System.out.println(tableToDouble);
        if (Character.isDigit(value.charAt(0))) {
            ValueToDouble = Double.parseDouble(value);
            ValueIsNumber = true;
        }

        return compareValues(indexOfAttrib, ValueToDouble, ListIsNumber, ValueIsNumber, operator, value, tableToDouble, tableContent);
    }

    public ArrayList<Integer> compareValues(int indexOfAttrib, Double ValueToDouble, boolean ListIsNumber, boolean ValueIsNumber, String operator, String value, ArrayList<Double> tableToDouble, ArrayList<ArrayList<String>> tableContent) throws Exception {
        ArrayList<Integer> matchLineIndex = new ArrayList<>();
        switch (operator) {
            case "==" -> {
                for (int i = 1; i < tableContent.size(); i++) {
                    if (tableContent.get(i).get(indexOfAttrib).equals(value)) {
                        matchLineIndex.add(i);
                    }
                }
            }
            case "!=" -> {
                for (int i = 1; i < tableContent.size(); i++) {
                    if (!tableContent.get(i).get(indexOfAttrib).equals(value)) {
                        matchLineIndex.add(i);
                    }
                }
            }
            case ">" -> {
                isBoolOrNull(value);
                if (ListIsNumber != ValueIsNumber) {
                    throw new Exception("Different type cannot be compared by size.");
                }
                if (ListIsNumber) {
                    for (int i = 0; i < tableToDouble.size(); i++) {
                        if (tableToDouble.get(i) > ValueToDouble) {
                            matchLineIndex.add(i + 1);
                        }
                    }
                } else {
                    for (int i = 1; i < tableContent.size(); i++) {
                        if (value.compareTo(tableContent.get(i).get(indexOfAttrib)) < 0) {
                            matchLineIndex.add(i);
                        }
                    }
                }
            }
            case "<" -> {
                isBoolOrNull(value);
                if (ListIsNumber != ValueIsNumber) {
                    throw new Exception("Different type cannot be compared by size.");
                }
                if (ListIsNumber) {
                    for (int i = 0; i < tableToDouble.size(); i++) {
                        if (tableToDouble.get(i) < ValueToDouble) {
                            matchLineIndex.add(i + 1);
                        }
                    }
                } else {
                    for (int i = 1; i < tableContent.size(); i++) {
                        if (value.compareTo(tableContent.get(i).get(indexOfAttrib)) > 0) {
                            matchLineIndex.add(i);
                        }
                    }
                }
            }
            case ">=" -> {
                isBoolOrNull(value);
                if (ListIsNumber != ValueIsNumber) {
                    throw new Exception("Different type cannot be compared by size.");
                }
                if (ListIsNumber) {
                    for (int i = 0; i < tableToDouble.size(); i++) {
                        if (tableToDouble.get(i) >= ValueToDouble) {
                            matchLineIndex.add(i + 1);
                        }
                    }
                } else {
                    for (int i = 1; i < tableContent.size(); i++) {
                        if (value.compareTo(tableContent.get(i).get(indexOfAttrib)) < 0 || value.compareTo(tableContent.get(i).get(indexOfAttrib)) == 0) {
                            matchLineIndex.add(i);
                        }
                    }
                }
            }
            case "<=" -> {
                isBoolOrNull(value);
                if (ListIsNumber != ValueIsNumber) {
                    throw new Exception("Different type cannot be compared by size.");
                }
                if (ListIsNumber) {
                    for (int i = 0; i < tableToDouble.size(); i++) {
                        if (tableToDouble.get(i) <= ValueToDouble) {
                            matchLineIndex.add(i + 1);
                        }
                    }
                } else {
                    for (int i = 1; i < tableContent.size(); i++) {
                        if (value.compareTo(tableContent.get(i).get(indexOfAttrib)) > 0 || value.compareTo(tableContent.get(i).get(indexOfAttrib)) == 0) {
                            matchLineIndex.add(i);
                        }
                    }
                }
            }
            case "LIKE" -> {
                isBoolOrNull(value);
                if (ListIsNumber || ValueIsNumber) {
                    throw new Exception("Number cannot be compared by LIKE.");
                }
                for (int i = 1; i < tableContent.size(); i++) {
                    if (tableContent.get(i).get(indexOfAttrib).contains(value)) {
                        matchLineIndex.add(i);
                    }
                }
            }
        }
        return matchLineIndex;
    }

    public ArrayList<Integer> calculateAndOr (int indexOfOP, ArrayList<Integer> matchLineIndex1, ArrayList<Integer> matchLineIndex2) throws Exception {
        ArrayList<Integer> finalResult = new ArrayList<>(matchLineIndex1);
        String relation = getCondRelation().get(indexOfOP);
        if(relation.equalsIgnoreCase("AND")){
            finalResult.retainAll(matchLineIndex2);
        }
        else if(relation.equalsIgnoreCase("OR")){
            finalResult.removeAll(matchLineIndex2);
            finalResult.addAll(matchLineIndex2);
        }
        else{
            throw new Exception("Only can use AND / OR with two condition or single condition in the condition query.");
        }
        Collections.sort(finalResult);
        return finalResult;
    }

    public ArrayList<Integer> matchAllCondition(ArrayList<ArrayList<String>> tableContent, ArrayList<String> valueLists) throws Exception {
        ArrayList<Integer> finalResult;
        if(getCondRelation().size() == 0) {
            //System.out.println("reach getCondRelation");
            finalResult = findMatchLine(0, tableContent, valueLists);
        }else{
            ArrayList<Integer> matchLineIndex1 = findMatchLine(0, tableContent, valueLists);
            ArrayList<Integer> matchLineIndex2 = findMatchLine(1, tableContent, valueLists);
            finalResult = calculateAndOr(0, matchLineIndex1, matchLineIndex2);
            for(int i = 2; i < valueLists.size()-1; i++) {
                matchLineIndex1 = findMatchLine(i, tableContent, valueLists);
                finalResult = calculateAndOr(i-1, matchLineIndex1, finalResult);
            }
        }
        return finalResult;
    }

}

