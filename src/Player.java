
/**
 *	Player - This class controls all the actions and collisions of the main character.
 *
 * @Author William Fiset, Micah Stairs
 * 
 *
 * Note on Collision Detection with the player:
 * There are currently eight sensors on the player, two for each side, they measure if the player 
 * came in contact with the block adjacent and if so they execute the code within the function.
 *
 *
 **/

import acm.graphics.*;
import acm.program.*;
import java.util.*;
import java.awt.Color.*;
import java.awt.*;

public class Player extends MovingAnimation {
	
	static byte lives = 3;
	static final byte maxLives = 3;

// Swirl related Variables
	static Swirl swirl;
	private final static byte SWIRL_MOUTH_DISTANCE = 15; 

// Movement Variables
	static final int HORIZONTAL_VELOCITY = 3; // For release make horizontal velocity 3
	int dx = 0;

// Collision Detection Variables
	final int VERTICAL_PX_BUFFER = 2;
	final int CRACK_SPACING = 3;
	final int JUMP_SPACING = 3;

// Variables concerning Gravity

	final double TERMINAL_VELOCITY = Data.TILE_SIZE - 1;
	final double STARTING_FALLING_VELOCITY = 2.5;
	final double STARTING_FALLING_ACCELERATION = 0.5;
	final double changeInAcceleration = 0.015;

	double fallingVelocity = STARTING_FALLING_VELOCITY;
	double fallingAcceleration = STARTING_FALLING_ACCELERATION;

	private static boolean onPlatform = false;
	private static boolean gravity = true;

// Variables concerning jumping
	
	// setBaseLine is true because we don't know where the player starts
	private static boolean keepJumping = false;
	private boolean setBaseLine = true;
	private static boolean isJumping = false;

	private int maxJumpHeight = (int)(3.5*Data.TILE_SIZE); // 3.5 tile jump limit
	private int baseLine;

// Jumping motion Variables
	final double STARTING_JUMPING_VELOCITY = 6.25; 
	final double STARTING_JUMPING_DECCELERATION = 0;
	final double CHANGE_IN_DECLERATION = 0.043; 

	double jumpingDecceleration = STARTING_JUMPING_DECCELERATION;
	double jumpingVelocity = STARTING_JUMPING_VELOCITY;



// Animation things
	GImage[] stillAnim, stillAnimH, shootAnim, shootAnimH, tongueAnim, tongueAnimH;
	public static boolean facingRight = true;

	public void setKeepJumping(boolean keepJumping){
		this.keepJumping = keepJumping;
	}

	/** triggers the variables that make the player jump **/
	public void jump(){

		if (keepJumping)
			setIsJumping(true);	

	}

	public Player(int x, int y){

		super(x, y, Data.playerStill, false, 1, true, 0);

		this.stillAnim = Data.playerStill;
		this.stillAnimH = Data.playerStillH;
		this.shootAnim = Data.playerShoot;
		this.shootAnimH = Data.playerShootH;
		this.tongueAnim = Data.playerTongue;
		this.tongueAnimH = Data.playerTongueH;

		boundaryLeft = Data.TILE_SIZE;
		boundaryRight = -Data.TILE_SIZE;
		
		swirl = new Swirl();
		
		// Reset static variables
		isJumping = false;
		onPlatform = false;
		lives = maxLives;

	}

	/** Calls all the players actions **/
	public void motion(){

		// System.out.printf("jumpingVelocity: %f isJumping: %b \n", jumpingVelocity, isJumping);

		// System.out.printf("fallingVelocity: %f  imageY: %d  imageX: %d \n", fallingVelocity, imageY, imageX);

		// Collisions
		checkCollisionDetection();
		objectCollisions();

		relativisticScreenMovement();
	
		// These methods control the movement of the player 
		jumpingEffect();
		gravityEffect();
		imageX += dx;

		enableJumping();
		updateHealth();
		grabbingItem();
	}
	

	/** Responds accordingly to collision detection **/
	private void checkCollisionDetection(){

		extraCollisionChecks();
		downwardsCollision();
		sidewaysCollision();
		upwardsCollision();

	}

