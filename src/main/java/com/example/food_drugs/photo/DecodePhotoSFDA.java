package com.example.food_drugs.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Random;

public class DecodePhotoSFDA {

	public Boolean deletePhoto(String photo,String modleType) {
		String path ="";
		if(modleType.equals("warehouse")) {
			path ="/var/www/html/sareb_photo/warehouse_photos/"+photo;
		}
		
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
		}		
		return true;
	}
	public String Base64_Image(String photo,String modleType) {
		
		int pos1=photo.indexOf(":");
		int pos2=photo.indexOf(";");
		
		String type=photo.substring(pos1+1,pos2);
		
		if(type.equals("image/png"))
        {
        	type=".png";
        }
        if(type.equals("image/jpg"))
        {
        	type=".jpg";
        }
        if(type.equals("image/jpeg"))
        {
        	type=".jpeg";
        }
		Random rand = new Random();
		int n = rand.nextInt(999999);
		
		String fileName=n + type;
		String path ="";
		if(modleType.equals("warehouse")) {
			path ="/var/www/html/sareb_photo/warehouse_photos/"+fileName;
		}
		
		
		int pos=photo.indexOf(",");
		byte[] data=Base64.getDecoder().decode(photo.substring(pos+1));	

		try {
			OutputStream outputStream = new FileOutputStream(path);
			try {
				outputStream.write(data);
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return fileName;
		
	}
}
