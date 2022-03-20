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

    public WGS84 WGS84() {
        int zoneNumber = (int) Math.floor(lng / 6 + 31);
        char zoneName;
        if (lat < -72) zoneName = 'C';
        else if (lat < -64) zoneName = 'D';
        else if (lat < -56) zoneName = 'E';
        else if (lat < -48) zoneName = 'F';
        else if (lat < -40) zoneName = 'G';
        else if (lat < -32) zoneName = 'H';
        else if (lat < -24) zoneName = 'J';
        else if (lat < -16) zoneName = 'K';
        else if (lat < -8) zoneName = 'L';
        else if (lat < 0) zoneName = 'M';
        else if (lat < 8) zoneName = 'N';
        else if (lat < 16) zoneName = 'P';
        else if (lat < 24) zoneName = 'Q';
        else if (lat < 32) zoneName = 'R';
        else if (lat < 40) zoneName = 'S';
        else if (lat < 48) zoneName = 'T';
        else if (lat < 56) zoneName = 'U';
        else if (lat < 64) zoneName = 'V';
        else if (lat < 72) zoneName = 'W';
        else zoneName = 'X';
        double x = 0.5 * Math.log((1 + Math.cos(lat * Math.PI / 180) * Math.sin(lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180)) / (1 - Math.cos(lat * Math.PI / 180) * Math.sin(lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180))) * 0.9996 * 6399593.62 / Math.pow((1 + Math.pow(0.0820944379, 2) * Math.pow(Math.cos(lat * Math.PI / 180), 2)), 0.5) * (1 + Math.pow(0.0820944379, 2) / 2 * Math.pow((0.5 * Math.log((1 + Math.cos(lat * Math.PI / 180) * Math.sin(lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180)) / (1 - Math.cos(lat * Math.PI / 180) * Math.sin(lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180)))), 2) * Math.pow(Math.cos(lat * Math.PI / 180), 2) / 3) + 500000;
        x = Math.round(x * 100) * 0.01;
        double y = (Math.atan(Math.tan(lat * Math.PI / 180) / Math.cos((lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180))) - lat * Math.PI / 180) * 0.9996 * 6399593.625 / Math.sqrt(1 + 0.006739496742 * Math.pow(Math.cos(lat * Math.PI / 180), 2)) * (1 + 0.006739496742 / 2 * Math.pow(0.5 * Math.log((1 + Math.cos(lat * Math.PI / 180) * Math.sin((lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180))) / (1 - Math.cos(lat * Math.PI / 180) * Math.sin((lng * Math.PI / 180 - (6 * zoneNumber - 183) * Math.PI / 180)))), 2) * Math.pow(Math.cos(lat * Math.PI / 180), 2)) + 0.9996 * 6399593.625 * (lat * Math.PI / 180 - 0.005054622556 * (lat * Math.PI / 180 + Math.sin(2 * lat * Math.PI / 180) / 2) + 4.258201531e-05 * (3 * (lat * Math.PI / 180 + Math.sin(2 * lat * Math.PI / 180) / 2) + Math.sin(2 * lat * Math.PI / 180) * Math.pow(Math.cos(lat * Math.PI / 180), 2)) / 4 - 1.674057895e-07 * (5 * (3 * (lat * Math.PI / 180 + Math.sin(2 * lat * Math.PI / 180) / 2) + Math.sin(2 * lat * Math.PI / 180) * Math.pow(Math.cos(lat * Math.PI / 180), 2)) / 4 + Math.sin(2 * lat * Math.PI / 180) * Math.pow(Math.cos(lat * Math.PI / 180), 2) * Math.pow(Math.cos(lat * Math.PI / 180), 2)) / 3);
        if (zoneName < 'N') y = y + 10000000;
        y = Math.round(y * 100) * 0.01;
        return new WGS84(x,y,zoneName,zoneNumber);
    }

    @Override
    public String toString() {
        return lat + "," + lng;
    }
}
