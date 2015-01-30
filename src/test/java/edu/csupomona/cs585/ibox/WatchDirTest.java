/**
 * 
 */
package edu.csupomona.cs585.ibox;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.csupomona.cs585.ibox.sync.FileSyncManager;

/**
 * @author Brian
 *
 */
public class WatchDirTest {

	
	private Path mockDir;
	private WatchKey mockKey;
	private WatchService mockWatcher;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockWatcher= mock(WatchService.class);
		mockDir = mock(Path.class);
		mockKey = mock(WatchKey.class);
		when(mockDir.register(mockWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)).thenReturn(mockKey);
		
		//WatchDir watcher = new WatchDir(mockDir, mockManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		mockWatcher = null;
		mockDir = null;
		mockKey = null;
	}

	/**
	 * Test method for {@link edu.csupomona.cs585.ibox.WatchDir#WatchDir(java.nio.file.Path, edu.csupomona.cs585.ibox.sync.FileSyncManager)}.
	 * @throws IOException 
	 */
	@Test
	public void testWatchDir() throws IOException {
		FileSyncManager mockManager = mock(FileSyncManager.class);
		new WatchDir(mockDir, mockManager);
		verify(mockDir.register(mockWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY));
		//fail("Not yet implemented ");
		//check to see if it updates now
		
	}

	
}
