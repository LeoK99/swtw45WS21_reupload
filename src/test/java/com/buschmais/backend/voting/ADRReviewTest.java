package com.buschmais.backend.voting;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRContainer;
import com.buschmais.backend.adr.ADRContainerRepository;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusProposed;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.infrastructure.AbstractIntegrationTest;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import({UserDao.class, ADRDao.class, ADRAccessDao.class, ImageDao.class})
class ADRReviewTest extends AbstractIntegrationTest {

	@Autowired
	private ADRDao adrDao;
	@Autowired
	private ADRContainerRepository adrContainerRepository;

	@BeforeEach
	public void beforeEach() {
		adrContainerRepository.deleteAll();
		adrDao.deleteAll();
	}

	@Test
	void userNotInvited() {
		// Given
		ADRReview r = new ADRReview();
		User u = new User("user1", "pwd1");

		// Then
		assertThrows(UserIsNotInvitedException.class, () -> r.putVote(u, VoteType.FOR));
	}

	@Test
	void putVote() {
		// Given
		ADRReview r = new ADRReview();
		User u = new User("user1", "pwd1");

		// When
		r.addVoter(u);

		// Then
		assertDoesNotThrow(() -> r.putVote(u, VoteType.FOR));
	}

	@Test
	void replaceVote() {
		// Given
		ADRReview r = new ADRReview();
		User u = new User("user1", "pwd1");

		// When
		r.addVoter(u);
		assertDoesNotThrow(() -> r.putVote(u, VoteType.FOR));
		assertDoesNotThrow(() -> r.putVote(u, VoteType.AGAINST));
		Optional<VoteType> v = r.getUserVote(u);

		// Then
		assertTrue(v.isPresent());
		assertEquals(v.get(), VoteType.AGAINST);
	}

	@Test
	void getUserVote() {
		// Given
		ADRReview r = new ADRReview();
		User u = new User("user1", "pwd1");
		User u2 = new User("user2", "pwd2");

		// When
		r.addVoter(u); r.addVoter(u2);
		assertDoesNotThrow(() -> r.putVote(u, VoteType.FOR));
		assertDoesNotThrow(() -> r.putVote(u2, VoteType.AGAINST));
		Optional<VoteType> v = r.getUserVote(u);
		Optional<VoteType> v2 = r.getUserVote(u2);

		// Then
		assertTrue(v.isPresent());
		assertTrue(v2.isPresent());
		assertEquals(v.get(), VoteType.FOR);
		assertEquals(v2.get(), VoteType.AGAINST);
	}

	@Test
	void getVoteResult() {
		// Given
		ADRReview r = new ADRReview();
		User u = new User("user1", "pwd1");
		User u2 = new User("user2", "pwd2");

		// When
		r.addVoter(u); r.addVoter(u2);
		assertDoesNotThrow(() -> r.putVote(u, VoteType.FOR));
		assertDoesNotThrow(() -> r.putVote(u2, VoteType.AGAINST));

		// Then
		Map<VoteType, Integer> result = r.getVoteResult();
		assertTrue(result.containsKey(VoteType.FOR));
		assertTrue(result.containsKey(VoteType.AGAINST));

		assertEquals(result.get(VoteType.FOR),1);
		assertEquals(result.get(VoteType.AGAINST), 1);
	}

	@Test
	void savable() {
		// Given
		ADRContainer cont = new ADRContainer("cont");
		cont = adrContainerRepository.save(cont);

		// When
		ADRStatusProposed sts = new ADRStatusProposed();
		ADR adr = new ADR(cont, "adr", sts);

		// Then
		assertDoesNotThrow(() -> {adrDao.save(adr);});
	}
}