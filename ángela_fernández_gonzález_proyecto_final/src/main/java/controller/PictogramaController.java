package controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import model.Pictograma;
import service.PictogramaService;

@RestController
@RequestMapping("/pictogramas")
public class PictogramaController {

    @Autowired
    private PictogramaService pictogramaService;

    @PostMapping
    public ResponseEntity<Pictograma> createPictograma(@RequestBody Pictograma p) {
        Pictograma nuevoPictograma = pictogramaService.crearPictograma(p);
        return new ResponseEntity<>(nuevoPictograma, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Pictograma>> getPictogramasDefault() {
        List<Pictograma> pictogramas = pictogramaService.obtenerPictogramasGenerales();
        if (pictogramas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pictogramas, HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuario_id}")
    public ResponseEntity<List<Pictograma>> getPictogramasByUser(@PathVariable long usuario_id) {
        List<Pictograma> pictogramas = pictogramaService.obtenerPictogramasPorUsuario(usuario_id);
        if (pictogramas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pictogramas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pictograma> getPictogramaById(@PathVariable int id) {
        Optional<Pictograma> pictograma = pictogramaService.obtenerPorId(id);
        return pictograma.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pictograma> updatePictograma(@PathVariable int id, @RequestBody Pictograma pictogramaActualizado) {
        Optional<Pictograma> actualizado = pictogramaService.actualizarPictograma(id, pictogramaActualizado);
        return actualizado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePictograma(@PathVariable int id) {
        boolean eliminado = pictogramaService.eliminarPictograma(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
