# Java实现人机五子棋

本文主要内容为通过Java语言实现一个具备人人对战、人机对战功能的五子棋，具体分为以下几个大步骤：

1. 绘制棋盘与棋子
2. 编写输赢判断条件
3. 编写事件监听器
4. 实现初级人机五子棋

## 0. 项目开始前的准备工作

项目开始之前，先创建好以下类和接口

1. GobangConfig接口 --- 五子棋的各项参数配置。
2. ListenerImpl类 --- 实现MouseListener, KeyListener, MouseMotionListener, ActionListener, GobangConfig等接口，重写所有方法，但不具体实现。
3. GobangListener类 --- 继承ListenerImpl类，重写其此项目需要使用到的方法。
4. AI类 --- 用于实现人机对战时电脑自动找到最佳位置下棋。
5. Piece类 --- 棋子类。
6. Pair类 --- 自定义的一种数据结构。
7. Rules类 --- 用于验证游戏输赢。
8. GobangUI --- 绘画棋盘和棋子，以及相关按钮。

## 1. 绘制棋盘与棋子

### 1.1 创建棋盘的默认参数

首先在创建好的GobangConfig接口中定义一些默认参数，这些参数一旦定义好便无需修改。

1. 整个棋盘左上角的坐标。目的是为了将棋盘与整个程序的四周分隔开来，如下图。

	![截屏2021-07-07 下午2.57.48](/Users/lihanshuo/Desktop/蓝杰代码/五子棋截屏/截屏2021-07-07 下午2.57.48.png)

2. 棋盘线与线之间的间距。

3. 棋盘的行列数目。

具体参数如下

```java
package pres.hanshuo.config;

/**
 * @author Li Hanshuo
 */
public interface GobangConfig {

    //棋盘左上角的坐标
    int X = 40;
    int Y = 60;

    //棋盘线与线之间的间距
    int SIZE = 40;

    //棋盘行列数目
    int COLUMN = 14;//列
    int ROW = 14;//行
}
```

**注意：**在GUI界面里，x轴为水平向右，y轴为水平向下。正宗的五子棋是15 * 15的棋盘，但是在使用for循环画棋盘时，i是从0开始，退出条件是i <= 14, 所以最终画出来的棋盘依旧是15 * 15的棋盘。

### 1.2 绘制棋盘与棋子

#### 1.2.1 定义棋子类

一个棋子主要有三个参数：

1. 棋子的x坐标
2. 棋子的y坐标
3. 棋子的颜色（黑色和白色）

明确了这三个参数之后便可以在我们的Piece类中对这三个参数进行声明，同时写出带有这三个参数的有参构造方法，get()，equals()和hashcode()方法（这里我是用IDEA自动生成出来的），具体代码如下：

```java
/**
 * @author Li Hanshuo
 */
public class Piece {

    private int x;
    private int y;
    private Color color;

    public Piece(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return x == piece.x && y == piece.y && Objects.equals(color, piece.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }
}
```



#### 1.2.2 绘制棋盘，棋子和按钮

完成Piece类之后，我们便可以在我们的GobangUI类中绘制我们的棋盘。

首先继承JFrame类实现GobangConfig接口，重写paint()方法。然后定义一个新的ArrayList，长度为225，类型为Piece类。（因为15*15的棋盘最多可以下入225颗棋子），并且创建返回值为void的initUI()方法和main()方法。

initUI()方法主要实现JFrame作为整个程序的最底层GUI框架，然后使用两个JPanel来分别放入棋盘与菜单按钮（JButton和JComboBox实现），并且将我们的ArrayList，graphics，和JFrame传给后续创建的GobangListener事件监听器。

initUI()方法

