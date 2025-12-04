import java.util.ArrayList;
import java.util.List;

/**
 * Path class representing the enemy movement path.
 */
public class Path {
    private List<Point> waypoints;

    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public Path() {
        waypoints = new ArrayList<>();

        // KIVRIMLI YOL - KÖŞELER SONRA SPLINE İLE YUVARLATILACAK
        // Soldan giriş
        waypoints.add(new Point(40, 260));    // spawn

        // Sağa
        waypoints.add(new Point(200, 260));

        // Yukarı
        waypoints.add(new Point(200, 140));

        // Sağa
        waypoints.add(new Point(360, 140));

        // Aşağı
        waypoints.add(new Point(360, 320));

        // Sağa
        waypoints.add(new Point(520, 320));

        // Yukarı, üssün olduğu tarafa
        waypoints.add(new Point(520, 180));

        // Son nokta (base)
        waypoints.add(new Point(700, 180));
    }

    public List<Point> getWaypoints() {
        return waypoints;
    }

    public Point getWaypoint(int index) {
        if (index >= 0 && index < waypoints.size()) {
            return waypoints.get(index);
        }
        return waypoints.get(waypoints.size() - 1);
    }

    public int getLength() {
        return waypoints.size();
    }

    public Point getBase() {
        return waypoints.get(waypoints.size() - 1);
    }

    /**
     * Daha eliptik / yumuşak geçişler için:
     * pathIndex: 0.0 .. (waypoints.size()-1)
     * Aralarda Catmull-Rom spline ile interpolate ediyoruz.
     */
    public Point getPosition(double pathIndex) {
        if (waypoints.isEmpty()) {
            return null;
        }
        if (waypoints.size() == 1) {
            return waypoints.get(0);
        }

        // Baş ve son güvenliği
        if (pathIndex <= 0.0) {
            return waypoints.get(0);
        }
        if (pathIndex >= waypoints.size() - 1) {
            return getBase();
        }

        // Integer kısım segment index'i, ondalık kısım t
        int i1 = (int) Math.floor(pathIndex);
        double t = pathIndex - i1;        // 0..1

        // İndeksleri clamp et
        int i2 = i1 + 1;
        int i0 = Math.max(i1 - 1, 0);
        int i3 = Math.min(i2 + 1, waypoints.size() - 1);

        Point p0 = waypoints.get(i0);
        Point p1 = waypoints.get(i1);
        Point p2 = waypoints.get(i2);
        Point p3 = waypoints.get(i3);

        return catmullRom(p0, p1, p2, p3, t);
    }

    /**
     * Catmull-Rom spline formülü (t: 0..1).
     * P1 ve P2 üzerinden geçen, P0 ve P3'e göre eğimi ayarlanan smooth bir eğri verir.
     */
    private Point catmullRom(Point p0, Point p1, Point p2, Point p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double x = 0.5 * ((2.0 * p1.x) +
                (-p0.x + p2.x) * t +
                (2.0 * p0.x - 5.0 * p1.x + 4.0 * p2.x - p3.x) * t2 +
                (-p0.x + 3.0 * p1.x - 3.0 * p2.x + p3.x) * t3);

        double y = 0.5 * ((2.0 * p1.y) +
                (-p0.y + p2.y) * t +
                (2.0 * p0.y - 5.0 * p1.y + 4.0 * p2.y - p3.y) * t2 +
                (-p0.y + 3.0 * p1.y - 3.0 * p2.y + p3.y) * t3);

        return new Point(x, y);
    }
}