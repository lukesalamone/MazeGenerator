import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Stack;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class Maze{

	private int width;
	private int height;
	private Stack<Cell> visit;
	private Cell[][] grid;
	private int renderWidth;
	private int renderHeight;
	private boolean animate;
	private ArrayList<BufferedImage> frames;

	private final int CELL_SIZE = 5;

	public Maze(int width, int height, boolean animate){
		this.width = width;
		this.height = height;
		this.renderWidth = (width * 2) + 1;
		this.renderHeight = (height * 2) + 1;
		this.animate = animate;
		visit = new Stack<>();
		frames = new ArrayList<>();
		setupGrid();
	}

	/*
	1. Choose a starting point in the field.
	2. Randomly choose a wall at that point and carve a passage through to the adjacent cell, 
		but only if the adjacent cell has not been visited yet. This becomes the new current cell.
	3. If all adjacent cells have been visited, back up to the last cell that has uncarved walls 
		and repeat.
	4. The algorithm ends when the process has backed all the way up to the starting point.
	*/
	public void draw(){
		// find random starting cell
		Cell current = randomCell(false);
		grid[current.x()][current.y()].visit();

		// push to stack
		visit.push(current);

		int x, y, direction;
		int lastDirection = -1;

		// continue while stack is not empty
		while(!visit.isEmpty()){
			if(animate){
				log("drawing frame %s", frames.size());
				addFrame(visit.peek());
			}

			current = visit.peek();
			boolean invalid = true;
			x = current.x();
			y = current.y();

			System.out.println(String.format("at cell %s, %s", x, y));

			// 0 - up, 1 - right, 2 - down, 3 - left
			Stack<Integer> directions = new Stack<>();
			directions.push(0);
			directions.push(1);
			directions.push(2);
			directions.push(3);

			// add bias here

			Collections.shuffle(directions);

			do{
				if(directions.isEmpty()){
					direction = -1;
					visit.pop();
					break;
				}

				direction = directions.pop();

				String textDir = direction == 0 ? "up" 
					: direction == 1 ? "right" 
					: direction == 2 ? "down"
					: "left";

				try{
					switch(direction){
						case 0:	// cannot go up if at at top or cell above is visited
							invalid = y < 3 || grid[x][y-2].visited();
							break;
						case 1:
							invalid = x > renderWidth-3 || grid[x+2][y].visited();
							break;
						case 2:
							invalid = y > renderHeight-3 || grid[x][y+2].visited();
							break;
						case 3: 
							invalid = x < 3 || grid[x-2][y].visited();
							break;
					}
				} catch (java.lang.IndexOutOfBoundsException e){
					invalid = true;
				}
				log("cannot go %s", textDir);
			} while(invalid);

			// dead end!
			if(direction == -1){
				continue;
			}

			// we found a valid direction
			switch(direction){
				case 0:
					grid[x][y-2].visit();
					grid[x][y-1].visit();
					grid[x][y-1].setType(1);
					visit.push(grid[x][y-2]);
					break;
				case 1:
					grid[x+2][y].visit();
					grid[x+1][y].visit();
					grid[x+1][y].setType(1);
					visit.push(grid[x+2][y]);
					break;
				case 2:
					grid[x][y+2].visit();
					grid[x][y+1].visit();
					grid[x][y+1].setType(1);
					visit.push(grid[x][y+2]);
					break;
				case 3:
					grid[x-2][y].visit();
					grid[x-1][y].visit();
					grid[x-1][y].setType(1);
					visit.push(grid[x-2][y]);
					break;
			}
		}
	}

	public ArrayList<BufferedImage> renderGif(){
		frames.add(renderPng());
		return frames;
	}

	public BufferedImage renderPng(){
		return render(BufferedImage.TYPE_INT_ARGB, null);
	}

	// create an image out of the maze grid
	private BufferedImage render(int type, Cell current){
		BufferedImage image = new BufferedImage(renderWidth * CELL_SIZE, renderHeight * CELL_SIZE, type);

		int white = new Color(255, 255, 255).getRGB();
		int black = new Color(0, 0, 0).getRGB();
		int red = new Color(255, 0, 0).getRGB();

		for(int x = 0; x < renderWidth * CELL_SIZE; x++) {
		    for(int y = 0; y < renderHeight * CELL_SIZE; y++) {
		    	int color = grid[x / CELL_SIZE][y / CELL_SIZE].visited() ? white : black;
		    	image.setRGB(x, y, color);
		    }
		}

		// color current node
		if(current != null){
			for(int i=0; i<CELL_SIZE; i++){
				for(int j=0; j<CELL_SIZE; j++){
					image.setRGB((current.x() * CELL_SIZE + i), (current.y() * CELL_SIZE + j), red);
				}
			}
		}

		return image;
	}

	// create grid of walls and open spaces
	private void setupGrid(){
		int x = renderWidth;	// width of rendered image
		int y = renderHeight;	// height of rendered image
		grid = new Cell[x][y];

		for(int i=0; i<x; i++){
			for(int j=0; j<y; j++){
				Cell cell = new Cell(i, j);

				if(i==x-1 || j==y-1 || i%2==0 || j%2==0){
					cell.setType(0);	// wall
				} else {
					cell.setType(1);	// open
				}

				grid[i][j] = cell;
			}
		}

		// add entrance and exit
		grid[1][0].visit();
		grid[renderWidth-1][renderHeight-2].visit();
	}

	private Cell randomCell(boolean includeWalls){
		if(includeWalls){
			int x = (int)Math.floor(Math.random() * width);
			int y = (int)Math.floor(Math.random() * height);
			return grid[x][y];
		} else {
			int x = (int)Math.floor(Math.random() * width) * 2 + 1;
			int y = (int)Math.floor(Math.random() * height) * 2 + 1;
			System.out.println(String.format("returning %s, %s", x, y));
			return grid[x][y];
		}
	}

	private void addFrame(Cell current){
		BufferedImage image = render(BufferedImage.TYPE_INT_ARGB, current);
		frames.add(image);
	}

	private void log(Object...args){
		System.out.println(String.format(args[0].toString(), Arrays.copyOfRange(args, 1, args.length)));
	}
}