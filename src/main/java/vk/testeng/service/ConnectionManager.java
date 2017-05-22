package vk.testeng.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class ConnectionManager {
    static Connection c;
    public static Connection connect()
    {
        c = null;
        try {
            Path file = Paths.get("C:/Users/VAIO/IdeaProjects/TestEngine/src/main/webapp/DB.cre");
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
}
