// ChatModel.java
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

public class ChatModel {
    private final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private final Map<String, PrintWriter> users = new ConcurrentHashMap<>();

    public synchronized void addMessage(String message) {
        notifyClients(message);
    }

    public void addClientWriter(String nickname, PrintWriter writer) {
        users.put(nickname, writer);
        clientWriters.add(writer);
        updateUsersList();
    }

    public void removeClientWriter(String nickname) {
        PrintWriter writer = users.remove(nickname);
        if (writer != null) {
            clientWriters.remove(writer);
            updateUsersList();
        }
    }

    private void notifyClients(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    private void updateUsersList() {
        StringBuilder userList = new StringBuilder("USERS:");
        for (String user : users.keySet()) {
            userList.append(user).append(" ");
        }
        notifyClients(userList.toString());
    }
}
