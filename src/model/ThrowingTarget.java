/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ThrowingTarget {
    private double x, y; // Koordinat posisi target yang dilempar
    private double velocityX, velocityY; // Kecepatan gerak arah X dan Y
    private boolean active = true; // Status apakah target masih aktif (bergerak) atau tidak
    private BufferedImage sprite; // Gambar sprite target
    private int value; // Nilai dari target
    private int targetX, targetY; // Titik tujuan target dilempar

    // Konstruktor: inisialisasi posisi awal, tujuan, gambar sprite, dan memulai pergerakan
    public ThrowingTarget(int startX, int startY, int targetX, int targetY, Target originalTarget) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX + 24; // Penyesuaian agar target dilempar ke tengah tile
        this.targetY = targetY + 24;
        this.value = originalTarget.getValue(); // Ambil nilai dari objek Target asli

        loadSprite(originalTarget); // Ambil gambar sprite target mati
        calculateLinearVelocity(); // Hitung arah dan kecepatan lemparan

        playShootBasketSound(); // Mainkan suara saat target dilempar
    }

    // Fungsi untuk memutar suara saat target dilempar
    private void playShootBasketSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/shoot_basket.wav")
            );

            AudioFormat format = audioInputStream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                // Konversi format audio jika belum PCM_SIGNED
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false
                );
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            // Abaikan jika terjadi kesalahan audio, game tetap jalan
        }
    }

    // Fungsi untuk memuat sprite dari target yang dilempar
    private void loadSprite(Target originalTarget) {
        try {
            String type = originalTarget.getType(); // Ambil tipe target (ikan, botol, dll)
            String prefix = (type == null) ? "ikan" : type;
            sprite = ImageIO.read(getClass().getResourceAsStream("/target/" + prefix + "_mati.png"));
        } catch (IOException e) {
            // Jika gagal load gambar, gunakan gambar sprite dari target asli
            sprite = originalTarget.getCurrentSprite();
        }
    }

    // Menghitung kecepatan gerak target berdasarkan posisi tujuan
    private void calculateLinearVelocity() {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy); // Jarak total

        double speed = 8.0; // Kecepatan konstan
        velocityX = dx / distance * speed;
        velocityY = dy / distance * speed;
    }

    // Memperbarui posisi target setiap frame
    public void update() {
        if (!active) return; // Jika tidak aktif, tidak usah update

        x += velocityX;
        y += velocityY;

        // Jika sudah dekat dengan target tujuan, hentikan
        double distToTarget = Math.hypot(x - targetX, y - targetY);
        if (distToTarget < 10) {
            active = false;
        }

        // Jika keluar dari layar, juga nonaktifkan
        if (x < -50 || x > 900 || y < -50 || y > 700) {
            active = false;
        }
    }

    // Menggambar target di layar
    public void draw(java.awt.Graphics2D g2, int tileSize) {
        if (!active || sprite == null) return; // Jika tidak aktif atau sprite null, skip

        int drawX = (int)(x - tileSize / 2);
        int drawY = (int)(y - tileSize / 2);

        // Gambar sprite target
        g2.drawImage(sprite, drawX, drawY, tileSize, tileSize, null);

        // Tampilkan nilai dari target di bawah sprite
        g2.setColor(java.awt.Color.WHITE);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        String val = String.valueOf(value);
        int textX = drawX + tileSize / 2 - g2.getFontMetrics().stringWidth(val) / 2;
        int textY = drawY + tileSize + g2.getFontMetrics().getHeight();
        g2.drawString(val, textX, textY);
    }

    // Getter untuk status aktif
    public boolean isActive() { return active; }

    // Getter untuk nilai
    public int getValue() { return value; }

    // Getter untuk posisi
    public double getX() { return x; }
    public double getY() { return y; }
}
