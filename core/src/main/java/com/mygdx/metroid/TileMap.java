package com.mygdx.metroid;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.metroid.jsonloaders.LevelJson;

public class TileMap {
    public static final int TILE_SIZE = 64;
    public int mapWidth;
    public int mapHeight;
    public int[][] tiles;
    private AssetManager manager;
    private SpriteBatch batch;
    public int scrollX;

    // Campos para el tileset
    private Texture tilesetTexture;
    private TextureRegion[] tileRegions;
    private int tilesetColumns;
    private int tilesetRows;

    public TileMap(AssetManager manager, SpriteBatch batch) {
        this.manager = manager;
        this.batch = batch;

        // Se asume que ya se ha cargado el tileset en el AssetManager
        // Asegúrate de haber cargado "tileset.png" previamente (por ejemplo, en el LoadingScreen)
        tilesetTexture = manager.get("gothicvania church files/Assets/ENVIRONMENT/tileset.png", Texture.class);

        // Calcula cuántos tiles hay en cada fila y columna del tileset
        tilesetColumns = tilesetTexture.getWidth() / TILE_SIZE;
        tilesetRows = tilesetTexture.getHeight() / TILE_SIZE;

        // Divide la textura en una matriz de TextureRegions
        TextureRegion[][] tmp = TextureRegion.split(tilesetTexture, TILE_SIZE, TILE_SIZE);
        tileRegions = new TextureRegion[tilesetRows * tilesetColumns];
        int index = 0;
        for (int row = 0; row < tilesetRows; row++) {
            for (int col = 0; col < tilesetColumns; col++) {
                tileRegions[index++] = tmp[row][col];
            }
        }
    }

    // Lee los datos del nivel desde el objeto LevelJson
    public void loadFromLevel(LevelJson level) {
        this.mapWidth = level.getMapWidth();
        this.mapHeight = level.getMapHeight();
        this.tiles = level.getTileMap();
    }

    // Renderiza los tiles del mapa.
    // Se asume que en el JSON, un valor 0 indica espacio vacío,
    // y que un valor 1 corresponde al primer tile (tileRegions[0]), 2 al segundo, etc.
    public void render() {
        batch.begin();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileId = tiles[y][x];
                if (tileId != 0) {
                    // Resta 1 al tileId para indexar el array (si el JSON empieza en 1)
                    TextureRegion region = tileRegions[tileId - 1];
                    batch.draw(region, x * TILE_SIZE - scrollX, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        // No se destruye tilesetTexture ya que el AssetManager se encarga de su ciclo de vida.
    }
}
