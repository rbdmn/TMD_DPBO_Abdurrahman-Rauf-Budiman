
/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Target extends Entity{

    private final int speed; // Kecepatan pergerakan target
    private final boolean moveRight; // Arah pergerakan target (true = kanan, false = kiri)
    private final BufferedImage[] sprites = new BufferedImage[2]; // Array untuk menyimpan 2 frame sprite animasi
    private int spriteIndex = 0; // Index sprite yang sedang ditampilkan
    private int spriteCounter = 0; // Counter untuk timing animasi sprite
    private final int value; // Nilai poin yang didapat jika target berhasil ditangkap
    private boolean collected = false; // Status apakah target sudah dikumpulkan
    private boolean beingPulled = false; // Status apakah target sedang ditarik oleh bullet
    private String type; // Jenis ikan (untuk variasi visual)

    // Array tipe target yang tersedia
    private static final String[] targetTypes = {null, "ikan_merah", "ikan_hijau"}; // null = default

    // Constructor untuk membuat target baru
    public Target(int startX, int startY, boolean moveRight) {
        this.x = startX;
        this.y = startY;
        this.moveRight = moveRight;

        // Tentukan tipe ikan secara acak
        this.type = targetTypes[new Random().nextInt(targetTypes.length)];

        // Atur value dan speed berdasarkan tipe
        Random rand = new Random();
        if (type == null) { // default
            this.value = 20 + rand.nextInt(31); // 20–50
            this.speed = 3;
        } else if (type.equals("ikan_merah")) {
            this.value = 60 + rand.nextInt(41); // 60–100
            this.speed = 5;
        } else if (type.equals("ikan_hijau")) {
            this.value = 100 + rand.nextInt(101); // 100–200
            this.speed = 7;
        } else {
            this.value = 10; // fallback
            this.speed = 2;
        }

        loadSprites();
    }

    public String getType() {
        return type;
    }

    // Method untuk memuat sprite berdasarkan jenis dan arah gerakan
    private void loadSprites() {
        try {
            // Tentukan prefix berdasarkan jenis ikan
            String prefix = (type == null) ? "ikan" : type;

            if (moveRight) {
                // Load sprite untuk gerakan ke kanan
                sprites[0] = ImageIO.read(getClass().getResourceAsStream("/target/" + prefix + "_kanan1.png"));
                sprites[1] = ImageIO.read(getClass().getResourceAsStream("/target/" + prefix + "_kanan2.png"));
            } else {
                // Load sprite untuk gerakan ke kiri
                sprites[0] = ImageIO.read(getClass().getResourceAsStream("/target/" + prefix + "_kiri1.png"));
                sprites[1] = ImageIO.read(getClass().getResourceAsStream("/target/" + prefix + "_kiri2.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method untuk update posisi dan animasi target
    public void update(int panelWidth) {
        if (collected || beingPulled) return;

        if (moveRight) {
            x += speed;
            if (x > panelWidth) x = -48;
        } else {
            x -= speed;
            if (x + 48 < 0) x = panelWidth;
        }

        animate();
    }


    // Method untuk animasi sprite
    private void animate() {
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteIndex = (spriteIndex + 1) % sprites.length;
            spriteCounter = 0;
        }
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public BufferedImage getCurrentSprite() { return sprites[spriteIndex]; }
    public int getValue() { return value; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
    public boolean isBeingPulled() { return beingPulled; }
    public void setBeingPulled(boolean beingPulled) { this.beingPulled = beingPulled; }
}