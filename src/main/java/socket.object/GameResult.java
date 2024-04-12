package socket.object;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Player> playerRanking = new LinkedList<>();

    public GameResult(List<Player> playerList) {
        List<Player> winPlayers = playerList.stream().filter(player -> !player.isLosesTurn()).collect(Collectors.toList());
        List<Player> losePlayers = playerList.stream().filter(Player::isLosesTurn).collect(Collectors.toList());
        winPlayers.sort((a,b) ->  b.getPoint() - a.getPoint());
        losePlayers.sort((a,b) -> b.getPoint() - a.getPoint());
        for(Player player : winPlayers) {
            playerRanking.add(new Player(player));
        }
        for(Player player : losePlayers) {
            playerRanking.add(new Player(player));
        }
    }

    public List<Player> getPlayerRanking() {
        return playerRanking;
    }
}
