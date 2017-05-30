package py.una.pol.iin.pwb.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

public class FileUpload {
	
	public static final Logger logger = Logger.getAnonymousLogger();
	
	public static final String TEMP_LOCATION="tempdir/";
	
	public static void writeToFile(InputStream inputStream,
		String fileName) {

		OutputStream out = null;
		try {
			createDirectoryIfNotExists();
			File f = new File(TEMP_LOCATION + fileName);
			System.out.println(f.getAbsolutePath());
			out = new FileOutputStream(f);
			
			
			int read;
			byte[] bytes = new byte[1024];
		
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "an exception was thrown", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "an exception was thrown", e);
				}
			}
		}
	}
	
	private static void createDirectoryIfNotExists()
	{
		File file = new File(TEMP_LOCATION); 
		if (!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}
	}
}