	/** Side Collisions **/
	private boolean sidewaysCollision(){

		boolean collision = false;

		// Player is moving EAST
		if (FruitFever.dx == 1) {

			Block eastNorth;
			Block eastSouth;

			// When not on platform
			eastNorth = Block.getBlock(x + width, y + VERTICAL_PX_BUFFER); 
			eastSouth = Block.getBlock(x + width, y + height - VERTICAL_PX_BUFFER);

			// No block right of player
			if (eastSouth == null && eastNorth == null) {
				// System.out.println("Side");
				dx = HORIZONTAL_VELOCITY;
				collision = true;
			} else {

				// Stop viewX from moving as well as player
				dx = 0; 
				FruitFever.vx = 0;
			}

		// Player is moving WEST
		} else if (FruitFever.dx == -1) {
			
			
			Block westNorth = Block.getBlock(x, y + VERTICAL_PX_BUFFER); 
			Block westSouth = Block.getBlock(x, y + height - VERTICAL_PX_BUFFER);

			// No block left of player
			if (westNorth == null && westSouth == null){
				// System.out.println("Side");
				dx = -HORIZONTAL_VELOCITY;
				collision = true;
			} else {
				
				// Stop viewX from moving as well as player
				dx = 0; 
				FruitFever.vx = 0;
			}
		}

		return collision;

	}

	/** Test if player is going to hit a platform while falling **/
	private void downwardsCollision(){


		// SOUTH
		Block southWest = null, southEast = null;

		// Need to do this because starting starting falling velocity is never 0
		if (gravity) {
			southWest = Block.getBlock(x + CRACK_SPACING, y + height + VERTICAL_PX_BUFFER + (int) fallingVelocity);			
			southEast = Block.getBlock(x + width - CRACK_SPACING, y + height + VERTICAL_PX_BUFFER + (int) fallingVelocity);
		
		// Will this ever execute?
		} else {
			System.out.println("downwardsCollision - ?Executes?");
			southWest = Block.getBlock(x + CRACK_SPACING, y + height + VERTICAL_PX_BUFFER);			
			southEast = Block.getBlock(x + width - CRACK_SPACING, y + height + VERTICAL_PX_BUFFER);
		}

		
		if (southEast != null || southWest != null) {
			
			// System.out.println("Down");

			onPlatform = true;	

			// This is what actually stops the fall 
			if (southEast != null) placePlayerOnBlock(southEast);
			else placePlayerOnBlock(southWest);
			
		} else
			onPlatform = false;

	}

	/** Does extra checks for special case(s) **/
	private void extraCollisionChecks(){

		// ** Fixes issue #64 (player falling into block) **//

		// Executes only when falling downwards 
		if (!isJumping && jumpingVelocity > STARTING_JUMPING_VELOCITY) {
			System.out.println(jumpingVelocity);
			for (int horizontalPosition = 3; horizontalPosition <= 22 ; horizontalPosition++){

				// optimization (we don't need to check all the points)
				if (horizontalPosition > 6 && horizontalPosition < 19) continue;

				// Forms a line of points that determine if the player has hit anything
				Block southernBlock = Block.getBlock(x + horizontalPosition , y + height + (int) fallingVelocity - 4);

				// If collision 
				if (southernBlock != null) {
					System.out.println("extraCollisionChecks() triggered");
					onPlatform = true; 
					placePlayerOnBlock(southernBlock);
					return;
				}
			}
		}

	}

	/** Does all the upwards boundary colliion checks **/
	private void upwardsCollision(){

		Block northWest = Block.getBlock(x + JUMP_SPACING, y - VERTICAL_PX_BUFFER );
		Block northEast = Block.getBlock(x + width - JUMP_SPACING, y - VERTICAL_PX_BUFFER );

		// Collision on block above this one has happened
		if (northWest != null || northEast != null){
			// System.out.println("Up");
			resetJump();
		}

	}

	/** Places the player on top of the block he is currently on **/
	private void placePlayerOnBlock(Block block) {
		if (onPlatform)
			// This could be more general if there were different size blocks
			imageY = block.imageY - block.height;
	}

