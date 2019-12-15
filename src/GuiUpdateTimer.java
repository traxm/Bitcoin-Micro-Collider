import java.util.TimerTask;

public class GuiUpdateTimer extends TimerTask {

	private BitcoinMicroCollider bitcoinCollider;
	
	public GuiUpdateTimer(BitcoinMicroCollider thisCollider) {
		bitcoinCollider = thisCollider;
	}
	
	
	@Override
	public void run() {
		bitcoinCollider.updateGuiElements();
		
	}
	


}
