import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDemo {

    static final String DB_URL = "jdbc:mysql://localhost:3306/college_db?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "ankush123"; // Replace with your MySQL password

    public static void main(String[] args) {
        System.out.println("=== JDBC Database Connectivity Demo (MySQL) ===\n");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded.");
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to MySQL database: college_db\n");
            
            createTable(conn);
            insertRecords(conn);
            System.out.println("--- All Records ---");
            displayRecords(conn);
            updateRecord(conn, 3, 91.5f);
            deleteRecord(conn, 5);
            System.out.println("--- After UPDATE and DELETE ---");
            displayRecords(conn);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    System.out.println("Close error: " + e.getMessage());
                }
            }
        }
    }

    static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS students");
        
        stmt.execute("CREATE TABLE students ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "name VARCHAR(100) NOT NULL,"
                + "branch VARCHAR(100) NOT NULL,"
                + "semester INT NOT NULL,"
                + "marks FLOAT NOT NULL,"
                + "grade VARCHAR(5) NOT NULL)");
        stmt.close();
        System.out.println("Table 'students' created.\n");
    }

    static void insertRecords(Connection conn) throws SQLException {
        String sql = "INSERT INTO students (name,branch,semester,marks,grade) VALUES (?,?,?,?,?)";
        Object[][] data = {
            {"Arjun Sharma",  "Computer Science",  5, 87.5f, "A"},
            {"Priya Patel",   "Electronics",        3, 92.0f, "A+"},
            {"Rohit Verma",   "Mechanical",         7, 74.0f, "B"},
            {"Sneha Gupta",   "Civil Engineering",  4, 68.5f, "B"},
            {"Manish Kumar",  "Information Tech",   6, 55.0f, "C"},
        };
        PreparedStatement ps = conn.prepareStatement(sql);
        for (Object[] row : data) {
            ps.setString(1, (String)  row[0]);
            ps.setString(2, (String)  row[1]);
            ps.setInt   (3, (Integer) row[2]);
            ps.setFloat (4, (Float)   row[3]);
            ps.setString(5, (String)  row[4]);
            ps.executeUpdate();
        }
        ps.close();
        System.out.println("5 records inserted.\n");
    }

    static void displayRecords(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs   = stmt.executeQuery("SELECT * FROM students ORDER BY id");
        System.out.println("+----+---------------+-------------------+-----+-------+-------+");
        System.out.println("| ID | Name          | Branch            | Sem | Marks | Grade |");
        System.out.println("+----+---------------+-------------------+-----+-------+-------+");
        int count = 0;
        while (rs.next()) {
            System.out.printf("| %-2d | %-13s | %-17s | %-3d | %-5.1f | %-5s |\n",
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("branch"),
                rs.getInt("semester"),
                rs.getFloat("marks"),
                rs.getString("grade"));
            count++;
        }
        System.out.println("+----+---------------+-------------------+-----+-------+-------+");
        System.out.println("Total: " + count + " record(s)\n");
        rs.close();
        stmt.close();
    }

    static void updateRecord(Connection conn, int id, float newMarks) throws SQLException {
        String grade = newMarks >= 90 ? "A+" : newMarks >= 80 ? "A"
                     : newMarks >= 70 ? "B+" : newMarks >= 60 ? "B" : "C";
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE students SET marks=?, grade=? WHERE id=?");
        ps.setFloat (1, newMarks);
        ps.setString(2, grade);
        ps.setInt   (3, id);
        int rows = ps.executeUpdate();
        ps.close();
        System.out.println("UPDATE: ID=" + id + " marks=" + newMarks
                + " grade=" + grade + " (" + rows + " row affected)\n");
    }

    static void deleteRecord(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM students WHERE id=?");
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        System.out.println("DELETE: ID=" + id + " removed (" + rows + " row affected)\n");
    }
}