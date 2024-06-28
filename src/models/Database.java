package models;

import models.datastructures.DataScore;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * See klass tegeleb andmebaasi ühenduse ja "igasuguste" päringutega tabelitest.
 * Alguses on ainult ühenduse jaoks funktsionaalsus
 */
public class Database {
    /**
     * Algselt ühendust pole
     */
    private Connection connection = null;
    /**
     * Andmebaasi ühenduse string
     */
    private String databaseUrl;
    /**
     * Loodud mudel
     */
    private Model model;

    /**
     * Klassi andmebaas konstruktor
     * @param model loodud mudel
     */
    public Database(Model model) {
        this.model = model;
        this.databaseUrl = "jdbc:sqlite:" + model.getDatabaseFile();
        this.selectUniqueCategories();
    }


    /**
     * Loob andmebaasiga ühenduse
     * @return andmebaasi ühenduse
     */
    private Connection dbConnection() throws SQLException {
        // https://stackoverflow.com/questions/13891006/
        if(connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(databaseUrl);
        return connection;
    }

    private void selectUniqueCategories() {
        String sql = "SELECT DISTINCT(category) as category FROM words ORDER BY category;";
        List<String> categories = new ArrayList<>();  // Tühi kategooriate list
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String category = rs.getString("category");
                categories.add(category); // Lisa kategooria listi kategooriad (categories)
            }
            categories.add(0, model.getChooseCategory()); // Esimeseks "Kõik kategooriad"
            // System.out.println(categories.toString());  // Test kas kategooriad on olemas
            String[] result = categories.toArray(new String[0]);  // List<String> => String[]
            model.setCmbCategories(result); // Määra kategooriad mudelisse
            connection.close();  // Andmebaasi ühendus sulgeda
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectScores() {
        String sql = "SELECT * FROM scores ORDER BY gametime, playertime DESC, playername;";
        List<DataScore> data = new ArrayList<>();
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            model.getDataScores().clear(); // Tühjenda mudelis listi sisu

            while (rs.next()) {
                String datetime = rs.getString("playertime");
                LocalDateTime playerTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String playerName = rs.getString("playername");
                String guessWord = rs.getString("guessword");
                String wrongChar = rs.getString("wrongcharacters");
                int timeSeconds = rs.getInt("gametime");
                // System.out.println(datetime + " | " + playerTime);

                // Lisa listi kirje
                data.add(new DataScore(playerTime, playerName, guessWord, wrongChar, timeSeconds));
            }
            model.setDataScores(data); // Muuda andmed mudelis
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToTable(String playertime, String playername, String guessword, String wrongcharacters, String gametime) {
        String sql = "INSERT INTO scores (playertime, playername, guessword, wrongcharacters, gametime) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = this.dbConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, playertime);
            pstmt.setString(2, playername);
            pstmt.setString(3, guessword);
            pstmt.setString(4, wrongcharacters);
            pstmt.setString(5, gametime);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data has been inserted successfully!");
            }

            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setWordByCategory(String category) {
        String sql = "";
        if(category == "Kõik kategooriad") {
            sql = "SELECT * FROM words;";
        }
        else if(category.equals("Hoone")) {
            sql = "SELECT * FROM words WHERE category = 'Hoone';";
        }
        else if (category.equals("Elukutse")) {
            sql = "SELECT * FROM words WHERE category = 'Elukutse';";
        }
        List<String> words = new ArrayList<>();  // Tühi kategooriate list
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String word = rs.getString("word");
                words.add(word); // Lisa kategooria listi kategooriad (categories)
            }
            //categories.add(0, model.getChooseCategory()); // Esimeseks "Kõik kategooriad"
            // System.out.println(categories.toString());  // Test kas kategooriad on olemas
            String[] result = words.toArray(new String[0]);// List<String> => String[]
            System.out.println(words);
            //model.setCmbCategories(result); // Määra kategooriad mudelisse
            Random random = new Random();
            int randomWordIndex = random.nextInt(result.length);
            String word = result[randomWordIndex];
            System.out.println(word);
            model.setWord(word.toLowerCase());
            connection.close();  // Andmebaasi ühendus sulgeda
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
