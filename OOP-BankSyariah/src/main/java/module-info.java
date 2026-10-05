module com.klm5pens.oop.banksyariah {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.klm5pens.oop.banksyariah to javafx.fxml;
    exports com.klm5pens.oop.banksyariah;
}
