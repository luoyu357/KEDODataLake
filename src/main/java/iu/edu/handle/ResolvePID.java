package iu.edu.handle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


import iu.edu.mongo.HandleTableMongo;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

public class ResolvePID {
	
	public int Pcount = 0;
	
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
	
	public void httpResolve(String PIDurl) throws Exception {
		if (PIDurl.equals("None")) {
			
		} else {
			this.Pcount++;
			System.out.println(PIDurl);
			URL object = new URL(PIDurl);
			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setRequestMethod("GET");
			
			StringBuilder content;
	
	        try (BufferedReader in = new BufferedReader(
	                new InputStreamReader(con.getInputStream()))) {
	
	            String line;
	            content = new StringBuilder();
	
	            while ((line = in.readLine()) != null) {
	                content.append(line);
	                content.append(System.lineSeparator());
	            }
	        }
	        String jsonString = content.toString();
	        
	        JSONObject output = new JSONObject(jsonString);
	        
			JSONArray values = output.getJSONArray("values");
			
			for (Object items : values) {
				JSONObject item = (JSONObject) items;
				if (item.get("type").equals("wasDerivedFrom")) {
					httpResolve(item.getJSONObject("data").getString("value"));
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws HandleException, Exception {
		ResolvePID test = new ResolvePID();
		test.httpResolve("http://35.83.244.177:8000/api/handles/20.500.12033/c7ba0b48-ec42-4d18-b3c7-586a6a2564ef");
		
	
	}
	
	

}
