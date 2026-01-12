/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Bullet {
    private double x, y; // Posisi bullet dengan koordinat double untuk gerakan yang halus
    private double directionX, directionY; // Arah pergerakan bullet
    private final int speed = 8; // Kecepatan bullet
    private boolean active = true; // Status bullet apakah masih aktif
    private Target attachedTarget = null; // Target yang terkait dengan bullet
    private boolean returning = false; // Status apakah bullet sedang kembali ke player
    private Player player; // Referensi ke player
    private BufferedImage sprite; // Sprite gambar bullet

    // Properti lasso/rope
    private double maxDistance;        // Jarak maksimum lasso
    private double currentDistance;    // Jarak saat ini dari titik awal
    private int startX, startY;        // Titik awal lasso
    private boolean reachedMaxDistance = false;  // Apakah sudah mencapai jarak maksimum
    private boolean extending = true;            // Apakah sedang memanjang

    // Constructor untuk membuat bullet dengan target koordinat
    public Bullet(int startX, int startY, int targetX, int targetY) {
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;

        // Menghitung vektor arah menuju target
        double distance = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));
        this.directionX = (targetX - startX) / distance;
        this.directionY = (targetY - startY) / distance;

        // Set jarak maksimum berdasarkan jarak ke target
        this.maxDistance = distance;
        this.currentDistance = 0;

        // Memuat sprite bullet
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/player/tile_0039.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Memutar suara grappler saat bullet dibuat
        playGrapplerSound();
    }

    // Method untuk memutar suara grappler
    private void playGrapplerSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/grappler.wav")
            );

            AudioFormat format = audioInputStream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
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
            // Mengabaikan error audio - game tetap berjalan tanpa suara
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    // Method utama untuk update logika bullet
    public void update() {
        if (!active) return;

        if (returning && attachedTarget != null) {
            // Menarik target menuju player
            int targetCenterX = attachedTarget.getX() + 24;
            int targetCenterY = attachedTarget.getY() + 24;

            int playerCenterX = player.getX() + 24;
            int playerCenterY = player.getY() + 24;

            double distanceToPlayer = Math.sqrt(
                    Math.pow(playerCenterX - targetCenterX, 2) + Math.pow(playerCenterY - targetCenterY, 2)
            );

            if (distanceToPlayer > 30) {
                // Menghitung arah tarik menuju player
                double pullDirX = (playerCenterX - targetCenterX) / distanceToPlayer;
                double pullDirY = (playerCenterY - targetCenterY) / distanceToPlayer;

                // Memindahkan target menuju player
                attachedTarget.setX((int)(attachedTarget.getX() + pullDirX * 6));
                attachedTarget.setY((int)(attachedTarget.getY() + pullDirY * 6));

                // Update posisi bullet mengikuti target
                this.x = targetCenterX;
                this.y = targetCenterY;

                // Update jarak saat ini untuk rendering rope
                this.currentDistance = Math.sqrt(
                        Math.pow(this.x - startX, 2) + Math.pow(this.y - startY, 2)
                );
            } else {
                // Target telah sampai ke player
                active = false;
            }
        } else if (extending) {
            // Memanjangkan lasso hingga jarak maksimum atau mengenai target
            if (currentDistance < maxDistance && !reachedMaxDistance) {
                x += directionX * speed;
                y += directionY * speed;
                currentDistance += speed;

                // Cek apakah sudah mencapai jarak maksimum
                if (currentDistance >= maxDistance) {
                    reachedMaxDistance = true;
                    extending = false;
                }
            } else {
                // Mulai menarik kembali jika tidak ada target yang terkena
                if (attachedTarget == null) {
                    extending = false;
                    retractLasso();
                }
            }
        } else {
            // Menarik lasso kembali ke player
            retractLasso();
        }
    }

    // Method untuk menarik lasso kembali ke player
    private void retractLasso() {
        // Menghitung arah kembali ke player
        int playerCenterX = player.getX() + 24;
        int playerCenterY = player.getY() + 24;

        double distanceToPlayer = Math.sqrt(
                Math.pow(playerCenterX - x, 2) + Math.pow(playerCenterY - y, 2)
        );

        if (distanceToPlayer > 10) {
            double retractDirX = (playerCenterX - x) / distanceToPlayer;
            double retractDirY = (playerCenterY - y) / distanceToPlayer;

            x += retractDirX * speed;
            y += retractDirY * speed;

            // Update jarak saat ini
            currentDistance = Math.sqrt(
                    Math.pow(x - startX, 2) + Math.pow(y - startY, 2)
            );
        } else {
            // Sudah sampai ke player, nonaktifkan bullet
            active = false;
        }
    }

    // Method untuk mengecek collision dengan target
    public boolean checkCollision(Target target) {
        if (!active || returning || !extending) return false;

        Rectangle bulletRect = new Rectangle((int)x - 2, (int)y - 2, 4, 4);
        Rectangle targetRect = new Rectangle(target.getX(), target.getY(), 48, 48);

        return bulletRect.intersects(targetRect);
    }

    // Method untuk menempelkan target ke bullet
    public void attachTarget(Target target, int playerX, int playerY) {
        this.attachedTarget = target;
        this.returning = true;
        this.extending = false;
        this.reachedMaxDistance = true;

        // Update posisi awal untuk rendering rope
        this.startX = playerX + 24;
        this.startY = playerY + 24;
    }

    // Method untuk mendapatkan segmen rope untuk rendering
    public Point[] getRopeSegments(int playerCenterX, int playerCenterY) {
        int segments = Math.max(1, (int)(currentDistance / 20)); // Satu segmen per 20 pixel
        Point[] points = new Point[segments + 1];

        points[0] = new Point(playerCenterX, playerCenterY);

        for (int i = 1; i <= segments; i++) {
            double ratio = (double)i / segments;
            int segmentX = (int)(playerCenterX + (x - playerCenterX) * ratio);
            int segmentY = (int)(playerCenterY + (y - playerCenterY) * ratio);
            points[i] = new Point(segmentX, segmentY);
        }

        return points;
    }

    // Getter methods
    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }

    public double getPreciseX() {
        return x;
    }

    public double getPreciseY() {
        return y;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Target getAttachedTarget() {
        return attachedTarget;
    }

    public boolean isReturning() {
        return returning;
    }

    public boolean isExtending() {
        return extending;
    }

    public double getCurrentDistance() {
        return currentDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}