```java
public void initUI(){
    GobangListener gobangListener = new GobangListener();//创建一个新的五子棋监听器
    setTitle("五子棋"); //设置标题
    setSize(800,700);   //设置界面大小
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置关闭方式

    JPanel jp1 = new JPanel();  //用于存放棋盘的JPanel
    jp1.setPreferredSize(new Dimension(650, 0));    //设置JPanel大小
    jp1.setBackground(Color.LIGHT_GRAY);    //将JPanel的背景颜色设置为浅灰色

    JPanel jp2 = new JPanel();  //用于存放菜单的JPanel
    jp2.setPreferredSize(new Dimension(150, 0));    //设置JPanel大小
    jp2.setBackground(Color.white); //将JPanel的背景颜色设置为白色

    //使用BorderLayout，分别将jp1和jp2分别置于整个界面的左右两侧
    add(jp1, BorderLayout.WEST);
    add(jp2, BorderLayout.EAST);

    //创建菜单按钮
    String[] buttonNames = {"开始新游戏", "悔棋", "认输"};
    for (int i = 0; i < buttonNames.length; i++) {
        JButton button = new JButton(buttonNames[i]);
        jp2.add(button);
        button.addActionListener(gobangListener);
    }

    //创建下拉菜单选项
    String[] boxNames = {"人人对战", "人机对战"};
    JComboBox jComboBox = new JComboBox(boxNames);
    jComboBox.addActionListener(gobangListener);
    jp2.add(jComboBox);

    //将整个页面设置为可见
    setVisible(true);

    //将新声明的五子棋监听器分别传入MouseListener和KeyListene
    addMouseListener(gobangListener);
    addKeyListener(gobangListener);

    //获取JFrame中的Graphics
    Graphics graphics = getGraphics();
    //将Graphics，pieces（存放棋子的ArrayList）和当前JFrame传入五子棋监听器
    gobangListener.setGraphics(graphics);
    gobangListener.setPieces(pieces);
    gobangListener.setJFrame(this);
}
```

paint()方法主要画出棋盘，并且每下一个棋，将其重新绘制出来。

```java
@Override
    public void paint(Graphics g) {
        super.paint(g);
        //画棋盘
        for (int i = 0; i <= 14; i++) {
            //画出棋盘的所有竖线。X, Y+i*SIZE为每条竖线的最上点，X+COLUMN*SIZE, Y+i*SIZE为每条竖线的最下点
			g.drawLine(X, Y+i*SIZE, X+ROW*SIZE, Y+i*SIZE);
            //画出棋盘的所有横线。X+i*SIZE, Y为每条横线的最左点，X+i*SIZE, Y+ROW*SIZE为每条横线的最右点
            g.drawLine(X+i*SIZE, Y, X+i*SIZE, Y+COLUMN*SIZE);            
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
```

## 2. 编写输赢判断条件

Rules类中实现输赢判断条件。

**思路：**对于一个15 * 15的棋盘，我们可以创建一个15 * 15的二维数组来存储每个棋子的位置（这里的位置是棋盘的行列值，取值范围值0-14，而不是游戏界面的上的坐标值，后续在编写鼠标事件时会着重讲述如何通过鼠标指针的坐标值转换成行列值）。在这个二维数组中，0代表此位置为空，1代表黑棋，2代表白棋。每次无论黑方还是白方下棋之后，通过落棋点分别向8个方向遍历（上、下、左、右、左上、左下、右上、右下），若该方向同颜色的棋子大于等于5个，该方便获取胜利。具体代码如下:

```java
package pres.hanshuo.pojo;

/**
 * @author Li Hanshuo
 */
public class Rules {
    private int[][] coordinates;


    public Rules(int[][] coordinates) {
        this.coordinates = coordinates;
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
        return count >= 5;
    }
    //判断右斜方向是否赢棋
    public boolean titleRight(int x, int y, int[][] coordinates){
        int count = 1;
        int j = y;
        //先向右上方检查
        for (int i = x - 1; i >= 0; i--) {
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
        return count >= 5;
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
        return count >= 5;
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
        return count >= 5;
    }
}

```

## 3. 编写事件监听器

在我们的GobangListener中首先创建以下成员变量及其部分set方法

```java
private int[][] coordinates = new int[15][15];    //棋子坐标
    private int[][] rewards = new int[15][15];    //权值坐标
    private int reward = 10;    //初始权值
    private ArrayList<Piece> pieces;	//存放棋子，用于JFrame中的重绘
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
```

### 3.1 人人对战与人机对战鼠标事件监听

玩家每次都会在十字交叉点落棋，但是玩家下棋时不会真正的完全将鼠标放在十字交叉点上再去点击，只是会在十字交叉点的附近点击。而且每次画出棋子时，我们需要将鼠标作为棋子的圆心画出棋子，而不是棋子的左上角（想象一下当我们在Windows桌面进行右键刷新时，鼠标并不是在弹窗的中心，而是在弹窗的左上角。同理我们在画棋时需要将棋子移到鼠标的中心）在这里便需要处理两个问题：

