package Player;

import com.orbischallenge.pacman.api.common.*;
import com.orbischallenge.pacman.api.java.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * The Player class is the parent class of your AI player. It is just like a
 * template. Your AI player class (called PacPlayer) must implement the
 * following methods.
 *
 */
public class PacPlayer implements Player {

    private int lives = 3;
    private Pac pac;
    private MazeGraph graph;
    private List<MoveDir> motion;
    private Ghost targetGhost;
    private static final double[] ghostSpeed = {4, 6, 7, 7, 7.5, 7.5, 7.5, 7.5, 8};
    private int level = 0;

    /**
     * This is method decides Pacman�s moving direction in the next frame (See
     * Frame Concept). The parameters represent the maze, ghosts, Pacman, and
     * score after the execution of last frame. In the next frame, the game will
     * call this method to set Pacman�s direction and let him move (see Pacman�s
     * Move).
     *
     * @param maze A Maze object representing the current maze.
     * @param ghosts An array of Ghost objects representing the four ghosts.
     * @param pac A Pac object representing Pacman
     * @param score The current score
     * @return MoveDir
     */
    @Override
    public MoveDir calculateDirection(Maze maze, Ghost[] ghosts, Pac pac, int score) {
        //if (this.motion.isEmpty()) {
        MazeItem[][] items = maze.toMatrix();
        if (items[this.pac.getTileY()][this.pac.getTileX()] == MazeItem.TELEPORT) {
            return MoveDir.LEFT;
        }
        List<Point> dots = this.graph.findNearestDots(this.pac.getTile(), this.graph.getCurrentDots());

        // Try targetGhost first
        if (targetGhost.getState() == GhostState.FRIGHTEN && targetGhost.framesTillRecover() > 5) {
            List<Point> path = this.graph.getShortestPath(this.pac.getTile(), this.targetGhost.getTile());
            if (path.size() < 5) {
                List<MoveDir> dir = MazeGraph.pathToMoveDir(this.pac.getTile(), path);
                this.motion = dir;
                if (dir.isEmpty()) {
                    MoveDir pacDir = this.pac.getDir();
                    for (MoveDir possibleDir : this.pac.getPossibleDirs()) {
                        if (possibleDir == pacDir) {
                            return pacDir;
                        }
                    }
                    return this.pac.getPossibleDirs().get(0);
                }
                return this.motion.get(0);
            }
        }

        // Try chase ghost first
        boolean chasingGhost = false;
        boolean ghostComingChasingGhost = false;
        List<Point> chasePath = new ArrayList<Point>();
        for (Ghost ghost : this.graph.findNearestGhosts(this.pac.getTile(), ghosts)) {
            if (ghost.getState() == GhostState.FRIGHTEN && ghost.framesTillRecover() > 5) {
                if (Utility.manhattan_distance(this.pac.getTileX(), this.pac.getTileY(), ghost.getTileX(), ghost.getTileY()) < 5) {
                    if (!chasingGhost && this.pac.getTileX() != ghost.getTileX() && this.pac.getTileY() != ghost.getTileY()) {
                        chasePath = this.graph.getShortestPath(this.pac.getTile(), ghost.getTile());
                        chasingGhost = true;
                        this.targetGhost = ghost;
                        break;
                    }
                }
            }
        }
        if (chasingGhost) {
            for (Ghost ghost : ghosts) {
                List<Point> ghostPath = this.graph.getShortestPath(ghost.getTile(), this.pac.getTile());
                if (!this.isGhostOnPath(ghostPath, ghosts)) {
                    if (ghost.getState() != GhostState.FRIGHTEN || ghost.framesTillRecover() < 5) {
                        if (ghostPath.size() / this.getGhostSpeed(level) < chasePath.size() / 8.0 + 0.5 && !ghostPath.isEmpty()) {
                            if (isPathsOverlap(chasePath, ghostPath)) {
                                ghostComingChasingGhost = true;
                            }
                        }
                    }
                }
            }
        }
        if (ghostComingChasingGhost || !chasingGhost) {
            for (Point dot : dots) {
                List<Point> path = this.graph.getShortestPath(this.pac.getTile(), dot);
                boolean ghostComing = false;
                if (!this.isGhostOnPath(path, ghosts)) {
                    for (Ghost ghost : ghosts) {
                        List<Point> ghostPath = this.graph.getShortestPath(ghost.getTile(), this.pac.getTile());
                        if (ghost.getState() != GhostState.FRIGHTEN || ghost.framesTillRecover() < 5) {
                            if (ghostPath.size() / this.getGhostSpeed(level) < path.size() / 8.0 + 0.5 && !ghostPath.isEmpty()) {
                                if (isPathsOverlap(path, ghostPath)) {
                                    ghostComing = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (ghostComing) {
                        continue;
                    }
                    List<MoveDir> dir = MazeGraph.pathToMoveDir(this.pac.getTile(), path);
                    this.motion = dir;
                    if (dir.isEmpty()) {
                        MoveDir pacDir = this.pac.getDir();
                        for (MoveDir possibleDir : this.pac.getPossibleDirs()) {
                            if (possibleDir == pacDir) {
                                return pacDir;
                            }
                        }
                        return this.pac.getPossibleDirs().get(0);
                    }
                    return this.motion.get(0);
                }
            }
        } else {
            List<MoveDir> dir = MazeGraph.pathToMoveDir(this.pac.getTile(), chasePath);
            if (dir.size() > 0) {
                this.motion = dir;
                return this.motion.get(0);
            }
        }
        List<MoveDir> possibleDir = this.pac.getPossibleDirs();
        Random r = new Random();
        MoveDir startDir = possibleDir.get(r.nextInt(possibleDir.size()));
        this.motion = MazeGraph.pathToMoveDir(this.pac.getTile(), this.graph.getPathToNextNode(this.pac.getTile(), startDir));

        return this.motion.get(0);
        //return startDir;
    }

    /**
     * This method will be called by the game whenever a new level starts. The
     * parameters represent the game objects at their initial states. This
     * method will always be called before calculateDirection.
     *
     * @param maze A Maze object representing the current maze.
     * @param ghosts An array of Ghost objects representing the four ghosts.
     * @param pac A Pac object representing Pacman
     * @param score The current score
     */
    @Override
    public void onLevelStart(Maze maze, Ghost[] ghosts, Pac pac, int score) {
        System.out.println("Java player start new level!");
        this.graph = new MazeGraph(maze);
        this.pac = pac;
        this.motion = new ArrayList<MoveDir>();
        this.targetGhost = ghosts[0];
        this.level++;
    }

    /**
     * This method will be called by the game whenever Pacman receives a new
     * life, including the first life. The parameters represent the repositioned
     * game objects. This method will always be called before calculateDirection
     * and after onLevelStart.
     *
     * @param maze A Maze object representing the current maze.
     * @param ghosts An array of Ghost objects representing the four ghosts.
     * @param pac A Pac object representing Pacman
     * @param score The current score
     */
    @Override
    public void onNewLife(Maze maze, Ghost[] ghosts, Pac pac, int score) {
        System.out.println("Hi, I still have " + lives + " lives left.");
        this.lives--;
        this.pac = pac;
        this.motion = new ArrayList<MoveDir>();
        this.targetGhost = ghosts[0];
    }

    /**
     * 
     * @param path
     * @param ghosts
     * @return 
     */
    private boolean isGhostOnPath(List<Point> path, Ghost[] ghosts) {
        for (Point point : path) {
            for (Ghost ghost : ghosts) {
                if (ghost.getState() != GhostState.FRIGHTEN || ghost.framesTillRecover() < 5) {
                    Point ghostPoint = ghost.getTile();
                    if (ghostPoint.x == point.x && ghostPoint.y == point.y) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPathsOverlap(List<Point> path1, List<Point> path2) {
        HashSet<String> hash = new HashSet<String>();
        for (Point point : path1) {
            String x = new String(point.x + "," + point.y);
            hash.add(x);
        }
        for (Point point : path2) {
            String x = new String(point.x + "," + point.y);
            if (hash.contains(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the pac
     */
    public Pac getPac() {
        return this.pac;
    }

    public double getGhostSpeed(int level) {
        if (level >= ghostSpeed.length) {
            return ghostSpeed[ghostSpeed.length - 1];
        } else {
            return ghostSpeed[level];
        }
    }
}
