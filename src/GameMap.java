import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Scanner;

public class GameMap {
	public String difficulty;
	public int width;
	public int height;

	public float[][] elevation;
	public float[][] moisture;

	public Terrain[][] terrainGrid;
	
	// might not be necessary
	public BufferedImage elevationImage;
	public BufferedImage moistureImage;

	// colors to mark what items are where
	// public static final String RESET = "\u001B[0m"; // default
	// public static final String RED   = "\u001B[31m"; // food
	// public static final String GREEN = "\u001B[32m"; // trader
	// public static final String YELLOW = "\u001B[33m"; // gold
	// public static final String BLUE  = "\u001B[34m"; // water

	// emojis to mark what items are where
	public static final String RESET = "\u001B[0m"; // default
	public static final String RED   = "🌮"; // food
	public static final String GREEN = "👤"; // trader
	public static final String YELLOW = "💰"; // gold
	public static final String BLUE = "💧"; // water
	
	public GameMap(int width, int height){
		this.width = width;
		this.height = height;

		elevation = createNoiseMap(width, height);
		moisture = createNoiseMap(width, height);

		elevationImage = createMapImage(elevation);
		moistureImage = createMapImage(moisture);

		//each cell will hold a terrain
		terrainGrid = new Terrain[height][width];
		generateTerrainGrid();
	}
	
	public static GameMap askForSize(Scanner userInput){
		int width = 0;
		int height = 0;
		boolean valid = false;

		while(!valid){
			System.out.println("What size map would you like?");
			System.out.println("1) 5x5\n" + "2) 10x10\n" + "3) 15x15");

			if(userInput.hasNextInt()){
				int sizeOption = userInput.nextInt();
				userInput.nextLine();
				switch(sizeOption){
					case 1:
						width = 5;
						height = 5;
						valid = true;
						break;
					case 2:
						width = 10;
						height = 10;
						valid = true;
						break;
					case 3:
						width = 15;
						height = 15;
						valid = true;
						break;
					default:
						System.out.println("Please choose an option");

				}
			} else {
				System.out.println("Invalid input, please enter a number.");
				userInput.next();
			}
		}
		return new GameMap(width, height);
	}
	
	public void askForDifficulty(Scanner userInput){
		boolean valid = false;

		while(!valid){
			System.out.println("Please choose a difficulty: Easy, Medium, Hard");
			String input = userInput.nextLine().trim();

			if(input.equalsIgnoreCase("Easy") ||
				input.equalsIgnoreCase("Medium") ||
				input.equalsIgnoreCase("Hard")) {
					valid = true;
					this.difficulty = input;
				} else {
					System.out.println("Invalid input, try again.");
				}
		}
	}

	public float[][] createNoiseMap(int width, int height) {
		Random random = new Random();
		int seed = random.nextInt(10000);

		float[][]noiseMap = new float[height][width];
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
		noise.SetSeed(seed);

		for (int y = 0; y < height; y++) {
		for(int x = 0; x < width; x++) {

			float nx = (float)x / width - 0.5f;
			float ny = (float)y / height - 0.5f;

			float frequency = 500.0f;
			float scaledNx = nx * frequency;
			float scaledNy = ny * frequency;


			float rawNoise  = 1.0f * noise.GetNoise(1 * scaledNx, 1 * scaledNy) 
			+  0.5f * noise.GetNoise(2 * scaledNx, 2 * scaledNy) 
			+ 0.25f * noise.GetNoise(4 * scaledNx, 4 * scaledNy);

			float normalizedNoise = (rawNoise / (1.0f + 0.5f + 0.25f) + 1) / 2;

			noiseMap[y][x] = (float)Math.pow(normalizedNoise, 1.5);
		}
		}
		return noiseMap;
	}

