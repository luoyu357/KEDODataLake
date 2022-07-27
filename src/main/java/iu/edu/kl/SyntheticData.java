package iu.edu.kl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
				if (i == 2) {
					temp_string.add(input[i]);
				} else {
					if (temp_check[i] == 1) {
						temp_string.add(input[i]);
					}
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
	
	
	public ArrayList<String> data(int class_number, int instance_number){
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
		
		
		ArrayList<String[]> t_key = new ArrayList<String[]>();
		for (int i = 0; i < class_number; i++) {
			String[] key = UUID.randomUUID().toString().split("-");
			t_key.add(modify(key,instance_number));
		}
		
		ArrayList<String[]> t_value = new ArrayList<String[]>();
		for (int i = 0; i < class_number; i++) {
			String[] value = UUID.randomUUID().toString().split("-");
			t_value.add(modify(value,instance_number));
		}
		
		
		ArrayList<String[]> head = new ArrayList<String[]>();
		
		for (int i = 0; i< class_number; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for (String item_key : h_key.get(i)) {
				for (String item_value : h_value.get(i)) {
					temp.add(item_key+":"+item_value);
				}
			}
			
			String[] temp_head = new String[temp.size()];
			for (int j = 0; j < temp.size(); j++) {
				temp_head[j] = temp.get(j);
			}
			head.add(temp_head);
		}
		
		
		ArrayList<String[]> tail = new ArrayList<String[]>();
		
		for (int i = 0; i< class_number; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for (String item_key : t_key.get(i)) {
				for (String item_value : t_value.get(i)) {
					temp.add(item_key+":"+item_value);
				}
			}
			
			String[] temp_tail = new String[temp.size()];
			for (int j = 0; j < temp.size(); j++) {
				temp_tail[j] = temp.get(j);
			}
			tail.add(temp_tail);
		}
		
		
		ArrayList<String> triplet = new ArrayList<String>();
		
		for (int i = 0 ; i < class_number; i++) {
			for (int j = 0; j< class_number; j++) {
				String relation = "relation"+Integer.toString(i)+","+Integer.toString(j);
				
				for (String item_h : head.get(i)) {
					for (String item_t : tail.get(j)) {
						triplet.add(item_h+"\t"+relation+"\t"+item_t);
					}
				}
			}
		}
		return triplet;
	}
	
	public static void main(String[] args) {


	}

}
