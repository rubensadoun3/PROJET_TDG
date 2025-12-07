package Simulation;

public class IntersectionSimu {
    public String id;
    public int x, y;

    public boolean estVisite = false;

    public IntersectionSimu(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
