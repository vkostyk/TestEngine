package vk.testeng.model.answer;

public class InputAnswer extends AbstractAnswer
{
    private String answer;
    public InputAnswer(String answer)
    {
        this.answer = answer;
    }
    public void setAnswer(String answer) {this.answer = answer.trim();}
    public double check(AbstractAnswer obj)
    {

        InputAnswer option = (InputAnswer)obj;
        if (option.getAnswer().trim().toLowerCase().equals(this.answer.toLowerCase())) {return 1.0;} else {return 0;}
    }
    public String getAnswer()
    {
        return answer;
    }
}
