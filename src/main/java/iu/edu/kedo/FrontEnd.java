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
package iu.edu.kedo;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import iu.edu.data.Filesystem;
import iu.edu.data.UploadFile;
import iu.edu.handle.ResolvePID;
import iu.edu.handle.UpdateProvenance;
import iu.edu.kg.KGbuilder;
import iu.edu.kg.KGcreate;
import iu.edu.kg.KGquery;
import iu.edu.kg.KGupdate;
import iu.edu.mongo.HandleTableMongo;
import net.handle.hdllib.HandleException;



@Controller
public class FrontEnd {

	@GetMapping("/")
	public String home()  {
        return "home";
    }
	
	

	@GetMapping("/upload")
	public String uploadPage() {
		return "upload";
	}
	

	@GetMapping("/query")
	public String queryPage() {
		return "query";
	}
	
	@GetMapping("/downloadPage")
	public String downloadPage() {
		return "downloadPage";
	}
	
	@GetMapping("/update")
	public String updatePage() {
		return "update";
	}
	
	@GetMapping("/updateInsight")
	public String updateInsightPage() {
		return "updateInsight";
	}
	
	
	@PostMapping("/updateInsight")
	public ModelAndView insightUpdate(
			 @RequestParam(required = true) String filePID,
			 @RequestParam(required = true) String containerPID,
			 @RequestParam(required = true) String varIRI,
			 @RequestParam(required = true) String description,
			 @RequestParam(required = true) String runCommand,
			 @RequestParam(required = true) String result	
			) throws Exception {
		
		KGcreate temp = new KGcreate();
		String pid = temp.createInsight(filePID, containerPID, varIRI, runCommand, result, description);
		
		ModelAndView mav = new ModelAndView("result");
		mav.addObject("pid", pid);
		
		return mav;
		
	}
	
	
	@PostMapping("/update")
	public ModelAndView provenanceUpdate(@RequestParam(required = true) String targetPID,
			 @RequestParam(required = true) String provenance,
			 @RequestParam(required = true) String objectPID	
			) throws Exception {
		
		UpdateProvenance updateOp = new UpdateProvenance();
		
		HashMap<String, String> prov = new HashMap<String, String>();
		prov.put(provenance, objectPID);
		
		HashMap<String, String> output = new HashMap<String, String>();
		updateOp.UpdateProvenance(targetPID, prov);
		ResolvePID handle = new ResolvePID();
		
		List<String> alternateOf = new ArrayList<String>();
		List<String> primarySourceOf = new ArrayList<String>();
		List<String> quotationOf = new ArrayList<String>();
		List<String> revisionOf = new ArrayList<String>();
		List<String> specializationOf = new ArrayList<String>();
		List<String> wasDerivedFrom = new ArrayList<String>();
		String lastModified = "";
		
		
		ArrayList<String[]> temp = handle.handleResolve(targetPID);
		
		for (String[] item : temp) {
			output.put(item[1], item[2]);
			
			if (item[1].equals("lastModified")) {
				lastModified = item[2];
			}
			if (item[1].equals("alternateOf")) {
				alternateOf = Arrays.asList(item[2].split(","));
			}
			if (item[1].equals("primarySourceOf")) {
				primarySourceOf = Arrays.asList(item[2].split(","));
			}
			if (item[1].equals("quotationOf")) {
				quotationOf = Arrays.asList(item[2].split(","));
			}
			if (item[1].equals("revisionOf")) {
				revisionOf = Arrays.asList(item[2].split(","));
			}
			if (item[1].equals("specializationOf")) {
				specializationOf = Arrays.asList(item[2].split(","));
			}
			if (item[1].equals("wasDerivedFrom")) {
				wasDerivedFrom = Arrays.asList(item[2].split(","));
			}
		}
		
		String[] oldKGPID = new KGquery().queryOne(targetPID, (new Property()).property.getProperty("graphdb")).split("\n");
		
		KGupdate update = new KGupdate();
		if (oldKGPID.length > 1) {
			//existed
			for (String item : oldKGPID) {
				String key = item.split(",")[0].split("#")[1];
				String value = item.split(",")[0];
				
				if (key.equals("lastModified")) {
					update.PIDlastModified(targetPID, lastModified);
				}
				if (key.equals("alternateOf")) {
					alternateOf.remove(value);
				}
				if (key.equals("primarySourceOf")) {
					primarySourceOf.remove(value);
				}
				if (key.equals("quotationOf")) {
					quotationOf.remove(value);
				}
				if (key.equals("revisionOf")) {
					revisionOf.remove(value);
				}
				if (key.equals("specializationOf")) {
					specializationOf.remove(value);
				}
				if (key.equals("wasDerivedFrom")) {
					wasDerivedFrom.remove(value);
				}
				
			}
			
		}
		
		if (!alternateOf.isEmpty()) {
			for (String extra : alternateOf)
			update.PIDprovenUpdate(targetPID, "alternateOf", extra);
		}
		if (!primarySourceOf.isEmpty()) {
			for (String extra : primarySourceOf)
			update.PIDprovenUpdate(targetPID, "primarySourceOf", extra);
		}
		if (!quotationOf.isEmpty()) {
			for (String extra : quotationOf)
			update.PIDprovenUpdate(targetPID, "quotationOf", extra);
		}
		if (!revisionOf.isEmpty()) {
			for (String extra : revisionOf)
			update.PIDprovenUpdate(targetPID, "revisionOf", extra);
		}
		if (!specializationOf.isEmpty()) {
			for (String extra : specializationOf)
			update.PIDprovenUpdate(targetPID, "specializationOf", extra);
		}
		if (!wasDerivedFrom.isEmpty()) {
			for (String extra : wasDerivedFrom)
			update.PIDprovenUpdate(targetPID, "wasDerivedFrom", extra);
		}
		
		
		ModelAndView mav = new ModelAndView("result");
		mav.addObject("output", output);
		
		return mav;
		
	}

