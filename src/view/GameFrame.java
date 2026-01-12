/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package view;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Kelas GameFrame adalah jendela utama (main window) dari permainan.
 * Kelas ini merupakan turunan dari JFrame dan berfungsi untuk menampilkan
 * GamePanel serta menangani event ketika jendela ditutup.
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel; // Panel utama tempat game berjalan

    /**
     * Konstruktor GameFrame
     * @param gamePanel objek GamePanel yang akan ditampilkan di frame
     */
    public GameFrame(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Atur agar ketika tombol close ditekan, tidak langsung menutup program
        this.setResizable(false); // Mencegah pengguna mengubah ukuran jendela
        this.add(gamePanel); // Menambahkan gamePanel ke frame
        this.pack(); // Mengatur ukuran frame agar pas dengan ukuran komponen di dalamnya
        this.setLocationRelativeTo(null); // Menempatkan frame di tengah layar
        this.setVisible(true); // Menampilkan frame

        // Menetapkan referensi ke frame di dalam GamePanel
        gamePanel.setParentFrame(this);

        // Memberikan fokus ke GamePanel setelah frame tampil
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());

        // Menambahkan listener untuk menangani aksi ketika jendela akan ditutup
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ketika jendela akan ditutup, jalankan proses endGame
                gamePanel.endGame();
            }
        });
    }
}
