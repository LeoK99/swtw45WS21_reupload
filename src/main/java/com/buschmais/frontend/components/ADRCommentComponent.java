package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADRComment;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@CssImport("./themes/adr-workbench/adrVoting/comment-component.css")
public class ADRCommentComponent extends VerticalLayout {

		private final UserDao userService;

		private final ADRComment adrComment;
		private final List<ADRComment> adrCommentReplies;

		private VerticalLayout authorLayout;
		private VerticalLayout messageLayout;

		private Label authorLabel;
		private Label commentLabel;

		private boolean isCurrentUser;

		public ADRCommentComponent(@NonNull ADRComment adrComment, @NonNull @Autowired UserDao userService, boolean isCurrentUser){
			this.userService = userService;
			this.adrComment = adrComment;
			this.adrCommentReplies = adrComment.getReplies();
			this.isCurrentUser = isCurrentUser;

			setupComponents();
			addComponents();
		}

		private void setupComponents(){

			/* define components */
			{
				this.authorLayout = new VerticalLayout();
				this.messageLayout = new VerticalLayout();

				this.authorLabel = new Label(this.adrComment.getAuthor());
				this.commentLabel = new Label(this.adrComment.getText());
			}

			/* set properties */
			{
				this.setSpacing(false);
				this.setWidth("75%");

				this.authorLayout.setSpacing(false);
				this.messageLayout.setSpacing(false);

				this.addClassName("comment-component-main-div");
				this.authorLayout.addClassName("comment-component-author-layout");

				// written by user
				if(this.isCurrentUser){
					// this.authorLayout.setAlignItems(Alignment.END);
					this.authorLabel.setText("You"); // you
					this.addClassName("comment-component-right-align");
				}else{
					// this.authorLayout.setAlignItems(Alignment.START);
					this.addClassName("comment-component-left-align");
				}

				this.authorLabel.addClassName("comment-component-author-label");
			}
		}

		private void addComponents() {
			this.authorLayout.add(this.authorLabel);
			this.messageLayout.add(commentLabel);

			this.add(this.authorLabel);
			this.add(this.messageLayout);
		}


}