	@PostMapping("/upload")
	public ModelAndView singleFileUpload(@ModelAttribute("uploadForm") FileUploadForm uploadForm,
			@RequestParam("name") String name, Model map) throws Exception {
		
		String lakePath = (new Property()).property.getProperty("lake")+name+UUID.randomUUID().toString();
		
		//property = new Property();
		
		File theDir = new File(lakePath);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		
		List<MultipartFile> files = uploadForm.getFiles();

		HashMap<String, String> output = new HashMap<String, String>();
		
		if(null != files && files.size() > 0) {
			for (MultipartFile multipartFile : files) {
				
				String fileName = multipartFile.getOriginalFilename();
				String newlo = lakePath+"/"+fileName;
				UploadFile save = new UploadFile();
				save.fileUpload(multipartFile, newlo);
				

			}
		}
		
		KGcreate kg = new KGcreate();
		output = kg.createKG(lakePath);
		
		
		ModelAndView mav = new ModelAndView("result");
		mav.addObject("output", output);
		
		return mav;
		
	}
	

	@GetMapping("/queryPage")
	public ModelAndView query(@RequestParam(required = false) String pid,
			 @RequestParam(required = false) String table,
			 @RequestParam(required = false) String knowledge,
			 @RequestParam(required = false) String query) throws Exception {
		
		ModelAndView mav = new ModelAndView("query");
		ResolvePID handle = new ResolvePID();
		HashMap<String, String> output = new HashMap<String, String>();
		if (pid != null) {
			List<String> alternateOf = new ArrayList<String>();
			List<String> primarySourceOf = new ArrayList<String>();
			List<String> quotationOf = new ArrayList<String>();
			List<String> revisionOf = new ArrayList<String>();
			List<String> specializationOf = new ArrayList<String>();
			List<String> wasDerivedFrom = new ArrayList<String>();
			String lastModified = "";
			
			
			ArrayList<String[]> temp = handle.handleResolve(pid);
			
			for (String[] item : temp) {
				output.put(item[1], item[2]);
				
				if (item[1].equals("lastModified")) {
					lastModified = item[2];
				}
				if (item[1].equals("alternateOf")) {
					alternateOf = Arrays.asList(item[2].split(","));
				}
				if (item[1].equals("primarySourceOf")) {
					primarySourceOf = Arrays.asList(item[2].split(","));
				}
				if (item[1].equals("quotationOf")) {
					quotationOf = Arrays.asList(item[2].split(","));
				}
				if (item[1].equals("revisionOf")) {
					revisionOf = Arrays.asList(item[2].split(","));
				}
				if (item[1].equals("specializationOf")) {
					specializationOf = Arrays.asList(item[2].split(","));
				}
				if (item[1].equals("wasDerivedFrom")) {
					wasDerivedFrom = Arrays.asList(item[2].split(","));
				}
			}
			
			String[] oldKGPID = new KGquery().queryOne(pid, (new Property()).property.getProperty("graphdb")).split("\n");
			
			KGupdate update = new KGupdate();
			if (oldKGPID.length > 1) {
				//existed
				for (String item : oldKGPID) {
					String key = item.split(",")[0].split("#")[1];
					String value = item.split(",")[0];
					
					if (key.equals("lastModified")) {
						update.PIDlastModified(pid, lastModified);
					}
					if (key.equals("alternateOf")) {
						alternateOf.remove(value);
					}
					if (key.equals("primarySourceOf")) {
						primarySourceOf.remove(value);
					}
					if (key.equals("quotationOf")) {
						quotationOf.remove(value);
					}
					if (key.equals("revisionOf")) {
						revisionOf.remove(value);
					}
					if (key.equals("specializationOf")) {
						specializationOf.remove(value);
					}
					if (key.equals("wasDerivedFrom")) {
						wasDerivedFrom.remove(value);
					}
					
				}
				
			}
			
			if (!alternateOf.isEmpty()) {
				for (String extra : alternateOf)
				update.PIDprovenUpdate(pid, "alternateOf", extra);
			}
			if (!primarySourceOf.isEmpty()) {
				for (String extra : primarySourceOf)
				update.PIDprovenUpdate(pid, "primarySourceOf", extra);
			}
			if (!quotationOf.isEmpty()) {
				for (String extra : quotationOf)
				update.PIDprovenUpdate(pid, "quotationOf", extra);
			}
			if (!revisionOf.isEmpty()) {
				for (String extra : revisionOf)
				update.PIDprovenUpdate(pid, "revisionOf", extra);
			}
			if (!specializationOf.isEmpty()) {
				for (String extra : specializationOf)
				update.PIDprovenUpdate(pid, "specializationOf", extra);
			}
			if (!wasDerivedFrom.isEmpty()) {
				for (String extra : wasDerivedFrom)
				update.PIDprovenUpdate(pid, "wasDerivedFrom", extra);
			}
			
			mav.addObject("output", output);
			
		}
		
		if (table != null) {
			HandleTableMongo mongo = new HandleTableMongo();
			List<Document> temp = mongo.query(table);
			
			for (Document item : temp) {
				List<Document> has = (List<Document>) item.get("has");
				
				for (Document object : has) {
					output.put(object.getString("property"), object.getString("_id"));
				}
				
			}
			mav.addObject("output", output);
			
		}
		System.out.println(knowledge);
		System.out.println(query);
		if (knowledge != null && query != null) {
			
			if (query.equals("local")) {
				String[] entity = new KGquery().queryOne(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				KGbuilder object = new KGbuilder();
				object.createResource("", knowledge, new HashMap<String, String>());
				if (entity.length > 1) {
					for (int i = 1 ; i < entity.length; i++) {
						String type = entity[i].split(",")[0];
						String value = entity[i].split(",")[1];

						if (!type.contains("digitalObjectLocation")) {
							if (value.contains("http")) {
								object.addPredicate(knowledge, type, value);
							} else {
								object.addProperty(knowledge, type, value);
							}
						} else {
							object.addProperty(knowledge, type, value);
						}
					}
					System.out.println(object.printKG());
					mav.addObject("knowledgeOutput", object.printKG());
				}else {
					mav.addObject("knowledgeOutput", "No such entity");
				}
			}
			
			if (query.equals("pidGraph")) {
				String[] list = new KGquery().queryPIDGraph(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				KGbuilder object = new KGbuilder();
				if (list.length > 1) {
					for (int i = 1 ; i < list.length; i++) {
						String id = list[i];
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb")).split("\n");
						object.createResource("", list[i], new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1 ; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];

								if (!type.contains("digitalObjectLocation")) {
									if (value.contains("http")) {
										object.addPredicate(list[i], type, value);
									} else {
										object.addProperty(list[i], type, value);
									}
								} else {
									object.addProperty(list[i], type, value);
								}
							}
						}
					}
					System.out.println(object.printKG());
					mav.addObject("knowledgeOutput", object.printKG());
				}else {
					mav.addObject("knowledgeOutput", "No such entity");
				}
			}
			
			
			if (query.equals("roWFeature")) {
				String[] list = new KGquery().queryroWFeature(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				KGbuilder object = new KGbuilder();
				if (list.length > 1) {
					for (int i = 1 ; i < list.length; i++) {
						String id = list[i];
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb")).split("\n");
						object.createResource("", id, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1 ; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];

								if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id, type, value);
									} else {
										object.addProperty(id, type, value);
									}
								} else {
									object.addProperty(id, type, value);
								}
							}
						}
					}
					
