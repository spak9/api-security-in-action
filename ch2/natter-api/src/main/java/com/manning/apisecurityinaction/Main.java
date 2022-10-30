package com.manning.apisecurityinaction;

import java.nio.file.*;

import com.manning.apisecurityinaction.SpaceController.*;
import org.dalesbred.Database;
import org.dalesbred.result.EmptyResultException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.*;
import spark.*;

import static spark.Spark.*;

public class Main {

    public static void main(String... args) throws Exception {
        // 1. Create privileged connection with default user to create database/tables
        var datasource = JdbcConnectionPool.create(
            "jdbc:h2:mem:natter", "natter", "password");
        var database = Database.forDataSource(datasource);
        createTables(database);

        // 2. Switch to our "natter_api_user" with fewer permissions (SELECT, INSERT)
        System.out.println("[+] Switching to 'natter_api_user'.");
        datasource = JdbcConnectionPool.create(
            "jdbc:h2:mem:natter", "natter_api_user", "password");
        database = Database.forDataSource(datasource);


        // Create our Controllers
        var spaceController = new SpaceController(database);


        // Prepare our routes
        post("/spaces", spaceController::createSpace);


        // after filters
        after((request, response) -> {
            response.type("application/json");
        });

        internalServerError(new JSONObject().put("error", "internal server error").toString());

        notFound(new JSONObject().put("error", "not found").toString());

    }

    private static void createTables(Database database) throws Exception {
        System.out.println("[+] createTables");
        var path = Paths.get(Main.class.getResource("/schema.sql").toURI());
        database.update(Files.readString(path));
    }
}