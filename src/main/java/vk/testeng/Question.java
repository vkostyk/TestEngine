package vk.testeng;

import java.util.ArrayList;

public class Question
{
    public enum AnswerType {ONEOPTION, FEWOPTIONS, MATCHING, INPUT, ESSAY};
    private int id;
    private AnswerType type;
    private int maxPoints;
    private String task;
    private ArrayList<String> options;
    private AbstractAnswer correctAnswer;

    public Question()
    {

    }

    public Question(int id, AnswerType type, int maxPoints, String task, ArrayList<String> options, AbstractAnswer answer)
    {
        set(id, type, maxPoints, task, options, answer);
    }
    public void set(int id, AnswerType answerType, int maxPoints, String task, ArrayList<String> options, AbstractAnswer answer) throws RuntimeException
    {
        if (id<0) {throw new RuntimeException("id must a positive number");}
        if (maxPoints<0) {throw new RuntimeException("maxPoints should be a positive number");}
        if (task == null) {throw new RuntimeException("task does not reference anything (null value)");}
        if (task.length()<10) {throw new RuntimeException("task is too short");}
        if      (
                (answerType==AnswerType.MATCHING||answerType==AnswerType.FEWOPTIONS)
                        &&  (options.size()<3)
                )
        {
            throw new RuntimeException("too few elements of [ArrayList<String> options] as for desired type");
        }
        if      (
                !(answer instanceof OneOptionAnswer)
                        &&  !(answer instanceof FewOptionsAnswer)
                        &&  !(answer instanceof MatchingAnswer)
                        &&  !(answer instanceof InputAnswer)
                        &&  !(answer instanceof EssayAnswer)
                )
        {
            throw new RuntimeException("answer should be of one of predefined types");
        }
        if      (
                ((answerType==AnswerType.ONEOPTION)&&!(answer instanceof OneOptionAnswer))
                        ||  ((answerType==AnswerType.FEWOPTIONS)&&!(answer instanceof FewOptionsAnswer))
                        ||  ((answerType==AnswerType.MATCHING)&&!(answer instanceof MatchingAnswer))
                        ||  ((answerType==AnswerType.INPUT)&&!(answer instanceof InputAnswer))
                        ||  ((answerType==AnswerType.ESSAY)&&!(answer instanceof EssayAnswer))
                )
        {
            throw new RuntimeException("actual type of answer and answerType does not correspond");
        }

        if (options==null) {throw new RuntimeException("no options received");}
        for (String o : options)
        {
            if (o==null) {throw  new RuntimeException("options element does not reference anything");}
            if (o.length()==0) {throw  new RuntimeException("options element contains empty value");}
        }
        ArrayList<Integer> als;
        switch (type)
        {
            case ONEOPTION:
                if (((OneOptionAnswer)answer).getAnswer()<0) {throw new RuntimeException("wrong answer value");}
                break;
            case FEWOPTIONS:
                als = ((FewOptionsAnswer)answer).getAnswer();
                if (als==null) {throw new RuntimeException("answer parameter is empty");}
                for (Integer a : als)
                {
                    if (a<0) {throw new RuntimeException("wrong answer parameter");}
                }
                break;

            case MATCHING:
                als = ((MatchingAnswer)answer).getAnswer();
                if (als==null) {throw new RuntimeException("answer parameter is empty");}
                for (Integer a : als)
                {
                    if (a<0) {throw new RuntimeException("wrong answer parameter");}
                }
                break;
        }

        this.id = id;
        this.type = answerType;
        this.maxPoints = maxPoints;
        this.task = task;
        this.correctAnswer = answer;
        this.options = options;

    }

    public double checkAnswer(AbstractAnswer answer)
    {
        return correctAnswer.check(answer);
    }
    public AbstractAnswer getCorrectAnswer()
    {
        return this.correctAnswer;
    }
    public int getMaxPoints()
    {
        return maxPoints;
    }
    public int getId()
    {
        return this.id;
    }
    public AnswerType getType()
    {
        return type;
    }
    public String getTask()
    {
        return task;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    public void setType(AnswerType answerType)
    {
        this.type = answerType;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void setCorrectAnswer(AbstractAnswer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}