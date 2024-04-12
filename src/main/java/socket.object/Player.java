package socket.object;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private int point;

    private String name;
    public Player(String name, int point) {
        this.name = name;
        this.point = point;
    }

    public Player(Player player) {
        this.name = player.name;
        this.point = player.point;
        this.isLosesTurn = player.isLosesTurn;
    }

    private  boolean isLosesTurn = false;

    public boolean isLosesTurn() {
        return isLosesTurn;
    }

    public void setIsLosesTurn(boolean isLosesTurn) {
        this.isLosesTurn = isLosesTurn;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }


}
