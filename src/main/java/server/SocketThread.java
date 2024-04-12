package server;

import socket.object.Answer;
import socket.object.Player;
import socket.object.Question;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Duration;

public class SocketThread extends Thread {
    public  ObjectOutputStream out;
    public ObjectInputStream in;
    public Socket socket;
    public Player player;

    public Duration answerTime;

    public boolean isAnswerCorrect;

    public SocketThread(ObjectOutputStream out, ObjectInputStream in, Socket socket, Player player) {
        this.out = out;
        this.in = in;
        this.socket = socket;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            //if player answer wrong 3 times, he/she will be disqualified
            if(player.isLosesTurn()) {
                return;
            }

            //send question to client
            String question = QuestionGenerator.getQuestion();
            out.writeObject(new Question(question));

            // Start the timer
            socket.setSoTimeout(25000);

            //Wait for the answer
            Answer answer = (Answer) in.readObject();

            // Check the answer
            isAnswerCorrect = answer.getAnswer() == QuestionGenerator.getAnswer();
            answerTime = answer.getAnswerTime();
        } catch(SocketTimeoutException e) {
            System.out.println("Answer timeout");
            isAnswerCorrect = false;
            answerTime = Duration.ofSeconds(26);
        }
        catch(SocketException | EOFException e) {
            System.out.println("User disconnected...");
            isAnswerCorrect = false;
            answerTime = Duration.ofSeconds(26);
        }
        catch (IOException | ClassNotFoundException e) {
            // Handle exceptions
            e.printStackTrace();
        }

    }
}
