import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;
import javax.swing.*;

public class ChatController {
    private final ChatView view;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Thread clientThread;
    private ServerRunner serverRunner;

    public ChatController(ChatView view) {
        this.view = view;

        view.addConnectButtonListener(e -> {
            try {
                connectToServer(view.getServerAddress(), view.getServerPort(), view::toggleClientConnected);
            } catch (IOException err) {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться: " + err.getMessage());
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(null, "Порт должен быть числом");
            }
        });

        view.addDisconnectButtonListener(e -> disconnectFromServer());

        view.addStartServerButtonListener(e -> {
            try {
                startServer();
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(null, "Порт должен быть числом");
            }
        });

        view.addStopServerButtonListener(e -> stopServer());

        view.show();
    }

    private void connectToServer(String serverAddress, int serverPort, Consumer<Boolean> connectedConsumer) throws IOException {
        String nickname = JOptionPane.showInputDialog(
                "Введи свой ник:",
                "Пользователь"
        );

        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Отправляем никнейм на сервер
        out.println("NICKNAME:" + nickname);

        ActionListener listener = view.addTextFieldListener(e -> {
            String message = view.getTextFieldText();
            out.println(message);
        });

        clientThread = new Thread(() -> {
            System.out.println("Подключились к серверу");
            connectedConsumer.accept(true);
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("USERS:")) {
                        view.updateUserList(message.substring(6).replace(" ", "\n"));
                    } else {
                        view.addMessage(message);
                    }
                }
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedConsumer.accept(false);
            System.out.println("Отключились от сервера");

            view.removeTextFieldListener(listener);
        });
        clientThread.start();

        connectedConsumer.accept(true);
    }

    private void disconnectFromServer() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (clientThread != null) {
                clientThread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServerRunner implements Runnable {
        private ServerSocket serverSocket;
        private final Integer port;

        private final Consumer<Boolean> startedConsumer;

        private ServerRunner(Integer port, Consumer<Boolean> startedConsumer) {
            this.port = port;
            this.startedConsumer = startedConsumer;
        }

        public void run() {
            startedConsumer.accept(true);
            try {
                serverSocket = new ServerSocket(port);
                ChatServer.setServerSocket(serverSocket);
                ChatServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startedConsumer.accept(false);
        }

        public void stopServer() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer() {
        serverRunner = new ServerRunner(view.getServerPort(), view::toggleServerStarted);
        new Thread(serverRunner).start();
    }

    private void stopServer() {
        if (serverRunner != null) {
            serverRunner.stopServer();
        }
    }

    public static void main(String[] args) {
        ChatView view = new ChatView();
        new ChatController(view);
    }
}
