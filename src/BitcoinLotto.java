import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.prefs.Preferences;

import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;

public class BitcoinLotto {

	public static void main(String[] args) {
		BitcoinLotto thisLotto = new BitcoinLotto();
		thisLotto.startGui();
	}

	private Status lottoStatus = Status.MAIN_MENU;
	private Boolean isInitialized = false;
	private Address[] addressArray;
	private SearchThread[] threadArray;
	private Timer guiTimer;

	private GuiUpdateTimer guiUpdateTimer;

	private PrimaryWindow primaryWindow;
	private long totalCheckCount = 0;
	private long startTime = 0;
	private int threadCount = 1;

	private int activeThreadCount = 0; // threads loaded and running
	private long pausedTimeTotal = 0;

	private long pauseStartTime = 0;
	private String prefsNode = "com/github/traxm/BitcoinLotto";
	private String prefsFileString = "filePath";

	private String prefsThreadString = "threadCount";

	public Address[] getAddressArray() {
		return addressArray;
	}

	public void initializeLookupAddresses(File thisFile) {
		/*
		 * Read the address file and populate an array with the data
		 */

		String[] addressStringArray;
		Address thisAddress = null;
		ArrayList<Address> addressList = null;

		addressStringArray = readLines(thisFile);
		addressList = new ArrayList<Address>();

		for (int i = 0; i < addressStringArray.length; i++) {
			try {
				thisAddress = Address.fromString(MainNetParams.get(), addressStringArray[i]);
			} catch (org.bitcoinj.core.AddressFormatException ex) {
				primaryWindow.showFileErrorMessage();
				return;
			}
			addressList.add(thisAddress);
		}

		addressArray = new Address[addressList.size()];
		addressArray = addressList.toArray(addressArray);

		// Check to see if any addresses entries were read
		if (addressArray == null || addressArray.length <= 0)
			return;

		// Update the UI with the number of addresses loaded
		primaryWindow.setTotalAddressLabel(addressArray.length);

		// Allow the user to start the lotto
		primaryWindow.enableStartButton();

		// Save the file path for future use
		saveFilePath(thisFile);

		// Write a small test file to confirm that any matched addresses can be saved
		writeStarterFile();

		isInitialized = true;
	}

	private Boolean isFileAvailable(String filePath) {
		// Checks to see if a last address file is available to load

		if (filePath == null)
			return false;

		File tempFile = new File(filePath);
		return tempFile.exists();
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

		lottoStatus = Status.PAUSED;

		for (int i = 0; i < this.threadArray.length; i++)
			this.threadArray[i].stopThread();

		pauseStartTime = System.nanoTime();
		activeThreadCount = 0;
		primaryWindow.setThreadsActiveLabel(activeThreadCount);
	}

	private String[] readLines(File thisFile) {
		/*
		 * Read lines from a file
		 */

		BufferedReader bufferedReader;

		FileInputStream thisStream;
		List<String> lines = null;
		try {
			thisStream = new FileInputStream(thisFile);
			bufferedReader = new BufferedReader(new InputStreamReader(thisStream));

			lines = new ArrayList<String>();
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}

			bufferedReader.close();
		} catch (FileNotFoundException e) {
			primaryWindow.showFileErrorMessage();
			e.printStackTrace();

		} catch (IOException e) {
			primaryWindow.showFileErrorMessage();
			e.printStackTrace();
		}

		return lines.toArray(new String[lines.size()]);
	}

	public void resumeAllThreads() {
		/*
		 * Create and start new threads based on user input
		 */

		lottoStatus = Status.RUNNING;

		this.startThreads(threadCount);

		// Update the paused time
		this.pausedTimeTotal += (System.nanoTime() - this.pauseStartTime);
	}

	private void saveFilePath(File thisFile) {
		// Saves the file path for future use
		String filePathString = thisFile.getAbsolutePath();

		Preferences prefs = Preferences.userRoot().node(prefsNode);
		prefs.put(prefsFileString, filePathString);
	}

	private void saveThreadCount(int threadCount) {
		// Saves the file path for future use
		Preferences prefs = Preferences.userRoot().node(prefsNode);
		prefs.putInt(prefsThreadString, threadCount);
	}

	private void startGui() {
		/*
		 * Create a frame and populate with components
		 */
		primaryWindow = new PrimaryWindow(this);

		// Check to see if an existing address file is available
		Preferences prefs = Preferences.userRoot().node(prefsNode);
		// Does the preference entry exist
		// Is the file available
		if (isFileAvailable(prefs.get(prefsFileString, null))) {
			// Load the existing file
			System.out.println(isFileAvailable(prefs.get(prefsFileString, null)));
			primaryWindow.setAddressFile(new File(prefs.get(prefsFileString, null)));

			// Set the thread count
			primaryWindow.setThreadCount(prefs.getInt(this.prefsThreadString, 4));
		}

		primaryWindow.setWindowEnabled(true);

	}

	public void startLotto(int threadCount) throws URISyntaxException {
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
			SearchThread thisThread = new SearchThread(); // Create a Search Thread instance
			threadArray[i] = thisThread; // Add the instance to the thread array
			thisThread.initialize(this, (i + 1) * 1000); // Initialize the thread and pass startup delay/sleep duration
		}

		// Start each of the threads
		for (int i = 0; i < threadCount; i++) {
			threadArray[i].start();
		}

		// Update the lotto status
		lottoStatus = Status.RUNNING;
	}

	public void updateGuiElements() {
		// Update all gui values

		if (lottoStatus == Status.PAUSED)
			return;

		this.primaryWindow.setScanAttemptsLabel(totalCheckCount);

		long duration = System.nanoTime() - startTime - this.pausedTimeTotal;
		this.primaryWindow.setTimeLabel(duration / 1000000, totalCheckCount);
	}

	public void updateThreadCount() {
		// Called by threads at start - active threads communicated via UI

		if (activeThreadCount > threadCount)
			return;

		activeThreadCount++;
		primaryWindow.setThreadsActiveLabel(activeThreadCount);
	}

	private void writeStarterFile() {
		// Writes a blank file to confirm write process functions as intended
		String thisString = "Ignore this file";
		FileWriter thisFile;
		try {
			thisFile = new FileWriter("testFilePleaseIgnore.txt");
			BufferedWriter writer = new BufferedWriter(thisFile);
			writer.write(thisString);
			writer.close();
		} catch (IOException e) {
			// Exit
			primaryWindow.showFileErrorMessage();
		}
	}

}

enum Status {
	MAIN_MENU, RUNNING, PAUSED;
}