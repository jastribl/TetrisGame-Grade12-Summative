//Justin Stribling
//Date Started: Monday, May 26, 2014
//Date Compleated: Monday, June 9, 2014
//Purpose: An Application designed to simulate Tetris®
package tetris;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class Tetris extends JFrame implements MouseListener, WindowListener {

    private final File tetrisSaveFile;
    private final JPanel panel;
    private final Timer gameTimer, moveTimer;
    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private Grid grid;
    private Shape nextShape, currentShape;
    private int score, level, totalLines, shapeMoveX, shapeMoveY, gameState, highScore = 0;
    private String highScorer = "";

    //Tetris Constructor
    //Pre: none
    //Use: initializes all variables and prepares the game to be played
    //Returns: none
    Tetris() {
        //initializes main components
        setTitle("Tetris - Justing Stribling - 2014");
        setJMenuBar(TetrisMenuBar());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(487, 652);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) screenSize.getWidth() - getWidth()) / 2, (((int) screenSize.getHeight() - getHeight()) / 2) - 25);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/Files/Tetris Frame Icon.png")).getImage());
        panel = new JPanel();
        panel.setFocusable(true);
        add(panel);
        setVisible(true);
        memoryImage = createImage(getWidth(), getHeight());
        memoryGraphics = memoryImage.getGraphics();
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Timer for game play (varies with level difficulty)
        gameTimer = new Timer(600, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (currentShape.isResting(grid)) {
                    grid.lock(currentShape);
                    currentShape = nextShape;
                    nextShape = newShape(true, 0);
                    if (currentShape.isResting(grid)) {
                        endGame();
                    }
                    int linesRemoved = grid.removeRows();
                    if (linesRemoved > 0) {
                        totalLines += linesRemoved;
                        score += linesRemoved * linesRemoved * 10 * (level + 1);
                        level = totalLines / 10;
                        gameTimer.setDelay(600 - (50 * level));
                    }
                }
                currentShape.move(grid, 0, 1);
                drawGame();
            }
        });
        //Timer for shape movement
        moveTimer = new Timer(60, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean requestDraw = false;
                if (shapeMoveX != 0 && currentShape.move(grid, shapeMoveX, 0)) {
                    requestDraw = true;
                }
                if (shapeMoveY != 0 && currentShape.move(grid, 0, shapeMoveY)) {
                    requestDraw = true;
                }
                if (requestDraw) {
                    drawGame();
                }
            }
        });
        //KeyListener for game play (shape movement and pause)
        panel.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == 80) {
                    if (gameTimer.isRunning()) {
                        pauseGame();
                    } else {
                        resumeGame();
                    }
                } else if (gameTimer.isRunning()) {
                    if (ke.getKeyCode() == 37) {
                        shapeMoveX = -1;
                    } else if (ke.getKeyCode() == 38) {
                        if (currentShape.rotate(grid)) {
                            drawGame();
                        }
                    } else if (ke.getKeyCode() == 39) {
                        shapeMoveX = 1;
                    } else if (ke.getKeyCode() == 40) {
                        shapeMoveY = 1;
                    } else if (ke.getKeyCode() == 32) {
                        currentShape.slamDown(grid);
                        drawGame();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == 40) {
                    shapeMoveY = 0;
                } else if (ke.getKeyCode() == 37 || ke.getKeyCode() == 39) {
                    shapeMoveX = 0;
                }
            }

            @Override
            public void keyTyped(KeyEvent ke) {
            }
        });
        //determines the type of game to be loaded (paused game or  new game)
        tetrisSaveFile = new File("data.TetrisFile");
        if (tetrisSaveFile.exists()) {
            if (!loadGame()) {
                newGame();
            }
        } else {
            saveGame(false);
            newGame();
        }
    }

    //loadGame
    //Pre: none
    //Use: reads all values from file and initializes game variables (for either a new game or a paused game)
    //Returns: whether a paused game was loaded (true) or a new game was started (false)
    private boolean loadGame() {
        try (DataInputStream fileReader = new DataInputStream(new FileInputStream(tetrisSaveFile))) {
            highScore = fileReader.readInt();
            highScorer = fileReader.readUTF();
            //if paused game was stored
            if (fileReader.readInt() == 1) {
                score = fileReader.readInt();
                level = fileReader.readInt();
                totalLines = fileReader.readInt();
                shapeMoveX = 0;
                shapeMoveY = 0;
                gameState = 2;
                gameTimer.setDelay(600 - (50 * level));
                grid = new Grid();
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 20; j++) {
                        grid.setColour(i, j, fileReader.readInt());
                        if (grid.getColour(i, j) != 0) {
                            grid.lock(i, j, true);
                        }
                    }
                }
                currentShape = newShape(false, fileReader.read() - 1);
                nextShape = newShape(false, fileReader.read() - 1);
                if (JOptionPane.showConfirmDialog(null, "Continue with your saved game?", "Continue?", JOptionPane.YES_NO_OPTION) != 0) {
                    newGame();
                    saveGame(false);
                }
                return true;
            }
        } catch (IOException ex) {
        }
        return false;
    }

    //saveGame
    //Pre: must be sent whether a paused game is to be saved
    //Use: saves the highscore information and if needed, a paused game to be loaded later
    //Returns: none
    private void saveGame(boolean savePausedGame) {
        try (DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(tetrisSaveFile))) {
            fileWriter.writeInt(highScore);
            fileWriter.writeUTF(highScorer);
            //if paused game needs loading
            if (savePausedGame) {
                fileWriter.writeInt(1);
                fileWriter.writeInt(score);
                fileWriter.writeInt(level);
                fileWriter.writeInt(totalLines);
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 20; j++) {
                        fileWriter.writeInt(grid.getColour(i, j));
                    }
                }
                fileWriter.write(currentShape.getColour());
                fileWriter.write(nextShape.getColour());
            } else {
                fileWriter.write(0);
            }
        } catch (IOException ex) {
        }
    }

    //TetrisMenuBar
    //Pre: none
    //Use: creates the menu bar for Tetris (including all Menus and MenuItems)
    //Retunrs: the MenuBar for Tetris
    private JMenuBar TetrisMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        //Make File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.addMouseListener(this);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        JMenuItem file = new JMenuItem("New Game");
        file.setMnemonic(KeyEvent.VK_N);
        file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        file.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
                if (JOptionPane.showConfirmDialog(null, "Would you like to start a new game?", "New Game?", JOptionPane.YES_NO_OPTION) == 0) {
                    endGame();
                }
            }
        });
        fileMenu.add(file);
        JMenuItem exit = new JMenuItem("Quit Game");
        exit.setMnemonic(KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.SHIFT_MASK));
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        fileMenu.add(exit);
        //Maek Highscore Menu
        JMenu highscoreMenu = new JMenu("Highscore");
        highscoreMenu.addMouseListener(this);
        highscoreMenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(highscoreMenu);
        JMenuItem resetHighScoreMenu = new JMenuItem("Reset Highscore");
        resetHighScoreMenu.setMnemonic(KeyEvent.VK_R);
        resetHighScoreMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        resetHighScoreMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the Highscore?", "Reset Highscore?", JOptionPane.YES_NO_OPTION) == 0) {
                    highScore = 0;
                    highScorer = "";
                    saveGame(false);
                    drawGame();
                    JOptionPane.showMessageDialog(null, "The Highscore has been reset.", "Highscore Reset", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        highscoreMenu.add(resetHighScoreMenu);
        //make Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.addMouseListener(this);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        JMenuItem aboutMenu = new JMenuItem("About Tetris®");
        aboutMenu.setMnemonic(KeyEvent.VK_B);
        aboutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK));
        aboutMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();

                JOptionPane.showMessageDialog(null, "About Tetris®\n\nVersion 1.0.0\n\n\n(C) Justin Stribling 2014\nAll Rights Reserved\n\n\nUse the keyboard to play the game:\n          Up Arrow         -----------------------------------    Rotate\n          Down Arrow   -----------------------------------    Move Down\n          Right Arrow    -----------------------------------    Move Right\n          Left Arrow      -----------------------------------    Move Left\n          Space Bar    ----------------------------------       Slam Down\n          ”P”                   -----------------------------------    Pause\n\n\nYou can save and exit your game at any time and pick up right where you left off later\n\nThe game keeps track of your highscore.  This can also be reset at any time (HighscoreReset)\n\n", "About Tetris - Justin Stribling - 2014", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutMenu);
        return menuBar;
    }

    //main
    //Pre: none
    //Use: starts a new game of Tetris
    //rRturns: none
    public static void main(String[] args) {
        Tetris tetris = new Tetris();
    }

    //newGame
    //Pre: none
    //Use: initializes game variables to start a new game
    //Returns: none
    private void newGame() {
        score = level = totalLines = shapeMoveX = shapeMoveY = gameState = 0;
        gameTimer.setDelay(600);
        grid = new Grid();
        currentShape = newShape(true, 0);
        nextShape = newShape(true, 0);
        panel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                panel.removeKeyListener(this);
                resumeGame();
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });
        drawGame();
    }

    //resumeGame
    //Pre: none
    //Use: resumes Tetris (starts all timers and changes gameState to 1 (running))
    //Returns: none
    private void resumeGame() {
        if (gameState != 1) {
            gameState = 1;
            gameTimer.start();
            moveTimer.start();
            drawGame();
        }
    }

    //pauseGame
    //Pre: none
    //Use: pauses Tetris (stops all timers and changes gameState to 2 (paused))
    //Returns: none
    private void pauseGame() {
        if (gameState == 1) {
            gameState = 2;
            drawGame();
            gameTimer.stop();
            moveTimer.stop();
        }
    }

    //endGame
    //Pre: none
    //Use: ends Game (stops all timers and determines whether a highscore has been achieved, then calls "newGame")
    //Returns: none
    private void endGame() {
        gameTimer.stop();
        moveTimer.stop();
        if (score > highScore) {
            highScore = score;
            String tempName = JOptionPane.showInputDialog("Congratulations!  You Won!\n\nPlease enter your Name");
            if (tempName == null) {
                highScorer = "Anonymous";
            } else {
                highScorer = tempName;
            }
            saveGame(false);
        } else {
            JOptionPane.showMessageDialog(null, "Game Over!");
        }
        newGame();
    }

    //quit
    //Pre: none
    //Use: if a game is running - ask to confirm close and save, if game is not running - exit
    //Returns: none
    private void quit() {
        pauseGame();
        if (gameState == 0) {
            System.exit(0);
        } else {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to Quit?", "Quit?", JOptionPane.YES_NO_OPTION) == 0) {
                if (JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save Game?", JOptionPane.YES_NO_OPTION) == 0) {
                    saveGame(true);
                } else {
                    saveGame(false);
                }
                System.exit(0);
            }
        }
    }

    //newShape
    //Pre: must be told whether to create a random shape or not (if not which shape to create)
    //Use: creates the shape that has been requested
    //Returns: the required Shape
    private Shape newShape(boolean randomShape, int givenShape) {
        Shape[] shapes = {new Square(), new Straight(), new RightL(), new LeftL(), new Tee(), new RightS(), new LeftS()};
        if (randomShape) {
            return shapes[(int) (Math.random() * 7)];
        } else {
            return shapes[givenShape];
        }
    }

    //paint
    //Pre: overrides the frame's paint method (must be sent that frame's Graphics(g))
    //Use: calls the frames paint method and drawGame
    //Returns: none
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }

    //drawGame
    //Pre: none
    //Use: paints all required components and visual aspects of the game
    //Returns: none
    public void drawGame() {
        //**All graphics are drawn to a temporary image and then drawn to the panel (double buffering) to prevent flickering**
        //clears game panel and draws all grid lines
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        grid.draw(memoryGraphics);
        memoryGraphics.setColor(Color.white);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                memoryGraphics.drawRect(330 + (i * 30), 30 + (j * 30), 30, 30);
            }
        }
        //draws all text
        memoryGraphics.drawString("Level: " + level, 315, 180);
        memoryGraphics.drawString("Score: " + score, 315, 210);
        memoryGraphics.drawString("Lines Cleared: " + totalLines, 315, 240);
        memoryGraphics.drawString("Highscore:  " + highScore, 315, 540);
        if (highScore != 0) {
            memoryGraphics.drawString("by:  " + highScorer, 330, 560);
        }
        //draws the current Shape and the next Shape
        currentShape.draw(memoryGraphics, 1, 1);
        nextShape.draw(memoryGraphics, 241, 91);
        if (gameState == 0) {//if the game is at the title screen, draw the title image
            memoryGraphics.drawImage(new ImageIcon(getClass().getResource("/Files/Tetris Main Logo.png")).getImage(), 1, 196, this);
        } else if (gameState == 1) {//if the game is paused, draw the paused image and appropriate text
            memoryGraphics.drawString("p to pause", 400, 590);
            memoryGraphics.drawImage(new ImageIcon(getClass().getResource("/Files/controls.png")).getImage(), 306, 300, this);
        } else if (gameState == 2) {//if the game is being played, draw the control image and appropriate text
            memoryGraphics.drawString("p to unpause", 400, 590);
            memoryGraphics.drawImage(new ImageIcon(getClass().getResource("/Files/Tetris Paused Logo.png")).getImage(), 1, 196, this);
        }
        //draws the temporary graphics to the panel
        panel.getGraphics().drawImage(memoryImage, 0, 0, panel);
    }

    //mousePressed
    //Pre: sent the mouse event
    //Use: pauses the game when the menuBar items are clicked
    //Returns: none
    @Override
    public void mousePressed(MouseEvent me) {
        pauseGame();
    }

    //mouseClicked/mouseReleased/mouseEntered/mouseExited (the unused abstract classes of MouseListener)
    //Pre: sent the mouse event
    //Use: none
    //Returns: none
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        quit();
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
}
