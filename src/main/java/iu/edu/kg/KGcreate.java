package iu.edu.kg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import iu.edu.handle.HandleManager;
import iu.edu.handle.HandleTable;
import iu.edu.mongo.HandleTableMongo;
import iu.edu.netcdf.ReadInfo;
import iu.edu.ro.ROCrateDescriptor;


public class KGcreate {
	
	private KGbuilder kg;
	private HandleTable ht;
	private HandleManager hm;
	private HandleTableMongo hmo;
	private String gdb1 = "";
	private String service = "";
	private Property propertyPID = ModelFactory.createDefaultModel().createProperty("http://www.entity.com/field#pid");
	private HashMap<String, String> pidTable1 = new HashMap<String, String>();
	private HashMap<String, String> pidTable2 = new HashMap<String, String>();
	private HashMap<String, String> kiKG = new HashMap<String, String>();
	private HashMap<String, String> kiH = new HashMap<String, String>();
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    private String date;
    private String kedoObjectPID;
    private iu.edu.kedo.Property input;
	
	
	public KGcreate() throws Exception {
		this.input = new iu.edu.kedo.Property();
		this.gdb1 = input.property.getProperty("graphdb");
		this.service = input.property.getProperty("kedo.service")+"download?path=";
		this.kg = new KGbuilder();
		this.ht = new HandleTable();
		this.hm = new HandleManager();
		this.hmo = new HandleTableMongo();
		this.date = formatter.format(new Date());
	}
	

	
	public HashMap<String, String> createKG(String path) throws Exception{
		
		HashMap<String, String> PIDtableResult = new HashMap<String, String> ();
		
		ROCrateDescriptor ro = new ROCrateDescriptor();

		
		//1. create KEDO Object
		Resource kedoObject = createKEDO("KEDO Object", path);
		String kedoObjectID = kedoObject.getURI();
		kedoObjectPID = kedoObject.getProperty(propertyPID).getString();
		
		kiKG.put("dateCreation", date);
        kiKG.put("etag", "KEDO Object PID");
        kiKG.put("lastModified", date);
        kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoObjectID);
        
        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
        
        createHandle(kedoObjectPID, kiH);
        hm.createHandle(kedoObjectPID, kiH);
        
        this.kg.addPredicate(kedoObjectPID,"http://www.openarchives.org/ore/terms#describes", kedoObjectID);
        
        PIDtableResult.put(kedoObjectPID, "KEDO Object PID");
        
        
		
