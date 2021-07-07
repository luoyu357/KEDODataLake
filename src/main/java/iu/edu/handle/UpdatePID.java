package iu.edu.handle;

import java.io.File;
import java.security.PrivateKey;
import java.util.ArrayList;

import iu.edu.kedo.Property;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.Util;

public class UpdatePID {
	
	public String modifyHandle(ArrayList<String[]> object, String handle_identifier) throws Exception {
		
		Property input = new Property();
		HandleResolver resolver = new HandleResolver();
		
		File privKeyFile = new File(input.property.getProperty("private.key.file"));
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, input.property.getProperty("private.key.file.password"));
		byte adm_handle[] = Util.encodeString(input.property.getProperty("handle.admin.identifier"));
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);
		
		String prefix = input.property.getProperty("handle.prefix");
		handle_identifier = prefix+handle_identifier.split(prefix)[1];
	
		HandleValue[] new_values = new HandleValue[object.size()];
		
		int count = 0;
		for (String[] item : object){
			HandleValue new_value = new HandleValue(Integer.parseInt(item[0]), Util.encodeString(item[1]), Util.encodeString(item[2]));
			new_values[count] = new_value;
			count++;
		}
		
		ModifyValueRequest assign_request = new ModifyValueRequest(Util.encodeString(handle_identifier), new_values,
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
	
	public static void main(String[] args) throws HandleException, Exception {

	}

}
