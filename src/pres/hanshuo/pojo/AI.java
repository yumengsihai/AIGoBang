package pres.hanshuo.pojo;

import java.util.Arrays;
import java.util.HashMap;

public class AI {
    private int[][] rewards;
    private Integer reward;
    private HashMap<String, Integer> rewardsMap = new HashMap<String, Integer>();

    public AI(int[][] rewards, Integer reward) {
        this.rewards = rewards;
        this.reward = reward;
    }

    //黑棋的reward
    private static final int D_ONE_B = 10;
    private static final int L_ONE_B = 30;
    private static final int D_TWO_B = 70;
    private static final int L_TWO_B = 120;
    private static final int D_THREE_B = 300;
    private static final int L_THREE_B = 500;
    private static final int D_FOUR_B = 900;
    private static final int L_FOUR_B = 1500;
    private static final int FIVE_B = 5000;
    //白棋的reward
    private static final int D_ONE_W = 20;
    private static final int L_ONE_W = 40;
    private static final int D_TWO_W = 100;
    private static final int L_TWO_W = 200;
    private static final int D_THREE_W = 400;
    private static final int L_THREE_W = 700;
    private static final int D_FOUR_W = 1200;
    private static final int L_FOUR_W = 2000;
    private static final int FIVE_W = 9000;