	/** Handles Jumping triggered by the Player **/
	private void jumpingEffect(){

		// Jumping event was Triggered
		if (isJumping) {

			// Sets baseLine (where the player started before jumping)
			if (!setBaseLine) {
				baseLine = y;
				setBaseLine = true;	
			}
			
			// Player has not yet hit the maximum jump limit
			if (imageY - jumpingVelocity > baseLine - maxJumpHeight && jumpingVelocity > 0) {

				// Move the player's image up
				imageY -= jumpingVelocity;

				jumpingVelocity -= jumpingDecceleration;
				jumpingDecceleration += CHANGE_IN_DECLERATION;

			// Player has reached maxHeight, gravity now kicks in
			} else resetJump();				
		}
	}

	private void resetJump(){

		jumpingVelocity = STARTING_JUMPING_VELOCITY;
		jumpingDecceleration = STARTING_JUMPING_DECCELERATION;

		isJumping = false;
	}


	/** 
	 * It was a good idea to have a setter for IsJumping.  
	 * For example you don't always want to set isJumping to true if the
	 * character is in free fall. 
	 **/
	public void setIsJumping(boolean value){

		// If you are not jumping and are on a platform
		if (!setBaseLine && onPlatform)
			isJumping = true;
	}

	/** Resets players ability to jump if applicable **/
	private void enableJumping(){
		
		if(onPlatform)
			setBaseLine = false;
		
	}

	/** Takes care of making the player fall when not jumping and not on a platform **/
	private void gravityEffect(){

		// Gravity Effect triggered here
		if (!isJumping && gravity && !onPlatform) {

			// Move the player's image down
			imageY += fallingVelocity;

			if (fallingVelocity < TERMINAL_VELOCITY) {

				// Acceleration effect
				fallingVelocity += fallingAcceleration;
				fallingAcceleration += changeInAcceleration;
			
			} else
				fallingVelocity = TERMINAL_VELOCITY;
			
		// Executes when not falling or not allowed to fall
		} else {

			// Reset falling speed
			fallingVelocity = STARTING_FALLING_VELOCITY;
			fallingAcceleration = STARTING_FALLING_ACCELERATION;
		}

	}


	/** 
     * Moves the view of the screen relative to the character
     * (This method could use some serious refactoring, but I dare not!)
	 **/
	private void relativisticScreenMovement(){

		// Horizontal screen movement
		if (x + width > FruitFever.RIGHT_BOUNDARY && dx > 0) {
			
			// Makes sure view never passes maximum level width 
			if (FruitFever.viewX >= FruitFever.LEVEL_WIDTH - FruitFever.SCREEN_WIDTH + Data.TILE_SIZE) 
				FruitFever.vx = 0;
			else
				FruitFever.vx = dx;	
		
		} else if (x < FruitFever.LEFT_BOUNDARY && dx < 0) {

			// Makes sure view never shows blank left of screen
			if (FruitFever.viewX <= 0)
				FruitFever.vx = 0;
			else
				FruitFever.vx = dx;	
		}

		// DOWN bound 
		if (y + height > FruitFever.DOWN_BOUNDARY)
			FruitFever.vy = fallingVelocity;

		// UPPER bound
		else if (y < FruitFever.UP_BOUNDARY && FruitFever.viewY >= 0) {
			
			// When jump gets and starts falling jumpingVelocity = STARTING_JUMPING_VELOCITY
			if (jumpingVelocity != STARTING_JUMPING_VELOCITY)
				FruitFever.vy = -jumpingVelocity;		

		// Make sure screen doesn't move when player is not jumping or on platform
		} else if (!isJumping && onPlatform) 
			FruitFever.vy = 0;
		
		// Stop moving the screen up 
		if (FruitFever.viewY >= FruitFever.LEVEL_HEIGHT - FruitFever.SCREEN_HEIGHT + Data.TILE_SIZE && FruitFever.vy > 0 )
			FruitFever.vy = 0;

		FruitFever.viewX += FruitFever.vx;
		
		// Fixes the glitch with half jumping height
		if (FruitFever.viewY + FruitFever.vy >= 0)
 			FruitFever.viewY += FruitFever.vy;	
 		
	}

	public void eat(){
		
		// Makes sure you finish a cycle of images before starting a new one
		if(!images.equals(tongueAnim) && !images.equals(tongueAnimH))
			counter = -1;

		// Adjust Animation variables		
		repeat = false;
		active = true;

		// Switch animation images
		if(facingRight)
			images = tongueAnim;
		else
			images = tongueAnimH;
		
	}

