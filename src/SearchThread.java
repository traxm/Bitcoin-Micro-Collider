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

	private BitcoinLotto bitcoinLotto;
	private Address[][] addressArray;
	private int[][] indexArray;
	private char[] charArray;

	private Boolean isInterrupted = false;
	private int startDelay = 0;

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
				// Create the character array to speed searches
				this.charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
				// Update the active thread count
				bitcoinLotto.updateThreadCount();
				// Start checking addresses
				checkAddresses();
			}

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public SearchThread(BitcoinLotto thisLotto, ScriptType thisType, int thisStartDelay) {
		bitcoinLotto = thisLotto;
		addressArray = thisLotto.getAddressArray();
		this.indexArray = thisLotto.getIndexArray();
		startDelay = thisStartDelay;
	}

	private void checkAddresses() throws IOException, URISyntaxException {

		ECKey thisKey;
		Address pubAddress;

		while (!isInterrupted) {
			// Generate a random address/key combination
			thisKey = new ECKey();
			// Create an address object based on the address format
			pubAddress = Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH);

			// Get the correct address array
			Address[] thisAddressArray = this.getAddressArray(pubAddress);
			if (evaluateAddress(pubAddress, thisAddressArray))
				// If an address miraculously matches something from the address list
				handleMatch(thisKey);
		}
	}

	private Boolean evaluateAddress(Address thisAddress, Address[] thisArray) {

		bitcoinLotto.logAddressCheck();

		if (thisArray.length <= 0)
			return false;

		//System.out.println(thisAddress + " - " + thisArray[0]);

		for (int i = 0; i < thisArray.length; i++) {
			if (thisAddress.equals(thisArray[i]))
				return true;

		}
		return false;
	}

	private Address[] getAddressArray(Address thisAddress) {
		// Retrieves the correct address based on leading prefix

		String thisString = thisAddress.toString();
		char firstChar = thisString.charAt(1);
		char secondChar = thisString.charAt(2);

		int indexVal = this.getLookupIndex(firstChar, secondChar);

		return this.addressArray[indexVal];
	}

	private int getLookupIndex(char firstChar, char secondChar) {
		// Returns an index to lookup the correct segmented address array

		int firstVal = -1;
		int secondVal = -1;

		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == firstChar)
				firstVal = i;
			if (charArray[i] == secondChar)
				secondVal = i;
			
			if (firstVal > 0 && secondVal > 0) break;
		}

		return this.indexArray[firstVal][secondVal];

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

		FileWriter thisFile = new FileWriter("bitcoinLottoWinner" + randomInt + ".txt");
		BufferedWriter writer = new BufferedWriter(thisFile);
		writer.write(thisString);
		writer.close();

		// Notifies the primary class so UI elements can be updated
		bitcoinLotto.notifySuccess(Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH).toString(),
				thisKey.getPrivateKeyAsWiF(MainNetParams.get()));
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
