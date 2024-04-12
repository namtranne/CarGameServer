package server;

import java.util.Random;

public class QuestionGenerator {
    private static String question;

    private  static int answer;

    public static int getAnswer() {
        return answer;
    }

    public static String getQuestion() {
        return question;
    }

    public static void generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(20001) - 10000; // Random number between -10000 and 10000
        int num2 = random.nextInt(20001) - 10000; // Random number between -10000 and 10000
        char operator = "+-*/%".charAt(random.nextInt(5)); // Random operator
        answer = calculateAnswer(num1, num2, operator);
        question = num1 + " " + operator + " " + num2;
    }

    public static int calculateAnswer(int num1, int num2, int operator) {
        return switch (operator) {
            case '+' -> num1 + num2;
            case '-' -> num1 - num2;
            case '*' -> num1 * num2;
            case '/' -> num1 / num2;
            case '%' -> num1 % num2;
            default -> 0;
        };
    }
}
