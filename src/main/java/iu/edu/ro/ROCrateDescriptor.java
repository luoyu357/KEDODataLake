package iu.edu.ro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import iu.edu.kedo.Property;


public class ROCrateDescriptor {
	private JSONObject ro;
	
	public JSONArray graph;
	private String handleURL;
	
	private JSONObject root;
	
	public ROCrateDescriptor() throws IOException {
		this.ro = new JSONObject();
		this.ro.append("@context", "https://w3id.org/ro/crate/1.0/context");
		
		this.graph = new JSONArray();
		this.handleURL = (new Property()).property.getProperty("handle.restful.api.url");
	}
	
	
	public HashMap<String, String> rootCollection(String path) throws IOException {
		HashMap<String, String> output = new HashMap<String, String>();
		
		root = new JSONObject();
		root.put("@type", "CreativeWork");
		root.put("@id", "ro-crate-metadata.jsonld");
		root.put("conformsTo", new JSONObject().put("@id", "https://w3id/org/ro/crate/1.0"));
		
		String[] name = path.split("/");
		output.put("name", name[name.length-1]);
		
		JSONObject about = new JSONObject();
		about.put("@id", path);
		output.put("localPath", path);
		
		String uuid = UUID.randomUUID().toString();
		about.put("identifier", handleURL+uuid);
		output.put("pid", handleURL+uuid);
		
		about.put("category", "RO-Crate");
		output.put("category", "RO-Crate");
		
		long fileSize = Files.size(Paths.get(path));
		about.put("contentSize", fileSize);
		output.put("contentSize", String.valueOf(fileSize));
		
		root.put("about", about);
		
		this.graph.put(root);
		
		List<String> list = hasPart(path);
		String result = list.stream()
			      .map(n -> String.valueOf(n))
			      .collect(Collectors.joining(","));
		output.put("hasPart", result);
		
		
		return output;
	}
	
	
	public HashMap<String, String> file(String path) throws IOException, NoSuchAlgorithmException{
		HashMap<String, String> output = new HashMap<String, String>();
		
		JSONObject temp = new JSONObject();
		temp.put("@id", path);
		output.put("localPath", path);
		
		temp.put("@type", "File");
		
		String[] name = path.split("/");
		output.put("name", name[name.length-1]);
		
		String fileFormat = FilenameUtils.getExtension(path);
		temp.put("fileFormat", fileFormat);
		output.put("fileFormat", fileFormat);
		
		String uuid = UUID.randomUUID().toString();
		temp.put("identifier", handleURL+uuid);
		output.put("pid", handleURL+uuid);
		uuid = UUID.randomUUID().toString();
		temp.put("filePID", handleURL+uuid);
		output.put("filePID", handleURL+uuid);
		
		long fileSize = Files.size(Paths.get(path));
		temp.put("contentSize", fileSize);
		output.put("contentSize", String.valueOf(fileSize));
		
		File file = new File(path); 
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		String checksum = getFileChecksum(md5Digest, file);
		temp.put("checksum", checksum);
		output.put("checksum", checksum);
		
		graph.put(temp);
		
		return output;
	}
	
	public List<String> hasPart(String path) throws IOException{
		JSONArray temp = new JSONArray();
		
		Stream<Path> walk = Files.walk(Paths.get(path));
            // We want to find only regular files
		
        List<String> result = walk.filter(Files::isRegularFile)
        		.map(x -> x.toString()).collect(Collectors.toList());
        
        walk = Files.walk(Paths.get(path));
        result.addAll(walk.filter(Files::isDirectory)
        		.map(x -> x.toString()).collect(Collectors.toList()));
        
        result.remove(path);
        
        List<String> output = new ArrayList<String>();
        
        for (String i : result) {
        	if (!i.contains(".DS_Store")) {
        		temp.put(new JSONObject().put("@id", i));
        		output.add(i);
        	}
        }
        
        JSONObject structure = new JSONObject();
        structure.put("@id", path);
        structure.put("@type", new JSONArray().put("Dataset"));
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
        String date = formatter.format(new Date()); 
        structure.put("dataCreation", date);
        structure.put("hasPart", temp);
        
        graph.put(structure);
        
        return output;
	
	}
	
	
	public void subFolder(String path){
		JSONObject temp = new JSONObject();
		temp.put("@id", path);
		
		temp.put("@type", "Dataset");
		
		graph.put(temp);
	}
	
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0; 
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}
	
	public void addGraph() {
		ro.put("@graph", this.graph);
	}
	
	
	public void write(String path) throws IOException {
		FileWriter file = new FileWriter(path+"/ROCrate.json");
		file.write(this.ro.toString(4));
		file.flush();
		file.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		
	}

}