	/** Updates the players health  **/
	private void updateHealth(){

		// This statement kinda looks weird but it's clear and more efficient (I think!)
		if(checkForPlayerOutOfBounds()){}
		else if (checkForDangerousSpriteCollisions()) {}

	}

	/** checks if player touched any dangerous sprites (aka lava)**/
	private boolean checkForDangerousSpriteCollisions(){

		boolean collisionOccurred = false;

		// loop through all dangerous sprites
		for (Thing dangerousThing : FruitFever.dangerousThings) {
			
			/** As we start getting more and more dangerous sprites we will have to
			  * distinguish between types either by using instanceof or giving each 
			  * sprite a property **/
			
			dangerousThing.boundaryTop = (Data.TILE_SIZE/3);

			if (dangerousThing.intersects(this)) {

				collisionOccurred = true;

				lives--;		
				adjustLives(lives);	
				respawn();

				break;
			}
		}

		return collisionOccurred;

	}

	private boolean checkForPlayerOutOfBounds(){

		boolean playerOutOfBounds = (x + Data.TILE_SIZE < 0 || x > FruitFever.LEVEL_WIDTH || y + height < 0 || y - height > FruitFever.LEVEL_HEIGHT );

		if (playerOutOfBounds) {
			
			lives--;		
			adjustLives(lives);	
			respawn();
		}

		return playerOutOfBounds;
	}

	/** Checks for collisions with checkPoints, currency, vortex and other matter **/
	private void objectCollisions(){


		/** CheckPoint Collision **/
		for (Thing checkPoint : FruitFever.checkPoints) {
			checkPoint.boundaryLeft = 9;
			checkPoint.boundaryRight = -9;
			// Check if the player intersects the rod
			if (checkPoint.intersects(this)){
				
				// Check to see if this checkpoint hasn't already been attained
				if(FruitFever.greenCheckPoint == null || !FruitFever.greenCheckPoint.equals(checkPoint)){
				
					checkPoint.changeImage(Data.checkpointFlagGreen);
					FruitFever.greenCheckPoint = checkPoint;
					FruitFever.playerStartX = checkPoint.imageX;
					FruitFever.playerStartY = checkPoint.imageY + Data.TILE_SIZE;

					for(int i = 0; i < 7; i++)
						FruitFever.addToThings(new Animation(checkPoint.imageX - 17 + (int) (Math.random()*35),
						checkPoint.imageY - Data.TILE_SIZE*2 + (int) (Math.random()*35),
						Data.fireworkAnimation[(int) (Math.random()*3)], false, 2 + (int)(Math.random()*3),
						false, 3 ));
					
					break;
					
				}
			}
		}



		// Changes all the flags to red flags except the current green flag
		for (Thing checkPoint : FruitFever.checkPoints) {
			if (checkPoint == FruitFever.greenCheckPoint) continue;
			checkPoint.changeImage(Data.checkpointFlagRed);
		}

		// Reset Level if player touches vortex
		if (FruitFever.vortex != null && this.intersects(FruitFever.vortex))
			FruitFever.levelComplete = true;
		
	}


	/** Adjusts the amount of lives that the player has, and redraws the hearts accordingly */
	private void adjustLives(int livesLeft){
		for (int i = 0; i < maxLives; i++)
			FruitFever.livesImages[i].setVisible(livesLeft > i);
	}

