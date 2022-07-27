package iu.edu.neo4j;

import java.util.ArrayList;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;

public class neo4jQueryPIDGraph {
	private Driver driver;
    private Session session;
    
    public neo4jQueryPIDGraph() {
    	this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123"));
		this.session = this.driver.session();
    }
    
    public ArrayList<String> traversal(String pid, String prov, String deepth){
    	ArrayList<String> result = new ArrayList<String>();
    	String query = "MATCH (a:PID), (b:PID) "
    			+ "WHERE a.id = '"+pid+"' AND (a)-[:"+prov+"*1.."+deepth+"]->(b) "
    			+ "RETURN (b.id)";
    	
    	Result output = this.session.run(query);
    	if (output.hasNext()) {
	    	for (Record nextline: output.list()) {
	    		result.add(nextline.values().get(0).toString());
	    	}
    	}
    	
    	return result;
    	
    }
}
