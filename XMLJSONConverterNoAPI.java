package withoutAPI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class XMLJSONConverterNoAPI extends Application {
    
    private TextArea inputArea;
    private TextArea outputArea;
    private Label statusLabel;
    private ComboBox<String> conversionMode;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("XML ⇄ JSON Converter Pro");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

        VBox header = createHeader();
        root.setTop(header);

        HBox centerContent = createCenterContent();
        root.setCenter(centerContent);

        VBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);

        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fade = new FadeTransition(Duration.millis(800), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 30, 20, 30));
        header.setAlignment(Pos.CENTER);

        Label title = new Label(" XML ⇄ JSON Converter ⚡");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; " +
                      "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        Label subtitle = new Label("Conversion native sans dépendances externes");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9);");

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

        VBox inputBox = createTextAreaBox("📥 Entrée", true);
        inputArea = (TextArea) ((VBox) inputBox.getChildren().get(1)).getChildren().get(0);

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

    private String xmlToJson(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        
        Element root = doc.getDocumentElement();
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        elementToJson(root, json, 1);
        json.append("\n}");
        
        return json.toString();
    }

    private void elementToJson(Element element, StringBuilder json, int indent) {
        String indentStr = "  ".repeat(indent);
        json.append(indentStr).append("\"").append(element.getNodeName()).append("\": ");
        
        NodeList children = element.getChildNodes();
        List<Element> childElements = new ArrayList<>();
        StringBuilder textContent = new StringBuilder();
        
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                childElements.add((Element) child);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent().trim();
                if (!text.isEmpty()) {
                    textContent.append(text);
                }
            }
        }
        
        NamedNodeMap attributes = element.getAttributes();
        
        if (childElements.isEmpty() && attributes.getLength() == 0) {
            if (textContent.length() > 0) {
                json.append("\"").append(escapeJson(textContent.toString())).append("\"");
            } else {
                json.append("\"\"");
            }
        } else {
            json.append("{\n");
            
            if (attributes.getLength() > 0) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    json.append(indentStr).append("  \"@").append(attr.getNodeName()).append("\": ");
                    json.append("\"").append(escapeJson(attr.getNodeValue())).append("\"");
                    if (i < attributes.getLength() - 1 || childElements.size() > 0 || textContent.length() > 0) {
                        json.append(",");
                    }
                    json.append("\n");
                }
            }
            
            if (textContent.length() > 0 && childElements.isEmpty()) {
                json.append(indentStr).append("  \"#text\": \"").append(escapeJson(textContent.toString())).append("\"");
            } else if (childElements.size() > 0) {
                Map<String, List<Element>> groupedChildren = new HashMap<>();
                for (Element child : childElements) {
                    groupedChildren.computeIfAbsent(child.getNodeName(), k -> new ArrayList<>()).add(child);
                }
                
                int count = 0;
                for (Map.Entry<String, List<Element>> entry : groupedChildren.entrySet()) {
                    List<Element> elements = entry.getValue();
                    if (elements.size() == 1) {
                        elementToJson(elements.get(0), json, indent + 1);
                    } else {
                        json.append(indentStr).append("  \"").append(entry.getKey()).append("\": [\n");
                        for (int i = 0; i < elements.size(); i++) {
                            json.append(indentStr).append("    ");
                            Element child = elements.get(i);
                            json.append("{\n");
                            NodeList grandChildren = child.getChildNodes();
                            for (int j = 0; j < grandChildren.getLength(); j++) {
                                Node grandChild = grandChildren.item(j);
                                if (grandChild.getNodeType() == Node.ELEMENT_NODE) {
                                    elementToJson((Element) grandChild, json, indent + 2);
                                }
                            }
                            json.append("\n").append(indentStr).append("    }");
                            if (i < elements.size() - 1) json.append(",");
                            json.append("\n");
                        }
                        json.append(indentStr).append("  ]");
                    }
                    if (++count < groupedChildren.size()) json.append(",");
                    json.append("\n");
                }
            }
            
            json.append(indentStr).append("}");
        }
    }

    private String jsonToXml(String json) throws Exception {
        json = json.trim();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        
        parseJsonToXml(json, xml, 0);
        
        return xml.toString();
    }

    private void parseJsonToXml(String json, StringBuilder xml, int depth) {
        json = json.trim();
        String indent = "  ".repeat(depth);
        
        if (json.startsWith("{")) {
            json = json.substring(1, json.length() - 1).trim();
            String[] pairs = splitJson(json);
            
            if (pairs.length > 0) {
                String firstPair = pairs[0].trim();
                int colonIdx = firstPair.indexOf(':');
                if (colonIdx > 0) {
                    String key = firstPair.substring(0, colonIdx).trim();
                    key = key.replaceAll("^\"|\"$", "");
                    String rootTag = key.replaceAll("^@", "");
                    
                    xml.append(indent).append("<").append(rootTag).append(">\n");
                    
                    for (String pair : pairs) {
                        processPair(pair, xml, depth + 1);
                    }
                    
                    xml.append(indent).append("</").append(rootTag).append(">\n");
                }
            }
        }
    }

    private void processPair(String pair, StringBuilder xml, int depth) {
        pair = pair.trim();
        int colonIdx = pair.indexOf(':');
        if (colonIdx < 0) return;
        
        String key = pair.substring(0, colonIdx).trim().replaceAll("^\"|\"$", "");
        String value = pair.substring(colonIdx + 1).trim();
        String indent = "  ".repeat(depth);
        
        if (key.equals("#text")) {
            xml.append(indent).append(value.replaceAll("^\"|\"$", "")).append("\n");
        } else if (key.startsWith("@")) {
            // Attribute - skip for now in simple implementation
        } else {
            if (value.startsWith("{")) {
                xml.append(indent).append("<").append(key).append(">\n");
                parseNestedObject(value, xml, depth + 1);
                xml.append(indent).append("</").append(key).append(">\n");
            } else if (value.startsWith("[")) {
                parseArray(key, value, xml, depth);
            } else {
                String cleanValue = value.replaceAll("^\"|\"$", "").replaceAll(",$", "");
                xml.append(indent).append("<").append(key).append(">")
                   .append(cleanValue)
                   .append("</").append(key).append(">\n");
            }
        }
    }

    private void parseNestedObject(String json, StringBuilder xml, int depth) {
        json = json.substring(1, json.length() - 1).trim();
        String[] pairs = splitJson(json);
        for (String pair : pairs) {
            processPair(pair, xml, depth);
        }
    }

    private void parseArray(String key, String array, StringBuilder xml, int depth) {
        array = array.substring(1, array.length() - 1).trim();
        String[] items = splitJson(array);
        String indent = "  ".repeat(depth);
        
        for (String item : items) {
            xml.append(indent).append("<").append(key).append(">");
            String cleanItem = item.trim().replaceAll("^\"|\"$", "").replaceAll(",$", "");
            xml.append(cleanItem);
            xml.append("</").append(key).append(">\n");
        }
    }

    private String[] splitJson(String json) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int braceDepth = 0, bracketDepth = 0;
        boolean inString = false;
        
        for (char c : json.toCharArray()) {
            if (c == '"' && (current.length() == 0 || current.charAt(current.length() - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '{') braceDepth++;
                else if (c == '}') braceDepth--;
                else if (c == '[') bracketDepth++;
                else if (c == ']') bracketDepth--;
            }
            
            if (c == ',' && braceDepth == 0 && bracketDepth == 0 && !inString) {
                parts.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }
        
        return parts.toArray(new String[0]);
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
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

    public static void main(String[] args) {
        launch(args);
    }
}