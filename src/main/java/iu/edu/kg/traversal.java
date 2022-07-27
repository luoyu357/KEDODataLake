package iu.edu.kg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class traversal {
	
	private KGbuilder kg;
	private ArrayList<String> remove;
	
	public traversal() {
		this.kg = new KGbuilder();
		this.remove = new ArrayList<String>();
	}
	

	
	public void traversalOutbound(String startID, int minD, int maxD, int step) throws IOException {
			
		String query = "select ?x ?y where {\n";
		query += "<"+startID+"> ?x ?y.\n";
		query += "}";	
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL("http://localhost:7200/repositories/1?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    String[] temp = result.toString().split("\n");
   
	    if (temp.length > 1) {
	    	for (int i = 1 ; i < temp.length ; i++) {
	    		String[] output = temp[i].split(",");
	    		if (output[0].contains("rdf") || output[0].contains("field")) {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.createResource(startID, output[0], output[1]);
	    			}
	    		} else {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.addPredicate(startID, output[0], output[1]);
	    			}
	    			if (step <= maxD) {	
	    				
	    				traversalOutbound(output[1], minD, maxD, step+1);
	    			}			
	    		}
	    	}
	    }
	}
	
	public void traversalInbound(String startID, int minD, int maxD, int step) throws IOException {
		String query = "select ?x ?y where {\n";
		query += "?y ?x <"+startID+">.\n";
		query += "}";	
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL("http://localhost:7200/repositories/1?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    
	    String[] temp = result.toString().split("\n");
	    
	    if (step >= minD && step <= maxD) {
	    	queryOne(startID);
	    }
	    
	    if (temp.length > 1) {
	    	//this.step++;
	    	for (int i = 1 ; i < temp.length ; i++) {
	    		String[] output = temp[i].split(",");
	    		if (output[0].contains("rdf") || output[0].contains("field")) {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.createResource(startID, output[0], output[1]);
	    			}
	    		} else {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.addPredicate(startID, output[0], output[1]);
	    				
	    			}
	    			if (step <= maxD) {	
	    				
	    				traversalInbound(output[1], minD, maxD, step+1);
	    			}			
	    		}
	    	}
	    }
	}
	
	
	public void queryOne(String id) throws IOException {
		String query = "select ?x ?y where {\n";
		query += "<"+id+"> ?x ?y.\n";
		query += "}";	
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL("http://localhost:7200/repositories/1?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    String[] temp = result.toString().split("\n");
	    
	    if (temp.length > 1) {
	    	for (int i = 1 ; i < temp.length ; i++) {
	    		String[] output = temp[i].split(",");
	    		if (output[0].contains("rdf") || output[0].contains("field")) {
	    			this.kg.createResource(id, output[0], output[1]);
	    		}
	    	}
	    }
	}
	
	
	
	public void traversalOutboundAny(String startID, int minD, int maxD, int step) throws IOException {
		
		if (step < minD) {
			this.remove.add(startID);
		}
		
		String query = "select ?x ?y where {\n";
		query += "<"+startID+"> ?x ?y.\n";
		query += "}";	
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL("http://localhost:7200/repositories/1?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    String[] temp = result.toString().split("\n");
   
	    if (temp.length > 1) {
	    	for (int i = 1 ; i < temp.length ; i++) {
	    		String[] output = temp[i].split(",");
	    		if (output[0].contains("rdf") || output[0].contains("field")) {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.createResource(startID, output[0], output[1]);
	    			}
	    		} else {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.addPredicate(startID, output[0], output[1]);
	    			}
	    			if (step <= maxD) {	
	    				
	    				if (!this.remove.contains(output[1])) {
	    					traversalOutboundAny(output[1], minD, maxD, step+1);
	    					traversalInboundAny(output[1], minD, maxD, step+1);
	    				}
	    			}			
	    		}
	    	}
	    }
	}
	
	public void traversalInboundAny(String startID, int minD, int maxD, int step) throws IOException {
		
		if (step < minD) {
			this.remove.add(startID);
		}
		
		String query = "select ?x ?y where {\n";
		query += "?y ?x <"+startID+">.\n";
		query += "}";	
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL("http://localhost:7200/repositories/1?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    
	    String[] temp = result.toString().split("\n");
	    
	    if (step >= minD && step <= maxD) {
	    	queryOne(startID);
	    }
	    
	    if (temp.length > 1) {
	    	for (int i = 1 ; i < temp.length ; i++) {
	    		String[] output = temp[i].split(",");
	    		if (output[0].contains("rdf") || output[0].contains("field")) {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.createResource(startID, output[0], output[1]);
	    			}
	    		} else {
	    			if (step >= minD && step <= maxD) {
	    				this.kg.addPredicate(startID, output[0], output[1]);
	    				
	    			}
	    			if (step <= maxD) {	
	    				
	    				if (!this.remove.contains(output[1])) {
	    					traversalOutboundAny(output[1], minD, maxD, step+1);
	    					traversalInbound(output[1], minD, maxD, step+1);
	    				}
	    			}			
	    		}
	    	}
	    }
	}
	
	
	public void trversalAny(String startID, int minD, int maxD, int step) throws IOException {
		traversalInboundAny(startID, minD, maxD, step);
		traversalOutboundAny(startID, minD, maxD, step);
	}
	
	
	

	
	
	public static void main(String[] args) throws IOException {
		
	}

}
