package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Scanner;

public class main {
	
	// To remove additional spaces between characters or words, reduces to single space.
	 public static String reduceSpaces(String text) {
	        String reducedText = text.replaceAll("\\s+", " ");
	        return reducedText;
	    }
	 
	 public static String checkDataType(String value[][]) {
		 for(String a[] : value) {
			 if(a[1].equals("int") ||  a[1].equals("string") || a[1].equals("char")) continue;
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
		if(parenthesisCount != 1) return "Please Re-enter a Correct Query : \n";
		else return null;
	}
	
	public static String createQuery(String query) {
		// For checking the validity of a query
		String verifcation = parenthesisCheck(query);
		if(verifcation != null) return verifcation;
		int index = query.indexOf(" ");
		String tableName = "";
        if (index != -1) tableName = query.substring(0, index) + ".txt";  
        else return "Please re-enter a correct Table Name : \n";
        
        // CREATING THE FILE :
        String currentDirectory = System.getProperty("user.dir");
        String tablePath = currentDirectory + "/database/" + tableName;
        String metaPath = currentDirectory + "/database/meta_" + tableName;
        int startIndex = query.indexOf('(');
        int endIndex = query.indexOf(')');
        query = query.substring(startIndex+1, endIndex).trim();
        query = reduceSpaces(query);
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
		String verifcation = parenthesisCheck(query);
		
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
			String query = s.nextLine();
			
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
						System.out.println(reply);
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
