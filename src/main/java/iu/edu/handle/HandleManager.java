package iu.edu.handle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

import iu.edu.kg.KGbuilder;

public class HandleManager {
	
	
	public HandleManager() {
		
	}
	
	
	public void createHandle(String pid, HashMap<String, String> c) throws Exception {
		//RegisterPID re = new RegisterPID();
		//re.createHandle(pid, c);
	}
	
	public HashMap<String, String> KI(HashMap<String, String> c, 
			HashMap<String, String> prov,
			HashMap<String, String> graph){
		HashMap<String, String> result = new HashMap<String, String>();
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
        String date = formatter.format(new Date());

        result.put("dateCreation", (c.containsKey("dateCreation") ? c.get("dateCreation") : date));
        result.put("etag", (c.containsKey("etag") ? c.get("etag") : "None"));
		result.put("lastModified", (c.containsKey("lastModified") ? c.get("lastModified") : date));
		
		result.put("digitalObjectLocation", (c.containsKey("digitalObjectLocation") ? 
				c.get("digitalObjectLocation") : "None"));
		
			
		result.put("wasDerivedFrom", (prov.containsKey("wasDerivedFrom") ? 
				prov.get("wasDerivedFrom") : "None"));
		result.put("specializationOf", (prov.containsKey("specializationOf") ? 
				prov.get("specializationOf") : "None"));
		result.put("revisionOf", (prov.containsKey("revisionOf") ? 
				prov.get("revisionOf") : "None"));
		result.put("primarySourceOf", (prov.containsKey("primarySourceOf") ? 
				prov.get("primarySourceOf") : "None"));
		result.put("quotationOf", (prov.containsKey("quotationOf") ? 
				prov.get("quotationOf") : "None"));
		result.put("alternateOf", (prov.containsKey("alternateOf") ? 
				prov.get("alternateOf") : "None"));
		
		for (Map.Entry item : graph.entrySet()) {
			result.put(item.getKey().toString(), item.getValue().toString());
		}
		
		return result;
		
	}
	
	public void addGraph(HashMap<String, String> result, String property, String id){
		result.put(property, id);
	}

}
