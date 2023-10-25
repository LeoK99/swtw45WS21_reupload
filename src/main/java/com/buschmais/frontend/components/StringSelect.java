package com.buschmais.frontend.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class StringSelect extends VerticalLayout {
	private final Set<String> original;
	private final Set<String> selected;
	private final ComboBox<String> box;
	private final HorizontalLayout selectedLayout;
	private boolean updating;

	public StringSelect(final @NonNull String labelName){
		super();
		this.selected = new HashSet<>();
		this.original = new HashSet<>();

		selectedLayout = new HorizontalLayout();
		selectedLayout.getStyle().set("flex-wrap","wrap");

		box = new ComboBox<>(labelName);

		box.setItems(String::startsWith, original);

		updating = false;
		box.addValueChangeListener(e -> {
			if (!updating){
				if (e != null){
					selected.add(e.getValue());
					createSpan(e.getValue());
				}
				this.update();
			}
		});

		setPadding(false);
		add(box, selectedLayout);
	}

	public void load(final @NonNull Stream<String> initialPaths,
					 final @NonNull Stream<String> preselected){
		this.selected.clear();
		this.original.clear();

		this.selected.addAll(preselected.toList());
		this.original.addAll(initialPaths.filter(ele -> !selected.contains(ele)).toList());
		box.setItems(String::startsWith, original);

		this.selectedLayout.removeAll();
		selected.forEach(this::createSpan);
	}

	private void createSpan(String text) {
		Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
		closeBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
		closeBtn.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		closeBtn.getElement().setAttribute("title", "Remove");
		Span selectedSpan = new Span(text);

		selectedSpan.add(closeBtn);
		selectedSpan.getElement().getThemeList().add("badge contrast pill");

		closeBtn.addClickListener(clickEvent -> {
			selectedLayout.remove(selectedSpan);
			selected.remove(selectedSpan.getText());
			this.update();
		});

		this.selectedLayout.add(selectedSpan);
	}

	public void update(){
		updating = true;
		box.setItems(String::startsWith, original.stream().filter(ele -> !selected.contains(ele)).toList());
		updating = false;
	}

	public List<String> getSelected(){
		return selected.stream().toList();
	}
}
