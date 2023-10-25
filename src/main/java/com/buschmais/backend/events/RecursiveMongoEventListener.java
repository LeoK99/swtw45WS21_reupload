package com.buschmais.backend.events;

import com.buschmais.backend.config.RecursiveSaving;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RecursiveMongoEventListener extends AbstractMongoEventListener<Object> {

	private final MongoOperations mongoOperations;

	public RecursiveMongoEventListener(@Autowired final MongoOperations mongoOperations){
		this.mongoOperations = mongoOperations;
	}

	@Override
	public void onBeforeConvert(final BeforeConvertEvent<Object> event){
		Object src = event.getSource();
		ReflectionUtils.doWithFields(src.getClass(),
				field -> {
					if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(RecursiveSaving.class)) {
						ReflectionUtils.makeAccessible(field);
						Object fieldValue = field.get(src);
						if(fieldValue != null){
							if(fieldValue instanceof final List<?> objs){
								objs.forEach(mongoOperations::save);
							} else if(fieldValue instanceof final Set<?> objs){
								objs.forEach(mongoOperations::save);
							} else if(fieldValue instanceof final Map<?,?> objs){
								objs.forEach((k,v) -> {mongoOperations.save(k); mongoOperations.save(v);});
							}else {
								mongoOperations.save(fieldValue);
							}
						}
					}
				});
		super.onBeforeConvert(event);
	}
}