package edu.csupomona.cs585.ibox.sync;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

public class GoogleDriveFileSyncManagerTest {

	GoogleDriveFileSyncManager manager;
	Drive mockDrive;
	Files mockDir;
	Insert mockInsert;
	File mockFile;
	List mockList;
	Update mockUpdate;
	
	@Before
	public void setUp() throws Exception {
		mockDrive = mock(Drive.class);
		manager = new GoogleDriveFileSyncManager(mockDrive);
		mockFile = new File();
		mockFile.setId("test ID");
		java.util.List<File> testFiles = new ArrayList<File>();
		
		for(int i=0; i<4; i++){
			File mockFile = new File();
			mockFile.setId(Integer.toString(i));
			mockFile.setTitle("title "+i);
			testFiles.add(mockFile);
		}
		
		
		mockDir = mock(Files.class);
		mockInsert = mock(Insert.class);
		mockList = mock(List.class);
		FileList mockFileList = new FileList();
		mockFileList.setItems(testFiles);
		
		when(mockDrive.files()).thenReturn(mockDir);
		when(mockDir.insert(any(File.class), any(FileContent.class))).thenReturn(mockInsert);
		when(mockInsert.execute()).thenReturn(testFiles.get(0));
		when(mockDir.list()).thenReturn(mockList);
		when(mockList.execute()).thenReturn(mockFileList);
		
		mockUpdate = mock(Update.class);
		when(mockDir.update(anyString(), any(File.class), any(FileContent.class))).thenReturn(mockUpdate);
		when(mockUpdate.execute()).thenReturn(testFiles.get(0));
		
		Delete mockDeleteNull = mock(Delete.class);
		when(mockDir.delete(null)).thenReturn(mockDeleteNull);
		when(mockDeleteNull.execute()).thenThrow(new FileNotFoundException());
		
		when(mockDir.delete("2")).thenReturn(mock(Delete.class));
		
	}

	@After
	public void tearDown() throws Exception {
		manager = null;
		mockDrive = null;
		mockDir = null;
		mockInsert = null;
		mockFile = null;
		mockList = null;
		mockUpdate = null;
	}

	@Test
	public void testAddFile() throws IOException {
		
		java.io.File testFile = mock(java.io.File.class);
		manager.addFile(testFile);
		verify(mockDrive).files();
		verify(mockDir).insert(any(File.class), any(FileContent.class));
		verify(mockInsert).execute();
	}

	@Test
	public void testUpdateFileNull() throws IOException {
		java.io.File testFile = mock(java.io.File.class);
		//when(testFile.getName()).thenReturn("test_name");
		manager.updateFile(testFile);
		verify(mockDrive, atLeast(1)).files();
		verify(mockDir).insert(any(File.class), any(FileContent.class));
		verify(mockInsert).execute();
		verifyZeroInteractions(mockUpdate);
		//fail("Not yet implemented");
	}

	@Test
	public void testUpdateFile() throws IOException {
		java.io.File testFile = mock(java.io.File.class);
		when(testFile.getName()).thenReturn("title 0");
		manager.updateFile(testFile);
		verify(mockDrive, atLeast(1)).files();
		verify(mockDir).update(anyString(),any(File.class), any(FileContent.class));
		verify(mockUpdate).execute();
		verifyZeroInteractions(mockInsert);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testDeleteFileNull() throws IOException {
		java.io.File testFile = mock(java.io.File.class);
		when(testFile.getName()).thenReturn("title 5");
		manager.deleteFile(testFile);
	}
	
	@Test
	public void testDeleteFile() throws IOException {
		java.io.File testFile = mock(java.io.File.class);
		when(testFile.getName()).thenReturn("title 2");
		manager.deleteFile(testFile);
		verify(mockDrive, atLeast(1)).files();
		verify(mockDir).delete("2");
	}

}
