package com.buschmais.backend.adr;

import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.dataAccess.ADRRepository;
import com.buschmais.backend.adr.status.ADRStatusCreated;
import com.buschmais.backend.adr.status.ADRStatusProposed;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.events.RecursiveMongoEventListener;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.infrastructure.AbstractIntegrationTest;
import com.buschmais.backend.users.dataAccess.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback
@Import({RecursiveMongoEventListener.class, UserDao.class, ImageDao.class, ADRDao.class, ADRAccessDao.class})
class ADRDaoTest extends AbstractIntegrationTest {

	@Autowired
	private ADRDao adrDao;
	@Autowired
	private ADRRepository adrRepository;
	@Autowired
	private ADRContainerRepository adrContainerRepository;

	@BeforeEach
	public void beforeEach() {
		adrDao.deleteAll();
		adrRepository.deleteAll();
		adrContainerRepository.deleteAll();
	}

	@Test
	void findAllByName() {

		// Given
		ADRContainer cont1 = new ADRContainer("cont1");
		ADR adr1 = new ADR(cont1, "adr1", new ADRStatusCreated());
		adrRepository.save(adr1);

		// When
		List<ADR> foundAdr = adrRepository.findAllByName("adr1");

		// Then
		assertThat(foundAdr).isNotEmpty();
		assertThat(foundAdr.size()).isEqualTo(1);
		assertThat(foundAdr.get(0).getParentPath()).isEqualTo(cont1.getFullPath());
	}

	@Test
	void treeSaveAndRestore() {

		//         cont1
		//         |     \
		//        cont2    cont3
		//			       |    \
		//              cont4    adr1

		// Given
		ADRContainer cont1 = new ADRContainer("cont1");

		ADRContainer cont2 = new ADRContainer(cont1, "cont12");
		adrContainerRepository.save(cont2);

		ADRContainer cont3 = new ADRContainer(cont1, "cont13");
		ADRContainer cont4 = new ADRContainer(cont3, "cont134");
		adrContainerRepository.save(cont4);

		ADR adr1 = new ADR(cont3, "adr31", new ADRStatusCreated());
		adrRepository.save(adr1);

		// When
		List<ADR> foundAdr1 = adrRepository.findAllByName("adr31");
		List<ADR> foundAdr2 = adrRepository.findAllByParentPath(cont3.getFullPath());

		// Then
		assertThat(foundAdr1).isEqualTo(foundAdr2);

		ADRContainer foundParent = foundAdr1.get(0).getParent();
		assertThat(foundParent).isEqualTo(cont3);

		ADRContainer foundGrandParent = foundAdr1.get(0).getParent().getParent();
		assertThat(foundGrandParent).isEqualTo(cont1);

		List<ADRContainer> firstChildren = adrContainerRepository.findAllByParentPath(foundGrandParent.getFullPath());
		List<ADRContainer> secondChildren = adrContainerRepository.findAllByParentPath(foundParent.getFullPath());

		assertThat(firstChildren).contains(cont2, cont3);
		assertThat(secondChildren).contains(cont4);
	}

	@Test
	void findBySts() {
		// Given
		ADRContainer cont1 = new ADRContainer("cont1");
		ADR adr1 = new ADR(cont1, "adr1", new ADRStatusCreated());
		adrRepository.save(adr1);
		ADR adr2 = new ADR(cont1, "adr2", new ADRStatusProposed());
		adrRepository.save(adr2);

		// When
		List<ADR> foundCreated = adrRepository.findAllByStatusType(ADRStatusType.CREATED);
		List<ADR> foundProposed = adrRepository.findAllByStatusType(ADRStatusType.PROPOSED);

		// Then
		assertThat(foundCreated).contains(adr1);
		assertThat(foundProposed).contains(adr2);
	}

	@Test
	void findTagsMatchRegex() {
		// Given
		ADRContainer cont1 = new ADRContainer("cont1");

		ADR adr1 = new ADR(cont1, "adr1", new ADRStatusCreated());
		{
			ADRTag tag1 = new ADRTag();
			tag1.setTagText("adr1-tag1");
			adr1.addTag(tag1);

			ADRTag tag2 = new ADRTag();
			tag2.setTagText("adr1-tag2");
			adr1.addTag(tag1);
			adr1 = adrDao.save(adr1);
		}

		ADR adr2 = new ADR(cont1, "adr2", new ADRStatusProposed());
		{
			ADRTag tag1 = new ADRTag();
			tag1.setTagText("adr2-tag1");
			adr2.addTag(tag1);

			ADRTag tag2 = new ADRTag();
			tag2.setTagText("adr2-tag2");
			adr2.addTag(tag1);
			adr2 = adrDao.save(adr2);
		}

		// When
		List<ADRTag> foundFromAdr1 = adrDao.findAllTagsMatchRegex("adr1");
		List<ADRTag> foundFromAdr2 = adrDao.findAllTagsMatchRegex("adr2");

		// Then
		assertThat(foundFromAdr1).isEqualTo(adr1.getTags());
		assertThat(foundFromAdr2).isEqualTo(adr2.getTags());
	}
}