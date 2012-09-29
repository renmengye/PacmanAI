/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import com.orbischallenge.pacman.api.common.Utility;
import com.orbischallenge.pacman.api.java.Pac;
import com.orbischallenge.pacman.util.Point;

/**
 *
 * @author renme_000
 */
public class Goal {

    private Pac pac;
    private boolean positive;
    private Point point;
    private int distance;

    public Goal(Pac pac, boolean positive, Point target) {
        this.pac = pac;
        this.positive = positive;
        this.point = target;
    }

    /**
     * @return the positive
     */
    public boolean isPositive() {
        return positive;
    }

    /**
     * @param positive the positive to set
     */
    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    /**
     * @return the point
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     * @return the distance
     */
    public int getDistance(Point startPoint) {
        return Utility.manhattan_distance(this.point.x, this.point.x, this.pac.getTileX(), this.pac.getTileY());
    }
}
