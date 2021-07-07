package iu.edu.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import iu.edu.kedo.Property;



public class HandleTableMongo {
	public MongoClient mongoClient;
	public MongoDatabase database;
	
	public HandleTableMongo() throws IOException {
		this.mongoClient = new MongoClient((new Property()).property.getProperty("mongo"));
		//Accessing the database 
		this.database = mongoClient.getDatabase("local");	 
		
	}
	
	
	public void insertDocument(String pid, HashMap<String, String> list) {
		MongoCollection<Document> collection = database.getCollection("sampleCollection");
		List<Document> hasPart = new ArrayList<Document>();
		for (Map.Entry item : list.entrySet()) {
			Document document = new Document("_id", item.getKey())
					.append("property", item.getValue());
			hasPart.add(document);
		}
		Document document = new Document("_id", pid)
				.append("has", hasPart);
				
		collection.insertOne(document);
	}
	
	public List<Document> query(String id) {
		MongoCollection<Document> collection = database.getCollection("sampleCollection");
		Document query = new Document().append("_id", id);
		FindIterable<Document> iterDoc = collection.find(query);
		// Getting the iterator
		Iterator it = iterDoc.iterator();
		List<Document> result = new ArrayList<Document>();
		while (it.hasNext()) {
			//System.out.println(it.next().toString());
			result.add((Document)it.next());
			
		}
		//System.out.println(result);
		//System.out.println(result.get(0).get("_id"));
		return result;
	}
	
	
	public void update(String id, HashMap<String, String> list, Document doc) {
		MongoCollection<Document> collection = database.getCollection("sampleCollection");
		List<Document> hasPart = (List<Document>) doc.get("has");
		for (Map.Entry item : list.entrySet()) {
			hasPart.add(new Document("_id", item.getKey())
					.append("property", item.getValue()));
		}
		
		Document document = new Document("_id", id)
				.append("has", hasPart);
		
		collection.findOneAndReplace(Filters.eq("_id", id), document);
		
	}
	
	public void update(String id, String pro, String value, Document doc) {
		MongoCollection<Document> collection = database.getCollection("sampleCollection");
		List<Document> hasPart = (List<Document>) doc.get("has");
		
		hasPart.add(new Document("_id", value).append("property", pro));
		
		
		Document document = new Document("_id", id)
				.append("has", hasPart);
		
		collection.findOneAndReplace(Filters.eq("_id", id), document);
		
	}
	
	public void close() {
		this.mongoClient.close();
	}
	
	
	public static void main(String[] args) {
		
	}

}
