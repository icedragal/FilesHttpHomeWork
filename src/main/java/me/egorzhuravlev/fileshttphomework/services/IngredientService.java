package me.egorzhuravlev.fileshttphomework.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.egorzhuravlev.fileshttphomework.model.Ingredient;
import org.springframework.beans.factory.annotation.Value;
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
public class IngredientService {

    private final Map<Long, Ingredient> ingredients = new HashMap<>();
    private long idGenerator = 1;
    private final Path pathToFile;
    private final ObjectMapper objectMapper;

    public IngredientService(@Value("${application.path.to.ingredients}") String path) {
        this.pathToFile = Paths.get(path);
        this.objectMapper = new ObjectMapper();
    }
    @PostConstruct
    public void init() {
        try {
            Map<Long, Ingredient> fromFile = objectMapper.readValue(Files.readAllBytes(pathToFile), new TypeReference<>(){});
            ingredients.putAll(fromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() {
        try {
            byte[] data = objectMapper.writeValueAsBytes(ingredients);
            Files.write(pathToFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ingredient add(Ingredient ingredient){
        ingredients.put(idGenerator++, ingredient);
        writeToFile();
        return ingredient;
    }

    public Optional<Ingredient> get(long id){
        return Optional.ofNullable(ingredients.get(id));
    }

    public Optional<Ingredient> update(long id, Ingredient ingredient) {
        Optional<Ingredient> result =  Optional.ofNullable(ingredients.replace(id, ingredient));
        writeToFile();
        return result;
    }

    public Optional<Ingredient> delete(long id) {
        Optional<Ingredient> result =  Optional.ofNullable(ingredients.remove(id));
        writeToFile();
        return result;
    }

    public Map<Long, Ingredient> getAll() {
        return new HashMap<>(ingredients);
    }
}
