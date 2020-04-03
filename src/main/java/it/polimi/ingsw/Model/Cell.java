package it.polimi.ingsw.Model;

public class Cell {
    private int numRow;
    private int numColumn;
    private int level;
    private boolean isEmpty;

    public Cell (int riga, int colonna) throws IllegalArgumentException{
        if(riga<5&&colonna<5) {
            this.numColumn = colonna;
            this.numRow = riga;
            this.isEmpty = true;
            this.level = 0;
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public boolean isEmpty(){
        return this.isEmpty;
    }

    public boolean canMoveTo(Cell destination) {
        return  (destination.getNumRow() >= 0) && (destination.getNumRow() <= 4) &&
                (destination.getNumColumn() >= 0) && (destination.getNumColumn() <= 4) &&
                (destination.getNumRow() >= this.numRow - 1) &&
                (destination.getNumRow() <= this.numRow + 1) &&
                (destination.getNumColumn() >= this.numColumn - 1) &&
                (destination.getNumColumn() <= this.numColumn + 1) &&
                (destination.getLevel() >= this.level - 1) &&
                (destination.getLevel() <= this.level + 1) &&
                (destination.isEmpty());
    }

    public boolean canBuildIn(Cell destination){
        return ((destination.getNumRow() >= 0) && (destination.getNumRow() <= 4) &&
                (destination.getNumColumn() >= 0) && (destination.getNumColumn() <= 4) &&
                destination.getNumRow() >= this.numRow - 1) &&
                (destination.getNumRow() <= this.numRow + 1) &&
                (destination.getNumColumn() >= this.numColumn - 1) &&
                (destination.getNumColumn() <= this.numColumn + 1) &&
                (destination.isEmpty()) && (destination.getLevel() <= 4);
    }

//  ********** GETTER AND SETTER ******************

    public int getNumRow(){
        return this.numRow;
    }
    public int getNumColumn(){
        return this.numColumn;
    }
    public int getLevel(){
        return this.level;
    }
    public void setLevel(int level) {
        if(0 <= level && level <= 5) this.level = level;
    }
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

}
