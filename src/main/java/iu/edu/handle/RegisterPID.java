package iu.edu.handle;

import java.io.File;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import iu.edu.kedo.Property;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;


public class RegisterPID {

	
	public String createHandle(String pid, HashMap<String, String> object) throws Exception {
		
		Property input = new Property();
		HandleResolver resolver = new HandleResolver();
		

		File privKeyFile = new File(input.property.getProperty("private.key.file"));
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, input.property.getProperty("private.key.file.password"));
		byte adm_handle[] = Util.encodeString(input.property.getProperty("handle.admin.identifier"));
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);
		
		String prefix = input.property.getProperty("handle.prefix");
		String handle_identifier = prefix+pid.split(prefix)[1];
		
		
		HandleValue[] new_values = new HandleValue[object.keySet().size()];
		
		int count = 0;
		for (Map.Entry item : object.entrySet()){
			// the key-value pair of type and value 
			HandleValue new_value = new HandleValue(count+1, Util.encodeString(item.getKey().toString()), Util.encodeString(item.getValue().toString()));
			new_values[count] = new_value;
			count++;
		}
		
		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier), new_values,
				auth);
		
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			return handle_identifier;

		} else {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
			return "Failed";
		}
	}
	
	
	public static void main(String[] args) throws Exception {

	}
	
}
