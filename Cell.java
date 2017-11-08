public class Cell{
	private boolean visited;
	private int x;
	private int y;
	private int type;

	public Cell(int x, int y){
		this.x = x;
		this.y = y;
	}

	public boolean visited(){
		return this.visited;
	}

	public int x(){
		return this.x;
	}

	public int y(){
		return this.y;
	}

	public void visit(){
		this.visited = true;
	}

	// type 0: wall
	// type 1: open
	public void setType(int type){
		this.type = type;
	}

	public int type(){
		return type;
	}
}