1. 将玩家的棋子通过计算，放在十字交叉点的中心。

2. 每次画棋都以鼠标为棋子的圆心。

==下棋之前的先行判断条件：==

1. 游戏是否开始，模式时人人对战还是人机对战
2. 当前位置是否为空，算出来的行列值是否在棋盘之内
3. 当前轮换时黑棋还是白棋

上述条件都满足之后，便可进行下棋，并将棋子的信息（行列值及其颜色）存入到ArrayList中，用于上述JFrame中paint()方法的重绘棋子，这里还要将棋子压入一个stack中，用于后续的悔棋操作，并且每下一颗棋子，就要判断是否满足五连珠的条件，若满足，则当前对局结束，清空上述所有数据结构，清空棋盘，并将start置于false。

完整代码：

```java
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
        //如果对局开始且是人人对战
        if (start && !isComp){
            if (coordinates[row][column] == 0 && row <= 14 && column <= 14){    //检查当前位置是否可以放入棋子
                if (!turn){
                    //设置棋子颜色
                    graphics.setColor(Color.black);
                    //画出棋子
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.black); //生成棋子信息
                    pieces.add(piece);  //将每个棋子信息存入arraylist中
                    coordinates[row][column] = 1;//黑棋为1，放入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = true;
                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, 								coordinates) || rules.horizontal(row, column, coordinates) || rules.vertical(row, 						  column, coordinates)){
                        JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                        turn = false;   //转换成黑棋
                        pieces.clear(); //清除存放棋子的array
                        coordinates = new int[15][15];  //清空存放棋子行列值的array
                        stack.clear();
                        start = false;  //对局结束，新对局未开始
                        jFrame.repaint();   //重新绘画棋盘
                    }
                }
                else {
                    //设置棋子颜色
                    graphics.setColor(Color.white);
                    //画出棋子
                    graphics.fillOval(xCorrection, yCorrection, SIZE, SIZE);
                    Piece piece = new Piece(xCorrection, yCorrection, Color.white); //生成棋子信息
                    pieces.add(piece);	//将每个棋子信息存入arraylist中
                    coordinates[row][column] = 2;//将棋子位置存入array中
                    stack.push(new Pair(row, column));//入栈
                    turn = false;
                    //判断输赢
                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, 								coordinates) || rules.horizontal(row, column, coordinates) || rules.vertical(row, 						  column, coordinates)){
                        //弹窗
                        JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);
                        turn = false;   //转换成黑棋
                        pieces.clear(); //清除存放棋子的array
                        coordinates = new int[15][15];  //清空存放棋子行列值的array
                        stack.clear();
                        start = false;  //对局结束，新对局未开始
                        jFrame.repaint();   //重新绘画棋盘
                    }
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "请换一个位置", "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
        //如果对局开始且是人机对战
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

                    if (rules.tiltLeft(row, column, coordinates) || rules.titleRight(row, column, 								coordinates) ||
                            rules.horizontal(row, column, coordinates) || rules.vertical(row, column, 							coordinates)){
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

    /**
     * 实现人机的在最佳位置的自动落子
     * @param e
     */
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
                        rules.horizontal(maxX, maxY, coordinates) || rules.vertical(maxX, maxY, 							coordinates)){
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
```

### 3.2 编写按钮监听事件

1. “开始新游戏”

	- start = true; 由于前面鼠标控制器判断，防止玩家在游戏为开始时就可以下棋。
	- turn = false; 此目的是每次开始新游戏后都是黑方先下棋。
	- pieces.clear(); 这里清空存放棋子的ArrayList，防止在本局重绘棋子时重绘了上局的棋子。
	- coordinates = new int[15] [15]; 对应棋盘行列值的二维数组，新游戏开始时将所有位置都置为空。
	- stack.clear(); 清空栈中所有棋子，防止本局悔棋时出现悔到了上局棋的错误。
	- jFrame.repaint(); 重绘棋盘。

2. “悔棋”

	玩家点击“悔棋”时，先判断stack是否为空，若为空则弹窗错误。若不为空则pop出栈顶棋子，根据棋子信息将coordinates数组的对应位置设为0，然后重新绘制棋盘。这里要注意的是，若当前对局为人人对战，也要吧turn设置为其相反值，防止下棋顺序出现错乱。若为人机对战，则连续进行两次pop和coordinates数组对应位置设为0操作，turn无需做任何改变。

