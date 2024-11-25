package org.texttechnologylab.project.Parliament_Browser_09_2.website.impl;

import org.texttechnologylab.project.Parliament_Browser_09_2.website.SessionToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Implementation of the SessionToken class
 *
 * @author Stud
 */
public class SessionTokenImpl implements SessionToken {
    private final UUID token;
    private final String username;
    private final Instant validUntil;

    /**
     * Generates a new session token for the given user
     *
     * @param username username of the user
     */
    public SessionTokenImpl(String username) {
        this.token = UUID.randomUUID();
        this.username = username;
        // For demonstration purposes, this lasts only for 30 minutes
        this.validUntil = Instant.now().plus(30, ChronoUnit.MINUTES);
    }

    /**
     * Gets the session token
     *
     * @return The session token
     */
    @Override
    public UUID getToken() {
        return token;
    }

    /**
     * Gets the ID of the user
     *
     * @return The user id
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Checks if the token is valid or expired
     *
     * @return Whether the token is still valid
     */
    @Override
    public boolean valid() {
        return validUntil.isAfter(Instant.now());
    }
}
