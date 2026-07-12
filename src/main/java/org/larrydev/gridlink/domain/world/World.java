package org.larrydev.gridlink.domain.world;

import org.larrydev.gridlink.config.Config;
import org.larrydev.gridlink.domain.CommandResult;
import org.larrydev.gridlink.domain.Direction;
import org.larrydev.gridlink.domain.Position;
import org.larrydev.gridlink.domain.unit.Unit;
import org.larrydev.gridlink.domain.obstacle.Obstacle;
import org.larrydev.gridlink.domain.command.VisibleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class World {
    private final List<Unit> units = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private String currentUnitName;

    public World() {
        this(Config.getInstance().getObstacleMode());
    }

    public World(String obstacleMode) {
        Maze maze = new Maze(obstacleMode);
        this.obstacles.addAll(maze.getObstacles());
    }

    // --- Bounds ---
    public Position getTopLeft() {
        Config config = Config.getInstance();
        return new Position(-config.getHalfWidth(), config.getHalfHeight() - 1);
    }

    public Position getBottomRight() {
        Config config = Config.getInstance();
        return new Position(config.getHalfWidth() - 1, -config.getHalfHeight());
    }

    public boolean isInsideBounds(Position position) {
        if (position == null) return false;
        return position.isInside(getTopLeft(), getBottomRight());
    }

    // --- Units ---
    public List<Unit> getUnits() {
        return units;
    }

    public Optional<Unit> findUnitByName(String name) {
        if (name == null) return Optional.empty();
        String targetName = name.trim();
        for (Unit u : units) {
            if (u.getName().equalsIgnoreCase(targetName)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public boolean hasUnitNamed(String name) {
        return findUnitByName(name).isPresent();
    }

    public int getUnitCount() {
        return units.size();
    }

    public void setCurrentUnit(String name) {
        this.currentUnitName = name;
    }

    public Unit getCurrentUnit() {
        if (currentUnitName == null) return null;
        return findUnitByName(currentUnitName).orElse(null);
    }

    // --- Obstacles ---
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public boolean isBlocked(Position position) {
        if (position == null) return false;
        // Check obstacles
        for (Obstacle obs : obstacles) {
            if (obs.contains(position)) {
                return true;
            }
        }
        // Check units
        for (Unit u : units) {
            if (u.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLaunchPositionValid(Position position) {
        return isInsideBounds(position) && !isBlocked(position);
    }

    // --- Game actions (called by commands) ---
    public CommandResult launchUnit(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            return CommandResult.error("Unit name cannot be empty.");
        }
        if (hasUnitNamed(name)) {
            return CommandResult.error("Name already exists.");
        }
        Config config = Config.getInstance();
        if (getUnitCount() >= config.getMaxUnits()) {
            return CommandResult.error("Max units reached.");
        }
        Position spawn = new Position(0, 0);
        if (!isLaunchPositionValid(spawn)) {
            return CommandResult.error("Spawn position (0,0) is blocked.");
        }
        Unit unit = new Unit(name, type, spawn);
        units.add(unit);
        return CommandResult.ok("Unit " + name + " launched successfully.", unit.snapshot());
    }

    public CommandResult moveCurrentUnit(String direction, int steps) {
        if (steps <= 0) {
            return CommandResult.error("Steps must be greater than or equal to 1.");
        }
        Unit unit = getCurrentUnit();
        if (unit == null) {
            return CommandResult.error("No current unit set.");
        }
        String dir = (direction == null) ? "" : direction.trim().toLowerCase();
        int stepsTaken = 0;
        if (dir.equals("forward")) {
            stepsTaken = moveUnitForward(unit, steps);
        } else if (dir.equals("back")) {
            stepsTaken = moveUnitBack(unit, steps);
        } else {
            return CommandResult.error("Invalid direction: " + direction);
        }
        return CommandResult.ok("Moved " + stepsTaken + " steps", unit.snapshot());
    }

    public CommandResult turnCurrentUnit(String turn) {
        Unit unit = getCurrentUnit();
        if (unit == null) {
            return CommandResult.error("No current unit set.");
        }
        String t = (turn == null) ? "" : turn.trim().toLowerCase();
        if (t.equals("left")) {
            unit.setDirection(unit.getDirection().turnLeft());
        } else if (t.equals("right")) {
            unit.setDirection(unit.getDirection().turnRight());
        } else {
            return CommandResult.error("Invalid turn instruction: " + turn);
        }
        return CommandResult.ok("Turned " + t, unit.snapshot());
    }

    public CommandResult stateOfCurrentUnit() {
        Unit unit = getCurrentUnit();
        if (unit == null) {
            return CommandResult.error("No current unit set.");
        }
        return CommandResult.ok("Normal status", unit.snapshot());
    }

    public CommandResult lookFromCurrentUnit() {
        Unit unit = getCurrentUnit();
        if (unit == null) {
            return CommandResult.error("No current unit set.");
        }

        Config config = Config.getInstance();
        int effectiveRange = config.getVisibility() + unit.getVisibilityBonus();

        List<VisibleObject> visibleList = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        // 4 Directions: NORTH, EAST, SOUTH, WEST
        Direction[] scanDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (Direction dir : scanDirs) {
            for (int s = 1; s <= effectiveRange; s++) {
                Position checkedPos = unit.getPosition().offset(dir, s);

                // 1. Check if outside bounds
                if (!isInsideBounds(checkedPos)) {
                    VisibleObject edgeObj = new VisibleObject("EDGE", "", dir, s);
                    visibleList.add(edgeObj);
                    messages.add("Visible EDGE, " + dir + ", distance " + s);
                    break; // blocked by edge
                }

                // 2. Check if occupied by another unit
                Unit otherUnit = null;
                for (Unit u : units) {
                    if (u != unit && u.getPosition().equals(checkedPos)) {
                        otherUnit = u;
                        break;
                    }
                }
                if (otherUnit != null) {
                    VisibleObject unitObj = new VisibleObject("UNIT", otherUnit.getName(), dir, s);
                    visibleList.add(unitObj);
                    messages.add("Visible UNIT, " + otherUnit.getName() + ", " + dir + ", distance " + s);
                    break; // blocked by unit
                }

                // 3. Check if contains obstacle
                Obstacle foundObs = null;
                for (Obstacle obs : obstacles) {
                    if (obs.contains(checkedPos)) {
                        foundObs = obs;
                        break;
                    }
                }
                if (foundObs != null) {
                    VisibleObject obsObj = new VisibleObject("OBSTACLE", "", dir, s);
                    visibleList.add(obsObj);
                    messages.add("Visible " + foundObs.getType() + ", " + dir + ", distance " + s);
                    break; // blocked by obstacle
                }
            }
        }

        String finalMsg = String.join("; ", messages);
        if (finalMsg.isEmpty()) {
            finalMsg = "No obstacles or units in sight";
        }
        return CommandResult.ok(finalMsg, unit.snapshot(), visibleList);
    }

    // --- Internal movement helpers ---
    public int moveUnitForward(Unit unit, int steps) {
        int taken = 0;
        for (int i = 0; i < steps; i++) {
            Position next = unit.getPosition().offset(unit.getDirection(), 1);
            if (!isInsideBounds(next) || isBlocked(next)) {
                break;
            }
            unit.setPosition(next);
            taken++;
        }
        return taken;
    }

    public int moveUnitBack(Unit unit, int steps) {
        int taken = 0;
        Direction opposite = switch (unit.getDirection()) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
        for (int i = 0; i < steps; i++) {
            Position next = unit.getPosition().offset(opposite, 1);
            if (!isInsideBounds(next) || isBlocked(next)) {
                break;
            }
            unit.setPosition(next);
            taken++;
        }
        return taken;
    }
}

