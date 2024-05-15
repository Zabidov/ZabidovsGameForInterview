package game;

import lombok.extern.slf4j.Slf4j;
import player.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class GameServer {
    private static final int PORT = 8888;
    private static final int MAX_THREADS = 10;
    private static final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
    private static final List<Player> playerList = new ArrayList<>();
    private static final Object lock = new Object();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Server started on port {}", PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected");
                Runnable gameSessionHandler = new GameSessionHandler(clientSocket, playerList, lock);
                pool.execute(gameSessionHandler);
            }
        } catch (IOException e) {
            log.error("Error starting the server", e);
        }
    }
}
