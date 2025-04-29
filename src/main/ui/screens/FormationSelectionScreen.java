package main.ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Formation;

import java.util.List;
import java.util.function.Consumer;

public class FormationSelectionScreen extends VBox {

    public FormationSelectionScreen(List<Formation> opciones, Consumer<Formation> onFormationSelected) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(40);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #333;");

        Label titulo = new Label("Escoge tu formación");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setTextFill(Color.WHITE);
        this.getChildren().add(titulo);

        VBox botonesBox = new VBox(20);
        botonesBox.setAlignment(Pos.CENTER);

        for (Formation formacion : opciones) {
            Button btn = new Button(formacion.getName());
            btn.setPrefWidth(400);
            btn.setPrefHeight(50);
            btn.setFont(Font.font(18));

            btn.setOnAction(e -> onFormationSelected.accept(formacion));

            botonesBox.getChildren().add(btn);
        }

        this.getChildren().add(botonesBox);
    }
}