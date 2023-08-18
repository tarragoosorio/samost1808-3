package com.example.samost18082;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;
import java.util.ArrayList;

public class HelloController {

    ObservableList<Book> books = FXCollections.observableArrayList();
    @FXML
    TableView<Book> table;
    @FXML
    Button saveBtn;

    public void initialize() throws SQLException {
        try {
            example1();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        initTable();
    }


    private void example1() throws SQLException {
        Connection conn =  connectToDB();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT isbn, title, year FROM book ORDER BY title");
        rs.next();

        while (rs.next()) {
            int isbn = rs.getInt(1);
            String title = rs.getString(2);
            int year = rs.getInt(3);
            Book book = new Book(isbn, title,year);
            books.add(book);
        }
        rs.close();
        st.close();
        System.out.println(books);
    }

    private void initTable() {
        table.getColumns().clear();

        TableColumn<Book, Integer> columnISBN = new TableColumn<>("ISBN");
        columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        columnISBN.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn<Book, String> columnTitle = new TableColumn<>("Title");
        columnTitle.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        columnTitle.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Book, Integer> columnYear = new TableColumn<>("Year");
        columnYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        columnYear.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        table.getColumns().add(columnISBN);
        table.getColumns().add(columnTitle);
        table.getColumns().add(columnYear);
        table.setItems(books);
        table.setEditable(true);

    }

    private static Connection connectToDB() {
        String url = "jdbc:postgresql://10.10.104.166:5432/Lib?user=postgres&password=123";//&ssl=true
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("подключено");
            return conn;
        } catch (Exception e) {
            System.out.println("не удалось подключиться к базе. " + e.getMessage());
            return null;
        }
    }



}