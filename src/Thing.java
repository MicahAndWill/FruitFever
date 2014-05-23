/**
 *	Thing - This is the superclass for Animation, Scenery, and most other classes which draw an image on-screen.
 *
 * @Author Micah Stairs
 *
 **/

import acm.graphics.*;
import java.awt.*;

public class Thing extends Rectangle{

	/** Public instance variables **/
	public GImage image;
	int imageX, imageY;
	boolean active = true;
	
	// Used to alter the underlying Rectangle's boundaries, without adjusting the size of the image's or its position
	public int boundaryLeft, boundaryRight, boundaryTop, boundaryBottom;
	
	/** Constructors that define the image **/
	
	public Thing(int x, int y, int width, int height, GImage image){
		
		super(x, y, width, height);
		this.image = new GImage(image.getImage());
		this.imageX = x;
		this.imageY = y;
		
		// This call is important for images that do not move (such as the levelBackDrop)
		this.animate();
	}
	
	public Thing(int x, int y, GImage image){
		this(x, y, (int) image.getWidth(), (int) image.getHeight(), image);
	}
	
	public Thing(int x, int y, GImage image, boolean mirrorRandomly){
		this(x, y, (int) image.getWidth(), (int) image.getHeight(), mirrorRandomly ? ImageTransformer.mirrorRandomly(image) : image);
	}
	
	/** Constructors that do not define the image **/
	
	public Thing(int x, int y, int width, int height){
		super(x, y, width, height);
		this.imageX = x;
		this.imageY = y;
	}
	
	public Thing(int x, int y){
		this(x, y, Data.TILE_SIZE, Data.TILE_SIZE);	
	}

	/** Update image **/
	public void animate(){
	
		updateLocation();
		
		// Moves image to be on screen only if it needs to be on the screen
		if (x < FruitFever.SCREEN_WIDTH) 
		
			// The '+ Data.TILE_SIZE' prevents the bug of the tiles accumulating
			// at the bottom of the screen
			if (y < FruitFever.SCREEN_HEIGHT + Data.TILE_SIZE)
				image.setLocation(imageX - FruitFever.viewX, imageY  - FruitFever.viewY);		
		
	
		// Changes the size of bounding box
		setSize((int)image.getWidth() - boundaryLeft + boundaryRight, (int)image.getHeight() - boundaryTop + boundaryBottom);
		
	}

	/** This is the old .animate() method, we still need it to correctly position blocks and things
	 * in some bug situations **/
	public void naturalAnimate(){

		// Changes the position of the bounding box responsible for collision detection 
		setLocation(imageX + boundaryLeft - FruitFever.viewX, imageY + boundaryTop - FruitFever.viewY);

		// places image at the correct position on the screen
		image.setLocation(imageX - FruitFever.viewX, imageY  - FruitFever.viewY);		

		// Changes the size of bounding box
		setSize((int)image.getWidth() - boundaryLeft + boundaryRight, (int)image.getHeight() - boundaryTop + boundaryBottom);

	}
	
	/** Change image **/
	public void changeImage(GImage img) {
		img = new GImage(img.getImage());
		image.setImage(img.getImage());
	}
	
	/** Adjusts all boundary variables **/
	public void adjustBoundaries(int left, int right, int top, int bottom) {
		boundaryLeft = left;
		boundaryRight = right;
		boundaryTop = top;
		boundaryBottom = bottom;
	}
	
	public void updateLocation() {
		// Changes the position of the bounding box responsible for collision detection 
		setLocation(imageX + boundaryLeft - FruitFever.viewX, imageY + boundaryTop - FruitFever.viewY);
	}

}