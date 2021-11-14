import java.io.File;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.prefs.Preferences;

import org.bitcoinj.script.Script.ScriptType;

public class BitcoinMicroCollider {

	public static void main(String[] args) {
		BitcoinMicroCollider thisCollider = new BitcoinMicroCollider();	

		boolean isResetFilePath = false;
		if (args != null && args.length > 0 && !args[0].isEmpty())
			isResetFilePath = args[0].equals("resetFilePath") ? true : false;
		
		
		charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

		
		thisCollider.startGui(isResetFilePath);
	}

	private Status colliderStatus = Status.MAIN_MENU;
	private FileStatus fileStatus = FileStatus.FILE_NOT_LOADED;
	private Boolean isInitialized = false;
	private AddressGroup[][][][] p2pkh_address_array; // Array of address arrays
	private SearchThread[] threadArray;
	private Timer guiTimer;
	private String[] addressLabelArray = new String[10];
	public static char[] charArray;

	
	private GuiUpdateTimer guiUpdateTimer;

	private PrimaryWindow primaryWindow;
	private long totalCheckCount = 0;
	private long startTime = 0;
	private int threadCount = 1;

	private int activeThreadCount = 0; // threads loaded and running
	private long pausedTimeTotal = 0;

	private long pauseStartTime = 0;
	public static String prefsNode = "com/github/traxm/Bitcoin-Micro-Collider";
	public static String prefsFileString = "filePath";
	public static String prefsThreadString = "threadCount";

	public AddressGroup[][][][] getAddressArray() {
		return this.p2pkh_address_array;
	}



	public Boolean isInitialized() {
		return isInitialized;
	}

	public void logAddressCheck() {
		// Track address checks

		totalCheckCount++;
	}

	public void notifySuccess(String matchAddress, String matchKey) {
		primaryWindow.setSuccessLabel(true, matchAddress, matchKey);
	}

	public void pauseAllThreads() {
		/*
		 * End each thread based on user input
		 */

		colliderStatus = Status.PAUSED;

		for (int i = 0; i < this.threadArray.length; i++)
			this.threadArray[i].stopThread();

		pauseStartTime = System.nanoTime();
		activeThreadCount = 0;
		primaryWindow.setThreadsActiveLabel(activeThreadCount);
	}

	public void resumeAllThreads() {
		/*
		 * Create and start new threads based on user input
		 */

		colliderStatus = Status.RUNNING;

		this.startThreads(threadCount);

		// Update the paused time
		this.pausedTimeTotal += (System.nanoTime() - this.pauseStartTime);
	}

	private void saveThreadCount(int threadCount) {
		// Saves the file path for future use
		Preferences prefs = Preferences.userRoot().node(prefsNode);
		prefs.putInt(prefsThreadString, threadCount);
	}

	private void startGui(boolean resetFilePath) {
		/*
		 * Create a frame and populate with components
		 */
		primaryWindow = new PrimaryWindow(this);

		primaryWindow.setWindowEnabled(true);

		// Check to see if an existing address file is available
		Preferences prefs = Preferences.userRoot().node(prefsNode);
		// Does the preference entry exist
		// Is the file available
		if (LoadAddresses.isFileAvailable(prefs.get(prefsFileString, null))) {
			// Load the existing file
			if (!resetFilePath)
				primaryWindow.setAddressFile(new File(prefs.get(prefsFileString, null)));

			// Set the thread count
			primaryWindow.setThreadCount(prefs.getInt(BitcoinMicroCollider.prefsThreadString, 4));
		}
		
			
	}

	public void startCollider(int threadCount) throws URISyntaxException {
		/*
		 * Start the lottery process
		 */

		// Swap the active panel (0 is the main menu and 1 is the working panel)
		primaryWindow.setPanel(1);

		// Create the worker threads
		startThreads(threadCount);

		// Set a timer to update UI components
		guiTimer = new Timer();
		guiUpdateTimer = new GuiUpdateTimer(this);
		guiTimer.schedule(guiUpdateTimer, 0, 20);

		// Start tracking time
		startTime = System.nanoTime();

		// Save thread count for future use
		this.saveThreadCount(threadCount);

	}

	private void startThreads(int thisCount) {
		/*
		 * Create and start worker threads
		 */

		// Thread count is pulled from user input
		threadCount = thisCount;

		// Create the thread array
		threadArray = new SearchThread[threadCount];

		// Create specified threads
		for (int i = 0; i < threadCount; i++) {
			SearchThread thisThread = new SearchThread(this, ScriptType.P2SH, (i + 1) * 1000, i == 0); // Create a Search Thread
																								// instance
			threadArray[i] = thisThread; // Add the instance to the thread array
		}

		// Start each of the threads
		for (int i = 0; i < threadCount; i++) {
			threadArray[i].start();
		}

		// Update the collider status
		colliderStatus = Status.RUNNING;
	}

	public void updateGuiElements() {
		// Update all gui values

		if (colliderStatus == Status.PAUSED)
			return;

		this.primaryWindow.setScanAttemptsLabel(totalCheckCount);

		long duration = System.nanoTime() - startTime - this.pausedTimeTotal;
		this.primaryWindow.setTimeLabel(duration / 1000000, totalCheckCount);
		
		this.primaryWindow.setAddressLabels(this.addressLabelArray);
	}
	
	public String[] getAddressLabelArray() { 
		return this.addressLabelArray;
	}
	
	public void updateThreadCount() {
		// Called by threads at start - active threads communicated via UI

		if (activeThreadCount > threadCount)
			return;

		activeThreadCount++;
		primaryWindow.setThreadsActiveLabel(activeThreadCount);
	}

	public void setAddressArray(AddressGroup[][][][] thisAddressArray) {
		this.p2pkh_address_array = thisAddressArray;
	}
	
	public void setFileStatus(FileStatus thisStatus) {
		this.fileStatus = thisStatus;
	}
	
	public FileStatus getFileStatus() { 
		return this.fileStatus; 
	}
	
	public int[] getLookupIndex(String thisString) {
		return getLookupIndex(thisString.toCharArray());
	}
	
	
	public int[] getLookupIndex(char[] charLookupArray) {
		// Returns an index to lookup the correct segmented address array

		int firstVal = -1;
		int secondVal = -1;
		int thirdVal = -1;
		int fourthVal = -1;


		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == charLookupArray[1])
				firstVal = i;
			if (charArray[i] == charLookupArray[2])
				secondVal = i;
			if (charArray[i] == charLookupArray[3])
				thirdVal = i;
			if (charArray[i] == charLookupArray[4])
				fourthVal = i;

					
			
			if (firstVal > 0 
					&& secondVal > 0 
					&& thirdVal > 0
					&& fourthVal > 0
					) break;
	}
		
		

		return new int[] { firstVal, secondVal, thirdVal, fourthVal };

	}
}

enum Status {
	MAIN_MENU, RUNNING, PAUSED;
}
