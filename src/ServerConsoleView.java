import javax.swing.*;
import java.awt.*;

public class ServerConsoleView {
    private final JFrame frame = new JFrame("Консоль сервера");
    private final JTextArea consoleArea = new JTextArea(20, 50);

    public ServerConsoleView() {
        consoleArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(consoleArea), BorderLayout.CENTER);
        frame.pack();
    }

    public void show() {
        frame.setVisible(true);
    }

    public void addMessage(String message) {
        consoleArea.append(message + "\n");
    }
}
