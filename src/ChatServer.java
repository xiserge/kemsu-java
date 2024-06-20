import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final ChatModel model = new ChatModel();
    private static final ServerConsoleView consoleView = new ServerConsoleView();
    private static ServerSocket serverSocket;
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void start() {
        consoleView.show();
        try {
            log("Сервер запустился...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, model);
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            log("Остановка: " + e.getMessage());
        }

        for (ClientHandler client : clients) {
            client.close();
        }

        log("Сервер остановился");
    }
    public static void setServerSocket(ServerSocket serverSocket) {
        ChatServer.serverSocket = serverSocket;
    }

    private static void log(String message) {
        consoleView.addMessage(message);
        System.out.println(message); // Also log to standard output for debug purposes
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final ChatModel model;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;

        public ClientHandler(Socket socket, ChatModel model) {
            this.socket = socket;
            this.model = model;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                nickname = in.readLine().substring(9);
                model.addClientWriter(nickname, out);
                log("Пользователь подключился: " + nickname);
                model.addMessage("Пользователь подключился: " + nickname);

                String message;
                while ((message = in.readLine()) != null) {
                    if (!message.startsWith("NICKNAME:")) {
                        model.addMessage(nickname + ": " + message);
                        log(nickname + " написал: " + message);
                    }
                }
            } catch (IOException e) {
                log("Ошибка: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Ошибка: " + e.getMessage());
                }
                model.removeClientWriter(nickname);
                log("Пользователь отключился: " + nickname);
            }
        }

        public void close() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
