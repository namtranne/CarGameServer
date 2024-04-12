package socket.object;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;
    private int answer;
    private String question;

    private Duration answerTime;

    public Duration getAnswerTime() {
        return answerTime;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Answer(int answer, String question, Duration duration) {
        this.answer = answer;
        this.question = question;
        this.answerTime = duration;
    }
}
