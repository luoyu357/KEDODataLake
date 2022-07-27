package iu.edu.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class neo4jQueryEntity {
	private Driver driver;
    private Session session;
    
    public neo4jQueryEntity() {
    	this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123"));
		this.session = this.driver.session();
    }
    
    public ArrayList<String> queryEntityID(HashMap<String, String> info){
    	ArrayList<String> result = new ArrayList<String>();
    	String query = "MATCH (a) WHERE ";
    	
    	for (Map.Entry item : info.entrySet() ) {
    		query += "a."+item.getKey().toString() + " contains '"+item.getValue().toString()+"' AND ";
    	}
    	query = query.substring(0, query.length()-4);
    	query +=  "RETURN (a.id)";
    	
    	Result output = this.session.run(query);
    	if (output.hasNext()) {
	    	for (Record nextline: output.list()) {
	    		result.add(nextline.values().get(0).toString());
	    	}
    	}
    	
    	return result;
    	
    }
    
    
    public ArrayList<String> queryEntityID(HashMap<String, String> info, String label){
    	ArrayList<String> result = new ArrayList<String>();
    	String query = "MATCH (a:"+label+") WHERE ";
    	
    	for (Map.Entry item : info.entrySet() ) {
    		query += "a."+item.getKey().toString() + " contains '"+item.getValue().toString()+"' AND ";
    	}
    	query = query.substring(0, query.length()-4);
    	query +=  "RETURN (a.id)";
    	
    	Result output = this.session.run(query);
    	if (output.hasNext()) {
	    	for (Record nextline: output.list()) {
	    		result.add(nextline.values().get(0).toString());
	    	}
    	}
    	
    	return result;
    	
    }
    
    
    public ArrayList<String> queryEntityID(HashMap<String, String> info, String label, String type){
    	ArrayList<String> result = new ArrayList<String>();
    	String query = "MATCH (a:"+label+") WHERE ";
    	
    	for (Map.Entry item : info.entrySet() ) {
    		query += "a."+item.getKey().toString() + " contains '"+item.getValue().toString()+"' AND ";
    	}
    	query += "a.type = '"+type+"' ";
    	query +=  "RETURN (a.id)";
    	
    	Result output = this.session.run(query);
    	if (output.hasNext()) {
	    	for (Record nextline: output.list()) {
	    		result.add(nextline.values().get(0).toString());
	    	}
    	}
    	
    	return result;
    	
    }
}
