package org.texttechnologylab.project.Parliament_Browser_09_2.core;

import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.UserManager;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.AlreadyInTargetStateException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Executable class to create a webmaster user.
 * <p>
 * A webmaster user has permissions for everything, and cannot be deleted.
 * Therefore, this is not something that should be done by an ordinary user,
 * only by a web admin, and as such is not exposed on the website.
 * However, you will be guided through the process in the command line
 * when running this class.
 * </p>
 *
 * @author Stud
 */
public class CreateWebmasterUser {

    public static void main(String[] args) throws AlreadyInTargetStateException, InterruptedException {
        MongoDBHandler handler;
        try {
            handler = new MongoDBHandlerImpl("Project_09_02.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Sleep to make sure MongoDB-Messages have gone through
        Thread.sleep(1000);

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWelcome! To create a webmaster user, follow the steps below.\n");
        System.out.println("1) Make sure the webserver is running");
        System.out.println("2) Go to the /login page.");
        /* The steps through the web console are required to make sure the prehashing
         * is always exactly the same as when logging in. */
        System.out.println("3) Open the web console.");
        System.out.println("4) Call the `prehashPassword(username, password)` function, using your desired username and password as arguments.");
        System.out.println("   Use the output of the functions for the following steps. Press Enter when you're ready.");

        scanner.nextLine();
        System.out.print("Enter the username: ");
        String username = scanner.nextLine();
        System.out.print("Enter the prehashed password from the web console: ");
        byte[] password = scanner.nextLine().getBytes();

        UserManager userManager = new UserManager(handler);
        userManager.createUser(username, password, true);

        System.out.println("The webmaster user \"" + username + "\" has been created successfully.");
    }
}
