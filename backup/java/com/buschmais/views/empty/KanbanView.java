package com.buschmais.frontend.views.empty;

import com.buschmais.backend.adr.ADR;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

@Route(value="Kanban")
@Theme
@PageTitle("Kanban board")
public class KanbanView {
	Grid<ADR> acceptedADR = new Grid<>(ADR.class, false);
	Grid<ADR> rejectedADR = new Grid<>(ADR.class, false);
	Grid<ADR> oneditADR = new Grid<>(ADR.class, false);
	public KanbanView(){
		configureGrid();
		kanban();
	}
	private void configureGrid(){
		acceptedADR.addClassName("accepted");
		acceptedADR.setColumns("accepted");
		acceptedADR.addColumn(ADR::getTitle).setHeader("accepted");
		acceptedADR.getColumns().forEach(col -> col.setAutoWidth(true));
		rejectedADR.addClassName("rejected");
		rejectedADR.setColumns("rejected");
		rejectedADR.addColumn(ADR::getTitle).setHeader("rejected");
		rejectedADR.getColumns().forEach(col -> col.setAutoWidth(true));
		oneditADR.addClassName("on_edit");
		oneditADR.setColumns("on_edit");
		oneditADR.addColumn(ADR::getTitle).setHeader("on_edit");
		oneditADR.getColumns().forEach(col -> col.setAutoWidth(true));
	}
	private HorizontalLayout kanban(){
		HorizontalLayout adrs = new HorizontalLayout();
		adrs.add(acceptedADR);
		adrs.add(rejectedADR);
		adrs.add(oneditADR);
		return adrs;
	}
}

