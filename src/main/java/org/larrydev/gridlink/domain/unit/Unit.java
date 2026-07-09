package org.larrydev.gridlink.domain.unit;

import org.larrydev.gridlink.domain.Direction;
import org.larrydev.gridlink.domain.OperationalStatus;
import org.larrydev.gridlink.domain.Position;

public class Unit {

    private String name;
    private String type;
    private Position startPositon;
    private OperationalStatus status;
    private Direction direction;
    public Unit(String name, String type, Position startPositon){
        this.name = name;
        this.type = type;
        this.startPositon = startPositon;
        this.status = OperationalStatus.NORMAL;
        this.direction = Direction.NORTH;
    }

    public String getName(){
        return name;
    }

    public String getType() {
        return type;
    }

    public Position getStartPositon() {
        return startPositon;
    }

    public Direction getDirection() {
        return direction;
    }

    public OperationalStatus getStatus() {
        return status;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setStatus(OperationalStatus status) {
        this.status = status;
    }

    public void setPositon(Position startPositon) {
        this.startPositon = startPositon;
    }
}
