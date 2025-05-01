package controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import model.Pictograma;
import model.Usuario;
import service.PictogramaOcultoService;
import service.PictogramaService;
import service.UsuarioService;

@RestController
@RequestMapping("/pictogramas-ocultos")
public class PictogramaOcultoController {

    @Autowired
    private PictogramaOcultoService pictogramaOcultoService;

    @Autowired
    private PictogramaService pictogramaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/ocultar")
    public ResponseEntity<String> ocultarPictograma(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
        Pictograma pictograma = pictogramaService.obtenerPorId(pictogramaId.intValue())
                .orElse(null);
        Usuario usuario = usuarioService.obtenerPorId(usuarioId)
                .orElse(null);

        if (pictograma == null || usuario == null) {
            return ResponseEntity.badRequest().body("Usuario o pictograma no encontrados");
        }

        boolean ocultado = pictogramaOcultoService.ocultarPictograma(pictograma, usuario);
        if (!ocultado) {
            return ResponseEntity.badRequest().body("Ya estaba oculto");
        }

        return ResponseEntity.ok("Pictograma ocultado");
    }

    @DeleteMapping("/desocultar")
    public ResponseEntity<String> desocultarPictograma(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
        Pictograma pictograma = pictogramaService.obtenerPorId(pictogramaId.intValue())
                .orElse(null);
        Usuario usuario = usuarioService.obtenerPorId(usuarioId)
                .orElse(null);

        if (pictograma == null || usuario == null) {
            return ResponseEntity.badRequest().body("Usuario o pictograma no encontrados");
        }

        boolean eliminado = pictogramaOcultoService.desocultarPictograma(pictograma, usuario);
        if (!eliminado) {
            return ResponseEntity.badRequest().body("No estaba oculto");
        }

        return ResponseEntity.ok("Pictograma desocultado");
    }
}
