package org.texttechnologylab.project.Parliament_Browser_09_2.website;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.*;
import freemarker.template.Configuration;
import org.apache.commons.lang3.EnumUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javatuples.Pair;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.SitzungImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.Group;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.usermanagement.User;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.AbgeordneterDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.RedeNlpDBHelperInterface;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.AbgeordneterDBImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.RedeNlpDBHelper;
import org.texttechnologylab.project.Parliament_Browser_09_2.parsing.SessionParserThread;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.AlreadyInTargetStateException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.NotFoundException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.UserAuthenticationFailedException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.UserSessionExpiredException;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.impl.ProtokolToLaTeXImpl;
import spark.ModelAndView;
import spark.Response;
import spark.Spark;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Klasse zum Starten und Managen des Webservers
 *
 * @author Stud
 */
public class SparkHelper {

    private static boolean isRunning = false;
    private static UserManager userManager;
    private static MongoDBHandler mongoDBHandler;
    private static String headerHtml;

    /**
     * Versteckter Konstruktor
     */
    private SparkHelper() {
    }

    /**
     * Startet den Webserver
     *
     * @author Stud
     */
    public synchronized static void startup(MongoDBHandler handler) {

        if (isRunning) {
            throw new RuntimeException("Spark-Webserver läuft bereits");
        }

        userManager = new UserManager(handler);
        mongoDBHandler = handler;

        headerHtml = new BufferedReader(new InputStreamReader(SparkHelper.class.getClassLoader().getResourceAsStream("spark/other/header-frame.html"), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

        Spark.port(8002);
        Spark.staticFiles.location("/spark/static");
        Spark.ipAddress("localhost");
        Spark.init();

        Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        config.setClassForTemplateLoading(SparkHelper.class, "/spark/templates");
        FreeMarkerEngine engine = new FreeMarkerEngine(config);


        get("/", (request, response) -> new ModelAndView(Map.of("header", headerHtml), "main.ftl"), engine);

        addAdminRoutes(engine);
        addAbgeordneterRoutes(engine);
        addRedenRoutes(engine);
        addSitzungRoutes(engine);
        addVolltextsuche(engine);
        addSitzungRoutes(engine);

        isRunning = true;
    }

    /**
     * Helper function to create an error page
     *
     * @param response       Response object
     * @param code           HTTP status code
     * @param reason         HTTP status code description
     * @param detailedReason Human-readable description for what went wrong
     * @return A new page showing the error
     * @author Stud
     */
    private static ModelAndView makeErrorPage(Response response, int code, String reason, String detailedReason) {
        response.status(code);
        String status = code + " " + reason;
        return new ModelAndView(Map.of("status", status, "reason", detailedReason, "header", headerHtml), "error.ftl");
    }

    /**
     * Fügt die Routen für die Programm-Administration hinzu
     *
     * @param engine Template-Engine
     * @author Stud
     */
    private static void addAdminRoutes(TemplateEngine engine) {
        /*
         * Web-Route für die Login-Seite.
         * Methode: GET
         * Parameter:
         *     redirect (String, optional) - Website zum Weiterleiten nach erfolgreichem Login
         * Antwort: Html-Website. Alternativ: Redirect, falls Nutzer bereits eingeloggt ist.
         */
        get("/login", (request, response) -> {
            String redirect = request.queryParams("redirect");
            if (redirect == null || redirect.isEmpty()) {
                redirect = "/";
            }

            // TODO: If "session_token" cookie is already attached, redirect immediately.
            return new ModelAndView(Map.of("redirect_page", redirect, "header", headerHtml), "admin/login.ftl");
        }, engine);
        /*
         * Web-Route für Logout.
         * Methode: GET
         * Antwort: Html-Website.
         */
        get("/logout", (request, response) -> {
            String token = request.cookie("session_token");
            if (token != null) {
                userManager.logoutUser(token);
            }

            response.removeCookie("/", "session_token");
            return new ModelAndView(Map.of("header", headerHtml), "admin/logout.ftl");
        }, engine);
        /*
         * Route zum Authentifizieren.
         * Methode: POST
         *
         * Anfrage:
         * POST /login/authenticate
         * {
         *     username: "[USERNAME]",
         *     password: "[PASSWORD]"
         * }
         *
         * Antwort:
         * {
         *     status: "[success|userNotFound|userAuthenticationFailed]",
         *     token: "[TOKEN]?"
         * }
         */
        post("/login/authenticate", (request, response) -> {
            response.type("application/json");

            JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
            String username = json.get("username").getAsString();
            byte[] password = json.get("password").getAsString().getBytes();

            SessionToken token;
            // TODO: There are probably better way to communicate errors (e.g. via status codes) but I'm
            //  not sure how much I care.
            //  Also, we should probably use GSON or similar to create the JSON here instead of creating it manually, lol.
            try {
                token = userManager.loginUser(username, password);
            } catch (NotFoundException e) {
                return "{\"status\":\"userNotFound\"}";
            } catch (UserAuthenticationFailedException e) {
                return "{\"status\":\"userAuthenticationFailed\"}";
            }

            response.cookie("/", "session_token", token.getToken().toString(), -1, false /* Ideally we'd want secure cookies as well but Safari doesn't appear to allow them on localhost */, true);

            // TODO: I don't think we actually need to return the token for anything
            return "{\"status\":\"success\",\"token\":\"" + token.getToken() + "\"}";
        });

        /*
         * Path zu Account-Routen
         */
        path("/profile", () -> {
            /*
             * Web-Route für das Benutzerprofil.
             * Methode: GET
             * Antwort: Html-Website.
             *          Alternativ: Redirect, falls Nutzer nicht eingeloggt.
             */
            get("", (request, response) -> {
                String token = request.cookie("session_token");
                String username = userManager.getUsernameFromToken(token);
                if (username == null) {
                    response.redirect("/login?redirect=/profile");
                    return null;
                }

                User user;
                try {
                    user = userManager.getUserFromUsername(username);
                } catch (NotFoundException e) {
                    response.redirect("/login?redirect=/profile");
                    return null;
                }

                return new ModelAndView(Map.of("user", user, "header", headerHtml), "admin/profile.ftl");
            }, engine);
            /*
             * Route zum Passwort ändern
             * Methode: POST
             *
             * Anfrage:
             * POST /profile/changepassword
             * {
             *     username: "[USERNAME]",
             *     old_password: "[PASSWORD]",
             *     new_password: "[PASSWORD]"
             * }
             *
             * Antwort:
             * {
             *     status: "[success|userNotFound|userAuthenticationFailed]"
             * }
             */
            post("/changepassword", (request, response) -> {
                response.type("application/json");

                /* When changing the password and requiring the user to enter the old one, we
                 * might as well use it for authentication instead of the session token. */
                JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                String username = json.get("username").getAsString();
                byte[] oldPassword = json.get("old_password").getAsString().getBytes();
                byte[] newPassword = json.get("new_password").getAsString().getBytes();

                try {
                    if (!userManager.verifyUser(username, oldPassword)) {
                        return "{\"status\":\"userAuthenticationFailed\"}";
                    }
                    userManager.setUserPassword(username, newPassword);
                } catch (NotFoundException e) {
                    return "{\"status\":\"userNotFound\"}";
                }

                return "{\"status\":\"success\"}";
            });
        });

        /*
         * Path zu den Administrator-Routen
         */
        path("admin", () -> {
            /*
             * Path zur Benutzeradministration
             */
            path("/users", () -> {
                /*
                 * Web-Route für Benutzeradministration.
                 * Methode: GET
                 * Antwort: Html-Website.
                 *          Alternativ: Redirect, falls Nutzer nicht eingeloggt.
                 *          Alternativ: Fehler-Seite, falls Nutzer eingeloggt aber ohne Berechtigungen.
                 */
                get("", (request, response) -> {
                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_USERS)) {
                            // TODO: Not sure how to best handle this case
                            return makeErrorPage(response, 403, "Forbidden", "User does not have permissions to manage users");
                        }
                    } catch (UserSessionExpiredException e) {
                        response.redirect("/login?redirect=/admin/users");
                        return null;
                    }

                    List<User> users = userManager.listUsers()
                            .stream()
                            .sorted(Comparator.comparing(User::getUsername))
                            .collect(Collectors.toList());
                    List<Group> groups = userManager.listGroups()
                            .stream()
                            .sorted(Comparator.comparing(Group::getName))
                            .collect(Collectors.toList());
                    return new ModelAndView(Map.of("existingUsers", users, "existingGroups", groups, "header", headerHtml), "admin/users.ftl");
                }, engine);
                /*
                 * Route zum Nutzer erstellen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/users/create
                 * {
                 *     username: "[USERNAME]",
                 *     password: "[PASSWORD]",
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userNotFound|userAuthorizationFailed|userAlreadyExists|invalidUsername]"
                 * }
                 */
                post("/create", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    if (token == null) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }
                    boolean permitted;
                    try {
                        permitted = userManager.isUserPermitted(token, Permission.MANAGE_USERS);
                    } catch (UserSessionExpiredException ignored) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }
                    if (!permitted) {
                        return "{\"status\":\"userAuthorizationFailed\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String username = json.get("username").getAsString();
                    byte[] password = json.get("password").getAsString().getBytes();

                    // TODO: Validate user name (no hash sign, spaces, etc.). Maybe against alphabet regex.
                    // return "{\"status\":\"invalidUsername\"}";

                    try {
                        userManager.createUser(username, password, false);
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"userAlreadyExists\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Nutzer löschen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/users/delete
                 * {
                 *     username: "[USERNAME]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userNotFound|userAuthorizationFailed|userIsSelf|userIsWebmaster]"
                 * }
                 */
                post("/delete", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_USERS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String username = json.get("username").getAsString();

                    if (username.equals(userManager.getUsernameFromToken(token))) {
                        return "{\"status\":\"userIsSelf\"}";
                    }

                    try {
                        User toBeDeleted = userManager.getUserFromUsername(username);
                        if (toBeDeleted.getGroups().contains(Group.WEBMASTER)) {
                            return "{\"status\":\"userIsWebmaster\"}";
                        }
                        userManager.deleteUser(username);
                    } catch (NotFoundException e) {
                        return "{\"status\":\"userNotFound\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Nutzer zu einer Gruppe hinzufügen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/users/addgroup
                 * {
                 *     username: "[USERNAME]",
                 *     group_name: "[GROUP_NAME]",
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|userNotFound|userIsWebmaster|groupNotFound|userAlreadyInGroup]"
                 * }
                 */
                post("/addgroup", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_USERS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String username = json.get("username").getAsString();
                    String group_name = json.get("group_name").getAsString();

                    User user;
                    try {
                        user = userManager.getUserFromUsername(username);
                        if (user.getGroups().contains(Group.WEBMASTER)) {
                            return "{\"status\":\"userIsWebmaster\"}";
                        }
                    } catch (NotFoundException e) {
                        return "{\"status\":\"userNotFound\"}";
                    }

                    try {
                        userManager.addUserToGroup(user, group_name);
                    } catch (NotFoundException e) {
                        return "{\"status\":\"groupNotFound\"}";
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"userAlreadyInGroup\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Nutzer aus einer Gruppe entfernen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/users/removegroup
                 * {
                 *     username: "[USERNAME]",
                 *     group_name: "[GROUP_NAME]",
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|userNotFound|userIsWebmaster|userNotInGroup]"
                 * }
                 */
                post("/removegroup", (request, response) -> {
                    response.type("application/json");


                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_USERS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String username = json.get("username").getAsString();
                    String group_name = json.get("group_name").getAsString();

                    User user;
                    try {
                        user = userManager.getUserFromUsername(username);
                        if (user.getGroups().contains(Group.WEBMASTER)) {
                            return "{\"status\":\"userIsWebmaster\"}";
                        }
                    } catch (NotFoundException e) {
                        return "{\"status\":\"userNotFound\"}";
                    }

                    try {
                        userManager.removeUserFromGroup(user, group_name);
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"userNotInGroup\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
            });
            /*
             * Path zur Gruppenadministration
             */
            path("/groups", () -> {
                /*
                 * Web-Route für Gruppenadministration.
                 * Methode: GET
                 * Antwort: Html-Website.
                 *          Alternativ: Redirect, falls Nutzer nicht eingeloggt.
                 *          Alternativ: Fehler-Seite, falls Nutzer eingeloggt aber ohne Berechtigungen.
                 */
                get("", (request, response) -> {
                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_GROUPS)) {
                            // TODO: Not sure how to best handle this case
                            return makeErrorPage(response, 403, "Forbidden", "User does not have permissions to manage groups");
                        }
                    } catch (UserSessionExpiredException e) {
                        response.redirect("/login?redirect=/admin/groups");
                        return null;
                    }

                    List<Group> groups = userManager.listGroups()
                            .stream()
                            .sorted(Comparator.comparing(Group::getName))
                            .collect(Collectors.toList());
                    return new ModelAndView(Map.of("existingGroups", groups, "allPermissions", Permission.values(), "header", headerHtml), "admin/groups.ftl");
                }, engine);
                /*
                 * Route zum Gruppe erstellen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/groups/create
                 * {
                 *     group_name: "[GROUP_NAME]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|groupAlreadyExists|invalidGroupName]"
                 * }
                 */
                post("/create", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    if (token == null) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }
                    boolean permitted;
                    try {
                        permitted = userManager.isUserPermitted(token, Permission.MANAGE_GROUPS);
                    } catch (UserSessionExpiredException ignored) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }
                    if (!permitted) {
                        return "{\"status\":\"userAuthorizationFailed\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String groupName = json.get("group_name").getAsString();

                    if (groupName.equals(Group.WEBMASTER)) {
                        return "{\"status\":\"groupAlreadyExists\"}";
                    }
                    // TODO: Validate group name (no hash sign, spaces, etc.). Maybe against alphabet regex.
                    // return "{\"status\":\"invalidGroupName\"}";

                    try {
                        userManager.createGroup(groupName);
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"groupAlreadyExists\"}";
                    }
                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Gruppe löschen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/groups/delete
                 * {
                 *     group_name: "[GROUP_NAME]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|groupNotFound]"
                 * }
                 */
                post("/delete", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_GROUPS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String groupName = json.get("group_name").getAsString();

                    try {
                        userManager.deleteGroup(groupName);
                    } catch (NotFoundException e) {
                        return "{\"status\":\"groupNotFound\"}";
                    }

                    // TODO: Remove group from all users

                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Gruppenberechtigung erteilen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/groups/grant
                 * {
                 *     group_name: "[GROUP_NAME]",
                 *     permission: "[PERMISSION]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|permissionNotFound|groupNotFound|groupAlreadyHasPermission]"
                 * }
                 */
                post("/grant", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_GROUPS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String groupName = json.get("group_name").getAsString();
                    String permissionString = json.get("permission").getAsString();
                    Permission permission = EnumUtils.getEnum(Permission.class, permissionString);

                    if (permission == null) {
                        return "{\"status\":\"permissionNotFound\"}";
                    }

                    try {
                        userManager.grantPermissionToGroup(groupName, permission);
                    } catch (NotFoundException e) {
                        return "{\"status\":\"groupNotFound\"}";
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"groupAlreadyHasPermission\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
                /*
                 * Route zum Gruppenberechtigung entziehen
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /admin/groups/revoke
                 * {
                 *     group_name: "[GROUP_NAME]",
                 *     permission: "[PERMISSION]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|permissionNotFound|groupNotFound|groupDoesntHavePermission]"
                 * }
                 */
                post("/revoke", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_GROUPS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String groupName = json.get("group_name").getAsString();
                    String permissionString = json.get("permission").getAsString();
                    Permission permission = EnumUtils.getEnum(Permission.class, permissionString);

                    if (permission == null) {
                        return "{\"status\":\"permissionNotFound\"}";
                    }

                    try {
                        userManager.revokePermissionFromGroup(groupName, permission);
                    } catch (NotFoundException e) {
                        return "{\"status\":\"groupNotFound\"}";
                    } catch (AlreadyInTargetStateException e) {
                        return "{\"status\":\"groupDoesntHavePermission\"}";
                    }

                    return "{\"status\":\"success\"}";
                });
            });

            /* Path zum Protokolle einlesen */
            path("/sessions", () -> {
                get("", (request, response) -> {
                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_SESSIONS)) {
                            // TODO: Not sure how to best handle this case
                            return makeErrorPage(response, 403, "Forbidden", "User does not have permissions to edit sessions");
                        }
                    } catch (UserSessionExpiredException e) {
                        response.redirect("/login?redirect=/admin/sessions");
                        return null;
                    }
                    return new ModelAndView(Map.of("header", headerHtml), "sitzungen/admin.ftl");
                }, engine);
                get("/status", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_SESSIONS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    if (!SessionParserThread.isReady()) {
                        return "{\"status\":\"notReady\"}";
                    }

                    // TODO: This would be much better via proper JSON libraries.
                    double totalProgress = SessionParserThread.getTotalProgress();
                    if (SessionParserThread.isRunning()) {
                        return "{\"status\":\"running\",\"total_progress\":" + totalProgress + ",\"detailed_progress\":\"" + SessionParserThread.getFormattedProtocolProgress() + "\"}";
                    } else {
                        return "{\"status\":\"notRunning\",\"total_progress\":" + totalProgress + "}";
                    }
                });
                get("/start", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_SESSIONS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    try {
                        SessionParserThread.start();
                        return "{\"status\":\"success\"}";
                    } catch (Exception e) {
                        return "{\"status\":\"unknownFailure\"}";
                    }
                });
                get("/stop", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_SESSIONS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    try {
                        SessionParserThread.stop();
                        return "{\"status\":\"success\"}";
                    } catch (Exception e) {
                        return "{\"status\":\"unknownFailure\"}";
                    }
                });
            });
        });
    }

    /**
     * Fügt die Routen für die Abgeordneten-Übersicht etc. hinzu
     *
     * @param engine Template-Engine
     * @author Stud
     */
    private static void addAbgeordneterRoutes(TemplateEngine engine) {
        /*
         * Route für die Auflistung aller Abgeordneten
         */
        Spark.get("/abgeordneteListe", (req, res) -> {

            List<Bson> query12 = new ArrayList<>(List.of(
                    Aggregates.project(Projections.fields(
                            Projections.include("_id"),
                            Projections.include("vorname"),
                            Projections.include("nachname"))))
            );

            AggregateIterable<Document> abgeordneteDB = mongoDBHandler.aggregate(query12, "abgeordnete");

            List<List<String>> abgeordnete = new ArrayList<>();

            for (Document d : abgeordneteDB) {
                List<String> a = new ArrayList<>();
                a.add(d.getString("_id"));
                a.add(d.getString("vorname"));
                a.add(d.getString("nachname"));
                abgeordnete.add(a);
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("abgeordnete", abgeordnete);
            attributes.put("anzahlErgebnisse", abgeordnete.size());
            attributes.put("header", headerHtml);

            return new ModelAndView(attributes, "abgeordneter/alleAbgeordnete.ftl");
        }, engine);
        /*
         * Path zu allen Abgeordneten
         */

        /*
         * Path zu Abgeordneten
         */
        path("abgeordneter", () -> {
            /*
             * Web-Route für die Abgeordneten-Übersicht.
             * Methode: GET
             * Parameter:
             *     id (String, required) - ID des Abgeordneten
             * Antwort: Html-Website.
             */
            get("", (request, response) -> {
                String id = request.queryParams("id");
                if (id == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
                }

                Abgeordneter abgeordneter = mongoDBHandler.getAbgeordneterByID(id);
                if (abgeordneter == null) {
                    return makeErrorPage(response, 404, "Not Found", "Der Abgeordnete mit der ID \"" + id + "\" konnte nicht gefunden werden.");
                }

                String fotoAbgeordneter = mongoDBHandler.getImage(id);

                return new ModelAndView(Map.of("abgeordneter", abgeordneter, "foto", fotoAbgeordneter, "header", headerHtml), "abgeordneter/main.ftl");
            }, engine);
            get("/search", (request, response) -> {
                String vorname = request.queryParams("vorname");
                String nachname = request.queryParams("nachname");

                if (vorname == null || nachname == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"vorname\" oder \"nachname\" wurde nicht angegeben.");
                }

                List<Abgeordneter> abgeordnete = new ArrayList<>();
                mongoDBHandler.getMongoDatabase().getCollection("abgeordnete")
                        .find(Filters.and(Filters.regex("vorname", vorname, "i"), Filters.regex("nachname", nachname, "i")))
                        .map(AbgeordneterImpl::new)
                        .forEach((Consumer<? super Abgeordneter>) abgeordnete::add);

                String queryString;
                if (!vorname.isEmpty() || !nachname.isEmpty()) {
                    queryString = vorname + " " + nachname;
                } else {
                    queryString = "alle Abgeordneten";
                }

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("abgeordnete", abgeordnete);
                attributes.put("searchQuery", queryString);
                attributes.put("header", headerHtml);
                return new ModelAndView(attributes, "abgeordneter/suche.ftl");
            }, engine);
            /*
             * Path für Abgeordnetenerstellung
             */
            path("/create", () -> {
                /*
                 * Web-Route für Abgeordnetenerstellung.
                 * Methode: GET
                 * Antwort: Html-Website.
                 *          Alternativ: Redirect, falls Nutzer nicht eingeloggt.
                 *          Alternativ: Fehler-Seite, falls Nutzer eingeloggt aber ohne Berechtigungen.
                 */
                get("", (request, response) -> {
                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_ABGEORDNETE)) {
                            // TODO: Not sure how to best handle this case
                            return makeErrorPage(response, 403, "Forbidden", "User does not have permissions to manage speakers");
                        }
                    } catch (UserSessionExpiredException e) {
                        response.redirect("/login?redirect=/abgeordneter/create");
                        return null;
                    }

                    return new ModelAndView(Map.of("header", headerHtml), "abgeordneter/create.ftl");
                }, engine);
                /*
                 * Route zum Erstellen eines Abgeordneten
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /abgeordneter/create
                 * {
                 *     id: "[ID]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|abgeordneterIdNotProvided|abgeordneterAlreadyExists]"
                 * }
                 */
                post("", (request, response) -> {
                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_ABGEORDNETE)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String abgeordneterID = json.get("id").getAsString();

                    if (abgeordneterID == null) {
                        return "{\"status\":\"abgeordneterIdNotProvided\"}";
                    }

                    if (mongoDBHandler.getAbgeordneterByID(abgeordneterID) != null) {
                        return "{\"status\":\"abgeordneterAlreadyExists\"}";
                    }

                    Abgeordneter abgeordneter = new AbgeordneterImpl(abgeordneterID);

                    AbgeordneterDB abgeordneterDB = new AbgeordneterDBImpl(mongoDBHandler);
                    abgeordneterDB.insertAbgeordnetenDB(Set.of(abgeordneter));

                    return "{\"status\":\"success\"}";
                });
            });
            path("/edit", () -> {
                /*
                 * Web-Route für Abgeordneten-Bearbeitung.
                 * Methode: GET
                 * Antwort: Html-Website.
                 *          Alternativ: Redirect, falls Nutzer nicht eingeloggt.
                 *          Alternativ: Fehler-Seite, falls Nutzer eingeloggt aber ohne Berechtigungen.
                 */
                get("", (request, response) -> {

                    String id = request.queryParams("id");
                    if (id == null) {
                        return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
                    }

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.EDIT_ABGEORDNETE)) {
                            // TODO: Not sure how to best handle this case
                            return makeErrorPage(response, 403, "Forbidden", "User does not have permissions to manage speakers");
                        }
                    } catch (UserSessionExpiredException e) {
                        response.redirect("/login?redirect=/abgeordneter/edit?id=" + id);
                        return null;
                    }

                    Abgeordneter abgeordneter = mongoDBHandler.getAbgeordneterByID(id);
                    if (abgeordneter == null) {
                        return makeErrorPage(response, 404, "Not Found", "Der Abgeordnete mit der ID \"" + id + "\" konnte nicht gefunden werden.");
                    }

                    return new ModelAndView(Map.of("abgeordneter", abgeordneter, "abgeordneterGeburtsdatumString", new SimpleDateFormat("yyyy-MM-dd").format(abgeordneter.getGeburtsdatum()), "header", headerHtml), "abgeordneter/edit.ftl");
                }, engine);
                /*
                 * Route zum Bearbeiten der Stammdaten eines Abgeordneten
                 * Methode: POST
                 *
                 * Anfrage:
                 * POST /abgeordneter/edit/stammdaten
                 * {
                 *     abgeordneter: "[ID]",
                 *     nachname: "[NACHNAME]",
                 *     vorname: "[VORNAME]",
                 *     namenspraefix: "[NAMENSPRAEFIX]",
                 *     adelssuffix: "[ADELSSUFFIX]",
                 *     anrede: "[ANREDE]",
                 *     geburtsdatum: "[YYYY-MM-DD]",
                 *     geburtsort: "[GEBURTSORT]",
                 *     geschlecht: "[GESCHLECHT]",
                 *     religion: "[RELIGION]",
                 *     beruf: "[BERUF]",
                 *     vita: "[VITA]",
                 *     partei: "[PARTEI]",
                 *     fraktion: "[FRAKTION]"
                 * }
                 *
                 * Antwort:
                 * {
                 *     status: "[success|userNotLoggedIn|userAuthorizationFailed|abgeordneterIdNotProvided|abgeordneterNotFound]"
                 * }
                 */
                post("/stammdaten", (request, response) -> {
                    response.type("application/json");

                    String token = request.cookie("session_token");
                    try {
                        if (!userManager.isUserPermitted(token, Permission.MANAGE_GROUPS)) {
                            return "{\"status\":\"userAuthorizationFailed\"}";
                        }
                    } catch (UserSessionExpiredException e) {
                        return "{\"status\":\"userNotLoggedIn\"}";
                    }

                    JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
                    String abgeordneterID = json.get("abgeordneter").getAsString();

                    if (abgeordneterID == null) {
                        return "{\"status\":\"abgeordneterIdNotProvided\"}";
                    }

                    Abgeordneter abgeordneter = mongoDBHandler.getAbgeordneterByID(abgeordneterID);
                    if (abgeordneter == null) {
                        return "{\"status\":\"abgeordneterNotFound\"}";
                    }

                    abgeordneter.setNachname(json.get("nachname").getAsString());
                    abgeordneter.setVorname(json.get("vorname").getAsString());
                    abgeordneter.setNamenspraefix(json.get("namenspraefix").getAsString());
                    abgeordneter.setAdelssuffix(json.get("adelssuffix").getAsString());
                    abgeordneter.setAnrede(json.get("anrede").getAsString());
                    abgeordneter.setGeburtsdatum(Date.valueOf(json.get("geburtsdatum").getAsString()));
                    abgeordneter.setGeburtsort(json.get("geburtsort").getAsString());
                    abgeordneter.setGeschlecht(json.get("geschlecht").getAsString());
                    abgeordneter.setReligion(json.get("religion").getAsString());
                    abgeordneter.setBeruf(json.get("beruf").getAsString());
                    abgeordneter.setVita(json.get("vita").getAsString());
                    abgeordneter.setPartei(json.get("partei").getAsString());
                    abgeordneter.setFraktion(json.get("fraktion").getAsString());

                    AbgeordneterDB abgeordneterDB = new AbgeordneterDBImpl(mongoDBHandler);
                    abgeordneterDB.updateAbgeordneterDB(abgeordneter);

                    return "{\"status\":\"success\"}";
                });
            });
        });
    }


    /**
     * Fügt die Routen für Reden hinzu
     *
     * @param engine Template-Engine
     * @author Stud
     * @author Stud
     */
    private static void addRedenRoutes(TemplateEngine engine) {
        /*
         * Route für die Auflistung aller Reden
         */
        Spark.get("/redenListe", (req, res) -> {

            List<Bson> query13 = new ArrayList<>(List.of(
                    Aggregates.project(Projections.fields(
                            Projections.include("_id"),
                            Projections.include("rednerID"),
                            Projections.include("datum"))))
            );

            AggregateIterable<Document> redenDB = mongoDBHandler.aggregate(query13, "reden");

            List<List<String>> reden = new ArrayList<>();

            for (Document d : redenDB) {
                List<String> rede = new ArrayList<>();
                rede.add(d.getString("_id"));
                rede.add(d.getString("rednerID"));
                rede.add(d.getDate("datum").toString().substring(0, 10) + ", "
                        + d.getDate("datum").toString().substring(d.getDate("datum").toString().length() - 5));
                reden.add(rede);
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("reden", reden);
            attributes.put("anzahlErgebnisse", reden.size());
            attributes.put("header", headerHtml);

            return new ModelAndView(attributes, "rede/alleReden.ftl");
        }, engine);
        /*
         * Path zu Reden
         */
        path("rede", () -> {
            /*
             * Web-Route für eine Rede.
             * Methode: GET
             * Parameter:
             *     id (String, required) - ID der Rede
             * Antwort: Html-Website.
             */
            get("", (request, response) -> {
                String id = request.queryParams("id");
                if (id == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
                }

                Rede rede = mongoDBHandler.getRedeByID(id);
                if (rede == null) {
                    return makeErrorPage(response, 404, "Not Found", "Die Rede mit der ID \"" + id + "\" konnte nicht gefunden werden.");
                }

                String id_redner = rede.getRednerID();
                Abgeordneter abgeordneter = mongoDBHandler.getAbgeordneterByID(id_redner);
                String fotoAbgeordneter = mongoDBHandler.getImage(id_redner);
                if (fotoAbgeordneter == null){
                    fotoAbgeordneter = "";
                }
                RedeNlpDBHelperInterface helper = new RedeNlpDBHelper();

                return new ModelAndView(Map.of("rede", rede, "redner", abgeordneter, "helper", helper, "foto", fotoAbgeordneter, "header", headerHtml), "rede/main.ftl");

            }, engine);
            /*
             * Path für Redenerstellung
             */
            path("/create", () -> {
                // TODO Stud: Erstellung von Reden
            });
            /*
             * Path für Redenbearbeitung
             */
            path("/edit", () -> {
                // TODO Stud: Bearbeitung von Reden
            });
        });

        /*
         * Route für die Visualisierung aller Reden innerhalb eines bestimmten Zeitraumes
         */
        get("reden", (request, response) -> {
            /*
             * Web-Route für Darstellung aller Reden innerhalb eines bestimmten Zeitraumes
             * Methode: GET
             * Parameter:
             *     von (String, required) - Start des Zeitraumes im Format YYYY-MM-DD
             *     bis (String, required) - Ende des Zeitraumes im Format YYYY-MM-DD
             * Antwort: Html-Website.
             */

            Date filterDatumVon = null;
            Date filterDatumBis = null;

            try {
                filterDatumVon = Date.valueOf(request.queryParams("von"));
            } catch (IllegalArgumentException ignored) {
            }
            try {
                filterDatumBis = Date.valueOf(request.queryParams("bis"));
            } catch (IllegalArgumentException ignored) {
            }

            String timespan;
            if (filterDatumVon != null && filterDatumBis != null) {
                timespan = "Von " + filterDatumVon + " bis " + filterDatumBis;
            } else if (filterDatumVon != null) {
                timespan = "Ab " + filterDatumVon;
            } else if (filterDatumBis != null) {
                timespan = "Bis " + filterDatumBis;
            } else {
                timespan = "Immer";
            }

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("zeitraum", timespan);

            // Abbildung der verschiedenen Arten von POS auf deren Anzahl - abhängig vom Zeitraum
            List<Bson> query1 = new ArrayList<>(Arrays.asList(
                    Aggregates.unwind("$redeElemente"),
                    Aggregates.unwind("$redeElemente.nlp.pos"),
                    Aggregates.group("$redeElemente.nlp.pos.posValue", Accumulators.sum("anzahl", 1)),
                    Aggregates.sort(Sorts.descending("anzahl"))));
            if (filterDatumVon != null) {
                query1.add(0, Aggregates.match(Filters.gte("datum", filterDatumVon)));
            }
            if (filterDatumBis != null) {
                query1.add(0, Aggregates.match(Filters.lte("datum", filterDatumBis)));
            }

            AggregateIterable<Document> partsOfSpeech = mongoDBHandler.aggregate(query1, "reden");

            List<String> selectedPOSValues = new ArrayList<>();
            selectedPOSValues.add("NN");
            selectedPOSValues.add("ART");
            selectedPOSValues.add("NE");
            selectedPOSValues.add("VVFIN");
            selectedPOSValues.add("ADV");
            selectedPOSValues.add("PPER");
            selectedPOSValues.add("ADJA");
            selectedPOSValues.add("KON");
            selectedPOSValues.add("ADJD");
            selectedPOSValues.add("APPRART");
            selectedPOSValues.add("APPR");
            selectedPOSValues.add("CARD");

            partsOfSpeech.forEach((Block<? super Document>) obj -> {
                if (selectedPOSValues.contains(obj.getString("_id"))) {
                    attributes.put(obj.getString("_id"), obj.getInteger("anzahl").toString());
                }
            });

            // Sentiment der Reden - abhängig vom Zeitraum
            List<Bson> query2 = new ArrayList<>(List.of(
                    Aggregates.group("",
                            Accumulators.avg("avgPos", "$positiv"),
                            Accumulators.avg("avgNeu", "$neutral"),
                            Accumulators.avg("avgNeg", "$negativ")
                    )));
            if (filterDatumVon != null) {
                query2.add(0, Aggregates.match(Filters.gte("datum", filterDatumVon)));
            }
            if (filterDatumBis != null) {
                query2.add(0, Aggregates.match(Filters.lte("datum", filterDatumBis)));
            }


            AggregateIterable<Document> sentiment = mongoDBHandler.aggregate(query2, "reden");
            attributes.put("avgPos", Objects.requireNonNull(sentiment.first()).getDouble("avgPos").toString());
            attributes.put("avgNeu", Objects.requireNonNull(sentiment.first()).getDouble("avgNeu").toString());
            attributes.put("avgNeg", Objects.requireNonNull(sentiment.first()).getDouble("avgNeg").toString());

            // Abbildung der einzelnen Redner auf Anzahl der Reden - abhängig vom Zeitraum
            List<Bson> query3 = new ArrayList<>(List.of(
                    Aggregates.unwind("$reden_ids"),
                    Aggregates.lookup("reden", "reden_ids", "_id", "details"),
                    Aggregates.group("$_id", Accumulators.sum("anzahlReden", 1)),
                    Aggregates.lookup("abgeordnete", "_id", "_id", "abgeordneter"),
                    Aggregates.project(Projections.fields(
                            Projections.include("_id"),
                            Projections.include("abgeordneter.vorname"),
                            Projections.include("abgeordneter.nachname"),
                            Projections.include("anzahlReden"))),
                    Aggregates.sort(Sorts.descending("anzahlReden"))));
            if (filterDatumVon != null) {
                query3.add(2, Aggregates.match(Filters.gte("details.datum", filterDatumVon)));
            }
            if (filterDatumBis != null) {
                query3.add(2, Aggregates.match(Filters.lte("details.datum", filterDatumBis)));
            }


            AggregateIterable<Document> abgeordnete = mongoDBHandler.aggregate(query3, "abgeordnete");

            ArrayList<String> anzahlRedenX = new ArrayList<>();
            ArrayList<String> anzahlRedenY = new ArrayList<>();
            int gesamt = 0;

            DecimalFormat decimalFormat = new DecimalFormat("0000");

            for (Document doc : abgeordnete) {

                anzahlRedenX.add(doc.getList("abgeordneter", Document.class).get(0).get("vorname") + " " +
                        doc.getList("abgeordneter", Document.class).get(0).get("nachname"));
                anzahlRedenY.add(decimalFormat.format(doc.getInteger("anzahlReden")));
                gesamt += doc.getInteger("anzahlReden");
            }

            attributes.put("anzahlRedenX", String.join("#", anzahlRedenX));
            attributes.put("anzahlRedenY", anzahlRedenY.stream().map(Object::toString).collect(Collectors.joining("#")));
            attributes.put("gesamt", gesamt);
            attributes.put("header", headerHtml);


            /* Boxplot */
            List<Bson> aggregationPipeline = new ArrayList<>(List.of(
                    Aggregates.lookup("abgeordnete", "rednerID", "_id", "redner_info"),
                    Aggregates.unwind("$redner_info"),
                    Aggregates.project(
                            Projections.fields(
                                    new Document("fraktion", "$redner_info.fraktion"),
                                    new Document("sentiment", "$sentiment"),
                                    Projections.exclude("_id")
                            )
                    ),
                    Aggregates.group("$fraktion", Accumulators.push("sentiments", "$sentiment")),
                    Aggregates.project(
                            Projections.fields(
                                    new Document("fraktion", "$_id"),
                                    Projections.include("sentiments"),
                                    Projections.exclude("_id")
                            )
                    )
            ));

            if (filterDatumVon != null) {
                aggregationPipeline.add(0, Aggregates.match(Filters.gte("datum", filterDatumVon)));
            }
            if (filterDatumBis != null) {
                aggregationPipeline.add(0, Aggregates.match(Filters.lte("datum", filterDatumBis)));
            }

            List<String> sentimentJsons = new ArrayList<>();
            mongoDBHandler.getMongoDatabase()
                    .getCollection("reden")
                    .aggregate(aggregationPipeline)
                    .forEach((Consumer<? super Document>) document -> {
                        String fraktionKurz = SparkHelper.shortenFraktion(document.getString("fraktion"));
                        document.put("fraktion", fraktionKurz);
                        String color = SparkHelper.colorFraktion(fraktionKurz);
                        document.put("color", color);
                        sentimentJsons.add(document.toJson());
                    });
            attributes.put("sentimentInfos", sentimentJsons);

            return new ModelAndView(attributes, "reden.ftl");
        }, engine);
    }

    private static String shortenFraktion(String fraktion) {
        switch (fraktion) {
            case "Fraktion der Freien Demokratischen Partei":
                return "FDP";
            case "Alternative für Deutschland":
                return "AfD";
            case "Fraktion DIE LINKE.":
                return "LINKE";
            case "Fraktion der Sozialdemokratischen Partei Deutschlands":
                return "SPD";
            case "Fraktion der Christlich Demokratischen Union/Christlich - Sozialen Union":
                return "CDU/CSU";
            case "Fraktion BÜNDNIS 90/DIE GRÜNEN":
                return "GRÜNE";
            default:
                return fraktion;
        }
    }

    private static String colorFraktion(String fraktion) {
        switch (fraktion) {
            case "FDP":
                return "#ffed00";
            case "AfD":
                return "#734b17";
            case "LINKE":
                return "#8100A1";
            case "SPD":
                return "#E3000F";
            case "CDU/CSU":
                return "#000000";
            case "GRÜNE":
                return "#46962b";
            default:
                return "#ffffff";
        }
    }

    /**
     * Fügt die Routen für Sitzungen hinzu
     *
     * @param engine Template-Engine
     * @author Stud
     * @author Stud
     */
    private static void addSitzungRoutes(TemplateEngine engine) {
        /*
         * Web-Route für die Sitzungenübersicht.
         * Methode: GET
         * Antwort: Html-Website.
         */
        get("sitzungen", (request, response) -> {

            Set<Document> sitzungenSet = mongoDBHandler.getAllDocuments("sitzungen");
            List<Document> sitzungsListe = new ArrayList<>(sitzungenSet);

            sitzungsListe.sort((doc1, doc2) -> {
                java.util.Date date1 = doc1.getDate("date");
                java.util.Date date2 = doc2.getDate("date");

                return date1.compareTo(date2);
            });

            List<List<String>> sitzungList = new ArrayList<>();

            for (Document sitzung : sitzungsListe) {
                List<String> eineSitzung = new ArrayList<>();
                Sitzung sitzung1 = new SitzungImpl(sitzung);
                eineSitzung.add(sitzung1.getID());
                eineSitzung.add(String.valueOf(sitzung1.getSitzungsnummer()));
                sitzungList.add(eineSitzung);
            }

            return new ModelAndView(Map.of("sitzungen", sitzungList, "header", headerHtml), "sitzungen/main.ftl");

            // TODO: Optionale Datums-Queryparameter?

            // TODO: Route für Visualisierung der Sitzungenübersicht
            //  This code currently throws 500.
        }, engine);
        /*
         * Path zu Sitzungen
         */
        path("sitzung", () -> {
            /*
             * Web-Route für eine Rede.
             * Methode: GET
             * Parameter:
             *     id (String, required) - ID der Rede
             * Antwort: Html-Website.
             */
            get("", (request, response) -> {
                String id = request.queryParams("id");
                if (id == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
                }

                Sitzung sitzung = mongoDBHandler.getSitzungByID(id);
                if (sitzung == null) {
                    return makeErrorPage(response, 404, "Not Found", "Die Sitzung mit der ID \"" + id + "\" konnte nicht gefunden werden.");
                }

                List<Tagesordnungspunkt> tagesordnungspunkte = sitzung.getTagesordnungspunkte();

                return new ModelAndView(Map.of("sitzung", sitzung, "tagesordnungspunkte", tagesordnungspunkte, "id", id, "header", headerHtml), "sitzungen/protokoll.ftl");


                // TODO: Route für Visualisierung einer Sitzung
                //  This code currently throws 500.
            }, engine);
            /*
             * Path für Sitzungserstellung
             */
            path("/create", () -> {
                // TODO Stud: Erstellung von Sitzungen
            });
            /*
             * Path für Sitzungsbearbeitung
             */
            path("/edit", () -> {
                // TODO Stud: Bearbeitung von Sitzungen
            });
            get("/vorschau", (request, response) -> {
                String id = request.queryParams("id");
                if (id == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
                }

                ProtokolToLaTeXImpl.createLatexUndPdf(id);

                return new ModelAndView(Map.of("header", headerHtml, "sitzung_id", id), "sitzungen/latexvorschau.ftl");
            }, engine);
            get("/:id.pdf", (request, response) -> {
                String id = request.params(":id.pdf");
                if (id == null || !id.endsWith(".pdf")) {
                    return makeErrorPage(response, 400, "Bad Request", "ID wurde nicht angegeben oder Link fehlerhaft.");
                }
                id = id.substring(0, id.length() - 4);
                if (!id.matches("WP[0-9]*-[0-9]*")) {
                    return makeErrorPage(response, 400, "Bad Request", "Link ist fehlerhaft.");
                }

                File file = new File("tmp/latex/" + id + ".pdf");
                if (!file.exists()) {
                    return makeErrorPage(response, 400, "Bad Request", "Die Sitzung mit der ID " + id + " wurde noch nicht exportiert.");
                }

                byte[] data = Files.readAllBytes(file.toPath());
                OutputStream rawDataStream = response.raw().getOutputStream();
                rawDataStream.write(data);
                rawDataStream.flush();
                rawDataStream.close();
                response.type("application/pdf");
                return null;
            }, engine);
        });
    }

    private static void addVolltextsuche(TemplateEngine engine) {
        /*
         * Path zur Volltextsuche
         */
        path("volltextsuche", () -> {
            /*
             * Web-Route für Volltextsuche.
             * Methode: GET
             * Parameter:
             *     suchtext (String, required) - Text nach dem gesucht werden soll
             * Antwort: Html-Website.
             */
            get("", (request, response) -> {
                String suchtext = request.queryParams("suchtext");
                if (suchtext == null) {
                    return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"suchtext\" wurde nicht angegeben.");
                }

                // This is quite hacky, but I'm unable to get any Java method that requires arguments to work in Spark
                List<Rede> redenListe = mongoDBHandler.getDocumentsByRegex("volltext", suchtext, "reden")
                        .stream()
                        .map(RedeImpl::new)
                        .collect(Collectors.toList());
                Map<String, Abgeordneter> redner = new HashMap<>();
                redenListe.stream()
                        .map(Rede::getRednerID)
                        // There has to be a better way to ensure elements aren't in there multiple times
                        .collect(Collectors.toSet())
                        .stream()
                        .map(mongoDBHandler::getAbgeordneterByID)
                        .forEach(abgeordneter -> redner.put(abgeordneter.getID(), abgeordneter));
                List<Pair<Rede, Abgeordneter>> redenTuple = redenListe.stream().map(
                        rede -> new Pair<>(rede, redner.get(rede.getRednerID()))
                ).collect(Collectors.toList());

                return new ModelAndView(Map.of("redenTuple", redenTuple, "suchtext", suchtext, "header", headerHtml), "rede/volltextsuche.ftl");
            }, engine);
        });
    }
}
