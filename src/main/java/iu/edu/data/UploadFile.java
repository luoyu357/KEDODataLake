/*
#
# Copyright 2019 The Trustees of Indiana University
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
*/
package iu.edu.data;


import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;


public class UploadFile {

	

	
	public void fileUpload(MultipartFile file, String path) {
		try {
			byte[] bytes = file.getBytes();
		
			Files.write(Paths.get(path), bytes);
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
