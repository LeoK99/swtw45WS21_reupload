package com.buschmais.backend.config;

import com.buschmais.backend.events.RecursiveMongoEventListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.PostConstruct;

@Configuration
public class MongoConfig {

	private final AbstractApplicationContext context;
	private final BeanFactory beanFactory;

	public MongoConfig(@Autowired AbstractApplicationContext context, @Autowired BeanFactory beanFactory){
		this.context = context;
		this.beanFactory = beanFactory;
	}

	@PostConstruct
	public void registerListeners(){
		RecursiveMongoEventListener listener = beanFactory.getBean(RecursiveMongoEventListener.class);

		context.addApplicationListener(listener);
	}

}
