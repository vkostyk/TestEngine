package vk.testeng;

public class InputAnswer extends AbstractAnswer
{
    private String answer;
    public InputAnswer(String answer)
    {
        this.answer = answer;
    }
    public double check(AbstractAnswer obj)
    {
        //regexp
        return 1.0;
    }
    public String getAnswer()
    {
        return answer;
    }
}
