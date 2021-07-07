package iu.edu.netcdf;

import java.util.HashMap;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;


public class ReadInfo {
	public NetcdfFile ncfile;
	
	public ReadInfo(String path) {
		try {
			ncfile = NetcdfFiles.open(path);
		}catch(Exception e) {
			System.out.println("NetCDF: cannot read the file");
		}
	}
	
	public HashMap<String, String> getGlobalAttributes() {
		HashMap<String, String> globalAttributes = new HashMap<String, String>();
		for (Attribute i : this.ncfile.getGlobalAttributes()) {
			globalAttributes.put(i.getShortName(), i.getStringValue());
		}
		//System.out.println(globalAttributes);
		return globalAttributes;
	}
	
	public HashMap<String, HashMap<String, String>> getVariables() {
		HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();

		for (Variable i : this.ncfile.getVariables()) {
			String name = i.getName();
			if (name.contains("/")) {
				name = name.split("/")[1];
			}
			
			HashMap<String, String> content = new HashMap<String, String>();
			String type = i.getDataType().toString();
			content.put("type", type);
			for (Attribute j : i.getAttributes()){
				content.put(j.getShortName(), j.getStringValue());
			}
			variables.put(name, content);
		}
		//System.out.println(variables);
		return variables;
	}

	public static void main(String[] args) {
		
	}

}
