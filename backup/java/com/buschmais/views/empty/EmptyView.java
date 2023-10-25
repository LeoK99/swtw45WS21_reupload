package com.buschmais.frontend.views.empty;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.buschmais.frontend.MainLayout;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@PageTitle("Empty")
@Route(value = "empty", layout = MainLayout.class)
@Tag("empty-view")
@JsModule("./views/empty/empty-view.ts")
public class EmptyView extends LitTemplate implements HasStyle {

    public EmptyView() {
        addClassNames("flex", "flex-col", "h-full");
    }
}
