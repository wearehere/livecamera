package cn.codepanda.live.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;

public class MiscUtil{
	
	
    public static byte[] getBytesFromFile(String filename) throws IOException {
        File file = new File(filename);
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    public static void writeBytesToFile(String filename, byte[] content) throws IOException{
        File wfile = new File(filename);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(wfile));
        bos.write(content);
        bos.close();
    }
}