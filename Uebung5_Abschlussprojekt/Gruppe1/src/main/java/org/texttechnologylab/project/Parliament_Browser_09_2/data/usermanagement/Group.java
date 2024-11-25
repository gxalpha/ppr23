package org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.Permission;

import java.util.Set;

/**
 * A group that defines certain permission.
 * Add this group to a user to grant it the permissions.
 *
 * @author Stud
 */
public interface Group {
    String WEBMASTER = "webmaster";

    /**
     * Gets the name of the group
     *
     * @return Name of the group
     */
    String getName();

    /**
     * Gets the permissions the group has
     *
     * @return Set of permissions
     */
    Set<Permission> getPermissions();

    /**
     * Grants a group permissions
     *
     * @param permissions Permissions to grant
     */
    void grantPermissions(Permission... permissions);

    /**
     * Revokes permissions from a group
     *
     * @param permissions Permissions to revoke
     */
    void revokePermissions(Permission... permissions);

    /**
     * Converts the group to a document that can
     * be stored in the database.
     * Uses the name as the ID.
     *
     * @return The document
     */
    Document toDoc();
}
