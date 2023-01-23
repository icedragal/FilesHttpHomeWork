package me.egorzhuravlev.fileshttphomework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.egorzhuravlev.fileshttphomework.model.Ingredient;
import me.egorzhuravlev.fileshttphomework.services.IngredientService;
import me.egorzhuravlev.fileshttphomework.services.ValidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/ingredient")
@Tag(name = "Ингредиенты", description = "CRUD-операции для ингредиентов")
public class IngredientController {
    private final IngredientService ingredientService;
    private final ValidateService validateService;
    public IngredientController(IngredientService ingredientService, ValidateService validateService) {
        this.ingredientService = ingredientService;
        this.validateService = validateService;
    }

    @Operation(summary = "Добавление ингредиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Добавление прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @PostMapping
    public ResponseEntity<Ingredient> add(@RequestBody Ingredient ingredient) {
        if(validateService.isNotValid(ingredient)){
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ingredientService.add(ingredient));
    }
    @Operation(summary = "Получение ингредиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Получение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> get(@PathVariable long id) {
        return ResponseEntity.of(ingredientService.get(id));
    }

    @Operation(summary = "Изменение ингредиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изменение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> update(@PathVariable long id,
                             @RequestBody Ingredient ingredient){
        if(validateService.isNotValid(ingredient)){
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.of(ingredientService.update(id, ingredient));
    }

    @Operation(summary = "Удаление ингредиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Удаление прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Некоректные параметры")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Ingredient> delete(@PathVariable long id){
        return ResponseEntity.of(ingredientService.delete(id));
    }
    @Operation(summary = "Получение всех ингредиентов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Получение прошло успешно"),
            @ApiResponse(responseCode = "400", description = "")
    })
    @GetMapping()
    public Map<Long, Ingredient> getAll() {
        return ingredientService.getAll();
    }

    @PostMapping("/import")
    public void importData(@RequestParam("file") MultipartFile multipartFile){
        try {
            ingredientService.importData(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
