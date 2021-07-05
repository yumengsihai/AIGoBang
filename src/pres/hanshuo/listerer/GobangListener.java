package pres.hanshuo.listerer;

import pres.hanshuo.pojo.AI;
import pres.hanshuo.pojo.Pair;
import pres.hanshuo.pojo.Piece;
import pres.hanshuo.pojo.Rules;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @author Li Hanshuo
 */

public class GobangListener extends ListenerImpl{

    private int[][] coordinates = new int[15][15];    //棋子坐标
    private int[][] rewards = new int[15][15];    //权值坐标
    private int reward = 10;    //初始权值
    private ArrayList<Piece> pieces;
    private Graphics graphics;
    private boolean turn = false; //黑白棋交换
    private JFrame jFrame;
    private boolean start = false; //判断棋局是否开始
    private Stack<Pair> stack = new Stack<Pair>();
    private boolean isComp = false; //游戏开始时默认不是人机对战，是人人对战
    private Rules rules = new Rules(coordinates);   //规则对象，并将棋子坐标传入
    private AI ai = new AI(rewards, reward); //传入AI对象，作为人机对战是下棋的决策


    public void setJFrame(JFrame jFrame) {
        this.jFrame = jFrame;
    }
    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }
    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    //用于删除Arraylist的第一个元素，实现悔棋功能
    public static void removeFirstItem(int index, List<Piece> lst){
        Iterator<Piece> iter = lst.iterator();
        while (iter.hasNext()) {
            Piece item = iter.next();
            if (item.equals(lst.get(index))) {
                iter.remove();
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //获取当前鼠标指针的坐标
        int x = e.getX();
        int y = e.getY();
        //将坐标转换成行列值
        int row = (y + SIZE/2 - Y) / SIZE;
        int column = (x + SIZE/2 - X) / SIZE;

        //根据行列值将棋子放在中心位置
        int xCorrection = column * SIZE + X - SIZE / 2;
        int yCorrection = row * SIZE + Y - SIZE / 2;
        if (start && !isComp){
            if (coordinates[row][column] == 0 && row <= 14 && column <= 14){    //检查当前位置是否可以放入棋子
                if (!turn){
                    graphics.setColor(Color.black);
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.black);
                    pieces.add(piece);  //存入arraylist中
                    coordinates[row][column] = 1;//黑棋为1，放入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = true;
                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, coordinates) ||
                            rules.horizontal(row, column, coordinates) || rules.vertical(row, column, coordinates)){
                        JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                        turn = false;
                        pieces.clear();
                        coordinates = new int[15][15];
                        stack.clear();
                        start = false;
                        jFrame.repaint();
                    }
                }
                else {
                    graphics.setColor(Color.white);
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.white);
                    pieces.add(piece);
                    coordinates[row][column] = 2;//将棋子位置存入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = false;
                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, coordinates) ||
                            rules.horizontal(row, column, coordinates) || rules.vertical(row, column, coordinates)){
                        JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                        turn = false;
                        pieces.clear();
                        coordinates = new int[15][15];
                        stack.clear();
                        start = false;
                        jFrame.repaint();
                    }
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "请换一个位置", "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else if (start && isComp){
            if (coordinates[row][column] == 0 && row <= 14 && column <= 14){    //检查当前位置是否可以放入棋子
                if (!turn){
                    graphics.setColor(Color.black);
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.black);
                    pieces.add(piece);  //存入arraylist中
                    coordinates[row][column] = 1;//黑棋为1，放入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = true;

                    ai.AICheck(coordinates, rewards);
                    for (int i = 0; i < rewards.length; i++) {
                        System.out.println(Arrays.toString(rewards[i]));
                    }

                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, coordinates) ||
                            rules.horizontal(row, column, coordinates) || rules.vertical(row, column, coordinates)){
                        JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                        turn = false;
                        pieces.clear();
                        coordinates = new int[15][15];
                        rewards = new int[15][15];
                        stack.clear();
                        start = false;
                        jFrame.repaint();
                    }
                }

            }
            else{
                JOptionPane.showMessageDialog(null, "请换一个位置", "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "请点击开始新游戏", "Error", JOptionPane.PLAIN_MESSAGE);

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isComp && start){
            if (turn){
                graphics.setColor(Color.white);
                ai.AICheck(coordinates, rewards);
                for (int i = 0; i < rewards.length; i++) {
                    System.out.println(Arrays.toString(rewards[i]));
                }
                //确定rewards数组中的最大值并获取其索引
                int maxX = 0;
                int maxY = 0;
                int max = rewards[0][0];
                for (int i = 0; i < rewards.length; i++){
                    for (int j = 0; j < rewards.length; j++){
                        if (rewards[i][j] > max){
                            max = rewards[i][j];
                            maxX = i;
                            maxY = j;
                        }
                    }
                }
                System.out.println("max = " + max);
                System.out.println("maxX = " + maxX);
                System.out.println("maxY = " + maxY);
                int xCorrection1 = maxY * SIZE + X - SIZE / 2;
                int yCorrection1 = maxX * SIZE + Y - SIZE / 2;
                graphics.fillOval(xCorrection1, yCorrection1, SIZE, SIZE);
                Piece piece = new Piece(xCorrection1, yCorrection1, Color.white);
                pieces.add(piece);
                coordinates[maxX][maxY] = 2;//将棋子位置存入array中
                stack.push(new Pair(maxX, maxY));//入栈
                turn = false;
                rewards = new int[15][15];
                if (rules.tiltLeft(maxX, maxY, coordinates) || rules.titleRight(maxX, maxY, coordinates) ||
                        rules.horizontal(maxX, maxY, coordinates) || rules.vertical(maxX, maxY, coordinates)){
                    JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                    turn = false;
                    pieces.clear();
                    coordinates = new int[15][15];
                    rewards = new int[15][15];
                    stack.clear();
                    start = false;
                    jFrame.repaint();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("开始新游戏")){
            turn = false;
            start = true;
            pieces.clear();
            coordinates = new int[15][15];
            stack.clear();
            jFrame.repaint();
        }
        else if (e.getActionCommand().equals("悔棋")){
            if (!isComp){
                if (stack.size() == 0){
                    JOptionPane.showMessageDialog(null, "您不能在悔棋了", "Error", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                //如果白棋悔棋，则还是白棋继续。如果是黑棋悔棋，则还是黑棋
                if (turn){
                    turn = false;
                }
                else {
                    turn = true;
                }

                removeFirstItem(pieces.size() - 1, pieces);
                Pair pair = stack.pop();
                coordinates[pair.getLeft()][pair.getRight()] = 0;
                jFrame.repaint();
            }
            else{
                if (stack.size() == 0){
                    JOptionPane.showMessageDialog(null, "您不能在悔棋了", "Error", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                //只有玩家才会悔棋，所以悔棋后直接是黑棋继续
                turn = false;
                //直接删除连续的黑棋和白棋
                removeFirstItem(pieces.size() - 1, pieces);
                removeFirstItem(pieces.size() - 1, pieces);
                Pair pair1 = stack.pop();
                coordinates[pair1.getLeft()][pair1.getRight()] = 0;
                Pair pair2 = stack.pop();
                coordinates[pair2.getLeft()][pair2.getRight()] = 0;
                jFrame.repaint();
            }
        }
        else if (e.getActionCommand().equals("认输") && start){
            if (turn == true){
                JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
            }
            pieces.clear();
            coordinates = new int[15][15];
            stack.clear();
            turn = false;
            jFrame.repaint();
        }
        else if (e.getActionCommand().equals("认输") && !start){
            JOptionPane.showMessageDialog(null, "对局尚未开始，无法认输", "Error", JOptionPane.PLAIN_MESSAGE);

        }
        else if (e.getSource() instanceof JComboBox){
            JComboBox jComboBox = (JComboBox) e.getSource();
            int selectedIndex = jComboBox.getSelectedIndex();
            switch (selectedIndex){
                case 0 :
                    isComp = false;
                    JOptionPane.showMessageDialog(null, "请直接点击开始新游戏", "Warning", JOptionPane.PLAIN_MESSAGE);
                    break;
                case 1:
                    isComp = true;
                    JOptionPane.showMessageDialog(null, "请直接点击开始新游戏！", "Error", JOptionPane.PLAIN_MESSAGE);
                    break;
            }
        }
    }

}