	/** Adjusts View to place the player in the middle of the screen 
	 *
	 * @Param levelRespawn: Used to indicate that the player was loaded into a level rather 
	 * 		  than already previously being in the level
	 *
	 * The levelRespawn variable is needed because loading the player in the level as opposed
	 * to changing the players position from within the level seems to have different effect
	 * (I assume this may have to do with the player's actual size of 3*TILE_SIZE)
	 *
	 */
	public void focusViewOnPlayer(int newPlayerXPos, int newPlayerYPos, boolean levelRespawn){

		// Places the player exactly in the middle of the screen
		FruitFever.viewX = newPlayerXPos - (FruitFever.SCREEN_WIDTH/2) + (Data.TILE_SIZE/2);
		FruitFever.viewY = newPlayerYPos - (FruitFever.SCREEN_HEIGHT/2) + (Data.TILE_SIZE/2);

		// Adjust screen so that player cannot see outside view box
		FruitFever.viewY = Math.max(FruitFever.viewY, 0);
		FruitFever.viewX = Math.max(FruitFever.viewX, 0);
		
		FruitFever.viewY = Math.min(FruitFever.viewY, FruitFever.LEVEL_HEIGHT - FruitFever.SCREEN_HEIGHT + Data.TILE_SIZE);
		FruitFever.viewX = Math.min(FruitFever.viewX, FruitFever.LEVEL_WIDTH - FruitFever.SCREEN_WIDTH + Data.TILE_SIZE);


 		// Fixes the loading player position bug 
		if (levelRespawn) {
			imageX -= Data.TILE_SIZE; 
			x -= Data.TILE_SIZE;
		}
			
		FruitFever.naturalAnimateAll();

	}

	/** These are all the settings that need to be reset when the player respawns **/
	private void respawn(){

		// Reset falling speed
		fallingVelocity = STARTING_FALLING_VELOCITY;
		fallingAcceleration = STARTING_FALLING_ACCELERATION;

		// Load player in the correct spot 
		imageX = FruitFever.playerStartX;
		imageY = FruitFever.playerStartY;			

		// Resetswirl
		swirl.resetState();

		// Focus view on the player
		focusViewOnPlayer(FruitFever.playerStartX, FruitFever.playerStartY, true);

	}

	public void shootSwirl(){

		// Makes sure you finish a cycle of images before starting a new one
		if(!images.equals(shootAnim) && !images.equals(shootAnimH))
			counter = -1;

		// Adjust Animation variable
		repeat = false;
		
		/** Check if there's a Block in front/in back of the player before he shoots **/
		if (facingRight) {

			Block westNorth = Block.getBlock(x + Data.TILE_SIZE + SWIRL_MOUTH_DISTANCE, y + Data.TILE_SIZE/4 );
			Block westSouth = Block.getBlock(x + Data.TILE_SIZE + SWIRL_MOUTH_DISTANCE, y + Data.TILE_SIZE - (Data.TILE_SIZE/4));

			// If there is not Block in front of player
			if (westNorth == null && westSouth == null) {

				// Makes the swirl shoot out of the player from the left
				swirl.reset = false;
				swirl.imageX = x + SWIRL_MOUTH_DISTANCE + FruitFever.viewX;
				swirl.imageY = y + FruitFever.viewY;
				swirl.xSpeed = Swirl.dx;

				// Set Right shooting animation
				images = shootAnim;
			
			// If there is a block in front of the player, don't do swirl animation
			} else
				images = stillAnim;

		// Facing left
		} else {

			Block eastNorth = Block.getBlock(x - SWIRL_MOUTH_DISTANCE, y + Data.TILE_SIZE/4);
			Block eastSouth = Block.getBlock(x - SWIRL_MOUTH_DISTANCE, y + Data.TILE_SIZE - (Data.TILE_SIZE/4));

			// If there is not Block in front of player
			if (eastSouth == null && eastNorth == null) {

				// Makes the swirl shoot out of the player from the left
				swirl.reset = false;
				swirl.imageX = x - SWIRL_MOUTH_DISTANCE + FruitFever.viewX;
				swirl.imageY = y + FruitFever.viewY;
				swirl.xSpeed = -Swirl.dx;

				// Set Left shooting animation
				images = shootAnimH;
				
			// If there is a block in front of the player, don't do swirl animation
			} else images = stillAnim;

		}
	
	}
	