    {
        //黑棋活1
        rewardsMap.put("010", L_ONE_B);
        //黑棋死1
        rewardsMap.put("210", D_ONE_B);
        rewardsMap.put("012", D_ONE_B);
        rewardsMap.put("10", D_ONE_B);
        rewardsMap.put("01", D_ONE_B);
        //黑棋活2
        rewardsMap.put("0110", L_TWO_B);
        //黑棋死2
        rewardsMap.put("2110", D_TWO_B);
        rewardsMap.put("0112", D_TWO_B);
        rewardsMap.put("110", D_TWO_B);
        rewardsMap.put("011", D_TWO_B);
        //黑棋活3
        rewardsMap.put("01110", L_THREE_B);
        //黑棋死3
        rewardsMap.put("21110", D_THREE_B);
        rewardsMap.put("01112", D_THREE_B);
        rewardsMap.put("1110", D_THREE_B);
        rewardsMap.put("0111", D_THREE_B);
        //黑棋活4
        rewardsMap.put("011110", L_FOUR_B);
        //黑棋死4
        rewardsMap.put("211110", D_FOUR_B);
        rewardsMap.put("011112", D_FOUR_B);
        rewardsMap.put("11110", D_FOUR_B);
        rewardsMap.put("01111", D_FOUR_B);
        //黑5
        rewardsMap.put("11111", FIVE_B);

        //白棋活1
        rewardsMap.put("020", L_ONE_W);
        //白棋死1
        rewardsMap.put("120", D_ONE_W);
        rewardsMap.put("021", D_ONE_W);
        rewardsMap.put("20", D_ONE_W);
        rewardsMap.put("02", D_ONE_W);
        //白棋活2
        rewardsMap.put("0220", L_TWO_W);
        //白棋死2
        rewardsMap.put("1220", D_TWO_W);
        rewardsMap.put("0221", D_TWO_W);
        rewardsMap.put("220", D_TWO_W);
        rewardsMap.put("022", D_TWO_W);
        //白棋活3
        rewardsMap.put("02220", L_THREE_W);
        //白棋死3
        rewardsMap.put("12220", D_THREE_W);
        rewardsMap.put("02221", D_THREE_W);
        rewardsMap.put("2220", D_THREE_W);
        rewardsMap.put("0222", D_THREE_W);
        //白棋活4
        rewardsMap.put("022220", L_FOUR_W);
        //白棋死4
        rewardsMap.put("122220", D_FOUR_W);
        rewardsMap.put("022221", D_FOUR_W);
        rewardsMap.put("22220", D_FOUR_W);
        rewardsMap.put("02222", D_FOUR_W);
        //白5
        rewardsMap.put("22222", FIVE_W);
    }
    public void AICheck(int[][] coordinates, int[][] rewards){
        String str = "";    //起始连棋状态
        int color = 0;  //用于记录当前棋子颜色
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates[i].length; j++) {
                if (coordinates[i][j] != 0){
                    continue;
                }
                //遍历整个coordinates数组，对所有0元素在进行8个方向的遍历
                if (coordinates[i][j] == 0){
                    //左斜方向
                    //左上方检查
                    int n = j - 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (n >= 0 && str.length() == 0 && coordinates[k][n] == 0){
                            break;
                        }
                        else if (n >= 0 && coordinates[k][n] != 0 && color == 0){
                            str += 0 + "" + coordinates[k][n];
                            color = coordinates[k][n];
                            n--;
                        }
                        else if (n >= 0 && coordinates[k][n] == color && color != 0){
                            str += coordinates[k][n];
                            n--;
                        }
                        else if (n >= 0 && str.length() > 0 && coordinates[k][n] == 0){
                            str += coordinates[k][n];
                            break;
                        }
                        else if (n >= 0 && coordinates[k][n] != color){
                            str += coordinates[k][n];
                            break;
                        }
                    }
                    reward = rewardsMap.get(str);
                    if (reward != null){
                        rewards[i][j] += reward;
                    }
                    str = "";
                    color = 0;
                    //右下方检查
                    n = j + 1;
                    for (int k = i + 1; k < coordinates.length; k++) {
                        if (n < coordinates.length && str.length() == 0 && coordinates[k][n] == 0){
                            break;
                        }
                        else if (n < coordinates.length && coordinates[k][n] != 0 && color == 0){
                            str += 0 + "" + coordinates[k][n];
                            color = coordinates[k][n];
                            n++;
                        }
                        else if (n < coordinates.length && coordinates[k][n] == color && color != 0){
                            str += coordinates[k][n];
                            n++;
                        }
                        else if (n < coordinates.length && str.length() > 0 && coordinates[k][n] == 0){
                            str += coordinates[k][n];
                            break;
                        }
                        else if (n < coordinates.length && coordinates[k][n] != color){
                            str += coordinates[k][n];
                            break;
                        }
                    }
                    reward = rewardsMap.get(str);
                    if (reward != null){
                        rewards[i][j] += reward;
                    }
                    str = "";
                    color = 0;
                    //右斜方向
                    //右上方
                    n = j + 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (n < coordinates.length && str.length() == 0 && coordinates[k][n] == 0){
                            break;
                        }
                        else if (n < coordinates.length && coordinates[k][n] != 0 && color == 0){
                            str += 0 + "" + coordinates[k][n];
                            color = coordinates[k][n];
                            n++;
                        }
                        else if (n < coordinates.length && coordinates[k][n] == color && color != 0){
                            str += coordinates[k][n];
                            n++;
                        }
                        else if (n < coordinates.length && str.length() > 0 && coordinates[k][n] == 0){
                            str += coordinates[k][n];
                            break;
                        }
                        else if (n < coordinates.length && coordinates[k][n] != color){
                            str += coordinates[k][n];
                            break;
                        }
                    }
                    reward = rewardsMap.get(str);
                    if (reward != null){
                        rewards[i][j] += reward;
                    }
                    str = "";
                    color = 0;
                    //左下方
                    n = j - 1;
                    for (int k = i + 1; k < coordinates.length; k++) {
                        if (n >= 0 && str.length() == 0 && coordinates[k][n] == 0){
                            break;
                        }
                        else if (n >= 0 && coordinates[k][n] != 0 && color == 0){
                            str += 0 + "" + coordinates[k][n];
                            color = coordinates[k][n];
                            n--;
                        }
                        else if (n >= 0 && coordinates[k][n] == color && color != 0){
                            str += coordinates[k][n];
                            n--;
                        }
                        else if (n >= 0 && str.length() > 0 && coordinates[k][n] == 0){
                            str += coordinates[k][n];
                            break;
                        }
                        else if (n >= 0 && coordinates[k][n] != color){
                            str += coordinates[k][n];
                            break;
                        }
                    }
                    reward = rewardsMap.get(str);
                    if (reward != null){
                        rewards[i][j] += reward;
                    }
                    str = "";
                    color = 0;
                }
                //水平方向检查
                //向左
                for (int k = j - 1; k >= 0; k--) {
                    if (str.length() == 0 && coordinates[i][k] == 0){
                        break;
                    }
                    else if (coordinates[i][k] != 0 && color == 0){
                        str += 0 + "" + coordinates[i][k];
                        color = coordinates[i][k];
                    }
                    else if (coordinates[i][k] == color && color != 0){
                        str += coordinates[i][k];
                    }
                    else if (str.length() > 0 && coordinates[i][k] == 0){
                        str += coordinates[i][k];
                        break;
                    }
                    else if (coordinates[i][k] != color){
                        str += coordinates[i][k];
                        break;
                    }
                }
                reward = rewardsMap.get(str);
                if (reward != null){
                    rewards[i][j] += reward;
                }
                str = "";
                color = 0;
                //向右
                for (int k = j + 1; k < coordinates.length; k++) {
                    if (str.length() == 0 && coordinates[i][k] == 0){
                        break;
                    }
                    else if (coordinates[i][k] != 0 && color == 0){
                        str += 0 + "" + coordinates[i][k];
                        color = coordinates[i][k];
                    }
                    else if (coordinates[i][k] == color && color != 0){
                        str += coordinates[i][k];
                    }
                    else if (str.length() > 0 && coordinates[i][k] == 0){
                        str += coordinates[i][k];
                        break;
                    }
                    else if (coordinates[i][k] != color){
                        str += coordinates[i][k];
                        break;
                    }
                }
                reward = rewardsMap.get(str);
                if (reward != null){
                    rewards[i][j] += reward;
                }
                str = "";
                color = 0;
                //垂直方向
                //向上
                for (int k = i - 1; k >= 0; k--) {
                    if (str.length() == 0 && coordinates[k][j] == 0){
                        break;
                    }
                    else if (coordinates[k][j] != 0 && color == 0){
                        str += 0 + "" + coordinates[k][j];
                        color = coordinates[k][j];
                    }
                    else if (coordinates[k][j] == color && color != 0){
                        str += coordinates[k][j];
                    }
                    else if (str.length() > 0 && coordinates[k][j] == 0){
                        str += coordinates[k][j];
                        break;
                    }
                    else if (coordinates[k][j] != color){
                        str += coordinates[k][j];
                        break;
                    }
                }
                reward = rewardsMap.get(str);
                if (reward != null){
                    rewards[i][j] += reward;
                }
                str = "";
                color = 0;
                //向下
                for (int k = i + 1; k < coordinates.length; k++) {
                    if (str.length() == 0 && coordinates[k][j] == 0){
                        break;
                    }
                    else if (coordinates[k][j] != 0 && color == 0){
                        str += 0 + "" + coordinates[k][j];
                        color = coordinates[k][j];
                    }
                    else if (coordinates[k][j] == color && color != 0){
                        str += coordinates[k][j];
                    }
                    else if (str.length() > 0 && coordinates[k][j] == 0){
                        str += coordinates[k][j];
                        break;
                    }
                    else if (coordinates[k][j] != color){
                        str += coordinates[k][j];
                        break;
                    }
                }
                reward = rewardsMap.get(str);
                if (reward != null){
                    rewards[i][j] += reward;
                }
                str = "";
                color = 0;
            }
        }
    }

    public static void main(String[] args) {
        int[][] arr = {{1,1,1,1,1},
                       {0,0,0,0,0},
                       {0,0,0,0,0},
                       {0,0,0,0,0},
                       {0,0,0,0,0}};
        int[][] rewards = {{0,0,0,0,0},
                           {0,0,0,0,0},
                           {0,0,0,0,0},
                           {0,0,0,0,0},
                           {0,0,0,0,0}};
        AI ai = new AI(arr, 0);
        ai.AICheck(arr, rewards);

        for (int i = 0; i < rewards.length; i++) {
            System.out.println(Arrays.toString(rewards[i]));

        }
    }
}
