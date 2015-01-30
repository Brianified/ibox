package edu.csupomona.cs585.ibox.sync;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveFileSyncManagerIntegrationTest {

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String TEST_FILE_NAME = "test.txt";
	private	static String fileID;
	private Drive Service;
    private GoogleDriveFileSyncManager gdfsm;

    @Before
    public void setup() throws IOException{
            initGoogleDriveServices();
            gdfsm = new GoogleDriveFileSyncManager(Service);
    }

    public void initGoogleDriveServices() throws IOException {
           HttpTransport httpTransport = new NetHttpTransport();
           JsonFactory jsonFactory = new JacksonFactory();

           try{
               GoogleCredential credential = new  GoogleCredential.Builder()
                 .setTransport(httpTransport)
                 .setJsonFactory(jsonFactory)
                 .setServiceAccountId(
                             "257633467452-fab1eanigcg3t163e94pg9nv1utj4vcg@developer.gserviceaccount.com"
                             )
                 .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
                 .setServiceAccountPrivateKeyFromP12File(
                             new File("ibox-9f4284e72aef.p12"))
                 .build();

               Service = new Drive.Builder(httpTransport,
                               jsonFactory, credential).setApplicationName("ibox").build();
           }catch(GeneralSecurityException e){
               e.printStackTrace();
           }

       }
    
	@Test
	public void testAddFile() throws IOException, InterruptedException {
		// get file sync manager		
		File testFile = new File(USER_DIR+"/"+TEST_FILE_NAME);
        FileWriter testFW = new FileWriter(testFile);
        BufferedWriter writer = new BufferedWriter(testFW);
        writer.write("test is a test");
        writer.close();
        gdfsm.addFile(testFile);
        fileID = gdfsm.getFileId(TEST_FILE_NAME);
        assertNotNull(fileID);
        
	}

	@Test
	public void testUpdateFile() throws IOException, InterruptedException {
		 //open a file to append
		 File testFile = new File(USER_DIR+"/"+TEST_FILE_NAME);
		 FileWriter testFileWriter = new FileWriter(testFile, true);
	     BufferedWriter writer = new BufferedWriter(testFileWriter);
	     writer.append("this is appended to the file");
	     writer.close();
	     gdfsm.updateFile(testFile);
	     String newFileID = gdfsm.getFileId(TEST_FILE_NAME);
	     assertEquals("StringID does not match", fileID, newFileID);
	     
		
	}

	@Test
	public void testDeleteFile() throws InterruptedException, IOException {
		 File fileTemp = new File(USER_DIR+"/"+TEST_FILE_NAME);
		 assertTrue(fileTemp.exists());
		 String fileIDExists = gdfsm.getFileId(TEST_FILE_NAME);
		 assertNotNull(fileIDExists);
		 gdfsm.deleteFile(fileTemp);
		 if (fileTemp.exists()){
            fileTemp.delete();
         }
		 assertFalse(fileTemp.exists());
		 String newFileID = gdfsm.getFileId(TEST_FILE_NAME);
         assertNull(newFileID);
	}

}
