import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.ArrayList;

public class Generator{
	public static void main(String[] args){
		Stack visited = new Stack();
		Maze maze = new Maze(Integer.parseInt(args[0]), Integer.parseInt(args[1]), true);
		maze.draw();
		
		// create gif
		ArrayList<BufferedImage> image = maze.renderGif();
		System.out.println("rendering gif");
		saveGif(image);

		// save final output
		BufferedImage png = maze.renderPng();
		System.out.println("rendering png");
		saveImage(png);
	}

	private static void saveGif(ArrayList<BufferedImage> frames){
		try{
			ImageOutputStream output = new FileImageOutputStream(new File("maze.gif"));
			GifSequenceWriter writer = new GifSequenceWriter(output, frames.get(0).getType(), 1, false);

			for(BufferedImage frame : frames){
				writer.writeToSequence(frame);
			}

			writer.close();
			output.close();
		} catch(Exception e){
			//
		}
	}

	private static void saveImage(BufferedImage image){
		try {
		    // retrieve image
		    File outputfile = new File("maze.png");
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			// do nothing
		}
	}
}