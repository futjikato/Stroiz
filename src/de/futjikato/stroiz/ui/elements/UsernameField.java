package de.futjikato.stroiz.ui.elements;

public class UsernameField extends ValidationField {

    @Override
    public boolean validate(String value) {
        return value.length() >= 3 && !value.contains(" ");
    }
}