	public void swirlTeleport(){
	
		// Remember that the player has a width of TileSize*3 so we must subtract a tile-size!
		imageX = swirl.imageX - Data.TILE_SIZE;
		imageY = swirl.imageY;

		/** Fixes issue #77 (Jumping & teleporting) & #78 (teleporting and falling through blocks) **/
		y = swirl.imageY;


		// Hardcoded Values are to make precision more accurate
		Block upperRight = Block.getBlock(x, y + 3);
		Block upperLeft = Block.getBlock(x + Data.TILE_SIZE, y + 3);
		Block lowerLeft = Block.getBlock(x, y + Data.TILE_SIZE - 4);
		Block lowerRight = Block.getBlock(x + Data.TILE_SIZE, y + Data.TILE_SIZE - 4);

		/** Fixes Issue #42 where player semi teleports into blocks **/

		if (upperRight != null || upperLeft != null || lowerLeft != null || lowerRight != null){
			imageX = (imageX/Data.TILE_SIZE) * Data.TILE_SIZE;	
			
			// Takes into account that the player's center is top left
			if (!facingRight)
				imageX += Data.TILE_SIZE;
			
		}

		/** 
		* The player is sometimes teleported into a block (because swirl height varies) and
		* thus this checks to see if that happened
		* NOTE: This seems to cause a small bounce when teleporting
		*/
		extraCollisionChecks();

		// Focuses the view on the player placing the player in the center of the screen
		focusViewOnPlayer(swirl.imageX, swirl.imageY, false);

		// makes sure the player cannot jump directly after teleportation
		resetJump();
		// setKeepJumping(false);
		

		swirl.resetState();
		
	}

	// Overrides MovingAnimation.animate()
	@Override public void animate(){
		
		if(facingRight){

			if(images.equals(stillAnimH))
				images = stillAnim;
			else if(images.equals(shootAnimH))
				images = shootAnim;
			else if(images.equals(tongueAnimH))
				images = tongueAnim;

		} else {

			if(images.equals(stillAnim))
				images = stillAnimH;
			else if(images.equals(shootAnim))
				images = shootAnimH;
			else if(images.equals(tongueAnim))
				images = tongueAnimH;
		}
		
		if(!active){
		
			// Adjust Animation variables
			repeat = true;
			counter = -1;
			active = true;
			
			// Switch animation images
			if(facingRight)
				images = stillAnim;
			else
				images = stillAnimH;
		}

		// Fixes Issue #45 when Animating the swirl before you check the collision
		super.animate();
		swirl.animate();

		// If Swirl goes off screen or hits a block, destroy it
		if(swirl.imageX + Swirl.SWIRL_IMG_WIDTH < 0 || swirl.imageX > FruitFever.LEVEL_WIDTH || swirl.collidesWithBlock())
			swirl.resetState();

	}
	
	/** Update currently grabbed item or try to grab a item **/
	private void grabbingItem(){
		
		// If the player already has a grabbbed item
		if (FruitFever.grabbedItem != null) {
			
			// Reset item's position based on 
			FruitFever.grabbedItem.imageX = getTonguePosition().x - Data.TILE_SIZE/2;
			FruitFever.grabbedItem.imageY = getTonguePosition().y - Data.TILE_SIZE/2;
			FruitFever.grabbedItem.animate();
			
			// Remove item if animation has finished
			if(!images.equals(tongueAnim) && !images.equals(tongueAnimH)){
				FruitFever.screen.remove(FruitFever.grabbedItem.image);
				for(int i = 0; i < FruitFever.edibleItems.size(); i++)
					if(FruitFever.edibleItems.get(i).equals(FruitFever.grabbedItem)){
						FruitFever.edibleItems.remove(i);
						break;
					}
				FruitFever.grabbedItem = null;
			}
		
		// Try grabbing item (only eats one at a time because of the break statement)
		} if (!FruitFever.tongueButtonReleased) {
			for (int i = 0; i < FruitFever.edibleItems.size(); i++)
				// Check tongue's intersection with the fruit and make it the grabbed fruit if it collides
				if (FruitFever.edibleItems.get(i).intersects(getTongueRectangle())) {
					FruitFever.grabbedItem = FruitFever.edibleItems.get(i);
					break;
				}
		
		}
	
	}

	/** Returns the current location of the tip of the tongue **/
	private Point getTonguePosition(){
	
		int currentTongueWidth = 0;
		
		switch (counter){
			case 3: currentTongueWidth = 8; break;
			case 4: currentTongueWidth = 20; break;
		}
		
		if (facingRight)
			return new Point(imageX + Data.TILE_SIZE*2 + currentTongueWidth, imageY + (int) image.getHeight()/2);
		else
			return new Point(imageX + Data.TILE_SIZE - currentTongueWidth, imageY + (int) image.getHeight()/2);
	}
	
