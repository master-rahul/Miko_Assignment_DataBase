package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class main {
	
	// Inserting the Values Of Query into the respective table
	public static void addStringToEndOfFile(String filePath, String[] array) throws IOException {
		
		// File object for table file
        File inputFile = new File(filePath);
        // File object for newly created file
        File tempFile = new File("temp.txt");
        
        // Reading files using BufferedReader
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        
        String line;
        int index = 0;
        
        // Adding lines from the table and array in temp.txt
        while ((line = reader.readLine()) != null) {
            if (index < array.length) line +=  array[index]+", ";
            writer.write(line);
            writer.newLine();
            index++;
        }
        reader.close();
        writer.close();
        
        // Replacing the table file with the modified file temp.txt
        if (inputFile.delete()) tempFile.renameTo(inputFile);
        else throw new IOException("Failed to replace the original file with new file");
    }
	
	// Verifying whether the column names given in Insert Query is Correct
	public static String checkValidityOfColumns(String[] tableData, String[] columnData) {
		
		//for(String ss : tableData) System.out.println(ss);
		//for(String ss : columnData) System.out.println(ss);
		
		// Checks whether the number of columns in query is less than or equal to number of  columns in table 
    	if(tableData.length < columnData.length) return "*Column Not Present in Table In Query*\n";
    	
    	// Converting the table data into Hashmap having key as column name
    	Map<String, Integer> map = new HashMap<>();
    	
    	// Populating the HashMap
    	for(String columName : tableData) {
    		columName = columName.substring(0, columName.indexOf(":")-1);
    		map.put(columName, 0);
    	}
    	
    	// Verifying the column names present in query to the keys of Hashmap which are actual column name of table
    	for(String columnName  : columnData) if(!map.containsKey(columnName)) return "*Invalid Column for Table In Query*\n";
		return null;
	}
	
	//Verifying the DataType of values given in the Insert query via metaFile
	public static String checkValidityOfValues(String[] metaData, String[] valueData, String[] columnData) {
		
		//for(String ss : metaData) System.out.println(ss);
    	//for(String ss : valueData) System.out.println(ss);
		
		//Verifying whether the number of columns and number of values matches in Query
    	if(columnData.length != valueData.length) return "*Values for Unknown Column Present In Query*\n";
    	
    	// Converting the metadata into Hashmap having key as column name
    	Map<String, Integer> map = new HashMap<>();
    	
    	// Populating the HashMap with key as column name and value with index or position of column in file
    	for(int i = 0; i < metaData.length; i++) {
    		String columName = metaData[i];
    		columName = columName.substring(columName.indexOf("[")+1, columName.indexOf(":"));
    		map.put(columName, i);
    	}
    	
    	// Iterating through with the columns present in columnData
    	for(int i =0; i < columnData.length; i++) {
    		
    		// Checking whether the map contains the columnData item
    		if(map.containsKey(columnData[i])) {
    			
    			// We get the index of the column which is the line at which this column is present
    			int index = map.get(columnData[i]);
    			
    			// Finding the DataType of column from the metaData Array
    			String type = metaData[index].substring(metaData[index].indexOf(":")+1, metaData[index].indexOf("]"));
    			
    			// Verifying the type of data in Value Array to the type we found for the column in meta file.
    			if(valueData[i].charAt(0) =='\"' && type.charAt(0) != 'i') {      	// Checks for String or Char but not Integer
    				if(valueData[i].length() > 3 &&type.charAt(0) == 's' ) {	  	// Checks for String
    					continue;
    				}else if(valueData[i].length() == 3 &&type.charAt(0) == 'c'){	// Checks for Character
    					continue;
    				}else {
    					return "*Invalid Type For Value in Query*\n";
    				}
    			}else if(valueData[i].charAt(0) != '\"' && type.charAt(0) == 'i') {	// Checks for not String and Character but Integer
    				continue;
    			}else {
    				return "*Invalid Type For Value in Query*\n";
    			}
    		}
    	}
		return null;
	}
	
	// Read Files Line By Line
	public static String[] fileToArray(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path).toArray(new String[0]);
    }
	// Check whether the tableFile or metaTableFiles is present or not.
	public static boolean ifFileExists(String directoryPath, String fileName) {
        File directory = new File(directoryPath);
        File file = new File(directory, fileName);
        return file.exists();
    }
	
	// To remove additional spaces between characters or words, reduces to single space.
	 public static String reduceSpaces(String text) {
	        String reducedText = text.replaceAll("\\s+", " ");
	        return reducedText;
	    }
	 
	 public static String checkDataType(String value[][]) {
		 for(String a[] : value) {
			 if(a[1].toLowerCase().equals("integer") ||  a[1].toLowerCase().equals("string") || a[1].toLowerCase().equals("char")) continue;
			 else return "The DataTypes for the columns are invalid\n";
			
		 }
		 return null;
	 }
	 
	// Converting the column_names and data_types to 2D Array 
	public static String[][] convertTo2DArray(String input) {
		 // Spliting by commas
        String[] firstSplit = input.split(",");
        String[][] result = new String[firstSplit.length][];
        for (int i = 0; i < firstSplit.length; i++) {
        	// Split each element by spaces
            String[] secondSplit = firstSplit[i].trim().split(" "); 
            result[i] = new String[secondSplit.length];
            for (int j = 0; j < secondSplit.length; j++) {
                result[i][j] = secondSplit[j];
            }
        }
        return result;
    }
	
	public static String parenthesisCheck(String query) {
		Deque<Integer> stack = new ArrayDeque<>();
		int parenthesisCount= 0;
		for(int i = 0; i < query.length(); i++) {
			if(query.charAt(i) == '(') {
				stack.push(i);
				parenthesisCount++;
			}
			else if(query.charAt(i) ==')') {
				if(stack.isEmpty()) return "Error In Parenthesis At : "+ (7+i);
				stack.pop();
			}
		}
		if (!stack.isEmpty()) return "Error In Parenthesis At : "+ (7+stack.pop());
		else return null;
	}
	
	public static String createQuery(String query) {
		// For checking the validity of a query
		String verifcation = parenthesisCheck(query);
		if(verifcation != null) return verifcation;
		int index = query.indexOf('(');
		String tableName = "";
        if (index != -1) tableName = query.substring(0, index).trim() + ".txt";  
        else return "Please re-enter a correct Table Name : \n";
        
        // CREATING THE FILE :
        String currentDirectory = System.getProperty("user.dir");
        String tablePath = currentDirectory + "/database/" + tableName;
        String metaPath = currentDirectory + "/database/meta_" + tableName;
        int startIndex = query.indexOf('(');
        int endIndex = query.indexOf(')');
        query = query.substring(startIndex+1, endIndex).trim();
       // System.out.println(query);
        String queryColumns[][] = convertTo2DArray(query);
        String checkDataTypes = checkDataType(queryColumns);
        if(checkDataTypes != null) return checkDataTypes;
        System.out.println(query);
        for(String a[] : queryColumns) {
        	for(String cc  : a) System.out.print(cc+",");
        	System.out.println();
        }
        try (FileWriter tableWriter = new FileWriter(tablePath); FileWriter metaWriter = new FileWriter(metaPath)) {
        	for(String listItem[] : queryColumns) {
        		metaWriter.write("[" + listItem[0] + ":" + listItem[1] + "]\n");
        		tableWriter.write(""+listItem[0] + " : \n");
        	}
        	metaWriter.flush();     
        	tableWriter.flush();
            System.out.println("Table created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
		return null;
	}
	
	
	
	
	
	
	
	
	public static String insertQuery(String query) {
		if(!query.substring(0, 4).toLowerCase().equals("into")) return "****The Insert Query Syntax is Incorrect****\n";
		query = query.substring(5);
		System.out.println(query);
		String verifcation = parenthesisCheck(query);
		if(verifcation != null) return verifcation;
		int index = query.indexOf('(');
		String tableName = "";
		String metaName = "";
        if (index != -1) {
        	tableName = query.substring(0, index).trim() + ".txt";  
        	metaName ="meta_"+ query.substring(0, index).trim() + ".txt";  
        }
        else return "Please re-enter a correct Table Name : \n";
        String tableDirectory = System.getProperty("user.dir") +"/database";
        if(ifFileExists(tableDirectory, tableName)) {
        	try {
	        	int startIndex = query.indexOf('(');
	            int endIndex = query.indexOf(')');
	            String[] columnArr = query.substring(startIndex+1, endIndex).trim().split(",");
	            query = query.substring(endIndex+1);
	            startIndex = query.indexOf('(');
	            endIndex = query.indexOf(')');
	            String[] valueArr = query.substring(startIndex +1, endIndex).split(",");
            	String tableData[] = fileToArray(tableDirectory+"/"+tableName);
            	String metaData[] = fileToArray(tableDirectory+"/"+metaName);
            	for(String d : tableData) System.out.println(d);
            	for(String d : metaData) System.out.println(d);
            	for(int i = 0; i < valueArr.length; i++) valueArr[i] = valueArr[i].trim();
            	for(int i = 0; i < columnArr.length; i++) columnArr[i] = columnArr[i].trim();
            	//for(String ss : columnArr) System.out.println(ss);
            	//for(String ss : valueArr) System.out.println(ss);
            	String validity = checkValidityOfColumns(tableData, columnArr);
            	if(validity != null) return validity;
            	validity = checkValidityOfValues(metaData, valueArr, columnArr);
            	if(validity != null) return validity;
            	// Column Values To be inserted in Table
            	String value[] = new String[tableData.length];
            	for (int i = 0; i < value.length; i++)value[i] = ""; 
            	Map<String, Integer> map = new HashMap<>();
            	for(int i = 0; i < metaData.length; i++) {
            		String columName = metaData[i];
            		columName = columName.substring(columName.indexOf("[")+1, columName.indexOf(":"));
            		map.put(columName, i);
            	}
            	for(int i =0; i < columnArr.length; i++) {
            		if(map.containsKey(columnArr[i])) {
            			int pos = map.get(columnArr[i]);
            			value[pos] = valueArr[i];
            		}
            	}
            	System.out.println("Values to Be Inserted");
            	for(String sst  : value) System.out.print(sst);
            	addStringToEndOfFile(tableDirectory+"/"+tableName, value);
            }catch(IOException e) {
            	 System.out.println("An error occurred while reading the file: " + e.getMessage());
            }
        	
        }else return "*****The Table is not Present, Please Create First *****\n";
        
		
		return null;	
	}
	
	
	
	
	
	public static String updateQuery(String query) {
		String verifcation = parenthesisCheck(query);
		
		return null;
	}
	public static String deleteQuery(String query) {
		String verifcation = parenthesisCheck(query);
		
		return null;
	}
	
	
	
	public static void main(String args[]) {
		
		// Initializing Scanner for taking text input form console.
		Scanner s = new Scanner(System.in);
		
		System.out.print("WELCOME TO FILE BASED DATABASE SYSTEM USING JAVA\n");
		
		// For Storing Path of Table Files
		ArrayList<String> tables = new ArrayList<>();
		// For Storing Path of Table Metadata Files
		ArrayList<String> metadata = new ArrayList<>();
		while(true) {
			System.out.print("Please Enter Your Query Or Type Exit to terminate : \n");
			String query = s.nextLine();
			 query = reduceSpaces(query);
			// TERMINATES THE PROGRAM
			if(query.toLowerCase().equals("exit")) {   
				System.out.println("################## THANKS FOR USING OUR SIMPLE DATABSE ##################");
				break;
			}else {
				if(query.length() < 7) {
					System.out.println("Please Enter Remaining Query : ");
					continue;
				}
				// For Storing Replies
				String reply = "";
				// Converts the command into understandable options like CREATE, UPDATE, DELETE, INSERT
				switch(query.substring(0,7).toLowerCase()) {
					
					// For creating a table
					case "create " : 
						reply = createQuery(query.substring(7));
						if(reply == null) System.out.println("!!!!!!!!Congrats Your table is created successfully!!!!!!!!\n");
						else System.out.println(reply);
						break;
					// For inserting into table
					case "insert " : 
						reply = insertQuery(query.substring(7));
						System.out.println(reply);
						
						break;
					// For updating table
					case "update " : 
						reply = updateQuery(query.substring(7));
						System.out.println(reply);
						
						break;
					// For deleting from table
					case "delete " : 
						reply = deleteQuery(query.substring(7));
						System.out.println(reply);
	
						break;
					
					default :
						System.out.println("Please Choose a Correct DDL or DML command : ");
				
				}
			}
		}
		
	}

}
