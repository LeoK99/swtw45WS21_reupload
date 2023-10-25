package com.buschmais.frontend.views.kanban;

import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.components.KanbanColumnComponent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

//@Route(value = "overview2", layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/kanban/kanban-view.css")
@PageTitle("Kanban Board")
public class KanbanView extends HorizontalLayout {

	private final ADRDao adrService;
	private final UserDao userService;

	public KanbanView(@Autowired @NotNull ADRDao adrService, @Autowired @NotNull UserDao userService) {
		this.adrService = adrService;
		this.userService = userService;

		setupComponents();
	}

	private void setupComponents() {
		this.setId("kanban-main-layout");
		this.add(new KanbanColumnComponent(ADRStatusType.CREATED.getName(), this.userService, this.adrService, 10, ADRStatusType.CREATED, ADRStatusType.INTERNALLY_PROPOSED));
		this.add(new KanbanColumnComponent(ADRStatusType.PROPOSED.getName(), this.userService, this.adrService, 10, ADRStatusType.PROPOSED));
		this.add(new KanbanColumnComponent(ADRStatusType.APPROVED.getName(), this.userService, this.adrService, 10, ADRStatusType.APPROVED));
		this.add(new KanbanColumnComponent(ADRStatusType.REFUSED.getName(), this.userService, this.adrService, 10, ADRStatusType.REFUSED));
		this.add(new KanbanColumnComponent(ADRStatusType.SUPERSEDED.getName(), this.userService, this.adrService, 10, ADRStatusType.SUPERSEDED));
	}
}
