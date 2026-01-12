/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Basket {
    private int x, y;
    private BufferedImage sprite;
    private final int width = 48;
    private final int height = 48;

    public Basket(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    // load asset sprite
    private void loadSprite() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/tiles/basket.png"));
        } catch (IOException e) {
            sprite = null;
        }
    }

    // cek posisi target ada di koordinat basket
    public boolean isInside(int objectX, int objectY) {
        return objectX >= x && objectX <= x + width &&
                objectY >= y && objectY <= y + height;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public BufferedImage getSprite() { return sprite; }
}