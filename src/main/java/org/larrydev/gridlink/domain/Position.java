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
        if (this == o) return true;
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

    public boolean isInside(Position topLeft, Position bottomRight){
        boolean withinTop = y <= topLeft.getY();
        boolean withinBottom = y >= bottomRight.getY();
        boolean withinLeft = x >= topLeft.getX();
        boolean withinRight = x <= bottomRight.getX();

        return withinTop && withinBottom && withinLeft && withinRight;
    }

    public int distanceFrom(Position anotherAnother, Direction direction){
        return switch (direction){
            case NORTH, SOUTH -> Math.abs( y - anotherAnother.getY());
            case WEST, EAST -> Math.abs(x - anotherAnother.getX());
        };
    }

    public Position offset(Direction direction, int numSteps){
        int newX = x;
        int newY = y;

        switch (direction){
            case NORTH -> newY += numSteps;
            case SOUTH -> newY -= numSteps;
            case WEST -> newX -= numSteps;
            case EAST -> newX += numSteps;
        }

        return new Position(newX, newY);
    }
}
