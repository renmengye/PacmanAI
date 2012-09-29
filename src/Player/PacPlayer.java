package Player;

import java.awt.Point;
import java.util.List;

import com.orbischallenge.pacman.api.common.*;
import com.orbischallenge.pacman.api.java.*;
import java.util.ArrayList;

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
        Point dot = this.graph.findNearestDot(this.pac.getTile(), this.graph.getCurrentDots());
        return MazeGraph.pathToMoveDir(this.pac.getTile(), this.graph.getShortestPath(this.pac.getTile(), dot, 100)).get(0);
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
    }

    ;

    /**
     * @return the pac
     */
    public Pac getPac() {
        return this.pac;
    }
}