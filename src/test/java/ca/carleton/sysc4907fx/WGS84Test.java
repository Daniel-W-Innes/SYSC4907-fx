package ca.carleton.sysc4907fx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WGS84Test {
    private final static double x = 443715.53;
    private final static double y = 5019242.12;
    private final static char zoneName = 'T';
    private final static int zoneNumber = 18;

    @Test
    void offset() {
        int offset = 1;
        WGS84 wgs84 = new WGS84(x,y,zoneName,zoneNumber);
        WGS84 wgs84_2 = new WGS84(x+offset,y+offset,zoneName,zoneNumber);
        wgs84.offset(offset,offset);
        assertEquals(wgs84_2,wgs84);
    }

    @Test
    void getLatLong() {
        WGS84 wgs84 = new WGS84(x,y,zoneName,zoneNumber);
        Location location = wgs84.getLatLong();
        WGS84 wgs84_2 = location.WGS84();
        assertEquals(wgs84_2,wgs84);
    }

    @Test
    void getLatLongOffset() {
        int offset = 1;
        WGS84 wgs84 = new WGS84(x,y,zoneName,zoneNumber);
        Location location = wgs84.getLatLongOffset(offset,offset);
        WGS84 wgs84_2 = new WGS84(x+offset,y+offset,zoneName,zoneNumber);
        Location location2 = wgs84_2.getLatLong();
        assertEquals(location2,location);
    }
}