package vk.testeng.model.answer;

import java.util.ArrayList;

public class MatchingAnswer extends AbstractAnswer
{
    private ArrayList<Integer> answer;
    public MatchingAnswer(ArrayList<Integer> answer)
    {
        this.answer = answer;
    }
    public double check(AbstractAnswer obj)
    {
        MatchingAnswer option = (MatchingAnswer)obj;
        int counter = 0;
        int size = answer.size();
        for (int i = 0; i<size; i++)
        {
            if (answer.get(i)==option.getAnswer().get(i)) counter++;
        }
        return (double)counter/(double)size;
    }
    public ArrayList<Integer> getAnswer()
    {
        return answer;
    }
    public void setAnswer(ArrayList<Integer> answer) {
        this.answer = answer;
    }
}