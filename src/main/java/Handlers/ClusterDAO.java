package Handlers;

import model.History;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClusterDAO {
    private static ClusterDAO instance;

    public ClusterDAO(){
        try{
            createTable();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ClusterDAO getInstance() {
        if (instance == null) {
            instance =new ClusterDAO();
        }
        return instance;
    }

    public void createTable() throws Exception {
        try (Connection conn = Database.connect()) {
            conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS clusters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                ip TEXT NOT NULL,
                token TEXT NOT NULL
            )
        """);

            conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                value TEXT NOT NULL
            )
        """);
        }
    }

    public void saveHistory(String typeStr,String valueHis) throws Exception {
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO history(type,value) VALUES ( ?,? )""");
            stmt.setString(1, typeStr);
            stmt.setString(2, valueHis);

            stmt.executeUpdate();
        }
    }

    public List<History> getLastHourHistory() throws Exception {
        List<History> list = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("""
                            SELECT *
                            FROM history
                            WHERE timestamp >= datetime('now', '-1 hours');""");

            while (rs.next()) {
                History entry = new History();
                entry.type = rs.getString("type");
                entry.time = rs.getString("timestamp");
                entry.value = rs.getString("value");

                list.add(entry);
            }
        }
        return list;
    }
}
