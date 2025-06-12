package com.example.app.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.ConfiguracionSimple; // ¡Importa tu nuevo DTO!
import com.example.app.model.Configuracion; // Necesario si mantienes los métodos POST/DELETE que operan con la entidad completa
import com.example.app.model.TipoVoz;
import com.example.app.service.ConfiguracionService;

@RestController
@RequestMapping("/configuraciones")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ConfiguracionSimple> obtenerPorUsuario(@PathVariable Long usuarioId) {
        System.out.println(">>> DEBUG ConfiguracionController: Solicitud GET /configuraciones/usuario/" + usuarioId);
        Optional<ConfiguracionSimple> configuracionSimple = configuracionService.getConfiguracionSimpleByUsuarioId(usuarioId);
        
        if (configuracionSimple.isPresent()) {
            System.out.println(">>> DEBUG ConfiguracionController: Se encontró configuración. Devolviendo 200 OK.");
            return ResponseEntity.ok(configuracionSimple.get());
        } else {
            System.out.println(">>> DEBUG ConfiguracionController: No se encontró configuración. Devolviendo 404 Not Found.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Configuracion> crearOActualizar(@RequestBody Configuracion configuracion) {
        Configuracion guardada = configuracionService.guardarConfiguracion(configuracion);
        return ResponseEntity.ok(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Configuracion> actualizar(@PathVariable Integer id, @RequestBody Configuracion configuracionActualizada) {
        // Aseguramos que la configuración tenga el ID correcto
        configuracionActualizada.setId(id);
        Configuracion actualizada = configuracionService.guardarConfiguracion(configuracionActualizada);
        return ResponseEntity.ok(actualizada);
    }
    
    @GetMapping("/tipos-voz")
    public ResponseEntity<TipoVoz[]> obtenerTiposVoz() {
        return ResponseEntity.ok(TipoVoz.values());
    }


}