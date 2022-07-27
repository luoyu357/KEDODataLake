package iu.edu.neo4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.Record;

public class KGquery {
	
	private KGbuilder builder;
	
	public KGquery() throws IOException {
		this.builder = new KGbuilder();
		
	}
	
	public ArrayList<String> getPossibleList(HashMap<String, String> info) throws IOException {
		String query = "MATCH (a) WHERE ";
		
		for (Map.Entry itemV : info.entrySet()) {
			query += "a."+itemV.getKey()+ " = '"+itemV.getValue()+"' And ";
		}
		
		query += "RETURN (a.id)";
		
		Result output = this.builder.run(query);
		
		ArrayList<String> one = new ArrayList<String>();
		 
		if (output.hasNext()) {
			
			for (Record line : output.list()) {
				one.add(line.values().get(0).toString());
			}
			
		} 
		return one;
    
	}
	
	
	public Map<String, Object> getOne(String id) throws IOException{
		Map<String, Object> one = new HashMap<String, Object>();
		String query = "MATCH (a) WHERE ";

		query += "a.id = '"+id+"' ";
		
		query += "RETURN (a)";
		
		
		Result output = this.builder.run(query);
		
		
		if (output.hasNext()) {			
			
			one = output.next().values().get(0).asMap();
			
		} 
		return one;
	}
	
	
	public HashMap<String, String> getOnePIDGraph(String pid) throws IOException{
		HashMap<String, String> result = new HashMap<String, String>();
		String query = "MATCH (a:PID), (b:PID) WHERE a.id = '"+pid+"' "
				+ "AND (b)-[:aggregates]->(a) "
				+ "RETURN (b.id, b.etag) "
				+ "UNION "
				+ "MATCH (a:PID), (b:PID), (c:PID) WHERE a.id = '"+pid+"' "
				+ "where (a)-[:aggregates]->(b) AND (c)-[:aggregates]->(b) "
				+ "RETURN (c.id, c.etag) "
				+ "UNION "
				+ "MATCH (a:PID) WHERE a.id = '"+pid+"' "
				+ "RETURN (a.id, a.etag) ";
		
		Result output = this.builder.run(query);
		
 
		if (output.hasNext()) {
			
			for (Record line : output.list()) {
				if (!result.containsKey(line.values().get(0).asString())) {
					result.put(line.values().get(0).asString(), line.values().get(1).asString());
				}
			}
			
		} 
		return result;
	}
	
	
	public ArrayList<Neo4jTriple> getOneKEDO(String kedoObjectPID) {
		
		
		String query = "MATCH (s:PID), (a:PID), (b:KEDO), (c:KG),  (d:KEDOType), (e:ROCrate), (fa:Feature), (fv:Feature), (g:Insight) "
				+ "Where s.id = '"+kedoObjectPID + "' "
				+ " AND (s)-[r]->(b) "
				+ " AND (a)-[r]->(s) "
				+ " AND (c)-[r]->(b) "
				+ " AND (d)-[r]->(b) "
				+ " AND (d)-[r]->(b) "
				+ " AND (e)-[r]->(c) "
				+ " AND (e)-[r]->(e) "
				+ " AND (fa)-[r]->(c) "
				+ " AND (fa)-[r]->(fv) "
				+ " AND (fa)-[r]->(fv) "
				+ " AND (g)-[r]->(fv) "
				+ " AND (a)-[r]->(e) "
				+ " AND (a)-[r]->(g) "
				+ " RETURN *";
		
		Result output = this.builder.run(query);
		
		ArrayList<Neo4jTriple> result = new ArrayList<Neo4jTriple>();
		

		if (output.hasNext()) {
			
			for (Record line : output.list()) {
				result.add(new Neo4jTriple(line.values().get(0).asMap(), line.values().get(2).asRelationship().type().toString(), line.values().get(1).asMap()));
			}
			
		} 
		return result;
	
	}

}
