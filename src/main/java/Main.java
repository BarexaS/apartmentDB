import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "qwerty";

    static Connection conn;

    public static void main(String[] args) {
            try (Scanner sc = new Scanner(System.in)){
                try {
                    conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                    initDB();

                    while (true) {
                        System.out.println("Input '1' for add apartment");
                        System.out.println("Input '2' for search apartment");

                        String s = sc.nextLine();
                        switch (s) {
                            case "1":
                                addApartment(sc);
                                break;
                            case "2":
                                searchApartment(sc);
                                break;
                            default:
                                return;
                        }
                    }
                } finally {
                    if (conn != null) conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            }
        }

    private static void initDB() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Apartments");
//            (район, адрес, площадь, кол. комнат, цена)
            st.execute("CREATE TABLE Apartments (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    " state VARCHAR(255) NOT NULL," +
                    " address VARCHAR(255) NOT NULL, " +
                    " area FLOAT(10,2) NOT NULL," +
                    " rooms INT NOT NULL," +
                    " price DEC(15,2) NOT NULL)");
        }
    }

    private static void addApartment(Scanner sc) throws SQLException {
        System.out.print("Enter state: ");
        String state = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter apartment area: ");
        String sArea = sc.nextLine();
        double area = Double.parseDouble(sArea);
        System.out.print("Enter numbers of rooms: ");
        String sRooms = sc.nextLine();
        int rooms = Integer.parseInt(sRooms);
        System.out.print("Enter apartment price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);

        String statement = "INSERT INTO Apartments (state, address, area, rooms, price)" +
                " VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(statement)){
            ps.setString(1, state);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, rooms);
            ps.setDouble(5, price);
            ps.executeUpdate();
        }
    }

    private static void searchApartment(Scanner sc) throws SQLException {
        StringBuilder statement = new StringBuilder();

        requestBuilder(sc, statement);

        try (PreparedStatement ps = conn.prepareStatement(statement.toString());
             ResultSet rs = ps.executeQuery()){

            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t\t");
                }
                System.out.println();
            }
        }
    }

    private static void requestBuilder(Scanner sc, StringBuilder statement) {
        statement.append("SELECT * FROM Apartments WHERE ");
        System.out.println("Enter parameters name for request");
        System.out.println("In next format : ");
        System.out.println("'1+2+3+4+5' - for search by state,address,area,rooms and price");
        System.out.println("'1' - to add state as search parameters");
        System.out.println("'2' - to add address as search parameters");
        System.out.println("'3' - to add area as search parameters");
        System.out.println("'4' - to add rooms as search parameters");
        System.out.println("'5' - to add price as search parameters");

        String request = sc.nextLine();
        String[] params = request.split("\\+");
        for (String param: params) {
            switch (param){
                case "1":
                    statement.append("state=\'");
                    System.out.println("Enter state for search :");
                    statement.append(sc.nextLine()+"\' AND ");
                    break;
                case "2":
                    statement.append("address=\'");
                    System.out.println("Enter address for search :");
                    statement.append(sc.nextLine()+"\' AND ");
                    break;
                case "3":
                    statement.append("area=\'");
                    System.out.println("Enter area for search :");
                    statement.append(sc.nextLine()+"\' AND ");
                    break;
                case "4":
                    statement.append("rooms=\'");
                    System.out.println("Enter numbers of rooms for search :");
                    statement.append(sc.nextLine()+"\' AND ");
                    break;
                case "5":
                    statement.append("price=\'");
                    System.out.println("Enter price for search :");
                    statement.append(sc.nextLine()+"\' AND ");
                    break;
            }
        }
        statement.delete(statement.length()-5, statement.length());
        statement.append(";");
    }
}
