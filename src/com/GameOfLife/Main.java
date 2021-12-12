package com.GameOfLife;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
import java.util.Hashtable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Main extends JFrame implements ActionListener {
    private static final Dimension MaxWindowDim = new Dimension(2000, 2000);
    private static final Dimension MinWindowDim = new Dimension(600, 600);

    private JMenuBar menu;
    private JMenuItem start, stop, reset, exit, help;
    private JMenuItem help_about;
    private int NoOfTransformationsPerSec = 3;
    private ShowGameWindow board;
    private Thread game;
    static JSlider SpeedSlider, SizeSlider;
    public  int CellDimension =20;
    public int speedval=50;

    public static void main(String[] args) {
        //Framing the GameBoard
        JFrame game = new Main();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set Title
        game.setTitle("John Conway's Game of Life");
        //Set the dimensions of Window
        game.setSize(MaxWindowDim);
        game.setMinimumSize(MinWindowDim);
        //Setting the Location
        game.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - game.getWidth())/2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - game.getHeight())/2);
        game.setVisible(true);
    }
//Initialize all Constructors in the Main
    public Main() {
        menu = new JMenuBar();
        setJMenuBar(menu);
        help = new JMenu("Help");
        menu.add(help);
        start = new JMenuItem("Start");
        menu.add(start);
        start.addActionListener(this);
        stop = new JMenuItem("Stop");
        stop.addActionListener(this);
        reset = new JMenuItem("Reset");
        reset.addActionListener(this);
        exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        //Add all the variable buttons to the Menu
        menu.add(stop);
        menu.add(reset);
        menu.add(exit);
        SpeedSlider = new JSlider();
        SizeSlider = new JSlider();
        menu.add(SpeedSlider);
        menu.add(SizeSlider);
        // Text for about gives the details of the transformations
        help_about = new JMenuItem("About");
        help_about.addActionListener(this);
        help.add(help_about);
        board = new ShowGameWindow();
        add(board);


        SpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                speedval=100-((JSlider) ce.getSource()).getValue();
                //System.out.println(100-((JSlider) ce.getSource()).getValue());
            }
        });
        SizeSlider.setMinimum(10);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        Hashtable<Integer, JLabel> labels1 = new Hashtable<>();
        labels1.put(50, new JLabel("SPEED"));
        labels.put(50, new JLabel("SIZE"));
        SpeedSlider.setLabelTable(labels1);
        SpeedSlider.setPaintLabels(true);
        SizeSlider.setLabelTable(labels);
        SizeSlider.setPaintLabels(true);
        SizeSlider.setValue(CellDimension);
        SizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                CellDimension =((JSlider) ce.getSource()).getValue();
                System.out.println(CellDimension);
                if(CellDimension >2){
                board.d_gameBoardSize = new Dimension(getWidth()/ CellDimension -2, getHeight()/ CellDimension -2);
                board.UpdatingTheSizeOfArray();    }        }
        });
    }

    public void CheckGameStatus(boolean IsGameStarted) {
        if (IsGameStarted) {
            start.setEnabled(false);
            stop.setEnabled(true);
            game = new Thread(board);
            game.start();
        } else {
            start.setEnabled(true);
            stop.setEnabled(false);
            game.interrupt();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(exit)) {
            // Exit and end the game
            System.exit(0);
        } else if (ae.getSource().equals(reset)) {
            board.resetBoard();
            board.repaint();
        } else if (ae.getSource().equals(start)) {
            CheckGameStatus(true);
        } else if (ae.getSource().equals(stop)) {
            CheckGameStatus(false);
        } else if (ae.getSource().equals(help_about)) {
            JOptionPane.showMessageDialog(null, "Any live cell with fewer than two live neighbours dies, as if by underpopulation.\n" +
                    "Any live cell with two or three live neighbours lives on to the next generation.\n" +
                    "Any live cell with more than three live neighbours dies, as if by overpopulation.\n" +
                    "Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.");
        }
    }

    private class ShowGameWindow extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {
        private Dimension d_gameBoardSize = null;
        private ArrayList<Point> point = new ArrayList<Point>(0);

        public ShowGameWindow() {
            // Add resizing listener
            addComponentListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void UpdatingTheSizeOfArray() {
            ArrayList<Point> removeList = new ArrayList<Point>(0);
            for (Point current : point) {
                if ((current.x > d_gameBoardSize.width-1) || (current.y > d_gameBoardSize.height-1)) {
                    removeList.add(current);
                }
            }
            point.removeAll(removeList);
            repaint();
        }

        public void PlottingPoint(int a, int b) {
            if (!point.contains(new Point(a,b))) {
                point.add(new Point(a,b));
            }
            repaint();
        }

        public void PlottingPoint(MouseEvent me) {
            int s = me.getPoint().x/ CellDimension -1;
            int m = me.getPoint().y/ CellDimension -1;
            if ((s >= 0) && (s < d_gameBoardSize.width) && (m >= 0) && (m < d_gameBoardSize.height)) {
                PlottingPoint(s,m);
            }
        }


        public void resetBoard() {
            point.clear();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                for (Point newPoint : point) {
                    // Draw new point
                    g.setColor(Color.RED);
                    g.fillRect(CellDimension + (CellDimension *newPoint.x), CellDimension + (CellDimension *newPoint.y), CellDimension, CellDimension);
                }
            } catch (ConcurrentModificationException cme) {}
            // Setup grid
            g.setColor(Color.black);
            for (int i=0; i<=d_gameBoardSize.width; i++) {
                g.drawLine(((i* CellDimension)+ CellDimension), CellDimension, (i* CellDimension)+ CellDimension, CellDimension + (CellDimension *d_gameBoardSize.height));
            }
            for (int i=0; i<=d_gameBoardSize.height; i++) {
                g.drawLine(CellDimension, ((i* CellDimension)+ CellDimension), CellDimension *(d_gameBoardSize.width+1), ((i* CellDimension)+ CellDimension));
            }
        }

        @Override
        public void componentResized(ComponentEvent e) {

            // Setup the game board size with proper boundries
            d_gameBoardSize = new Dimension(getWidth()/ CellDimension -2, getHeight()/ CellDimension -2);
            UpdatingTheSizeOfArray();
        }
        @Override
        public void componentMoved(ComponentEvent e) {}
        @Override
        public void componentShown(ComponentEvent e) {}
        @Override
        public void componentHidden(ComponentEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {
            // Mouse was released (user clicked)
            PlottingPoint(e);
        }
        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            // Mouse is being dragged, user wants multiple selections
            PlottingPoint(e);
        }
        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void run() {
            boolean[][] gameBoard = new boolean[d_gameBoardSize.width+2][d_gameBoardSize.height+2];
            for (Point current : point) {
                gameBoard[current.x+1][current.y+1] = true;
            }

            ArrayList<Point> AliveCells = new ArrayList<Point>(0);
            // Iterate through the array, follow game of life rules
            for (int i=1; i<gameBoard.length-1; i++) {
                for (int j=1; j<gameBoard[0].length-1; j++) {
                    int NeighbouringCell = 0;
                    if (gameBoard[i-1][j-1]) { NeighbouringCell++; }
                    if (gameBoard[i-1][j])   { NeighbouringCell++; }
                    if (gameBoard[i-1][j+1]) { NeighbouringCell++; }
                    if (gameBoard[i][j-1])   { NeighbouringCell++; }
                    if (gameBoard[i][j+1])   { NeighbouringCell++; }
                    if (gameBoard[i+1][j-1]) { NeighbouringCell++; }
                    if (gameBoard[i+1][j])   { NeighbouringCell++; }
                    if (gameBoard[i+1][j+1]) { NeighbouringCell++; }
                    if (gameBoard[i][j]) {
                        // Cell is alive, Can the cell live? (2-3)
                        if ((NeighbouringCell == 2) || (NeighbouringCell == 3)) {
                            AliveCells.add(new Point(i-1,j-1));
                        }
                    } else {
                        // Cell is dead, will the cell be given birth? (3)
                        if (NeighbouringCell == 3) {
                            AliveCells.add(new Point(i-1,j-1));
                        }
                    }
                }
            }
            resetBoard();
            point.addAll(AliveCells);
            repaint();
            try {
                Thread.sleep((speedval*20)/ NoOfTransformationsPerSec);
                run();
            } catch (Exception e) {}
        }
    }
}
