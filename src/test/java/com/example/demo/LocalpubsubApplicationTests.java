package com.example.demo;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class LocalpubsubApplicationTests {

	@Autowired
	private UserRepository userRepository;

	FixtureMonkey fixtureMonkey = FixtureMonkey.create();

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	@Rollback(false)
	void populate() {
		List<User> users = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			User user = fixtureMonkey.giveMeBuilder(new TypeReference<User>() {})
					.set("id", null)
					.sample();
			System.out.println("user.getName() = " + user.getName());
			users.add(user);
		}
		userRepository.saveAll(users);
	}

}
