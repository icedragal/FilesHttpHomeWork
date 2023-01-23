package me.egorzhuravlev.fileshttphomework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.egorzhuravlev.fileshttphomework.model.Recipe;
import me.egorzhuravlev.fileshttphomework.services.RecipeService;
import me.egorzhuravlev.fileshttphomework.services.ValidateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Рецепты", description = "CRUD-операции для рецептов")
public class RecipeController {
    private final RecipeService recipeService;
    private final ValidateService validateService;
    public RecipeController(RecipeService recipeService, ValidateService validateService) {
        this.recipeService = recipeService;
        this.validateService = validateService;
    }
    @Operation(summary = "Добавление рецепта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Добавление прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @PostMapping
    public ResponseEntity<String> add(@RequestBody Recipe recipe){
        if(validateService.isNotValid(recipe)){
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(recipeService.add(recipe));
    }

    @Operation(summary = "Получение рецепта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Получение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> get(@PathVariable long id) {
        return ResponseEntity.of(recipeService.get(id));
    }

    @Operation(summary = "Изменение рецепта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изменение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable long id,
                                 @RequestBody Recipe recipe){
        if(validateService.isNotValid(recipe)){
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.of(recipeService.update(id, recipe));
    }

    @Operation(summary = "Удаление рецепта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Удаление прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Recipe> delete(@PathVariable long id){
        return ResponseEntity.of(recipeService.delete(id));
    }

    @Operation(summary = "Получение всех рецептов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Получение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "")
    })
    @GetMapping()
    public Map<Long, Recipe> getAll() {
        return recipeService.getAll();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(){
        byte[] data = recipeService.export();
        if (data == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok()
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recipes.json\"")
                .body(data);
    }

    @PostMapping("/import")
    public void importData(@RequestParam("file")MultipartFile multipartFile){
        try {
            recipeService.importData(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
