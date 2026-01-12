/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

package viewmodel;

import model.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GameViewModel {
    private final PlayerViewModel playerVM;
    private final TileManager tileManager;
    private final TargetViewModel targetViewModel;
    private final GameState gameState;

    // View-related data that View can access
    private GameRenderData renderData;

    public GameViewModel(PlayerViewModel playerVM, TileManager tileManager,
                         TargetViewModel targetViewModel, GameState gameState) {
        this.playerVM = playerVM;
        this.tileManager = tileManager;
        this.targetViewModel = targetViewModel;
        this.gameState = gameState;
        this.renderData = new GameRenderData();
    }

    public void updateGameState() {
        targetViewModel.updateAll(768); // screen width = 16 tiles * 48
        playerVM.update(targetViewModel.getTargets());
        updateRenderData();
    }

    private void updateRenderData() {
        // Update render data yang dibutuhkan pada view
        renderData.playerSprite = playerVM.getPlayer().getCurrentSprite();
        renderData.playerX = playerVM.getPlayer().getX();
        renderData.playerY = playerVM.getPlayer().getY();

        // UI data
        renderData.score = gameState.getScore();
        renderData.count = gameState.getCount();
        renderData.username = gameState.getUsername();
        renderData.hasTargetsToThrow = gameState.hasTargetsToThrow();

        if (gameState.hasTargetsToThrow()) {
            renderData.heldTargetValue = gameState.getHeldTarget().getValue();
        }

        // Bullet/Lasso data
        renderData.bullets = playerVM.getBullets();
        renderData.throwingTargets = playerVM.getThrowingTargets();

        // Map data
        renderData.mapTiles = tileManager.getMap();
        renderData.tileSprites = tileManager.getTiles();
        renderData.baskets = tileManager.getBaskets();

        // Target data
        renderData.targets = targetViewModel.getTargets();
    }

    // Method untuk View mendapatkan data render
    public GameRenderData getRenderData() {
        return renderData;
    }

    // Metode untuk UI actions dari View
    public void endGame() {
        gameState.saveResultToDatabase();
    }

    // Getters untuk keperluan tertentu
    public PlayerViewModel getPlayerViewModel() {
        return playerVM;
    }

    public GameState getGameState() {
        return gameState;
    }

    // Inner class untuk data yang dibutuhkan View
    public static class GameRenderData {
        // Player data
        public BufferedImage playerSprite;
        public int playerX, playerY;

        // UI data
        public int score, count;
        public String username;
        public boolean hasTargetsToThrow;
        public int heldTargetValue;

        // Game objects
        public List<Bullet> bullets;
        public List<ThrowingTarget> throwingTargets;
        public List<Target> targets;
        public List<Basket> baskets;

        // Map data
        public int[][] mapTiles;
        public Tile[] tileSprites;
    }

    // buat game
    public static GameViewModel createGame(String username, int mapIndex) {
        Player player = new Player(100, 300, 4);
        TileManager tileManager = new TileManager(16, 12);

        String[] maps = {"/maps/map01.txt", "/maps/map02.txt"};
        tileManager.loadMap(maps[mapIndex]);

        GameState gameState = new GameState(username);
        TargetViewModel targetVM = new TargetViewModel(16 * 48, 48);
        PlayerViewModel playerVM = new PlayerViewModel(player, tileManager, gameState);

        return new GameViewModel(playerVM, tileManager, targetVM, gameState);
    }

}