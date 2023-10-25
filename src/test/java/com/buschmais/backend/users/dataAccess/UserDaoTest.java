package com.buschmais.backend.users.dataAccess;

import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.infrastructure.AbstractIntegrationTest;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.UserRights;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({UserDao.class, ADRDao.class, ADRAccessDao.class, ImageDao.class})
class UserDaoTest extends AbstractIntegrationTest {

	@Autowired
	private UserDao userDao;

	@BeforeEach
	public void beforeEach() {
		userDao.deleteAll();
	}

	@Test
	void findAllByRights() {
		// Given
		User user = new User("user1", "pwd1");
		userDao.save(user);

		// When
		List<User> foundUsers = userDao.findAllByRights(new UserRights(0));

		// Then
		assertThat(foundUsers).isNotEmpty();
	}

	@Test
	void findAllHasRights() {
		// Given
		User user = new User("user1", "pwd1");
		user.setRights(new UserRights(UserRights.CAN_MANAGE_USERS));
		userDao.save(user);

		User user2 = new User("user2", "pwd2");
		user2.setRights(new UserRights(UserRights.CAN_MANAGE_VOTING));
		userDao.save(user2);

		User user3 = new User("user3", "pwd3");
		user3.setRights(new UserRights(UserRights.CAN_MANAGE_USERS | UserRights.CAN_MANAGE_VOTING));
		userDao.save(user3);

		// When
		List<User> foundUsers1 = userDao.findAllByHasRights(new UserRights(UserRights.CAN_MANAGE_USERS));
		List<User> foundUsers2 = userDao.findAllByHasRights(new UserRights(UserRights.CAN_MANAGE_VOTING));

		// Then
		assertThat(foundUsers1.size()).isEqualTo(2);
		assertThat(foundUsers1).contains(user, user3);

		assertThat(foundUsers2.size()).isEqualTo(2);
		assertThat(foundUsers2).contains(user2, user3);
	}
}