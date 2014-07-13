package de.futjikato.stroiz.ui.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public abstract class ValidationField extends TextField {

    private static final String CSS_CLASS_ERROR = "error";

    public String getValidatedValue() throws ValidationException {
        String value = getText();
        if(!validate(value)) {
            setError();
            throw new ValidationException("Validation failed");
        } else {
            unsetError();
        }

        return value;
    }

    public abstract boolean validate(String value);

    public boolean validate() {
        boolean success = validate(getText());

        // update view
        if(success) {
            unsetError();
        } else {
            setError();
        }

        return success;
    }

    protected void setError() {
        ObservableList<String> styleClasse = getStyleClass();
        if(!styleClasse.contains(CSS_CLASS_ERROR)) {
            styleClasse.add(CSS_CLASS_ERROR);
        }
    }

    protected void unsetError() {
        ObservableList<String> styleClasse = getStyleClass();
        if(styleClasse.contains(CSS_CLASS_ERROR)) {
            styleClasse.remove(CSS_CLASS_ERROR);
        }
    }
}
