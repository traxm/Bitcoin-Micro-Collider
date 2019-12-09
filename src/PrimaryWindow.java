import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import java.awt.CardLayout;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import java.awt.Color;
import javax.swing.JFileChooser;
import java.awt.SystemColor;
import javax.swing.JTextPane;

public class PrimaryWindow {

	private JFrame frmBitcoinLotto;
	private JTextField fileAddressField;
	private JButton startButton;
	private BitcoinLotto bitcoinLotto;
	private JPanel mainPanel;
	private JPanel workingPanel;
	private JButton pauseButton;
	private JProgressBar progressBar;
	private JSpinner threadSpinner;
	private JButton loadFileButton;
	private JTextPane statusTextPane;
	
	private JLabel totalAttemptsLabel;
	private JLabel attemptsPerSecondLabel;
	private JLabel totalAddressLabel;
	private JLabel successLabel;
	private JLabel timeLabel;
	private JLabel threadCountLabel;
	private JLabel matchedKeyLabel;
	private JLabel matchedAddressLabel;
	private JScrollPane scrollPane;

	/*
	 * Create the application.
	 */
	public PrimaryWindow(BitcoinLotto thisLotto) {
		bitcoinLotto = thisLotto;
		initialize();
	}

	public void setWindowEnabled(Boolean thisBool) {
		this.frmBitcoinLotto.setVisible(thisBool);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		UIManager.put("ProgressBar[Enabled+Finished].foregroundPainter", Color.black);

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		}

