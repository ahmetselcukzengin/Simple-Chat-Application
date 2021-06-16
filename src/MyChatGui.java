import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MyChatGui extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton beginSessionButton;
	private JButton endSessionButton;
	private JTextArea chatArea;
    private JTextField outgoingMessageField;
    private JTextField userNameField;
    private JTextField recipientUserField;
    private MyChatClient chatClient;
	public MyChatGui(MyChatClient mychatclient) {
		chatClient=mychatclient;
		initComponents();
	}

	private void initComponents() {
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // the panel is not visible in output
        connectPanel.add(new JLabel("Kullanici adiniz:"));
        userNameField = new JTextField(10);
        connectPanel.add(userNameField);
    	beginSessionButton = new JButton("Oturum ac");
        beginSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beginSessionButtonClicked(evt);
            }
        });
        connectPanel.add(beginSessionButton);
    	endSessionButton = new JButton("Oturumu kapat");
    	endSessionButton.setEnabled(false);
        endSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endSessionButtonClicked(evt);
            }
        });
        connectPanel.add(endSessionButton);

        // Ekranin orta bolumunda gelen mesaj alanini olustur
		chatArea = new JTextArea(15, 32);
        chatArea.setEditable(false);
        chatArea.setBackground(Color.LIGHT_GRAY);

        // Ekranin alt bolumunde mesaj gonderme panelini olustur. Bilesenler "Flow Layout"a gore yerlestiriliyor.
        JPanel sendPanel = new JPanel(); // the panel is not visible in output
        sendPanel.add(new JLabel("Alici:"));
		recipientUserField = new JTextField(10);
        sendPanel.add(recipientUserField);
        sendPanel.add(new JLabel("Mesaj:"));
		outgoingMessageField = new JTextField(32);
        sendPanel.add(outgoingMessageField);
        JButton sendButton = new JButton("Gonder");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonClicked(evt);
            }
        });
        sendPanel.add(sendButton);
        
        Container content = getContentPane();
        content.add(connectPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        content.add(sendPanel, BorderLayout.SOUTH);

        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        pack();
        outgoingMessageField.requestFocusInWindow();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void beginSessionButtonClicked(ActionEvent e) {
        beginSessionButton.setEnabled(false);
        endSessionButton.setEnabled(true);
        
        String userName = userNameField.getText();
        chatClient.comm.send("BEGIN_SESSION"+" "+ userName);
    }
    
    private void sendButtonClicked(ActionEvent e) {
    	String recipientUser = recipientUserField.getText();
    	String message = outgoingMessageField.getText();
    	chatArea.append(recipientUser + " alicisina gonderilen: " + message + "\n");
        outgoingMessageField.setText("");
        outgoingMessageField.requestFocusInWindow();
        chatClient.comm.send("SEND_SMS"+" "+ recipientUser+" "+message);
    }
    
    private void endSessionButtonClicked(ActionEvent e) {
        beginSessionButton.setEnabled(true);
        endSessionButton.setEnabled(false);
        chatClient.comm.send("END_SESSION");	
    }
    
	public void displayIncomingMessage(String message) {
		chatArea.append(message + "\n");
	}

}
