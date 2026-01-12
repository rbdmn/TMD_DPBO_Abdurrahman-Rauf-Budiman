/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package view;

import viewmodel.GameViewModel;
import viewmodel.PlayerViewModel;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;

/**
 * Kelas utama untuk menggambar dan mengelola logika tampilan game.
 * GamePanel adalah View yang berinteraksi dengan ViewModel.
 */
public class GamePanel extends JPanel implements Runnable {

    // Ukuran tile dan layar
    public final int tileSize = 48;
    private final int maxScreenCol = 16;
    private final int maxScreenRow = 12;
    private final int screenWidth = tileSize * maxScreenCol;
    private final int screenHeight = tileSize * maxScreenRow;

    private Clip backgroundMusic;  // Musik latar belakang
    private Font pixelFont;        // Font khusus (pixelated style)

    // Thread game
    private Thread gameThread;
    private boolean gameRunning = true;

    // ViewModel (akses data game melalui ViewModel, bukan langsung ke Model)
    private final GameViewModel gameViewModel;

    // Referensi ke JFrame utama
    private JFrame parentFrame;

    // Timer
    private long startTime; // waktu mulai
    private final long GAME_DURATION = 60_000; // 1 menit (dalam milidetik)
    private boolean gameOver = false; // flag agar tidak eksekusi berulang
    private Runnable onGameEnd; // callback untuk kembali ke GUI

    /**
     * Konstruktor utama GamePanel.
     */
    public GamePanel(GameViewModel gameViewModel, PlayerViewModel playerViewModel) {
        // Atur ukuran dan tampilan panel
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Untuk mencegah flickering
        this.addKeyListener(playerViewModel); // Input keyboard
        this.addMouseListener(playerViewModel); // Input mouse
        this.setFocusable(true);

        this.gameViewModel = gameViewModel;
        this.startTime = System.currentTimeMillis();
        playerViewModel.setGameEndCallback(this::endGame); // Event untuk mengakhiri game

        // Load custom font
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/font.ttf"))
                    .deriveFont(Font.PLAIN, 14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Monospaced", Font.BOLD, 14); // Fallback font
        }

        // Inisialisasi status awal dari ViewModel
        gameViewModel.updateGameState();
    }

    // Setter untuk frame utama
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    public void setOnGameEnd(Runnable callback) {
        this.onGameEnd = callback;
    }

