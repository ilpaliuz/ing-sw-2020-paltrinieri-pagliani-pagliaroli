package it.polimi.ingsw.View.Graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import it.polimi.ingsw.utils.Color;
import it.polimi.ingsw.utils.Divinity;

/**
 * this class represent the button of the Map and it's used mainly to override the repainting of the button if
 * there is a worker or a construction on it
 */
public class BoardButton extends JButton {

    private int row;
    private int column;
    private BufferedImage worker = null;
    private BufferedImage selectedFrame = null;
    private BufferedImage levelImage = null;

    private int level=0;
    private boolean haveWorker=false;
    private boolean selectableCell=false;
    private Color workerColor;
    private int workerNum=0;

    public void paint(Graphics g){
        super.paint(g);
        String path;
        if ((getLevel()==1)||(getLevel()==2)||(getLevel()==3)||(getLevel()==4)){
            path = "images/Level/Level"+getLevel()+".png";
            try {
                levelImage = ImageIO.read(new File(path));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            g.drawImage(levelImage, 5, 5,80,80, null);
        }
        if (haveWorker) {
            if (workerColor == Color.White) {
                path = "images/Workers/whiteWorker.png";
            } else if (workerColor == Color.Red) {
                path = "images/Workers/brownWorker.png";
            } else {
                path = "images/Workers/purpleWorker.png";
            }
            try {
                worker = ImageIO.read(new File(path));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            g.drawImage(worker, 10, 10, null);
        }
        if (selectableCell){
            path = "images/frameMove.png";
            try {
                selectedFrame = ImageIO.read(new File(path));
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            g.drawImage(selectedFrame,0,0,null);
        }
    }

    /**
     * set the selectableCell variable to the value of the
     * @param selectableCell and repaint the button itself
     */
    public void setSelectableCell(boolean selectableCell) {
        this.selectableCell = selectableCell;
        this.setEnabled(selectableCell);
        this.repaint();
    }

    /**
     * it's used to know if a worker in the cell represented by the button can build in a specific cell
     * @param destination represent where you want to know if you can build
     * @return  true if you can build in the destination cell or false if you can't
     */
    public boolean canBuildIn(BoardButton destination){
        return (destination.getRow() >= row - 1) &&
                (destination.getRow() <= row + 1) &&
                (destination.getColumn() >= column - 1) &&
                (destination.getColumn() <= column + 1) &&
                (!destination.getHaveWorker()) &&
                (destination.getLevel() < 4) &&
                (!this.equals(destination));
    }


    /**
     * it's used to know if a worker in the cell represented by the button can move in a specific cell using
     * eventualy a specific GodPower
     * @param destination represent the cell where you want to move in
     * @param power represent the power that you are using to move or Default if are not using any GodPower
     * @return true if you can move to the destination cell or false if you can't
     */
    public boolean canMoveTo(BoardButton destination, Divinity power) {
        return  // Should be one of the 8 cell near the worker
                destination.getRow() >= row - 1 &&
                        destination.getRow() <= row + 1 &&
                        destination.getColumn() >= column - 1 &&
                        destination.getColumn() <= column + 1 &&
                        // can be maximum one level higher
                        // unless the divinity is prometheus
                        ((power != Divinity.Prometheus && destination.getLevel() <= level + 1) ||
                                (power == Divinity.Prometheus && destination.getLevel() <= level)) &&

                        // should not be a dome
                        destination.getLevel() < 4 &&
                        // should not be the same cell as worker's position
                        !this.equals(destination) &&
                        // should not be another worker's cell
                        // unless the divinity is Apollo or Minotaur
                        (!destination.getHaveWorker() ||
                                power == Divinity.Apollo || power == Divinity.Minotaur);
    }

    public BoardButton(String string, Icon icon){
        super(string, icon);
    }

    public boolean getHaveWorker() {
        return haveWorker;
    }

    public void setHaveWorker(boolean haveWorker) {
        this.haveWorker = haveWorker;
    }

    public Color getWorkerColor() {
        return workerColor;
    }

    public void setWorkerColor(Color workerColor) {
        this.workerColor = workerColor;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public void setWorkerColorNull(){
        workerColor=null;
    }

}
