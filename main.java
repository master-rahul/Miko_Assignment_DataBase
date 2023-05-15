package main;

import java.io.File;
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
	
	// Verfying the Column names from query to table
	public static String checkValidityOfColumns(String[] tableData, String[] columnData) {
		System.out.println("TableData and Column Check");
		for(String ss : tableData) System.out.println(ss);
    	for(String ss : columnData) System.out.println(ss);
    	if(tableData.length < columnData.length) return "*****Column Not Present in Table*****\n";
    	Map<String, Integer> map = new HashMap<>();
    	for(String columName : tableData) {
    		columName = columName.substring(0, columName.indexOf(":")-1);
    		map.put(columName, 0);
    	}
    	for(String columnName  : columnData) if(!map.containsKey(columnName)) return "*****Invalid Column for Table*****\n";
		return null;
	}
	
	//Verfying the DataType of values from the query to metatable
	public static String checkValidityOfValues(String[] metaData, String[] valueData) {
		for(String ss : metaData) System.out.println(ss);
    	for(String ss : valueData) System.out.println(ss);
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
            	validity = checkValidityOfValues(metaData, valueArr);
            	if(validity != null) return validity;
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
		System.out.print("Please Enter Your Query Or Type Exit to terminate : \n");
		
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
