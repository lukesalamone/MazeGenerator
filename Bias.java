import java.util.Stack;

public class Bias{
	public static Stack<Integer> horizontalBias(Stack<Integer> directions, int last){
		for(int i=0; i<5; i++){
			directions.push(1);
			directions.push(3);
		}

		return directions;
	}	

	public static Stack<Integer> verticalBias(Stack<Integer> directions, int last){
		for(int i=0; i<5; i++){
			directions.push(0);
			directions.push(2 );
		}

		return directions;
	}	

	public static Stack<Integer> spiralBias(Stack<Integer> directions, int last){
		int next = (int)Math.floor(Math.random() * 4);
		switch(last){
			case -1:
				break;
			case 0:
			case 1:
			case 2:
			case 3:
				next = (next + 1) % 4;
				break;
		}

		for(int i=0; i<100; i++){
			directions.push(next);
		}

		return directions;
	}
}