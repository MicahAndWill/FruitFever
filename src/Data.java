/**
 *	Data - Loads images from webserver and stores them as GImages, loads level information.
 * 
 *	@Author Micah Stairs
 *
 **/

/**
To-do List 
-Make Fruit Rings spin at different spins
-Make buttons in level selection screen inactive when you can no longer change pages
**/

import acm.graphics.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.*;

public abstract class Data {
	
	public static final InformationStorer infoFile = new InformationStorer("levels/levelInformation");
	
	public static double loadingBarProgress = 0.0;
	
	public static final int TILE_SIZE = 25, TILESET_SIZE = 10;

	public static BufferedImage sheet = null;
	
	public static GImage 	loadingScreenBackground, loadingScreenBar, windowBorder, fruitFeverTitle,
							heartImage,
							sliderCircle, sliderCirclePressed, sliderBar, redX,
							purpleBallSmall, purpleBallBig, fireBallSmall, fireBallBig,
							checkpointFlagRed, checkpointFlagGreen,
							bronzeStar, silverStar, goldStar, starIcon, noStarIcon, locked,
							energyBar, energyBarBackground, healthBar, healthBarBackground,
							iconBackgroundBar;
						
	public static GImage[] 	sceneryImages = new GImage[30],
							
							gearButton = new GImage[3],
							fruitRingAnimation = new GImage[6],
							vortexAnimation = new GImage[5],
							powerups = new GImage[2],
							   
							playerStill = new GImage[1],
							playerStillH = new GImage[1],
							playerTongue = new GImage[5],
							playerTongueH = new GImage[5],
							playerShoot = new GImage[6],
							playerShootH = new GImage[6],
							  
							// fuzzyEnemyAttack = new GImage[4],
							// fuzzyEnemyAttackH = new GImage[4],
							// fuzzyEnemyMoving = new GImage[3],
							// fuzzyEnemyMovingH = new GImage[3],
							// fuzzyShot = new GImage[8],
							// wormEnemyMoving = new GImage[4],
							// wormEnemyMovingH = new GImage[4],
							   
							swirlAnimation = new GImage[6],
							
							gasBubbles = new GImage[4],
							playerGasBubbles = new GImage[4],
							lava = new GImage[1],
							spikesBlood = new GImage[1],
							spikesBloodV = new GImage[1],
							spikes = new GImage[1],
							spikesV = new GImage[1],
							musicButton = new GImage[2],
							soundEffectsButton = new GImage[2],
							hintSign = new GImage[3],
							refreshButton = new GImage[3],
							menuButtons = new GImage[12],
							leftArrowButton = new GImage[3],
							rightArrowButton = new GImage[3],
							levelButton = new GImage[2],
							
							buttonFrame = new GImage[3];

	public static GImage[][] 	fireworkAnimation = new GImage[3][5],
								fruits = new GImage [5][],
								torches = new GImage[3][3],
								torchesH = new GImage[3][3],
								blockImages = new GImage[19][4];
								
	public static GImage[][][] tileset = new GImage[3][3][3];

	/** Loads the images required for the loading screen **/
	public static void loadingScreen(){
		
		sheet = DataLoader.loadImage("img/loadingScreen/loadingScreen" + String.valueOf((int)(Math.random()*4)) + ".png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/loadingScreen/loadingScreen" + String.valueOf((int)(Math.random()*4)) + ".png");
		
		loadingScreenBackground = makeImage(0, 0, 700, 500 );
		loadingScreenBar = makeImage(0, 617, 2, 33);
	
	}
	    
