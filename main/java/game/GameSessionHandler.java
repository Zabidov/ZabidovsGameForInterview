package game;

import enums.ChooseType;
import lombok.extern.slf4j.Slf4j;
import player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

@Slf4j
public class GameSessionHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final GameSession gameSession;
    private final List<Player> playerList;
    private final Object lock;

    public GameSessionHandler(Socket socket, List<Player> playerList, Object lock) {
        this.playerList = playerList;
        this.clientSocket = socket;
        this.lock = lock;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.gameSession = new GameSession(out, lock);
        } catch (IOException e) {
            log.error("Error creating input-output streams", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            // Получение имени и выбора игрока
            String playerName = getPlayerName();
            while (true) {
                ChooseType chooseType = getPlayerChoice();
                Player player = new Player(playerName);
                player.setChoice(chooseType);
                log.info("Player {}: chose {}", playerName, chooseType);
                out.println("You chose " + chooseType + "!");
                // Добавление игрока в список
                synchronized (lock) {
                    playerList.add(player);
                    lock.notifyAll();
                }
                // Начало игры
                gameSession.startGame(playerList);
                // Если ничья, разрешаем игрокам выбирать снова
                if (Objects.isNull(player.getChoice())) {
                    continue;
                }
                break;
            }
        } catch (IOException e) {
            log.error("Error handling game session", e);
        } finally {
            closeSocket();
        }
    }

    private String getPlayerName() throws IOException {
        out.println("Hello! Write your name:");
        return in.readLine();
    }

    private ChooseType getPlayerChoice() throws IOException {
        while (true) {
            out.println("Make your choice (rock, paper, scissors):");
            try {
                return ChooseType.find(in.readLine());
            } catch (IllegalArgumentException e) {
                out.println("You have to choose one of the following types:");
            }
        }
    }

    private void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.error("Error closing socket", e);
        }
    }
}
