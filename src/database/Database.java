/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

// merupakan package folder database
package database;

// Import Library Java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Kelas Database
public class Database {
    // Menggunakan JDBC sebagai jembatan database ke game
    private static final String URL = "jdbc:mysql://localhost:3306/db_abdurrahman rauf budiman"; // URL menuju ke database
    private static final String USER = "root";   // User root agar semua bisa akses
    private static final String PASS = "";       // Password kosongkan juga agar semua bisa akses

    // Buat exception untuk cek koneksi
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
