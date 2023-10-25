package com.buschmais.backend.image;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ImageDao {

	private final GridFsTemplate gridFsTemplate;
	private final GridFsOperations operations;
	private final ImageRepository imageRepository;

	@Autowired
	public ImageDao(GridFsTemplate gridFsTemplate, GridFsOperations operations, ImageRepository imageRepository) {
		this.gridFsTemplate = gridFsTemplate;
		this.operations = operations;
		this.imageRepository = imageRepository;
	}

	public Image save(Image img, InputStream s)	{
		img = imageRepository.save(img);
		gridFsTemplate.store(s, img.getId(), img.getType().name(), img);
		return img;
	}

	public Optional<Image> findByName(@NonNull String name){
		return imageRepository.findByName(name);
	}

	public Optional<InputStream> getInputStream(final Image img) {
		GridFSFile file =  gridFsTemplate.findOne(new Query(Criteria.where("filename").is(img.getId())));
		if (file == null) return Optional.empty();
		try {
			InputStream s = operations.getResource(file.getFilename()).getInputStream();
			return Optional.of(s);
		}catch (IOException e){
			return Optional.empty();
		}
	}

	public void delete(@NonNull Image img){
		gridFsTemplate.delete(new Query(Criteria.where("filename").is(img.getId())));
		imageRepository.delete(img);
	}

	public void deleteAll(){
		imageRepository.findAll().forEach(
				img -> gridFsTemplate.delete(new Query(Criteria.where("filename").is(img.getId())))
		);
		imageRepository.deleteAll();
	}
}
