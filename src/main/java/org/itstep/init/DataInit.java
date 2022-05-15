package org.itstep.init;

import org.itstep.model.User;
import org.itstep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*
        User u1 = new User();
        u1.setName("name");
        u1.setSurname("surname");
        userRepository.save(u1);

         */
        }
}