	/** Returns the a Rectangle representing the tongue for collision detection **/
	private Rectangle getTongueRectangle(){
	
		int currentTongueWidth = 1;
		
		switch (counter){
			case 3: currentTongueWidth = 8; break;
			case 4: currentTongueWidth = 20; break;
		}
		
		// NOTE: The height of the tongue is 1 full tile for collision detection, whereas the real tongue is
		// only a few pixels tall.
		
		if (facingRight)
			return new Rectangle(x + Data.TILE_SIZE, y, currentTongueWidth, Data.TILE_SIZE);
		else
			return new Rectangle(x - currentTongueWidth, y, currentTongueWidth, Data.TILE_SIZE);
	}

	public static boolean isOnPlatform(){
		return onPlatform;
	}

	public static boolean isPlayerJumping(){
		return isJumping;
	}

	public void posInfo(){
		System.out.println("ImageX: " + imageX + "   ImageY: " + imageY + "   X: " + x + "   Y: " + y);
	}

	@Override public String toString(){
		return "Player: (Image: " + imageX + ", " + imageY + "   W: " + image.getWidth() + ", H: " + image.getHeight() + ") (Bounding Box: " + x + ", " + y + "   W: " + width + ", H: " + height + ")"; 
	}

}

	/** A swirl is a projectile shot from the player as a teleportation method  **/
	class Swirl extends MovingAnimation{
		
		static boolean reset = true;

		// Swirls velocity
		static final byte dx = 8;

		// This is the location of where the swirl is off screen when it is at rest
		static final short SWIRL_X_REST_POS = -100;
		static final short SWIRL_Y_REST_POS = -100;

		// These values are the actual image dimensions not the Data.TILE_SIZE width and height
		static final byte SWIRL_IMG_WIDTH = 14; 
		static final byte SWIRL_IMG_HEIGHT = 14; 

		// Since the swirl is a circle the collision buffer makes collision much more accurate 
		static final byte AIR_SPACING = 6;

		public Swirl(){

			super(SWIRL_X_REST_POS, SWIRL_Y_REST_POS, Data.swirlAnimation, false, 0, true, 0, 0, -1);
			resetState();

		}

		public void resetState(){	

			imageX = SWIRL_X_REST_POS;
			imageY = SWIRL_Y_REST_POS;

			x = SWIRL_X_REST_POS;
			y = SWIRL_Y_REST_POS;

			xSpeed = 0;
			ySpeed = 0;

			reset = true;

		}

		/** Returns true or false depending on if the swirl has collided with a block **/
		public boolean collidesWithBlock(){

			Block westNorth = Block.getBlock(x + AIR_SPACING + xSpeed, y + AIR_SPACING ) ;
			if (westNorth != null) return true;

			Block eastNorth = Block.getBlock(x + SWIRL_IMG_WIDTH + AIR_SPACING + xSpeed, y + AIR_SPACING) ;
			if (eastNorth != null) return true;

			Block westSouth = Block.getBlock(x + AIR_SPACING + xSpeed, y + SWIRL_IMG_HEIGHT + AIR_SPACING ) ;
			if (westSouth != null) return true;

			Block eastSouth = Block.getBlock(x + SWIRL_IMG_WIDTH + AIR_SPACING + xSpeed, y + SWIRL_IMG_HEIGHT + AIR_SPACING );
			if (eastSouth != null) return true;

			return false;

		}

		@Override public String toString(){
			return "Swirl   X: " + x + "  Y: " + y;
		}
	}





/*


				System.out.println((int)player.fallingVelocity);
				point1.setLocation( player.imageX + 2, player.imageY + 25 + (int) player.fallingVelocity );
				point2.setLocation( player.imageX + 23, player.imageY + 25 + (int) player.fallingVelocity);

				add(point1);
				add(point2);


				GRect point1 = new GRect(0 , 0, 3, 3);
				GRect point2 = new GRect(0 , 0, 3, 3);
				
				point1.setFillColor(Color.RED);
				point2.setFillColor(Color.RED);

				point1.setFilled(true);
				point2.setFilled(true);

*/



















