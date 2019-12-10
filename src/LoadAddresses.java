import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;

public class LoadAddresses extends Thread {

	private MicroBitcoinCollider bitcoinCollider;
	private PrimaryWindow primaryWindow;

	private File tempFile;

	private ProcessStatus thisProcessStatus = ProcessStatus.NOT_INITIALIZED;
	private Boolean isErrorShown = false;

	long addressCount = 0;
	char[] charArray;
	int[][] addressIndex; // Index values for address prefixes
	IndexHelper[] helperArray;

	@Override
	public void run() {
		initializeLookupAddresses();
	}

	public LoadAddresses(MicroBitcoinCollider thisCollider, PrimaryWindow thisWindow, File thisFile) {
		this.bitcoinCollider = thisCollider;
		this.tempFile = thisFile;
		this.primaryWindow = thisWindow;
		thisProcessStatus = ProcessStatus.INITIALIZED;

		// Setup the character array

		charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

		// Populate the address index array
		addressIndex = new int[charArray.length][charArray.length];
		int counterValue = 0;
		for (int i = 0; i < charArray.length; i++) {
			for (int j = 0; j < charArray.length; j++) {
				addressIndex[i][j] = counterValue;
				counterValue++;
				// System.out.println("set val " + i + " " + j + " to " + (counterValue-1));
			}
		}

	}

	private void initializeLookupAddresses() {
		/*
		 * Read the address file and populate an array with the data
		 */

		if (this.thisProcessStatus != ProcessStatus.INITIALIZED)
			return;

		primaryWindow.setStatusTextPane("LOADING ADDRESS FILE <br>");
		
		String[] addressStringArray;
		addressStringArray = readLines(this.tempFile);
		
		// Create index helper objects to speed process
		helperArray = new IndexHelper[addressStringArray.length];
		for (int i = 0; i < addressStringArray.length; i++) {
			helperArray[i] = new IndexHelper(addressStringArray[i]);
		}

		// Get address arrays for the three address types
		bitcoinCollider.setAddressArray(getAddressesFromArray(addressStringArray), this.addressIndex);

		primaryWindow.setStatusTextPane("--------------------<br>");
		primaryWindow.setStatusTextPane("<b>FINISHED LOADING " + String.format("%,d", addressCount) + " ADDRESSES</b>" + "<br>");
	
		
		// Update the UI with the number of addresses loaded
		primaryWindow.setTotalAddressLabel(this.getTotalAddressCount());

		// Allow the user to start the collider
		primaryWindow.setStartButtonEnabled(true);

		// Save the file path for future use
		saveFilePath(this.tempFile);

		// Write a small test file to confirm that any matched addresses can be saved
		writeStarterFile();

	}

	private Address[][] getAddressesFromArray(String[] sourceAddresses) {

		Address thisAddress = null;
		ArrayList<Address> addressList = null;

		addressList = new ArrayList<Address>();

		for (int i = 0; i < sourceAddresses.length; i++) {

			// Skip broken addresses
			if (sourceAddresses[i].length() > 35 || sourceAddresses[i].isEmpty())
				continue;

			// Only pull addresses of the specified type (we ignore bech32 since it
			// represents a small portion of addresses)

			// Create an address object from the string
			try {
				thisAddress = Address.fromString(MainNetParams.get(), sourceAddresses[i]);
			} catch (org.bitcoinj.core.AddressFormatException ex) {
				// ex.printStackTrace();
				if (!this.isErrorShown) {
					primaryWindow.showFileErrorMessage();
					primaryWindow.setStatusTextPane("Address format problem on entry " + sourceAddresses[i] + " <br>");
				}
				this.isErrorShown = true;
				//System.out.println("bad address on line " + i + ": " + sourceAddresses[i]);
				primaryWindow.setStatusTextPane("Address format problem on entry " + sourceAddresses[i] + " <br>");
				continue;
			}
			addressList.add(thisAddress);
		}

		Address[] tempAddressArray = new Address[addressList.size()];
		tempAddressArray = addressList.toArray(tempAddressArray);
		return this.setSegmentedArray_2d(tempAddressArray);
	}

