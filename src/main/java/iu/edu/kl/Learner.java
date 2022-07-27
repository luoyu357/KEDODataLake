package iu.edu.kl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class Learner {
	
	List<String> raw_key_list;
	List<String> raw_value_list;
	
	HashMap<String,Integer> key_index_class;
	HashMap<String,Integer> value_index_class;
	
	HashMap<Integer,String> index_key_class;
	HashMap<Integer,String> index_value_class;
	
	HashMap<String,ArrayList<String>> key_value_mapping;
	
	HashMap<String,Integer> key_class_index;
	HashMap<String,Integer> value_class_index;
	
	Table<Integer, Integer, Integer> key_value_class_index_mapping;
	
	HashMap<Integer,ArrayList<String>> block_class_index;
	
	Table<String, String,  String> block_relationship_rank;
	
	HashMap<Integer,String> block_index_id_list;
	
	Integer top;
	
	public Learner() {
		
	}
	
	
	public Learner(Integer top) {
		this.raw_key_list = new ArrayList<String>();
		this.raw_value_list = new ArrayList<String>();
		
		this.key_index_class = new HashMap<String,Integer>();
		this.value_index_class = new HashMap<String,Integer>();
		
		this.index_key_class = new HashMap<Integer,String>();
		this.index_value_class = new HashMap<Integer,String>();
		
		this.key_value_mapping = new HashMap<String,ArrayList<String>>();
		
		this.key_class_index = new HashMap<String,Integer>();
		this.value_class_index = new HashMap<String,Integer>();
		
		this.key_value_class_index_mapping = HashBasedTable.create();
		
		this.block_class_index = new HashMap<Integer, ArrayList<String>>();
		
		this.block_relationship_rank = HashBasedTable.create();
		
		this.block_index_id_list = new HashMap<Integer,String>();
		
		this.top = top;
		
	
	}
	
	public String[] score(String key_a, String key_b) {
		
		String[] a = key_a.split(" ");
		String[] b = key_b.split(" ");
		int total_score_a = 0;
		int total_score_b = 0;
		
		Integer[] order_score_a = new Integer[a.length];
		Arrays.fill(order_score_a, 0);
		Integer[] order_score_b = new Integer[b.length];
		Arrays.fill(order_score_b, 0);
		
		for (int i = 0; i < a.length; i++) {
			int index = 0;
			int score = 0;
			for (int j = i; j < a.length; j++) {
				for (int k = index; k < b.length; k++) {
					if (a[j].equalsIgnoreCase(b[k])) {
						score += (a.length-j)*(b.length-k);
						index = k+1;
						order_score_a[j] = order_score_a[j]+a.length-i;
						break;
					}
				}
			}
			total_score_a += score;
		}
		
		for (int i = 0; i < b.length; i++) {
			int index = 0;
			int score = 0;
			for (int j = i; j < b.length; j++) {
				for (int k = index; k < a.length; k++) {
					if (b[j].equalsIgnoreCase(a[k])) {
						score += (b.length-j)*(a.length-k);
						index = k+1;
						order_score_b[j] = order_score_b[j]+b.length-i;
						break;
					}
				}
			}
			total_score_b += score;
		}
		
		List<String> output_class = new ArrayList<String>();
		
		if (total_score_a == total_score_b) {
			if (total_score_a != 0) {
				String[] temp = a;
				Collections.reverse(Arrays.asList(temp));
				if (temp.equals(b) || a.equals(b)) {
					output_class = Arrays.asList(a);
				} else {
					
					List<Object[]> change = new ArrayList<Object[]>();
					for (int i = 0; i < order_score_a.length; i++) {
						change.add(new Object[] {order_score_a[i], a[i]});
					}
					
					change.sort(new Comparator<Object[]>() {
					    public int compare(Object[] a, Object[] b) {
					        return (Integer)((Integer) b[0]).compareTo((Integer) a[0]);
					    }
					});
					
					
					for (Object[] item : change) {
						if ((Integer) item[0] != 0) {
							output_class.add((String) item[1]);
						}
					}
				}
			} else {
				output_class = Arrays.asList(a);
			}
		}
		
		if (total_score_a > total_score_b) {
			List<Object[]> change = new ArrayList<Object[]>();
			for (int i = 0; i < order_score_a.length; i++) {
				change.add(new Object[] {order_score_a[i], a[i]});
			}
			
			change.sort(new Comparator<Object[]>() {
			    public int compare(Object[] a, Object[] b) {
			        return (Integer)((Integer) b[0]).compareTo((Integer) a[0]);
			    }
			});
			
			
			for (Object[] item : change) {
				if ((Integer) item[0] != 0) {
					output_class.add((String) item[1]);
				}
			}
		}
		
		if (total_score_a < total_score_b) {
			List<Object[]> change = new ArrayList<Object[]>();
			for (int i = 0; i < order_score_b.length; i++) {
				change.add(new Object[] {order_score_b[i], b[i]});
			}
			
			change.sort(new Comparator<Object[]>() {
			    public int compare(Object[] a, Object[] b) {
			        return (Integer)((Integer) b[0]).compareTo((Integer) a[0]);
			    }
			});
			
			
			for (Object[] item : change) {
				if ((Integer) item[0] != 0) {
					output_class.add((String) item[1]);
				}
			}
		}
		
		return new String[] {Integer.toString(total_score_a), Integer.toString(total_score_b), String.join(" ", output_class)};
	}
	
	
	public Integer[] key_value(String key, String value) {
		String find_key_class = "";
		String find_value_class = "";
		int find_key_class_index = 0;
		int find_value_class_index = 0;
		
		if (this.raw_key_list.contains(key)){
			
			find_key_class_index = this.key_index_class.get(key);
			find_key_class = this.index_key_class.get(find_key_class_index);
			if (this.raw_value_list.contains(value)) {
				find_value_class_index = this.value_index_class.get(value);
				find_value_class = this.index_value_class.get(find_value_class_index);
			} else {
				boolean stop = true;
				
				if (value.split(" ").length > 1) {
					for (int i = 0; i < this.raw_value_list.size(); i++) {
						String[] score = score(value, this.raw_value_list.get(i));
						
						if (Integer.valueOf(score[0]) >= Integer.valueOf(score[1]) &&
								Integer.valueOf(score[0]) != 0 &&
								score[2].split(" ").length > 1) {
							if (Integer.valueOf(score[0]) == Integer.valueOf(score[1])) {
								this.raw_value_list.add(i+1, value);
							}else {
								this.raw_value_list.add(i,value);
							}
							
							find_value_class = score[2];
							
							if (!this.value_class_index.keySet().contains(find_value_class)) {
								find_value_class_index = this.value_class_index.size();
								this.value_index_class.put(value, find_value_class_index);
								this.index_value_class.put(find_value_class_index, find_value_class);
								this.value_class_index.put(find_value_class, find_value_class_index);
							}else {
								find_value_class_index = this.value_class_index.get(find_value_class);
								this.value_index_class.put(value, find_value_class_index);
							}
							
							stop = false;
							break;
							
						}
					}
				}
					
				if (stop) {
					find_value_class = value;
					find_value_class_index = this.value_class_index.size();
					this.value_index_class.put(value, find_value_class_index);
					this.index_value_class.put(find_value_class_index, find_value_class);
					this.value_class_index.put(find_value_class, find_value_class_index);
				}
				
				
			}
			
		} else {
			boolean stop = true;
			if (key.split(" ").length > 1) {
				for (int i = 0; i < this.raw_key_list.size(); i++) {
					String[] score = score(key, this.raw_key_list.get(i));
					
					if (Integer.valueOf(score[0]) >= Integer.valueOf(score[1]) &&
							Integer.valueOf(score[0]) != 0 &&
							score[2].split(" ").length > 1) {
						if (Integer.valueOf(score[0]) == Integer.valueOf(score[1])) {
							this.raw_key_list.add(i+1, key);
						}else {
							this.raw_key_list.add(i,key);
						}
						
						find_key_class = score[2];
						
						if (!this.key_class_index.keySet().contains(find_key_class)) {
							find_key_class_index = this.key_class_index.size();
							this.key_index_class.put(key, find_key_class_index);
							this.index_key_class.put(find_key_class_index, find_key_class);
							this.key_class_index.put(find_key_class, find_key_class_index);
						}else {
							find_key_class_index = this.key_class_index.get(find_key_class);
							this.key_index_class.put(key, find_key_class_index);
						}
						
						stop = false;
						break;
						
					}
				}
			}
				
			if (stop) {
				find_key_class = key;
				find_key_class_index = this.key_class_index.size();
				this.key_index_class.put(key, find_key_class_index);
				this.index_key_class.put(find_key_class_index, find_key_class);
				this.key_class_index.put(find_key_class, find_key_class_index);
			}
			
			
			if (this.raw_value_list.contains(value)) {
				find_value_class_index = this.value_index_class.get(value);
				find_value_class = this .index_value_class.get(find_value_class_index);
			} else {
				stop = true;
				
				if (value.split(" ").length > 1) {
					for (int i = 0; i < this.raw_value_list.size(); i++) {
						String[] score = score(value, this.raw_value_list.get(i));
						
						if (Integer.valueOf(score[0]) >= Integer.valueOf(score[1]) &&
								Integer.valueOf(score[0]) != 0 &&
								score[2].split(" ").length > 1) {
							if (Integer.valueOf(score[0]) == Integer.valueOf(score[1])) {
								this.raw_value_list.add(i+1, value);
							}else {
								this.raw_value_list.add(i,value);
							}
							
							find_value_class = score[2];
							
							if (!this.value_class_index.keySet().contains(find_value_class)) {
								find_value_class_index = this.value_class_index.size();
								this.value_index_class.put(value, find_value_class_index);
								this.index_value_class.put(find_value_class_index, find_value_class);
								this.value_class_index.put(find_value_class, find_value_class_index);
							}else {
								find_value_class_index = this.value_class_index.get(find_value_class);
								this.value_index_class.put(value, find_value_class_index);
							}
							
							stop = false;
							break;
							
						}
					}
				}
					
				if (stop) {
					find_value_class = value;
					find_value_class_index = this.value_class_index.size();
					this.value_index_class.put(value, find_value_class_index);
					this.index_value_class.put(find_value_class_index, find_value_class);
					this.value_class_index.put(find_value_class, find_value_class_index);
				}
				
			}
		}
		        
        if (this.key_value_class_index_mapping.containsRow(find_key_class_index) && this.key_value_class_index_mapping.containsColumn(find_value_class_index)) {
        	if (!this.key_value_class_index_mapping.contains(find_key_class_index, find_value_class_index)) {
        		this.key_value_class_index_mapping.put(find_key_class_index, find_value_class_index,1);
        	}else {
        		int old_value = this.key_value_class_index_mapping.get(find_key_class_index, find_value_class_index);
        		this.key_value_class_index_mapping.put(find_key_class_index, find_value_class_index,old_value+1);
        	}
        }else {
        	this.key_value_class_index_mapping.put(find_key_class_index, find_value_class_index,1);
        }
        
        if (this.key_value_mapping.containsKey(key)) {
        	if (!this.key_value_mapping.get(key).contains(value)) {
        		this.key_value_mapping.get(key).add(value);
        	}
        } else {
        	ArrayList<String> temp_value_list = new ArrayList<String>();
        	temp_value_list.add(value);
        	this.key_value_mapping.put(key, temp_value_list);
        }
        
        if (!this.raw_key_list.contains(key)) {
        	this.raw_key_list.add(key);
        }
        
        if (!this.raw_value_list.contains(value)) {
        	this.raw_value_list.add(value);
        }


		return new Integer[] {find_key_class_index, find_value_class_index};
	}
	
	
	public Block block(ArrayList<String> block, String ID) {
		int block_index = -1;
		String id = "";
		ArrayList<String> block_class = new ArrayList<String>();
		ArrayList<Integer[]> block_class_output = new ArrayList<Integer[]>();
		for (String item: block) {
			String[] temp = item.split(":");
			Integer[] temp_key_value_class_index = key_value(temp[0], temp[1]);
			block_class.add(Arrays.toString(temp_key_value_class_index));
			block_class_output.add(temp_key_value_class_index);
		}
		
		boolean stop = true;
		Set<Entry<Integer,ArrayList<String>>> temp_class_list = this.block_class_index.entrySet();
		
		for (Entry<Integer,ArrayList<String>> item_class : temp_class_list) {
			ArrayList<String> copy = new ArrayList<String>(block_class);
			for (String item_block : block_class) {
				if (item_class.getValue().contains(item_block)) {
					
					copy.remove(item_block);
				}
			}

			if (copy.size() == 0 && item_class.getValue().size() == block_class.size()) {
				block_index = item_class.getKey();
				id = this.block_index_id_list.get(block_index);
				stop = false;
				break;
			}
		}
		
		if (stop) {
			block_index = this.block_class_index.size();
			this.block_class_index.put(block_index, block_class);
			this.block_index_id_list.put(block_index, ID);
			id = ID;
		}
		
		Block output = new Block(id, block_class_output);
		return output;
	}
	
	public class Block{
		String block_id;
		ArrayList<Integer[]> block_class;
		
		public Block(String block_id, ArrayList<Integer[]> block_class) {
			this.block_id = block_id;
			this.block_class = block_class;
		}
		
		public String getBlock_id() {
			return this.block_id;
		}
		
		public ArrayList<Integer[]> getBlock_class(){
			return this.block_class;
		}
	}
	
	public String[] build_block_relationship(ArrayList<String> block1, String id1, ArrayList<String> block2, String id2, String relationship) {
		Block block1_output = block(block1, id1);
		
		Block block2_output = block(block2, id2);
		
		
		
		ArrayList<Integer[]> index_pair = new ArrayList<Integer[]>(); 
		index_pair.addAll(block1_output.getBlock_class());
		index_pair.addAll(block2_output.getBlock_class());
		
		
		String new_relationship = rank(index_pair, relationship);
		return new String[] {block1_output.getBlock_id(), new_relationship, block2_output.getBlock_id()};
		
	}
	
	public void update_rank(ArrayList<Integer[]> index_pair, String relationship) {
		double temp_score = 1/((double) index_pair.size());
		String rank_score = Double.toString((double) Math.round(temp_score*100)/100);
		
		if (this.block_relationship_rank.size() == 0) {
			for (Integer[] item : index_pair) {
				this.block_relationship_rank.put(relationship, Arrays.toString(item), rank_score);
			}
		} else if (!this.block_relationship_rank.containsRow(relationship)) {
			for (Integer[] item : index_pair) {
				this.block_relationship_rank.put(relationship, Arrays.toString(item), rank_score);
			}
		} else {
			for (Integer[] item : index_pair) {
				if (this.block_relationship_rank.contains(relationship, Arrays.toString(item))) {
					String temp_value = Double.toString(Double.valueOf(this.block_relationship_rank.get(relationship, Arrays.toString(item))) + Double.valueOf(rank_score));
					this.block_relationship_rank.put(relationship, Arrays.toString(item), temp_value);
				} else {
					this.block_relationship_rank.put(relationship, Arrays.toString(item), rank_score);
				}
				
			}
		}
	}
	
	public boolean checkall(ArrayList<Integer[]> index_pair) {
		boolean check = true;
		
		for (Integer[] item : index_pair) {
			if (!this.block_relationship_rank.containsColumn(Arrays.toString(item))) {
				check = false;
				break;
			}
		}
		
		return check;
	}
	
	
	public String rank(ArrayList<Integer[]> index_pair, String relationship) {
		
		if (this.block_relationship_rank.size() == 0) {
			update_rank(index_pair, relationship);
			return relationship;
		} else {
			if (this.block_relationship_rank.containsRow(relationship) && checkall(index_pair)) {
				HashMap<String, Double> result = new HashMap<String, Double>();
				for (Integer[] item: index_pair) {
					if (this.block_relationship_rank.containsColumn(Arrays.toString(item))) {
						Map<String, String> temp_result = this.block_relationship_rank.column(Arrays.toString(item));
						for (Map.Entry<String, String> temp_item_result : temp_result.entrySet()) {
							if (result.containsKey(temp_item_result.getKey())) {
								result.put(temp_item_result.getKey(), Double.valueOf(result.get(temp_item_result.getKey()))+Double.valueOf(temp_item_result.getValue()));
							} else {
								result.put(temp_item_result.getKey(), Double.valueOf(temp_item_result.getValue()));
							}
						}
					}
				}
				
				Object[] result_sort = result.entrySet().toArray();
				Arrays.sort(result_sort, new Comparator() {
				    public int compare(Object o1, Object o2) {
				        return ((Map.Entry<String, Double>) o2).getValue()
				                   .compareTo(((Map.Entry<String, Double>) o1).getValue());
				    }
				});
				
				ArrayList<String> top_relationship = new ArrayList<String>();
				
				
				int count = 1;
				
				double temp_number = ((Map.Entry<String, Double>) result_sort[0]).getValue();
				top_relationship.add(((Map.Entry<String, Double>) result_sort[0]).getKey());
				
				for (int i = 1; i < result_sort.length; i++) {
					if (((Map.Entry<String, Double>) result_sort[i]).getValue() < temp_number) {
						temp_number = ((Map.Entry<String, Double>) result_sort[i]).getValue();
						count += 1;
						if (count == this.top+1) {
							break;
						} else {
							top_relationship.add(((Map.Entry<String, Double>) result_sort[i]).getKey());
						}
					} else {
						top_relationship.add(((Map.Entry<String, Double>) result_sort[i]).getKey());
					}
				}
				
				
				update_rank(index_pair, relationship);
				if (top_relationship.contains(relationship)) {
					return relationship;
				} else {
					
					return top_relationship.get(0);
				}
				
				
			} else {
				update_rank(index_pair, relationship);
				return relationship;
			}
		}
	}
	
	
	
	
	public static void main(String[] args) {
		
	}

}
