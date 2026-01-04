package withAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class XMLJSONConverterAPI extends Application {
    
    private TextArea inputArea;
    private TextArea outputArea;
    private Label statusLabel;
    private ComboBox<String> conversionMode;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("XML ⇄ JSON Converter Pro");

        // Layout principal avec dégradé
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

        // En-tête moderne
        VBox header = createHeader();
        root.setTop(header);

        // Centre avec les zones de texte
        HBox centerContent = createCenterContent();
        root.setCenter(centerContent);

        // Barre de contrôle en bas
        VBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);

        Scene scene = new Scene(root, 1400, 800);
        scene.getStylesheets().add(getStylesheet());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Animation d'entrée
        FadeTransition fade = new FadeTransition(Duration.millis(800), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 30, 20, 30));
        header.setAlignment(Pos.CENTER);

        Label title = new Label("XML ⇄ JSON Converter ⚡");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; " +
                      "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        Label subtitle = new Label("Conversion puissante et élégante avec API");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9);");

        // Mode de conversion
        HBox modeBox = new HBox(15);
        modeBox.setAlignment(Pos.CENTER);
        
        Label modeLabel = new Label("Mode:");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        conversionMode = new ComboBox<>();
        conversionMode.getItems().addAll("XML → JSON", "JSON → XML");
        conversionMode.setValue("XML → JSON");
        conversionMode.setStyle("-fx-background-color: white; -fx-font-size: 14px; " +
                               "-fx-background-radius: 20; -fx-padding: 8 20;");
        
        modeBox.getChildren().addAll(modeLabel, conversionMode);

        header.getChildren().addAll(title, subtitle, modeBox);
        return header;
    }

    private HBox createCenterContent() {
        HBox content = new HBox(20);
        content.setPadding(new Insets(20, 30, 20, 30));
        content.setAlignment(Pos.CENTER);

        // Zone d'entrée
        VBox inputBox = createTextAreaBox("📥 Entrée", true);
        inputArea = (TextArea) ((VBox) inputBox.getChildren().get(1)).getChildren().get(0);

        // Zone de sortie
        VBox outputBox = createTextAreaBox("📤 Sortie", false);
        outputArea = (TextArea) ((VBox) outputBox.getChildren().get(1)).getChildren().get(0);

        HBox.setHgrow(inputBox, Priority.ALWAYS);
        HBox.setHgrow(outputBox, Priority.ALWAYS);

        content.getChildren().addAll(inputBox, outputBox);
        return content;
    }

    private VBox createTextAreaBox(String title, boolean isInput) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                    "-fx-background-radius: 20; -fx-padding: 20; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        Label label = new Label(title);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        TextArea textArea = new TextArea();
        textArea.setPromptText(isInput ? "Collez votre " + (title.contains("Entrée") ? "XML ou JSON" : "") + " ici..." : "Résultat de la conversion...");
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; " +
                         "-fx-font-size: 13px; -fx-background-color: #f8f9fa; " +
                         "-fx-background-radius: 10; -fx-border-radius: 10; " +
                         "-fx-border-color: #e0e0e0; -fx-border-width: 1;");
        
        if (!isInput) {
            textArea.setEditable(false);
        }

        VBox.setVgrow(textArea, Priority.ALWAYS);
        
        VBox textContainer = new VBox(textArea);
        VBox.setVgrow(textContainer, Priority.ALWAYS);
        
        box.getChildren().addAll(label, textContainer);
        VBox.setVgrow(box, Priority.ALWAYS);
        
        return box;
    }

    private VBox createBottomControls() {
        VBox bottom = new VBox(15);
        bottom.setPadding(new Insets(20, 30, 30, 30));
        bottom.setAlignment(Pos.CENTER);

        // Boutons d'action
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button convertBtn = createStyledButton("🚀 Convertir", "#4CAF50");
        Button clearBtn = createStyledButton("🗑️ Effacer", "#f44336");
        Button loadBtn = createStyledButton("📂 Charger", "#2196F3");
        Button saveBtn = createStyledButton("💾 Sauvegarder", "#FF9800");

        convertBtn.setOnAction(e -> convert());
        clearBtn.setOnAction(e -> clearAll());
        loadBtn.setOnAction(e -> loadFile());
        saveBtn.setOnAction(e -> saveFile());

        buttonBox.getChildren().addAll(convertBtn, clearBtn, loadBtn, saveBtn);

        // Statut
        statusLabel = new Label("✨ Prêt à convertir");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; " +
                           "-fx-background-color: rgba(0,0,0,0.3); " +
                           "-fx-padding: 10 20; -fx-background-radius: 20;");

        bottom.getChildren().addAll(buttonBox, statusLabel);
        return bottom;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                    "-fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-font-weight: bold; -fx-padding: 12 30; " +
                    "-fx-background-radius: 25; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);");
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x: 1; -fx-scale-y: 1;"));
        
        return btn;
    }

    private void convert() {
        String input = inputArea.getText().trim();
        
        if (input.isEmpty()) {
            showStatus("⚠️ Veuillez entrer du contenu", "#FF9800");
            return;
        }

        try {
            String result;
            if (conversionMode.getValue().equals("XML → JSON")) {
                result = xmlToJson(input);
            } else {
                result = jsonToXml(input);
            }
            outputArea.setText(result);
            showStatus("✅ Conversion réussie!", "#4CAF50");
        } catch (Exception e) {
            outputArea.setText("❌ Erreur: " + e.getMessage());
            showStatus("❌ Erreur de conversion", "#f44336");
        }
    }

    @SuppressWarnings("deprecation")
	private String xmlToJson(String xml) {
        org.json.JSONObject jsonObject = XML.toJSONObject(xml);
        return gson.toJson(new JsonParser().parse(jsonObject.toString()));
    }

    private String jsonToXml(String json) {
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        return XML.toString(jsonObject);
    }

    private void clearAll() {
        inputArea.clear();
        outputArea.clear();
        showStatus("🧹 Zones effacées", "#2196F3");
    }

    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un fichier");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Fichiers XML/JSON", "*.xml", "*.json"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(inputArea.getScene().getWindow());
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                inputArea.setText(content);
                showStatus("📂 Fichier chargé: " + file.getName(), "#4CAF50");
            } catch (IOException e) {
                showStatus("❌ Erreur de chargement", "#f44336");
            }
        }
    }

    private void saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le résultat");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"),
            new FileChooser.ExtensionFilter("Fichiers XML", "*.xml")
        );
        
        File file = fileChooser.showSaveDialog(outputArea.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(outputArea.getText());
                showStatus("💾 Fichier sauvegardé: " + file.getName(), "#4CAF50");
            } catch (IOException e) {
                showStatus("❌ Erreur de sauvegarde", "#f44336");
            }
        }
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle(statusLabel.getStyle().replaceAll("-fx-background-color: [^;]+", 
                                                                "-fx-background-color: " + color));
    }

    private String getStylesheet() {
        return "data:text/css," +
               ".text-area { -fx-background-insets: 0; } " +
               ".text-area .content { -fx-background-color: #f8f9fa; }";
    }

    public static void main(String[] args) {
        launch(args);
    }
}