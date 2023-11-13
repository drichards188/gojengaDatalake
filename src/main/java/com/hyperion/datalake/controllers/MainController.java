package com.hyperion.datalake.controllers;

import com.google.gson.Gson;
import com.hyperion.datalake.models.User;
import com.hyperion.datalake.models.WorkItem;
import com.hyperion.datalake.services.DynamoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.hyperion.datalake.handlers.UserHandler.*;

@ComponentScan(basePackages = {"com.aws.rest"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/user")
public class MainController {
    private final DynamoDBService dbService;

    @Autowired
    MainController(
            DynamoDBService dbService
    ) {
        this.dbService = dbService;
    }

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
    public boolean createUser(@RequestBody Map<String, String> payload) throws SQLException {
        String name = payload.get("name");
        String balance = payload.get("balance");
        System.out.printf("hit root of with param: %s\n", name);

        if (name != null && balance != null) {
            String formattedStatement = String.format("('%s', %s)", name, balance);
            boolean insertResult = createData("ledgerTest (name, balance)", formattedStatement);
            if (!insertResult) {
                return false;
            }
        }

        return name != null && balance != null;
    }

    @PutMapping("/sql")
    public boolean putSql(@RequestParam(required = true) String name, @RequestBody Map<String, String> payload) throws SQLException {
        String balance = payload.get("balance");

        if (name != null && balance != null) {
            String setStatement = String.format("balance = %s", balance);
            String whereStatement = String.format("name = '%s'", name);
            boolean updateResult = updateData("ledgerTest", setStatement, whereStatement);
            if (!updateResult) {
                return false;
            }
        }

        return name != null && balance != null;
    }

    @DeleteMapping("/sql")
    public boolean deleteSql(@RequestParam(required = true) String name) throws SQLException {
        if (name != null) {
            String whereStatement = String.format("name = '%s'", name);
            boolean deleteResult = deleteData("ledgerTest", whereStatement);
            if (!deleteResult) {
                return false;
            }
        }

        return name != null;
    }

//    @GetMapping("" )
//    public List<WorkItem> getItems(@RequestParam(required=false) String archived) {
//        Iterable<WorkItem> result;
//        if (archived != null && archived.compareTo("false")==0)
//            result = dbService.getOpenItems();
//        else if (archived != null && archived.compareTo("true")==0)
//            result = dbService.getClosedItems();
//        else
//            result = dbService.getAllItems();
//
//        return StreamSupport.stream(result.spliterator(), false)
//                .collect(Collectors.toUnmodifiableList());
//    }

    // Notice the : character which is used for custom methods. More information can be found here:
    // https://cloud.google.com/apis/design/custom_methods
    @PutMapping("{id}:archive")
    public String modUser(@PathVariable String id) {
        dbService.archiveItemEC(id);
        return id +" was archived";
    }

//    @PostMapping("")
//    public List<WorkItem> addItem(@RequestBody Map<String, String> payload) {
//        String name = payload.get("name");
//        String guide = payload.get("guide");
//        String description = payload.get("description");
//        String status = payload.get("status");
//
//        WorkItem item = new WorkItem();
//        String workId = UUID.randomUUID().toString();
//        String date = LocalDateTime.now().toString();
//        item.setId(workId);
//        item.setGuide(guide);
//        item.setDescription(description);
//        item.setName(name);
//        item.setDate(date);
//        item.setStatus(status);
//        item.setArchived(0);
//        dbService.setItem(item);
//        Iterable<WorkItem> result= dbService.getOpenItems();
//        return StreamSupport.stream(result.spliterator(), false)
//                .collect(Collectors.toUnmodifiableList());
//    }
}