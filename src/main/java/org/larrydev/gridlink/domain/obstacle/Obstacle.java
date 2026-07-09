package org.larrydev.gridlink.domain.obstacle;

import org.larrydev.gridlink.domain.Position;

public class Obstacle {
    private Position topLeft;
    private Position bottomRight;
    private ObstacleType type;

    public Obstacle(Position topLeft, Position bottomRight, ObstacleType type){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.type = type;
    }

    public Position getTopLeft(){return topLeft;}

    public Position getBottomRight() {return bottomRight;}

    public ObstacleType getType() {return type;}

    public boolean blocksMovement(){
        return true;
    }

    public boolean contains(Position position){
        return true;
    }
}