		frmBitcoinLotto = new JFrame();
		frmBitcoinLotto.setTitle("Bitcoin Lotto");
		frmBitcoinLotto.setBounds(100, 100, 450, 700);
		frmBitcoinLotto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBitcoinLotto.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 661);
		frmBitcoinLotto.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));

		mainPanel = new JPanel();
		panel.add(mainPanel, "name_432466542248400");
		mainPanel.setLayout(null);

		JLabel lblBitcoinLotto = new JLabel("Bitcoin Lotto!");
		lblBitcoinLotto.setBounds(34, 11, 365, 75);
		mainPanel.add(lblBitcoinLotto);
		lblBitcoinLotto.setHorizontalAlignment(SwingConstants.CENTER);
		lblBitcoinLotto.setFont(new Font("Tahoma", Font.PLAIN, 62));

		loadFileButton = new JButton("Choose Address File");
		loadFileButton.setBounds(19, 455, 211, 33);
		loadFileButton.addActionListener(new FileBrowserListener());
		mainPanel.add(loadFileButton);
		loadFileButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		fileAddressField = new JTextField();
		fileAddressField.setBounds(19, 419, 396, 25);
		mainPanel.add(fileAddressField);
		fileAddressField.setColumns(10);

		JLabel lblLottoThreads = new JLabel("Lotto Threads");
		lblLottoThreads.setBounds(10, 331, 414, 33);
		mainPanel.add(lblLottoThreads);
		lblLottoThreads.setHorizontalAlignment(SwingConstants.CENTER);
		lblLottoThreads.setFont(new Font("Tahoma", Font.PLAIN, 24));

		startButton = new JButton("Start Lotto!");
		startButton.setBounds(10, 499, 300, 65);
		mainPanel.add(startButton);
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 24));
		startButton.setEnabled(false);

		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(320, 499, 104, 65);
		btnExit.setBackground(Color.red);
		mainPanel.add(btnExit);
		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JTextArea txtrTheBitcoinLotto = new JTextArea();
		txtrTheBitcoinLotto.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtrTheBitcoinLotto.setForeground(Color.BLACK);
		txtrTheBitcoinLotto.setBackground(SystemColor.controlHighlight);
		txtrTheBitcoinLotto.setEditable(false);
		txtrTheBitcoinLotto.setWrapStyleWord(true);
		txtrTheBitcoinLotto.setLineWrap(true);
		txtrTheBitcoinLotto.setText(
				"The Bitcoin Lotto creates random bitcoin private/public key pairs and compares the addresses to a list of existing addresses.  In the event that an address is matched, the public/private keys are saved to a text file in the program directory.\r\n\r\n- Step 1: Load a text file of bitcoin addresses which you would like to check for matches (one address per line)\r\n- Step 2: Start the lotto");
		txtrTheBitcoinLotto.setBounds(10, 97, 414, 213);
		mainPanel.add(txtrTheBitcoinLotto);

		JSeparator separator = new JSeparator();
		separator.setBounds(22, 321, 390, 2);
		mainPanel.add(separator);

		threadSpinner = new JSpinner();
		threadSpinner.setFont(new Font("Tahoma", Font.PLAIN, 22));
		threadSpinner.setBounds(165, 373, 104, 35);
		mainPanel.add(threadSpinner);
		SpinnerModel spinnerModel = new SpinnerNumberModel(4, 1, 64, 1);
		threadSpinner.setModel(spinnerModel);

		((JSpinner.DefaultEditor) threadSpinner.getEditor()).getTextField().setEditable(false);

		btnExit.addActionListener(new ExitListener());
		startButton.addActionListener(new StartListener());
		startButton.setBackground(Color.green);
		
		statusTextPane = new JTextPane();
		statusTextPane.setForeground(Color.BLUE);
		statusTextPane.setBackground(Color.WHITE);
		statusTextPane.setContentType("text/html");
		statusTextPane.setEditable(false);
		//statusTextPane.setBounds(10, 585, 151, 65);
		statusTextPane.setAutoscrolls(true);
		mainPanel.add(statusTextPane);
		
		scrollPane = new JScrollPane(statusTextPane);
		scrollPane.setBounds(10, 570, 414, 80);
		scrollPane.setAutoscrolls(true);
		mainPanel.add(scrollPane);
		

		workingPanel = new JPanel();
		panel.add(workingPanel, "name_432575102303500");
		workingPanel.setLayout(null);

		JLabel lblBitcoinLotto_1 = new JLabel("Bitcoin Lotto!");
		lblBitcoinLotto_1.setBounds(76, 5, 281, 51);
		lblBitcoinLotto_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblBitcoinLotto_1.setFont(new Font("Tahoma", Font.PLAIN, 42));
		workingPanel.add(lblBitcoinLotto_1);

		JLabel label_1 = new JLabel("Lottery Attempts");
		label_1.setBounds(10, 65, 414, 51);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 32));
		workingPanel.add(label_1);

		totalAttemptsLabel = new JLabel("0");
		totalAttemptsLabel.setBounds(10, 110, 414, 34);
		totalAttemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalAttemptsLabel.setFont(new Font("Tahoma", Font.PLAIN, 32));
		workingPanel.add(totalAttemptsLabel);

		JLabel label_3 = new JLabel("Time Elapsed");
		label_3.setBounds(152, 246, 130, 27);
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 22));
		workingPanel.add(label_3);

		timeLabel = new JLabel("0");
		timeLabel.setBounds(128, 270, 177, 29);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		workingPanel.add(timeLabel);

		pauseButton = new JButton("Pause Lotto");
		pauseButton.setBounds(10, 541, 414, 51);
		pauseButton.setFont(new Font("Tahoma", Font.PLAIN, 32));
		pauseButton.addActionListener(new PauseListener());
		pauseButton.setBackground(Color.yellow);
		workingPanel.add(pauseButton);

		JButton exitButton = new JButton("Exit");
		exitButton.setBounds(312, 603, 112, 47);
		exitButton.setFont(new Font("Tahoma", Font.PLAIN, 32));
		exitButton.setBackground(Color.red);
		exitButton.addActionListener(new ExitListener());
		workingPanel.add(exitButton);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(18, 164, 398, 34);
		workingPanel.add(progressBar);

		JLabel lblAverageSpeedper = new JLabel("Attempts per second");
		lblAverageSpeedper.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageSpeedper.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblAverageSpeedper.setBounds(10, 310, 414, 27);
		workingPanel.add(lblAverageSpeedper);

		attemptsPerSecondLabel = new JLabel("0");
		attemptsPerSecondLabel.setHorizontalAlignment(SwingConstants.CENTER);
		attemptsPerSecondLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		attemptsPerSecondLabel.setBounds(128, 334, 177, 29);
		workingPanel.add(attemptsPerSecondLabel);

		JLabel lblAnyMatchesThis = new JLabel("Any matches this session?");
		lblAnyMatchesThis.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnyMatchesThis.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblAnyMatchesThis.setBounds(10, 380, 414, 27);
		workingPanel.add(lblAnyMatchesThis);

		successLabel = new JLabel("NO");
		successLabel.setForeground(Color.RED);
		successLabel.setHorizontalAlignment(SwingConstants.CENTER);
		successLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
		successLabel.setBounds(119, 409, 195, 47);
		workingPanel.add(successLabel);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(26, 522, 398, 8);
		workingPanel.add(separator_1);

		totalAddressLabel = new JLabel("0");
		totalAddressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		totalAddressLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		totalAddressLabel.setBounds(138, 198, 122, 15);
		workingPanel.add(totalAddressLabel);

		JLabel addressTotal = new JLabel("Target Addresses:");
		addressTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		addressTotal.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addressTotal.setBounds(6, 198, 122, 15);
		workingPanel.add(addressTotal);

		threadCountLabel = new JLabel("0");
		threadCountLabel.setHorizontalAlignment(SwingConstants.LEFT);
		threadCountLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		threadCountLabel.setBounds(360, 198, 57, 15);
		workingPanel.add(threadCountLabel);

		JLabel lblThreadsActive = new JLabel("Threads Active:");
		lblThreadsActive.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreadsActive.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblThreadsActive.setBounds(259, 198, 95, 15);
		workingPanel.add(lblThreadsActive);

		JLabel matchedAddress = new JLabel("Matched Public Address:");
		matchedAddress.setHorizontalAlignment(SwingConstants.LEFT);
		matchedAddress.setBounds(10, 441, 137, 15);
		workingPanel.add(matchedAddress);

		JLabel matchedKey = new JLabel("Matched Private Key:");
		matchedKey.setHorizontalAlignment(SwingConstants.LEFT);
		matchedKey.setBounds(10, 479, 152, 15);
		workingPanel.add(matchedKey);

		matchedAddressLabel = new JLabel("Address");
		matchedAddressLabel.setForeground(Color.BLACK);
		matchedAddressLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		matchedAddressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		matchedAddressLabel.setBounds(10, 456, 414, 21);
		matchedAddressLabel.setText("none");
		workingPanel.add(matchedAddressLabel);

		matchedKeyLabel = new JLabel("Address");
		matchedKeyLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		matchedKeyLabel.setHorizontalAlignment(SwingConstants.LEFT);
		matchedKeyLabel.setBounds(10, 492, 414, 21);
		matchedKeyLabel.setText("none");
		workingPanel.add(matchedKeyLabel);

	}

	public void setPanel(int panelIndex) {
		switch (panelIndex) {
		case 0:
			mainPanel.setVisible(true);
			workingPanel.setVisible(false);
			break;
		case 1:
			mainPanel.setVisible(false);
			workingPanel.setVisible(true);
			break;
		}
	}

	public void setScanAttemptsLabel(long thisLong) {
		this.totalAttemptsLabel.setText(String.format("%,d", thisLong));
	}
	
	public void setTotalAddressLabel(long thisLong) {
		this.totalAddressLabel.setText(String.format("%,d", thisLong));
	}

	public void setSuccessLabel(Boolean thisBool, String matchedAddress, String matchedPrivateKey) {
		if (thisBool) {
			this.successLabel.setText("YES!!");
			this.successLabel.setForeground(Color.GREEN);

			this.matchedAddressLabel.setText(matchedAddress);
			this.matchedKeyLabel.setText(matchedPrivateKey);
		}
	}

	public void setThreadsActiveLabel(int thisThreadCount) {
		this.threadCountLabel.setText(String.format("%,d", thisThreadCount));
	}

	public void setStartButtonEnabled(Boolean thisBool) {
		startButton.setEnabled(thisBool);
	}
	
	public void setAddressFile(File thisFile) {
		
		this.setStartButtonEnabled(false);

		fileAddressField.setText(thisFile.getPath());
		
		//Set the new address file
		LoadAddresses loader = new LoadAddresses(this.bitcoinLotto, this, thisFile);
		loader.run();
	}
	
	public void setThreadCount(int thisInt) {
		threadSpinner.setValue(Integer.valueOf(thisInt));
	}
	
	public void setTimeLabel(long duration, long addressesChecked) {

		long second = (duration / 1000) % 60;
		long minute = (duration / (1000 * 60)) % 60;
		long hour = (duration / (1000 * 60 * 60)) % 24;

		String time = String.format("%04d:%02d:%02d", hour, minute, second);
		this.timeLabel.setText(String.format(time));

		// Set attempts per second
		duration /= 1000;
		if (duration <= 0)
			return;
		attemptsPerSecondLabel.setText(String.format("%,d", addressesChecked / duration));
	}
	
	public void showFileErrorMessage() {
		JOptionPane.showMessageDialog(frmBitcoinLotto, "File read/write failed.");
	}
	
	public void showAddressFormatMessage() {
		JOptionPane.showMessageDialog(frmBitcoinLotto, "One or more addresses are have incorrect formats");
	}
	
	public void setStatusTextPane(String thisString) {
		
		HTMLDocument doc = (HTMLDocument) statusTextPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)statusTextPane.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), thisString, 0, 0, null);
			statusTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class PauseListener implements ActionListener {

		Boolean isPaused = false;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (isPaused) {
				isPaused = false;
				pauseButton.setBackground(Color.yellow);
				pauseButton.setText("Pause Lotto");
				progressBar.setVisible(true);
				bitcoinLotto.resumeAllThreads();
			} else {
				isPaused = true;
				pauseButton.setBackground(Color.green);
				pauseButton.setText("Resume Lotto");
				progressBar.setVisible(false);
				bitcoinLotto.pauseAllThreads();
			}
		}
	}

	public class StartListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				int cpuThreads = (Integer) threadSpinner.getValue();
				startButton.setText("Loading...");
				bitcoinLotto.startLotto(cpuThreads);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public class FileBrowserListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
			fileChooser.setFileFilter(filter);
			
			int returnVal = fileChooser.showOpenDialog(frmBitcoinLotto);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File thisFile = fileChooser.getSelectedFile();
				setAddressFile(thisFile);
			}		
		}
	}

	public class ExitListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}
		
}
