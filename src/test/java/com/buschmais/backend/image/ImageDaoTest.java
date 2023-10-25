package com.buschmais.backend.image;

import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.infrastructure.AbstractIntegrationTest;
import com.buschmais.backend.users.dataAccess.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Import({UserDao.class, ADRDao.class, ADRAccessDao.class, ImageDao.class})
class ImageDaoTest extends AbstractIntegrationTest {

	@Autowired
	private ImageDao imageDao;

	@BeforeEach
	public void beforeEach() {
		imageDao.deleteAll();
	}

	@Test
	void saveImage() {
		// Given
		try (InputStream fs = new FileInputStream("./frontend/images/place-holder-image.png")) {
			// when
			Image img = new Image("img1");
			imageDao.save(img, fs);
		} catch (IOException exp) {
			fail(exp);
		}

		// Then
		Optional<Image> img = imageDao.findByName("img1");

		assertTrue(img.isPresent());

		Optional<InputStream> s = imageDao.getInputStream(img.get());
		assertTrue(s.isPresent());

		try {
			byte[] expected = new FileInputStream("./frontend/images/place-holder-image.png").readAllBytes();

			byte[] readBytes = s.get().readAllBytes();
			assertEquals(readBytes.length, expected.length);
			for (int i = 0; i < readBytes.length; ++i) {
				assertEquals(readBytes[i], expected[i]);
			}
		} catch (IOException e) {
			fail(e);
		}
	}
}
