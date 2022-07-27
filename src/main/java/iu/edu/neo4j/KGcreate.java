package iu.edu.neo4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import iu.edu.handle.HandleManager;
import iu.edu.handle.HandleTable;
import iu.edu.mongo.HandleTableMongo;
import iu.edu.netcdf.ReadInfo;
import iu.edu.ro.ROCrateDescriptor;

public class KGcreate {

	private HandleTable ht;
	private HandleManager hm;
	private HandleTableMongo hmo;

	private String service = "";
	private HashMap<String, String> pidTable1 = new HashMap<String, String>();
	private HashMap<String, String> pidTable2 = new HashMap<String, String>();
	private HashMap<String, String> kiKG = new HashMap<String, String>();
	private HashMap<String, String> kiH = new HashMap<String, String>();
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    private String date;
    private String kedoObjectPID;
    private iu.edu.kedo.Property input;
    private HashMap<String,String> PIDtable;

    private KGbuilder builder;

	
	

	public KGcreate() throws Exception {
		this.input = new iu.edu.kedo.Property();

		this.service = input.property.getProperty("kedo.service")+"download?path=";
		
		this.ht = new HandleTable();
		this.hm = new HandleManager();
		this.hmo = new HandleTableMongo();
		this.date = formatter.format(new Date());
		this.PIDtable = new HashMap<String, String>();

		this.builder = new KGbuilder();

	}
	
    
    

	
	public HashMap<String, String> createKG(String path) throws Exception{
		HashMap<String, String> PIDtableResult = new HashMap<String, String> ();
		
		ROCrateDescriptor ro = new ROCrateDescriptor();
		
		//1. create KEDO Object
		
		String[] kedo = createKEDO("KEDO Object", path);
		String kedoObjectID = kedo[0];
		kedoObjectPID = kedo[1];
		
		kiKG.put("dateCreation", date);
        kiKG.put("etag", "KEDO Object PID");
        kiKG.put("lastModified", date);
        kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoObjectID);
        
        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());

        createHandle(kedoObjectPID, kiH, new HashMap<String, String>(), "KEDO Object PID");

        hm.createHandle(kedoObjectPID, kiH);
        
        this.builder.addPredicate(kedoObjectPID, kedoObjectID, "describes");

        
        PIDtableResult.put(kedoObjectPID, "KEDO Object PID");

		//2. create KEDO KG

        String[] kg = createKEDOKG("KEDO KG", "");
		String kedoKGID = kg[0];
		String kedoKGPID = kg[1];
			
        kiKG.put("dateCreation", date);
        kiKG.put("etag", "KEDO KG PID");
        kiKG.put("lastModified", date);
        kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoKGID);
           
        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
		
		createHandle(kedoKGPID, kiH, new HashMap<String, String>(), "KEDO KG PID");

		hm.createHandle(kedoKGPID, kiH);

		
		this.builder.addPredicate(kedoKGPID, kedoObjectPID, "aggregates");
		this.builder.addPredicate(kedoKGPID, kedoKGID, "describes");
		this.builder.addPredicate(kedoKGID, kedoObjectID, "describes");

		pidTable1.put(kedoKGPID, "KEDO KG PID");
		PIDtableResult.put(kedoKGPID, "KEDO KG PID");

		//3. create the KEDO Type in KG
			
		String[] kedoType = createKEDOType(path, kedoKGID, this.input.property.getProperty("DLname"));

		String kedoTypeID = kedoType[0];
		String kedoTypePID = kedoType[2];
		if (kedoType[1] == "1") {
			//a new KEDO Type
					
			kiKG.put("etag", "KEDO Type PID");

			kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoTypeID);
					
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
		
		
		
		pidTable1.put(kedoTypePID, "KEDO Type PID");
		PIDtableResult.put(kedoTypePID, "KEDO Type PID");
		
		    
		//4. create KEDO RO-Crate
		List<String> hasPart = ro.hasPart(path);
		HashMap <String, String> root = ro.rootCollection(path);
		
		String[] kedoRO = createKEDORO(root);
		String kedoROID = kedoRO[0];
		String kedoROPID = kedoRO[1];
		
		kiKG.put("etag", "KEDO RO PID");

		kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoROID);
				
		kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
		
		createHandle(kedoROPID, kiH, new HashMap<String, String>(), "KEDO RO PID");
		
		this.builder.addPredicate(kedoROID, kedoKGID, "describes");
		this.builder.addPredicate(kedoROPID, kedoObjectPID, "aggregates");
		this.builder.addPredicate(kedoROPID, kedoROID, "describes");
		
		pidTable1.put(kedoROPID, "KEDO RO PID");
		PIDtableResult.put(kedoROPID, "KEDO RO PID");
		
		
		for (String i : hasPart) {
			
			File file = new File(i);
			
			if (file.isFile()) {
				pidTable2 = new HashMap<String, String>();
				//5. create file RO-Crate
				HashMap<String, String> fileM = ro.file(i);
				
				String[] internalRO = createInternalRO(fileM);
				String internalROID = internalRO[0];
				String internalROPID = internalRO[1];
					
				kiKG.put("etag", "ROCrate PID");
				//*************************
				kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+fileM.get("id"));
					
					
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
			
				createHandle(internalROPID, kiH, new HashMap<String, String>(), "ROCrate PID");
				
				hm.createHandle(internalROPID, kiH);
				
				this.builder.addPredicate(internalROPID,  kedoObjectPID, "aggregates");
				this.builder.addPredicate(internalROPID, internalROID, "describes");
				this.builder.addPredicate(internalROID, kedoROID, "isPartOf");
				
				pidTable1.put(fileM.get("pid"), "RO-Crate PID");
				pidTable2.put(fileM.get("pid"), "RO-Crate PID");
				
				PIDtableResult.put(internalROPID, "RO PID");
					
				//6. create handle for file
				kiKG.put("etag", "File PID");
				kiKG.put("digitalObjectLocation", this.service+fileM.get("localPath"));
					
				// update the exact file provenance *****
					
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
							
				HashMap<String, String> provenance = new HashMap<String, String>();		
				
				createHandle(fileM.get("filePID"), kiH, provenance, "File PID");
				
				kiH.putAll(provenance);
				
				hm.createHandle(fileM.get("filePID"), kiH);
				
				this.builder.addPredicate(fileM.get("filePID"), kedoObjectPID, "aggregates");
					
				pidTable1.put(fileM.get("filePID"), "File PID");
				
				PIDtableResult.put(fileM.get("filePID"), "File PID");
								

				if (fileM.get("fileFormat").equals("nc")) {
					//9. create Feature in KG
					//*****************************
					
					createFeature(fileM, kedoKGID, internalROID);		
			
				}
				
				this.hmo.insertDocument(fileM.get("filePID"), pidTable2);
				
			} else {
				ro.subFolder(i);
			}	
		}
		
		//7. insert the PID table in MongoDB
		this.hmo.insertDocument(kedoObjectPID, pidTable1);
		this.hmo.close();
		return PIDtableResult;
		
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
	
	public String[] createKEDOType(String path, String kedoKGID, String DLname) throws IOException {		
		String typeID = UUID.randomUUID().toString();
		String typePID = UUID.randomUUID().toString();
		

		String size = sizeLevel(Files.size(Paths.get(path)));
			
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

	
	public void createFeature(HashMap<String, String> fileM, String KEDOKGID, String internalROID) throws Exception {
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
		this.builder.addPredicate(attID, internalROID, "isReferencedBy");
		
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
		newKIKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+id);
		
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
