package iu.edu.kg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import iu.edu.handle.ResolvePID;
import iu.edu.kedo.Property;
import net.handle.hdllib.HandleException;

public class KGupdate {
	
	
	
	public void PIDprovenUpdate(String targetPID, String type, String value) throws IOException {
		StringBuilder tokenUri = new StringBuilder("?update=");
		String delete = "delete data {";
		delete += "<"+targetPID+"> <http://www.entity.com/field#"+type+"> 'None' .";
		delete += "}";
		
		tokenUri.append(URLEncoder.encode(delete, StandardCharsets.UTF_8));
		
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL((new Property()).property.getProperty("graphdb")+"/statements"+tokenUri.toString());
	   
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setDoOutput(true);
	    
	    try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
        for (String line; (line = reader.readLine()) != null; ) {
            	result.append(line);
        	}
	    }
		
		KGbuilder builder = new KGbuilder();
	    
	    HashMap<String, String> info = new HashMap<String, String>();
	    
	    builder.addPredicate(targetPID, "http://www.entity.com/field#"+type, value);
		
		builder.publishKG((new Property()).property.getProperty("graphdb"));
	}
	
	
	public void PIDlastModified(String targetPID, String date) throws IOException {
		
		StringBuilder tokenUri = new StringBuilder("?update=");
		String delete = "delete {";
		delete += "<"+targetPID+"> <http://www.entity.com/field#lastModified> ?date .";
		delete += "} where {<"+targetPID+"> <http://www.entity.com/field#lastModified> ?date .}";
		
		tokenUri.append(URLEncoder.encode(delete, StandardCharsets.UTF_8));
		
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL((new Property()).property.getProperty("graphdb")+"/statements"+tokenUri.toString());
	   
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setDoOutput(true);
	    
	    try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
        for (String line; (line = reader.readLine()) != null; ) {
            	result.append(line);
        	}
	    }
	    
	    KGbuilder builder = new KGbuilder();
	    
	    HashMap<String, String> info = new HashMap<String, String>();
	    
		info.put("http://www.entity.com/field#lastModified", date);
	    builder.createResource("", targetPID, info);
		
		builder.publishKG((new Property()).property.getProperty("graphdb"));
		
	}
	
	
	
	
	
	//http://149.165.168.252:8000/api/handles/11723/6e0f2f7e-cd05-4f5a-93ca-f8986e6b48bf
		
	public static void main(String[] args) throws HandleException, Exception {
		
	}

}
