package iu.edu.neo4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.Result;

import iu.edu.handle.HandleManager;
import iu.edu.handle.HandleTable;
import iu.edu.mongo.HandleTableMongo;
//import iu.edu.neo4j.KGbuilder;
import iu.edu.netcdf.ReadInfo;
import iu.edu.ro.ROCrateDescriptor;

public class Neo4jInsertTest {
	
	private HandleTable ht;
	private HandleManager hm;

	private String service = "";
	private HashMap<String, String> kiKG = new HashMap<String, String>();
	private HashMap<String, String> kiH = new HashMap<String, String>();
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    private String date;
    private String kedoObjectPID;
    private HashMap<String,String> PIDtable;

    private KGbuilder builder;

	
	

	public Neo4jInsertTest() throws Exception {
		
		this.ht = new HandleTable();
		this.hm = new HandleManager();
		this.date = formatter.format(new Date());
		this.PIDtable = new HashMap<String, String>();

		this.builder = new KGbuilder();

	}
	
	
	public String[] modify(String[] input, int instance_numnber) {
		
		ArrayList<String> output = new ArrayList<String>();
		
		while (output.size() < instance_numnber) {
			Integer[] temp_check = new Integer[5];
			
			for (int i = 0; i < 5; i++) {
				Random rand = new Random();
				temp_check[i] = rand.nextInt(2);
			}
			List<String> temp_string = new ArrayList<String>();
			for (int i = 0 ; i < 5; i++) {
				
				if (temp_check[i] == 1) {
					temp_string.add(input[i]);
					
				}
			}
			
			String temp_output = String.join(" ", temp_string);
			if (!output.contains(temp_output)) {
				output.add(temp_output);
			}
		}
		
		String[] result = new String[output.size()];
		
		for (int i = 0; i < output.size(); i++) {
			result[i] = output.get(i);
		}
		return result;
			
	}
	
	public ArrayList<String[]> attributes(int class_number, int instance_number) {
		ArrayList<String[]> h_key = new ArrayList<String[]>();
		for (int i = 0; i < class_number; i++) {
			String[] key = UUID.randomUUID().toString().split("-");
			h_key.add(modify(key,instance_number));
		}
		
		ArrayList<String[]> h_value = new ArrayList<String[]>();
		for (int i = 0; i < class_number; i++) {
			System.out.println(i);
			String[] value = UUID.randomUUID().toString().split("-");
			h_value.add(modify(value,instance_number));
		}
		
		ArrayList<String[]> pair = new ArrayList<String[]>();
		
		for (int i = 0; i< class_number; i++) {
			for (String item_key : h_key.get(i)) {
				for (String item_value : h_value.get(i)) {
					pair.add(new String[]{item_key, item_value});
				}
			}
			
		}
		
		return pair;
	}
	
    
    

	
	public void createKG(int kedo_size, int file_size, int variable_size) throws Exception{
		String path = "null";
		
		ArrayList<String[]> pair = attributes(20,20);
		ArrayList<String> pidList = new ArrayList<String>();
		
		
		for (int i = 0; i < kedo_size; i++) {
			
			//1. create KEDO Object
			
			String[] kedo = createKEDO("KEDO Object", path);
			String kedoObjectID = kedo[0];
			kedoObjectPID = kedo[1];
			
			kiKG.put("dateCreation", date);
	        kiKG.put("etag", "KEDO Object PID");
	        kiKG.put("lastModified", date);
	        kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+kedoObjectID);
	        
	        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());

	        createHandle(kedoObjectPID, kiH, new HashMap<String, String>(), "KEDO Object PID");

	        hm.createHandle(kedoObjectPID, kiH);
	        
	        this.builder.addPredicate(kedoObjectPID, kedoObjectID, "describes");
	        
	        
	        //2. create KEDO KG

	        String[] kg = createKEDOKG("KEDO KG", "");
			String kedoKGID = kg[0];
			String kedoKGPID = kg[1];
				
