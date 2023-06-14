package com.lincentpega.labjdbc.views;

import com.lincentpega.labjdbc.model.User;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;


public class UserForm extends FormLayout {
    TextField id = new TextField("Id");
    TextField name = new TextField("Name");
    TextField age = new TextField("Age");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<User> binder = new BeanValidationBinder<>(User.class);


    public UserForm() {
        addClassName("user-form");
        binder.bindInstanceFields(this);
        add(id, name, age, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setUser(User user) {
        binder.setBean(user);
    }

    @Getter
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {

        private User user;
        public UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }

    }
    public static class SaveEvent extends UserFormEvent {

        public SaveEvent(UserForm source, User user) {
            super(source, user);
        }

    }
    public static class DeleteEvent extends UserFormEvent {

        public DeleteEvent(UserForm source, User user) {
            super(source, user);
        }

    }
    public static class CloseEvent extends UserFormEvent {

        public CloseEvent(UserForm source) {
            super(source, null);
        }

    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}
