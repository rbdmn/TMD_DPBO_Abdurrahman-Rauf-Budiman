/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package model;

import database.T_Hasil;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int score = 0; // Skor total yang diperoleh player
    private int count = 0; // Jumlah target yang berhasil dimasukkan ke basket
    private List<Target> collectedTargets = new ArrayList<>(); // Daftar target yang telah dikumpulkan player
    private Target heldTarget = null; // Target yang sedang dipegang player
    private String username; // Username player untuk menyimpan ke database
    private List<Target> activeTargets = new ArrayList<>(); // Daftar target yang masih aktif di game
    public GameState() {} // Constructor default

    public GameState(String username) { // Constructor dengan parameter username
        this.username = username;
    }

    // getter setter
    public void setUsername(String username) {
        this.username = username;
    }

    // getter setter
    public String getUsername() {
        return username;
    }

    // Method untuk menambah skor
    public void addScore(int points) {
        this.score += points;
    }

    // Method untuk menambah counter target yang berhasil dimasukkan
    public void incrementCount() {
        this.count++;
    }

    // Method untuk mengumpulkan target
    public void collectTarget(Target target) {
        collectedTargets.add(target);
        if (heldTarget == null) {
            heldTarget = target;
        }
    }

    // Method untuk memasukkan target ke basket dan menambah skor
    public void scoreTarget() {
        if (heldTarget != null) {
            // Memutar suara basket saat target berhasil dimasukkan
            playBasketSound();

            addScore(heldTarget.getValue());
            incrementCount();
            collectedTargets.remove(heldTarget);

            // Ambil target berikutnya jika tersedia
            if (!collectedTargets.isEmpty()) {
                heldTarget = collectedTargets.get(0);
            } else {
                heldTarget = null;
            }
        }
    }

    // Method untuk memutar suara basket
    private void playBasketSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/basket.wav")
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

    // Method untuk spawn target baru ke dalam game
    public void spawnTargets(int count, int panelWidth) {
        for (int i = 0; i < count; i++) {
            int x = (int) (Math.random() * panelWidth);
            int y = 100 + (int) (Math.random() * 200);
            boolean moveRight = Math.random() < 0.5;

            Target newTarget = new Target(x, y, moveRight);
            activeTargets.add(newTarget);
        }
    }

    // Method untuk update semua target aktif
    public void updateTargets(int panelWidth) {
        for (Target t : activeTargets) {
            t.update(panelWidth);
        }
    }

    public List<Target> getActiveTargets() {
        return activeTargets;
    }

    // Method untuk menghapus target yang sedang dipegang (untuk animasi lempar)
    public void removeHeldTarget() {
        if (heldTarget != null) {
            collectedTargets.remove(heldTarget);

            // Ambil target berikutnya jika tersedia
            if (!collectedTargets.isEmpty()) {
                heldTarget = collectedTargets.get(0);
            } else {
                heldTarget = null;
            }
        }
    }

    // Method untuk mengecek apakah masih ada target untuk dilempar
    public boolean hasTargetsToThrow() {
        return heldTarget != null;
    }

    // Method untuk memutar suara berhasil simpan data
    private void playSuccessSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/gameover.wav")
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
            // Jika gagal, tetap lanjut tanpa suara
        }
    }


    // Method untuk menyimpan hasil ke database
    public void saveResultToDatabase() {
        if (username != null && !username.trim().isEmpty()) {
            T_Hasil hasilModel = new T_Hasil();
            hasilModel.saveOrUpdateScore(username, score, count);
            playSuccessSound(); // putar suara saat berhasil simpan
        }
    }


    // Getter methods
    public int getScore() {
        return score;
    }

    public int getCount() {
        return count;
    }

    public Target getHeldTarget() {
        return heldTarget;
    }

    public List<Target> getCollectedTargets() {
        return collectedTargets;
    }
}