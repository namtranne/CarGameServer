package socket.object;

import java.io.Serializable;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String questionText;

    public Question(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

}