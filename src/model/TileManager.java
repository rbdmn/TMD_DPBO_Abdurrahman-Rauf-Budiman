/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TileManager {
    private final Tile[] tiles;               // Array untuk menyimpan berbagai jenis tile
    private final int[][] mapTileNum;         // Matriks yang menyimpan nomor tile untuk tiap posisi peta
    private final List<Basket> baskets;       // List untuk menyimpan posisi basket di peta

    /**
     * Konstruktor: inisialisasi array tile, matriks peta, dan list basket
     * @param maxCols jumlah kolom maksimal di peta
     * @param maxRows jumlah baris maksimal di peta
     */
    public TileManager(int maxCols, int maxRows) {
        tiles = new Tile[10];
        mapTileNum = new int[maxCols][maxRows];
        baskets = new ArrayList<>();
        loadTileImages(); // Load semua gambar tile ke array
    }

    // Getter untuk array tile
    public Tile[] getTiles() { return tiles; }

    // Getter untuk matriks peta
    public int[][] getMap() { return mapTileNum; }

    // Getter untuk list basket
    public List<Basket> getBaskets() { return baskets; }

    /**
     * Fungsi untuk memuat semua gambar tile ke dalam array tiles[]
     * dan mengatur status collision jika diperlukan
     */
    private void loadTileImages() {
        try {
            tiles[0] = new Tile(); // tanah
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tanah.png"));

            tiles[1] = new Tile(); // air atas
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/air_atas.png"));
            tiles[1].collision = true;

            tiles[2] = new Tile(); // full air
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/full_air.png"));
            tiles[2].collision = true;

            tiles[3] = new Tile(); // basket
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/basket.png"));
            tiles[3].collision = true;

            tiles[4] = new Tile(); // pohon
            tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/pohon.png"));
            tiles[4].collision = false;

            tiles[5] = new Tile(); // air bawah
            tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/air_bawah.png"));
            tiles[5].collision = false;

            tiles[6] = new Tile(); // tanah dengan bunga
            tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tanah_bunga.png"));
            tiles[6].collision = false;

            tiles[7] = new Tile(); // jembatan
            tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/jembatan.png"));
            tiles[7].collision = false;

            tiles[8] = new Tile(); // jalan 02
            tiles[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/jalan02.png"));
            tiles[8].collision = false;

            tiles[9] = new Tile(); // jalan air
            tiles[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/jalan_air.png"));
            tiles[9].collision = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengecek apakah sebuah tile memiliki collision (tidak bisa dilewati)
     * @param tileX posisi kolom pada matriks peta
     * @param tileY posisi baris pada matriks peta
     * @return true jika tile merupakan penghalang (collision), false jika bisa dilewati
     */
    public boolean isCollisionTile(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= mapTileNum.length || tileY >= mapTileNum[0].length) {
            return true; // Di luar batas peta dianggap collision
        }
        int tileNum = mapTileNum[tileX][tileY];
        return tiles[tileNum].collision;
    }

    /**
     * Membaca peta dari file teks dan mengisi mapTileNum sesuai isi file
     * Juga membuat objek Basket untuk setiap tile bernilai 3 (basket)
     * @param filePath path ke file teks berisi peta
     */
    public void loadMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            int row = 0;
            String line;
            while ((line = br.readLine()) != null && row < mapTileNum[0].length) {
                String[] numbers = line.split(" ");
                for (int col = 0; col < numbers.length; col++) {
                    int tileNum = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = tileNum;

                    // Jika tile adalah basket (kode 3), buat objek basket dan simpan ke list
                    if (tileNum == 3) {
                        baskets.add(new Basket(col * 48, row * 48));
                    }
                }
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menghitung lebar peta dalam piksel
    public int getMapWidth() {
        return mapTileNum.length * 48;
    }

    // Menghitung tinggi peta dalam piksel
    public int getMapHeight() {
        return mapTileNum[0].length * 48;
    }
}
