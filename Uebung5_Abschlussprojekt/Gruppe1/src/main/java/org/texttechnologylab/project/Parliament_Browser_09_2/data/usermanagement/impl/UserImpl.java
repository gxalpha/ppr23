package org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.impl;

import org.bson.Document;
import org.bson.types.Binary;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the User interface
 *
 * @author Stud
 */
public class UserImpl implements User {
    private final String username;
    private final Set<String> groups;
    private byte[] passwordHash;

    /**
     * Constructor for new user objects.
     *
     * @param username     username of the user
     * @param passwordHash Hash of the password of the user as a byte array
     * @param groups       Groups that the user is in
     * @author Stud
     */
    public UserImpl(String username, byte[] passwordHash, Set<String> groups) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.groups = new HashSet<>(groups);
    }

    /**
     * Constructor for the user object read from MongoDB
     *
     * @param document Document from the database
     */
    public UserImpl(Document document) {
        this.username = document.getString("_id");
        this.passwordHash = document.get("passwordHash", Binary.class).getData();
        this.groups = new HashSet<>(document.getList("groups", String.class));
    }

    /**
     * Gets the username
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Gets the hashed password to validate against
     *
     * @return The hashed password
     */
    @Override
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets a new password hash
     *
     * @param hash The hashed password
     */
    @Override
    public void setPasswordHash(byte[] hash) {
        this.passwordHash = hash;
    }

    /**
     * Returns the names of the groups a user is in.
     *
     * @return Set of groups
     */
    @Override
    public Set<String> getGroups() {
        return new HashSet<>(groups);
    }

    /**
     * Adds the user to a group
     *
     * @param groupName Group to add to
     */
    @Override
    public void addGroup(String groupName) {
        groups.add(groupName);
    }

    /**
     * Removes the user from a group
     *
     * @param groupName Group to remove from
     */
    @Override
    public void removeGroup(String groupName) {
        groups.remove(groupName);
    }

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
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("_id", username);
        document.put("passwordHash", passwordHash);
        document.put("groups", groups);
        return document;
    }
}
