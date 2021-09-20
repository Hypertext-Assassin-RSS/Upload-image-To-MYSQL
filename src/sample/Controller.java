package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;

public class Controller {

    @FXML
    public ImageView ImageView;

    @FXML
    private Button Open;

    @FXML
    private Button Load;

    private PreparedStatement store , retrieve;
    private String storeStatement = "INSERT INTO Photos(image) VALUES (?) ";
    private String loadStatement = "SELECT image FROM photos WHERE id = ? ";

    public void initialize() {



        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/java",
                    "root",
                    "1234");

            store = connection.prepareStatement(storeStatement ,ResultSet.CONCUR_READ_ONLY);
            retrieve = connection.prepareStatement(loadStatement, ResultSet.CONCUR_READ_ONLY);

        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
        Open.setOnAction(event ->choseFile());
        Load.setOnAction(event ->loadFile());
    }

    private void loadFile() {
        try {
            retrieve.setInt(1,2);
            ResultSet resultSet = retrieve.executeQuery();
            if (resultSet.first()){
                Blob blob = resultSet.getBlob(1);
                InputStream inputStream = blob.getBinaryStream();
                Image image = new Image(inputStream);
                ImageView.setImage(image);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void choseFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(Open.getScene().getWindow());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            store.setBinaryStream(1,fileInputStream,fileInputStream.available());
            store.execute();
            Image image = new Image(fileInputStream);
            ImageView.setImage(image);
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }

    }


}
