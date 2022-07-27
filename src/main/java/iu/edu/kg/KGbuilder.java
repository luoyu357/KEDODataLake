package iu.edu.kg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class KGbuilder {
	private Model model;
	
	
	public KGbuilder(){
		this.model = ModelFactory.createDefaultModel();
	}
	
	public void print() {
		this.model.write(System.out, "TTL");
		
	}
	
	
	public void publishKG(String gdb) throws IOException {
		URL url = new URL(gdb+"/statements");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoInput(true);
        con.setDoOutput (true);             
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-turtle");
        
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream()); 
        
        String syntax = "TTL"; // also try "N-TRIPLE" and "TURTLE"
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String result = out.toString();
		
        writer.write(result);                
        writer.flush(); 
        writer.close();
        //read the request
        BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        while ((response=reader.readLine())!=null) 
            System.out.println(response);
        
	}
	
	public String printKG() {
		String syntax = "TTL"; // also try "N-TRIPLE" and "TURTLE"
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		return out.toString();
	}
	
		
	
	public Resource createResource(String ns, String s, HashMap<String, String> properties) {
		Resource subject = model.createResource(ns+s);
		for (Map.Entry item : properties.entrySet()) {
			Property predicate = model.createProperty(item.getKey().toString());
			subject.addProperty(predicate, item.getValue().toString());
		}
		
		return subject;
	}
	
	public Resource createResource(String id, String property, String value) {
		Resource subject = model.createResource(id);
		Property predicate = model.createProperty(property);
		subject.addProperty(predicate, value);
		
		return subject;
	}
	
	
	public void addProperty(String id, String type, String value) {
		Resource subject = model.getResource(id);
		Property predicate = model.createProperty(type);
		subject.addProperty(predicate, value);
	}
	
	
	public void addPredicate(String s, String p, String o) {
		Resource subject = model.getResource(s);
		Property predicate = model.createProperty(p);
		Resource object = model.getResource(o);
		Statement stmt = model.createStatement(subject, predicate, object);
		model.add(stmt);	
	}
	
	public void addPredicate(Resource s, String p, Resource o) {
		Property predicate = model.createProperty(p);
		Statement stmt = model.createStatement(s, predicate, o);
		model.add(stmt);	
	}
	
	

	public static void main(String[] args) throws IOException {

		
	}

}
