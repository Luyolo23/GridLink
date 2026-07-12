package org.larrydev.gridlink.domain.unit;

import org.larrydev.gridlink.domain.Direction;
import org.larrydev.gridlink.domain.OperationalStatus;
import org.larrydev.gridlink.domain.Position;
import org.larrydev.gridlink.domain.UnitSnapshot;

public final class Unit {
    private final String name;
    private final String type;
    private Position position;
    private Direction direction;
    private OperationalStatus status;

    public Unit(String name, String type, Position startPosition) {
        this.name = name;
        // Treat invalid type as worker (worker/scout/heavy)
        String cleanedType = (type == null) ? "worker" : type.trim().toLowerCase();
        if (!cleanedType.equals("scout") && !cleanedType.equals("heavy") && !cleanedType.equals("worker")) {
            cleanedType = "worker";
        }
        this.type = cleanedType;
        this.position = startPosition;
        this.status = OperationalStatus.NORMAL;
        this.direction = Direction.NORTH;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public OperationalStatus getStatus() {
        return status;
    }

    public int getVisibilityBonus() {
        return "scout".equals(type) ? 1 : 0;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setStatus(OperationalStatus status) {
        this.status = status;
    }

    public UnitSnapshot snapshot() {
        return new UnitSnapshot(name, type, position, direction, status);
    }
}

