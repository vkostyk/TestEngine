package vk.testeng.model;

import vk.testeng.model.answer.AbstractAnswer;

import java.util.ArrayList;

public class Quiz {
    private Test test;
    private ArrayList<AbstractAnswer> answers;
    public Quiz() {
        test = new Test();
    }
    public ArrayList<AbstractAnswer> execute()
    {
        return new ArrayList<>();
    }
    public void parseAnswers()
    {
        double overall = 0;
        ArrayList<Question> questions = test.getQuestions();
        for (int i = 0; i<test.getQuestionsCount();i++)
        {

        }

    }
    public void setGrades()
    {

    }
}
