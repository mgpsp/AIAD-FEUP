package gui;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import agents.Direction;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

public class Door implements Drawable{
	private static final String IMGDEFAULT = "img/lift.png";
	private static final String IMGALLEDUP = "img/liftUp.png";
	private static final String IMGALLEDDOWN = "img/liftDown.png";
	private static final String IMGALLEDUPDOWN = "img/liftUpDown.png";
	
	private int x;
	private int y;
	private int floor;
	private Direction state;
	
	BufferedImage imgDefault, imgCalledUp, imgCalledDown, imgCalledUpDown;
	
	public Door(int x, int y, int numFloors) {
		this.x = x;
		this.y = y;
		this.floor = numFloors - y - 1;
		this.state = Direction.STOPPED;
		this.imgDefault = this.imgCalledUp = this.imgCalledDown = this.imgCalledUpDown = null;
		try {
			this.imgDefault = ImageIO.read(new File(IMGDEFAULT));
			this.imgCalledUp = ImageIO.read(new File(IMGALLEDUP));
			this.imgCalledDown = ImageIO.read(new File(IMGALLEDDOWN));
			this.imgCalledUpDown = ImageIO.read(new File(IMGALLEDUPDOWN));
		} catch (IOException e) {
			System.out.println("Couldn't load lift doors image");
		}
	}
	
	public void setState(Direction state, boolean remove) {
		switch(this.state) {
		case UP:
			if (!remove && state == Direction.DOWN)
				this.state = Direction.UPDOWN;
			else if (remove && state == Direction.UP)
				this.state = Direction.STOPPED;
			break;
		case DOWN:
			if (!remove && state == Direction.UP)
				this.state = Direction.UPDOWN;
			else if (remove && state == Direction.DOWN)
				this.state = Direction.STOPPED;
			break;
		case UPDOWN:
			if (remove && state == Direction.UP)
				this.state = Direction.DOWN;
			else if (remove && state == Direction.DOWN)
				this.state = Direction.UP;
			break;
		case STOPPED:
			if (!remove && state == Direction.UP)
				this.state = Direction.UP;
			else if (!remove && state == Direction.DOWN)
				this.state = Direction.DOWN;
			break;
		}
	}
	
	@Override
	public void draw(SimGraphics G) {
		switch(state) {
		case UP:
			G.drawImageToFit(imgCalledUp);
			break;
		case DOWN:
			G.drawImageToFit(imgCalledDown);
			break;
		case UPDOWN:
			G.drawImageToFit(imgCalledUpDown);
			break;
		case STOPPED:
			G.drawImageToFit(imgDefault);
			break;
		}
	}
	
	public int getFloor() {
		return floor;
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}

}
