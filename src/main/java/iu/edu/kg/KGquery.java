package iu.edu.kg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import iu.edu.data.Filesystem;

public class KGquery {
	
	public String queryKEDOType(HashMap<String, String> c, String gdb) throws IOException {
		String query = "select ?x ?z where {\n";
		for (Map.Entry item : c.entrySet()) {
			query += "?x <" + item.getKey() + '>'+ " " +'"'+ item.getValue() + '"'+" .\n";		 
		}
		query += "?x <http://www.entity.com/field#pid> ?z .\n";
		query += "}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    
	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+",");
	        }
	    }
	    
	    return result.toString();
    
	}
	
	public String queryFeature(HashMap<String, String> c, String gdb) throws IOException {
		String query = "select ?x where {\n";
		for (Map.Entry item : c.entrySet()) {
			query += "?x <" + item.getKey() + '>'+ " " +'"'+ item.getValue() + '"'+" .\n";
		}
		query += "}";

		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    
	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line);
	        }
	    }
	    
	    return result.toString();
	}
	
	public String querySharedPID(HashMap<String, String> c, String gdb) throws IOException {
		String query = "select distinct ?w where {\n";
		for (Map.Entry item : c.entrySet()) {
			query += "?x <" + item.getKey() + '>'+ " " +'"'+ item.getValue() + '"'+" .\n";
		}
		query += "?y <http://www.openarchives.org/ore/terms#isDescribedBy> ?x .\n";
		query += "?y <http://www.openarchives.org/ore/terms#isReferencedBy> ?z .\n";
		query += "?z <http://www.entity.com/field#filePID> ?w .\n";
		query += "}";
		
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    
	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line);
	        }
	    }
	    
	    return result.toString();
	}
	
	
	
	public String queryOne(String id, String gdb) throws IOException{
		
		String query = "select distinct ?w ?z where {\n";
		query += "<"+id+"> ?w ?z }";
	
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	
	public String queryPIDGraph(String id, String gdb) throws IOException{
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "<"+id+"> ?aggregates ?y. ?y rdf:type 'PID'";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?aggregates ?z. ?y ?aggregates ?z. ?y rdf:type 'PID'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?y ?aggregates <"+id+">. ?y rdf:type 'PID'";
		query += "}}}";
	
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String queryroWFeature(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?y. ?y rdf:type 'RO-Crate'";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?y ?isReferencedBy ?z. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?w ?isReferencedBy ?z. ?w ?isDescribedBy ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?w ?isReferencedBy ?z. ?w ?specializationOf ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?w ?isReferencedBy ?z. ?z ?checksum ?q. ?w ?isDescribedBy ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?w ?isReferencedBy ?z. ?z ?checksum ?q. ?w ?specializationOf ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}

	
	public String queryrfileFeature(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "?x ?filePID '"+id+"'. ?x rdf:type 'RO-Crate'. ?y ?isReferencedBy ?x. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?x ?filePID '"+id+"'. ?x rdf:type 'RO-Crate'. ?w ?isReferencedBy ?x. ?w ?isDescribedBy ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?x ?filePID '"+id+"'. ?x rdf:type 'RO-Crate'. ?w ?isReferencedBy ?x. ?w ?specializationOf ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?e ?filePID '"+id+"'. ?e rdf:type 'RO-Crate'. ?w ?isReferencedBy ?e. ?e ?checksum ?q. ?w ?isDescribedBy ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}} union {";
		query += "select ?y where {";
		query += "?e ?filePID '"+id+"'. ?e rdf:type 'RO-Crate'. ?w ?isReferencedBy ?e. ?e ?checksum ?q. ?w ?specializationOf ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String queryShare(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "<"+id+"> rdf:type 'Feature'. ?x ?isDescribedBy <"+id+">. ?x ?isReferencedBy ?w. ?w <http://www.entity.com/field#filePID> ?y";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> rdf:type 'Feature'. ?x ?specializationOf <"+id+">. ?x ?isReferencedBy ?w. ?w <http://www.entity.com/field#filePID> ?y";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String useKGPIDfindRO(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "<"+id+"> ?describes ?z. ?z rdf:type 'KEDO KG'. ?y ?isPartOf ?z. ?y rdf:type 'RO-Crate'.";
		query += "}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String useROfindFeature(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "?y ?isReferencedBy <"+id+">. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?w ?isReferencedBy <"+id+">. ?w ?isDescribedBy ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?w ?isReferencedBy <"+id+">. ?w ?specializationOf ?y. ?y rdf:type 'Feature'";
		query += "}} union {";
		query += "select ?y where {";
		query += "?w ?isReferencedBy <"+id+">. <"+id+"> ?checksum ?q. ?w ?isDescribedBy ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}} union {";
		query += "select ?y where {";
		query += "?w ?isReferencedBy <"+id+">. <"+id+"> ?checksum ?q. ?w ?specializationOf ?x. ?y ?aggregates ?x. ?y rdf:type 'Insight'. ?y ?checksum ?q";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	public String useKGPIDlistAll(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?y. ?y rdf:type 'KEDO KG'.";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?z rdf:type 'KEDO KG'. ?y ?isPartOf ?z. ?y rdf:type 'RO-Crate'.";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?z rdf:type 'KEDO KG'. ?z ?isAggregatedBy ?y. ?y rdf:type 'KEDO Type'.";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?z. ?z rdf:type 'KEDO KG'. ?z ?isDescribedBy ?y. ?y rdf:type 'KEDO Type'.";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String queryChecksum(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "?x <http://www.entity.com/field#filePID> '"+id+"'.";
		query += "?x <http://www.entity.com/field#checksum> ?y.";
		query += "}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	public String queryfileKEDOObjectPID(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "<"+id+"> ?aggregates ?y.";
		query += "?y rdf:type 'PID'.";
		query += "}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	public String queryLocationKEDOObjectPID(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "<"+id+"> ?describes ?x.";
		query += "?x <http://www.entity.com/field#location> ?y.";
		query += "}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	
	
	public String listAllKEDOObjectPID(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "{";
		query += "select ?y where {";
		query += "?y ?aggregates <"+id+">.";
		query += "}} union {";
		query += "select ?y where {";
		query += "<"+id+"> ?describes ?y. ?y rdf:type 'KEDO Object'";
		query += "}}}";
		
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
		
	}
	
	public String KGPIDfromKEDOObjectpid(String id, String gdb) throws IOException {
		String query = "select distinct ?y where {";
		query += "?y ?aggregates <"+id+">.";
		query += "?y rdfs:label 'KEDO KG PID'.";
		query += "}";
				
		StringBuilder result = new StringBuilder();
	    URL url = new URL(gdb+"?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	    
	    try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line+"\n");
	        }
	    }
	    
	    return result.toString();
	}
	
	public void test(String kedoPID) throws IOException {
		String location = new KGquery().queryLocationKEDOObjectPID(kedoPID, "http://localhost:7200/repositories/1").split("\n")[1];
		String[] aroundKEDOObjectPID = new KGquery().listAllKEDOObjectPID(kedoPID, "http://localhost:7200/repositories/1").split("\n");
		String kgPID = new KGquery().KGPIDfromKEDOObjectpid(kedoPID, "http://localhost:7200/repositories/1").split("\n")[1];
		 
		KGbuilder object = new KGbuilder();
		 
		String[] entity1 = new KGquery().queryOne(kedoPID, "http://localhost:7200/repositories/1").split("\n");
		object.createResource("", kedoPID, new HashMap<String, String>());
		if (entity1.length > 1) {
			for (int j = 1 ; j < entity1.length; j++) {
				String type = entity1[j].split(",")[0];
				String value = entity1[j].split(",")[1];
				if (!type.contains("digitalObjectLocation") && !type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
					if (value.contains("http")) {
						object.addPredicate(kedoPID, type, value);
					} else {
						object.addProperty(kedoPID, type, value);
					}
				} else {
					object.addProperty(kedoPID, type, value);
				}
			}
		}
		 
		 if (aroundKEDOObjectPID.length > 1) {
			for (int i = 1 ; i < aroundKEDOObjectPID.length; i++) {
				String id = aroundKEDOObjectPID[i];
				System.out.println(id);
				String[] entity = new KGquery().queryOne(id, "http://localhost:7200/repositories/1").split("\n");
				object.createResource("", id, new HashMap<String, String>());
				if (entity.length > 1) {
					for (int j = 1 ; j < entity.length; j++) {
						String type = entity[j].split(",")[0];
						String value = entity[j].split(",")[1];
						if (!type.contains("digitalObjectLocation") && !type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
							if (value.contains("http")) {
								object.addPredicate(id, type, value);
							} else {
								object.addProperty(id, type, value);
							}
						} else {
							object.addProperty(id, type, value);
						}
					}
				}
			}
		}
		 
		String[] kglistAll = new KGquery().useKGPIDlistAll(kgPID, "http://localhost:7200/repositories/1").split("\n");
		
		if (kglistAll.length > 1) {
			for (int i = 1 ; i < kglistAll.length; i++) {
				String id = kglistAll[i];
				System.out.println(id);
				String[] entity = new KGquery().queryOne(id, "http://localhost:7200/repositories/1").split("\n");
				object.createResource("", id, new HashMap<String, String>());
				if (entity.length > 1) {
					for (int j = 1 ; j < entity.length; j++) {
						String type = entity[j].split(",")[0];
						String value = entity[j].split(",")[1];
						if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
							if (value.contains("http")) {
								object.addPredicate(id, type, value);
							} else {
								object.addProperty(id, type, value);
							}
						} else {
							object.addProperty(id, type, value);
						}
					}
				}
			}
		}
		
		String[] roList = new KGquery().useKGPIDfindRO(kgPID, "http://localhost:7200/repositories/1").split("\n");
		if (roList.length > 1) {
			for (int i = 1; i < roList.length; i++) {
				String id = kglistAll[i];
				String[] list = new KGquery().useROfindFeature(id, "http://localhost:7200/repositories/1").split("\n");
				if (list.length > 1) {
					for (int j = 1; j < list.length; j++) {
						String id2 = list[j];
						System.out.println(id2);
						String[] entity = new KGquery().queryOne(id2, "http://localhost:7200/repositories/1").split("\n");
						object.createResource("", id2, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int k = 1 ; k < entity.length; k++) {
								String type = entity[k].split(",")[0];
								String value = entity[k].split(",")[1];
								if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id2, type, value);
									} else {
										object.addProperty(id2, type, value);
									}
								} else {
									object.addProperty(id2, type, value);
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println(object.printKG());
		Filesystem system = new Filesystem();
		system.writeFile("rdf.ttl", object.printKG(), location);
		
		system.zipFolder(location, location+".zip");
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
	}

}
