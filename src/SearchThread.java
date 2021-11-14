import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script.ScriptType;

public class SearchThread extends Thread {

	private BitcoinMicroCollider bitcoinCollider;
	private AddressGroup[][][][] addressArray;
	private String[] labelArray; //For gui
	private int thisThreadAddressCount = 0;
	private int labelCounter = 0;
	
	private Boolean isInterrupted = false;
	private int startDelay = 0;
	private Boolean isStringLabelProvider = false; //Does this thread populate the gui labels
	private String thisString;
	
	
	@Override
	public void run() {
		try {
			try {
				if (startDelay > 0)
					Thread.sleep(startDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// If the start delay is greater than zero assume this is the initial run (ie
			// not a resumed event)
			if (startDelay > 0 && !isInterrupted) {
				// Update the active thread count
				bitcoinCollider.updateThreadCount();
				// Start checking addresses
				checkAddresses();
			}

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public SearchThread(BitcoinMicroCollider thisCollider, ScriptType thisType, int thisStartDelay, Boolean isGuiProvider) {
		bitcoinCollider = thisCollider;
		addressArray = thisCollider.getAddressArray();
		this.isStringLabelProvider = isGuiProvider;
		startDelay = thisStartDelay;
		
		if (this.isStringLabelProvider)
			labelArray = bitcoinCollider.getAddressLabelArray();
		
	}

	private void checkAddresses() throws IOException, URISyntaxException {

		ECKey thisKey;
		Address pubAddress;

		while (!isInterrupted) {
			// Generate a random address/key combination
			thisKey = new ECKey();
			// Create an address object based on the address format
			pubAddress = Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH);
			
			// Get the correct address group
			if (evaluateAddress(pubAddress, this.getAddressArray(pubAddress)))
				// If an address miraculously matches something from the address list
				handleMatch(thisKey);
		}
	}

	private Boolean evaluateAddress(Address thisAddress, AddressGroup thisGroup) {

		bitcoinCollider.logAddressCheck();
		thisThreadAddressCount++;
		if (this.isStringLabelProvider)
			this.updateGuiElements(thisAddress);
		
		
		if (thisGroup.getAddressArray().length <= 0) {
			//Debug
			//System.out.println("skipping prefix value not indexed");
			return false;
		}
			

		//Debugging
		//System.out.println("----- testing against addresses count of : " + thisGroup.getAddressArray().length);
		
		for (int i = 0; i < thisGroup.getAddressArray().length; i++) {
			
			//Debugging
			//String string1 = thisAddress.toString();
			//String string2 = thisGroup.getAddressArray()[i].toString();
			//System.out.println("comparing " + string1 + " to " + string2);
			
			if (thisAddress.equals(thisGroup.getAddressArray()[i]))
				return true;

		}
		return false;
	}

	private AddressGroup getAddressArray(Address thisAddress) {
		// Retrieves the correct address based on leading prefix
		
		thisString = thisAddress.toString();
		int[] indexVal = bitcoinCollider.getLookupIndex(thisString);
		return this.addressArray[indexVal[0]][indexVal[1]][indexVal[2]][indexVal[3]];
	}



	private void handleMatch(ECKey thisKey) throws IOException {
		/*
		 * Called if an address match is made. Saves the info to a text file.
		 */

		String thisString = "";

		thisString += Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH);
		thisString += "\n";
		thisString += thisKey.getPrivateKeyAsHex();
		thisString += "\n";
		thisString += thisKey.getPublicKeyAsHex();
		thisString += "\n";
		thisString += thisKey.getPrivateKeyAsWiF(MainNetParams.get());

		int randomInt = new Random().nextInt(9999999);

		FileWriter thisFile = new FileWriter("bitcoin_Collider_Match_" + randomInt + ".txt");
		BufferedWriter writer = new BufferedWriter(thisFile);
		writer.write(thisString);
		writer.close();

		// Notifies the primary class so UI elements can be updated
		bitcoinCollider.notifySuccess(Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH).toString(),
				thisKey.getPrivateKeyAsWiF(MainNetParams.get()));
	}
	
	private void updateGuiElements(Address thisAddress) {
		//Populate the label array with some addresses checked
		if (thisThreadAddressCount % 25 == 0) {
			labelArray[labelCounter] = thisAddress.toString();
			
			this.labelCounter++;
			if (this.labelCounter >= 10) this.labelCounter = 0;
		}
	}

	public void resumeThread() {
		isInterrupted = false;
		try {
			checkAddresses();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void stopThread() {
		isInterrupted = true;
	}

	
	
	
}
