package org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.impl;

import org.apache.commons.lang3.EnumUtils;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.Group;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.Permission;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the group interface
 *
 * @author Stud
 */
public class GroupImpl implements Group {
    private final String name;
    private final Set<Permission> permissions;

    /**
     * Creates a group.
     * Initially, this group does not have any permissions.
     *
     * @param name Name der Gruppe
     * @author Stud
     */
    public GroupImpl(String name) {
        if (name.equals(Group.WEBMASTER)) {
            throw new RuntimeException("You are not allowed to call a group \"" + Group.WEBMASTER + "\"!");
        }
        this.name = name;
        this.permissions = new HashSet<>();
    }

    /**
     * Creates a group from a MongoDB document.
     * If any permissions can't be mapped, they will be discarded.
     *
     * @param document The MongoDB document
     * @author Stud
     */
    public GroupImpl(Document document) {
        this.name = document.getString("_id");
        this.permissions = document
                .getList("permissions", String.class)
                .stream()
                .map(string -> EnumUtils.getEnum(Permission.class, string))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    /**
     * Gets the name of the group
     *
     * @return Name of the group
     * @author Stud
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the permissions the group has
     *
     * @return Set of permissions
     * @author Stud
     */
    @Override
    public Set<Permission> getPermissions() {
        return new HashSet<>(permissions);
    }

    /**
     * Grants a group permissions
     *
     * @param permissions Permissions to grant
     * @author Stud
     */
    @Override
    public void grantPermissions(Permission... permissions) {
        this.permissions.addAll(Set.of(permissions));
    }

    /**
     * Revokes permissions from a group
     *
     * @param permissions Permissions to revoke
     * @author Stud
     */
    @Override
    public void revokePermissions(Permission... permissions) {
        this.permissions.removeAll(Set.of(permissions));
    }

    /**
     * Converts the group to a document that can
     * be stored in the database.
     * Uses the name as the ID.
     *
     * @return The document
     * @author Stud
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("_id", name);
        document.put("permissions", permissions.stream().map(Permission::toString).collect(Collectors.toSet()));
        return document;
    }
}
