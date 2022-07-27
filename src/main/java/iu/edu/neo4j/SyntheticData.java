package iu.edu.neo4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

public class SyntheticData {
	
	public SyntheticData() {
		
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
	
	
	public void createSyntheticData(int kedo_size, int file_size, int variable_size) {
		
		
		ArrayList<String[]> pair = attributes(20,20);
		ArrayList<String> pidList = new ArrayList<String>();
		
		
		
		for (int i = 0; i < kedo_size; i++) {
			JSONObject kedo = new JSONObject();
			JSONArray files = new JSONArray();
			
			JSONObject kedotype = new JSONObject();
			kedotype.put("size", Math.abs(new Random().nextInt()));
			
			kedo.put("type", kedotype);
			
			ArrayList<String> tempPID = new ArrayList<String>();
			
			for (int j =0 ;j < file_size; j++) {
				JSONObject file = new JSONObject();
				String filePID = UUID.randomUUID().toString();
				file.put("pid", filePID);
				tempPID.add(filePID);
				
				JSONObject ro = new JSONObject();
				
				ro.put("name", pair.get(new Random().nextInt(pair.size()))[1]);
				
				ro.put("fileFormat", "nc");
				ro.put("contentSize", String.valueOf(new Random().nextInt()));
				ro.put("checksum", UUID.randomUUID().toString());
				
				file.put("ro", ro);
				
				List<Object> prov = Arrays.asList(new String[] {"wasDerivedFrom", "specializationOf","revisionOf","primarySourceOf","quotationOf","alternatOf"});
				
				ArrayList<String> idList = new ArrayList<String>();
				JSONObject provenance = new JSONObject();
				
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
				
				file.put("provenance", provenance);
				
				JSONObject att = new JSONObject();
				
				String[] attribute = new String[2];
				
				attribute = pair.get(new Random().nextInt(pair.size()));
				
				att.put(attribute[0], attribute[1]);
				
				file.put("attribute", att);
				
				JSONObject var = new JSONObject();
				
				ArrayList<String[]> tempV =  new ArrayList<String[]>();
				for (int k = 0; k < variable_size; k++) {
					String[] variable = pair.get(new Random().nextInt(pair.size()));
					while (!tempV.contains(variable)) {
						variable = pair.get(new Random().nextInt(pair.size()));
					}
					
					tempV.add(variable);
					var.put(variable[0], variable[1]);
				}
				
				file.put("variable", var);
				
				files.put(file);
				
			}
			kedo.put("files", files);
			pidList.addAll(tempPID);
			
		}
		
		
		
	}
	
	
	
	public static void main(String[] args) {

	}

}
