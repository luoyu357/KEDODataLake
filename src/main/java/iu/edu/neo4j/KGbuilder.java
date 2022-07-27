package iu.edu.neo4j;

import java.io.IOException;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;


public class KGbuilder {
	
	private Driver driver;
    private Session session;
    private iu.edu.kedo.Property input;
    
    public KGbuilder() throws IOException {
    	
    	this.input = new iu.edu.kedo.Property();
    	this.driver = GraphDatabase.driver(input.property.getProperty("neo4j"), AuthTokens.basic(input.property.getProperty("neo4j.repository"), input.property.getProperty("neo4j.password")));
		this.session = this.driver.session();
    }

    
    public Result run(String query) {
    	return this.session.run(query);
    }
    
	public void addPredicate(String id1, String id2, String pre) throws IOException {
		String query = "MATCH (a), (b)";
		query += "WHERE a.id = '"+id1+"' AND b.id = '"+id2+"'";
		query += "CREATE (a)-[r:"+pre+"]->(b)";
		query += "RETURN type(r)";

		this.session.run(query);

	}
}