	/** Loads all the images from the sprite sheet **/
	public static void loadImages(){
	
		updateLoadingBar(0.1);
		
		/** Blocks **/
		sheet = DataLoader.loadImage("img/sprites/blocks.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/blocks.png");
	
		for (int i = 0; i < blockImages.length; i++) {
			blockImages[i][0] = makeImage(TILE_SIZE*2, TILE_SIZE*i, TILE_SIZE, TILE_SIZE);
			for (int j = 1; j < 4; j++)
				blockImages[i][j] = ImageTransformer.rotateCounterClockwise(blockImages[i][j - 1]);
		}
		
		updateLoadingBar(0.1);
		
		/** Scenery **/
		sheet = DataLoader.loadImage("img/sprites/plants.png","https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/plants.png");

		// Scenery (Row 1)
		sceneryImages[0] = makeImage(0, TILE_SIZE, TILE_SIZE*2, TILE_SIZE);
		for (int i = 2; i < 7; i++)
			sceneryImages[i - 1] = makeImage(TILE_SIZE*i, TILE_SIZE, TILE_SIZE, TILE_SIZE);
		sceneryImages[6] = makeImage(TILE_SIZE*7, TILE_SIZE - 2, TILE_SIZE + 5, TILE_SIZE + 2);

		// Scenery (Row 2)
		for (int i = 0; i < 9; i++)
			sceneryImages[i + 7] = makeImage(TILE_SIZE*i, TILE_SIZE*2, TILE_SIZE, TILE_SIZE);

		// Scenery (Row 3)
		for (int i = 0; i < 9; i++)
			sceneryImages[i + 16] = makeImage(TILE_SIZE*i, TILE_SIZE*3, TILE_SIZE, TILE_SIZE);
		sceneryImages[24] = makeImage(TILE_SIZE*8, TILE_SIZE*3, TILE_SIZE*2, TILE_SIZE);
		
		// Scenery (Row 4)
		for (int i = 0; i < 3; i++)
			sceneryImages[i + 25] = makeImage(TILE_SIZE*i, TILE_SIZE*4, TILE_SIZE, TILE_SIZE);
		
		// Moss for blocks
		sceneryImages[28] = makeImage(TILE_SIZE*9, TILE_SIZE, TILE_SIZE, TILE_SIZE);
		sceneryImages[29] = makeImage(TILE_SIZE*10, TILE_SIZE, TILE_SIZE, TILE_SIZE);
		
		updateLoadingBar(0.1);
		
		/** Fruits **/
		sheet = DataLoader.loadImage("img/sprites/fruits.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/fruits.png");
		
		fruits[0] = new GImage[5];
		for (int i = 0; i < 5; i++)
			fruits[0][i] = makeImage(TILE_SIZE*i, 0, TILE_SIZE, TILE_SIZE);

		fruits[1] = new GImage[6];
		for (int i = 0; i < 6; i++)
			fruits[1][i] = makeImage(TILE_SIZE*i, TILE_SIZE, TILE_SIZE, TILE_SIZE);	
			
		fruits[2] = new GImage[7];
		for (int i = 0; i < 7; i++)
			fruits[2][i] = makeImage(TILE_SIZE*i, TILE_SIZE*2, TILE_SIZE, TILE_SIZE);
			
		fruits[3] = new GImage[20];
		fruits[4] = new GImage[20];
		for (int i = 0; i < 10; i++) {
			fruits[3][i] = makeImage(TILE_SIZE*i, TILE_SIZE*3, TILE_SIZE, TILE_SIZE);
			fruits[3][i + 10] = ImageTransformer.horizontalFlip(fruits[3][i]);

			fruits[4][i] = makeImage(TILE_SIZE*i, TILE_SIZE*4, TILE_SIZE, TILE_SIZE);
			fruits[4][i + 10] = ImageTransformer.horizontalFlip(fruits[4][i]);
		}
		
		
		updateLoadingBar(0.1);
		
		/** Miscellaneous **/
		sheet = DataLoader.loadImage("img/sprites/miscellaneous.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/miscellaneous.png");
		
		// Heart Image
		heartImage = makeImage(0, 0, TILE_SIZE, TILE_SIZE);
		
		// Gear Button Images
		for (int i = 0; i < 3; i++)
			gearButton[i] = makeImage(TILE_SIZE*(i + 1), 0, TILE_SIZE, TILE_SIZE);
			
		// Back Button Images
		int randomButtonColor = (int) (Math.random()*4);
		for (int i = 0; i < 3; i++)
			refreshButton[i] = makeImage(TILE_SIZE*randomButtonColor, TILE_SIZE*(i + 5), TILE_SIZE, TILE_SIZE);	
			
		// Lava Image
		lava[0] = makeImage(TILE_SIZE*7, 0, TILE_SIZE, TILE_SIZE);
			
		// Checkpoint Flags
		checkpointFlagRed = makeImage(0, TILE_SIZE, TILE_SIZE, TILE_SIZE*2);
		checkpointFlagGreen = makeImage(TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE*2);
			
		// Fruit Rings Animation Images
		for (int i = 0; i < 6; i++)
			fruitRingAnimation[i] = makeImage(TILE_SIZE*(i + 2), TILE_SIZE*4, TILE_SIZE, TILE_SIZE);
		
		// Vortex Animation Images
		for (int i = 0; i < 5; i++)
			vortexAnimation[i] = makeImage(TILE_SIZE*14, TILE_SIZE*i, TILE_SIZE*2, TILE_SIZE);
		
		// Powerup Blocks
		for (int i = 0; i < 2; i++)
			powerups[i] = makeImage(TILE_SIZE*(i + 11), 0, TILE_SIZE, TILE_SIZE);
	
		// Fireworks
		for (int n = 0; n < 3; n++) 
			for (int i = 0; i < 5; i++) 
				fireworkAnimation[n][i] = makeImage(TILE_SIZE*(i + 2), TILE_SIZE*(n + 1), TILE_SIZE, TILE_SIZE);
		
		// Torches
		for (int n = 0; n < 3; n++) 
			for (int i = 0; i < 3; i++) {
				torches[n][i] = makeImage(TILE_SIZE*(n*2 + 10), TILE_SIZE*(i + 5), TILE_SIZE, TILE_SIZE);
				torchesH[n][i] = ImageTransformer.horizontalFlip(torches[n][i]);
			}
				
		// Spikes
		spikesBlood[0] = makeImage(TILE_SIZE*4, TILE_SIZE*5, TILE_SIZE, TILE_SIZE);
		spikesBloodV[0] = ImageTransformer.verticalFlip(spikesBlood[0]);
		spikes[0] = makeImage(TILE_SIZE*5, TILE_SIZE*5, TILE_SIZE, TILE_SIZE);
		spikesV[0] = ImageTransformer.verticalFlip(spikes[0]);
				
		// Stars
		bronzeStar = makeImage(TILE_SIZE*8, 0, TILE_SIZE, TILE_SIZE);
		silverStar = makeImage(TILE_SIZE*8, TILE_SIZE*2, TILE_SIZE*2, TILE_SIZE*2);
		noStarIcon = ImageTransformer.resize(silverStar, 15, 15);
		goldStar = makeImage(TILE_SIZE*8, TILE_SIZE*4, TILE_SIZE*2, TILE_SIZE*2);
		starIcon = ImageTransformer.resize(goldStar, 15, 15);
		
		// Locked
		locked = makeImage(TILE_SIZE*10, TILE_SIZE, TILE_SIZE*2, TILE_SIZE*2);
		
		// Notes
		for (int i = 0; i < 2; i++) {
			soundEffectsButton[i] = makeImage(TILE_SIZE*i, TILE_SIZE*3, TILE_SIZE, TILE_SIZE);
			musicButton[i] = makeImage(TILE_SIZE*i, TILE_SIZE*4, TILE_SIZE, TILE_SIZE);
		}
		redX = makeImage(TILE_SIZE*7, TILE_SIZE*3, TILE_SIZE, TILE_SIZE);
		
		// Hint Sign (3 colors)
		for (int i = 0; i < 3; i++)
			hintSign[i] = makeImage(TILE_SIZE*i, TILE_SIZE*8, TILE_SIZE, TILE_SIZE);
			
		// Gas Bubbles
		for (int i = 0; i < 4; i++) {
			gasBubbles[i] = makeImage(TILE_SIZE*(i + 3), TILE_SIZE*8, TILE_SIZE, TILE_SIZE);
			playerGasBubbles[i] = makeImage(TILE_SIZE*(i + 7), TILE_SIZE*8, TILE_SIZE, TILE_SIZE);
		}
		
		// Energy and Health Bars for Player
		healthBar = makeImage(116, 176, 122, 11);
		healthBarBackground = makeImage(116, 188, 122, 11);
		FruitFever.screenHandler.currentHealthBar = Thing.copyImage(healthBar);
		energyBar = makeImage(116, 152, 122, 11);
		energyBarBackground = makeImage(116, 164, 122, 11);
		FruitFever.screenHandler.currentEnergyBar = Thing.copyImage(energyBar);
		
		updateLoadingBar(0.1);
	
		/** Projectiles **/
		sheet = DataLoader.loadImage("img/sprites/projectiles.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/projectiles.png");
	
		// Fuzzy Projectile Animation Images
		// for (int i = 0; i < 8; i++)
			// fuzzyShot[i] = makeImage(TILE_SIZE*i, 0, TILE_SIZE, TILE_SIZE);
			
		// Swirl Animation Images
		for (int i = 0; i < 6; i++)
			swirlAnimation[i] = makeImage(TILE_SIZE*i, TILE_SIZE, TILE_SIZE, TILE_SIZE);
		
		// Fireball and Purple Ball Images
		purpleBallSmall = makeImage(0, TILE_SIZE*2, TILE_SIZE*2, TILE_SIZE);
		purpleBallBig = makeImage(TILE_SIZE*2, TILE_SIZE*2, TILE_SIZE, TILE_SIZE);
		fireBallSmall = makeImage(TILE_SIZE*3, TILE_SIZE*2, TILE_SIZE*2, TILE_SIZE);
		fireBallBig = makeImage(TILE_SIZE*5, TILE_SIZE*2, TILE_SIZE, TILE_SIZE);

		updateLoadingBar(0.1);
		
		/** Player **/
		sheet = DataLoader.loadImage("img/sprites/player.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/player.png");
		
		playerStill[0] = makeImage(0, 0, TILE_SIZE*3, TILE_SIZE);
		playerStillH[0] = ImageTransformer.horizontalFlip(playerStill[0]);
		
		for (int i = 0; i < 9; i += 2) {
			playerTongue[i/2] = makeImage(0, TILE_SIZE*i, TILE_SIZE*3, TILE_SIZE);
			playerTongueH[i/2] = ImageTransformer.horizontalFlip(playerTongue[i/2]);
		}
		
		for (int i = 0; i < 6; i++) {
			playerShoot[i] = makeImage(TILE_SIZE*3, TILE_SIZE*i, TILE_SIZE*3, TILE_SIZE); 
			playerShootH[i] = ImageTransformer.horizontalFlip(playerShoot[i]);	
		}
		
		updateLoadingBar(0.1);
		
		/** Slider **/
		sheet = DataLoader.loadImage("/img/Menu/Slider.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/Menu/Slider.png");
		
		sliderCircle = makeImage(0, 0, TILE_SIZE, TILE_SIZE); 
		sliderCirclePressed = makeImage(TILE_SIZE, 0, TILE_SIZE, TILE_SIZE); 
		sliderBar = makeImage(0, TILE_SIZE, TILE_SIZE*7, TILE_SIZE); 
		
		/** Enemies
		sheet = DataLoader.loadImage("img/sprites/enemies.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/sprites/enemies.png");
		
		for (int i = 0; i < 4; i++) {
			fuzzyEnemyAttack[i] = makeImage(TILE_SIZE*(2*i), 0, TILE_SIZE*2, TILE_SIZE); 
			fuzzyEnemyAttackH[i] = ImageTransformer.horizontalFlip(fuzzyEnemyAttack[i]);	
		}
		
		for (int i = 0; i < 3; i++) {
			fuzzyEnemyMoving[i] = makeImage(TILE_SIZE*(2*i), TILE_SIZE*2, TILE_SIZE*2, TILE_SIZE); 
			fuzzyEnemyMovingH[i] = ImageTransformer.horizontalFlip(fuzzyEnemyMoving[i]);	
		}
		
		for (int i = 0; i < 4; i++) {
			wormEnemyMoving[i] = makeImage(TILE_SIZE*(2*i), TILE_SIZE*5, TILE_SIZE*2, TILE_SIZE); 
			wormEnemyMovingH[i] = ImageTransformer.horizontalFlip(wormEnemyMoving[i]);
		}
		**/
		
		updateLoadingBar(0.1);
		
		/** Import menu images **/
		sheet = DataLoader.loadImage("img/Menu/Menu_Red.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/Menu/Menu_Red.png");
		
		// Menu Button Images
		for (int i = 0; i < 12; i++)
			menuButtons[i] = makeImage(0, i*69, 266, 69);
			
		// Fruit Fever Title
		fruitFeverTitle = makeImage(267, 338, 351, 36);
		
		// Icon Background Bar
		iconBackgroundBar = makeImage(0, Data.TILE_SIZE*34, Data.TILE_SIZE*28, Data.TILE_SIZE);
		
		/** Import level selection arrow images **/
		sheet = DataLoader.loadImage("img/LevelSelection/arrows/brownArrows.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/LevelSelection/arrows/brownArrows.png");
		for (int i = 0; i < 3; i++) {
			leftArrowButton[i] = makeImage(0, i*33, 36, 31);
			rightArrowButton[i] = makeImage(36, i*33, 36, 31);
		}
		
		updateLoadingBar(0.1);
		
		/** Import level boxes and button images **/
		sheet = DataLoader.loadImage("img/menu/tileset.png", "https://raw.githubusercontent.com/MicahAndWill/FruitFever/master/src/img/menu/tileset.png");
		
		levelButton[0] = makeImage(TILE_SIZE*2, 0, TILE_SIZE*2, TILE_SIZE*2);
		levelButton[1] = makeImage(0, 0, TILE_SIZE*2, TILE_SIZE*2);
			
		for (int n = 0; n < 3; n++)
			for (int y = 0; y < 3; y++)
				for (int x = 0; x < 3; x++)
					tileset[n][y][x] = makeImage(TILE_SIZE*4 + TILESET_SIZE*(x + n*3), TILESET_SIZE*y, TILESET_SIZE, TILESET_SIZE);
		
		for (int i = 0; i < 3; i++)
			buttonFrame[i] = ImageTransformer.joinSet(tileset[i], 175, 37);
		
		windowBorder = ImageTransformer.joinSet(tileset[0], 260, 334);

		updateLoadingBar(0.05);

		/** Add buttons and set locations **/
		addButtonsToArrayList();
		
		updateLoadingBar(0.05);

	}
		
