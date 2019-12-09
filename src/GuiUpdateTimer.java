import java.util.TimerTask;

public class GuiUpdateTimer extends TimerTask {

	private BitcoinLotto bitcoinLotto;
	
	public GuiUpdateTimer(BitcoinLotto thisLotto) {
		bitcoinLotto = thisLotto;
	}
	
	
	@Override
	public void run() {
		bitcoinLotto.updateGuiElements();
	}
	


}
