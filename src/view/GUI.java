/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import database.T_Hasil;
import main.Main;
import viewmodel.GameViewModel;
import viewmodel.PlayerViewModel;

public class GUI extends JFrame {

    private final JTextField usernameField = new JTextField(15);        // input username
    private final JButton playButton = new JButton("PLAY");             // tombol main
    private final JButton quitButton = new JButton("QUIT");             // tombol keluar
    private final JButton licenseButton = new JButton("LICENSE");       // tombol lisensi popup

    private JComboBox<String> mapSelector;                               // pilihan map
    private JTable table;                                                // tabel leaderboard
    private DefaultTableModel tableModel;                                // model tabel
    private final T_Hasil hasilModel = new T_Hasil();                    // koneksi database hasil

    private Font pixelFont;                                              // font pixel art

    public GUI() {
        setTitle("Fishbound");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loadPixelFont();
        getContentPane().setBackground(new Color(20, 20, 20));

        // Inisialisasi komponen GUI
        initTopPanel();
        initCenterContent(); // <-- PERUBAHAN: method baru untuk menggabungkan logo dan tabel
        initBottomPanel();

        playButton.addActionListener(e -> handlePlayClick());
        quitButton.addActionListener(e -> System.exit(0));
        licenseButton.addActionListener(e -> showLicense());

        loadLeaderboard();
        setVisible(true);
    }

    private void loadPixelFont() {
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/font.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (IOException | FontFormatException e) {
            pixelFont = new Font("Monospaced", Font.PLAIN, 12);
            System.err.println("Failed to load pixel font, using default.");
        }
    }

    private void styleComponent(JComponent comp, Color bg, Color fg) {
        comp.setFont(pixelFont);
        comp.setBackground(bg);
        comp.setForeground(fg);
        comp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        if (comp instanceof JButton) {
            ((JButton) comp).setFocusPainted(false);
        }
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(20, 20, 20));

        mapSelector = new JComboBox<>(new String[]{"Map 1", "Map 2"});
        styleComponent(usernameField, Color.WHITE, Color.BLACK);
        styleComponent(mapSelector, Color.WHITE, Color.BLACK);

        JLabel usernameLabel = new JLabel("USERNAME:");
        JLabel mapLabel = new JLabel("MAP:");
        usernameLabel.setFont(pixelFont);
        usernameLabel.setForeground(Color.WHITE);
        mapLabel.setFont(pixelFont);
        mapLabel.setForeground(Color.WHITE);

        topPanel.add(usernameLabel);
        topPanel.add(usernameField);
        topPanel.add(mapLabel);
        topPanel.add(mapSelector);

        // Menempatkan panel input di bagian paling atas
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * PERUBAHAN UTAMA DI SINI
     * Method ini membuat satu panel tengah yang berisi logo DI ATAS tabel leaderboard.
     */
    private void initCenterContent() {
        // 1. Buat panel utama untuk bagian tengah dengan BorderLayout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(20, 20, 20));

        // 2. Buat dan tambahkan logo ke bagian ATAS (NORTH) dari panel tengah
        try {
            ImageIcon logoAsli = new ImageIcon("res/fishbound.png");
            int lebarBaru = 600;
            Image gambarAsli = logoAsli.getImage();
            Image gambarKecil = gambarAsli.getScaledInstance(lebarBaru, -1, Image.SCALE_SMOOTH);
            ImageIcon logoKecil = new ImageIcon(gambarKecil);
            JLabel logoLabel = new JLabel(logoKecil, SwingConstants.CENTER);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10)); // Beri sedikit jarak
            centerPanel.add(logoLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            System.err.println("Gagal memuat logo.png di folder res.");
            // Jika logo gagal dimuat, area ini akan kosong
        }

        // 3. Buat dan tambahkan tabel ke bagian TENGAH (CENTER) dari panel tengah
        tableModel = new DefaultTableModel(new Object[]{"USERNAME", "SCORE", "COUNT"}, 0);
        table = new JTable(tableModel);
        table.setFont(pixelFont.deriveFont(10f));
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.GREEN);
        table.setGridColor(Color.DARK_GRAY);
        table.setSelectionBackground(Color.GRAY);

        JTableHeader header = table.getTableHeader();
        header.setFont(pixelFont.deriveFont(10f));
        header.setBackground(new Color(40, 40, 40));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(new Color(20, 20, 20));
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Padding kiri-kanan

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // 4. Terakhir, tambahkan panel tengah yang sudah lengkap ini ke frame utama
        add(centerPanel, BorderLayout.CENTER);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 20, 20));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        styleComponent(playButton, new Color(255, 85, 85), Color.WHITE);
        styleComponent(quitButton, new Color(0, 150, 200), Color.WHITE);
        styleComponent(licenseButton, new Color(100, 100, 100), Color.WHITE);

        bottomPanel.add(playButton);
        bottomPanel.add(quitButton);
        bottomPanel.add(licenseButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handlePlayClick() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username!");
            return;
        }

        int selectedMap = mapSelector.getSelectedIndex();
        hasilModel.saveOrUpdateScore(username, 0, 0);
        dispose();
        startGameInGUI(username, selectedMap);
    }

    private void loadLeaderboard() {
        tableModel.setRowCount(0);
        List<Object[]> data = hasilModel.getAll();
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void startGameInGUI(String username, int mapIndex) {
        GameViewModel gameVM = GameViewModel.createGame(username, mapIndex);
        PlayerViewModel playerVM = gameVM.getPlayerViewModel();
        GamePanel gamePanel = new GamePanel(gameVM, playerVM);
        GameFrame gameFrame = new GameFrame(gamePanel);
        gamePanel.startGameThread();
    }


    private void showLicense() {
        String message = """
                Sound effect: https://pixabay.com
                ------------------------------
                Asset:
                Tiny Battle (1.0)
                Created/distributed by Kenney (www.kenney.nl)
                Creation date: 08-08-2023
                License: (Creative Commons Zero, CC0)
                http://creativecommons.org/publicdomain/zero/1.0/
                ------------------------------
                BACKGROUND MUSIC:
                "8 Bit Seas!" By HeatleyBros
                https://www.youtube.com/watch?v=-vP3XSoAr4Q&list=PLobY7vO0pgVKn4FRDgwXk5FUSiGS8_jA8&index=16
                """;

        JOptionPane.showMessageDialog(this, message, "Lisensi", JOptionPane.INFORMATION_MESSAGE);
    }
}