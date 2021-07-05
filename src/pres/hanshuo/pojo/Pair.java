package pres.hanshuo.pojo;

/**
 * 自定义的一种数据结构：
 * Pair(Left, Right)
 */
public class Pair {
    private int left;
    private int right;

    public Pair(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }
}