	        kiKG.put("dateCreation", date);
	        kiKG.put("etag", "KEDO KG PID");
	        kiKG.put("lastModified", date);
	        kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+kedoKGID);
	           
	        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
			
			createHandle(kedoKGPID, kiH, new HashMap<String, String>(), "KEDO KG PID");

			hm.createHandle(kedoKGPID, kiH);

			
			this.builder.addPredicate(kedoKGPID, kedoObjectPID, "aggregates");
			this.builder.addPredicate(kedoKGPID, kedoKGID, "describes");
			this.builder.addPredicate(kedoKGID, kedoObjectID, "describes");
			
			//3. create the KEDO Type in KG
			String size = Integer.toString(Math.abs(new Random().nextInt()));
			String[] kedoType = createKEDOType(path, kedoKGID, "D2I", sizeLevel(Integer.valueOf(size)));

			String kedoTypeID = kedoType[0];
			String kedoTypePID = kedoType[2];
			if (kedoType[1] == "1") {
				//a new KEDO Type
						
				kiKG.put("etag", "KEDO Type PID");

				kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+kedoTypeID);
						
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
				
				createHandle(kedoTypePID, kiH, new HashMap<String, String>(), "KEDO Type PID");
				
				hm.createHandle(kedoTypePID, kiH);
				
				this.builder.addPredicate(kedoTypePID, kedoObjectPID, "aggregates");
				this.builder.addPredicate(kedoTypePID, kedoTypeID, "describes");
				this.builder.addPredicate(kedoTypeID, kedoObjectID, "describes");
				
			} else {
				//reuse the existing Type
				this.builder.addPredicate(kedoTypePID, kedoObjectPID, "aggregates");
				this.builder.addPredicate(kedoTypePID, kedoTypeID, "describes");
				this.builder.addPredicate(kedoTypeID, kedoObjectID, "specializationOf");

			}
			
			
			
			//4. create KEDO RO-Crate
			HashMap <String, String> root = new HashMap<String, String>();
			root.put("identifier", UUID.randomUUID().toString());
			root.put("pid", UUID.randomUUID().toString());
			root.put("size", size);
			root.put("path", path);
			
			String[] kedoRO = createKEDORO(root);
			String kedoROID = kedoRO[0];
			String kedoROPID = kedoRO[1];
			
			kiKG.put("etag", "KEDO RO PID");

			kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+kedoROID);
					
			kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
			
			createHandle(kedoROPID, kiH, new HashMap<String, String>(), "KEDO RO PID");
			
			this.builder.addPredicate(kedoROID, kedoKGID, "describes");
			this.builder.addPredicate(kedoROPID, kedoObjectPID, "aggregates");
			this.builder.addPredicate(kedoROPID, kedoROID, "describes");

			
			
			
			
			ArrayList<String> tempPID = new ArrayList<String>();
			
			for (int j =0 ;j < file_size; j++) {
				
				String filePID = UUID.randomUUID().toString();

				tempPID.add(filePID);
						
				
				//5. create file RO-Crate
				HashMap<String, String> fileM = new HashMap<String, String>();
				fileM.put("name", pair.get(new Random().nextInt(pair.size()))[1]);
				fileM.put("fileFormat", "nc");
				fileM.put("identifier", UUID.randomUUID().toString());
				fileM.put("filePID", filePID);
				fileM.put("pid", UUID.randomUUID().toString());
				fileM.put("contentSize", Integer.toString(Math.abs(new Random().nextInt())));
				fileM.put("checksum", UUID.randomUUID().toString());
				
				
				String[] internalRO = createInternalRO(fileM);
				String internalROID = internalRO[0];
				String internalROPID = internalRO[1];
					
				kiKG.put("etag", "ROCrate PID");
				kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+fileM.get("id"));
					
					
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
			
				createHandle(internalROPID, kiH, new HashMap<String, String>(), "ROCrate PID");
				
				hm.createHandle(internalROPID, kiH);
				
				this.builder.addPredicate(internalROPID,  kedoObjectPID, "aggregates");
				this.builder.addPredicate(internalROPID, internalROID, "describes");
				this.builder.addPredicate(internalROID, kedoROID, "isPartOf");
				
				
				
				//6. create handle for file
				kiKG.put("etag", "File PID");
				kiKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+fileM.get("localPath"));
					
					
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
							
				HashMap<String, String> provenance = new HashMap<String, String>();	
				
				List<Object> prov = Arrays.asList(new String[] {"wasDerivedFrom", "specializationOf","revisionOf","primarySourceOf","quotationOf","alternatOf"});
				
				ArrayList<String> idList = new ArrayList<String>();
				
				for (int k =0 ;k < 3; k++) {
					int index = new Random().nextInt(prov.size());
					
					String selectProv = prov.remove(index).toString();
					String selectID = "";
					if (pidList.size() != 0) {
						selectID = pidList.get(new Random().nextInt(pidList.size()));
					}
					
					if (!idList.contains(selectID) && !selectID.equals("")) {
						idList.add(selectID);
						provenance.put(selectProv, selectID);
					}
				
				}
				
				
				createHandle(fileM.get("filePID"), kiH, provenance, "File PID");
				
				kiH.putAll(provenance);
				
				hm.createHandle(fileM.get("filePID"), kiH);
				
				this.builder.addPredicate(fileM.get("filePID"), kedoObjectPID, "aggregates");

				
				//attributes
				String attID = UUID.randomUUID().toString();

				String query = "CREATE (kedo:Feature {";
				query += "type: 'Attribute', ";
				String[] tempV = pair.get(new Random().nextInt(pair.size()));
				query +=tempV[0] +  ": '" + tempV[1]+"', ";

				
				
				query += "id : '"+attID+"'})";

				this.builder.run(query);
				
				this.builder.addPredicate(attID, kedoKGID, "isReferencedBy");
				this.builder.addPredicate(attID, internalROID, "isReferencedBy");
				
				//variable
				
				ArrayList<String[]> variable = new ArrayList<String[]>();
				
				for (int q = 0; q < variable_size; q++) {	
					String[] var = pair.get(new Random().nextInt(pair.size()));
					while(!variable.contains(var)) {
						var = pair.get(new Random().nextInt(pair.size()));
					}
					query = "MATCH (a:Feature) WHERE ";

					query += "a."+var[0]+ " = '"+var[1]+"' And ";
					
					query += "a.type = 'Variable'";
					query += "RETURN (a.id)";
					
					
					Result output = this.builder.run(query);
					
					String featureID = "";

					if (output.hasNext()) {
						//existed Feature
						
						featureID = output.next().get(0).asString();
						this.builder.addPredicate(attID, featureID, "specializationOf");
						
					} else {
						// new Feature
						featureID = UUID.randomUUID().toString();
						query = "CREATE (kedo:Feature {";
						query += "type: 'Variable', ";
						query += "a."+var[0]+ " = '"+var[1]+"' And ";
						query += "id: '"+featureID+"'})";
						
						this.builder.run(query);
						this.builder.addPredicate(attID, featureID, "isDescribedBy");
						
					}		
					
				}
				
			}

			pidList.addAll(tempPID);
			
		}
		  
	}
	
	
	
	public void createHandle(String pid, HashMap<String, String> c, HashMap<String, String> prov, String name) throws IOException{
		String query = "CREATE (";
		query += "kedo:PID {id: '"+pid+"',";

		for (Map.Entry item : c.entrySet()) {
			query += item.getKey()+": '"+item.getValue()+"',";
		}
		
		for (Map.Entry item : prov.entrySet()) {
			query += item.getKey()+": '"+item.getValue()+"',";
		}
		query += "type: '"+name+"'})";

		this.builder.run(query);
        
		for (Map.Entry item : prov.entrySet()) {
			this.builder.addPredicate(pid, item.getValue().toString(), item.getKey().toString());
		}

	
	}
	
	public String[] createKEDO(String name, String path) throws IOException {
		String kedoID  = UUID.randomUUID().toString();
		String kedoPID = UUID.randomUUID().toString();
		
		String query = "CREATE (";
		query += "kedo:KEDO {id: '"+kedoID+"',";
		query += "type: '"+name+"',";
		if (path != "") {
			query += "pid: '"+kedoPID+"',";
			query += "location: '"+path+"'})";
		} else {
			query += "pid: '"+kedoPID+"'})";
		}
		
	
		this.builder.run(query);

		return new String[] {kedoID, kedoPID};
		
	}
	
	
	public String[] createKEDOKG(String name, String path) throws IOException {
		String kedoID  = UUID.randomUUID().toString();
		String kedoPID = UUID.randomUUID().toString();
		
		String query = "CREATE (";
		query += "kedo:KG {id: '"+kedoID+"',";
		query += "type: '"+name+"',";
		
		if (path != "") {
			query += "pid: '"+kedoPID+"',";
			query += "location: '"+path+"'})";
		} else {
			query += "pid: '"+kedoPID+"'})";
		}
			
		this.builder.run(query);
		
		return new String[] {kedoID, kedoPID};
		
	}
	
	public String[] createKEDOType(String path, String kedoKGID, String DLname, String size) throws IOException {		
		String typeID = UUID.randomUUID().toString();
		String typePID = UUID.randomUUID().toString();
			
		//QUERY
		
		String query = "MATCH (a:KEDOType)";
		query += "WHERE a.repository = '"+DLname+"' AND a.sizelevel = '"+size+"'";
		query += "RETURN (a.id)";
		
		Result output = this.builder.run(query);
        
		if (output.hasNext()) {
			
			//existed Type
			typeID = output.next().values().get(0).asString();
			query = "MATCH (a:KG), (b:KEDOType) WHERE a.id = '"+kedoKGID+"' "
					+ "AND b.id = '"+typeID+"' "
					+ "CREATE (a) -[:isAggregatedBy]-> (b)"
					+"RETURN (b.pid)";
			
			Result temp = this.builder.run(query);
	        
			typePID = temp.next().values().get(0).asString();
			return new String[] {typeID, "0" ,typePID};
			
		} else {
			
			//new Type
			query = "MATCH (a:KG)  WHERE a.id = '"+kedoKGID+"' "
					+ "CREATE (a) -[:isDescribedBy]-> (b:KEDOType {id: '"+typeID+"', "
					+ "type: 'KEDO Type', "
					+ "repository: '"+DLname+"', "
					+ "pid: '"+typePID+"', "
					+ "sizelevel: '"+size+"'})";

			this.builder.run(query);
			
			return new String[] {typeID, "1" ,typePID};
		}
		
		
				
	}
	
	public String[] createKEDORO(HashMap<String, String> info) throws IOException {
		String id = info.get("identifier");
		String query = "CREATE (kedo:ROCrate {";
		query += "type: 'File ROCrate', ";
		
		for (Map.Entry item : info.entrySet()) {
			
			if (!item.getKey().equals("identifier")) {
				query += item.getKey() + ": '"+item.getValue()+"', ";
			}
		}
		query += "id : '"+id+"'})";
		
		this.builder.run(query);

		
		return new String[] {id, info.get("pid")};
	}
	
	
	public String[] createInternalRO(HashMap<String, String> fileM) throws IOException {
		String id = fileM.get("identifier");
		
		String query = "CREATE (kedo:ROCrate {";
		query += "type: 'File ROCrate', ";
		
		for (Map.Entry item : fileM.entrySet()) {
			query += item.getKey() + ": '"+item.getValue()+"', ";
		}
		query += "id : '"+id+"'})";
		
		this.builder.run(query);
        
		
		
		
		return new String[] {id, fileM.get("pid")};
	}

	
	public void createFeature(HashMap<String, String> fileM, String KEDOKGID) throws Exception {
		String attID = UUID.randomUUID().toString();
		
		//attributes
		
		ReadInfo info = new ReadInfo(fileM.get("localPath"));
		String query = "CREATE (kedo:Feature {";
		query += "type: 'Attribute', ";
		
		for (Map.Entry item : info.getGlobalAttributes().entrySet()) {
			if (!item.getKey().toString().equals("pid")) {
				if (item.getValue() == null) {
					query +=item.getKey()+  ": 'null', ";
				} else {
					query +=item.getKey() +  ": '" + item.getValue()+"', ";
				}
				
			}
			
		}
		
		
		query += "id : '"+attID+"'})";

		this.builder.run(query);
		
		this.builder.addPredicate(attID, KEDOKGID, "isReferencedBy");
		
		//variable
		
		HashMap<String, String> variable = new HashMap<String, String>();
		
		for (Map.Entry item : info.getVariables().entrySet()) {	
			for (Map.Entry data : ((HashMap<String, String>) item.getValue()).entrySet()) {
		
				if (data.getValue() == null) {
					variable.put(data.getKey().toString(), "null");
				}else {
					variable.put(data.getKey().toString(), data.getValue().toString());
				}
				
			}
			query = "MATCH (a:Feature) WHERE ";
			
			for (Map.Entry itemV : variable.entrySet()) {
				query += "a."+itemV.getKey()+ " = '"+itemV.getValue()+"' And ";
			}
			query += "a.type = 'Variable'";
			query += "RETURN (a.id)";
			
			
			Result output = this.builder.run(query);
			
			String featureID = "";

			if (output.hasNext()) {
				//existed Feature
				featureID = output.next().get(0).asString();
				this.builder.addPredicate(attID, featureID, "specializationOf");
				
			} else {
				// new Feature
				featureID = UUID.randomUUID().toString();
				query = "CREATE (kedo:Feature {";
				query += "type: 'Variable', ";
				for (Map.Entry itemV : variable.entrySet()) {
					query += itemV.getKey()+": '"+itemV.getValue()+"', ";
				}
				query += "id: '"+featureID+"'})";
				
				this.builder.run(query);

				
				this.builder.addPredicate(attID, featureID, "isDescribedBy");
				
			}		
			
		}
		
	}
	
	public void createInsight(String dataPID, String containerPID, String varIRI, String runCommand, String result, String description, String checksum, String KEDOObjectPID) throws Exception {
	
		String id = UUID.randomUUID().toString();
		String pid = UUID.randomUUID().toString();
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String newDate = formatter.format(new Date());
		
		HashMap<String, String> newKIKG = new HashMap<String, String>();
		newKIKG.put("dateCreation", newDate);
		newKIKG.put("etag", "Insight PID");
		
		newKIKG.put("lastModified", newDate);
		newKIKG.put("digitalObjectLocation", "queryPage?query=local&knowledge="+id);
		
		HashMap<String, String> newkiH = hm.KI(newKIKG, new HashMap<String, String>(), new HashMap<String, String>());
				
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("source", dataPID);
		content.put("script", containerPID);
		content.put("outcome", result);
		content.put("wrokflow", runCommand);
		content.put("description", description);
		content.put("checksum", checksum);
		content.put("pid", pid);
			
		
		String query = "CREATE (kedo:Insight {";
		query += "type : 'Insight', ";
		
		for (Map.Entry item: content.entrySet()) {
			query+= item.getKey() + ": '" + item.getValue() + "', ";
		}
		
		query += "id : '"+id+"'})";

		this.builder.run(query);

		this.builder.addPredicate(id, varIRI, "aggregates");
		

        createHandle(pid, newKIKG, new HashMap<String, String>(), "Insight PID");
		

		this.PIDtable.put(pid, "Insight PID");

        hm.createHandle(pid, newkiH);

        this.builder.addPredicate(pid, id, "describes");
        
        this.builder.addPredicate(pid, KEDOObjectPID, "aggregates");
        
		
	}
	
		
	
	public String sizeLevel(long size) {
		
		if ((size = size/1024) < 1) {
			return "Bytes";
		} else if ((size = size/1024) < 1) {
			return "KB";
		} else if ((size = size/1024) < 1) {
			return "MB";
		} else {
			return "GB";
		}
	}

	
	public static void main(String[] args) throws Exception {
		
	}

}
