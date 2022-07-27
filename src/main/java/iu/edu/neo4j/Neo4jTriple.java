package iu.edu.neo4j;

import java.util.Map;

public class Neo4jTriple {
	
	private Map<String, Object> head;
	private Map<String, Object> tail;
	private String relationship;
	
	public Neo4jTriple(Map<String, Object> map, String relationship, Map<String, Object> map2) {
		this.head = map;
		this.tail = map2;
		this.relationship = relationship;
	}
	
	
	public Map<String, Object> getHead(){
		return this.head;
	}
	
	public Map<String, Object> getTail(){
		return this.tail;
	}
	
	
	public String getRelationship() {
		return this.relationship;
	}

}
