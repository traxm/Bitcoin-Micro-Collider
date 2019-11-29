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
	private Address[] addressArray;
		
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
			
			//If the start delay is greater than zero assume this is the initial run (ie not a resumed event)
			if (startDelay > 0 && !isInterrupted) {
				bitcoinLotto.updateThreadCount();
				checkAddresses();	
			}

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void initialize(BitcoinLotto thisLotto, int thisStartDelay) {
		bitcoinLotto = thisLotto;
		addressArray = thisLotto.getAddressArray();
		startDelay = thisStartDelay;
	}

	private void checkAddresses() throws IOException, URISyntaxException {

		while (!isInterrupted) {
			//Generate a random address/key combination
			ECKey thisKey = new ECKey();
			//Create an address object
			Address pubAddress = Address.fromKey(MainNetParams.get(), thisKey, ScriptType.P2PKH);
			//Compare the newly-created address against the address list
			if (evaluateAddress(pubAddress))
				//If an address miraculously matches something from the address list
				handleMatch(thisKey);
		}
	}

	private Boolean evaluateAddress(Address thisAddress) {

		bitcoinLotto.logAddressCheck();

		for (int i = 0; i < addressArray.length; i++) {
			if (thisAddress.equals(addressArray[i]))
				return true;
		}
		return false;
	}

	private void handleMatch(ECKey thisKey) throws IOException {
		/*
		 * Called if an address match is made.  Saves the info to a text file.
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
		
		//Notifies the primary class so UI elements can be updated
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