		//2. create KEDO KG
		Resource kedoKG = createKEDO("KEDO KG", "");
		String kedoKGID = kedoKG.getURI();
		String kedoKGPID = kedoKG.getProperty(propertyPID).getString();
		
			
        kiKG.put("dateCreation", date);
        kiKG.put("etag", "KEDO KG PID");
        kiKG.put("lastModified", date);
        kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoKGID);
        
        kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
		
		createHandle(kedoKGPID, kiKG);
		hm.createHandle(kedoKGPID, kiH);
		
		this.kg.addPredicate(kedoKGPID, "http://www.openarchives.org/ore/terms#aggregates", kedoObjectPID);
		this.kg.addPredicate(kedoKGPID, "http://www.openarchives.org/ore/terms#describes", kedoKGID);
		
		pidTable1.put(kedoKGPID, "KEDO KG PID");

		PIDtableResult.put(kedoKGPID, "KEDO KG PID");
		
		//3. create the KEDO Type in KG
		Resource kedoType = createKEDOType(path, "D2I", kedoKG.getURI());
		String kedoTypeID = kedoType.getURI();
		String kedoTypePID = kedoType.getProperty(propertyPID).getString();
		if (kedoType.hasProperty(RDF.type)) {
			//a new KEDO Type
					
			kiKG.put("etag", "KEDO Type PID");
			kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+kedoTypeID);
			
			kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
			
			createHandle(kedoType.getProperty(propertyPID).getString(), kiH);
			hm.createHandle(kedoTypePID, kiH);
			
			this.kg.addPredicate(kedoTypePID, "http://www.openarchives.org/ore/terms#aggregates", kedoObjectPID);	
			this.kg.addPredicate(kedoTypePID, "http://www.openarchives.org/ore/terms#describes", kedoTypeID);
			
		} else {
			//reuse the existing Type
			
			this.kg.addPredicate(kedoTypePID, "http://www.openarchives.org/ore/terms#aggregates", kedoObjectPID);	
			this.kg.addPredicate(kedoTypePID, "http://www.openarchives.org/ore/terms#describes", kedoTypeID);
			
		}
		
		pidTable1.put(kedoTypePID, "KEDO Type PID");
		
		this.kg.publishKG(gdb1);
		PIDtableResult.put(kedoTypePID, "KEDO Type PID");
		
		
		//4. create KEDO RO-Crate
		List<String> hasPart = ro.hasPart(path);
		HashMap <String, String> root = ro.rootCollection(path);
			
		this.kg = new KGbuilder();
		
		for (String i : hasPart) {
			File file = new File(i);
			if (file.isFile()) {
				pidTable2 = new HashMap<String, String>();
				//5. create file RO-Crate
				
				HashMap<String, String> fileM = ro.file(i);
			
				Resource internalRO = createInternalRO(fileM, kedoKG);
				String internalROID = internalRO.getURI();
				String internalROPID = internalRO.getProperty(propertyPID).getString();
				
				kiKG.put("etag", "RO-Crate PID");
				kiKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+internalRO.getURI());
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
		
				createHandle(internalROPID, kiH);
				hm.createHandle(internalROPID, kiH);
				
				this.kg.addPredicate(internalROPID, "http://www.openarchives.org/ore/terms#aggregates", kedoObjectPID);
				this.kg.addPredicate(internalROPID, "http://www.openarchives.org/ore/terms#describes", internalROID);
				
				pidTable1.put(fileM.get("pid"), "RO-Crate PID");
				pidTable2.put(fileM.get("pid"), "RO-Crate PID");
				
				PIDtableResult.put(internalROPID, "RO PID");
				
				//6. create handle for file
				kiKG.put("etag", "File PID");
				kiKG.put("digitalObjectLocation", this.service+fileM.get("localPath"));
				kiH = hm.KI(kiKG, new HashMap<String, String>(), new HashMap<String, String>());
				
				createHandle(fileM.get("filePID"), kiH);
				hm.createHandle(fileM.get("filePID"), kiH);
				
				this.kg.addPredicate(fileM.get("filePID"), "http://www.openarchives.org/ore/terms#aggregates", kedoObjectPID);
				
				pidTable1.put(fileM.get("filePID"), "File PID");
				
				PIDtableResult.put(fileM.get("filePID"), "File PID");
				

				if (fileM.get("fileFormat").equals("nc")) {
					
					//9. create Feature in KG
					createFeature(fileM);
					
					
				}
				
				this.hmo.insertDocument(fileM.get("filePID"), pidTable2);
				this.kg.publishKG(gdb1);
				
				
			} else {
				ro.subFolder(i);
			}
			
		}
		//7. insert the PID table in MongoDB
		this.hmo.insertDocument(kedoObjectPID, pidTable1);
		
		this.hmo.close();
		ht.publish(gdb1);

		ro.addGraph();
		
		for (Map.Entry item : PIDtableResult.entrySet()) {
			System.out.println(item.getKey().toString());
			System.out.println(item.getValue().toString());
		}
		
		return PIDtableResult;
		
	}
	
	
	
	
	public void createHandle(String pid, HashMap<String, String> c){
		HashMap<String, String> cn = new HashMap<String, String>();

		String propertyns = "http://www.entity.com/field#";
		for (Map.Entry item : c.entrySet()) {
			if (item.getKey().toString().equals("etag")) {
				cn.put(RDFS.label.toString(), item.getValue().toString());
			}
			cn.put(propertyns+item.getKey().toString(), item.getValue().toString());
		}
		
		cn.put(RDF.type.toString(), "PID");
		this.kg.createResource("", pid, cn);
	
	}
	
	public Resource createKEDO(String name, String path) {
		String hs = "http://www.kedo.com/";
		String prefix = this.input.property.getProperty("handle.restful.api.url");
		String s = UUID.randomUUID().toString();
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(RDF.type.toString(), name);
		properties.put(RDFS.label.toString(), name);
		properties.put("http://www.entity.com/field#pid", prefix+s);
		if (path != "") {
			properties.put("http://www.entity.com/field#location", path);
		}
		
		return this.kg.createResource(hs, s, properties);
		
	}
	
	public Resource createKEDOType(String path, String DLname, String KEDOurl) throws IOException {
		

		HashMap<String, String> properties = new HashMap<String, String>();
		
		properties.put("http://www.entity.com/field#repository", DLname);
		properties.put("http://www.entity.com/field#sizelevel", sizeLevel(Files.size(Paths.get(path))));
		properties.put(RDF.type.toString(), "KEDO Type");
		properties.put(RDFS.label.toString(), "KEDO Type");
		//QUERY
		KGquery query = new KGquery();
		String[] result = query.queryKEDOType(properties, gdb1).split(",");
		
		if (result.length>2) {
			String existedType = result[result.length-2];
			Resource KEDOType = ModelFactory.createDefaultModel().createResource(existedType);
			KEDOType.addProperty(ModelFactory.createDefaultModel().createProperty("http://www.entity.com/field#pid"),
					result[result.length-1]);
			this.kg.addPredicate(KEDOurl, "http://www.openarchives.org/ore/terms#isAggregatedBy", existedType);
			
			return KEDOType;
		} else {
			String hs = "http://www.kedo.com/";
			String prefix =  this.input.property.getProperty("handle.restful.api.url");
			String s = UUID.randomUUID().toString();
			//properties.put("http://www.entity.com/field#pid", prefix+s);
			//CREATE
			Resource KEDOType = this.kg.createResource(hs, s, properties);
			
			this.kg.addPredicate(KEDOurl, "http://www.openarchives.org/ore/terms#isDescribedBy", hs+s);
			
			return KEDOType;
		}
				
	}
	
	public Resource createKEDORO(HashMap<String, String> c, Resource KEDO) {
		HashMap<String, String> cn = new HashMap<String, String>();
		String pid = c.get("pid");
		String hs = pid.split(this.input.property.getProperty("handle.prefix")+"/")[1];
		
		String propertyns = "http://www.entity.com/field#";
		for (Map.Entry item : c.entrySet()) {
			cn.put(propertyns+item.getKey().toString(), item.getValue().toString());
		}
		
		cn.put(RDF.type.toString(), "RO-Crate");
		cn.put(RDFS.label.toString(), "KEDO RO-Crate");
		//cn.put("http://www.entity.com/field#pid", pid);
		Resource KEDORO =  this.kg.createResource("http://www.ro.com/", hs, cn);
		
		this.kg.addPredicate(KEDORO, "http://www.openarchives.org/ore/terms#describes", KEDO);
		
		return KEDORO;
	}
	
	public Resource createInternalRO(HashMap<String, String> fileM, Resource KEDORO) {
		HashMap<String, String> cn = new HashMap<String, String>();
		String pid = fileM.get("pid");
		String hs = pid.split(this.input.property.getProperty("handle.prefix")+"/")[1];
		String propertyns = "http://www.entity.com/field#";
		for (Map.Entry item : fileM.entrySet()) {
			cn.put(propertyns+item.getKey().toString(), item.getValue().toString());		
		}
		
		cn.put(RDF.type.toString(), "RO-Crate");
		cn.put(RDFS.label.toString(), "RO-Crate");
		Resource InternalRO =  this.kg.createResource("http://www.ro.com/", hs, cn);
		
		this.kg.addPredicate(InternalRO, "http://www.openarchives.org/ore/terms#isPartOf", KEDORO);
		
		return InternalRO;
	}
	
	public Resource createFeature(HashMap<String, String> fileM) throws IOException {
		String pid = fileM.get("pid");
		String hs = pid.split(this.input.property.getProperty("handle.prefix")+"/")[1];
		
		
		ReadInfo info = new ReadInfo(fileM.get("localPath"));
		HashMap<String, String> attribute = new HashMap<String, String>();
		for (Map.Entry item : info.getGlobalAttributes().entrySet()) {
			if (!item.getKey().toString().equals("pid")) {
				attribute.put("http://www.entity.com/field#"+item.getKey().toString(), item.getValue().toString());
			}
			
		}
		attribute.put(RDF.type.toString(), "Feature");
		attribute.put(RDFS.label.toString(), "Attritbue");
		
		Resource attE = this.kg.createResource("http://www.feature.com/", hs, attribute);
		this.kg.addPredicate(attE.getURI(), "http://www.openarchives.org/ore/terms#isReferencedBy", "http://www.ro.com/"+hs);
		for (Map.Entry item : info.getVariables().entrySet()) {
			HashMap<String, String> variable = new HashMap<String, String>();
			for (Map.Entry data : ((HashMap<String, String>) item.getValue()).entrySet()) {
				variable.put("http://www.entity.com/field#"+data.getKey().toString(), data.getValue().toString());
			}
			variable.put(RDF.type.toString(), "Feature");
			variable.put(RDFS.label.toString(), item.getKey().toString());
			
			KGquery query = new KGquery();
			String result = query.queryFeature(variable, gdb1);
			
			if (result.length()>1) {
				String existedType = result.split("x")[1];
				Resource varE = ModelFactory.createDefaultModel().createResource(existedType);
				this.kg.addPredicate(attE.getURI(), "http://www.openarchives.org/ore/terms#specializationOf", varE.getURI());
				
				String temp1 = query.querySharedPID(variable, gdb1);
				String existedObject = temp1.split("w")[1];
				this.pidTable2.put(existedObject, "Share");
				
			} else {
				Resource varE = this.kg.createResource("http://www.feature.com/", UUID.randomUUID().toString(), variable);
			
				this.kg.addPredicate(attE.getURI(), "http://www.openarchives.org/ore/terms#isDescribedBy", varE.getURI());
				
			}
			
		}
		
		return null;
	}
	
	public String createInsight(String dataPID, String containerPID, String varIRI, String runCommand, String result, String description) throws Exception {
		String hs = "http://www.kedo.com/";
		String prefix = input.property.getProperty("handle.restful.api.url");
		String s = UUID.randomUUID().toString();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String newDate = formatter.format(new Date());
		
		HashMap<String, String> newKIKG = new HashMap<String, String>();
		newKIKG.put("dateCreation", newDate);
		newKIKG.put("etag", "Insight PID");
		
		newKIKG.put("lastModified", newDate);
		newKIKG.put("digitalObjectLocation", this.input.property.getProperty("kedo.service")+"queryPage?query=local&knowledge="+hs+s);
		
		HashMap<String, String> newkiH = hm.KI(newKIKG, new HashMap<String, String>(), new HashMap<String, String>());
		
		String checksum = new KGquery().queryChecksum(dataPID, this.input.property.getProperty("graphdb")).split("\n")[1];
		
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("http://www.entity.com/field#source", dataPID);
		content.put("http://www.entity.com/field#script", containerPID);
		content.put("http://www.entity.com/field#outcome", result);
		content.put("http://www.entity.com/field#wrokflow", runCommand);
		content.put("http://www.entity.com/field#description", description);
		content.put("http://www.entity.com/field#checksum", checksum);
		content.put(RDF.type.toString(), "Insight");
		content.put(RDFS.label.toString(), "Insight");
		content.put("http://www.entity.com/field#pid", prefix+s);
			
		
		
		KGbuilder newKG = new KGbuilder();
		Resource insight = newKG.createResource(hs, s, content);

		newKG.addPredicate(insight.getURI(), "http://www.openarchives.org/ore/terms#aggregates", varIRI);
		
        
        createHandle(prefix+s, kiKG);
        hm.createHandle(prefix+s, newkiH);
        
        newKG.addPredicate(insight.getURI(), "http://www.openarchives.org/ore/terms#isReferencedBy", prefix+s);
        
        String KEDOObjectPID = new KGquery().queryfileKEDOObjectPID(dataPID, this.input.property.getProperty("graphdb")).split("\n")[1];
        
        newKG.addPredicate(prefix+s, "http://www.openarchives.org/ore/terms#aggregates", KEDOObjectPID);
        
        HandleTableMongo mongo = new HandleTableMongo();
        Document filePIDtable = mongo.query(dataPID).get(0);
        mongo.update(dataPID, "Insight PID", prefix+s, filePIDtable);
        
        Document kedoPIDtable = mongo.query(KEDOObjectPID).get(0);
        mongo.update(KEDOObjectPID, "Insight PID", prefix+s, kedoPIDtable);

        newKG.publishKG(this.input.property.getProperty("graphdb"));
        
        return prefix+s;
		
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
