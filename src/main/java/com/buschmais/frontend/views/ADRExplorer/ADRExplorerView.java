package com.buschmais.frontend.views.ADRExplorer;

import com.buschmais.backend.adr.ADR;
import com.buschmais.frontend.components.ADRExplorerComponent;
import com.buschmais.frontend.components.ErrorNotification;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.buschmais.frontend.views.adrCreate.ADRRichCreateView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ADRExplorerView extends Div {
	ADRExplorerComponent explorerComponent;
	private ADR selectedADR = null;


	 public ADRExplorerView(@Autowired ADRExplorerComponent explorerComponent){
		this.explorerComponent =  explorerComponent;

		explorerComponent.setAllRowsVisible(true);
		explorerComponent.addSelectionListener(selection ->
				selection.getFirstSelectedItem().ifPresent(adr -> selectedADR = adr));
		explorerComponent.setSizeUndefined();


		 Button Edit = new Button("demo Edit");
		 Edit.addClickListener(e->{
			 if(selectedADR != null){
				 String route = RouteConfiguration.forSessionScope().getUrl(ADRRichCreateView.class, selectedADR.getId());
				 this.getUI().ifPresent((ui) -> ui.getPage().setLocation(route));
			 }else{
				ErrorNotification errNot = new ErrorNotification("Select an ADR to edit");
				 errNot.open();
			 }


		 });

		 add(explorerComponent);
	 }


}
