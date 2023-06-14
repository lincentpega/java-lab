package com.lincentpega.labjdbc.views;

import com.lincentpega.labjdbc.dao.UserDAO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DBComponent extends VerticalLayout {
    private final UserView parentView;
    private final TextField dbName = new TextField("DB Name");
    private final Button create = new Button("Create");
    private final Button clear = new Button("Clear");
    private final Button connect = new Button("Connect");
    private final Button delete = new Button("Delete");

    public DBComponent(UserDAO userDAO, UserView parentView) {
        this.parentView = parentView;

        setClassName("db-edit");

        add(dbName, connect, clear, create, delete);

        connect.addClickListener(e -> {
            String dbNameValue = dbName.getValue();
            if (userDAO.checkDatabaseExists(dbNameValue)) {
                userDAO.setDatabase(dbNameValue);
            }
            parentView.updateList();
        });
        clear.addClickListener(e -> {
            String dbNameValue = dbName.getValue();
            if (userDAO.checkDatabaseExists(dbNameValue)) {
                userDAO.clearTable();
            }
            parentView.updateList();
        });
        create.addClickListener(e -> {
            String dbNameValue = dbName.getValue();
            if (!userDAO.checkDatabaseExists(dbNameValue)) {
                userDAO.createDatabase(dbNameValue);
            }
            parentView.updateList();
        });
        delete.addClickListener(e -> {
            String dbNameValue = dbName.getValue();
            if (userDAO.checkDatabaseExists(dbNameValue)) {
                userDAO.deleteDatabase(dbNameValue);
            }
            parentView.updateList();
        });
    }
}
