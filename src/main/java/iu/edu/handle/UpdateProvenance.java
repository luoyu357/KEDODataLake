package iu.edu.handle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.handle.hdllib.HandleException;

public class UpdateProvenance {
	
	public void UpdateProvenance(String target, HashMap<String, String> provenance) throws HandleException, Exception {
		System.out.println("testse");
		ArrayList<String[]> resolve = new ResolvePID().handleResolve(target);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
        String date = formatter.format(new Date());
		for (int i = 0; i < resolve.size(); i++) {
			if (provenance.containsKey(resolve.get(i)[1])) {
				if (resolve.get(i)[2].equals("None")) {
					resolve.get(i)[2] = provenance.get(resolve.get(i)[1]);
				} else {
					resolve.get(i)[2] = resolve.get(i)[2]+","+provenance.get(resolve.get(i)[1]);
				}
				
			}
			
			if (resolve.get(i)[1].equals("lastModified")) {
				resolve.get(i)[2] = date;
			}
		}
		
		UpdatePID update = new UpdatePID();
		update.modifyHandle(resolve, target);	
	}
	
	public static void main(String[] args) throws HandleException, Exception {
	
	}

}
