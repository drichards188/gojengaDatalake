package com.hyperion.datalake.controllers;

import com.hyperion.datalake.models.UserItem;
import com.hyperion.datalake.services.DynamoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.hyperion.datalake.handlers.UserHandler.*;
import static com.hyperion.datalake.handlers.UserHandler.deleteData;

@ComponentScan(basePackages = {"com.aws.rest"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("")
    public ResponseEntity<HashMap<String, String>> getUser(String name) throws SQLException {
        System.out.printf("hit root of with param: %s\n", name);
        String formattedSelector = String.format("name = '%s'", name);
        HashMap<String, HashMap<String, String>> queryResult = readData("*", "usersTest", formattedSelector);

        if (queryResult.isEmpty()) {
            HashMap<String, String> error = new HashMap<>();
            error.put("error", "user not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        HashMap<String, String> user = queryResult.get(name);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("")
    public HashMap<String, String> createUser(@RequestBody Map<String, String> payload) throws SQLException {
        String name = payload.get("name");
        String balance = payload.get("balance");
        System.out.printf("hit root of with param: %s\n", name);
        HashMap<String, String> response = new HashMap<>();

        try {
            if (name != null && balance != null) {
                String formattedStatement = String.format("('%s', %s)", name, balance);
                HashMap<String, String> insertResult = createData("ledgerTest (name, balance)", formattedStatement);
                if (insertResult.containsKey("error")) {
                    response.put("error", insertResult.get("error"));
                    return response;
                } else {
                    response.put("success", "user created");
                    return response;
                }

            }
        }
        catch (Exception e) {
            response.put("error", e.getMessage());
            System.out.println(e.getMessage());
            return response;
        }
        response.put("error", "user not found");
        return response;
    }

    @PutMapping("")
    public HashMap<String, String> putUser(String name, @RequestBody Map<String, String> payload) throws SQLException {
        String balance = payload.get("balance");
        HashMap<String, String> response = new HashMap<>();

        try {
            if (name != null && balance != null) {
                String setStatement = String.format("balance = %s", balance);
                String whereStatement = String.format("name = '%s'", name);
                HashMap<String, String> updateResult = updateData("ledgerTest", setStatement, whereStatement);
                if (updateResult.containsKey("error")) {
                    response.put("error", updateResult.get("error"));
                    return response;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.put("error", e.getMessage());
            return response;
        }

        response.put("success", "user updated");
        return response;
    }

    @DeleteMapping("")
    public HashMap<String, String> deleteUser(@RequestParam(required = true) String name) throws SQLException {
        HashMap<String, String> response = new HashMap<>();
        if (name != null) {
            String whereStatement = String.format("name = '%s'", name);
            HashMap<String, String> deleteResult = deleteData("usersTest", whereStatement);
            if (deleteResult.containsKey("error")) {
                response.put("error", deleteResult.get("error"));
                return response;
            }
        }

        response.put("success", "user deleted");
        return response;
    }
}