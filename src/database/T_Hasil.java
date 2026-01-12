/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

// merupakan package folder dari database
package database;

// Import library java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// kelas T_Hasil
public class T_Hasil {

    // Fungsi ini untuk mengambil semua data pada database di tabel thasil
    public List<Object[]> getAll() {
        List<Object[]> data = new ArrayList<>(); // Karena isinya banyak buat jadi list
        // Isi query yang akan digunakan
        String query = "SELECT username, skor, count FROM thasil ORDER BY skor DESC"; // ambil username skor dan count dari tabel thasil dan di urut berdasarkan skor descending

        try (Connection conn = Database.getConnection();
             // buat statement dan execute query
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // masukan objek objek tersebut sesuai dengan nama atributnya
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("username"),
                        rs.getInt("skor"),
                        rs.getInt("count")
                });
            }
        } catch (SQLException e) { // Exception error handling
            e.printStackTrace();
        }

        return data;
    }

    // fungsi ini untuk menyimpan data dan update skor ke database
    public void saveOrUpdateScore(String username, int score, int count) {
        try (Connection conn = Database.getConnection()) {
            // Cek apakah user sudah ada
            String checkQuery = "SELECT skor, count FROM thasil WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int oldScore = rs.getInt("skor");
                int oldCount = rs.getInt("count");

                // Bandingkan dan ambil nilai tertinggi
                int newScore = Math.max(oldScore, score);
                int newCount = Math.max(oldCount, count);

                String updateQuery = "UPDATE thasil SET skor = ?, count = ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, newScore);
                updateStmt.setInt(2, newCount);
                updateStmt.setString(3, username);
                updateStmt.executeUpdate();
            } else {
                // Insert baru kalau belum ada
                String insertQuery = "INSERT INTO thasil (username, skor, count) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, username);
                insertStmt.setInt(2, score);
                insertStmt.setInt(3, count);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