3. “认输”

	首先判断对局是否开始，若开始，则根据turn的boolean值来判断是哪方认输，然后进行如下操作：

	```java
	pieces.clear();
	coordinates = new int[15][15];
	stack.clear();
	turn = false;
	jFrame.repaint();
	```

	否则，发出弹窗警告，“对局为开始，无法认输”。

4. 模式选择

	根据JComboBox的index判断当前晚间选择的模式是“人人对战”还是“人机对战”，若为“人人对战”，则isComp为false，反之则为true。

==菜单监听完整代码：==

```java
    /**     * 菜单事件监听器     * @param e     */    @Override    public void actionPerformed(ActionEvent e) {        if (e.getActionCommand().equals("开始新游戏")){            turn = false;            start = true;            pieces.clear();            coordinates = new int[15][15];            stack.clear();            jFrame.repaint();        }        else if (e.getActionCommand().equals("悔棋")){            if (!isComp){                if (stack.size() == 0){                    JOptionPane.showMessageDialog(null, "您不能在悔棋了", "Error", JOptionPane.PLAIN_MESSAGE);                    return;                }                //如果白棋悔棋，则还是白棋继续。如果是黑棋悔棋，则还是黑棋                if (turn){                    turn = false;                }                else {                    turn = true;                }                removeFirstItem(pieces.size() - 1, pieces);                Pair pair = stack.pop();                coordinates[pair.getLeft()][pair.getRight()] = 0;                jFrame.repaint();            }            else{                if (stack.size() == 0){                    JOptionPane.showMessageDialog(null, "您不能在悔棋了", "Error", JOptionPane.PLAIN_MESSAGE);                    return;                }                //只有玩家才会悔棋，所以悔棋后直接是黑棋继续                turn = false;                //直接删除连续的黑棋和白棋                removeFirstItem(pieces.size() - 1, pieces);                removeFirstItem(pieces.size() - 1, pieces);                Pair pair1 = stack.pop();                coordinates[pair1.getLeft()][pair1.getRight()] = 0;                Pair pair2 = stack.pop();                coordinates[pair2.getLeft()][pair2.getRight()] = 0;                jFrame.repaint();            }        }        else if (e.getActionCommand().equals("认输") && start){            if (turn == true){                JOptionPane.showMessageDialog(null, "黑棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);            }            else {                JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);            }            pieces.clear();            coordinates = new int[15][15];            stack.clear();            turn = false;            jFrame.repaint();        }        else if (e.getActionCommand().equals("认输") && !start){            JOptionPane.showMessageDialog(null, "对局尚未开始，无法认输", "Error", JOptionPane.PLAIN_MESSAGE);        }        else if (e.getSource() instanceof JComboBox){            JComboBox jComboBox = (JComboBox) e.getSource();            int selectedIndex = jComboBox.getSelectedIndex();            switch (selectedIndex){                case 0 :                    isComp = false;                    JOptionPane.showMessageDialog(null, "请直接点击开始新游戏", "Warning", 					     					      						   JOptionPane.PLAIN_MESSAGE);                    break;                case 1:                    isComp = true;                    JOptionPane.showMessageDialog(null, "请直接点击开始新游戏！", "Error", 				 		 											  JOptionPane.PLAIN_MESSAGE);                    break;            }        }    }
```

至此，我们的五子棋已经可以实现人人对战。

![截屏2021-07-09 下午3.23.27](/Users/lihanshuo/Desktop/蓝杰代码/五子棋截屏/截屏2021-07-09 下午3.23.27.png)

## 4. 实现初级人机五子棋

在人机对战中，玩家是黑棋，电脑是白棋。其中最难的部分就是电脑如何找到最佳位置来落棋，这里我们需要定义一个新的二维数组，用于电脑寻找下棋的最佳落点，该二维数组所存数据为权值。

### 4.1 什么是权值？

我们都知道，在下五子棋时，都会产生不同的情况（这里0代表空位，1代表黑棋，2代表白棋）。010是黑活一，即黑棋的两边不是边界也不是其他棋，12220是白死三，即在某个方向的白棋三连珠中，有一头是有一个黑棋（这里也可以是墙，但是应用2220或0222来代替），每一种情况都有不同的权值，然后在将每一种情况和其对应的权值存入到一个HashMap中。

