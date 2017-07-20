/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package verifier;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
/**
 *
 * @author msgeden
 */
public class Verifier {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Verifier Started!");
		List<File> traceFiles=FileHandler.retrieveTraceFiles("execution");
		for (File file:traceFiles)
			//ControlFlowChecker(file);
			ControlDependencyChecker(file);
	}

	//Finds only control attacks with O(N) complexity
	public static boolean ControlFlowChecker(File traceFile) throws IOException
	{
		boolean attackFound=false;
		HashSet<String> flowPairs=new HashSet<String>();
		
		//Read control flow pairs
		String CFG = FileHandler.readFileToString(FileHandler.readConfigValue(Constants.CONTROL_FLOW_PATH_CONFIG)).replaceAll(Constants.NEW_LINE,Constants.COMMA);
		String[] CFGArray = CFG.split(Constants.COMMA);
		flowPairs.addAll(Arrays.asList(CFGArray));
		//Read actual execution trace
		String trace=FileHandler.readFileToString(traceFile.getAbsolutePath());
		String traceArray[] = trace.split(Constants.EDGE_CHAR);
		//Check execution trace against control-flow edges
		
		System.out.println("\nFile\t" + traceFile.getName()); 
		System.out.println("CFG:\t" +CFG);
		System.out.println("Trace:\t" +trace);
		
		for (int i=0;i<traceArray.length-1;i++)
		{
			String tracePair=traceArray[i]+Constants.EDGE_CHAR+traceArray[i+1];
			attackFound = !flowPairs.contains(tracePair);
			System.out.println(tracePair + (attackFound?" not":"") + " found in CFG");
			if (attackFound)
				break;
		}
		
		if (attackFound)
			System.out.println("CFI violation found for " + traceFile.getName());
		else
			System.out.println("No CFI violation found for " + traceFile.getName());
		return attackFound;
	}
	
	//Finds some of control attacks with O(N^2) complexity
	public static boolean ControlDependencyChecker(File traceFile) throws IOException
	{
		boolean attackFound=false;
		//key=dependent, value=free
		HashMap<String,String> controlDependencyPairs=new HashMap<String,String>();
		
		//Read control flow pairs
		String CDG = FileHandler.readFileToString(FileHandler.readConfigValue(Constants.CONTROL_DEPENDENCIES_PATH_CONFIG)).replaceAll(Constants.NEW_LINE,Constants.COMMA);
		String[] CDGArray = CDG.split(Constants.COMMA);
		for (String CDGPair:CDGArray)
		{
			String[] nodes=CDGPair.split(Constants.EDGE_CHAR);
			//key=dependent, value=free
			controlDependencyPairs.put(nodes[1], nodes[0]);
		}
			
		//Read actual execution trace
		String trace=FileHandler.readFileToString(traceFile.getAbsolutePath());
		String traceArray[] = trace.split(Constants.EDGE_CHAR);
		
		//Check execution trace against control-dependency edges
		System.out.println("\nFile\t" + traceFile.getName()); 
		System.out.println("CDG:\t" +CDG);
		System.out.println("Trace:\t" +trace);
		//Traverse in backwards
		for (int i=traceArray.length-2;i>0;i--){
			boolean precedingFound=false;
			
		 	String precedingNode = controlDependencyPairs.get(traceArray[i]);
		 	//System.out.println("Node: "+traceArray[i] + " Preceding:"+precedingNode);
		 	
		 	if(precedingNode==null)
		 	{
		 		attackFound=true;
		 		break;
		 	}
			for(int j=i;j>=0;j--){
				if (traceArray[j].equals(precedingNode)){
					precedingFound=true;
					break;
				}
			}
			attackFound=!precedingFound;
			if (attackFound)
				break;
		}
		
		if (attackFound)
			System.out.println("CDI violation found for " + traceFile.getName());
		else
			System.out.println("No CDI violation found for " + traceFile.getName());
		return attackFound;
	}
	
	
}
