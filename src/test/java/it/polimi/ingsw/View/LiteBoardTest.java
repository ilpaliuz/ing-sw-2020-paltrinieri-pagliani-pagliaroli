package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


public class LiteBoardTest {


    Board tabella = new Board();
    Game game = new Game();
    Player player1 = new Player("roni",Color.Green, game);
    Player player2 = new Player("rsani",Color.Yellow, game);

    @Test
    public void testCostruttore (){
        Cell test1 = tabella.getCell(1,1);
        Cell test2 = tabella.getCell(2,1);
        player1.placeWorkers(test1);
        player1.placeWorkers(test2);
        Cell test3 = tabella.getCell(3,2);
        Cell test4 = tabella.getCell(0,1);
        player2.placeWorkers(test3);
        player2.placeWorkers(test4);
        LiteBoard test = new LiteBoard("ciao", tabella, game);
        assertEquals("Esatto",0, test.getLevelLR(1,0));
        System.out.println(test.getLevelLR(1,1));
        tabella.getCell(1,1).setLevel(1);
        test = new LiteBoard("ciao", tabella, game);
        test.printBoardCLI();

    }
    }

