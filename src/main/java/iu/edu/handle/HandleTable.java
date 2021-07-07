package iu.edu.handle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import iu.edu.kg.KGbuilder;

public class HandleTable {
	public KGbuilder handleT;
	
	public HandleTable() {
		this.handleT = new KGbuilder();
		
		
	}
	
	public void print() {
		this.handleT.print();
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
		this.handleT.createResource("", pid, cn);
	
	}
	
	public void addProvenance(String pid, HashMap<String, String> p) {
		String propertyns = "http://www.entity.com/provenance#";
		for (Map.Entry item : p.entrySet()) {
			this.handleT.addPredicate(pid, propertyns+item.getKey().toString(), item.getValue().toString());
		}
	}
	
	public void addGraph(String pid, String property, String id) {
		
		this.handleT.addPredicate(pid, property, id);
		
	}
	
	public void publish(String gdb) throws IOException {
		//this.handleT.print();
		this.handleT.publishKG(gdb);
	}
	
}
