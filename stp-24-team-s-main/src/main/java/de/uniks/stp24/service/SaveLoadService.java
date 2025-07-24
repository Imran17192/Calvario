package de.uniks.stp24.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.uniks.stp24.model.Gang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SaveLoadService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public String fileName = Constants.DATA_FOLDER_NAME + "/" + "save.json";

    @Inject
    public SaveLoadService() {

    }

    public void setUserId(String userId) {
        fileName = Constants.DATA_FOLDER_NAME + "/" + userId + "save.json";
    }

    public void saveGang(ObservableList<Gang> gang) {
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            if (Files.notExists(Constants.DATA_FOLDER)) {
                Files.createDirectory(Constants.DATA_FOLDER);
            }
            this.objectMapper.writeValue(Paths.get(fileName).toFile(), gang);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<Gang> loadGangs() {
        ObservableList<Gang> gangs = FXCollections.observableArrayList();
        if (Files.exists(Path.of(fileName))) {
            try {
                gangs = FXCollections.observableArrayList(Arrays.asList(objectMapper.readValue(Paths.get(fileName).toFile(), Gang[].class)));
            } catch (IOException e) {
                return gangs;
            }
        }
        return gangs;
    }
}
