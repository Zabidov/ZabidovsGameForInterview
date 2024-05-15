package game;

import enums.ChooseType;
import lombok.extern.slf4j.Slf4j;
import player.Player;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static enums.ChooseType.*;

@Slf4j
public class GameSession {
    private final PrintWriter out;
    private final Object lock;

    public GameSession(PrintWriter out, Object lock) {
        this.out = out;
        this.lock = lock;
    }

    public void startGame(List<Player> playerList) {
        synchronized (lock) {
            checkPlayersSize(playerList);
            lock.notifyAll();
        }
        Player player1 = playerList.get(0);
        Player player2 = playerList.get(1);
        if (Objects.nonNull(player1.getChoice()) && Objects.nonNull(player2.getChoice())) {
            determineWinner(player1, player2);
            playerList.remove(player1);
            playerList.remove(player2);
            log.info("Players {} and {} removed", player1.getName(), player2.getName());
            log.info("List size is {}", playerList.size());
        }
    }

    private void checkPlayersSize(List<Player> playerList) {
        synchronized (lock) {
            while (playerList.size() < 2) {
                try {
                    out.println("Please wait while a new player connecting to the game");
                    lock.wait();
                } catch (InterruptedException e) {
                    out.println("Interrupted while waiting for player to connect");
                }
            }
        }
    }

    private void determineWinner(Player player1, Player player2) {
        ChooseType choice1 = player1.getChoice();
        ChooseType choice2 = player2.getChoice();
        if (Objects.equals(choice1, choice2)) {
            out.println("Draw! Both players chose " + choice1);
            // Если ничья, разрешаем игрокам выбирать снова
            player1.setChoice(null);
            player2.setChoice(null);
            out.println("Lets play again");
        } else if ((choice1.equals(ROCK) && choice2.equals(SCISSORS))
                || (choice1.equals(PAPER) && choice2.equals(ROCK))
                || (choice1.equals(SCISSORS) && choice2.equals(PAPER))) {
            printResult(player2, choice2);
            printResult(player1, choice1);
            printWinner(player1);
        } else {
            printResult(player1, choice1);
            printResult(player2, choice2);
            printWinner(player2);
        }
    }

    private void printResult(Player player, ChooseType choice) {
        out.println("Player " + player.getName() + " chose " + choice);
    }

    private void printWinner(Player player) {
        out.println("Player " + player.getName() + " wins!");
    }
}
