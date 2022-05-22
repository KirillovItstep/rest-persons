package org.itstep.service;

import org.itstep.model.User;
import org.itstep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    public class UserService {

        @Autowired
        private UserRepository userRepository;

        public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

        public Page<User> findAll(Pageable pageable) {
            return (Page<User>) userRepository.findAll(pageable);
        }

    public Page<User> findAll(Specification specification, Pageable pageable) {
        return (Page<User>) userRepository.findAll(specification, pageable);
    }

        public User findById(Long id) {
            return userRepository.findById(id).orElse(null);
        }
    }