具体如下：

```java
//权值的定义//黑棋的rewardprivate static final int D_ONE_B = 10;private static final int L_ONE_B = 30;private static final int D_TWO_B = 70;private static final int L_TWO_B = 120;private static final int D_THREE_B = 300;private static final int L_THREE_B = 500;private static final int D_FOUR_B = 900;private static final int L_FOUR_B = 1500;private static final int FIVE_B = 5000;//白棋的rewardprivate static final int D_ONE_W = 20;private static final int L_ONE_W = 40;private static final int D_TWO_W = 100;private static final int L_TWO_W = 200;private static final int D_THREE_W = 400;private static final int L_THREE_W = 700;private static final int D_FOUR_W = 1200;private static final int L_FOUR_W = 2000;private static final int FIVE_W = 9000;//不同情况对应的权值并存入HashMap//黑棋活1rewardsMap.put("010", L_ONE_B);//黑棋死1rewardsMap.put("210", D_ONE_B);rewardsMap.put("012", D_ONE_B);rewardsMap.put("10", D_ONE_B);rewardsMap.put("01", D_ONE_B);//黑棋活2rewardsMap.put("0110", L_TWO_B);//黑棋死2rewardsMap.put("2110", D_TWO_B);rewardsMap.put("0112", D_TWO_B);rewardsMap.put("110", D_TWO_B);rewardsMap.put("011", D_TWO_B);//黑棋活3rewardsMap.put("01110", L_THREE_B);//黑棋死3rewardsMap.put("21110", D_THREE_B);rewardsMap.put("01112", D_THREE_B);rewardsMap.put("1110", D_THREE_B);rewardsMap.put("0111", D_THREE_B);//黑棋活4rewardsMap.put("011110", L_FOUR_B);//黑棋死4rewardsMap.put("211110", D_FOUR_B);rewardsMap.put("011112", D_FOUR_B);rewardsMap.put("11110", D_FOUR_B);rewardsMap.put("01111", D_FOUR_B);//黑5rewardsMap.put("11111", FIVE_B);//白棋活1rewardsMap.put("020", L_ONE_W);//白棋死1rewardsMap.put("120", D_ONE_W);rewardsMap.put("021", D_ONE_W);rewardsMap.put("20", D_ONE_W);rewardsMap.put("02", D_ONE_W);//白棋活2rewardsMap.put("0220", L_TWO_W);//白棋死2rewardsMap.put("1220", D_TWO_W);rewardsMap.put("0221", D_TWO_W);rewardsMap.put("220", D_TWO_W);rewardsMap.put("022", D_TWO_W);//白棋活3rewardsMap.put("02220", L_THREE_W);//白棋死3rewardsMap.put("12220", D_THREE_W);rewardsMap.put("02221", D_THREE_W);rewardsMap.put("2220", D_THREE_W);rewardsMap.put("0222", D_THREE_W);//白棋活4rewardsMap.put("022220", L_FOUR_W);//白棋死4rewardsMap.put("122220", D_FOUR_W);rewardsMap.put("022221", D_FOUR_W);rewardsMap.put("22220", D_FOUR_W);rewardsMap.put("02222", D_FOUR_W);//白5rewardsMap.put("22222", FIVE_W);
```

### 4.2 将权值存入二维数组的对应位置

**思路：**每当我们下一步棋时，我们首先遍历整个用于存棋子位置的数组，然后对每个空位置，即0，进行8个方向的遍历（上、下、左、右、左上、左下、右上、右下），查看是否存在某一种落棋情况。如有，则将对应的权值情况相加，存入到权值二位数组的相同位置。（例如，在第三行第一列的0的右上方向存在黑活三，下方向存在白死二，这里的活三和死二必须紧挨着空位，即0的右上第一个棋子就是1，下方向的第一个棋子就是2。这里将两种情况的权值相加，即500 + 100 = 600存入对应位置的权值二位数组中）。

完整代码如下：

