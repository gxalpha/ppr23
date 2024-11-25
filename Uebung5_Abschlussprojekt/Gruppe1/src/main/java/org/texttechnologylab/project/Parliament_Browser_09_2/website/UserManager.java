package org.texttechnologylab.project.Parliament_Browser_09_2.website;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.Group;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.User;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.impl.GroupImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.impl.UserImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.AlreadyInTargetStateException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.NotFoundException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.UserAuthenticationFailedException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.UserSessionExpiredException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.impl.SessionTokenImpl;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class to manage users of the program
 *
 * @author Stud
 */
public class UserManager {
    private final MongoDatabase database;
    private final SecureRandom random;
    private final BCrypt.Verifyer verifier;
    private final BCrypt.Hasher hasher;
    private final Map<UUID, SessionToken> tokens;

    /**
     * Creates a new UserManager instance.
     *
     * @param handler MongoDBHandler of the database to use
     * @author Stud
     */
    public UserManager(MongoDBHandler handler) {
        this.database = handler.getMongoDatabase();
        this.random = new SecureRandom();
        this.hasher = BCrypt.withDefaults();
        this.verifier = BCrypt.verifyer();
        this.tokens = new HashMap<>();
    }

    /**
     * Creates a new user and saves it to the database.
     * Synchronized to make sure we're not potentially creating the same user twice.
     *
     * @param username Username of the user. Must be unique.
     * @param password Password of the user
     * @throws AlreadyInTargetStateException If the user already exists
     * @author Stud
     */
    public synchronized void createUser(String username, byte[] password, boolean webmaster) throws AlreadyInTargetStateException {
        if (database.getCollection("users_user").find(Filters.eq("_id", username)).first() != null) {
            throw new AlreadyInTargetStateException();
        }

        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] hash = hasher.hash(12, salt, password);
        User user = new UserImpl(username, hash, Set.of());
        if (webmaster) {
            user.addGroup(Group.WEBMASTER);
        }
        upsertUser(user);
    }

    /**
     * Sets a new password for a user.
     * Invalidates all active session tokens.
     *
     * @param username    Username of the user
     * @param newPassword New password
     * @throws NotFoundException If the user couldn't be found
     */
    public void setUserPassword(String username, byte[] newPassword) throws NotFoundException {
        Document document = database.getCollection("users_user").find(Filters.eq("_id", username)).first();
        if (document == null) {
            throw new NotFoundException();
        }
        User user = new UserImpl(document);

        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] hash = hasher.hash(12, salt, newPassword);

        user.setPasswordHash(hash);
        upsertUser(user);

        Set<UUID> invalidTokens = tokens.values()
                .stream()
                .filter(token -> token.getUsername().equals(username))
                .map(SessionToken::getToken)
                .collect(Collectors.toSet());
        invalidTokens.forEach(tokens::remove);
    }


    /**
     * Creates a new group and saves it to the database.
     * Synchronized to make sure we're not potentially creating the same group twice.
     *
     * @param groupName Name of the group. Must be unique.
     * @throws AlreadyInTargetStateException If the group already exists (yes we're reusing the exception)
     * @author Stud
     */
    public synchronized void createGroup(String groupName) throws AlreadyInTargetStateException {
        if (database.getCollection("users_group").find(Filters.eq("_id", groupName)).first() != null) {
            throw new AlreadyInTargetStateException();
        }

        Group group = new GroupImpl(groupName);
        upsertGroup(group);
    }

    /**
     * Deletes a user from the database
     *
     * @param username username of the user to delete
     * @throws NotFoundException When the user doesn't exist in the first place
     * @author Stud
     */
    public synchronized void deleteUser(String username) throws NotFoundException {
        Document document = database.getCollection("users_user").find(Filters.eq("_id", username)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        database.getCollection("users_user").deleteOne(Filters.eq("_id", username));
    }

    /**
     * Deletes a group from the database
     *
     * @param groupName name of the group to delete
     * @throws NotFoundException When the group doesn't exist in the first place (or is the webmaster group)
     * @author Stud
     */
    public synchronized void deleteGroup(String groupName) throws NotFoundException {
        Document document = database.getCollection("users_group").find(Filters.eq("_id", groupName)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        database.getCollection("users_group").deleteOne(Filters.eq("_id", groupName));
    }

    /**
     * Verifies a user's password
     *
     * @param username username of the user
     * @param password password of the user
     * @return Whether the user's password is correct
     * @throws NotFoundException When the user doesn't exist
     * @author Stud
     */
    public boolean verifyUser(String username, byte[] password) throws NotFoundException {
        Document document = database.getCollection("users_user").find(Filters.eq("_id", username)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        User user = new UserImpl(document);
        BCrypt.Result result = verifier.verify(password, user.getPasswordHash());
        return result.verified;
    }

    /**
     * Logs in a user and creates a token for the session
     *
     * @param username username of the user
     * @param password password of the user
     * @return SessionToken UUID
     * @throws NotFoundException                 When the user couldn't be found
     * @throws UserAuthenticationFailedException When the user authentication failed
     * @author Stud
     */
    public synchronized SessionToken loginUser(String username, byte[] password) throws NotFoundException, UserAuthenticationFailedException {
        if (!verifyUser(username, password)) {
            throw new UserAuthenticationFailedException();
        }

        SessionToken token = new SessionTokenImpl(username);
        tokens.put(token.getToken(), token);
        return token;
    }

    /**
     * Logs out a user, destroying its token
     *
     * @param sessionToken Session token of the user to log out
     * @author Stud
     */
    public void logoutUser(String sessionToken) {
        tokens.remove(UUID.fromString(sessionToken));
    }

    /**
     * Find out whether a user with a given session token has
     * a certain permission
     *
     * @param sessionToken The session token
     * @param permission   Permission to check
     * @return Whether the user has the permission
     * @author Stud
     */
    public boolean isUserPermitted(String sessionToken, Permission permission) throws UserSessionExpiredException {
        if (sessionToken == null) {
            throw new UserSessionExpiredException();
        }
        SessionToken token = tokens.get(UUID.fromString(sessionToken));
        if (token == null || !token.valid()) {
            throw new UserSessionExpiredException();
        }

        Document document = database.getCollection("users_user").find(Filters.eq("_id", token.getUsername())).first();
        if (document == null) {
            return false;
        }

        User user = new UserImpl(document);

        // Webmaster accounts are permitted to do *everything*
        if (user.getGroups().contains(Group.WEBMASTER)) {
            return true;
        }

        Set<Group> groups = user.getGroups()
                .stream()
                .map(groupName -> database.getCollection("users_group").find(Filters.eq("_id", groupName)).first())
                .filter(Objects::nonNull)
                .map(GroupImpl::new)
                .collect(Collectors.toSet());

        // Normal foreach loop so that we can return
        for (Group group : groups) {
            if (group.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the name of the user from a given session token
     *
     * @param sessionToken Token of the session
     * @author Stud
     */
    public String getUsernameFromToken(String sessionToken) {
        if (sessionToken == null) {
            return null;
        }
        SessionToken token = tokens.get(UUID.fromString(sessionToken));
        if (token == null || !token.valid()) {
            return null;
        }
        return token.getUsername();
    }

    /**
     * Gets user information for a given user
     *
     * @param username Name of the user to find
     * @return User object of the user. May be null.
     * @author Stud
     */
    public User getUserFromUsername(String username) throws NotFoundException {
        Document document = database.getCollection("users_user").find(Filters.eq("_id", username)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        return new UserImpl(document);
    }

    /**
     * Returns all currently known users
     *
     * @return Set of all users
     * @author Stud
     */
    public Set<User> listUsers() {
        Set<User> users = new HashSet<>();
        database.getCollection("users_user").find()
                .map(UserImpl::new)
                .forEach((Consumer<? super User>) users::add);
        return users;
    }

    /**
     * Returns all currently known groups
     *
     * @return Set of all groups
     * @author Stud
     */
    public Set<Group> listGroups() {
        Set<Group> groups = new HashSet<>();
        database.getCollection("users_group").find()
                .map(GroupImpl::new)
                .forEach((Consumer<? super Group>) groups::add);
        return groups;
    }

    /**
     * @param user      User to add to the group
     * @param groupName Name of the group
     * @throws NotFoundException             When the group can't be found
     * @throws AlreadyInTargetStateException When the user already is in the group
     * @author Stud
     */
    public void addUserToGroup(User user, String groupName) throws NotFoundException, AlreadyInTargetStateException {
        Document document = database.getCollection("users_group").find(Filters.eq("_id", groupName)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        if (user.getGroups().contains(groupName)) {
            throw new AlreadyInTargetStateException();
        }

        user.addGroup(groupName);
        upsertUser(user);
    }

    /**
     * @param user      User to remove from the group
     * @param groupName Name of the group
     * @throws AlreadyInTargetStateException When the user isn't even in the group
     * @author Stud
     */
    public void removeUserFromGroup(User user, String groupName) throws AlreadyInTargetStateException {
        if (!user.getGroups().contains(groupName)) {
            throw new AlreadyInTargetStateException();
        }

        user.removeGroup(groupName);
        upsertUser(user);
    }

    /**
     * Grants a permission to a group
     *
     * @param groupName  Name of the group
     * @param permission Permission to grant
     * @throws NotFoundException             If the group can't be found
     * @throws AlreadyInTargetStateException If the group already has the permission
     * @author Stud
     */
    public synchronized void grantPermissionToGroup(String groupName, Permission permission) throws NotFoundException, AlreadyInTargetStateException {
        Document document = database.getCollection("users_group").find(Filters.eq("_id", groupName)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        Group group = new GroupImpl(document);
        if (group.getPermissions().contains(permission)) {
            throw new AlreadyInTargetStateException();
        }
        group.grantPermissions(permission);

        upsertGroup(group);
    }

    /**
     * Grants a permission to a group
     *
     * @param groupName  Name of the group
     * @param permission Permission to grant
     * @throws NotFoundException             If the group can't be found
     * @throws AlreadyInTargetStateException If the group doesn't have the permission
     * @author Stud
     */
    public synchronized void revokePermissionFromGroup(String groupName, Permission permission) throws NotFoundException, AlreadyInTargetStateException {
        Document document = database.getCollection("users_group").find(Filters.eq("_id", groupName)).first();
        if (document == null) {
            throw new NotFoundException();
        }

        Group group = new GroupImpl(document);
        if (!group.getPermissions().contains(permission)) {
            throw new AlreadyInTargetStateException();
        }
        group.revokePermissions(permission);

        upsertGroup(group);
    }

    /**
     * Updates a user in the database, or creates it if it does not already exist.
     *
     * @param user User to upsert
     * @author Stud
     */
    private void upsertUser(User user) {
        MongoCollection<Document> collection = database.getCollection("users_user");
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        collection.replaceOne(Filters.eq("_id", user.getUsername()), user.toDoc(), options);
    }

    /**
     * Updates a group in the database, or creates it if it does not already exist.
     *
     * @param group Group to upsert
     * @author Stud
     */
    private void upsertGroup(Group group) {
        MongoCollection<Document> collection = database.getCollection("users_group");
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        collection.replaceOne(Filters.eq("_id", group.getName()), group.toDoc(), options);
    }

    /**
     * Purges the list of tokens to remove all expired ones
     *
     * @author Stud
     */
    private synchronized void purgeTokens() {
        Set<UUID> expiredTokens = tokens.values().stream()
                .filter(Predicate.not(SessionToken::valid))
                .map(SessionToken::getToken)
                .collect(Collectors.toUnmodifiableSet());
        expiredTokens.forEach(tokens::remove);
    }
}
