package vk.testeng.service;

import vk.testeng.model.Question;
import vk.testeng.model.Test;
import vk.testeng.model.answer.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class TestManager {

    private class OptionsContainer
    {
        ArrayList<String> options;
        AbstractAnswer answer;
        OptionsContainer()
        {
            options = new ArrayList<>();
            answer = null;
        }
    }
    private OptionsContainer getOneOption(int questionId, Connection c)
    {
        Statement stmt = null;
        OptionsContainer optionsContainer = new OptionsContainer();
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE question_id="+questionId+" ORDER BY id;");
            int counter = 0;
            while (queryResultSet.next())
            {
                if (queryResultSet.getBoolean("is_correct"))
                {
                    optionsContainer.answer = new OneOptionAnswer(counter);
                }
                optionsContainer.options.add(queryResultSet.getString("option"));
                counter++;
            }
            return optionsContainer;
        } catch (Exception e)
        {
            return null;
        } finally
        {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private OptionsContainer getFewOptions(int questionId, Connection c)
    {
        Statement stmt = null;
        OptionsContainer optionsContainer = new OptionsContainer();
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE question_id="+questionId+" ORDER BY id;");
            ArrayList<Integer> answers = new ArrayList<>();
            int counter = 0;
            while(queryResultSet.next())
            {
                if (queryResultSet.getBoolean("is_correct")) answers.add(counter);
                optionsContainer.options.add(queryResultSet.getString("option"));
                counter++;
            }
            optionsContainer.answer = new FewOptionsAnswer(answers);
            return optionsContainer;
        } catch (Exception e) {
            return null;
        } finally  {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private OptionsContainer getMatching(int questionId, Connection c)
    {
        Statement stmt = null;
        OptionsContainer optionsContainer = new OptionsContainer();
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM matching WHERE question_id="+questionId+" ORDER BY id;");
            ArrayList<String> options = new ArrayList<>();
            ArrayList<Integer> optionId = new ArrayList<>();
            ArrayList<Integer> optionPair = new ArrayList<>();

            while (queryResultSet.next())
            {
                options.add(queryResultSet.getString("option"));
                optionId.add(queryResultSet.getInt("id"));
                int pairId = queryResultSet.getInt("pair_id");
                optionPair.add(pairId);
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

            long seed = questionId*leftOptions.size();
            Shuffle.withSeed(leftOptions, seed);
            Shuffle.withSeed(keys, seed);
            //leftOptions -> key+8 ->rightOptions;
            for (int i = 0; i<rightOptions.size(); i++)
            {
                leftOptions.add(rightOptions.get(i));
            }
            options = leftOptions;

            optionsContainer.options = options;
            optionsContainer.answer = new MatchingAnswer(keys);
            return optionsContainer;
        } catch (Exception e) {
            return null;
        } finally {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }
    private OptionsContainer getInput(int questionId, Connection c)
    {
        OptionsContainer optionsContainer = new OptionsContainer();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet queryResultSet = stmt.executeQuery("SELECT * FROM options WHERE qid="+questionId+";");
            queryResultSet.next();
            optionsContainer.answer = new InputAnswer(queryResultSet.getString("option"));
            return optionsContainer;
        } catch (Exception e) {
            return null;
        } finally {
            if (stmt != null) {
                try {stmt.close();}
                catch (Exception e) {}
            }
        }
    }

    private void addOneOption(int questionId, Connection c, Question question)
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
    private void addFewOptions(int questionId, Connection c, Question question)
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

    private void addMatching(int questionId, Connection c, Question question)
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
    private void addInput(int questionId, Connection c, Question question)
    {
        PreparedStatement ps = null;
        String answer = ((InputAnswer)question.getCorrectAnswer()).getAnswer();
        try {
            ps = c.prepareStatement("INSERT INTO options(question_id, option, is_correct) VALUES(?,?,?);");
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

    private void editOneOption(Connection c, Question question)
    {
        PreparedStatement ps = null;
        int correctAnswer = ((OneOptionAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        try {
            for (int j = 0; j<question.getOptions().size(); j++)
            {
                ps = c.prepareStatement("UPDATE options SET option=?, is_correct=? WHERE question_id = ?;");
                ps.setString(1, options.get(j));
                ps.setBoolean(2, (j == correctAnswer));
                ps.setInt(3, question.getId());
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

    private void editFewOptions(Connection c, Question question)
    {
        ArrayList<Integer> fewCorrectAnswers =  ((FewOptionsAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        PreparedStatement ps = null;
        try {
            for (int j = 0; j<options.size(); j++)
            {
                ps = c.prepareStatement("UPDATE options SET option=?, is_correct=? WHERE question_id=?;");
                ps.setString(1, options.get(j));
                ps.setBoolean(2,fewCorrectAnswers.contains(j));
                ps.setInt(3,question.getId());
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

    private void editMatching(Connection c, Question question)
    {
        PreparedStatement ps = null;
        ArrayList<Integer> matchingCorrectAnswer = ((MatchingAnswer)question.getCorrectAnswer()).getAnswer();
        ArrayList<String> options = question.getOptions();
        try {
            ps = c.prepareStatement("SELECT * FROM matching WHERE question_id=? ORDER BY id;");
            ps.setInt(1, question.getId());
            ResultSet rs = ps.executeQuery();
            for (int j = 0; j<options.size()/2;j++)
            {
                rs.next();
                int leftId = rs.getInt("id");
                rs.next();
                int rightId = rs.getInt("id");


                ps = c.prepareStatement("UPDATE matching SET option=?, pair_id=? WHERE question_id=? AND id=?;");
                ps.setString(1, options.get(j));
                ps.setInt(2, rightId);
                ps.setInt(3, question.getId());
                ps.setInt(4, leftId);

                ps.executeUpdate();

                ps = c.prepareStatement("UPDATE matching SET option=?, pair_id=? WHERE question_id=? AND id=?;");
                ps.setString(1, options.get(matchingCorrectAnswer.get(j)+options.size()/2));
                ps.setInt(2, leftId);
                ps.setInt(3, question.getId());
                ps.setInt(4, rightId);

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

    private void editInput(Connection c, Question question)
    {
        PreparedStatement ps = null;
        String answer = ((InputAnswer)question.getCorrectAnswer()).getAnswer();
        try {
            ps = c.prepareStatement("UPDATE options SET option=?, is_correct=? WHERE question_id=?;");
            ps.setString(1,answer);
            ps.setBoolean(2,true);
            ps.setInt(3,question.getId());
            ps.executeUpdate();
        } catch (Exception e) {

        } finally {
            if (ps != null) {
                try {ps.close();}
                catch (Exception e) {}
            }
        }
    }

    public  ArrayList<Integer> getQuestionIds(int testId)
    {
        ArrayList<Integer> questionIds = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = ConnectionManager.connect();
            //c.setAutoCommit(false);

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT question_id FROM test_map WHERE test_id="+testId+" ORDER BY id;");
            while (rs.next())
            {
                questionIds.add(rs.getInt("id"));
            }

            rs.close();
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return questionIds;
    }
    //returns info only
    public ArrayList<Test> getTestsInfo()
    {
        ArrayList<Test> tests = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = ConnectionManager.connect();
            //c.setAutoCommit(false);

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM tests;");
            while (rs.next())
            {
                Test test = new Test();
                test.setId(rs.getInt("id"));
                test.setMaxPoints(rs.getInt("points"));
                test.setName(rs.getString("name"));
                test.setDescription(rs.getString("description"));
                tests.add(test);
            }

            rs.close();
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return tests;
    }




    public Test getTest(int id)
    {
        Test test = new Test();
        Connection c = null;
        Statement stmt = null;
        try {
            c = ConnectionManager.connect();
            //c.setAutoCommit(false);

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM tests WHERE id="+id+";");
            rs.next();
            test.setMaxPoints(rs.getInt("points"));
            test.setName(rs.getString("name"));
            test.setDescription(rs.getString("description"));

            rs = stmt.executeQuery("SELECT question_id FROM test_map WHERE test_id="+id+" ORDER BY id;");

            while (rs.next())
            {
                Question question;
                int questionId = rs.getInt("question_id");
                question = getQuestion(questionId);
                test.addQuestion(question);
            }

            rs.close();
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

        return test;
    }
    public int addTest(Test test)
    {
        Connection c = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        try {
            //c.setAutoCommit(false);
            c = ConnectionManager.connect();
            stmt = c.createStatement();
            ResultSet rs;
            ps = c.prepareStatement("INSERT INTO tests(name, description, points) VALUES(?,?,?) RETURNING id;");
            ps.setString(1, test.getName());
            ps.setString(2, test.getDescription());
            ps.setInt(3, test.getMaxPoints());
            rs = ps.executeQuery();
            rs.next();
            int testId = rs.getInt("id");
            for (int i = 0; i<test.getQuestionsCount(); i++)
            {
                Question question = test.getQuestion(i);
                addQuestion(testId, question);
            }
            stmt.close();
            //c.commit();
            c.close();
            return testId;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            return -1;
        }
    }
    public int addQuestion(int testId, Question question)
    {
        Question.AnswerType questionType = question.getType();
        Connection c = null;
        Statement stmt = null;
        PreparedStatement ps = null;

        try {
            c = ConnectionManager.connect();
            ps = c.prepareStatement("INSERT INTO questions(task, type, max_points) VALUES(?,?,?) RETURNING id;");
            ps.setString(1,question.getTask());
            ps.setString(2,questionType.name());
            ps.setInt(3,question.getMaxPoints());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int questionId = rs.getInt("id");
            ps = c.prepareStatement("INSERT INTO test_map(test_id, question_id) VALUES(?,?);");
            ps.setInt(1,testId);
            ps.setInt(2,questionId);
            ps.executeUpdate();

            switch (questionType) {
                case ONE_OPTION:
                    addOneOption(questionId, c, question);
                    break;
                case FEW_OPTIONS:
                    addFewOptions(questionId, c, question);
                    break;
                case MATCHING:
                    addMatching(questionId, c, question);
                    break;
                case INPUT:
                    addInput(questionId, c, question);
                    break;
                case ESSAY:
                    //nothing to add
                    break;
            }
            return questionId;
        } catch (Exception e)
        {
            return -1;
        }
    }
    public Question.AnswerType getQuestionType(int questionId)
    {
        Connection c = null;
        PreparedStatement ps = null;
        try
        {
            c = ConnectionManager.connect();
            ps = c.prepareStatement("SELECT type FROM questions WHERE id=?;");
            ps.setInt(1,questionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return Question.AnswerType.valueOf(rs.getString("type"));

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    public Question getQuestion(int questionId)
    {
        Connection c = null;
        Statement stmt = null;
        Question question;
        Question.AnswerType type;
        String task;
        int maxPoints;
        try
        {
            c = ConnectionManager.connect();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions WHERE id="+questionId+";");
            rs.next();

            type = Question.AnswerType.valueOf(rs.getString("type"));

            task = rs.getString("task");
            maxPoints = rs.getInt("maxpoints");
            OptionsContainer optionsContainer;
            switch (type)
            {
                case ONE_OPTION:
                    optionsContainer = getOneOption(questionId, c);
                    break;
                case FEW_OPTIONS:
                    optionsContainer = getFewOptions(questionId, c);
                    break;
                case MATCHING:
                    optionsContainer = getMatching(questionId, c);
                    break;
                case INPUT:
                    optionsContainer = getInput(questionId, c);
                    break;
                default:
                    return null;
            }
            rs.close();
            stmt.close();
            c.close();
            return new Question(questionId, type, maxPoints, task, optionsContainer.options, optionsContainer.answer);
        } catch (Exception e)
        {
            return null;
        }
    }

    //if exists, update insteadof insert
    //newQuestion must contain valid question id in its field [id] corresponding to id in DB
    public void editQuestion(int testId, Question newQuestion) {
        Connection c = null;
        Statement stmt = null;

        Question original = getQuestion(newQuestion.getId());
        if ((original.getType() == newQuestion.getType()&&(original.getOptions().size()==newQuestion.getOptions().size()))) {
            try {
                c = ConnectionManager.connect();
                stmt = c.createStatement();
                stmt.executeUpdate("UPDATE questions SET test_id="+testId+", task='"+newQuestion.getTask()+"', type='"+newQuestion.getType()+"', max_points="+newQuestion.getMaxPoints()+";");


                switch(original.getType())
                {
                    case ONE_OPTION:
                        editOneOption(c, newQuestion);
                        break;
                    case FEW_OPTIONS:
                        editFewOptions(c, newQuestion);
                        break;
                    case MATCHING:
                        editMatching(c, newQuestion);
                        break;
                    case INPUT:
                        editInput(c, newQuestion);
                        break;
                    case ESSAY:

                        break;
                }


            } catch (Exception e) {

            }
        } else {
            //error
        }
    }

    public int getLastAttempt(int testId, int userId)
    {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try
        {
            c = ConnectionManager.connect();
            ps = c.prepareStatement("SELECT last_attempt FROM attempts WHERE test_id=? AND user_id=?;");
            ps.setInt(1,testId);
            ps.setInt(2,userId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt("last_attempt");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    public void newAttempt(int testId, int userId)
    {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try
        {
            int lastAttempt;
            c = ConnectionManager.connect();
            ps = c.prepareStatement("SELECT last_attempt FROM attempts WHERE test_id=? AND user_id=?;");
            ps.setInt(1,testId);
            ps.setInt(2,userId);
            rs = ps.executeQuery();
            if (rs.next())
            {
                lastAttempt = rs.getInt("last_attempt")+1;
                ps = c.prepareStatement("UPDATE attempts SET last_attempt=? WHERE test_id=? AND user_id=?;");
                ps.setInt(1,lastAttempt);
                ps.setInt(2,testId);
                ps.setInt(3,userId);
            } else {
                ps = c.prepareStatement("INSERT INTO attempts(test_id, user_id, last_attempt) VALUES(?,?,?);");
                ps.setInt(1,testId);
                ps.setInt(2,userId);
                ps.setInt(3,0);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void addUserAnswer(int testId, int questionId, int userId, int attempt, AbstractAnswer answer, Question.AnswerType type)
    {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            c = ConnectionManager.connect();
            ArrayList<Integer> answers = null;
            String userInput = null;
            switch (type)
            {
                case ONE_OPTION:
                    answers = new ArrayList<>();
                    answers.add(((OneOptionAnswer)answer).getAnswer());
                    break;
                case FEW_OPTIONS:
                    answers = ((FewOptionsAnswer) answer).getAnswer();
                   break;
                case MATCHING:
                    answers = ((MatchingAnswer) answer).getAnswer();
                    break;
                case INPUT:
                    userInput = ((InputAnswer)answer).getAnswer();
                    break;
                case ESSAY:
                    userInput = ((EssayAnswer)answer).getAnswer();
                    break;
                default:
                    answers = null;
                    userInput = null;
            }
            if (answers==null&&userInput==null)
            {
                throw new RuntimeException();
            }
            if (answers!=null)
            {
                for (int i = 0; i<answers.size(); i++)
                {
                    ps = c.prepareStatement("INSERT INTO answers(test_id, question_id, user_id, attempt, answer) VALUES(?,?,?,?,?);");
                    ps.setInt(1,testId);
                    ps.setInt(2,questionId);
                    ps.setInt(3,userId);
                    ps.setInt(4,attempt);
                    ps.setInt(5,answers.get(i));
                    ps.executeUpdate();
                }
            } else {
                ps = c.prepareStatement("INSERT INTO user_inputs(test_id, question_id, user_id, attempt, answer) VALUES(?,?,?,?,?);");
                ps.setInt(1, testId);
                ps.setInt(2, questionId);
                ps.setInt(3, userId);
                ps.setInt(4, attempt);
                ps.setString(5, userInput);
            }

        } catch ( Exception e) {

        }
    }
}