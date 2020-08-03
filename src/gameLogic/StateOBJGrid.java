package gameLogic;

public class StateOBJGrid implements IStateOBJ{
	private int[][] grid;

	public StateOBJGrid(int[][] grid){
		this.grid = grid;
	}

	@Override
	public int[][] getGrid() {
		return grid;
	}

	@Override
	public void overwriteGrid(int[][] grid) {
		this.grid = grid;
	}

	@Override
	public boolean equals(Object obj){
		try {
			StateOBJGrid s = (StateOBJGrid) obj;
			if(compareArrays(grid, s.grid)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			System.out.println("StateOBJGrid.equals error!");
			return false;
		}
	}

	private boolean compareArrays(int[][] array1, int[][] array2) {
		boolean b = true;
		if (array1 != null && array2 != null){
			if (array1.length != array2.length && array1[0].length != array2[0].length){
				b = false;
			}
			else{
				for (int i = 0; i < array2.length; i++) {
					for (int j = 0; j < array2[0].length; j++) {
						if (array2[i][j] != array1[i][j]) {
							b = false;    
						} 
					}           
				}
			}
		}else{
			b = false;
		}
		return b;
	}
}