	/** Used to help get the sub-images from the sprite-sheet **/
	private static GImage makeImage(int x, int y, int width, int height) {
		return new GImage(sheet.getSubimage(x, y, width, height).getScaledInstance(-50, -50, 0));
	}
	
	/** Updates level information from the file **/
	public static void updateLevelInformation(int level) {
	
		infoFile.addItem(String.valueOf(level), FruitFever.levelInformation[level]);
	
	}
	
	/** Loads level information from the file **/
	public static void loadLevelInformation() {
		
		// Load level information
		if (new File("levels/levelInformation.ser").exists()) {
			for (int i = 0; i < 100; i++)
				FruitFever.levelInformation[i] = (LevelInformation) infoFile.getItem(String.valueOf(i));
		}
		// Generate level information from levels.txt file
		else {
			
			for (int i = 0; i < 100; i++)

				try {
					Scanner sc = new Scanner(new File("levels/exportedLevels/" + i + ".txt"));
						
					FruitFever.levelInformation[i] = new LevelInformation(sc.nextLine(), i, false);
					infoFile.addItem(String.valueOf(i), FruitFever.levelInformation[i]);
			
				/** Create unlocked level with no highscore or name or stars since it does not exist **/
				} catch (IOException e) {
					FruitFever.levelInformation[i] = new LevelInformation("", i, false);
					infoFile.addItem(String.valueOf(i), FruitFever.levelInformation[i]);
				}
		
		}
		
	}

