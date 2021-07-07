package iu.edu.handle;

import java.util.ArrayList;

import iu.edu.mongo.HandleTableMongo;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

public class ResolvePID {
	
	public ArrayList<String[]> handleResolve(String pidID) throws HandleException, Exception {
		HandleValue values[] = new HandleResolver().resolveHandle(pidID, null, null);
		ArrayList<String[]> result = new ArrayList<String[]>();
		for (int i = 0; i < values.length; i++) {
			String[] content = values[i].toString().split(" ");
			String[] extract = new String[3];
			extract[0] = content[1].split("=")[1];
			extract[1] = content[2].split("=")[1];
			extract[2] = content[4].replaceAll("^\"|\"$", "");
			result.add(extract);
			
		}
		
		return result;
	}
	
	public static void main(String[] args) throws HandleException, Exception {

	}

}
