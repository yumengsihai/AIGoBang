package pres.hanshuo.ui;

import pres.hanshuo.config.GobangConfig;
import pres.hanshuo.listerer.GobangListener;
import pres.hanshuo.listerer.ListenerImpl;
import pres.hanshuo.pojo.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GobangUI extends JFrame implements GobangConfig {

    private ArrayList<Piece> pieces = new ArrayList<Piece>(225);

    public void initUI(){
        GobangListener gobangListener = new GobangListener();//监听器
        setTitle("五子棋");
        setSize(800,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jp1 = new JPanel();
        jp1.setPreferredSize(new Dimension(650, 0));
        jp1.setBackground(Color.LIGHT_GRAY);

        ImageIcon imageIcon = new ImageIcon("board.png");
        JPanel jp2 = new JPanel();
        jp2.setPreferredSize(new Dimension(150, 0));
        jp2.setBackground(Color.white);

        add(jp1, BorderLayout.WEST);
        add(jp2, BorderLayout.EAST);
        String[] buttonNames = {"开始新游戏", "悔棋", "认输"};
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            jp2.add(button);
            button.addActionListener(gobangListener);
        }

        String[] boxNames = {"人人对战", "人机对战"};
        JComboBox jComboBox = new JComboBox(boxNames);
        jComboBox.addActionListener(gobangListener);
        jp2.add(jComboBox);


        setVisible(true);



        addMouseListener(gobangListener);
        addKeyListener(gobangListener);


        Graphics graphics = getGraphics();
        gobangListener.setGraphics(graphics);
        gobangListener.setPieces(pieces);
        gobangListener.setJFrame(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Image image = new ImageIcon("board.png").getImage();

        for (int i = 0; i <= 14; i++) {
            g.drawLine(X, Y+i*SIZE, X+COLUMN*SIZE, Y+i*SIZE);
            g.drawLine(X+i*SIZE, Y, X+i*SIZE, Y+ROW*SIZE);
        }
        //将棋子重新绘画出来
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            if (piece != null){
                g.setColor(piece.getColor());
                g.fillOval(piece.getX(), piece.getY(), SIZE, SIZE);
            }
        }

    }

    public static void main(String[] args) {
        new GobangUI().initUI();
    }
}
