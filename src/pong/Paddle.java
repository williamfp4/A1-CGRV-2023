package pong;

public class Paddle {
    private float xLeft, xRight, y;

    public Paddle(float xL, float xR, float y) {
        this.xLeft = xL;
        this.xRight = xR;
        this.y = y;
    }

    public float getxLeft() {
        return xLeft;
    }

    public float getxRight() {
        return xRight;
    }

    public float getY() {
        return y;
    }

    public void setxLeft(float xLeft) {
        this.xLeft = xLeft;
    }

    public void setxRight(float xRight) {
        this.xRight = xRight;
    }

    public void setY(float y) {
        this.y = y;
    }

}