	//used logic from realTerrain but instead of printing terrain,
	//I store it in terrainGrid
	private void generateTerrainGrid() {

		// default moisture for desert and plain requirements
		float desertMoisture = 0.35f;
		float plainsMoisture = 0.45f;

		if (difficulty != null && difficulty.equalsIgnoreCase("Medium")) {
			// Medium difficulty, slightly higher chance for desert terrain
			desertMoisture = desertMoisture + 0.05f;
		} else if (difficulty != null && difficulty.equalsIgnoreCase("Hard")) {
			// Hard difficulty, bigger chance for desert terrain
			// Slightly Lower chance for plain terrain
			desertMoisture = desertMoisture + 0.10f;
			plainsMoisture = plainsMoisture - 0.05f;
		}

		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				float e = elevation[y][x];
				float m = moisture [y][x];

				Terrain t;

				if (e < 0.35f) {//f just converts 0.35 to float
					if (m < desertMoisture) {
						terrainGrid[y][x] = new Desert();
					}
					else if (m < plainsMoisture) {
						terrainGrid[y][x] = new Plains();
					}
					else {
						terrainGrid[y][x] = new River();
					}
				}
				else if (e < 0.45f) {
					if (m < plainsMoisture){
						terrainGrid[y][x] = new Plains();
					}
					else {
						terrainGrid[y][x] = new Swamp();
					}
				}
				else {
					terrainGrid[y][x] = new Mountain();
				}
			}
		}
	}

	//helper function to access terrain
	public Terrain getTerrainAt(int x, int y) {
		//check if coordinates are out of bounds
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return null;
		}
		return terrainGrid[y][x];
	}
	
	//Just prints the noise map of what is passed in.
	public void printMap(float[][] map) {
		System.out.println();
		System.out.println("Start:");
		for (int y = 0; y < map.length; y++) {
		for(int x = 0; x < map[y].length; x++) {
			System.out.print(map[y][x] + " ");
		}
		System.out.println();
		}
	}

	// Able to display key whenever
	public void showKey(){
		System.out.println("Key");
		System.out.println("---------------");
		System.out.println(RED + " = Food         P = Plains");
		System.out.println(GREEN + " = Trader       M = Mountains");
		System.out.println(YELLOW + " = Gold         R = River");
		System.out.println(BLUE + " = Water        S = Swamp");
		System.out.println("                  D = Desert");
	}
	
	//Takes the elevation and moisture of each square and decides what terrain it is.
	public void realTerrain(float[][] elevation, float[][] moisture) {

		// default moisture for desert and plain requirements
		float desertMoisture = 0.35f;
		float plainsMoisture = 0.45f;

		if (difficulty != null && difficulty.equalsIgnoreCase("Medium")) {
			// Medium difficulty, slightly higher chance for desert terrain
			desertMoisture = desertMoisture + 0.05f;
		} else if (difficulty != null && difficulty.equalsIgnoreCase("Hard")) {
			// Hard difficulty, bigger chance for desert terrain
			// Slightly Lower chance for plain terrain
			desertMoisture = desertMoisture + 0.10f;
			plainsMoisture = plainsMoisture - 0.05f;
		}

		int width = elevation[0].length;
		int height = elevation.length;
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {

			String biome;

			if(elevation[y][x] < 0.35){
				if(moisture[y][x] < desertMoisture){
					biome = "D"; // desert
				} else if(moisture[y][x] < plainsMoisture){
					biome = "P"; // plains
				} else {	// moisture <= 1
					biome = "R"; // river
				}
			} else if(elevation[y][x] < 0.45){
				if(moisture[y][x] < plainsMoisture){
					biome = "P"; // plains
				} else {	// moisture < 1
					biome = "S"; // swamp
				}
			} else {	// elevation < 1, moisture < 0.45 || moisture < 1
				biome = "M"; // mountain
			}
			String item = randomItem();
			
			if(item.equals("\u001B[0m")){
				item = " ";
			}
			System.out.printf("%-2s %-3s", item, biome);
		}
		System.out.println();
		}
	}

	//These two are for testing to see distribution of the terrain types.
	public void elevationToTerrain(float[][] map) {

		int width = map[0].length;
		int height = map.length;
		
		System.out.println();
		for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {

			if (map[y][x] < 0.35) {
			System.out.print("Low ");
			}
			else if (map[y][x] < 0.45) {
			System.out.print("Mid ");
			}
			else if (map[y][x] <= 1) {
			System.out.print("High ");
			}

		}
		System.out.println();
		}
	}

	public void moistureToTerrain(float[][] map) {

		int width = map[0].length;
		int height = map.length;
		
		System.out.println();
		for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {

			if (map[y][x] < 0.30) {
			System.out.print("Dry  ");
			}
			else if (map[y][x] < 0.45) {
			System.out.print("Mild ");
			}
			else if (map[y][x] <= 1) {
			System.out.print("Wet  ");
			}

		}
		System.out.println();
		}
	}

	public BufferedImage getElevationImage() {
		return elevationImage;
	}
	public BufferedImage getMoistureImage() {
		return moistureImage;
	}

	//These are just for turning the noise into the black and white picture.
	int toRGB(float value) {
		int part = Math.round(value * 255);
		return part * 0x10101;
	}

	public BufferedImage createMapImage(float[][] map) {
		int width = map[0].length;
		int height = map.length;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			image.setRGB(j, i, toRGB(map[i][j]));
		}
		}

		return image;
	}

	public String randomItem() {
		Random itemGen = new Random();
		String[] items = {RESET, RED, GREEN, BLUE, YELLOW };
		return items[itemGen.nextInt(items.length)];
	}
}
