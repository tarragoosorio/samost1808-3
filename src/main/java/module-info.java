module com.example.samost18082 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.samost18082 to javafx.fxml;
    exports com.example.samost18082;
}