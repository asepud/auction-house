package com.asdin.test_rest.config;

import com.asdin.test_rest.domain.*;
import com.asdin.test_rest.enums.Role;
import com.asdin.test_rest.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Seeds only categories and the documented development administrator. */
@org.springframework.context.annotation.Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(CategoryRepository categories, UserRepository users, PasswordEncoder encoder,
            @Value("${app.seed.admin-email}") String email, @Value("${app.seed.admin-password}") String password) {
        return args -> {
            for (String name : new String[] { "Elektronik", "Koleksi", "Otomotif", "Properti" })
                if (categories.findByNameIgnoreCase(name).isEmpty())
                    categories.save(Category.builder().name(name).build());
            if (users.findByEmailIgnoreCase(email).isEmpty())
                users.save(User.builder().name("Administrator").email(email.toLowerCase())
                        .password(encoder.encode(password)).role(Role.ADMIN).build());
        };
    }
}
