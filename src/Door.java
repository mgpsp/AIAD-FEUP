import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

public class Door implements Drawable{
	private static final String IMGDEFAULT = "imgDefault.jpg";
	private static final String IMGALLEDUP = "imgCalledUp.jpg";
	private static final String IMGALLEDDOWN = "imgCalledDown.jpg";
	private static final String IMGALLEDUPDOWN = "imgCalledUpDown.jpg";
	
	private int x;
	private int y;
	private Direction state;
	
	BufferedImage imgDefault, imgCalledUp, imgCalledDown, imgCalledUpDown;
	
	public Door(int x, int y) {
		this.x = x;
		this.y = y;
		this.state = Direction.STOPPED;
		this.imgDefault = this.imgCalledUp = this.imgCalledDown = this.imgCalledUpDown = null;
		try {
			this.imgDefault = ImageIO.read(new File(IMGDEFAULT));
			this.imgCalledUp = ImageIO.read(new File(IMGALLEDUP));
			this.imgCalledDown = ImageIO.read(new File(IMGALLEDDOWN));
			this.imgCalledUpDown = ImageIO.read(new File(IMGALLEDUPDOWN));
		} catch (IOException e) {
			System.out.println("Couldn't load elevators image");
		}
	}
	
	public void setState(Direction state) {
		this.state = state;
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
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}

}