```java
package pres.hanshuo.pojo;import java.util.Arrays;import java.util.HashMap;/** * @author Li Hanshuo */public class AI {    private int[][] rewards;    private Integer reward;    private HashMap<String, Integer> rewardsMap = new HashMap<String, Integer>();    public AI(int[][] rewards, Integer reward) {        this.rewards = rewards;        this.reward = reward;    }    //黑棋的reward    private static final int D_ONE_B = 10;    private static final int L_ONE_B = 30;    private static final int D_TWO_B = 70;    private static final int L_TWO_B = 120;    private static final int D_THREE_B = 300;    private static final int L_THREE_B = 500;    private static final int D_FOUR_B = 900;    private static final int L_FOUR_B = 1500;    private static final int FIVE_B = 5000;    //白棋的reward    private static final int D_ONE_W = 20;    private static final int L_ONE_W = 40;    private static final int D_TWO_W = 100;    private static final int L_TWO_W = 200;    private static final int D_THREE_W = 400;    private static final int L_THREE_W = 700;    private static final int D_FOUR_W = 1200;    private static final int L_FOUR_W = 2000;    private static final int FIVE_W = 9000;    {        //黑棋活1        rewardsMap.put("010", L_ONE_B);        //黑棋死1        rewardsMap.put("210", D_ONE_B);        rewardsMap.put("012", D_ONE_B);        rewardsMap.put("10", D_ONE_B);        rewardsMap.put("01", D_ONE_B);        //黑棋活2        rewardsMap.put("0110", L_TWO_B);        //黑棋死2        rewardsMap.put("2110", D_TWO_B);        rewardsMap.put("0112", D_TWO_B);        rewardsMap.put("110", D_TWO_B);        rewardsMap.put("011", D_TWO_B);        //黑棋活3        rewardsMap.put("01110", L_THREE_B);        //黑棋死3        rewardsMap.put("21110", D_THREE_B);        rewardsMap.put("01112", D_THREE_B);        rewardsMap.put("1110", D_THREE_B);        rewardsMap.put("0111", D_THREE_B);        //黑棋活4        rewardsMap.put("011110", L_FOUR_B);        //黑棋死4        rewardsMap.put("211110", D_FOUR_B);        rewardsMap.put("011112", D_FOUR_B);        rewardsMap.put("11110", D_FOUR_B);        rewardsMap.put("01111", D_FOUR_B);        //黑5        rewardsMap.put("11111", FIVE_B);        //白棋活1        rewardsMap.put("020", L_ONE_W);        //白棋死1        rewardsMap.put("120", D_ONE_W);        rewardsMap.put("021", D_ONE_W);        rewardsMap.put("20", D_ONE_W);        rewardsMap.put("02", D_ONE_W);        //白棋活2        rewardsMap.put("0220", L_TWO_W);        //白棋死2        rewardsMap.put("1220", D_TWO_W);        rewardsMap.put("0221", D_TWO_W);        rewardsMap.put("220", D_TWO_W);        rewardsMap.put("022", D_TWO_W);        //白棋活3        rewardsMap.put("02220", L_THREE_W);        //白棋死3        rewardsMap.put("12220", D_THREE_W);        rewardsMap.put("02221", D_THREE_W);        rewardsMap.put("2220", D_THREE_W);        rewardsMap.put("0222", D_THREE_W);        //白棋活4        rewardsMap.put("022220", L_FOUR_W);        //白棋死4        rewardsMap.put("122220", D_FOUR_W);        rewardsMap.put("022221", D_FOUR_W);        rewardsMap.put("22220", D_FOUR_W);        rewardsMap.put("02222", D_FOUR_W);        //白5        rewardsMap.put("22222", FIVE_W);    }    public void AICheck(int[][] coordinates, int[][] rewards){        String str = "";    //起始连棋状态        int color = 0;  //用于记录当前棋子颜色        for (int i = 0; i < coordinates.length; i++) {            for (int j = 0; j < coordinates[i].length; j++) {                if (coordinates[i][j] != 0){                    continue;                }                //遍历整个coordinates数组，对所有0元素在进行8个方向的遍历                if (coordinates[i][j] == 0){                    //左斜方向                    //左上方检查                    int n = j - 1;                    for (int k = i - 1; k >= 0; k--) {                        if (n >= 0 && str.length() == 0 && coordinates[k][n] == 0){                            break;                        }                        else if (n >= 0 && coordinates[k][n] != 0 && color == 0){                            str += 0 + "" + coordinates[k][n];                            color = coordinates[k][n];                            n--;                        }                        else if (n >= 0 && coordinates[k][n] == color && color != 0){                            str += coordinates[k][n];                            n--;                        }                        else if (n >= 0 && str.length() > 0 && coordinates[k][n] == 0){                            str += coordinates[k][n];                            break;                        }                        else if (n >= 0 && coordinates[k][n] != color){                            str += coordinates[k][n];                            break;                        }                    }                    reward = rewardsMap.get(str);                    if (reward != null){                        rewards[i][j] += reward;                    }                    str = "";                    color = 0;                    //右下方检查                    n = j + 1;                    for (int k = i + 1; k < coordinates.length; k++) {                        if (n < coordinates.length && str.length() == 0 && coordinates[k][n] == 0){                            break;                        }                        else if (n < coordinates.length && coordinates[k][n] != 0 && color == 0){                            str += 0 + "" + coordinates[k][n];                            color = coordinates[k][n];                            n++;                        }                        else if (n < coordinates.length && coordinates[k][n] == color && color != 0){                            str += coordinates[k][n];                            n++;                        }                        else if (n < coordinates.length && str.length() > 0 && coordinates[k][n] == 0){                            str += coordinates[k][n];                            break;                        }                        else if (n < coordinates.length && coordinates[k][n] != color){                            str += coordinates[k][n];                            break;                        }                    }                    reward = rewardsMap.get(str);                    if (reward != null){                        rewards[i][j] += reward;                    }                    str = "";                    color = 0;                    //右斜方向                    //右上方                    n = j + 1;                    for (int k = i - 1; k >= 0; k--) {                        if (n < coordinates.length && str.length() == 0 && coordinates[k][n] == 0){                            break;                        }                        else if (n < coordinates.length && coordinates[k][n] != 0 && color == 0){                            str += 0 + "" + coordinates[k][n];                            color = coordinates[k][n];                            n++;                        }                        else if (n < coordinates.length && coordinates[k][n] == color && color != 0){                            str += coordinates[k][n];                            n++;                        }                        else if (n < coordinates.length && str.length() > 0 && coordinates[k][n] == 0){                            str += coordinates[k][n];                            break;                        }                        else if (n < coordinates.length && coordinates[k][n] != color){                            str += coordinates[k][n];                            break;                        }                    }                    reward = rewardsMap.get(str);                    if (reward != null){                        rewards[i][j] += reward;                    }                    str = "";                    color = 0;                    //左下方                    n = j - 1;                    for (int k = i + 1; k < coordinates.length; k++) {                        if (n >= 0 && str.length() == 0 && coordinates[k][n] == 0){                            break;                        }                        else if (n >= 0 && coordinates[k][n] != 0 && color == 0){                            str += 0 + "" + coordinates[k][n];                            color = coordinates[k][n];                            n--;                        }                        else if (n >= 0 && coordinates[k][n] == color && color != 0){                            str += coordinates[k][n];                            n--;                        }                        else if (n >= 0 && str.length() > 0 && coordinates[k][n] == 0){                            str += coordinates[k][n];                            break;                        }                        else if (n >= 0 && coordinates[k][n] != color){                            str += coordinates[k][n];                            break;                        }                    }                    reward = rewardsMap.get(str);                    if (reward != null){                        rewards[i][j] += reward;                    }                    str = "";                    color = 0;                }                //水平方向检查                //向左                for (int k = j - 1; k >= 0; k--) {                    if (str.length() == 0 && coordinates[i][k] == 0){                        break;                    }                    else if (coordinates[i][k] != 0 && color == 0){                        str += 0 + "" + coordinates[i][k];                        color = coordinates[i][k];                    }                    else if (coordinates[i][k] == color && color != 0){                        str += coordinates[i][k];                    }                    else if (str.length() > 0 && coordinates[i][k] == 0){                        str += coordinates[i][k];                        break;                    }                    else if (coordinates[i][k] != color){                        str += coordinates[i][k];                        break;                    }                }                reward = rewardsMap.get(str);                if (reward != null){                    rewards[i][j] += reward;                }                str = "";                color = 0;                //向右                for (int k = j + 1; k < coordinates.length; k++) {                    if (str.length() == 0 && coordinates[i][k] == 0){                        break;                    }                    else if (coordinates[i][k] != 0 && color == 0){                        str += 0 + "" + coordinates[i][k];                        color = coordinates[i][k];                    }                    else if (coordinates[i][k] == color && color != 0){                        str += coordinates[i][k];                    }                    else if (str.length() > 0 && coordinates[i][k] == 0){                        str += coordinates[i][k];                        break;                    }                    else if (coordinates[i][k] != color){                        str += coordinates[i][k];                        break;                    }                }                reward = rewardsMap.get(str);                if (reward != null){                    rewards[i][j] += reward;                }                str = "";                color = 0;                //垂直方向                //向上                for (int k = i - 1; k >= 0; k--) {                    if (str.length() == 0 && coordinates[k][j] == 0){                        break;                    }                    else if (coordinates[k][j] != 0 && color == 0){                        str += 0 + "" + coordinates[k][j];                        color = coordinates[k][j];                    }                    else if (coordinates[k][j] == color && color != 0){                        str += coordinates[k][j];                    }                    else if (str.length() > 0 && coordinates[k][j] == 0){                        str += coordinates[k][j];                        break;                    }                    else if (coordinates[k][j] != color){                        str += coordinates[k][j];                        break;                    }                }                reward = rewardsMap.get(str);                if (reward != null){                    rewards[i][j] += reward;                }                str = "";                color = 0;                //向下                for (int k = i + 1; k < coordinates.length; k++) {                    if (str.length() == 0 && coordinates[k][j] == 0){                        break;                    }                    else if (coordinates[k][j] != 0 && color == 0){                        str += 0 + "" + coordinates[k][j];                        color = coordinates[k][j];                    }                    else if (coordinates[k][j] == color && color != 0){                        str += coordinates[k][j];                    }                    else if (str.length() > 0 && coordinates[k][j] == 0){                        str += coordinates[k][j];                        break;                    }                    else if (coordinates[k][j] != color){                        str += coordinates[k][j];                        break;                    }                }                reward = rewardsMap.get(str);                if (reward != null){                    rewards[i][j] += reward;                }                str = "";                color = 0;            }        }    }}
```

