package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.PictogramaConCategorias;
import com.example.app.service.PictogramaOcultoService;

@RestController
@RequestMapping("/pictogramas-ocultos")
public class PictogramaOcultoController {

    @Autowired
    private PictogramaOcultoService pictogramaOcultoService;

    @PostMapping("/ocultar")
    public ResponseEntity<String> ocultarPictograma(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
    	System.out.println(usuarioId);
        boolean resultado = pictogramaOcultoService.ocultarPorIds(pictogramaId, usuarioId);
        if (!resultado) {
            return ResponseEntity.badRequest().body("Pictograma ya estaba oculto o no existe");
        }
        return ResponseEntity.ok("Pictograma ocultado correctamente");
    }

    @DeleteMapping("/desocultar")
    public ResponseEntity<String> desocultarPictograma(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
        boolean resultado = pictogramaOcultoService.desocultarPorIds(pictogramaId, usuarioId);
        if (!resultado) {
            return ResponseEntity.badRequest().body("Pictograma no estaba oculto o no existe");
        }
        return ResponseEntity.ok("Pictograma desocultado correctamente");
    }
    
    @GetMapping("/es-oculto")
    public ResponseEntity<Boolean> estaOculto(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
        boolean resultado = pictogramaOcultoService.estaOculto(pictogramaId, usuarioId);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PictogramaConCategorias>> getPictogramasOcultos(@PathVariable Long usuarioId) {
    	System.out.println("Controller: "+usuarioId);
        List<PictogramaConCategorias> ocultos = pictogramaOcultoService.obtenerPictogramasOcultos(usuarioId);
        return ResponseEntity.ok(ocultos);
    }

}
