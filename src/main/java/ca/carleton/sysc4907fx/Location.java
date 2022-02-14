package ca.carleton.sysc4907fx;

public record Location(double lat, double lng) {
    public static final int Radius = 6371;

    public double distance(Location l2) {
        double latDistance = Math.toRadians(l2.lat - lat);
        double lonDistance = Math.toRadians(l2.lng - lng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(l2.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Radius * c * 1000;
    }

    @Override
    public String toString() {
        return lat + "," + lng;
    }
}