	private Address[][] setSegmentedArray_2d(Address[] thisAddressArray) {
		// Returns a 2D array divided by addresses starting letter

		// Create an arraylist to house the Address arrays
		ArrayList<Address[]> thisList = new ArrayList<Address[]>();

		// Add an array based on each char to the list
		for (int i = 0; i < charArray.length; i++) {
			for (int j = 0; j < charArray.length; j++) {
				Address[] thisArray = getSegmentedArray(thisAddressArray, charArray[i], charArray[j]);
				thisList.add(thisArray);
			}
		}

		// Create the final 3D array
		Address[][] tempArray = new Address[thisList.size()][];
		for (int i = 0; i < thisList.size(); i++)
			tempArray[i] = thisList.get(i);
		
		return tempArray;
	}

	private Address[] getSegmentedArray(Address[] thisArray, char firstPrefix, char secondPrefix) {
		// Returns an array of addresses with a specific leading char
		ArrayList<Address> thisList = new ArrayList<Address>();

		for (int i = 0; i < helperArray.length; i++) {
			if (helperArray[i].isMatch(firstPrefix, secondPrefix)) {
				thisList.add(thisArray[i]);
				addressCount++;
				if (addressCount % 1000 == 0)
					primaryWindow.setStatusTextPane("Loading addresses : " + String.format("%,d", addressCount) + "<br>");
				continue;
			}
		}

		Address[] tempArray = new Address[thisList.size()];
		return thisList.toArray(tempArray);
	}

	private String[] readLines(File thisFile) {
		/*
		 * Read lines from a file
		 */

		long startTime = System.nanoTime();

		BufferedReader bufferedReader;

		FileInputStream thisStream;
		List<String> lines = null;
		try {
			thisStream = new FileInputStream(thisFile);
			bufferedReader = new BufferedReader(new InputStreamReader(thisStream));

			lines = new ArrayList<String>();
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				// only add addresses starting with 1
				if (line.charAt(0) == '1')
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

		long endTime = System.nanoTime();

		primaryWindow.setStatusTextPane("File text read time " + ((endTime - startTime)/10000000) + "ms");
		primaryWindow.setStatusTextPane("Loading addresses into memory... <br>");

		return lines.toArray(new String[lines.size()]);
	}

	private long getTotalAddressCount() {
		// Counts the number of addresses across all arrays

		Address[][] thisArray = this.bitcoinCollider.getAddressArray();

		long totalCount = 0;

		for (int i = 0; i < thisArray.length; i++)
			totalCount += thisArray[i].length;

		return totalCount;
	}

	private void saveFilePath(File thisFile) {
		// Saves the file path for future use
		String filePathString = thisFile.getAbsolutePath();

		Preferences prefs = Preferences.userRoot().node(MicroBitcoinCollider.prefsNode);
		prefs.put(MicroBitcoinCollider.prefsFileString, filePathString);
	}

	public static Boolean isFileAvailable(String filePath) {
		// Checks to see if a last address file is available to load

		if (filePath == null)
			return false;

		File tempFile = new File(filePath);
		return tempFile.exists();
	}

	public void writeStarterFile() {
		// Writes a blank file to confirm write process functions as intended
		String thisString = "Ignore this file";
		FileWriter thisFile;
		try {
			thisFile = new FileWriter("testFilePleaseIgnore.txt");
			BufferedWriter writer = new BufferedWriter(thisFile);
			writer.write(thisString);
			writer.close();
		} catch (IOException e) {
			//Exit
			primaryWindow.showFileErrorMessage();
		}
	}

	enum ProcessStatus {
		NOT_INITIALIZED, INITIALIZING, INITIALIZED;
	}

	class IndexHelper {

		public IndexHelper(String thisString) {
			char1 = thisString.charAt(1);
			char2 = thisString.charAt(2);
		}

		public Boolean isMatch(char c1, char c2) {
			if (c1 != char1) return false;
			if (c2 != char2) return false;
			
			return true;			
		}

		// Use custom class to only perform string operations once
		char char1;
		char char2;
	}

}
