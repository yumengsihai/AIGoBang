package pres.hanshuo.listerer;

import pres.hanshuo.pojo.Pair;
import pres.hanshuo.pojo.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class GobangListener extends ListenerImpl{

    private int[][] coordinates = new int[15][15];    //棋子坐标
    private ArrayList<Piece> pieces;
    private Graphics graphics;
    private boolean turn = false; //黑白棋交换
    private JFrame jFrame;
    private boolean start = false; //判断棋局是否开始
    private Stack<Pair> stack = new Stack<Pair>();

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
        if (start == true){
            if (coordinates[row][column] == 0 && row <= 14 && column <= 14){    //检查当前位置是否可以放入棋子
                if (turn == false){
                    graphics.setColor(Color.black);
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.black);
                    pieces.add(piece);  //存入arraylist中
                    coordinates[row][column] = 1;//黑棋为1，放入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = true;
                    if (tiltLeft(row, column, coordinates) || titleRight(row, column, coordinates) ||
                            horizontal(row, column, coordinates) || vertical(row, column, coordinates)){
                        JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
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
                    if (tiltLeft(row, column, coordinates) || titleRight(row, column, coordinates) ||
                            horizontal(row, column, coordinates) || vertical(row, column, coordinates)){
                        JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
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
        else{
            JOptionPane.showMessageDialog(null, "请点击开始新游戏", "Error", JOptionPane.PLAIN_MESSAGE);

        }



    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("开始新游戏")){
            start = true;
            pieces.clear();
            coordinates = new int[15][15];
            stack.clear();
            jFrame.repaint();
        }
        else if (e.getActionCommand().equals("悔棋")){
            if (stack.size() == 0){
                JOptionPane.showMessageDialog(null, "您不能在悔棋了", "Error", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            //如果白棋悔棋，则还是白棋继续。如果是黑棋悔棋，则还是黑棋
            if (turn == true){
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
                    JOptionPane.showMessageDialog(null, "请直接点击开始新游戏", "Warning", JOptionPane.PLAIN_MESSAGE);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "暂不支持此功能！", "Error", JOptionPane.PLAIN_MESSAGE);
                    break;
            }
        }
    }

    //判断左斜方向是否赢了
    public boolean tiltLeft(int x, int y, int[][] coordinates){
        int count = 1;  //记录棋子个数
        //先向左上方向检查
        int j = y;
        for (int i = x - 1; i >= 0 ; i--) {
            if (j > 0 && coordinates[x][y] == coordinates[i][j - 1]){
                count++;
                j--;
            }
            else{
                j = y;
                break;
            }
        }
        //在向右下方检查
        for (int i = x + 1; i < coordinates.length; i++) {
            if (j < 14 && coordinates[x][y] == coordinates[i][j + 1]){
                count++;
                j++;
            }
            else{
                break;
            }
        }
        return count == 5;
    }
    //判断右斜方向是否赢棋
    public boolean titleRight(int x, int y, int[][] coordinates){
        int count = 1;
        int j = y;
        //先向右上方检查
        for (int i = x - 1; i > 0; i--) {
            if (j < 14 && coordinates[x][y] == coordinates[i][j + 1]){
                count++;
                j++;
            }
            else{
                j = y;
                break;
            }
        }
        //在向左下方检查
        for (int i = x + 1; i < coordinates.length ; i++) {
            if (j > 0 && coordinates[x][y] == coordinates[i][j - 1]){
                count++;
                j--;
            }
            else {
                break;
            }
        }
        return count == 5;
    }

    //水平方向检查
    public boolean horizontal(int x, int y, int[][] coordinates){
        int count = 1;
        int i;
        //先向左检查
        for (i = y; i > 0; i--) {
            if (coordinates[x][y] == coordinates[x][i-1]){
                count++;
            }
            else {
                break;
            }
        }
        //在向右检查
        for (i = y; i < coordinates.length-1; i++) {
            if (coordinates[x][y] == coordinates[x][i+1]){
                count++;
            }
            else {
                break;
            }
        }
        return count == 5;
    }

    //垂直方向检查
    public boolean vertical(int x, int y, int[][] coordinates){
        int count = 1;
        int i;
        //先向上检查
        for (i = x; i > 0 ; i--) {
            if (coordinates[x][y] == coordinates[i-1][y]){
                count++;
            }
            else{
                i = x;
                break;
            }
        }
        //在向下检查
        for ( i = x; i < coordinates.length-1; i++) {
            if (coordinates[x][y] == coordinates[i+1][y]){
                count++;
            }
            else{
                break;
            }
        }
        return count == 5;
    }

}
