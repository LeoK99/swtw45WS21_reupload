package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@CssImport(value = "./themes/adr-workbench/adrVoting/vote-result-bar.css")
public class VoteResultBar extends HorizontalLayout {

	public VoteResultBar() {
		this.addClassName("adr-vote-result-bar-main-layout");
	}

	public VoteResultBar(int userCount){
		this(0, 0, userCount);
	}

	public VoteResultBar(int positiveCount, int negativeCount, int userCount){
		this();

		int remainingCount = Math.abs(userCount - (positiveCount + negativeCount));

		Label posCountLabel = new Label(String.valueOf(positiveCount));
		Label negCountLabel = new Label(String.valueOf(negativeCount));
		Label remainingVotersLabel = new Label(remainingCount + " / " + userCount);

		posCountLabel.addClassName("adr-vote-positive-label");
		negCountLabel.addClassName("adr-vote-negative-label");
		remainingVotersLabel.addClassName("adr-vote-remaining-label");

		Div posCountLayout = new Div();
		posCountLayout.addClassName("adr-vote-positive-div-layout");

		Div posCount = new Div();
		Div negCount = new Div();
		Div remainingVoters = new Div();

		posCount.addClassName("adr-vote-positive-bar");
		negCount.addClassName("adr-vote-negative-bar");
		remainingVoters.addClassName("adr-vote-remaining-voters-div");

		int posWidth = ((userCount > 0) ? positiveCount*100/userCount : 0);
		int negWidth = ((userCount > 0) ? negativeCount*100/userCount : 0);
		int remWidth = 100 - posWidth - negWidth;

		if(positiveCount > 0) {
			if(posWidth >= 5) {
				posCount.add(posCountLabel);
			}
			if(posWidth == 100) {
				posCount.addClassName("adr-vote-full-positive-bar");
			}
		}
		else if(remWidth != 100) {
			remainingVoters.addClassName("adr-vote-remaining-voters-div-left-rounded");
		}
		posCount.setWidth(posWidth + "%");

		if(negativeCount > 0) {
			if(negWidth >= 5) {
				negCount.add(negCountLabel);
			}
			if(negWidth == 100) {
				negCount.addClassName("adr-vote-full-negative-bar");
			}
		}
		else if(remWidth != 100) {
			remainingVoters.addClassName("adr-vote-remaining-voters-div-right-rounded");
		}
		negCount.setWidth(negWidth + "%");

		if(positiveCount + negativeCount != userCount || userCount == 0) {
			if(remWidth >= 5) {
				if(remWidth < 10) {
					remainingVotersLabel.setText(Integer.toString(remainingCount));
				}
				else if(remWidth == 100) {
					remainingVoters.addClassName("adr-vote-remaining-voters-div-all-rounded");
				}
				remainingVoters.add(remainingVotersLabel);
			}
		}
		else if(positiveCount != 0 && negativeCount != 0){
			remainingVoters.addClassName("adr-vote-remaining-voters-div-red");
		}
		remainingVoters.setWidth(remWidth + "%");

		posCountLayout.add(posCount, remainingVoters, negCount);
		//negCountLayout.add(negCount);
		//remainingVotersLayout.add(remainingVoters);

		this.add(posCountLayout);
	}

}
