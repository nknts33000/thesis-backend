package com.example.platform.data;

import com.example.platform.model.User;
import com.example.platform.repo.UserRepo;
import com.github.javafaker.Faker;
import jakarta.persistence.Column;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepo userRepo;

    public DataLoader(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();

//        for (int i = 0; i < 100; i++) {
//            String firstName = faker.name().firstName();
//            String lastName = faker.name().lastName();
//            String email = faker.internet().emailAddress();
//            String password = faker.internet().password();
//            String location = faker.address().city();
//            //String roles = "USER";
//
//            User user = new User(email, password, firstName, lastName, location);
//            //user.setRoles(roles);
//
//            userRepo.save(user);
//        }
    }
}
