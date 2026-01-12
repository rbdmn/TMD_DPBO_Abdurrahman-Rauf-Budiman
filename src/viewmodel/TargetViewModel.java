/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package viewmodel;

import model.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TargetViewModel {

    private final List<Target> targets = new ArrayList<>(); // Daftar target yang sedang aktif di layar
    private final int[] targetRows = {1, 2, 9, 10}; // Baris-baris tempat target bisa muncul (berdasarkan indeks tile vertikal)
    private final int tileSize; // Ukuran tile dalam piksel
    private final int panelWidth; // Lebar panel game (digunakan untuk mengetahui batas layar)
    private int spawnCounter = 0; // Counter frame untuk mengatur waktu spawn target
    private final Random random = new Random(); // Objek Random untuk menghasilkan angka acak
    private final int minSpacing = 120; // Jarak minimal antar target di baris yang sama agar tidak terlalu rapat
    private final int maxTargets = 20; // Jumlah maksimal target yang diizinkan tampil di layar

    // Konstruktor untuk inisialisasi awal
    public TargetViewModel(int panelWidth, int tileSize) {
        this.panelWidth = panelWidth;
        this.tileSize = tileSize;
    }

    /**
     * Memperbarui semua target dan mencoba membuat target baru jika waktunya sudah tepat
     */
    public void updateAll(int panelWidth) {
        // Iterator digunakan agar dapat menghapus elemen sambil iterasi
        Iterator<Target> iterator = targets.iterator();
        while (iterator.hasNext()) {
            Target target = iterator.next();

            // Hapus target jika sudah dikumpulkan
            if (target.isCollected()) {
                iterator.remove();
                continue;
            }

            // Update posisi target berdasarkan arah geraknya
            target.update(panelWidth);

            // Hapus target jika sudah keluar jauh dari layar dan tidak sedang ditarik
            if ((target.getX() < -100 && !target.isBeingPulled()) ||
                    (target.getX() > panelWidth + 100 && !target.isBeingPulled())) {
                iterator.remove();
            }
        }

        // Naikkan counter frame, lalu spawn target baru jika waktunya dan jumlah belum melebihi batas
        spawnCounter++;
        if (spawnCounter >= 30 && targets.size() < maxTargets) { // Setiap ~1.5 detik jika 60fps
            trySpawnTarget();
            spawnCounter = 0;
        }
    }

    /**
     * Coba membuat target baru secara acak di salah satu baris yang tersedia
     */
    private void trySpawnTarget() {
        // Pilih baris acak dari daftar targetRows
        int rowIndex = random.nextInt(targetRows.length);
        int row = targetRows[rowIndex];

        // Hitung posisi Y berdasarkan baris dan ukuran tile
        int y = row * tileSize;

        // Tentukan arah gerak berdasarkan baris (atas ke kanan, bawah ke kiri)
        boolean moveRight = (row == 1 || row == 2);

        // Tentukan posisi X awal berdasarkan arah gerak (masuk dari kiri atau kanan)
        int spawnX = moveRight ? -48 : panelWidth;

        // Cek apakah terlalu dekat dengan target lain di baris yang sama
        for (Target existing : targets) {
            if (existing.getY() == y && Math.abs(existing.getX() - spawnX) < minSpacing) {
                return; // Terlalu dekat, batal spawn
            }
        }

        // Beri kemungkinan 33% untuk benar-benar spawn (mencegah terlalu sering)
        if (random.nextInt(3) == 0) {
            targets.add(new Target(spawnX, y, moveRight));
        }
    }

    /**
     * Mengembalikan daftar target yang saat ini ada di layar
     */
    public List<Target> getTargets() {
        return targets;
    }
}