然后我们继续完善我们mousePress()方法中的人机对战部分，黑棋部分与人人对战几乎相同，仅多了一步计算权值并存入权值数组的步骤（因为黑棋是玩家）。因为玩家下完棋之后电脑也应该下完棋，所以这里我们将电脑下棋的部分写入到mouseReleased()方法中去。

1. 首先在权值数组中进行遍历，找到最大的权值并获得其索引。
2. 根据索引（行列值），计算出对应的坐标并且下棋。
3. 之后便是入栈，存入ArrayList，存入坐标二维数组，验证输赢等操作。

```java
/**     * 实现人机的在最佳位置的自动落子     * @param e     */    @Override    public void mouseReleased(MouseEvent e) {        if (isComp && start){            if (turn){                graphics.setColor(Color.white);                ai.AICheck(coordinates, rewards);                //确定rewards数组中的最大值并获取其索引                int maxX = 0;                int maxY = 0;                int max = rewards[0][0];                for (int i = 0; i < rewards.length; i++){                    for (int j = 0; j < rewards.length; j++){                        if (rewards[i][j] > max){                            max = rewards[i][j];                            maxX = i;                            maxY = j;                        }                    }                }                int xCorrection1 = maxY * SIZE + X - SIZE / 2;                int yCorrection1 = maxX * SIZE + Y - SIZE / 2;                graphics.fillOval(xCorrection1, yCorrection1, SIZE, SIZE);                Piece piece = new Piece(xCorrection1, yCorrection1, Color.white);                pieces.add(piece);                coordinates[maxX][maxY] = 2;//将棋子位置存入array中                stack.push(new Pair(maxX, maxY));//入栈                turn = false;                rewards = new int[15][15];                if (rules.tiltLeft(maxX, maxY, coordinates) || rules.titleRight(maxX, maxY, coordinates) ||                    rules.horizontal(maxX, maxY, coordinates) || rules.vertical(maxX, maxY, 									coordinates)){                    JOptionPane.showMessageDialog(null, "白棋获胜！", "Win", JOptionPane.PLAIN_MESSAGE);                    turn = false;                    pieces.clear();                    coordinates = new int[15][15];                    rewards = new int[15][15];                    stack.clear();                    start = false;                    jFrame.repaint();                }            }        }    }
```

至此，我们的五子棋全部完成。

