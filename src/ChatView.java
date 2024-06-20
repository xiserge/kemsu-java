import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatView {
    private final JFrame frame = new JFrame("Чат");
    private final JTextField textField = new JTextField(40);
    private final JTextArea messageArea = new JTextArea(8, 40);
    private final JTextArea userArea = new JTextArea(8, 15);
    private final JTextField serverAddressField = new JTextField("127.0.0.1", 15);
    private final JTextField serverPortField = new JTextField("12345", 5);
    private final JButton connectButton = new JButton("Подключиться");
    private final JButton disconnectButton = new JButton("Отключиться");
    private final JButton startServerButton = new JButton("Запуск сервера");
    private final JButton stopServerButton = new JButton("Остановка сервера");

    public ChatView() {
        messageArea.setEditable(false);
        userArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Сервер:"));
        panel.add(serverAddressField);
        panel.add(new JLabel("Порт:"));
        panel.add(serverPortField);
        panel.add(connectButton);
        panel.add(disconnectButton);
        panel.add(startServerButton);
        panel.add(stopServerButton);

        toggleClientConnected(false);
        toggleServerStarted(false);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.getContentPane().add(new JScrollPane(userArea), BorderLayout.EAST);
        frame.pack();
    }

    public void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void addMessage(String message) {
        messageArea.append(message + "\n");
    }

    public ActionListener addTextFieldListener(ActionListener listener) {
        textField.addActionListener(listener);
        return listener;
    }
    public void removeTextFieldListener(ActionListener listener) {
        textField.removeActionListener(listener);
    }
    public String getTextFieldText() {
        String text = textField.getText();
        textField.setText("");
        return text;
    }

    public void updateUserList(String users) {
        userArea.setText(users);
    }

    public String getServerAddress() {
        return serverAddressField.getText();
    }

    public int getServerPort() {
        return Integer.parseInt(serverPortField.getText());
    }

    public void toggleClientConnected(Boolean connected) {
        connectButton.setVisible(!connected);
        disconnectButton.setVisible(connected);
        messageArea.setVisible(connected);
        userArea.setVisible(connected);
        textField.setVisible(connected);

        if(!connected) {
            messageArea.setText("");
        }
    }

    public void toggleServerStarted(Boolean started) {
        startServerButton.setVisible(!started);
        stopServerButton.setVisible(started);
    }

    public void addConnectButtonListener(ActionListener listener) {
        connectButton.addActionListener(listener);
    }

    public void addDisconnectButtonListener(ActionListener listener) {
        disconnectButton.addActionListener(listener);
    }

    public void addStartServerButtonListener(ActionListener listener) {
        startServerButton.addActionListener(listener);
    }

    public void addStopServerButtonListener(ActionListener listener) {
        stopServerButton.addActionListener(listener);
    }
}
