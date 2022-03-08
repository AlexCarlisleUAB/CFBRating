/*
TO DO LIST:


1. implement rating update, SOV/SOD update in ratingUpdate() method?
2. order database by highest rating--orderDatabase() method?
3. tweak formula until satisfied
4. Implement tiebreakers (Head to head)
5. Add option to add games from text file (BONUS)

Main formula right now

Team Record = 55%
Strength of Victory = 25%
Strength of Defeat = 20%
Tiebreaker = head to head

*/

import java.util.Scanner;
import java.sql.*;

/**
 * Runs the code from classes Team and Game
 */
public class Driver
{
    public static int elements = 0;
    public static void main (String [] args) throws SQLException
    {
        Connection conn = getConnection();

        //initializeTeams(conn);

        addGames(conn); //to add games manually

        // TO DO: possibly give option to add games from a text file?
    }
    
    //takes a winner id and a loser id and adds a game based off of that
    public static void addGames(Connection connIn) throws SQLException 
    {
        int input = 0;
        Scanner sc = new Scanner(System.in);
        int exit = 0;
        int i = 1;

        while (exit != -1)
        {
            System.out.println("What is the winner's ID? Type -1 to exit.");
            input = sc.nextInt();
            int winner = input;
            if (input == -1)
            {
                break;
            }

            System.out.println("What is the loser's ID?");
            input = sc.nextInt();
            int loser = input;

            //Statement state = connIn.createStatement(); //storing G to database

            String iString = String.valueOf(i);
            String winString = String.valueOf(winner);
            String lossString = String.valueOf(loser);

            String gameState = "INSERT into games " + " (GameID, winningID, losingID)" + " VALUES (?, ?, ?)";
            PreparedStatement gamestatement = connIn.prepareStatement(gameState);
            gamestatement.setString(1, iString);
            gamestatement.setString(2, winString);
            gamestatement.setString(3, lossString);

            gamestatement.executeUpdate(); 
            //state.executeUpdate("UPDATE team SET wins = wins + 1, winPercent = wins/(wins + losses) WHERE teamID = winner");
            String updateWinner = "UPDATE TEAM SET wins = wins + 1, winPercent = wins/(wins + losses) WHERE teamID = ?";
            PreparedStatement psWinner = connIn.prepareStatement(updateWinner);
            psWinner.setInt(1, winner);
            psWinner.executeUpdate();

            
            //state.executeUpdate("UPDATE team SET losses = losses + 1, winPercent = wins/(wins + losses) WHERE teamID = loser"); 
            String updateLoser = "UPDATE TEAM SET losses = losses + 1, winPercent = wins/(wins + losses) WHERE teamID = ?";
            PreparedStatement psLoser = connIn.prepareStatement(updateLoser);
            psLoser.setInt(1, loser);
            psLoser.executeUpdate();

            i++;
        }

        sc.close();
    }

    //stores INITIAL teams in a database
    public static void initializeTeams(Connection conn) throws SQLException
    {

        Statement state = conn.createStatement(); //storing teams to database?
        //database format for teams below
        //ID, rank, teamName, rating, wins, losses, winPercent, SOV, SOD, gamesWON, GamesLOST
        
        /* PREVIOUS TEAMS
        state.executeUpdate("INSERT INTO team VALUES (1, 1, 'Alabama', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (2, 1, 'Auburn', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (3, 1, 'Arkansas', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (4, 1, 'LSU', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (5, 1, 'Texas A&M', 0, 0, 0, 0)");
        state.executeUpdate("INSERT INTO team VALUES (6, 1, 'Mississippi State', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (7, 1, 'Ole Miss', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (8, 1, 'Oklahoma', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (9, 1, 'Texas', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (10, 1, 'Georgia', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (11, 1, 'Florida', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (12, 1, 'South Carolina', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (13, 1, 'Missouri', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (14, 1, 'Kentucky', 0, 0, 0, 0)"); 
        state.executeUpdate("INSERT INTO team VALUES (15, 1, 'Tennessee', 0, 0, 0, 0)");
        state.executeUpdate("INSERT INTO team VALUES (16, 1, 'Vanderbilt', 0, 0, 0, 0)");
        */

    }
    
    //establishes a connection with the database
    public static Connection getConnection()
    {
        try
        {
            String url = "jdbc:mysql://localhost:3306/new_schema"; 

            String username = "root";
            String password = "SQLAlexCarlisle33!!"; //SQL password

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to Database");

            return conn;
        } catch(Exception e) {System.out.println(e);}

        return null;
    }

    //creates a statement
    public static Statement getStatement(Connection connIn)
    {
        try
        {
            Statement state = connIn.createStatement();
            return state; 
        } catch(Exception e) {System.out.println(e);}

        return null;
    }

    //gives a set of results, NOT NEEDED?!?
    public static ResultSet getResult(Statement stateIn)
    {
        try
        {
            ResultSet result = stateIn.executeQuery("SELECT * FROM team ORDER BY rank"); 
            
            return result;
        } catch(Exception e) {System.out.println(e);}

        return null;
    }

    //Turns database result to a string, NOT NEEEDED?!?
    public static String[] resToString(ResultSet resIn)
    {
        try
        {
            String[] resToStr = new String[5]; 

            int i = 0; //index

            while (resIn.next())
            {
                int rank = resIn.getInt("Rank");

                String name = resIn.getString("TeamName");
                String formattedName = String.format("%-30s", name);

                int wins = resIn.getInt("Wins");
                int losses = resIn.getInt("Losses");
                double rating = resIn.getDouble("Rating");

                String rowTS = rank + "\t\t" + formattedName + "\t\t" + wins + "\t\t" + losses + "\t\t" + rating;
                resToStr[i] = rowTS;
                i++;
            }

            Driver.elements = i;
            return resToStr;
        } catch(Exception e) {System.out.println(e);}

        return null;
    }
}