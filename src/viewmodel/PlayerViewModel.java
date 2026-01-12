/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package viewmodel;

import model.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerViewModel implements KeyListener, MouseListener {
    private final Player player; // Objek pemain
    private final TileManager tileManager; // Manajer tile/peta
    private final GameState gameState; // Status permainan (skor, target yang dimiliki, dll)
    private final int tileSize = 48; // Ukuran tiap tile dalam pixel

    private int lassoCooldown = 0; // Counter cooldown sebelum menembak ulang
    private final int LASSO_COOLDOWN_TIME = 5; // Lama cooldown dalam frame (lebih kecil = lebih cepat bisa nembak lagi)

    // Tombol arah yang sedang ditekan
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    private GameEndCallback gameEndCallback; // Callback jika game diakhiri dengan tombol SPACE

    private final List<Bullet> bullets = new ArrayList<>(); // Daftar peluru (lasso) aktif
    private final List<ThrowingTarget> throwingTargets = new ArrayList<>(); // Daftar target yang sedang dilempar

    // Interface untuk callback saat game berakhir
    public interface GameEndCallback {
        void onGameEnd();
    }

    // Konstruktor
    public PlayerViewModel(Player player, TileManager tileManager, GameState gameState) {
        this.player = player;
        this.tileManager = tileManager;
        this.gameState = gameState;
    }

    public void setGameEndCallback(GameEndCallback callback) {
        this.gameEndCallback = callback;
    }

    // Update logika player setiap frame
    public void update(List<Target> targets) {
        if (lassoCooldown > 0) {
            lassoCooldown--;
        }

        updateMovement(); // Update gerakan
        updateBullets(targets); // Update peluru
        updateThrowingTargets(); // Update animasi lempar target
    }

    // Menggerakkan player jika tombol arah ditekan dan tidak menabrak
    private void updateMovement() {
        int newX = player.getX();
        int newY = player.getY();
        boolean moved = false;

        if (upPressed) {
            newY -= player.getSpeed();
            player.setDirection("up");
            moved = true;
        } else if (downPressed) {
            newY += player.getSpeed();
            player.setDirection("down");
            moved = true;
        } else if (leftPressed) {
            newX -= player.getSpeed();
            player.setDirection("left");
            moved = true;
        } else if (rightPressed) {
            newX += player.getSpeed();
            player.setDirection("right");
            moved = true;
        }

        if (!moved) return;

        // Validasi agar player tidak keluar dari peta
        int mapWidth = tileManager.getMapWidth();
        int mapHeight = tileManager.getMapHeight();
        if (newX < 0 || newY < 0 || newX + tileSize > mapWidth || newY + tileSize > mapHeight) return;

        // Validasi agar player tidak menabrak tile penghalang
        int left = newX;
        int right = newX + tileSize - 1;
        int top = newY;
        int bottom = newY + tileSize - 1;

        int leftTileX = left / tileSize;
        int rightTileX = right / tileSize;
        int topTileY = top / tileSize;
        int bottomTileY = bottom / tileSize;

        boolean blocked =
                tileManager.isCollisionTile(leftTileX, topTileY) ||
                        tileManager.isCollisionTile(rightTileX, topTileY) ||
                        tileManager.isCollisionTile(leftTileX, bottomTileY) ||
                        tileManager.isCollisionTile(rightTileX, bottomTileY);

        if (!blocked) {
            player.setX(newX);
            player.setY(newY);
            player.updateAnimation();
        }
    }

    // Update posisi peluru dan pengecekan tabrakan dengan target
    private void updateBullets(List<Target> targets) {
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            if (!bullet.isActive()) {
                if (bullet.getAttachedTarget() != null) {
                    Target target = bullet.getAttachedTarget();
                    target.setCollected(true);
                    gameState.collectTarget(target);
                    targets.remove(target);
                }
                bulletIterator.remove();
                continue;
            }

            // Cek tabrakan dengan target hanya saat lasso masih memanjang
            if (bullet.isExtending() && !bullet.isReturning()) {
                for (Target target : targets) {
                    if (!target.isCollected() && bullet.checkCollision(target)) {
                        target.setBeingPulled(true);
                        bullet.attachTarget(target, player.getX(), player.getY());
                        break;
                    }
                }
            }

            // Safety check: hapus peluru jika keluar layar
            if (bullet.getX() < -200 || bullet.getX() > tileManager.getMapWidth() + 200 ||
                    bullet.getY() < -200 || bullet.getY() > tileManager.getMapHeight() + 200) {
                bulletIterator.remove();
            }
        }
    }

    // Memutar suara dari resource (dipanggil saat target dilempar masuk basket)
    private void playSoundFromResource(String resourcePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(resourcePath)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Update animasi/gerak target yang sedang dilempar
    private void updateThrowingTargets() {
        Iterator<ThrowingTarget> iterator = throwingTargets.iterator();
        while (iterator.hasNext()) {
            ThrowingTarget t = iterator.next();
            t.update();

            if (!t.isActive()) {
                gameState.addScore(t.getValue());
                gameState.incrementCount();

                // Putar suara saat target masuk basket
                playSoundFromResource("/audio/basket.wav");

                iterator.remove();
            }
        }
    }

    // Menembakkan lasso (jika belum cooldown dan tidak ada lasso aktif)
    public void shoot(int targetX, int targetY) {
        boolean hasActiveBullet = bullets.stream().anyMatch(Bullet::isActive);
        if (lassoCooldown <= 0 && !hasActiveBullet) {
            int playerCenterX = player.getX() + tileSize / 2;
            int playerCenterY = player.getY() + tileSize / 2;

            Bullet bullet = new Bullet(playerCenterX, playerCenterY, targetX, targetY);
            bullet.setPlayer(player);
            bullets.add(bullet);

            lassoCooldown = LASSO_COOLDOWN_TIME;
        }
    }

    // Melempar target yang sedang dibawa ke keranjang
    public void throwTargetToBasket(int clickX, int clickY) {
        if (gameState.hasTargetsToThrow()) {
            Basket targetBasket = null;
            for (Basket b : tileManager.getBaskets()) {
                if (b.isInside(clickX, clickY)) {
                    targetBasket = b;
                    break;
                }
            }

            if (targetBasket != null) {
                Target heldTarget = gameState.getHeldTarget();
                int playerCenterX = player.getX() + tileSize / 2;
                int playerCenterY = player.getY() + tileSize / 2;

                ThrowingTarget t = new ThrowingTarget(
                        playerCenterX, playerCenterY,
                        targetBasket.getX(), targetBasket.getY(),
                        heldTarget
                );
                throwingTargets.add(t);
                gameState.removeHeldTarget();
            }
        }
    }

    // Getter
    public Player getPlayer() { return player; }
    public List<Bullet> getBullets() { return bullets; }
    public List<ThrowingTarget> getThrowingTargets() { return throwingTargets; }

    // Event ketika tombol ditekan
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> upPressed = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> downPressed = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> leftPressed = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = true;
            case KeyEvent.VK_SPACE -> {
                if (gameEndCallback != null) {
                    gameEndCallback.onGameEnd();
                }
            }
        }
    }

    // Event ketika tombol dilepas
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> upPressed = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> downPressed = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> leftPressed = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    // Event ketika mouse diklik
    @Override
    public void mouseClicked(MouseEvent e) {}

    // Event saat mouse ditekan: melempar atau menembak
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameState.hasTargetsToThrow()) {
            throwTargetToBasket(e.getX(), e.getY());
        } else {
            shoot(e.getX(), e.getY());
        }
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
