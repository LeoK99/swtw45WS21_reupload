package com.buschmais.backend.cePersisters;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRComment;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.collaborationengine.CollaborationMessage;
import com.vaadin.collaborationengine.CollaborationMessagePersister;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@SpringComponent
public class MessagePersisterImpl implements CollaborationMessagePersister {

	private final ADRDao adrDao;


	private final UserDao userDao;



	public MessagePersisterImpl(@Autowired ADRDao adrDao,
							    @Autowired UserDao userDao) {
		this.adrDao = adrDao;
		this.userDao = userDao;
	}

	@Override
	public Stream<CollaborationMessage> fetchMessages(FetchQuery query) {

		ADR adr = adrDao.findById(query.getTopicId().substring(4)).orElse(null);

		List<CollaborationMessage> comments = new LinkedList<>();

		if(adr == null) {
			System.err.println("ADR doesn't exist. ID: " + query.getTopicId().substring(4));
			return comments.stream();
		}

		for(ADRComment comment : adr.getComments()) {
			if(!comment.getTimeStamp().isBefore(query.getSince())) {
				User author = userDao.findById(comment.getAuthor()).orElse(null);
				if(author == null) continue;

				comments.add(new CollaborationMessage(new UserInfo(author.getId(), author.getUserName()), comment.getText(), comment.getTimeStamp()));
			}
		}

		return comments.stream();

/*		return messageService
				.findAllByTopicSince(query.getTopicId(), query.getSince())
				.map(messageEntity -> {
					User author = messageEntity.getAuthor();


					UserInfo userInfo = new UserInfo(author.getId(),
							author.getName(), author.getImageUrl());

					return new CollaborationMessage(userInfo,
							messageEntity.getText(), messageEntity.getTime());
				});
*/	}

	@Override
	public void persistMessage(PersistRequest request) { //Synchronisieren
		CollaborationMessage message = request.getMessage();

		ADRComment adrComment = new ADRComment(message.getUser().getId(), message.getText());
		adrComment.setTimeStamp(message.getTime());

		synchronized (adrDao) {
			ADR adr = adrDao.findById(request.getTopicId().substring(4)).orElse(null);

			if (adr == null) {
				System.err.println("ADR doesn't exist. ID: " + request.getTopicId().substring(4));
				return;
			}

			adr.addADRComment(adrComment);
			adrDao.save(adr);
		}
	}
}
