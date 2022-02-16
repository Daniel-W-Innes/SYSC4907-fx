package ca.carleton.sysc4907fx;

public final class PhysicsEngine {
    public static double xComponent(double d, int angle) {
        if (angle < 90) {
            return d * Math.sin(Math.toRadians(angle));
        }
        if (angle < 180) {
            return d * Math.cos(Math.toRadians(angle - 90));
        }
        if (angle < 270) {
            return -d * Math.sin(Math.toRadians(angle - 180));
        }
        return -d * Math.cos(Math.toRadians(angle - 270));
    }

    public static double yComponent(double d, int angle) {
        if (angle < 90) {
            return d * Math.cos(Math.toRadians(angle));
        }
        if (angle < 180) {
            return -d * Math.sin(Math.toRadians(angle - 90));
        }
        if (angle < 270) {
            return -d * Math.cos(Math.toRadians(angle - 180));
        }
        return d * Math.sin(Math.toRadians(angle - 270));
    }

    public static double deltaD(double dt, double v, double a, double j) {
        return v * dt + 0.5 * a * Math.pow(dt, 2) + 1.0 / 6 * j * Math.pow(Math.min(-a / j, dt), 3);
    }

    public static double deltaV(double dt, double a, double j) {
        return a * dt + 0.5 * j * Math.pow(Math.min(-a / j, dt), 2);
    }

    public static double deltaA(double dt, double a, double j) {
        return j * Math.min(-a / j, dt);
    }
}
