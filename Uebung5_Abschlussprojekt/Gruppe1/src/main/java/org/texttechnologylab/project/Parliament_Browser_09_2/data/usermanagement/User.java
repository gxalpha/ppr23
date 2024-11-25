package org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement;

import org.bson.Document;

import java.util.Set;

/**
 * Interface to store a user in the database.
 *
 * @author Stud
 */
public interface User {
    /**
     * Gets the username
     *
     * @return the username
     */
    String getUsername();

    /**
     * Gets the hashed password to validate against
     *
     * @return The hashed password
     */
    byte[] getPasswordHash();

    /**
     * Sets a new password hash
     *
     * @param hash The hashed password
     */
    void setPasswordHash(byte[] hash);

    /**
     * Returns the names of the groups a user is in.
     *
     * @return Set of groups
     */
    Set<String> getGroups();

    /**
     * Adds the user to a group
     *
     * @param groupName Group to add to
     */
    void addGroup(String groupName);

    /**
     * Removes the user from a group
     *
     * @param groupName Group to remove from
     */
    void removeGroup(String groupName);

    /**
     * Converts the user to a document that can be saved
     * in the database. Uses the username as the ID, the
     * username can not be changed during the life of a
     * user (this is also because the client side uses
     * the username as the salt for an <i>additional</i>
     * layer of hashing - that way, the server never
     * actually receives the cleartext password that the
     * user entered).
     *
     * @return Document of the user
     */
    Document toDoc();
}
