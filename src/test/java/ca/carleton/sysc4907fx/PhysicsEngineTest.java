package ca.carleton.sysc4907fx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsEngineTest {

    @Test
    void xComponent() {
        assertEquals(1,PhysicsEngine.xComponent(2,30),0.0001,"30-60-90 a=30,d=2");
        assertEquals(1,PhysicsEngine.xComponent(2,150),0.0001,"30-60-90 a=150),d=2");
        assertEquals(-1,PhysicsEngine.xComponent(2,210),0.0001,"30-60-90 a=210,d=2");
        assertEquals(-1,PhysicsEngine.xComponent(2,330),0.0001,"30-60-90 a=330,d=2");

        assertEquals(1,PhysicsEngine.xComponent(Math.sqrt(2),45),0.0001,"45-45-90 a=45,d=sqrt(2)");
        assertEquals(1,PhysicsEngine.xComponent(Math.sqrt(2),135),0.0001,"45-45-90 a=135,d=sqrt(2)");
        assertEquals(-1,PhysicsEngine.xComponent(Math.sqrt(2),225),0.0001,"45-45-90 a=225,d=sqrt(2)");
        assertEquals(-1,PhysicsEngine.xComponent(Math.sqrt(2),315),0.0001,"45-45-90 a=315,d=sqrt(2)");
    }

    @Test
    void yComponent() {
        assertEquals(1,PhysicsEngine.yComponent(2,60),0.0001,"30-60-90 a=60,d=2");
        assertEquals(-1,PhysicsEngine.yComponent(2,120),0.0001,"30-60-90 a=120,d=2");
        assertEquals(-1,PhysicsEngine.yComponent(2,240),0.0001,"30-60-90 a=240,d=2");
        assertEquals(1,PhysicsEngine.yComponent(2,300),0.0001,"30-60-90 a=300,d=2");

        assertEquals(1,PhysicsEngine.yComponent(Math.sqrt(2),45),0.0001,"45-45-90 a=45,d=sqrt(2)");
        assertEquals(-1,PhysicsEngine.yComponent(Math.sqrt(2),135),0.0001,"45-45-90 a=135,d=sqrt(2)");
        assertEquals(-1,PhysicsEngine.yComponent(Math.sqrt(2),225),0.0001,"45-45-90 a=225,d=sqrt(2)");
        assertEquals(1,PhysicsEngine.yComponent(Math.sqrt(2),315),0.0001,"45-45-90 a=315,d=sqrt(2)");
    }
}