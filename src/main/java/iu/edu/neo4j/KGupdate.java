package iu.edu.neo4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import iu.edu.kedo.Property;

public class KGupdate {
	private KGbuilder builder;
	
	public KGupdate() throws IOException {
		this.builder = new KGbuilder();
		
	}
	
	public void PIDprovenUpdate(String targetPID, String type, String objectPID) throws IOException {
		this.builder.addPredicate(targetPID, objectPID, type);

	}
	
	
	public void PIDlastModified(String targetPID, String date) throws IOException {
		
		String query = "MATCH (a:PID) WHERE a.id = '"+targetPID+"' "
				+ "SET a.lastModified = '"+date+"' "
				+"RETURN (a.id)";
		
		this.builder.run(query);
		
		
	}

}
