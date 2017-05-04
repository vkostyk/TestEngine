package vk.testeng.model;

public class OneOptionAnswer extends AbstractAnswer
{
    private int answer;
    public OneOptionAnswer()
    {
        answer = -1;
    }
    public OneOptionAnswer(int answer)
    {
        this.answer = answer;
    }
    public int getAnswer()
    {
        return answer;
    }
    public double check(AbstractAnswer obj)
    {
        OneOptionAnswer option = (OneOptionAnswer)obj;
        if (option.getAnswer()==this.answer) {return (double)1;}else{return (double)0;}
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}