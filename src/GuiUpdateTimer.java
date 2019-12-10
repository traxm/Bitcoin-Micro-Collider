import java.util.TimerTask;

public class GuiUpdateTimer extends TimerTask {

	private MicroBitcoinCollider bitcoinCollider;
	
	public GuiUpdateTimer(MicroBitcoinCollider thisCollider) {
		bitcoinCollider = thisCollider;
	}
	
	
	@Override
	public void run() {
		bitcoinCollider.updateGuiElements();
	}
	


}
