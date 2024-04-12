package socket.object;

import server.SocketThread;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Game  implements Serializable {

    private int GOAL_SCORE;

    private List<Player> playerLIst = new LinkedList<>();

    private Player victoryPlayer;

    private static final long serialVersionUID = 1L;

    public List<Player> getPlayerLIst() {
        return playerLIst;
    }
    public void setPlayerLIst(List<Player> playerLIst) {
        this.playerLIst = playerLIst;
    }

    public int getGOAL_SCORE() {
        return GOAL_SCORE;
    }

    public void setGOAL_SCORE(int GOAL_SCORE) {
        this.GOAL_SCORE = GOAL_SCORE;
    }

    public Player getVictoryPlayer() {
        return victoryPlayer;
    }

    public void setVictoryPlayer(Player victoryPlayer) {
        this.victoryPlayer = victoryPlayer;
    }

    public void updateGame(List<SocketThread> threads, Map<Player, Integer> penalty) {
        //filter the players that are disqualified from the race
        List<SocketThread> filterThreads = threads.stream()
                .filter(thread -> !thread.player.isLosesTurn()).toList();
        List<SocketThread> correctPlayerList = new LinkedList<>();

        //update players' penalty and count number of wrong answer
        int wrongAnswerCount = updatePlayerPenalty(filterThreads, correctPlayerList, penalty);

        //sort the player that answer correctly based on time
        //player at the first position will receive more point
        correctPlayerList.sort(Comparator.comparing(a -> a.answerTime));

        //Reward point to players
        //First player get more points than other players
        updatePlayerPoints(correctPlayerList, wrongAnswerCount);

        //Check if there is a winner
        updateVictoryPlayer();
    }

    private void updateVictoryPlayer() {
        int point = GOAL_SCORE;
        for(Player player : playerLIst) {
            if(player.getPoint() >= point) {
                point = player.getPoint();
                victoryPlayer = player;
            }
        }
    }

    private void updatePlayerPoints(List<SocketThread> correctPlayerList, int wrongAnswerCount) {
        for(int i = 0; i < correctPlayerList.size() ;i++) {
            Player player = correctPlayerList.get(i).player;
            int currentPoint = player.getPoint();
            if(i==0) {
                int rewardPoint = Math.max(2, wrongAnswerCount);
                player.setPoint(currentPoint + rewardPoint);
            }
            else {
                player.setPoint(currentPoint+1);
            }
        }
    }

    private int updatePlayerPenalty(List<SocketThread> filterThreads, List<SocketThread> correctPlayerList, Map<Player, Integer> penalty) {
        int wrongAnswerCount = 0;
        for(SocketThread thread : filterThreads) {
            Player player = thread.player;
            if(!thread.isAnswerCorrect) {
                wrongAnswerCount++;
                int currentPoint = player.getPoint();
                player.setPoint(Math.max(currentPoint - 1, 0));
                int penaltyCount = penalty.get(player) + 1;
                if(penaltyCount == 3) player.setIsLosesTurn(true);
                penalty.put(player, penaltyCount);
            }
            else {
                penalty.put(player, 0);
                correctPlayerList.add(thread);
            }
        }
        return wrongAnswerCount;
    }

    public Game(int GOAL_SCORE, List<Player> playerLIst) {
        this.GOAL_SCORE = GOAL_SCORE;
        this.playerLIst = playerLIst;
    }

    public Game(Game game) {
        this.GOAL_SCORE = game.GOAL_SCORE;
        for(Player player : game.playerLIst) {
            Player clonePlayer = new Player(player);
            this.playerLIst.add(clonePlayer);
            if(player == game.victoryPlayer) {
                this.victoryPlayer = clonePlayer;
            }
        }
    }
}
