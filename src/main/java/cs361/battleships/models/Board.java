package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	@JsonProperty
	private List<Ship> ships;
	@JsonProperty
	private List<Result> attacks;
	@JsonProperty
	private List<Square> sonars;
	private int rows;
	private int cols;
	private int  numAttacks;
	//private int numSonars;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		rows = 10;
		cols = 10;
		//numSonars = 2;
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		sonars = new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		if (ships.size() >= 4) {
			return false;
		}
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}

		//String kind = ship.getKind();
		final var placedShip = new Ship(ship.getKind());

		placedShip.place(y, x, isVertical);
		if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
			return false;
		}
		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
	//	ship.setCaptainsQuarters(x, y, isVertical);
		return true;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {

		Result result = new Result();

		// check for attack result in bounds of grid
		if (x < 1 || x > 10 || y < 'A' || y > 'J') {
			result.setResult(AtackStatus.INVALID);
			return result;
		}

		// check to see if attack location has been chosen before
		for (Result allResults: this.attacks) {
			if ((allResults.getLocation().getColumn() == y) && (allResults.getLocation().getRow() == x)) {
				result.setResult(AtackStatus.INVALID);
				return result;
			}
		}

		this.attacks.add(result);

		// loops through all ships, and looks at each ships coordinates and if one of them
		// matches with the attack coordinates, occupy the square and set the status of the
		// square to 'HIT'
		for (Ship ships: this.ships) {
			List<Square> taken = ships.getOccupiedSquares();

			for (Square occupied : taken) {
				if ((occupied.getRow() == x) && (occupied.getColumn() == y)) {
					result.setResult(AtackStatus.HIT);
					result.setLocation(occupied);

					// knowing that it is a hit, loop through all attacks performed and if
					// there are 14 hits, the total amount of squares for ships at the moment,
					// then set the game to surrender and end the game
					for (Result allResults: this.attacks) {
						if (allResults.getResult() == AtackStatus.HIT) {
							this.numAttacks++;
						}
					}
					if (this.numAttacks == 14) {
						result.setResult(AtackStatus.SURRENDER);
					}

					return result;
				}
				// else, if it is not a hit and the square is not invalid, it is a 'MISS'
				// and the square status of the square should be set to 'MISS'
				else {
					Square square = new Square(x,y);
					result.setLocation(square);
					result.setResult(AtackStatus.MISS);
				}
			}
		}

		return result;
	}

	public boolean sonar(int x, char y, int numSonars) {

		// conditional to return false if all sonars are used or no ships have been sunk
		if (!ifSonar() || numSonars == 0) {
			return false;
		}

		int r = 2;
		int i;

		List<Square> squares = new ArrayList<>();

		// loop to create the radius for the sonar just like in 'game.js'
		for (i = 1; i < (r + 1); i++) {
			squares.add(new Square((x - i), y));
			squares.add(new Square((x + i), y));
			// typecasting is need for the rest of these because we cannot pass in 'int' for function
			squares.add(new Square(x, (char) ((int) y - i)));
			squares.add(new Square(x, (char) ((int) y + i)));
		}
		squares.add(new Square(x, y));
		squares.add(new Square((x - 1), (char) ((int) y - 1)));
		squares.add(new Square((x - 1), (char) ((int) y + 1)));
		squares.add(new Square((x + 1), (char) ((int) y - 1)));
		squares.add(new Square((x + 1), (char) ((int) y + 1)));

		for (Square s : squares) {
			// this adds the squares that the sonar is used on; if there are square that aren't on the board, then
			// we just ignore it, but if it is in it, then we add it to sonar list
			if ((s.getRow() <= 10) && (s.getRow() > 0) && (((int) s.getColumn() - 65) < 10) && (((int) s.getColumn() - 65) >= 0)) {
				this.sonars.add(s);
			}
		}

		Result result = new Result();
		for (Ship ships: this.ships) {
			List<Square> taken = ships.getOccupiedSquares();
			for (Square sonar: this.sonars) {
				for (Square occupied : taken) {
					if ((occupied.getRow() == sonar.getRow()) && (occupied.getColumn() == sonar.getColumn())) {
						result.setResult(AtackStatus.HIT);
						result.setLocation(occupied);
					} else {
						Square square = new Square(x, y);
						result.setLocation(square);
						result.setResult(AtackStatus.MISS);
					}
				}
			}
		}
		//this.numSonars--;

		return true;

	}

	private boolean ifSonar() {

		// checks to see if any ships have been sunk in order to use the sonar weapon
		for (Result r : this.attacks) {
			if (r.getResult() == AtackStatus.SUNK) {
				return true;
			}
		}

		return false;

	}

	public Result moveFleet(String Dir){

		Result result = new Result();
		AtackStatus temp;

		if(Dir.equals("UP")){
			for(int i = 0; i < ships.size(); i++){
				boolean move = true;
				for(int j = 0; j< ships.get(i).getOccupiedSquares().size();j++){
					if(ships.get(i).getOccupiedSquares().get(j).getRow() < 2);{
						move = false;
					}
				}
				if(move){
					for(int j=0; j<ships.get(i).getOccupiedSquares().size();j++){
						int x = ships.get(i).getOccupiedSquares().get(j).getRow();
						ships.get(i).getOccupiedSquares().get(j).setRow(x-1);
					}
				}
			}
		}

		else if(Dir.equals("DOWN"))
		{
			for(int i = 0; i < ships.size(); i++){
				boolean move = true;
				for(int j = 0; j< ships.get(i).getOccupiedSquares().size();j++){
					if(ships.get(i).getOccupiedSquares().get(j).getRow() > 9);{
						move = false;
					}
				}
				if(move){
					for(int j=0; j<ships.get(i).getOccupiedSquares().size();j++){
						int x = ships.get(i).getOccupiedSquares().get(j).getRow();
						ships.get(i).getOccupiedSquares().get(j).setRow(x+1);
					}
				}
			}
			temp = AtackStatus.MOVEFLEET;
			result.setResult(temp);
		}





		return result;
	}

	List<Ship> getShips() {
		return ships;
	}

	public List<Square> getSonars() {
		return this.sonars;
	}

	public int getRows() {
		return this.rows;
	}

	public int getCols() {
		return this.cols;
	}

	/*public int getNumSonars() {
		return this.numSonars;
	}*/
}
