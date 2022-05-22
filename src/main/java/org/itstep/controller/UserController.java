package org.itstep.controller;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.itstep.model.User;
import org.itstep.repository.UserRepository;
import org.itstep.rsql.CustomRsqlVisitor;
import org.itstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
    public class UserController {

        @Autowired
        private UserService userService;

        @GetMapping(value="/users", params={})
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

    //Только пагинация
    @GetMapping(value="/users", params={"page", "size"})
    public Page<User> paginationUsers(@RequestParam("page") int page,
                                      @RequestParam("size") int size) throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findAll(pageable);
    }

    /*
    //Пагинация с сортировкой по одному из полей
    @GetMapping("/users")
    public Page<User> paginationUsers(@RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @RequestParam("sort") String sort) throws IOException {
        //Сортировка по убыванию: проверяем наличие в пути /users/sort=id,desc наличие или отсутствие строки :desc
        boolean sortDesc = sort.contains(",desc");
        Sort sortable;
        if (sortDesc)
             sortable = Sort.by(Sort.Direction.DESC,sort.replace(",desc",""));
        else
             sortable = Sort.by(Sort.Direction.ASC,sort);
        Pageable pageable = PageRequest.of(page, size, sortable);
        return userService.findAll(pageable);
    }
     */

    //Пагинация с сортировкой по нескольким полям из строки типа ?sort=name,desc&sort=surname,asc
    //Вспомогательная функция для получение порядка сортировки
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    //http://localhost:8080/users?page=0&size=10&sort=name,desc&sort=surname,desc
    @GetMapping(value="/users", params={"page", "size","sort"})
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        try {
            List<Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                // will sort more than 2 columns
                for (String sortOrder : sort) {
                    // sortOrder="column, direction"
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[column, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
            List<User> users = userService.findAll(pageable).getContent();
            //users.stream().forEach(System.out::println);
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Пагинация, сортировка и поиск
    /*
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name==Петр;surname==Агеев
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name!=Иван;surname==Иванов
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name<Антон
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name==В*;surname==И*
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name==*ви*
    http://localhost:8080/users?size=10&page=0&sort=surname,asc&search=name=in=(Юрий,Яков)
     */
    @GetMapping(value="/users", params={"page", "size", "sort", "search"})
    public ResponseEntity<List<User>> paginationUsers(@RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @RequestParam(defaultValue = "id,asc") String[] sort,
                                     @RequestParam(value = "search") String search) throws IOException {
        try {
            List<Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                // will sort more than 2 columns
                for (String sortOrder : sort) {
                    // sortOrder="column, direction"
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[column, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
            Node rootNode = new RSQLParser().parse(search);
            Specification<User> specification = rootNode.accept(new CustomRsqlVisitor<User>());
            List<User> users = userService.findAll(specification, pageable).getContent();
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


