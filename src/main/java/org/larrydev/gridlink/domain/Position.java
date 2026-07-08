package org.larrydev.gridlink.domain;

import java.util.Objects;

public final class Position {

    private int x;
    private int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString(){
        return "(x: "+x+", y: "+y+")";
    }

    public boolean isIn(Position topLeft, Position bottomRight){
        boolean withinTop = y <= topLeft.getY();
        boolean withinBottom = y >= bottomRight.getY();
        boolean withinLeft = x >= topLeft.getX();
        boolean withinRight = x <= bottomRight.getX();

        return withinTop && withinBottom && withinLeft && withinRight;
    }
}
