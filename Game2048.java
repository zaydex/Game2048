package com.javarush.games.game2048;

import com.javarush.engine.cell.*;


public class Game2048 extends Game
{
    private boolean isGameStopped = false;
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private int score = 0;


    @Override
    public void initialize()
    {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame()
    {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }

    private void drawScene()
    {
        for (int j = 0; j < SIDE; j++)
        {
            for (int i = 0; i < SIDE; i++)
            {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }

    private void createNewNumber()
    {
        int maxValue = getMaxTileValue();
        if(maxValue >= 2048)
        {
            win();
            return;
        }
        boolean isCreated = false;
        do
        {
            int x = getRandomNumber(SIDE);
            int y = getRandomNumber(SIDE);
            int random = getRandomNumber(10);
            if(gameField[y][x] == 0)
            {
                if (random < 9)
                {
                    gameField[y][x] = 2;
                } else if (random == 9)
                {
                    gameField[y][x] = 4;
                }
                isCreated = true;
            }
        }while (!isCreated);
    }

    private void setCellColoredNumber(int x, int y, int value)
    {
        Color color = getColorByValue(value);
        String str = value > 0 ? "" + value : "";
        setCellValueEx(x, y, color, str);
    }

    private Color getColorByValue (int value)
    {
        switch (value) {
            case 0:
                return Color.WHITE;
            case 2:
                return Color.PLUM;
            case 4:
                return Color.SLATEBLUE;
            case 8:
                return Color.DODGERBLUE;
            case 16:
                return Color.DARKTURQUOISE;
            case 32:
                return Color.MEDIUMSEAGREEN;
            case 64:
                return Color.LIMEGREEN;
            case 128:
                return Color.DARKORANGE;
            case 256:
                return Color.SALMON;
            case 512:
                return Color.ORANGERED;
            case 1024:
                return Color.DEEPPINK;
            case 2048:
                return Color.MEDIUMVIOLETRED;
            default:
                return Color.NONE;
        }
    }

    public void setCellValueEx (int x, int y, Color cellColor, String value)
    {
        setCellValue(x, y, value);
        setCellColor(x, y, cellColor);
    }

    private boolean compressRow(int[] row)
    {
        boolean isMoved = false;
        int insertPosition = 0;
        for (int i = 0; i < SIDE; i++) {
            if(row[i] != 0) {
                if (i != insertPosition) {
                    row[insertPosition] = row[i];
                    row[i] = 0;
                    isMoved = true;
                }
                insertPosition++;
            }
        }
        return isMoved;
    }

    private boolean mergeRow (int[] row)
    {
        boolean isMoved = false;
        for (int i = 0; i < row.length - 1; i++)
        {
            if(row[i] != 0 && row[i+1] == row[i])
            {
                row[i] += row[i + 1];
                row[i + 1] = 0;
                isMoved = true;
                score += row[i];
                setScore(score);
            }
        }
        return isMoved;
    }


    @Override
    public void onKeyPress(Key key)
    {
        //Game Over
        if (isGameStopped)
        {
            if (key == Key.SPACE)
            {
                isGameStopped = false;
                score = 0;
                setScore(score);
                createGame();
            } else
            {
                return;
            }
        }
        if(!canUserMove())
        {
            gameOver();
            return;
        }
        //Press Left, Right, Up, Down key
        if(key == Key.LEFT)
        {
            moveLeft();
        }
        else if (key == Key.RIGHT)
        {
            moveRight();
        }
        else if (key == Key.UP)
        {
            moveUp();
        }
        else if (key == Key.DOWN)
        {
            moveDown();
        }
        else
        {
            return;
        }
        drawScene();
    }

    private void moveLeft()
    {
        boolean isDone = false;
        for (int i = 0; i < SIDE; i++)
        {
            boolean wasCompressed = compressRow(gameField[i]);
            boolean wasMerged = mergeRow(gameField[i]);

            if(wasMerged)
            {
                compressRow(gameField[i]);
            }

            if(wasCompressed || wasMerged)
            {
                isDone = true;
            }
        }

        if(isDone)
        {
            createNewNumber();
        }
    }

    private void moveRight()
    {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp()
    {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown()
    {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise()
    {
        int[][] temp = new int[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++)
        {
            for (int j = 0; j < SIDE; j++)
            {
                temp[j][SIDE - 1 - i] = gameField[i][j];
            }
        }
        gameField = temp;
    }


    private int getMaxTileValue()
    {
        int maxNumber = gameField[0][0];
        for (int i = 0; i < SIDE; i++)
        {
            for (int j = 0; j < SIDE; j++)
            {
                if(maxNumber < gameField[i][j])
                {
                    maxNumber = gameField[i][j];
                }
            }
        }
        return maxNumber;
    }

    private void win()
    {
        showMessageDialog(Color.GREEN, "You won!", Color.WHITE, 72);
        isGameStopped = true;
    }

    private boolean canUserMove()
    {
        //true, if we have element "0" in our matrix, two numbers are equal horizontal or vertical
        for (int i = 0; i < SIDE; i++)
        {
            for (int j = 0; j < SIDE; j++)
            {
                if(gameField[i][j] == 0)
                {
                    return true;
                }
                //vertical
                else if(i < SIDE - 1 && gameField[i][j] == gameField[i+1][j])
                {
                    return true;
                }
                //horizontal
                else if(j < SIDE - 1 && gameField[i][j] == gameField[i][j+1])
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void gameOver()
    {
        showMessageDialog(Color.RED, "You lost!", Color.WHITE, 72);
        isGameStopped = true;
    }
}
