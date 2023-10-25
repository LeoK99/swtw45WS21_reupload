package com.buschmais.frontend.components;

import com.vaadin.flow.component.dependency.CssImport;
import org.vaadin.klaudeta.quill.QuillEditor;

@CssImport(value = "./themes/adr-workbench/rich-text-editor/quill-editor.css")
public class RichTextEditor extends QuillEditor {

	public RichTextEditor(){
		super();
	}

	public RichTextEditor(String RichLabel){
		this();
		this.setReadOnly(true);
		this.setValue(RichLabel);
	}


	public void load(String value){

		/*String html = "<div class=\"ql-editor\" data-gramm=\"false\" contenteditable=\"true\">" +
				"<p>" + value + "</p>" +
				"</div>" +
				"<div class=\"ql-clipboard\" tabindex=\"-1\" contenteditable=\"true\">" +
				"</div>" +
				"<div class=\"ql-tooltip ql-hidden\">" +
				"<a class=\"ql-preview\" rel=\"noopener noreferrer\" target=\"_blank\" href=\"about:blank\"></a>" +
				"<input type=\"text\" data-formula=\"e=mc^2\" data-link=\"https://quilljs.com\" data-video=\"Embed URL\"><a class=\"ql-action\"></a>" +
				"<a class=\"ql-remove\"></a>" +
				"</div>";

		this.getChildren().findFirst().get().getElement().getChild(1).getChild(0).setProperty("innerHTML",html);*/

		this.getToolbarConfigurator().getElement().executeJs("this.quillEditor.root.innerHTML = $0", value);
	}


}
