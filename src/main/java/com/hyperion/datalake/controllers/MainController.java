package com.hyperion.datalake.controllers;

import com.hyperion.datalake.models.WorkItem;
import com.hyperion.datalake.services.DynamoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.hyperion.datalake.handlers.UserHandler.getAllFromUsersTest;

@ComponentScan(basePackages = {"com.aws.rest"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/items")
public class MainController {
    private final DynamoDBService dbService;

    @Autowired
    MainController(
            DynamoDBService dbService
    ) {
        this.dbService = dbService;
    }

    @GetMapping("/sql")
    public ResponseEntity<String> rootSql(@RequestParam(required = false) String name) throws SQLException {
        System.out.printf("hit root of with param: %s\n", name);
        getAllFromUsersTest();
        return new ResponseEntity<>("Success!", HttpStatus.OK);
    }

    @GetMapping("" )
    public List<WorkItem> getItems(@RequestParam(required=false) String archived) {
        Iterable<WorkItem> result;
        if (archived != null && archived.compareTo("false")==0)
            result = dbService.getOpenItems();
        else if (archived != null && archived.compareTo("true")==0)
            result = dbService.getClosedItems();
        else
            result = dbService.getAllItems();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toUnmodifiableList());
    }

    // Notice the : character which is used for custom methods. More information can be found here:
    // https://cloud.google.com/apis/design/custom_methods
    @PutMapping("{id}:archive")
    public String modUser(@PathVariable String id) {
        dbService.archiveItemEC(id);
        return id +" was archived";
    }

    @PostMapping("")
    public List<WorkItem> addItem(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String guide = payload.get("guide");
        String description = payload.get("description");
        String status = payload.get("status");

        WorkItem item = new WorkItem();
        String workId = UUID.randomUUID().toString();
        String date = LocalDateTime.now().toString();
        item.setId(workId);
        item.setGuide(guide);
        item.setDescription(description);
        item.setName(name);
        item.setDate(date);
        item.setStatus(status);
        item.setArchived(0);
        dbService.setItem(item);
        Iterable<WorkItem> result= dbService.getOpenItems();
        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toUnmodifiableList());
    }
}