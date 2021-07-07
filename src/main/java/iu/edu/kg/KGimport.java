package iu.edu.kg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import iu.edu.handle.HandleManager;

public class KGimport {
	private static Property propertyPID = ModelFactory.createDefaultModel().createProperty("http://www.entity.com/field#pid");
	
	
	
	
	public static void main(String[] args) throws Exception {
		String path = "/Users/luoyu/Desktop/KG/example/rdf.ttl";
		Model model = ModelFactory.createDefaultModel();
		model.read(new FileInputStream(path),null,"TTL");

		Resource feature = model.getResource("http://www.feature.com/5ee4b3e3-2cc1-492f-9e1b-bb1e205ce29c");
		List<Statement> temp = feature.listProperties().toList();
		Model newM = ModelFactory.createDefaultModel();
		for (Statement item : temp) {
			//System.out.println(item);
			//System.out.println(item);
			newM.add(item);
			
		
		}
		
		
		String pidService = "http://localhost:8082/queryPage?query=local&knowledge=";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String date = formatter.format(new Date());
		//1. create KEDO Object
		Model output = ModelFactory.createDefaultModel();
		String hs = "http://www.kedo.com/";
		String prefix = "http://35.83.244.177:8000/api/handles/20.500.12033/";
		String s = UUID.randomUUID().toString();
		Resource kedoObject = output.createResource(hs+s);
		
		Resource oldKGObject = model.listResourcesWithProperty(RDF.type, "KEDO Object").toList().get(0);
		List<Statement> property = oldKGObject.listProperties().toList();
		String oldKEDOObjectPID = "";
		for (Statement item : property) {
			Property pro = item.getPredicate();
			if (pro.toString().contains("pid")) {
				oldKEDOObjectPID = item.getObject().toString();
				kedoObject.addProperty(pro, prefix+s);
			} else if (pro.toString().contains("location")) {
				kedoObject.addProperty(pro, path);
			} else {
				
				kedoObject.addProperty(pro, item.getLiteral());
			}	
		}
		
		//1.1 create Handle for kedo object
		Resource oldKEDOObjectPIDentity = model.getResource(oldKEDOObjectPID);
		System.out.println(1);
		System.out.println(oldKEDOObjectPID);
		System.out.println(2);
		Resource kedoObjectPID = output.createResource(prefix+s);
		property = oldKEDOObjectPIDentity.listProperties().toList();
		
		HashMap<String, String> kiKG = new HashMap<String, String>();
		
		for (Statement item : property) {
			Property pro = item.getPredicate();
			if (pro.toString().contains("digitalObjectLocation")) {
				kedoObjectPID.addProperty(pro, pidService+hs+s);
				kiKG.put(pro.toString().split("#")[1], pidService+hs+s);
			} else if (pro.toString().contains("alternateOf") ||
					pro.toString().contains("primarySourceOf") ||
					pro.toString().contains("quotationOf") ||
					pro.toString().contains("revisionOf") ||
					pro.toString().contains("wasDerivedFrom")) {		
				if (!item.getObject().toString().equals("None")) {
					Statement stat = output.createStatement(kedoObjectPID, pro, item.getObject().toString());
					output.add(stat);
					kiKG.put(pro.toString().split("#")[1], item.getObject().toString());
				} else {
					kedoObjectPID.addProperty(pro, "None");
					kiKG.put(pro.toString().split("#")[1], item.getObject().toString());
				}
				
			} else if (pro.toString().contains("dataCreation") || pro.toString().contains("lastModified")) {
				kedoObjectPID.addProperty(pro, date);
				kiKG.put(pro.toString().split("#")[1], date);
			} else if (pro.toString().contains("describes")){
				Statement stat = output.createStatement(kedoObjectPID, pro, kedoObject);
				output.add(stat);
			} else if (pro.toString().contains("specializationOf")){
				if (item.getObject().toString().equals("None")) {
					Statement stat = output.createStatement(kedoObjectPID, pro, oldKEDOObjectPIDentity.getURI());
					output.add(stat);
					String subjectList = item.getSubject().toString();		
					kiKG.put(pro.toString().split("#")[1], oldKEDOObjectPIDentity.getURI());
				} else {
					Statement stat = output.createStatement(kedoObjectPID, pro, item.getObject().toString());
					output.add(stat);
					stat = output.createStatement(kedoObjectPID, pro, oldKEDOObjectPIDentity.getURI());
					output.add(stat);
					String subjectList = item.getSubject().toString();		
					subjectList = (String) subjectList.subSequence(1, subjectList.length());
					kiKG.put(pro.toString().split("#")[1], subjectList+","+oldKEDOObjectPIDentity.getURI());
				}
				
			} else {
				kedoObjectPID.addProperty(pro, item.getLiteral());
				kiKG.put(pro.toString().split("#")[1], item.getLiteral().toString());
			}
		}
		output.write(System.out);
		HandleManager hm = new HandleManager();
		hm.createHandle(prefix+s, kiKG);
		
		
		
		HashMap<String, String> filePIDwithRO = new HashMap<String, String>();
		List<Resource> oldFilePIDlist = model.listResourcesWithProperty(RDFS.label, "File PID").toList();
		
		for (Resource item : oldFilePIDlist) {
			Resource relatedRO = model.listResourcesWithProperty(output.createProperty("http://www.entity.com/field#filePID"), item.getURI()).toList().get(0);
			filePIDwithRO.put(item.getURI(), relatedRO.getURI());
		}
		
		
		List<Resource> oldROPIDlist = model.listResourcesWithProperty(RDFS.label, "RO-Crate PID").toList();
		List<Resource> oldInsightPIDlist = model.listResourcesWithProperty(RDFS.label, "RO-Crate PID").toList();
		List<Resource> oldTypePIDlist = model.listResourcesWithProperty(RDFS.label, "KEDO Type PID").toList();

		
		List<Resource> PIDlist = model.listResourcesWithProperty(RDF.type, "PID").toList();
		
		
		
	}
	
	

}
