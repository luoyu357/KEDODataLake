package iu.edu.handle;

import java.util.ArrayList;

import iu.edu.mongo.HandleTableMongo;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

public class ResolvePID {
	
	public ArrayList<String[]> handleResolve(String pidID) throws HandleException, Exception {
		pidID = pidID.split("handles/")[1];
		HandleValue values[] = new HandleResolver().resolveHandle(pidID, null, null);
		ArrayList<String[]> result = new ArrayList<String[]>();
		for (int i = 0; i < values.length; i++) {
			String[] content = values[i].toString().split(" rwr- ");
			String[] extract = new String[3];
			extract[0] = content[0].split(" ")[1].split("=")[1];
			extract[1] = content[0].split(" ")[2].split("=")[1];
			extract[2] = content[1].replaceAll("^\"|\"$", "");
			result.add(extract);
			
		}
		
		return result;
	}
	
	public static void main(String[] args) throws HandleException, Exception {
		ResolvePID test = new ResolvePID();
		test.handleResolve("http://35.83.244.177:8000/api/handles/20.500.12033/6c7cedec-ab76-4c0d-a151-03c3726807ac");

	}

}