	/** Loads objects from the file **/
	public static void loadObjects() {
		
		try {

			Scanner sc = new Scanner(new File("levels/exportedLevels/" + FruitFever.currentLevel + ".txt"));

			// Clear ArrayLists
			Block.resetBlockLists();

			FruitFever.levelInformation[FruitFever.currentLevel].name = sc.nextLine();
			FruitFever.LEVEL_WIDTH = Integer.valueOf(sc.nextLine());
			FruitFever.LEVEL_HEIGHT = Integer.valueOf(sc.nextLine());
			

			int lineNumber = 0;
			String line = "";
			ArrayList<Thing> fallingObjectsInBlockLayer = new ArrayList<>();

			/** BLOCKS (as well as Player, Lava, and Fruits, etc.) **/

			while (sc.hasNextLine() && !(line = sc.nextLine()).equals("") && !line.equals("+")) {

				// Iterate through each character in the line, instantiating the specified block (if it exists)
				for (int i = 0; i < line.length(); i++) {

					char character = line.charAt(i);

					// Skip if it's a blank
					if (character == '-' || character == ' ')
						continue;
						
					// Lava
					if (character == '~') {
						Animation obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, lava, Animation.Type.LAVA);
						obj.boundaryTop = (Data.TILE_SIZE/3);
						FruitFever.things.add(obj);
						FruitFever.dangerousThings.add(obj);
						fallingObjectsInBlockLayer.add(obj);
						continue;
					}
					
					// Spike
					if (character == '^') {
					
						Animation obj;

						// Creates spikes by using block detection to determine orientation
						if (Block.getBlock(i*TILE_SIZE, lineNumber*TILE_SIZE - Data.TILE_SIZE) == null) {
							obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, spikes, Animation.Type.SPIKES);
							obj.boundaryTop = 15;
							fallingObjectsInBlockLayer.add(obj);
						} else {
							obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, spikesV, Animation.Type.SPIKES);
							obj.boundaryBottom = -15;

							Block blockAbove = Block.getBlock(i*TILE_SIZE, (lineNumber - 1)*TILE_SIZE);
							if (blockAbove != null)
								blockAbove.connectedObjects.add(obj);
						}
						
						FruitFever.things.add(obj);
						FruitFever.dangerousThings.add(obj);
						continue;
					}
					
					// Gas Bubbles
					if (character == ':') {
						Animation obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, gasBubbles, false, 4, true, Animation.Type.GAS_BUBBLES, true);
						FruitFever.things.add(obj);
						FruitFever.dangerousThings.add(obj);
						fallingObjectsInBlockLayer.add(obj);
						continue;
					}
					
