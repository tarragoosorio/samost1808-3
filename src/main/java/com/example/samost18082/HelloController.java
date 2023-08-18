package com.example.samost18082;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HelloController {

    ObservableList<Book> books = FXCollections.observableArrayList();
    HashMap<Integer, Book> isbnMap = new HashMap<>();

    @FXML
    TableView<Book> table;
    @FXML
    Button saveBtn;

    public void initialize() throws SQLException {
        try {
            zapolnitBooksFromDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        initTable();

        startChangeTracking();

        saveBtn.setOnAction(actionEvent -> {
                    try {
                        saveToFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

        );
    }

    private void saveToFile() throws IOException {

        String fname = "OUTPUT.txt";
        FileWriter output = new FileWriter(new File(fname));
        for (Map.Entry<Integer, Book> Map :isbnMap.entrySet()) {
            output.write(Map.getKey() + " " + Map.getValue());
            System.out.println("Что-то изменилось");
            output.close();
        }

    }

    public void startChangeTracking(){
        for (Book b: books     ) {
            b.titleProperty().addListener((val, o, n)->  isbnMap.put(b.getIsbn(), b) );
            b.yearProperty().addListener( (val, o, n)->  isbnMap.put(b.getIsbn(), b) );
        }
    }

    private void zapolnitBooksFromDB() throws SQLException {
        Connection conn =  connectToDB();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT isbn, title, year FROM book ORDER BY title");
        rs.next();

        while (rs.next()) {
            int isbn = rs.getInt("isbn");
            String title = rs.getString("title");
            int year = rs.getInt("year");
            Book book = new Book(isbn, title,year);
            books.add(book);
            //books.add(new Book(rs.getInt("isbn"), rs.getString("title"), rs.getInt("year")));
        }
        rs.close();
        st.close();
        //System.out.println(books);
    }

    private void initTable() {
        table.getColumns().clear();

        TableColumn<Book, Integer> columnISBN = new TableColumn<>("ISBN");
        columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        //columnISBN.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

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