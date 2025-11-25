import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.lang.Math;

public class GameMap {
	public String difficulty;
	public int width;
	public int height;

	public float[][] elevation;
	public float[][] moisture;
	
	// might not be necessary
	public BufferedImage elevationImage;
	public BufferedImage moistureImage;

	// colors to mark what items are where
	public static final String RESET = "\u001B[0m"; // default
	public static final String RED   = "\u001B[31m"; // food
	public static final String GREEN = "\u001B[32m"; // trader
	public static final String YELLOW = "\u001B[33m"; // gold
	public static final String BLUE  = "\u001B[34m"; // water

	public GameMap(int width, int height){
		this.width = width;
		this.height = height;

		elevation = createNoiseMap(width, height);
		moisture = createNoiseMap(width, height);

		// might not be needed
		elevationImage = createMapImage(elevation);
		moistureImage = createMapImage(moisture);
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
	
	//Just prints the noise map of what is passed in.
	public void printMap(float[][] map) {
		System.out.println();
		System.out.println("Start:");
		for (int x = 0; x < map.length; x++) {
		for(int y = 0; y < map[x].length; y++) {
			System.out.print(map[x][y] + " ");
		}
		System.out.println();
		}
	}

	//Takes the elevation and moisture of each square and decides what terrain it is.
	public void realTerrain(float[][] elevation, float[][] moisture) {

		int width = elevation[0].length;
		int height = elevation.length;
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {

			if (elevation[x][y] < 0.35) {

			if (moisture[x][y] < 0.35) {
				System.out.print(randomItem() + "Desert   " + RESET);
			}
			else if (moisture[x][y] < 0.45) {
				System.out.print(randomItem() + "Plains   " + RESET);
			}
			else if (moisture[x][y] <= 1) {
				System.out.print(randomItem() + "River    " + RESET);
			}

			}

			else if (elevation[x][y] < 0.45) {

			if (moisture[x][y] < 0.45) {
				System.out.print(randomItem() + "Plains   " + RESET);
			}
			else if (moisture[x][y] < 1) {
				System.out.print(randomItem() + "Swamp    " + RESET);
			}

			}

			else if (elevation[x][y] < 1) {

			if (moisture[x][y] < 0.45) {
				System.out.print(randomItem() + "Mountain " + RESET);
			}
			else if (moisture[x][y] < 1) {
				System.out.print(randomItem() + "Mountain " + RESET);
			}
			
			}
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

			if (map[x][y] < 0.35) {
			System.out.print("Low ");
			}
			else if (map[x][y] < 0.45) {
			System.out.print("Mid ");
			}
			else if (map[x][y] <= 1) {
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

			if (map[x][y] < 0.30) {
			System.out.print("Dry  ");
			}
			else if (map[x][y] < 0.45) {
			System.out.print("Mild ");
			}
			else if (map[x][y] <= 1) {
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
