import java.util.Arrays;

public class GameLogic {
    private String gameDifficulty;
    private String gameBoard;
    private boolean gameOver;
    public boolean clientWon;
    public boolean draw;
    int movesMade = 0;

    public GameLogic(String gameDifficulty){
        this.gameDifficulty = gameDifficulty;
        gameOver = false;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public void updateClientGameBoard(String gameBoard){
        this.gameBoard = gameBoard;
        movesMade++;
    }

    public String[] createServerMove(){
        AI_MinMax aiMinMax = new AI_MinMax(gameBoard);

        if (checkIfGameWon(aiMinMax.init_board, 'O')){ //Player wins
            clientWon = true;
            return aiMinMax.init_board;
        }
        else if (movesMade == 9){ //Draw
            gameOver = true;
            draw = true;
            return aiMinMax.init_board;
        }

        movesMade++;
        if (gameDifficulty.equals("Hard")){
            return createHardMove(aiMinMax);
        }
        else if (gameDifficulty.equals("Medium")){
            return createMediumMove(aiMinMax);
        }
        else{
            return createEasyMove(aiMinMax);
        }
    }

    private String[] createHardMove(AI_MinMax aiMinMax){
        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                if (checkIfGameWon(aiMinMax.init_board, 'X'))
                    System.out.println("Server won");
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 0) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == -10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        return new String[]{"Hard Pick Failed"};
    }

    private String[] createMediumMove(AI_MinMax aiMinMax){
        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 0) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == -10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                checkIfGameWon(aiMinMax.init_board, 'X');
                return aiMinMax.init_board;
            }
        }

        return new String[]{"Medium Pick Failed"};
    }

    private String[] createEasyMove(AI_MinMax aiMinMax){
        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == -10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 0) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                return aiMinMax.init_board;
            }
        }

        for (Node node : aiMinMax.movesList) {
            if (node.getMinMax() == 10) {
                aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                checkIfGameWon(aiMinMax.init_board, 'X');
                return aiMinMax.init_board;
            }
        }

        return new String[]{"Easy Pick Failed"};
    }

    public boolean checkIfGameWon(String[] gameBoard, char player){ //X X X  O O O  X X X
        for (int i = 0; i < 3; i++){
            if (gameBoard[i].charAt(0) == player && gameBoard[i + 1].charAt(0) == player && gameBoard[i + 2].charAt(0) == player){
                gameOver = true;
                return true;
            }
        }

        for (int i = 0; i < 3; i++){
            if (gameBoard[i].charAt(0) == player && gameBoard[i + 3].charAt(0) == player && gameBoard[i + 6].charAt(0) == player){
                gameOver = true;
                return true;
            }
        }

        if (gameBoard[0].charAt(0) == player && gameBoard[4].charAt(0) == player && gameBoard[8].charAt(0) == player){
            gameOver = true;
            return true;
        }

        if (gameBoard[2].charAt(0) == player && gameBoard[4].charAt(0) == player && gameBoard[6].charAt(0) == player){
            gameOver = true;
            return true;
        }

        return false;
    }

    public int pointsWon(){
        if (!clientWon)
            return 0;

        if (gameDifficulty.equals("Hard"))
            return 5;
        if (gameDifficulty.equals("Medium"))
            return 3;
        return 1;
    }
}
