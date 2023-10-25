package com.buschmais.frontend.views.empty1;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.buschmais.frontend.MainLayout;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@PageTitle("Empty1")
@Route(value = "empty1", layout = MainLayout.class)
@Tag("empty1-view")
@JsModule("./views/empty1/empty1-view.ts")
public class Empty1View extends LitTemplate implements HasStyle {

    public Empty1View() {
        addClassNames("flex", "flex-col", "h-full");
    }
}
