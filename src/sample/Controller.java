package sample;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    JFXTextField minText;
    @FXML
    JFXTextField maxText;
    @FXML
    JFXButton runBtn;
    @FXML
    JFXButton loadBtn;
    @FXML
    JFXButton dropBtn;
    @FXML
    JFXListView numListView;
    @FXML
    Label msgLabel;

    final String AWS_URL = "jdbc:mysql://hap.cstctc64fqwk.us-east-1.rds.amazonaws.com:3306/numlist?user=admin&password=11223344";



    private void runBtn(String url)
    {
        try
        {
            Connection conn = DriverManager.getConnection(AWS_URL);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE NUMBERS (" +
                    "GENNUM INT)");
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            msgLabel.setText("TABLE NOT CREATED");
        }
        try
        {
            Connection conn = DriverManager.getConnection(AWS_URL);
            Statement stmt = conn.createStatement();

            int minNum = Integer.parseInt(minText.getText());
            int maxNum = Integer.parseInt(maxText.getText());

            Random rand = new Random();
            int numRand = rand.nextInt(maxNum - minNum + 1) + minNum;
            //Reference: https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java

            stmt.execute("INSERT INTO NUMBERS VALUES ('"+ numRand +"');");

            msgLabel.setText("OUR PROGRAM GENERATED: " + numRand);

            stmt.close();
            conn.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            msgLabel.setText("MIN < MAX & BOTH MUST BE NUMBERS");
            System.out.println(msg);
        }
    }

    public void loadBtn(String url)
    {
        try
        {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String sqlQuery = "SELECT GENNUM FROM NUMBERS";
            ObservableList<Number> numDBlist = FXCollections.observableArrayList();
            ResultSet result = stmt.executeQuery(sqlQuery);

            while (result.next())
            {
                Number number = new Number();
                number.genNum = result.getInt("GENNUM");
                numDBlist.add(number);
                msgLabel.setText("THE NUMBER HAS BEEN LOADED BELOW");
            }
            numListView.setItems(numDBlist);

            stmt.close();
            conn.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            msgLabel.setText("TABLE NOT LOADED");
            System.out.println(msg);
        }
    }

    private void dropBtn(String url)
    {
        try
        {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE NUMBERS;");
            numListView.getItems().clear();
            msgLabel.setText("PLEASE INPUT");
            minText.clear();
            maxText.clear();
            stmt.close();
            conn.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            System.out.println(msg);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        runBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                runBtn(AWS_URL);
            }
        });

        loadBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                loadBtn(AWS_URL);
            }
        });

        dropBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                dropBtn(AWS_URL);
            }
        });
    };
}

