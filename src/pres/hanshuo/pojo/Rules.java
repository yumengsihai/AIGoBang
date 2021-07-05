package pres.hanshuo.pojo;

import java.util.HashMap;

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

    public void AICheck(int[][] coordinates, int[][] rewards){
        String str = new String("");
        //从左到右横向扫描
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates[i].length; j++) {
                //若当前棋子的下一个位置依旧为空，直接跳出循环
                if (j + 1 < coordinates.length && coordinates[i][j] == 0 && coordinates[i][j+1] == 0){
                    break;
                }
                else{
                    str += coordinates[i][j];
                    rewards[i][j] = coordinates[i][j];
                }
            }
        }

        //从右到左横向扫描
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = coordinates.length - 1; j >= 0; j--) {
                //coordinates[i][j];
            }
        }

        //从上倒下纵向扫描
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                //coordinates[j][i];
            }
        }

        //从下到上纵向扫描
        for (int i = coordinates.length - 1; i >= 0; i--) {
            for (int j = 0; j < coordinates.length; j--) {
                //coordinates[j][i];
            }
        }

        //从左下角向右上角扫描（上半部分）
        int j = 0;
        for (int row = coordinates.length - 1; row >= 0; row--) {
            for (int i = row; i >= 0; i--) {
                coordinates[i][j] = 1;
                if (j < coordinates.length){
                    j++;
                }
            }
            j = 0;
        }

        //从左下角向右上角扫描（下半部分）
        int j1 = coordinates.length - 1;
        for (int column = 1; column < coordinates.length; column++) {
            for (int i = column; i < coordinates.length; i++) {
                coordinates[j1][i] = 1;
                if (j1 >= 0){
                    j1--;
                }
            }
            j1 = coordinates.length - 1;
        }

        //从右上角向左下角扫描（上半部分）
        int k = 0;
        for (int column = coordinates.length - 1; column >= 0; column--) {
            for (int j4 = column; j4 >= 0; j4--) {
                coordinates[k][j4] = 1;
                k++;
            }
            k = 0;
        }

        //从右上角向左下角扫描（下半部分）
        int k1 = coordinates.length - 1;
        for (int row = 1; row < coordinates.length; row++) {
            for (int i = row; i < coordinates.length; i++) {
                coordinates[i][k1] = 1;
                k1--;
            }
            k1 = coordinates.length - 1;
        }

        //从右下角向左上角扫描（上半部分）
        int j2 = coordinates.length - 1;
        for (int row1 = coordinates.length - 1; row1 >= 0; row1--) {
            for (int i = row1; i >= 0 ; i--) {
                //coordinates[i][j2] = 1;
                if (j2 >= 0){
                    j2--;
                }
            }
            j2 = coordinates.length - 1;
        }

        //从右下角向左上角扫描（下半部分）
        int j3 = coordinates.length - 1;
        for (int column1 = coordinates.length - 2; column1 >= 0; column1--){
            for (int i = column1; i >= 0; i--) {
                coordinates[j3][i] = 1;
                if (j3 >= 0){
                    j3--;
                }
            }
            j3 = coordinates.length - 1;
        }

        //从左上角向右下角扫描（上半部分）
        int k2 = 0;
        for (int z = 0; z < coordinates.length; z++) {
            for (int x = z; x < coordinates.length; x++) {
                coordinates[k2][x] = 1;
                k2++;
            }
            k2 = 0;
        }

        //从左上角向右下角扫描（上半部分）
        int k3 = 0;
        for (int v = 1; v < coordinates.length; v++) {
            for (int n = v; n < coordinates.length; n++) {
                coordinates[n][k3] = 1;
                k3++;
            }
            k3 = 0;
        }
    }
}
