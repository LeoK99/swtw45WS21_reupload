package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@CssImport(value = "./themes/adr-workbench/kanban/kanban-view.css")
public class KanbanColumnComponent extends VerticalLayout {

	private VerticalLayout titleLayout;
	private VerticalLayout adrLayout;
	private Scroller adrScroller;

	private Label title;

	private Span separator;

	public KanbanColumnComponent() {
		super();
		this.addClassName("kanban-view-status-layout");
	}

	public KanbanColumnComponent(@NonNull String title) {
		this();
		this.title = new Label(title);
		this.title.addClassName("kanban-view-status-heading");

		this.titleLayout = new VerticalLayout(this.title);
		this.titleLayout.setAlignItems(Alignment.CENTER);
		this.add(this.titleLayout);
	}

	public KanbanColumnComponent(String title, @NonNull UserDao userService, @NonNull ADRDao adrService, int amount, ADRStatusType ...statuses) {
		this(title);

		for(ADRStatusType status : statuses){
			if (status.equals(ADRStatusType.CREATED)) {
				this.addClassName("kanban-view-status-layout-created");
			} else if (status.equals(ADRStatusType.SUPERSEDED)) {
				this.addClassName("kanban-view-status-layout-superseded");
			}
		}

		this.separator = new Span();
		this.separator.addClassName("kanban-view-separator");
		this.add(this.separator);

		this.adrLayout = new VerticalLayout();
		this.adrLayout.addClassName("kanban-view-adr-layout");

		List<ADR> adrs = new LinkedList<>();

		if(statuses.length == 1) {
			adrService.findAllRelevantByStatusType(statuses[0], amount).forEach(adr -> this.adrLayout.add(new ADRComponent(adr, userService.getCurrentUser(), adrService)));
		}
		else {
			for(ADRStatusType status : statuses){
				adrs.addAll(adrService.findAllRelevantByStatusType(status, amount));
			}

			adrs.sort((a1, a2) -> a2.getTimeStamp().compareTo(a1.getTimeStamp()));
			amount = Math.max(0, amount);
			int endIndex = Math.min(amount, adrs.size());
			adrs.subList(0, endIndex);
			adrs.forEach(adr -> this.adrLayout.add(new ADRComponent(adr, userService.getCurrentUser(), adrService)));
		}

		this.adrScroller = new Scroller(this.adrLayout);
		this.adrScroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
		this.adrScroller.addClassName("kanban-view-scroller");

		this.add(this.adrScroller);
	}

}
