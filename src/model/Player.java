/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity {
    private String direction; // Arah pergerakan player saat ini
    private int spriteCounter = 0; // Counter untuk animasi sprite
    private int spriteNum = 1; // Nomor sprite yang sedang ditampilkan (1 atau 2)

    // Sprite images untuk setiap arah dan frame animasi
    private BufferedImage up1, up2;       // Sprite untuk arah atas
    private BufferedImage down1, down2;   // Sprite untuk arah bawah
    private BufferedImage left1, left2;   // Sprite untuk arah kiri
    private BufferedImage right1, right2; // Sprite untuk arah kanan

    // Constructor untuk inisialisasi player
    public Player(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.direction = "down";
        loadSprites();
    }

    // Method untuk memuat semua sprite player
    private void loadSprites() {
        try {
            // Load sprite untuk arah atas
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_09.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_10.png"));

            // Load sprite untuk arah bawah
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_01.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_02.png"));

            // Load sprite untuk arah kiri
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_05.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_06.png"));

            // Load sprite untuk arah kanan
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_03.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/l0_sprite_04.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method untuk update animasi sprite
    public void updateAnimation() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    // Method untuk mendapatkan sprite yang sedang aktif berdasarkan arah dan frame
    public BufferedImage getCurrentSprite() {
        return switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };
    }

    // Getter dan setter untuk direction
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    // Methods untuk pergerakan player ke berbagai arah
    public void moveUp() {
        y -= speed;
        direction = "up";
    }

    public void moveDown() {
        y += speed;
        direction = "down";
    }

    public void moveLeft() {
        x -= speed;
        direction = "left";
    }

    public void moveRight() {
        x += speed;
        direction = "right";
    }
}