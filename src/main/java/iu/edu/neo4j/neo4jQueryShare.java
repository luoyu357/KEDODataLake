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

public class neo4jQueryShare {
	private Driver driver;
    private Session session;
    
    public neo4jQueryShare() {
    	this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123"));
		this.session = this.driver.session();
    }
    
    public ArrayList<String> share(HashMap<String, String> info){
    	ArrayList<String> result = new ArrayList<String>();
    	String query = "MATCH (a:Feature), (b:Feature), (c:ROCrate) "
    			+ "WHERE (a)-[:specializationOf]->(b) AND "
    			+ "(a)-[:isReferencedBy]->(c) AND ";
    	
    	for (Map.Entry item : info.entrySet()) {
    		query+= "b."+item.getKey().toString()+" contains '"+item.getValue().toString()+"' AND ";
    	}
    	query = query.substring(0, query.length()-4);
    	query += "RETURN (c.filePID)";
    	
    	Result output = this.session.run(query);
    	if (output.hasNext()) {
	    	for (Record nextline: output.list()) {
	    		result.add(nextline.values().get(0).toString());
	    	}
    	}
    	
    	return result;
    	
    }
 
}
