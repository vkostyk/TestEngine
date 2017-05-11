package vk.testeng.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vk.testeng.service.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Test {
    private int id;
    private ArrayList<Question> questions;
    private int maxPoints;
    public Test()
    {
        this(0);
    }
    public Test(int id)
    {
        this.id = id;
        maxPoints = 0;
        questions = new ArrayList<>();
    }
    public void setId(int testId)
    {
        this.id = testId;
    }
    public int getId()
    {
        return id;
    }
    void create(ArrayList<Question> questions)
    {
        this.questions = questions;
    }

    public Test getFromDB(int id)
    {
        this.id = id;
        return getFromDB();
    }

    public Test getFromDB()
    {
        TestDB testDB = new TestDB();
        Test tmp = testDB.getTest(id);
        this.questions = tmp.getQuestions();
        return tmp;

    }

    public void addToDB()
    {
        TestDB testDB = new TestDB();
        testDB.addTest(this);
    }

    public Test load(int id)
    {

        this.id = id;
        return load();
    }
    public Test load()
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Test.class, new TestDeserializer())
                .registerTypeAdapter(Question.class, new QuestionDeserializer())
                .create();

        Path file = Paths.get("./file"+ id +".json");
        String json = "";
        String line = null;
        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            while ((line = reader.readLine()) != null) {json+=line;}
        } catch (IOException x) {
            System.err.println(x);
        }
        Test test = gson.fromJson(json, Test.class);
        this.questions = test.getQuestions();
        return test;
    }
    public void save(int id)
    {
        this.id = id;
        save();
    }
    public void save()
    {


        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Question.class, new QuestionSerializer())
                .registerTypeAdapter(Test.class, new TestSerializer())
                .create();

        String json = gson.toJson(this);

        byte data[] = json.getBytes();
        Path p = Paths.get("./file"+ id +".json");

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public int getQuestionsCount() {return questions.size();}
    public String getTask(int questionId)
    {
        return questions.get(questionId).getTask();
    }
    public double getQuestionMaxPoints(int questionId)
    {
        return questions.get(questionId).getMaxPoints();
    }
    public Question.AnswerType getQuestionType(int id)
    {
        return questions.get(id).getType();
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public Question getQuestion(int index)
    {
        return questions.get(index);
    }
    public ArrayList<String> getOptions(int id)
    {
        return questions.get(id).getOptions();
    }
    public int getMaxPoints()
    {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints)
    {
        this.maxPoints  = maxPoints;
    }

    public void setQuestion(int id, Question question)
    {
        maxPoints -=questions.get(id).getMaxPoints();
        maxPoints +=question.getMaxPoints();
        this.questions.set(id, question);
    }
    public void addQuestion(Question question)
    {
        maxPoints +=question.getMaxPoints();
        this.questions.add(question);
    }
    //public void setOptions(ArrayList<String> options) {this.}

}
