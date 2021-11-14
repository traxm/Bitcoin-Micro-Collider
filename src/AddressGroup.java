import java.util.ArrayList;

import org.bitcoinj.core.Address;

public class AddressGroup {

	ArrayList<Address> addressList;
	Address[] addressArray;
	
	boolean isFinalized = false;		//Has the arraylist been coverted to an array
	
	
	public AddressGroup() {
		addressList = new ArrayList<Address>();
	}
	
	public void addAddressArray(Address[] thisArray) {		
		for (int i=0; i < thisArray.length; i++) {
			addressList.add(thisArray[i]);
		}
	}
	
	public void addAddress(Address thisAddress) {
		addressList.add(thisAddress);
	}
	
	public Address[] getAddressArray() {
		return addressArray;
	}
	
	public void finalizeGroup() {
		
		addressArray = new Address[addressList.size()];
		
		for (int i = 0; i < addressList.size(); i++)
			addressArray[i] = addressList.get(i);
		
		addressList = null;
		
		isFinalized = true;
	}


}
