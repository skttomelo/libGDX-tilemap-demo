package com.tigames.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

public class Driver extends ApplicationAdapter {
	JSONObject mapObj;
	Scanner reader;
	World map; // level
	OrthographicCamera cam; // camera for view world
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth(); // screen width
		float h = Gdx.graphics.getHeight(); // screen height
		
		cam = new OrthographicCamera(30, 30 * (h / w)); // no idea how this will look, but we'll see
		cam.position.set(cam.viewportWidth/2f, 0, 0);
		cam.update();
		
		map = new World(loadMap());
	}

	@Override
	public void render () {
		handleInput();
		cam.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		map.render(cam);
	}
	
	@Override
	public void dispose () {
		map.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = 30;  //We will see width/32f units!
		cam.viewportHeight = 30 * height/width;
		cam.update();
	}
	
	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.translate(-3, 0, 0);
			//If the LEFT Key is pressed, translate the camera -3 units in the X-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.translate(3, 0, 0);
			//If the RIGHT Key is pressed, translate the camera 3 units in the X-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cam.translate(0, -3, 0);
			//If the DOWN Key is pressed, translate the camera -3 units in the Y-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cam.translate(0, 3, 0);
			//If the UP Key is pressed, translate the camera 3 units in the Y-Axis
		}
		
		cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
	}

	// read in json file for level data
	// to be past off to a JSONObject
	private ArrayList<Tile> loadMap() {
		ArrayList<Tile> tile_arraylist = new ArrayList<Tile>();
		String level_data = readFile("level.json");
		
		mapObj = new JSONObject(level_data);
		JSONArray tile = mapObj.getJSONArray("tiles");
		for(int i = 0; i < tile.length(); i++) {
			// gets current tile in the array
			JSONObject data = tile.getJSONObject(i);
			
			tile_arraylist.add(new Tile(data.getString("block-type"), (float) data.getInt("x"), (float) data.getInt("y")));
		}
		
		return tile_arraylist;
	}
	
	// Used for reading in file data from txt/json
	// not for reading in image data
	private String readFile(String file_dest) {
		String data = "";
		try {
			
			reader = new Scanner(new File(file_dest));
			
			while(reader.hasNextLine()) {
				data += reader.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
}

enum TileType{
	FLOOR,
	WATER
}

class Tile{
	private float x, y;
	private TileType type;
	public Tile(String tile_type, float x, float y) {
		switch(tile_type) {
		case "water":
			type = TileType.WATER;
			break;
		default:
			type = TileType.FLOOR;
			break;
		}
		this.x = x;
		this.y = y;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public TileType getType() {
		return type;
	}
}

class World{
	ArrayList<Tile> tiles;
	ShapeRenderer SR;
	
	public World(ArrayList<Tile> tiles){
		this.tiles = tiles;
		SR = new ShapeRenderer();
	}
	
	public void render(OrthographicCamera cam) {
		SR.setProjectionMatrix(cam.combined);
		// render all tiles on screen
		for(Tile tile : tiles) {
			SR.begin(ShapeType.Filled);

			// set the tile color
			switch(tile.getType()) {
			case WATER:
				SR.setColor(Color.BLUE);
				break;
			default:
				SR.setColor(Color.GREEN);
				break;
			}
			
			SR.rect(tile.getX()*1.5f, tile.getY()*1.5f, 1.5f, 1.5f);
			SR.end();
		}
	}
	
	public void dispose() {
		SR.dispose();
	}
	
}