    // Memulai thread game dan musik
    public void startGameThread() {
        playBackgroundMusic();
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Mengakhiri permainan dengan menyimpan skor dan kembali ke menu
    // Mengakhiri permainan dengan menyimpan skor dan kembali ke menu
    public void endGame() {
        gameRunning = false;
        gameViewModel.endGame(); // End logic diserahkan ke ViewModel

        if (!gameOver) {
            gameOver = true;

            // Simpan skor ke database
            gameViewModel.getGameState().saveResultToDatabase();

            // Eksekusi UI di thread Event Dispatch
            SwingUtilities.invokeLater(() -> {
                GameViewModel.GameRenderData data = gameViewModel.getRenderData();

                // Tampilkan pesan hasil akhir
                String message = String.format(
                        "Game Over!\n\nFinal Score: %d\nTargets Collected: %d\n",
                        data.score,
                        data.count
                );

                JOptionPane.showMessageDialog(this, message, "Game Finished", JOptionPane.INFORMATION_MESSAGE);

                // Tutup jendela game jika ada
                if (parentFrame != null) {
                    parentFrame.dispose();
                } else {
                    SwingUtilities.getWindowAncestor(this).dispose();
                }

                // Hentikan musik background jika ada
                if (backgroundMusic != null && backgroundMusic.isRunning()) {
                    backgroundMusic.stop();
                    backgroundMusic.close();
                }

                // Kembali ke menu utama (GUI)
                new GUI();
            });
        }
    }


    // Loop utama game (60 FPS)
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / 60; // Target 60 frame per detik
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null && gameRunning) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint(); // Memanggil paintComponent()
                delta--;
            }
        }
    }

    // Update logika game
    public void update() {
        if (gameRunning) {
            gameViewModel.updateGameState();
        }
        if (!gameOver && System.currentTimeMillis() - startTime >= GAME_DURATION) {
            endGame(); // Waktu habis, akhiri game
        }
    }

    // Render visual game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GameViewModel.GameRenderData data = gameViewModel.getRenderData();

        drawTiles(g2, data);
        drawBaskets(g2, data);
        drawTargets(g2, data);
        drawBullets(g2, data);
        drawThrowingTargets(g2, data);
        drawPlayer(g2, data);
        drawUI(g2, data);

        // Hitung waktu tersisa
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = Math.max(0, (GAME_DURATION - elapsed) / 1000); // dalam detik
        String timerText = String.format("%02d:%02d", remaining / 60, remaining % 60);

        // Atur font dan metrik
        g.setFont(pixelFont.deriveFont(Font.BOLD, 18f));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(timerText);
        int textHeight = fm.getHeight();

        // Ukuran dan posisi kontainer
        int boxWidth = textWidth + 30;
        int boxHeight = textHeight + 20;
        int x = (getWidth() - boxWidth) / 2;
        int y = getHeight() - boxHeight - 10;

        // Gambar border tebal (cara: 2 lapis kotak)
        g.setColor(Color.BLACK);
        g.fillRoundRect(x - 3, y - 3, boxWidth + 6, boxHeight + 6, 20, 20); // border luar hitam

        // Gambar background oranye di atasnya
        g.setColor(new Color(255, 140, 0)); // oranye
        g.fillRoundRect(x, y, boxWidth, boxHeight, 20, 20);

        // Gambar teks timer
        g.setColor(Color.WHITE);
        g.drawString(timerText, x + (boxWidth - textWidth) / 2, y + (boxHeight + textHeight) / 2 - 2);


        g2.dispose(); // Bebaskan resource Graphics
    }

    // ========================== DRAWING METHODS ============================= //
    // Gambar Tile
    private void drawTiles(Graphics2D g2, GameViewModel.GameRenderData data) {
        int[][] map = data.mapTiles;
        for (int row = 0; row < map[0].length; row++) {
            for (int col = 0; col < map.length; col++) {
                int tileIndex = map[col][row];
                Image img = data.tileSprites[tileIndex].image;
                g2.drawImage(img, col * tileSize, row * tileSize, tileSize, tileSize, null);
            }
        }
    }

    // Gambar Basket
    private void drawBaskets(Graphics2D g2, GameViewModel.GameRenderData data) {
        boolean showGlow = data.hasTargetsToThrow;

        for (model.Basket basket : data.baskets) {
            if (showGlow) {
                g2.setColor(new Color(255, 255, 0, 50));
                g2.fillRect(basket.getX() + 4, basket.getY() + 4, basket.getWidth() - 8, basket.getHeight() - 8);
            }

            g2.setColor(new Color(139, 69, 19)); // Warna coklat
            g2.fillRect(basket.getX() + 8, basket.getY() + 8, basket.getWidth() - 16, basket.getHeight() - 16);

            g2.setColor(Color.BLACK);
            g2.drawRect(basket.getX() + 8, basket.getY() + 8, basket.getWidth() - 16, basket.getHeight() - 16);
        }
    }

    // Gambar Target
    private void drawTargets(Graphics2D g2, GameViewModel.GameRenderData data) {
        for (model.Target target : data.targets) {
            if (!target.isCollected()) {
                // Gambar target (ikan/item) seperti biasa
                g2.drawImage(target.getCurrentSprite(), target.getX(), target.getY(), tileSize, tileSize, null);

                // Font dan string nilai
                g2.setFont(new Font("Arial", Font.BOLD, 14)); // Sedikit perbesar font agar jelas
                FontMetrics fm = g2.getFontMetrics();
                String valueStr = String.valueOf(target.getValue());

                // Hitung posisi X agar teks tetap di tengah secara horizontal
                int textX = target.getX() + (tileSize - fm.stringWidth(valueStr)) / 2;

                // Hitung posisi Y agar teks berada di atas gambar dengan sedikit jarak
                int gap = 4;
                int textY = target.getY() - gap;

                // Gambar outline/bayangan untuk teks (warna hitam)
                g2.setColor(Color.BLACK);
                g2.drawString(valueStr, textX - 1, textY);
                g2.drawString(valueStr, textX + 1, textY);
                g2.drawString(valueStr, textX, textY - 1);
                g2.drawString(valueStr, textX, textY + 1);

                // Gambar teks utama (warna putih atau warna cerah lainnya)
                g2.setColor(Color.WHITE);
                g2.drawString(valueStr, textX, textY);
            }
        }
    }

    // Gambar Hook
    private void drawBullets(Graphics2D g2, GameViewModel.GameRenderData data) {
        int playerCenterX = data.playerX + tileSize / 2;
        int playerCenterY = data.playerY + tileSize / 2;

        for (model.Bullet bullet : data.bullets) {
            drawRope(g2, bullet, playerCenterX, playerCenterY);

            if (bullet.getSprite() != null) {
                g2.drawImage(bullet.getSprite(), bullet.getX() - 12, bullet.getY() - 12, 24, 24, null);
            } else {
                g2.setColor(Color.YELLOW);
                g2.fillOval(bullet.getX() - 6, bullet.getY() - 6, 12, 12);
                g2.setColor(Color.ORANGE);
                g2.drawOval(bullet.getX() - 6, bullet.getY() - 6, 12, 12);
            }
        }
    }

    // Gambar tali hook
    private void drawRope(Graphics2D g2, model.Bullet bullet, int playerCenterX, int playerCenterY) {
        Point[] segments = bullet.getRopeSegments(playerCenterX, playerCenterY);

        g2.setColor(new Color(0, 0, 0, 100));
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < segments.length - 1; i++) {
            g2.drawLine(segments[i].x + 1, segments[i].y + 1, segments[i + 1].x + 1, segments[i + 1].y + 1);
        }

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < segments.length - 1; i++) {
            g2.drawLine(segments[i].x, segments[i].y, segments[i + 1].x, segments[i + 1].y);
        }

        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < segments.length - 1; i++) {
            g2.drawLine(segments[i].x, segments[i].y, segments[i + 1].x, segments[i + 1].y);
        }

        g2.setStroke(new BasicStroke(1)); // Reset stroke
    }

    // Gambar lemparan target
    private void drawThrowingTargets(Graphics2D g2, GameViewModel.GameRenderData data) {
        for (model.ThrowingTarget t : data.throwingTargets) {
            t.draw(g2, tileSize);
        }
    }

    // Gambar user pemain
    private void drawPlayer(Graphics2D g2, GameViewModel.GameRenderData data) {
        g2.drawImage(data.playerSprite, data.playerX, data.playerY, tileSize, tileSize, null);
    }

    // Gambar UI
    private void drawUI(Graphics2D g2, GameViewModel.GameRenderData data) {
        // Atur font dan padding
        g2.setFont(pixelFont.deriveFont(14f));
        FontMetrics fm = g2.getFontMetrics();
        int padding = 8;

        // ==== KIRI ====
        java.util.List<String> leftText = new java.util.ArrayList<>();
        if (data.username != null) leftText.add("Player: " + data.username);
        leftText.add("Score: " + data.score);
        leftText.add("Count: " + data.count);

        int maxWidth = 0;
        for (String line : leftText) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }
        int height = leftText.size() * fm.getHeight();
        int x = 8, y = 8;

        // Gambar border hitam (kiri)
        g2.setColor(Color.BLACK);
        g2.fillRect(x - padding / 2 - 2, y - padding / 2 - 2, maxWidth + padding + 4, height + padding + 4);

        // Gambar background oranye (kiri)
        g2.setColor(new Color(255, 140, 0));
        g2.fillRect(x - padding / 2, y - padding / 2, maxWidth + padding, height + padding);

        // Gambar teks kiri
        g2.setColor(Color.BLACK);
        int textY = y + fm.getAscent();
        for (String line : leftText) {
            g2.drawString(line, x, textY);
            textY += fm.getHeight();
        }

        // ==== KANAN ====
        java.util.List<String> rightText = new java.util.ArrayList<>();
        rightText.add("Mode: " + (data.hasTargetsToThrow ? "THROW" : "SHOOT"));

        maxWidth = 0;
        for (String line : rightText) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }

        x = screenWidth - maxWidth - padding - 10;
        y = 8;
        height = rightText.size() * fm.getHeight();

        // Gambar border hitam (kanan)
        g2.setColor(Color.BLACK);
        g2.fillRect(x - padding / 2 - 2, y - padding / 2 - 2, maxWidth + padding + 4, height + padding + 4);

        // Gambar background oranye (kanan)
        g2.setColor(new Color(255, 140, 0));
        g2.fillRect(x - padding / 2, y - padding / 2, maxWidth + padding, height + padding);

        // Gambar teks kanan
        g2.setColor(Color.BLACK);
        textY = y + fm.getAscent();
        for (String line : rightText) {
            g2.drawString(line, x, textY);
            textY += fm.getHeight();
        }
    }



    // ============================ AUDIO ============================= //

    private void playBackgroundMusic() {
        try {
            File musicPath = new File("res/audio/bgm.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInput);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
            } else {
                System.out.println("File audio tidak ditemukan: " + musicPath.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
