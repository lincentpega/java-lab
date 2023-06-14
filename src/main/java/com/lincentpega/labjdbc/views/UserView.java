package com.lincentpega.labjdbc.views;

import com.lincentpega.labjdbc.dao.UserDAO;
import com.lincentpega.labjdbc.model.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Users")
public class UserView extends VerticalLayout {

    private final UserDAO userDAO;
    Grid<User> grid = new Grid<>(User.class);
    TextField filterText = new TextField();
    UserForm form = new UserForm();
    DBComponent dbComponent;


    public UserView(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.dbComponent = new DBComponent(userDAO, this);

        setClassName("user-view");
        setSizeFull();
        configureGrid();
        configureForm();
        configureDBPanel();

        add(getToolBar(), getContent());
        updateList();
        closeEditor();
    }

    public void updateList() {
        grid.setItems(userDAO.findAllUsers(filterText.getValue()));
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(dbComponent, grid, form);
        content.setFlexGrow(1, dbComponent);
        content.setFlexGrow(3, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveUser);
        form.addDeleteListener(this::deleteUser);
        form.addCloseListener(e -> closeEditor());
    }

    private void configureDBPanel() {
        dbComponent.setWidth("25em");
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        User user = event.getUser();
        Long id = user.getId();
        if (userDAO.checkUserExists(id)) {
            userDAO.deleteUser(id);
        }
        updateList();
        closeEditor();
    }

    private void saveUser(UserForm.SaveEvent event) {
        User user = event.getUser();
        if (userDAO.checkUserExists(user.getId())) {
            userDAO.updateUser(user);
        } else {
            userDAO.createUser(user);
        }
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setWidth(null);
        grid.setSizeFull();
        grid.setColumns("id", "name", "age");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editUser(event.getValue()));
    }

    public void editUser(User user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addUser() {
        grid.asSingleSelect().clear();
        editUser(new User());
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addUserButton = new Button("Add user");
        addUserButton.addClickListener(click -> addUser());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addUserButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
}
