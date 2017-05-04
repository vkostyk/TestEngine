package vk.testeng.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TestDB {
    private Connection connect()
    {
        Connection c = null;
        try {
            Path file = Paths.get("/DB.cre");
            String line = null;
            ArrayList<String> data = new ArrayList<String>();
            try (InputStream in = Files.newInputStream(file);
                 BufferedReader reader =
                         new BufferedReader(new InputStreamReader(in))) {
                while ((line = reader.readLine()) != null) {
                    data.add(line);
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://"+data.get(0)+"/"+data.get(1),
                            data.get(2), data.get(3));

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }

    private void getOneOption(int questionId, Connection c, Question question)
    {
        Statement stmt = null;
        //OneOptionAnswer oneOptionAnswer = new  OneOptionAnswer();
        try {
            stmt = c.createStatement();
            ArrayList<String> options = new ArrayList<>();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE question_id="+questionId+" ORDER BY id;");
            int counter = 0;
            while (queryResultSet.next())
            {
                if (queryResultSet.getBoolean("is_correct")) question.setCorrectAnswer(new OneOptionAnswer(counter));
                options.add(queryResultSet.getString("option"));
                counter++;
            }
            question.setOptions(options);
        } catch (Exception e)
        {

        } finally
        {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private void getFewOptions(int questionId, Connection c, Question question)
    {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ArrayList<String> options = new ArrayList<>();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE question_id="+questionId+" ORDER BY id;");
            ArrayList<Integer> answers = new ArrayList<>();
            int counter = 0;
            while(queryResultSet.next())
            {
                if (queryResultSet.getBoolean("is_correct")) answers.add(counter);
                options.add(queryResultSet.getString("option"));
                counter++;
            }
            question.setCorrectAnswer(new FewOptionsAnswer(answers));
            question.setOptions(options);
        } catch (Exception e) {

        } finally  {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private void getMatching(int questionId, Connection c, Question question)
    {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM matching WHERE question_id="+questionId+" ORDER BY id;");
            ArrayList<String> options = new ArrayList<>();
            ArrayList<Integer> optionId = new ArrayList<>();
            ArrayList<Integer> optionPair = new ArrayList<>();
            options = new ArrayList<>();
            while (queryResultSet.next())
            {
                options.add(queryResultSet.getString("option"));
                optionId.add(queryResultSet.getInt("id"));
                optionPair.add(queryResultSet.getInt("pair_id"));
            }
            ArrayList<String> rightOptions = new ArrayList<>();

            int size = optionId.size();
            for (int i = 0; i < size; i++) {
                if (optionId.get(i) == null) {
                    continue;
                }
                for (int j = i + 1; j < size; j++) {
                    if (optionPair.get(j) == optionId.get(i)) {
                        rightOptions.add(options.get(j));
                        options.set(j, null);
                        break;
                    }
                }
            }
            size = options.size();
            ArrayList<String> leftOptions = new ArrayList<>();
            ArrayList<Integer> keys = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                if (options.get(i) != null) {
                    leftOptions.add(options.get(i));
                }
            }
            for (int i = 0; i < leftOptions.size(); i++) {
                keys.add(i);
            }

            long seed = System.nanoTime();
            Collections.shuffle(leftOptions, new Random(seed));
            Collections.shuffle(keys, new Random(seed));
            //leftOptions -> key+8 ->rightOptions;
            for (int i = 0; i<rightOptions.size(); i++)
            {
                leftOptions.add(rightOptions.get(i));
            }
            options = leftOptions;
            question.setOptions(options);
            question.setCorrectAnswer(new MatchingAnswer(keys));
        } catch (Exception e) {

        } finally {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private void getInput(int questionId, Connection c, Question question)
    {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE qid="+questionId+";");
            queryResultSet.next();
            question.setCorrectAnswer(new InputAnswer(queryResultSet.getString("option")));
        } catch (Exception e) {

        } finally {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }

    private void setOneOption(int questionId, Connection c, Question question)
    {
        int correctAnswer = ((OneOptionAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        PreparedStatement ps = null;
        try {
            for (int j = 0; j<options.size();j++)
            {
                ps = c.prepareStatement("INSERT INTO options(question_id, option, is_correct) VALUES(?,?,?)");
                ps.setInt(1,questionId);
                ps.setString(2, options.get(j));
                ps.setBoolean(3,(j==correctAnswer));
                ps.executeUpdate();
            }
        } catch (Exception e) {

        } finally {
            if (ps != null) {
                try {ps.close();}
                catch (Exception e) {}
            }
        }
    }
    private void setFewOptions(int questionId, Connection c, Question question)
    {
        ArrayList<Integer> fewCorrectAnswers =  ((FewOptionsAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        PreparedStatement ps = null;
        try {
            for (int j = 0; j<options.size(); j++)
            {
                ps = c.prepareStatement("INSERT INTO options(question_id, option, is_correct) VALUES(?,?,?)");
                ps.setInt(1,questionId);
                ps.setString(2, options.get(j));
                ps.setBoolean(3,fewCorrectAnswers.contains(j));
                ps.executeUpdate();
            }
        } catch (Exception e) {

        } finally {
            if (ps != null) {
                try {ps.close();}
                catch (Exception e) {}
            }
        }
    }

    private void setMatching(int questionId, Connection c, Question question)
    {
        PreparedStatement ps = null;
        ArrayList<Integer> matchingCorrectAnswer = ((MatchingAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        try {
            for (int j = 0; j<options.size()/2;j++)
            {
                ps = c.prepareStatement("INSERT INTO matching(question_id, option, pair_id) VALUES(?,?,?) RETURNING id;");
                ps.setInt(1, questionId);
                ps.setString(2, options.get(j));
                ps.setInt(3, -1);//matchingCorrectAnswer.get(j)
                ResultSet rs = ps.executeQuery();
                rs.next();
                int leftId = rs.getInt("id");

                ps = c.prepareStatement("INSERT INTO matching(question_id, option, pair_id) VALUES(?,?,?) RETURNING id;");
                ps.setInt(1, questionId);
                ps.setString(2, options.get(matchingCorrectAnswer.get(j)+options.size()/2));
                ps.setInt(3, leftId);
                rs = ps.executeQuery();
                rs.next();
                int rightId = rs.getInt("id");

                ps = c.prepareStatement("UPDATE matching SET pair_id=? WHERE question_id=? AND id=?;");
                ps.setInt(1,rightId);
                ps.setInt(2,questionId);
                ps.setInt(3,leftId);
                ps.executeUpdate();
            }
        } catch (Exception e) {

        } finally {
            if (ps != null) {
                try {ps.close();}
                catch (Exception e) {}
            }
        }
    }
    private void setInput(int questionId, Connection c, Question question)
    {
        PreparedStatement ps = null;
        String answer = ((InputAnswer)question.getCorrectAnswer()).getAnswer();
        try {
            ps = c.prepareStatement("INSERT INTO options(question_id, option, is_correct) VALUES(?,?,?)");
            ps.setInt(1,questionId);
            ps.setString(2,answer);
            ps.setBoolean(3,true);
            ps.executeUpdate();
        } catch (Exception e) {

        } finally {
            if (ps != null) {
                try {ps.close();}
                catch (Exception e) {}
            }
        }
    }

    public Test getTest(int id)
    {
        Test test = new Test();
        Connection c = null;
        Statement stmt = null;
        try {
            c = connect();
            //c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM tests WHERE id="+id+";");
            rs.next();
            test.setMaxPoints(rs.getInt("points"));
            rs = stmt.executeQuery("SELECT * FROM questions WHERE test_id="+id+" ORDER BY id;");

            while (rs.next())
            {
                Question question = new Question();
                int questionId = rs.getInt("id");
                question.setId(questionId);
                Question.AnswerType type = Question.AnswerType.valueOf(rs.getString("type"));
                question.setType(type);
                question.setTask(rs.getString("task"));
                question.setMaxPoints(rs.getInt("maxpoints"));
                ResultSet queryResultSet = null;
                ArrayList<String> options = null;
                ArrayList<Integer> answers;
                int counter;

                switch(type)
                {
                    case ONEOPTION:
                        getOneOption(questionId, c, question);
                        break;
                    case FEWOPTIONS:
                        getFewOptions(questionId, c, question);
                        break;
                    case MATCHING:
                        getMatching(questionId, c, question);
                        break;
                    case INPUT:
                        getInput(questionId, c, question);
                        break;
                    case ESSAY:
                        //nothing to load

                        break;
                }
                try {queryResultSet.close();} catch (Exception x) {System.err.println("Something gone wrong: perhaps no answer from DB;");}
                question.setOptions(options);
                test.addQuestion(question);

            }

            rs.close();
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return test;
    }
    public void addTest(Test test)
    {
        Connection c = null;
        Statement stmt = null;
        try {
            //c.setAutoCommit(false);
            c = connect();
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery("INSERT INTO tests(points) VALUES("+ test.getMaxPoints()+") RETURNING id;");
            rs.next();
            int testId = rs.getInt("id");
            for (int i = 0; i<test.getQuestionsCount(); i++)
            {
                Question question = test.getQuestion(i);
                Question.AnswerType questionType = question.getType();

                rs = stmt.executeQuery("INSERT INTO questions(test_id, task, type, max_points) VALUES("+testId+", "+question.getTask()+", "+questionType.name()+", "+question.getMaxPoints()+") RETURNING id;");
                rs.next();
                int questionId = rs.getInt("id");
                //PreparedStatement ps;
                //ArrayList<String> options = question.getOptions();
                switch (questionType)
                {
                    case ONEOPTION:
                        setOneOption(questionId, c, question);
                        break;
                    case FEWOPTIONS:
                        setFewOptions(questionId, c, question);
                        break;
                    case MATCHING:
                        setMatching(questionId, c, question);
                        break;
                    case INPUT:
                        setInput(questionId, c, question);
                        break;
                    case ESSAY:
                        //nothing to add
                        break;
                }

            }
            stmt.close();
            //c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
    private Question getQuestion(int questionId)
    {
        Connection c = null;
        Statement stmt = null;
        Question question = new Question();
        try
        {
            c = connect();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions WHERE id="+questionId+";");
            rs.next();

            question.setId(questionId);
            Question.AnswerType type = Question.AnswerType.valueOf(rs.getString("type"));
            question.setType(type);
            question.setTask(rs.getString("task"));
            question.setMaxPoints(rs.getInt("maxpoints"));


            switch (type)
            {
                case ONEOPTION:
                    getOneOption(questionId, c, question);
                    break;
                case FEWOPTIONS:
                    getFewOptions(questionId, c, question);
                    break;
                case MATCHING:
                    getMatching(questionId, c, question);
                    break;
                case INPUT:
                    getInput(questionId, c, question);
                    break;
                case ESSAY:

                    break;
            }
            rs.close();
            stmt.close();
            c.close();
            return question;
        } catch (Exception e)
        {

        }
        return question;
    }
    private void editQuestion(int questionId, int testId, Question newQuestion)
    {
        Connection c = null;
        Statement stmt = null;

        Question original = getQuestion(questionId);
        /*if (original.getType()!=)

            try
            {
                c = connect();
                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery();


            } catch (Exception e)
            {

            }*/
    }
}