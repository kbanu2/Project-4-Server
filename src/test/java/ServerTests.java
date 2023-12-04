import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
public class ServerTests {
    @Test
    public void testAiLoadBoard(){
        AI_MinMax aiMinMax = new AI_MinMax("b b b b b b b b b");
        String[] expected = {"b", "b", "b","b", "b", "b", "b", "b", "b"};

        assertArrayEquals(expected, aiMinMax.init_board);
        assertNotEquals(0, aiMinMax.movesList.size());
    }

    @Test
    public void testAiNoOption(){
        AI_MinMax aiMinMax = new AI_MinMax("O O O O O O O O O");
        String[] expected = {"b", "b", "b","b", "b", "b", "b", "b", "b"};

        //assertArrayEquals(expected, aiMinMax.init_board);
        assertEquals(0, aiMinMax.movesList.size());
    }

    @Test
    public void testAiHasWinningOption(){
        AI_MinMax aiMinMax = new AI_MinMax("b b X X b b O O b");
        int max = -10;

        for (Node node : aiMinMax.movesList){
            max = Integer.max(max, node.getMinMax());
        }

        assertEquals(10, max);
    }

    @Test
    public void testAIHasDrawOption(){
        AI_MinMax aiMinMax = new AI_MinMax("O b b b b b b b b");
        int max = -10;

        for (Node node : aiMinMax.movesList){
            max = Integer.max(max, node.getMinMax());
        }

        assertEquals(0, max);
    }

    @Test
    public void testAIHasLosingOption(){
        AI_MinMax aiMinMax = new AI_MinMax("O O O b b b b b b");
        int max = -10;

        for (Node node : aiMinMax.movesList){
            max = Integer.max(max, node.getMinMax());
        }

        assertEquals(-10, max);
    }

    @Test
    public void testCreateValidServer(){
        assertDoesNotThrow(() -> {
            Server s = new Server(new Consumer<Serializable>() {
                @Override
                public void accept(Serializable serializable) {}
            }, 1000);
        });
    }

    @Test
    public void testServerConstructor(){
        Server s = new Server(new Consumer<Serializable>() {
            @Override
            public void accept(Serializable serializable) {}
        }, 1001);

        assertEquals(1001, s.port);
        assertNotNull(s.playerRankings);
        assertNotNull(s.callback);
        assertNotNull(s.serverThread);
    }

    @Test
    public void testInitializeGameState(){
        GameState gameState = new GameState();
        String[] expected = {"b", "b", "b","b", "b", "b", "b", "b", "b"};

        assertArrayEquals(expected, gameState.gameBoard);
        assertFalse(gameState.gameOver);
        assertFalse(gameState.clientWon);
        assertFalse(gameState.draw);
        assertNotNull(gameState.topScorePoints);
        assertNotNull(gameState.topScoreNames);
    }

    @Test
    public void testGameLogicConstructor(){
        GameLogic gameLogic = new GameLogic("b b X X b b O O b");

        assertFalse(gameLogic.isGameOver());
        assertFalse(gameLogic.draw);
        assertEquals(0, gameLogic.movesMade);
    }

    @Test
    public void testGameLogicUpdateGameBoard(){
        GameLogic gameLogic = new GameLogic("b b X X b b O O b");
        gameLogic.updateClientGameBoard("b b X X b b O O b");

        assertEquals(1, gameLogic.movesMade);
    }

    @Test
    public void testGameLogicGameWon(){
        GameLogic gameLogic = new GameLogic("b b X X b b O O b");

        String[] expected = new String[]{"O", "O", "O","b", "b", "b", "b", "b", "b"};
        assertTrue(gameLogic.checkIfGameWon(expected, 'O'));

        expected = new String[]{"O", "b", "b", "O", "b", "b", "O", "b", "b"};
        assertTrue(gameLogic.checkIfGameWon(expected, 'O'));

        expected = new String[]{"O", "b", "b", "b", "O", "b", "b", "b", "O"};
        assertTrue(gameLogic.checkIfGameWon(expected, 'O'));

        expected = new String[]{"b", "b", "O", "b", "O", "b", "O", "b", "b"};
        assertTrue(gameLogic.checkIfGameWon(expected, 'O'));
    }
}
