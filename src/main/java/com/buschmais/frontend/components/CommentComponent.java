package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADRComment;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public class CommentComponent extends Component {
	//TODO replies of comment
	private final ADRComment comment;

	private Avatar AuthorPicture;

	private Button commentCreate;
	// add time
	private Instant time;

	private HorizontalLayout mainLayout;
	private HorizontalLayout authorLayout;
	private VerticalLayout contentLayout;

	public CommentComponent(@NotNull ADRComment comment){
		this.comment = comment;
		commentConfigure();
	}
	private void commentConfigure(){
		AuthorPicture = new Avatar();
		//AuthorPicture.setImage();
		mainLayout = new HorizontalLayout();
		authorLayout = new HorizontalLayout();
		contentLayout = new VerticalLayout();

		authorLayout.add(comment.getAuthor());
		authorLayout.add(time.toString());//not sure

		contentLayout.add(authorLayout);
		contentLayout.add(comment.getText());

		mainLayout.add(AuthorPicture);
		mainLayout.add(contentLayout);
	}

	public HorizontalLayout getMainLayout() {
		return mainLayout;
	}
}
