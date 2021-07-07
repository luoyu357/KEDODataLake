package iu.edu.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;


public class Filesystem{
	
	public Filesystem() {

	}
	
	/*
	 * write file into the local LIDO service
	 * @param filename
	 * @param object: the content of file
	 * @param location
	 */
	public void writeFile(String fileName, String object, String location) {
		try (FileWriter write = new FileWriter(location+"/"+fileName, true)) {
			write.write(object);
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public void deleteFile(String fileName, String location) {
		File file = new File(location+fileName);
		file.delete();
	}
	
	/*
	 * read the metadata instance document
	 * @param fileName
	 * @param location
	 * @return the content of the metadata instance
	 */
	public JSONObject readFile(String fileName, String location) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(location+fileName))));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    JSONObject output = new JSONObject(out.toString());
		
		reader.close();
		return output;
	}
	
	/*
	 * read the metadata instance document
	 * @param path
	 * @return the content of the metadata instance
	 */
	public JSONObject readFile(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    JSONObject output = new JSONObject(out.toString());
		
		reader.close();
		return output;
	}
	
	
	public Boolean deleteFolder(String location) {
		File dir = new File(location); 
		if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFolder(children[i].getAbsolutePath());
                if (!success) {
                    return false;
                }
            }
        }
        System.out.println("removing file or directory : " + dir.getName());
        return dir.delete();
	}
	
	/*
	 * generate the result bundle zip file
	 * @param folderPath: the result bundle folder
	 * @param zipPath
	 */
	public void zipFolder(String folderPath, String zipPath) throws IOException {
		byte[] buffer = new byte[1024];
		 
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);
        File dir = new File(folderPath);
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            FileInputStream fis = new FileInputStream(files[i]);
            zos.putNextEntry(new ZipEntry(files[i].getName()));           
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }

        zos.close();
	}
	
	



}
