package jef.core.ui.swt.utils;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;

public class GIFMarkup
{
	private int imageNumber = 0;
	private ImageLoader loader;
	private Point displayPoint;
	
	public GIFMarkup(ImageLoader loader)
	{
		this.loader = loader;
	}
	
	public Point getDisplayPoint()
	{
		return this.displayPoint;
	}

	public void setDisplayPoint(Point displayPoint)
	{
		this.displayPoint = displayPoint;
	}

	public void draw(GC gc)
	{
		ImageData nextFrameData = loader.data[imageNumber % loader.data.length];
		Image frameImage = new Image(gc.getDevice(), nextFrameData);
		Point point = this.displayPoint;
		point.x -= nextFrameData.width / 2;
		point.y -= nextFrameData.height / 2;
		gc.drawImage(frameImage, point.x, point.y);
		frameImage.dispose();
		
		imageNumber += 1;
	}
}