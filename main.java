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
	
	// Converting Each Line of a File into an element in String Array
	public static String[] fileToArray(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path).toArray(new String[0]);
    }
	
	// Verifying whether the table file and table meta file exits or not
	public static boolean ifFileExists(String directoryPath, String fileName) {
        File directory = new File(directoryPath);
        File file = new File(directory, fileName);
        return file.exists();
    }
	
	// Removes additional Spaces from the Query, making maximum 1 space amongst and words or characters.
	 public static String reduceSpaces(String text) {
        String reducedText = text.replaceAll("\\s+", " ");
        return reducedText;
	 }
	 
	 // Verifying the DataType of the columns given in the Query
	 public static String checkDataType(String value[][]) {
		 for(String a[] : value) {
			 if(a[1].toLowerCase().equals("integer") ||  a[1].toLowerCase().equals("string") || a[1].toLowerCase().equals("char")) continue;
			 else return "*DataTypes Invalid for Columne*\n";
		 }
		 return null;
	 }
	 
	// Converting the Column Name and Column DataTypes into a 2D array with 2 column of column_name and data_type respectively
	public static String[][] convertTo2DArray(String query) {
		
		// Splitting the query into 1D array based on comma
        String[] firstSplit = query.split(",");
        
        // Creating a 2D array of length based on number of column 
        String[][] result = new String[firstSplit.length][];
        
        //Populating the 2D array by splitting each element of 1D array based on space
        for (int i = 0; i < firstSplit.length; i++) {
        	// Split each element by spaces
            String[] secondSplit = firstSplit[i].trim().split(" "); 
            // System.out.println("Splitting column and datatype");
            //System.out.println(secondSplit[0] +" :: "+ secondSplit[1]);
            result[i] = new String[secondSplit.length];
            for (int j = 0; j < secondSplit.length; j++) {
                result[i][j] = secondSplit[j];
            }
        }
        return result;
    }
	
	// Validating the parenthesis for the query
	public static String parenthesisCheck(String query) {
		// Deque for inserting the index at which the parenthesis occurs.
		Deque<Integer> stack = new ArrayDeque<>();
		int parenthesisCount= 0;
		for(int i = 0; i < query.length(); i++) {
			
			// Checks for opening parenthesis
			if(query.charAt(i) == '(') {
				stack.push(i);
				parenthesisCount++;
			} // Checks for closing parenthesis
			else if(query.charAt(i) ==')') {
				if(stack.isEmpty()) return "*Error In Parenthesis*\n";
				stack.pop();
			}
		}
		if (!stack.isEmpty()) return "*Error In Parenthesis*\n";
		if(parenthesisCount != 1) return "*Error In Parenthesis*\n";
		else return null;
	}
	
	// Creating a Table via CreateQuery
	public static String createQuery(String query) {
		
		// Verifying The Parenthesis in the Query
		String verifcation = parenthesisCheck(query);
		if(verifcation != null) return verifcation;
		
		// Fetching the table_name from the query
		int index = query.indexOf('(');
		String tableName = "";
        if (index != -1) tableName = query.substring(0, index).trim() + ".txt";  
        else return "*Please re-enter a correct Table Name : *\n";
        
        // Getting the current directory
        String currentDirectory = System.getProperty("user.dir");
        
        // Getting the path for table file
        String tablePath = currentDirectory + "/database/" + tableName;
        
        // Getting the path for maeta file
        String metaPath = currentDirectory + "/database/meta_" + tableName;
        
        int startIndex = query.indexOf('(');
        int endIndex = query.indexOf(')');
        
        // Reducing the query to string inside '(' and ')'
        query = query.substring(startIndex+1, endIndex).trim();
        // System.out.println(query);
        
        // Converting the reduced query to 2D array for inserting in values into table file and meta file
        String queryColumns[][] = convertTo2DArray(query);
        
        // Verifying the DataType of the columns
        String checkDataTypes = checkDataType(queryColumns);
        if(checkDataTypes != null) return checkDataTypes;
        
        //System.out.println(query);
        //for(String a[] : queryColumns) {
        //	for(String cc  : a) System.out.print(cc+",");
        //	System.out.println();
        //}
        
        // Creating table file and meta file using FileWriter
        try (FileWriter tableWriter = new FileWriter(tablePath); FileWriter metaWriter = new FileWriter(metaPath)) {
        	
        	// Writes the listItems into table file and meta file
        	for(String listItem[] : queryColumns) {
        		
        		// Writing into the meta file
        		metaWriter.write("[" + listItem[0] + ":" + listItem[1] + "]\n");
        		
        		// Writing into the table file
        		tableWriter.write(""+listItem[0] + " : \n");
        	}
        	metaWriter.flush();     
        	tableWriter.flush();
            //System.out.println("Table created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
		return null;
	}
	
	// Inserting into a table using Insert Query
	public static String insertQuery(String query) {
		// Verifying the query containing keyword 'into'
		if(!query.substring(0, 4).toLowerCase().equals("into")) return "*The Insert Query Syntax is Incorrect*\n";
		
		// Reducing the query to query after the word 'into'
		query = query.substring(5);
		//System.out.println(query);
		
		// Verifying The Parenthesis in the Query
		String verifcation = parenthesisCheck(query.substring(0, query.indexOf(')')+1));
		if(verifcation != null) return verifcation;
		
		int index = query.indexOf('(');
		String tableName = "";
		String metaName = "";
		
		// Fetching the table_name from query
        if (index != -1) {
        	tableName = query.substring(0, index).trim() + ".txt";  
        	metaName ="meta_"+ query.substring(0, index).trim() + ".txt";  
        }
        else return "*Please re-enter a correct Table Name : \n*";
        
        // Getting the directory of table file
        String tableDirectory = System.getProperty("user.dir") +"/database";
        
        // Checking whether the table name present in query is present in out database
        if(ifFileExists(tableDirectory, tableName)) {
        	try {
	        	int startIndex = query.indexOf('(');
	            int endIndex = query.indexOf(')');
	            
	            // Getting the column names from the query
	            String[] columnArr = query.substring(startIndex+1, endIndex).trim().split(",");
	            
	            // Reducing the query to remaining string after column names
	            query = query.substring(endIndex+1).trim();
	            
	            // Verifying the the second pair of parenthesis
	            verifcation = parenthesisCheck(query);
	    		if(verifcation != null) return verifcation;
	    		
	            if(!query.substring(0, query.indexOf('(')).trim().toLowerCase().equals("values")) return "*Insert Query Invalid*\n";
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
            	// System.out.println("Values to Be Inserted");
            	// for(String sst  : value) System.out.print(sst);
            	addStringToEndOfFile(tableDirectory+"/"+tableName, value);
            	//System.out.println("Values inserted into table successfully.");
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
						if(reply == null) System.out.println("!!  Congrats Your table is created successfully  !!\n");
						else System.out.println(reply);
						break;
					// For inserting into table
					case "insert " : 
						reply = insertQuery(query.substring(7));
						if(reply == null) System.out.println("!!  Values inserted into table successfully  !!\n");
		            	else System.out.println(reply);
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