					// Torches
					if (character == '&') {

						Thing obj;

						if (Block.getBlock((i - 1)*TILE_SIZE, lineNumber*TILE_SIZE) == null) {
							obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, torches[(int)(Math.random()*3)], false, 4, true, Animation.Type.NOT_AVAILABLE, true);
							FruitFever.things.add(obj);
						} else {
							obj = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, torchesH[(int)(Math.random()*3)], false, 4, true, Animation.Type.NOT_AVAILABLE, true);
							FruitFever.things.add(obj);
						}

						fallingObjectsInBlockLayer.add(obj);
						continue;
					}
					
					// Hint Sign
					if (character == '?') {
						Hint obj = new Hint(i*TILE_SIZE, lineNumber*TILE_SIZE);
						FruitFever.things.add(obj);
						FruitFever.hints.add(obj);
						fallingObjectsInBlockLayer.add(obj);
						continue;
					}
					
					// Set the player's start position
					if (character == '@') {
						Player.startX = i*TILE_SIZE;
						Player.startY = lineNumber*TILE_SIZE;
						continue;
					}

					// Vortex
					if (character == '%') {
						Animation vortex = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, vortexAnimation, false, 2, true, Animation.Type.NOT_AVAILABLE, true);
						FruitFever.vortex = vortex;
						FruitFever.vortex.adjustBoundaries(7, -7, 7, -7);
						FruitFever.things.add(vortex);
						continue;
					}
					
					// Fruit Ring
					if (character == '*') {
						Animation fruitRing = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, fruitRingAnimation, true, 3, true, Animation.Type.FRUIT_RING, true);
						FruitFever.edibleItems.add(fruitRing);
						FruitFever.things.add(fruitRing);
						FruitFever.totalFruitRings++;
						continue;
					}

					// CheckPoint
					if (character == '|') {
						Thing obj = new Thing(i*TILE_SIZE, lineNumber*TILE_SIZE - TILE_SIZE, checkpointFlagRed);
						FruitFever.checkPoints.add(obj);
						FruitFever.things.add(obj);
						continue;
					}

					// Reads in a fruit
					if (Character.isDigit(character)) {
						Animation fruit = new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, fruits[Integer.valueOf(String.valueOf(character))], true, 3, true, Animation.Type.FRUIT, true);	
						FruitFever.edibleItems.add(fruit);
						FruitFever.things.add(fruit);
						continue;
					}		

					try {

						GImage image;
						int color;

						// Normal Blocks
						if (character - 'a' >= 0) {
							color = character - 'a';
							image = blockImages[color][(int) (Math.random()*4)];
						}
						// Capital letters
						else {
							// We will use capitals for something else later (falling blocks?)
							continue;
						}


						// Add Block to the ArrayList
						FruitFever.blocks.add(new Block(i*TILE_SIZE, lineNumber*TILE_SIZE, image));

					} catch (ArrayIndexOutOfBoundsException e) { 
						System.out.printf("\nBLOCK LAYER contains invalid character: '%c' \n", character);
						System.exit(0);
					}
				}

				lineNumber++;

			}

			for (Thing obj : fallingObjectsInBlockLayer) {
				Block blockBelow = Block.getBlock(obj.imageX, obj.imageY + Data.TILE_SIZE);
				Block blockToRight = Block.getBlock(obj.imageX + Data.TILE_SIZE, obj.imageY);
				Block blockToLeft = Block.getBlock(obj.imageX - Data.TILE_SIZE, obj.imageY);
				if (blockBelow != null)
					blockBelow.connectedObjects.add(obj);
				else if (blockToRight != null)
					blockToRight.connectedObjects.add(obj);
				else if (blockToLeft != null)
					blockToLeft.connectedObjects.add(obj);
			}

			lineNumber = 0;

			/** SCENERY **/

			while (sc.hasNextLine() && !(line = sc.nextLine()).equals("") && !line.equals("+")) {

				// Iterate through each character in the line, instantiating the specified block (if it exists)
				for (int i = 0; i < line.length(); i++) {

					Block blockAbove = Block.getBlock(i*TILE_SIZE, (lineNumber - 1)*TILE_SIZE);
					Block blockOn = Block.getBlock(i*TILE_SIZE, lineNumber*TILE_SIZE);
					Block blockBelow = Block.getBlock(i*TILE_SIZE, (lineNumber + 1)*TILE_SIZE);

					char character = line.charAt(i);

					// Skip if it's a blank
					if (character == '-' || character == '#' || character == ' ' || character == '?')
						continue;
					
					try {
						// Read in a powerup
						if (Character.isDigit(character)) {
							int number = Integer.valueOf(String.valueOf(character));
							FruitFever.edibleItems.add(new Animation(i*TILE_SIZE, lineNumber*TILE_SIZE, new GImage[]{powerups[number]}, false, 0, true, Animation.powerupTypes[number]));
							continue;
						}
					} catch (ArrayIndexOutOfBoundsException e) { 
						System.out.printf("SCENERY LAYER contains invalid character: '%c' \n", character);
						System.exit(0);
					}
					
					try {
					
						// Lowercase
						if (character - 'a' >= 0) {
							
							// Hard-Coded Exceptions (origin adjustments)
							int type = character - 'a', xOffset;
							if (type == 0)
								xOffset = -12;
							else if (type == 6)
								xOffset = -3;
							else
								xOffset = 0;

							Thing obj = new Thing(i*TILE_SIZE + xOffset, lineNumber*TILE_SIZE, sceneryImages[type]);
							FruitFever.things.add(obj);
							
							if (blockOn != null)
								blockOn.connectedObjects.add(obj);
							else if (blockBelow != null)
								blockBelow.connectedObjects.add(obj);

							
						}
						
						// Uppercase
						else {
							int type = character - 'A';

							Thing obj = new Thing(i*TILE_SIZE, lineNumber*TILE_SIZE, sceneryImages[26 + type]);
							FruitFever.things.add(obj);

							if (blockOn != null)
								blockOn.connectedObjects.add(obj);
							else if (blockBelow != null)
								blockBelow.connectedObjects.add(obj);
						}

					} catch(ArrayIndexOutOfBoundsException e) { 
						System.out.printf("\nSCENERY LAYER contains invalid character: '%c' \n", character);
						System.exit(0);
					}	
				}
				
				lineNumber++;
			}
			
			lineNumber = 0;
			
			/** HINTS **/

			for (int i = 0; i < FruitFever.hints.size(); i++)
				FruitFever.hints.get(i).hint = sc.nextLine();
			
			/** ENEMIES

			while (sc.hasNextLine() && !(line = sc.nextLine()).equals("") && !line.equals("+")) {
				
				int characterOffset = 0;
				
				// Iterate through each character in the line, instantiating the specified enemy (if it exists)
				for (int i = 0; i < line.length(); i++) {
					
					char character = line.charAt(i);

					// Skip if it's a blank
					if (character == '-' || character == ' ' || character == '#' || character == '?')
						continue;
						
					// Read in enemy
					if (character == '<') {
						int currentCharacterOffset = 1;
						char enemyType = line.charAt(i - characterOffset + currentCharacterOffset);
						currentCharacterOffset++;
						char enemyColor = line.charAt(i - characterOffset + currentCharacterOffset);
						currentCharacterOffset++;
						
						String left = findFirstNumber(line, i - characterOffset + currentCharacterOffset);
						currentCharacterOffset += left.length() + 1;
						
						String right = findFirstNumber(line, i - characterOffset + currentCharacterOffset);
						currentCharacterOffset += right.length() + 1;
						
						String up = findFirstNumber(line, i - characterOffset + currentCharacterOffset);
						currentCharacterOffset += up.length() + 1;
						
						String down = findFirstNumber(line, i - characterOffset + currentCharacterOffset);
						currentCharacterOffset += down.length();
						
						int x = (i - characterOffset);
						characterOffset += currentCharacterOffset;
						int y = lineNumber;
						
						// if(enemyType == 'a')
							FruitFever.enemies.add(new Enemy(new int[]{(x - Integer.valueOf(left))*TILE_SIZE, (x + Integer.valueOf(right))*TILE_SIZE}, new int[]{(y - Integer.valueOf(up))*TILE_SIZE, (y + Integer.valueOf(down))*TILE_SIZE}, new GImage[][]{ wormEnemyMoving, wormEnemyMovingH}, true, 2, true, 1, 1));

					}
		
				}

				lineNumber++;

			}
			
			**/

			sc.close();
		
		} catch (IOException e) {
			System.out.println("Level " + FruitFever.currentLevel + " could not load.\n");
		}

	}
	
	public static void addButtonsToArrayList(){
	
		Button tempButton;
		
		// Adds main menu buttons to the ArrayLists
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - menuButtons[0].getWidth()/2), 125, Button.Type.PLAY, menuButtons[0], menuButtons[1], menuButtons[2]), FruitFever.mainMenuButtons);
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - menuButtons[3].getWidth()/2), 125 + 75, Button.Type.CONTROLS, menuButtons[3], menuButtons[3 + 1], menuButtons[3 + 2]), FruitFever.mainMenuButtons);
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - menuButtons[6].getWidth()/2), 125 + 75*2, Button.Type.OPTIONS, menuButtons[3*2], menuButtons[3*2 + 1], menuButtons[3*2 + 2]), FruitFever.mainMenuButtons);
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - menuButtons[9].getWidth()/2), 125 + 75*3, Button.Type.MULTIPLAYER, menuButtons[3*3], menuButtons[3*3 + 1], menuButtons[3*3 + 2]), FruitFever.mainMenuButtons);
		
		/** Adds level box buttons to the ArrayLists for Level Selection Screen **/
		for (int i = 0; i < 20; i++)
			addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - 115 + (i%4)*60), 97 + (i/4)*55, levelButton, i), FruitFever.levelSelectionButtons);
		
		/** Adds arrow buttons to the ArrayLists for Level Selection Screen **/
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - leftArrowButton[0].getWidth()/2 - 70), 375, Button.Type.LEFT_ARROW, leftArrowButton), FruitFever.levelSelectionButtons);
		
		addToButtons(new Button((int) (FruitFever.SCREEN_WIDTH/2 - rightArrowButton[0].getWidth()/2 + 70), 375, Button.Type.RIGHT_ARROW, rightArrowButton), FruitFever.levelSelectionButtons);
		
		/** Adds gear button to the ArrayLists for In-Game Screen **/
		addToButtons(new Button((int) FruitFever.SCREEN_WIDTH - 31 - TILE_SIZE, 0, Button.Type.GEAR, gearButton), FruitFever.inGameButtons);
		
		/** Adds refresh button to the ArrayList for In-Game Screen **/
		addToButtons(new Button((int) FruitFever.SCREEN_WIDTH - 31, 0, Button.Type.REFRESH, refreshButton), FruitFever.inGameButtons);
		
		/** Adds sliders and music/sound effect toggles to the ArrayList for Pause Menu Screen **/
		
		addToButtons(new Slider((int) ((FruitFever.SCREEN_WIDTH - sliderBar.getWidth() + TILE_SIZE)/2), TILE_SIZE*6, sliderCircle, sliderCirclePressed, sliderCirclePressed, sliderBar, 0.5), FruitFever.pauseMenuButtons);
		addToButtons(new Button((int)(FruitFever.SCREEN_WIDTH/2 - TILE_SIZE*4.5), TILE_SIZE*6, Button.Type.MUSIC, musicButton), FruitFever.pauseMenuButtons);
		
		addToButtons(new Slider((int) ((FruitFever.SCREEN_WIDTH - sliderBar.getWidth() + TILE_SIZE)/2), TILE_SIZE*8, sliderCircle, sliderCirclePressed, sliderCirclePressed, sliderBar, 0.8), FruitFever.pauseMenuButtons);
		addToButtons(new Button((int)(FruitFever.SCREEN_WIDTH/2 - TILE_SIZE*4.5), TILE_SIZE*8, Button.Type.SOUND_EFFECTS, soundEffectsButton), FruitFever.pauseMenuButtons);
		
		/** Adds Main Menu, Level Selection, and Resume buttons to the ArrayList for Pause Menu Screen **/
		
		addToButtons(new Button((int)((FruitFever.SCREEN_WIDTH - buttonFrame[0].getWidth())/2), TILE_SIZE*10, Button.Type.MAIN_MENU, buttonFrame), FruitFever.pauseMenuButtons);
		addToButtons(new Button((int)((FruitFever.SCREEN_WIDTH - buttonFrame[0].getWidth())/2), TILE_SIZE*12, Button.Type.LEVEL_SELECTION, buttonFrame), FruitFever.pauseMenuButtons);
		addToButtons(new Button((int)((FruitFever.SCREEN_WIDTH - buttonFrame[0].getWidth())/2), TILE_SIZE*14, Button.Type.RESUME, buttonFrame), FruitFever.pauseMenuButtons);
		
	}
	
	private static void addToButtons(Button button, ArrayList<Button> buttonList) {
		FruitFever.buttons.add(button);
		buttonList.add(button);
	}
	
	private static void updateLoadingBar(double newProgress) {
		try {
			loadingBarProgress += newProgress;
			loadingScreenBar = ImageTransformer.resize(loadingScreenBar, (int) (700*(loadingBarProgress)), 20);
			loadingScreenBar.setLocation(0, FruitFever.SCREEN_HEIGHT - (int) loadingScreenBar.getHeight());
			FruitFever.screenHandler.add(loadingScreenBar);
		} catch (NullPointerException e) { }
	}
	
	/** Grabs an integer out of an enemy entry in the Enemy block 
	private static String findFirstNumber(String line, int index) {
	
		String str = "";
		
		while (line.charAt(index) != ',' && line.charAt(index) != '>') {
			str += line.charAt(index++);
		}
		
		return str;
	} **/
	
}