package org.itstep;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.itstep.model.User;
import org.itstep.rsql.CustomRsqlVisitor;
import org.itstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@SpringBootApplication
public class CheckRsql {
    @Autowired
    private static UserService userService;

    public static void main(String[] args) {
        Node rootNode = new RSQLParser().parse("name==john;surname==doe");
        Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
        Pageable pageable = PageRequest.of(0, 10);
        List<User> results = userService.findAll(spec, pageable).getContent();

        results.stream().forEach(System.out::println);
    }

}
