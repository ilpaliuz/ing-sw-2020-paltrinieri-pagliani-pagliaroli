package it.polimi.ingsw.Model;

public class Cell {
    private int numRow;
    private int numColumn;
    private int level;
    private boolean isEmpty;
    private Board board;

    public Cell (int Row, int Column){
        this.numRow=Row;
        this.numColumn=Column;

        this.isEmpty=true;
        this.level=0;
    }

    public void setNumRow(int i){
        this.numRow=i;
    }
    public void setNumColumn(int j){
        this.numColumn=j;
    }
    public int getNumRow(){
        return this.numRow;
    }
    public int getNumColumn(){
        return this.numColumn;
    }
    public int getLevel(){
        return this.level;
    }
    public boolean isEmpty(){
        return this.isEmpty;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    //metodi

    public void setEmptyDefault() {
        this.isEmpty = true;
    }
// si deve aggiungere l'eccezione che il worker è sul bordo
   /* public boolean canMoveTo(Cell destination) {
        return ((






                (destination.getNumRow()>this.getNumRow()-1)&&(<destination.getNumColumn()<)
                &&(destination.getNumRow()<=this.getNumRow()+1)&&
                (destination.getNumColumn()>=this.getNumColumn()-1)&&
                (destination.getNumColumn()<=this.getNumColumn()+1)&&
                (destination.isEmpty())&&
                (destination.level<=this.level+1));


    } */

}
