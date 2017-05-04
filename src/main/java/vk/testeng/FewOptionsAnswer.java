package vk.testeng;

import java.util.ArrayList;

public class FewOptionsAnswer extends AbstractAnswer
{
    private ArrayList<Integer> answer;
    public FewOptionsAnswer(int... answer)
    {
        setAnswer(answer);
    }
    public FewOptionsAnswer(ArrayList<Integer> answer)
    {
        setAnswer(answer);
    }
    public ArrayList<Integer> getAnswer()
    {
        return answer;
    }
    public double check(AbstractAnswer obj)
    {
        FewOptionsAnswer option = (FewOptionsAnswer)obj;
        int counter = 0;
        int size = answer.size();
        for (int i = 0;i<answer.size();i++)
        {
            if (option.getAnswer().contains(answer.get(i))) counter++;
        }
        return (double)counter/(double)size;
    }

    public void setAnswer(ArrayList<Integer> answer)
    {
        this.answer = answer;
    }
    public void setAnswer(int... answer) {
        this.answer = new ArrayList<Integer>();
        for (int i : answer)
        {
            this.answer.add(i);
        }
    }
}