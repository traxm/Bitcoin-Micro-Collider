import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.prefs.Preferences;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;

public class LoadAddresses extends Thread {

	private int addressLimit = 10000000;
	private int reportingIncrement = 50000;
	
	private BitcoinMicroCollider bitcoinCollider;
	private PrimaryWindow primaryWindow;

	private File tempFile;
		
	private ProcessStatus thisProcessStatus = ProcessStatus.NOT_INITIALIZED;
	private Boolean isErrorShown = false;

	@Override
	public void run() {
		initializeLookupAddresses();
	}

	public LoadAddresses(BitcoinMicroCollider thisCollider, PrimaryWindow thisWindow, File thisFile) {
		this.bitcoinCollider = thisCollider;
		this.tempFile = thisFile;
		this.primaryWindow = thisWindow;
		thisProcessStatus = ProcessStatus.INITIALIZED;



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
		

		// Get address arrays for the three address types
		bitcoinCollider.setAddressArray(getAddressesFromArray(addressStringArray));
		
		//Clear the array
		addressStringArray = null;

		long addressCount = this.getTotalAddressCount();
		
		primaryWindow.setStatusTextPane("--------------------<br>");
		primaryWindow.setStatusTextPane("<b>FINISHED LOADING " + String.format("%,d", addressCount) + " ADDRESSES</b>" + "<br>");
	
		bitcoinCollider.setFileStatus(FileStatus.FILE_LOADED);
		
		// Update the UI with the number of addresses loaded
		primaryWindow.setTotalAddressLabel(addressCount);

		// Allow the user to start the collider
		primaryWindow.setStartButtonEnabled(true);
		primaryWindow.setLoadFileButtonEnabled(true);

		// Save the file path for future use
		saveFilePath(this.tempFile);

		// Write a small test file to confirm that any matched addresses can be saved
		writeStarterFile();

	}

	private AddressGroup[][][][] getAddressesFromArray(String[] sourceAddresses) {

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
			
			//Update the UI
			if (i % reportingIncrement == 0)
				primaryWindow.setStatusTextPane("Sorting addresses : " + String.format("%,d", i) + "<br>");
			
		}

		Address[] tempAddressArray = new Address[addressList.size()];
		
		for (int i=0; i < addressList.size(); i++ ) {
			tempAddressArray[i] = addressList.get(i);
			
			//Update the UI
			if (i % reportingIncrement == 0)
				primaryWindow.setStatusTextPane("Populating addresses : " + String.format("%,d", i) + "<br>");
		}
		
		
		return this.createSegmentedArray(tempAddressArray);
	}

	private AddressGroup[][][][] createSegmentedArray(Address[] thisAddressArray) {
		// Returns a 3D array divided by addresses starting letter
		

		int arrayLength = BitcoinMicroCollider.charArray.length;
		
		AddressGroup[][][][] tempArray = new AddressGroup[arrayLength][arrayLength][arrayLength][arrayLength];
				
		
		//Create the address groups
		for (int a = 0; a < arrayLength; a++) {
			for (int b = 0; b < arrayLength; b++) {
				for (int c = 0; c < arrayLength; c++) {
					for (int d = 0; d < arrayLength; d++) {
									tempArray[a][b][c][d] = new AddressGroup();

					
					}		
				}
			}
		}

		
		//Populate the array with sorted addresses
		for (int i = 0; i < thisAddressArray.length; i++) {
			IndexHelper thisHelper = new IndexHelper(thisAddressArray[i].toString());
			int[] lookupIndex = thisHelper.getLookupIndex();
			tempArray[lookupIndex[0]] [lookupIndex[1]] [lookupIndex[2]] [lookupIndex[3]].addAddress(thisAddressArray[i]);
			
		}

		
		for (int a = 0; a < arrayLength; a++) {
			for (int b = 0; b < arrayLength; b++) {
				for (int c = 0; c < arrayLength; c++) {
					for (int d = 0; d < arrayLength; d++) {
								tempArray[a][b][c][d].finalizeGroup();
												
						}
					}
				}
			}
				
		
		return tempArray;
	}


	private String[] readLines(File thisFile) {
		/*
		 * Read lines from a file
		 */

		int lineCounter = 0;
		
		long startTime = System.nanoTime();

		Scanner thisScanner = null;
		
		
		FileInputStream thisStream = null;
		List<String> linesList = null;
		try {
			
			linesList = new ArrayList<String>();
			String line = null;
			
			thisStream = new FileInputStream(thisFile);
			thisScanner = new Scanner(thisStream, "UTF-8");
			
			while (thisScanner.hasNextLine() && lineCounter < addressLimit) {

				line = thisScanner.nextLine();
							
				// only add addresses starting with 1
				if (line.charAt(0) == '1') {
					linesList.add(line);
					lineCounter++;
					//System.out.println("Read: " + line);
					//System.out.println("Added a Line " + linesList.size());
					if (lineCounter % reportingIncrement == 0)
						primaryWindow.setStatusTextPane("Reading addresses : " + String.format("%,d", lineCounter) + "<br>");
					
				}
			}
			
		} catch (FileNotFoundException e) {
			primaryWindow.showFileErrorMessage();
			e.printStackTrace();

		} finally {
			if (thisStream != null)
				try {
					thisStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			if (thisStream != null)
				try {
					thisStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		long endTime = System.nanoTime();

		primaryWindow.setStatusTextPane("File text read time " + ((endTime - startTime)/10000000) + "ms");
		primaryWindow.setStatusTextPane("Loading addresses into memory... <br>");

		
		String[] returnArray = new String[linesList.size()];
		
		for (int i=0; i < linesList.size(); i++) {
			returnArray[i] =linesList.get(i);
			
			if (i % reportingIncrement == 0)
				primaryWindow.setStatusTextPane("Converting addresses : " + String.format("%,d", i) + "<br>");
			
		}
		
		return returnArray;
		
		
		
		//return linesList.toArray(new String[linesList.size()]);
	}

	private long getTotalAddressCount() {
		// Counts the number of addresses across all arrays

		AddressGroup[][][][] thisArray = this.bitcoinCollider.getAddressArray();

		long totalCount = 0;

		for (int a = 0; a < thisArray.length; a++)
			for (int b = 0; b < thisArray[a].length; b++) {
				for (int c = 0; c < thisArray[a][b].length; c++)
					for (int d = 0; d < thisArray[a][b][c].length; d++) {
					totalCount += thisArray[a][b][c][d].getAddressArray().length;
				}
			}
			

		return totalCount;
	}

	private void saveFilePath(File thisFile) {
		// Saves the file path for future use
		String filePathString = thisFile.getAbsolutePath();

		Preferences prefs = Preferences.userRoot().node(BitcoinMicroCollider.prefsNode);
		prefs.put(BitcoinMicroCollider.prefsFileString, filePathString);
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

		char[] charArray;
		
		public IndexHelper(String thisString) {		
			charArray = thisString.toCharArray();
			
		}

		public int[] getLookupIndex() {
			return bitcoinCollider.getLookupIndex(charArray);
		}

	}
	
	
}
