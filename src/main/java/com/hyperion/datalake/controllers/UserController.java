package com.hyperion.datalake.controllers;

import com.hyperion.datalake.models.UserItem;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ComponentScan(basePackages = {"com.aws.rest"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {
    private final DynamoDBService dbService;

    @Autowired
    UserController(
            DynamoDBService dbService
    ) {
        this.dbService = dbService;
    }

    @GetMapping("")
    public ResponseEntity<String> getItems(@RequestParam(required = false) String archived) {
        System.out.println("hit root of user");
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }
}