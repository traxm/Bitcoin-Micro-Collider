import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Insets;
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
import javax.swing.BorderFactory;
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
import java.awt.Dimension;

import javax.swing.JFileChooser;
import java.awt.SystemColor;
import javax.swing.JTextPane;

public class PrimaryWindow {

	private JFrame mbcFrame;
	private JTextField fileAddressField;
	private JButton startButton;
	private BitcoinMicroCollider microCollider;
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
	private JLabel[] addressLabelArray = new JLabel[10];

	
	private Color color_1;
	private Color color_2;
	private Color color_3;
	private Color color_4;
	private Color color_5;
	private Color exitColor;
	private Color acceptColor;
	private Color pauseColor;
	private Color disabledColor;
	
	
	
	/*
	 * Create the application.
	 */
	public PrimaryWindow(BitcoinMicroCollider thisCollider) {
		microCollider = thisCollider;
		initialize();
	}

	public void setWindowEnabled(Boolean thisBool) {
		//this.mbcFrame.setResizable(false);
		this.mbcFrame.setVisible(thisBool);
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
		
		color_1 = Color.decode("#4A6491");
		color_2 = Color.decode("#30395C"); 
		color_3 = Color.decode("#4A6491");
		color_4 = Color.decode("#EC7263");
		color_5 = Color.decode("#D0E4F2");
		exitColor = Color.decode("#C1292E");
		acceptColor = Color.decode("#4CB963");
		pauseColor = Color.decode("#F5F749");
		disabledColor = Color.decode("#545454");
		
		UIManager.getLookAndFeelDefaults().put("nimbusOrange", color_3);

		
		
		mbcFrame = new JFrame();
		mbcFrame.setTitle("Bitcoin Micro Collider");
		mbcFrame.setBounds(100, 100, 450, 700);
		mbcFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mbcFrame.getContentPane().setLayout(null);
		mbcFrame.setBackground(color_1);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 661);
		mbcFrame.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));

		mainPanel = new JPanel();
		mainPanel.setBackground(color_1);
		panel.add(mainPanel, "name_432466542248400");
		mainPanel.setLayout(null);

		JLabel mcLabel = new JLabel("Bitcoin Micro Collider!");
		mcLabel.setBounds(0, 0, panel.getWidth(), 86);
		mainPanel.add(mcLabel);
		mcLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mcLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
		mcLabel.setForeground(color_5);
		mcLabel.setBackground(color_2);
		mcLabel.setBorder(BorderFactory.createEmptyBorder());
		mcLabel.setOpaque(true);

		loadFileButton = new JButton("Choose Address File");
		loadFileButton.setBounds(19, 455, 211, 33);
		loadFileButton.addActionListener(new FileBrowserListener());
		loadFileButton.setForeground(color_2);
		mainPanel.add(loadFileButton);
		loadFileButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		fileAddressField = new JTextField();
		fileAddressField.setBounds(19, 419, 396, 25);
		mainPanel.add(fileAddressField);
		fileAddressField.setColumns(10);

		JLabel threadsLabel = new JLabel("Collider Threads");
		threadsLabel.setBounds(10, 331, 414, 33);
		mainPanel.add(threadsLabel);
		threadsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		threadsLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		threadsLabel.setForeground(color_5);

		startButton = new JButton("Start Collider!");
		startButton.setBounds(10, 499, 300, 65);
		mainPanel.add(startButton);
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 32));
		startButton.setForeground(color_2);
		this.setStartButtonEnabled(false);
		this.setLoadFileButtonEnabled(true);

		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(320, 499, 104, 65);
		btnExit.setBackground(exitColor);
		btnExit.setForeground(color_2);
		mainPanel.add(btnExit);
		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JTextArea mcTextArea = new JTextArea();
		mcTextArea.setTabSize(0);
		mcTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
		mcTextArea.setForeground(color_5);
		mcTextArea.setBackground(color_3);
		mcTextArea.setEditable(false);
		mcTextArea.setWrapStyleWord(true);
		mcTextArea.setLineWrap(true);
		mcTextArea.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
		mcTextArea.setText(
				"The Bitcoin Micro Collider creates random bitcoin private/public key pairs and compares the addresses to a list of existing addresses.  In the event that an address is matched, the public/private keys are saved to a text file in the program directory.\r\n\r\n- Step 1: Load a text file of bitcoin addresses which you would like to check for matches (one address per line)\r\n- Step 2: Start the collider");
		mcTextArea.setBounds(0, 84, panel.getWidth(), 221);
		mainPanel.add(mcTextArea);

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
		
		statusTextPane = new JTextPane();
		statusTextPane.setForeground(Color.BLUE);
		statusTextPane.setBackground(Color.WHITE);
		statusTextPane.setContentType("text/html");
		statusTextPane.setEditable(false);
		//statusTextPane.setBounds(10, 585, 151, 65);
		statusTextPane.setAutoscrolls(true);
		mainPanel.add(statusTextPane);
		
		scrollPane = new JScrollPane(statusTextPane);
		scrollPane.setBounds(10, 570, 415, 80);
		scrollPane.setAutoscrolls(true);
		mainPanel.add(scrollPane);
		

		workingPanel = new JPanel();
		workingPanel.setBackground(color_1);
		panel.add(workingPanel, "name_432575102303500");
		workingPanel.setLayout(null);

		JLabel mcLabel_1 = new JLabel("Bitcoin Micro Collider!");
		mcLabel_1.setBounds(0, 0, panel.getWidth(), 51);
		mcLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 34));
		mcLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		mcLabel_1.setOpaque(true);
		mcLabel_1.setForeground(color_5);
		mcLabel_1.setBackground(color_2);
		mcLabel_1.setBorder(BorderFactory.createEmptyBorder());
		workingPanel.add(mcLabel_1);

		JLabel label_1 = new JLabel("Collider Attempts");
		label_1.setBounds(10, 65, 414, 51);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 32));
		label_1.setForeground(color_5);
		workingPanel.add(label_1);

		totalAttemptsLabel = new JLabel("0");
		totalAttemptsLabel.setBounds(10, 110, 414, 34);
		totalAttemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalAttemptsLabel.setFont(new Font("Tahoma", Font.PLAIN, 32));
		totalAttemptsLabel.setForeground(color_5);
		workingPanel.add(totalAttemptsLabel);

		JLabel label_3 = new JLabel("Time Elapsed");
		label_3.setBounds(152, 224, 130, 27);
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 22));
		label_3.setForeground(color_5);
		workingPanel.add(label_3);

		timeLabel = new JLabel("0");
		timeLabel.setBounds(128, 248, 177, 29);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		timeLabel.setForeground(color_5);
		workingPanel.add(timeLabel);

		pauseButton = new JButton("Pause Collider");
		pauseButton.setBounds(10, 601, 284, 50);
		pauseButton.setFont(new Font("Tahoma", Font.PLAIN, 32));
		pauseButton.addActionListener(new PauseListener());
		pauseButton.setBackground(pauseColor);
		workingPanel.add(pauseButton);

		JButton exitButton = new JButton("Exit");
		exitButton.setBounds(312, 603, 112, 50);
		exitButton.setFont(new Font("Tahoma", Font.PLAIN, 32));
		exitButton.setBackground(exitColor);
		exitButton.addActionListener(new ExitListener());
		workingPanel.add(exitButton);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(18, 164, 398, 34);		
		workingPanel.add(progressBar);

		
		JLabel lblAverageSpeedper = new JLabel("Attempts per second");
		lblAverageSpeedper.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageSpeedper.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblAverageSpeedper.setBounds(10, 282, 414, 27);
		lblAverageSpeedper.setForeground(color_5);
		workingPanel.add(lblAverageSpeedper);

		attemptsPerSecondLabel = new JLabel("0");
		attemptsPerSecondLabel.setHorizontalAlignment(SwingConstants.CENTER);
		attemptsPerSecondLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		attemptsPerSecondLabel.setBounds(128, 306, 177, 29);
		attemptsPerSecondLabel.setForeground(color_5);
		workingPanel.add(attemptsPerSecondLabel);

		JLabel lblAnyMatchesThis = new JLabel("Any matches this session?");
		lblAnyMatchesThis.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnyMatchesThis.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblAnyMatchesThis.setBounds(10, 445, 414, 27);
		lblAnyMatchesThis.setForeground(color_5);
		workingPanel.add(lblAnyMatchesThis);

		successLabel = new JLabel("NO");
		successLabel.setForeground(Color.RED);
		successLabel.setHorizontalAlignment(SwingConstants.CENTER);
		successLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
		successLabel.setBounds(119, 474, 195, 47);
		workingPanel.add(successLabel);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(18, 582, 398, 8);
		workingPanel.add(separator_1);

		totalAddressLabel = new JLabel("0");
		totalAddressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		totalAddressLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		totalAddressLabel.setBounds(138, 198, 122, 15);
		totalAddressLabel.setForeground(color_5);
		workingPanel.add(totalAddressLabel);

		JLabel addressTotal = new JLabel("Target Addresses:");
		addressTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		addressTotal.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addressTotal.setBounds(6, 198, 122, 15);
		addressTotal.setForeground(color_5);
		workingPanel.add(addressTotal);

		threadCountLabel = new JLabel("0");
		threadCountLabel.setHorizontalAlignment(SwingConstants.LEFT);
		threadCountLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		threadCountLabel.setBounds(360, 198, 57, 15);
		threadCountLabel.setForeground(color_5);
		workingPanel.add(threadCountLabel);

		JLabel lblThreadsActive = new JLabel("Threads Active:");
		lblThreadsActive.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreadsActive.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblThreadsActive.setBounds(259, 198, 95, 15);
		lblThreadsActive.setForeground(color_5);
		workingPanel.add(lblThreadsActive);

		JLabel matchedAddress = new JLabel("Matched Public Address:");
		matchedAddress.setHorizontalAlignment(SwingConstants.LEFT);
		matchedAddress.setBounds(10, 506, 137, 15);
		matchedAddress.setForeground(color_5);
		workingPanel.add(matchedAddress);

		JLabel matchedKey = new JLabel("Matched Private Key:");
		matchedKey.setHorizontalAlignment(SwingConstants.LEFT);
		matchedKey.setBounds(10, 544, 152, 15);
		matchedKey.setForeground(color_5);
		workingPanel.add(matchedKey);

		matchedAddressLabel = new JLabel("Address");
		matchedAddressLabel.setForeground(Color.BLACK);
		matchedAddressLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		matchedAddressLabel.setHorizontalAlignment(SwingConstants.LEFT);
		matchedAddressLabel.setBounds(10, 521, 414, 21);
		matchedAddressLabel.setText("none");
		matchedAddressLabel.setForeground(color_5);
		workingPanel.add(matchedAddressLabel);

		matchedKeyLabel = new JLabel("Address");
		matchedKeyLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		matchedKeyLabel.setHorizontalAlignment(SwingConstants.LEFT);
		matchedKeyLabel.setBounds(10, 557, 414, 21);
		matchedKeyLabel.setText("none");
		matchedKeyLabel.setForeground(color_5);
		workingPanel.add(matchedKeyLabel);
		
		JLabel addLabel_1 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[0] = addLabel_1;
		addLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_1.setForeground(Color.WHITE);
		addLabel_1.setBounds(10, 342, 209, 15);
		workingPanel.add(addLabel_1);
		
		JLabel addLabel_2 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[1] = addLabel_2;
		addLabel_2.setForeground(Color.WHITE);
		addLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_2.setBounds(10, 365, 209, 15);
		workingPanel.add(addLabel_2);
		
		
		JLabel addLabel_3 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[2] = addLabel_3;
		addLabel_3.setForeground(Color.WHITE);
		addLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_3.setBounds(10, 384, 209, 15);
		workingPanel.add(addLabel_3);
		
		JLabel addLabel_4 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[3] = addLabel_4;
		addLabel_4.setForeground(Color.WHITE);
		addLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_4.setBounds(10, 405, 209, 15);
		workingPanel.add(addLabel_4);
		
		JLabel addLabel_5 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[4] = addLabel_5;
		addLabel_5.setForeground(Color.WHITE);
		addLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_5.setBounds(10, 426, 209, 15);
		workingPanel.add(addLabel_5);
		
		JLabel addLabel_6 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[5] = addLabel_6;
		addLabel_6.setForeground(Color.WHITE);
		addLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_6.setBounds(225, 342, 209, 15);
		workingPanel.add(addLabel_6);
		
		JLabel addLabel_7 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[6] = addLabel_7;
		addLabel_7.setForeground(Color.WHITE);
		addLabel_7.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_7.setBounds(225, 365, 209, 15);
		workingPanel.add(addLabel_7);
		
		JLabel addLabel_8 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[7] = addLabel_8;
		addLabel_8.setForeground(Color.WHITE);
		addLabel_8.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_8.setBounds(225, 384, 209, 15);
		workingPanel.add(addLabel_8);
		
		JLabel addLabel_9 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[8] = addLabel_9;
		addLabel_9.setForeground(Color.WHITE);
		addLabel_9.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_9.setBounds(225, 405, 209, 15);
		workingPanel.add(addLabel_9);
		
		JLabel addLabel_10 = new JLabel("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
		addressLabelArray[9] = addLabel_10;
		addLabel_10.setForeground(Color.WHITE);
		addLabel_10.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addLabel_10.setBounds(225, 426, 209, 15);
		workingPanel.add(addLabel_10);
		
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

	public void setLoadFileButtonEnabled(Boolean thisBool) {
		FileStatus thisStatus = this.microCollider.getFileStatus();
		
		if (thisBool) {
			loadFileButton.setText("Choose Address File");
		} else {
			if (thisStatus == FileStatus.FILE_LOADING)
				loadFileButton.setText("Loading...");
			else if (thisStatus == FileStatus.FILE_NOT_LOADED)
				loadFileButton.setText("Choose Address File");
		}
		
		loadFileButton.setEnabled(thisBool);
	}
	
	public void setStartButtonEnabled(Boolean thisBool) {
		FileStatus thisStatus = this.microCollider.getFileStatus();
		
		if (thisBool) {
			startButton.setText("Start Collider");
			startButton.setBackground(this.acceptColor);
		} else {
			if (thisStatus == FileStatus.FILE_LOADING)
				startButton.setText("Loading...");
			else if (thisStatus == FileStatus.FILE_NOT_LOADED)
				startButton.setText("Waiting on file");
			startButton.setBackground(disabledColor);
		}
		
		startButton.setEnabled(thisBool);

	}
	
	public void setAddressFile(File thisFile) {
		
		this.microCollider.setFileStatus(FileStatus.FILE_LOADING);
		this.setStartButtonEnabled(false);
		this.setLoadFileButtonEnabled(false);
		fileAddressField.setText(thisFile.getPath());
		
		//Set the new address file
		LoadAddresses loader = new LoadAddresses(this.microCollider, this, thisFile);
		loader.start();
		//loader.run();
	}
	
	public void setThreadCount(int thisInt) {
		threadSpinner.setValue(Integer.valueOf(thisInt));
	}
	
	public void setTimeLabel(long duration, long addressesChecked) {

		long second = (duration / 1000) % 60;
		long minute = (duration / (1000 * 60)) % 60;
		long hour = (duration / (1000 * 60 * 60));

		String time = String.format("%04d:%02d:%02d", hour, minute, second);
		this.timeLabel.setText(String.format(time));

		// Set attempts per second
		duration /= 1000;
		if (duration <= 0)
			return;
		attemptsPerSecondLabel.setText(String.format("%,d", addressesChecked / duration));
	}
	
	public void showFileErrorMessage() {
		JOptionPane.showMessageDialog(mbcFrame, "File read/write failed.");
	}
	
	public void showAddressFormatMessage() {
		JOptionPane.showMessageDialog(mbcFrame, "One or more addresses are have incorrect formats");
	}
	
	public void setStatusTextPane(String thisString) {
		
		HTMLDocument doc = (HTMLDocument) statusTextPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)statusTextPane.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), thisString, 0, 0, null);
			statusTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
	}

	public class PauseListener implements ActionListener {

		Boolean isPaused = false;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (isPaused) {
				isPaused = false;
				pauseButton.setBackground(pauseColor);
				pauseButton.setText("Pause Collider");
				progressBar.setVisible(true);
				microCollider.resumeAllThreads();
			} else {
				isPaused = true;
				pauseButton.setBackground(acceptColor);
				pauseButton.setText("Resume Collider");
				progressBar.setVisible(false);
				microCollider.pauseAllThreads();
			}
		}
	}
	
	public void setAddressLabels(String[] thisStringArray) {
		//Set text for each of the address labels
		
		for (int i=0; i < this.addressLabelArray.length; i++) {
			this.addressLabelArray[i].setText(thisStringArray[i]);
		}
	}

	public class StartListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				int cpuThreads = (Integer) threadSpinner.getValue();
				startButton.setText("Loading...");
				microCollider.startCollider(cpuThreads);
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
			
			int returnVal = fileChooser.showOpenDialog(mbcFrame);

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
