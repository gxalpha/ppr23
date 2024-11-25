package org.texttechnologylab.project.Parliament_Browser_09_2.website;

import java.util.UUID;

/**
 * Interface to store user session tokens
 *
 * @author Stud
 */
public interface SessionToken {
    /**
     * Gets the session token
     *
     * @return The session token
     */
    UUID getToken();

    /**
     * Gets the ID of the user
     *
     * @return The user id
     */
    String getUsername();

    /**
     * Checks if the token is valid or expired
     *
     * @return Whether the token is still valid
     */
    boolean valid();
}