					System.out.println(object.printKG());
					mav.addObject("knowledgeOutput", object.printKG());
				}else {
					mav.addObject("knowledgeOutput", "No such entity");
				}
			}
			
			if (query.equals("feature")) {
				String[] list = new KGquery().queryrfileFeature(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				KGbuilder object = new KGbuilder();
				if (list.length > 1) {
					for (int i = 1 ; i < list.length; i++) {
						String id = list[i];
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb")).split("\n");
						object.createResource("", id, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1 ; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];

								if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id, type, value);
									} else {
										object.addProperty(id, type, value);
									}
								} else {
									object.addProperty(id, type, value);
								}
							}
						}
					}
					
					System.out.println(object.printKG());
					mav.addObject("knowledgeOutput", object.printKG());
				}else {
					mav.addObject("knowledgeOutput", "No such entity");
				}
			}
			
			if (query.equals("provenance")) {
				String[] oldKGPID = new KGquery().queryOne(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				if (oldKGPID.length > 1) {
					//existed
					for (String item : oldKGPID) {
						String key = item.split(",")[0].split("#")[1];
						String value = item.split(",")[0];

						if (key.equals("alternateOf")) {
							output.put("alternateOf", value);
						}
						if (key.equals("primarySourceOf")) {
							output.put("primarySourceOf", value);
						}
						if (key.equals("quotationOf")) {
							output.put("quotationOf", value);
						}
						if (key.equals("revisionOf")) {
							output.put("revisionOf", value);
						}
						if (key.equals("specializationOf")) {
							output.put("specializationOf", value);
						}
						if (key.equals("wasDerivedFrom")) {
							output.put("wasDerivedFrom", value);
						}
						
					}	
				}
				mav.addObject("output", output);
			}
			
			if (query.equals("share")) {
				String[] oldKGPID = new KGquery().queryShare(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				List<String> pidList = new ArrayList<String>();
				if (oldKGPID.length > 1) {
					for (int i =1 ; i < oldKGPID.length; i++) {
						pidList.add(oldKGPID[i]);
					}
				}
				mav.addObject("list", pidList);
			}
			
			if (query.equals("kg")) {
				String[] kglistAll = new KGquery().useKGPIDlistAll(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				KGbuilder object = new KGbuilder();
				if (kglistAll.length > 1) {
					for (int i = 1 ; i < kglistAll.length; i++) {
						String id = kglistAll[i];
						System.out.println(id);
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb")).split("\n");
						object.createResource("", id, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1 ; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];
								if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id, type, value);
									} else {
										object.addProperty(id, type, value);
									}
								} else {
									object.addProperty(id, type, value);
								}
							}
						}
					}
				}
				
				String[] roList = new KGquery().useKGPIDfindRO(knowledge, (new Property()).property.getProperty("graphdb")).split("\n");
				if (roList.length > 1) {
					for (int i = 1; i < roList.length; i++) {
						String id = kglistAll[i];
						String[] list = new KGquery().useROfindFeature(id, (new Property()).property.getProperty("graphdb")).split("\n");
						if (list.length > 1) {
							for (int j = 1; j < list.length; j++) {
								String id2 = list[j];
								System.out.println(id2);
								String[] entity = new KGquery().queryOne(id2, (new Property()).property.getProperty("graphdb")).split("\n");
								object.createResource("", id2, new HashMap<String, String>());
								if (entity.length > 1) {
									for (int k = 1 ; k < entity.length; k++) {
										String type = entity[k].split(",")[0];
										String value = entity[k].split(",")[1];
										if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
											if (value.contains("http")) {
												object.addPredicate(id2, type, value);
											} else {
												object.addProperty(id2, type, value);
											}
										} else {
											object.addProperty(id2, type, value);
										}
									}
								}
							}
						}
					}
				}
				
				System.out.println(object.printKG());
				mav.addObject("knowledgeOutput", object.printKG());
				
			}
			
		}

		return mav;
	}
	
	
	

	 @GetMapping("/download")
	 public ResponseEntity<Resource> download(@RequestParam(required = false) String path,  
			 @RequestParam(required = false) String kedoPID, HttpServletRequest request) throws HandleException, Exception{
			ResolvePID handle = new ResolvePID();
			Resource resource = null;
			if (path != null) {
				resource = new PathResource(path);
			}

			if (kedoPID != null) {
				String location = new KGquery()
						.queryLocationKEDOObjectPID(kedoPID, (new Property()).property.getProperty("graphdb")).split("\n")[1];
				String[] aroundKEDOObjectPID = new KGquery()
						.listAllKEDOObjectPID(kedoPID, (new Property()).property.getProperty("graphdb")).split("\n");
				String kgPID = new KGquery().KGPIDfromKEDOObjectpid(kedoPID, (new Property()).property.getProperty("graphdb"))
						.split("\n")[1];

				KGbuilder object = new KGbuilder();

				String[] entity1 = new KGquery().queryOne(kedoPID, (new Property()).property.getProperty("graphdb")).split("\n");
				object.createResource("", kedoPID, new HashMap<String, String>());
				if (entity1.length > 1) {
					for (int j = 1; j < entity1.length; j++) {
						String type = entity1[j].split(",")[0];
						String value = entity1[j].split(",")[1];
						if (!type.contains("digitalObjectLocation") && !type.contains("filePID")
								&& !type.contains("pid") && !type.contains("link")) {
							if (value.contains("http")) {
								object.addPredicate(kedoPID, type, value);
							} else {
								object.addProperty(kedoPID, type, value);
							}
						} else {
							object.addProperty(kedoPID, type, value);
						}
					}
				}

				if (aroundKEDOObjectPID.length > 1) {
					for (int i = 1; i < aroundKEDOObjectPID.length; i++) {
						String id = aroundKEDOObjectPID[i];
						System.out.println(id);
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb"))
								.split("\n");
						object.createResource("", id, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];
								if (!type.contains("digitalObjectLocation") && !type.contains("filePID")
										&& !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id, type, value);
									} else {
										object.addProperty(id, type, value);
									}
								} else {
									object.addProperty(id, type, value);
								}
							}
						}
					}
				}

				String[] kglistAll = new KGquery().useKGPIDlistAll(kgPID, (new Property()).property.getProperty("graphdb"))
						.split("\n");

				if (kglistAll.length > 1) {
					for (int i = 1; i < kglistAll.length; i++) {
						String id = kglistAll[i];
						System.out.println(id);
						String[] entity = new KGquery().queryOne(id, (new Property()).property.getProperty("graphdb"))
								.split("\n");
						object.createResource("", id, new HashMap<String, String>());
						if (entity.length > 1) {
							for (int j = 1; j < entity.length; j++) {
								String type = entity[j].split(",")[0];
								String value = entity[j].split(",")[1];
								if (!type.contains("filePID") && !type.contains("pid") && !type.contains("link")) {
									if (value.contains("http")) {
										object.addPredicate(id, type, value);
									} else {
										object.addProperty(id, type, value);
									}
								} else {
									object.addProperty(id, type, value);
								}
							}
						}
					}
				}

				String[] roList = new KGquery().useKGPIDfindRO(kgPID, (new Property()).property.getProperty("graphdb"))
						.split("\n");
				if (roList.length > 1) {
					for (int i = 1; i < roList.length; i++) {
						String id = kglistAll[i];
						String[] list = new KGquery().useROfindFeature(id, (new Property()).property.getProperty("graphdb"))
								.split("\n");
						if (list.length > 1) {
							for (int j = 1; j < list.length; j++) {
								String id2 = list[j];
								System.out.println(id2);
								String[] entity = new KGquery().queryOne(id2, (new Property()).property.getProperty("graphdb"))
										.split("\n");
								object.createResource("", id2, new HashMap<String, String>());
								if (entity.length > 1) {
									for (int k = 1; k < entity.length; k++) {
										String type = entity[k].split(",")[0];
										String value = entity[k].split(",")[1];
										if (!type.contains("filePID") && !type.contains("pid")
												&& !type.contains("link")) {
											if (value.contains("http")) {
												object.addPredicate(id2, type, value);
											} else {
												object.addProperty(id2, type, value);
											}
										} else {
											object.addProperty(id2, type, value);
										}
									}
								}
							}
						}
					}
				}
				System.out.println(object.printKG());
				Filesystem system = new Filesystem();
				system.writeFile("rdf.ttl", object.printKG(), location);
				system.zipFolder(location, location + ".zip");

				resource = new PathResource(location + ".zip");
			}

			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
			}

			if (contentType == null) {
				contentType = "application/octet-stream";
			}


			
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);		

		}

	}
