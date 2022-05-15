package org.itstep.controller;

import org.itstep.model.User;
import org.itstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
    public class UserController {

        @Autowired
        private UserService userService;

        @GetMapping("/users/all")
        public List<User> getUsers(){
            var users = (List<User>) userService.findAll();
            return users;
        }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getBookById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
    }

    //Pageable
    @GetMapping("/users")
    public Page<User> paginationBook(@RequestParam("limit") int limit,
                                     @RequestParam("page") int page) throws IOException {
        Pageable firstPageWith10Elements = PageRequest.of(page, limit);
        return userService.findAll(firstPageWith10Elements);
    }




}

