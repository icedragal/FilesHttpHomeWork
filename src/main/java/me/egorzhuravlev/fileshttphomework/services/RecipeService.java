package me.egorzhuravlev.fileshttphomework.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.egorzhuravlev.fileshttphomework.model.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RecipeService {
    private final Map<Long, Recipe> recipes = new HashMap<>();
    private long idGenerator = 1;

    private final Path pathToFile;
    private final ObjectMapper objectMapper;

    public RecipeService(@Value("${application.path.to.recipes}") String path) {
        this.pathToFile = Paths.get(path);
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        readFromFile();
    }

    private void readFromFile() {
        try {
            Map<Long, Recipe> fromFile = objectMapper.readValue(Files.readAllBytes(pathToFile), new TypeReference<>() {
            });
            recipes.putAll(fromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() {
        try {
            byte[] data = objectMapper.writeValueAsBytes(recipes);
            Files.write(pathToFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String add(Recipe recipe) {
        recipes.put(idGenerator++, recipe);
        writeToFile();
        return "Добавлен рецепт: \"" + recipe.getTitle() + "\", его ID: " + idGenerator;
    }

    public Optional<Recipe> get(long id) {
        return Optional.ofNullable(recipes.get(id));
    }

    public Optional<Recipe> update(long id, Recipe recipe) {
        Optional<Recipe> result = Optional.ofNullable(recipes.replace(id, recipe));
        writeToFile();
        return result;
    }

    public Optional<Recipe> delete(long id) {
        Optional<Recipe> result = Optional.ofNullable(recipes.remove(id));
        writeToFile();
        return result;
    }

    public Map<Long, Recipe> getAll() {
        return new HashMap<>(recipes);
    }

    @Nullable
    public byte[] export() {
        try {
            return Files.readAllBytes(pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void importData(byte[] data) {
        try {
            Files.write(pathToFile, data);
            readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
