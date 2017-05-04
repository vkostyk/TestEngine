package vk.testeng.model;

public class EssayAnswer extends AbstractAnswer
{
    private String answer;
    public EssayAnswer(String answer)
    {
        this.answer = answer;
    }

    public double check(AbstractAnswer obj) {
        EssayAnswer option = (EssayAnswer)obj;
        if (obj!=null) {return 0;} else {return -1;}
    }
    public String getAnswer()
    {
        return answer;
    }
}
