package com.tallerwebi.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void hashNoDebeGuardarLaContraseniaEnTextoPlano() {
        String plain = "miClaveSegura";
        String hashed = passwordHasher.hash(plain);

        assertNotNull(hashed);
        assertNotEquals(plain, hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$"));
    }

    @Test
    void matchesDebeAceptarLaMismaContrasenia() {
        String plain = "test";
        String hashed = passwordHasher.hash(plain);

        assertTrue(passwordHasher.matches(plain, hashed));
        assertFalse(passwordHasher.matches("otraClave", hashed));
    }

    @Test
    void debeReconocerHashesDeLosUsuariosSeed() {
        // Mismos hashes que data.sql (claves originales: test / 1234 / 123)
        assertTrue(passwordHasher.matches(
                "test",
                "$2b$10$FVwnVjiUNGF4NAEYYGg4rec8fLmsuJNRUWNPPFTiCnyFrhW0fqrSC"
        ));
        assertTrue(passwordHasher.matches(
                "1234",
                "$2b$10$wmQo7Duh3OCh1BPjXDKQ2OaQ/JCqATA7/jsA9iB0Y4DDfOcis9Ke6"
        ));
        assertTrue(passwordHasher.matches(
                "123",
                "$2b$10$e4JBS0NalALR/mf3uQvN8eSD5xIaQFcpAvPyyTX3VIcSCok5LZeTm"
        ));
    }
}
