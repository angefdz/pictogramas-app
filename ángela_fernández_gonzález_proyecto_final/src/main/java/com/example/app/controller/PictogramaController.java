package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.PictogramaConCategorias;
import com.example.app.dto.PictogramaConCategoriasInput;
import com.example.app.dto.PictogramaSimple;
import com.example.app.model.Usuario;
import com.example.app.service.PictogramaService;
import com.example.app.service.UsuarioService;

@RestController
@RequestMapping("/pictogramas")
public class PictogramaController {

    @Autowired
    private PictogramaService pictogramaService;

    @Autowired
    private UsuarioService usuarioService;

    private String getCorreoAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
        	System.out.println("El correo de los putos cojones es: "+usuario.getEmail());
            return usuario.getEmail();
        }

        return principal.toString();
    }
    
    
    @PostMapping("/general")
    public ResponseEntity<PictogramaConCategorias> crearPictogramaGeneral(
            @RequestBody PictogramaConCategoriasInput input
    ) {
    	System.out.println("Entra esto????");
        PictogramaConCategorias creado = pictogramaService.crearPictogramaUsuario(null, input);
        return ResponseEntity.ok(creado);
    }


    @PostMapping
    public ResponseEntity<PictogramaConCategorias> createPictograma(@RequestBody PictogramaConCategoriasInput input) {
        String correo = getCorreoAutenticado();
        System.out.println("El correo de los putísimos cojones estoy harta: "+ correo);
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
        System.out.println("El usuario de los putísimos cojones es: "+usuarioId);
        return ResponseEntity.ok(pictogramaService.crearDesdeInputDTO(input,usuarioId));
    }


    @PostMapping("/yo")
    public ResponseEntity<PictogramaConCategorias> createPictogramaUsuario(@RequestBody PictogramaConCategoriasInput input) {
        String correo = getCorreoAutenticado();
        System.out.println("El correo de los putísimos cojones estoy harta: "+ correo);
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
        System.out.println("El usuario de los putísimos cojones es: "+usuarioId);
        System.out.println("No entiendo nada");
        return ResponseEntity.ok(pictogramaService.crearDesdeInputDTO(input,usuarioId));
    }

    @GetMapping("/generales")
    public ResponseEntity<List<PictogramaConCategorias>> getPictogramasGenerales() {
    	System.out.println("ESSTOY ENTRANDO");
        List<PictogramaConCategorias> pictogramas = pictogramaService.obtenerTodosConCategorias();
        if (pictogramas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pictogramas);
    }
    
    @GetMapping("/generales/nombres")
    public ResponseEntity<List<String>> getNombresPictogramasGenerales() {
        List<String> nombres = pictogramaService.obtenerNombresPictogramasGenerales();
        return ResponseEntity.ok(nombres);
    }


    
    @GetMapping("/{id}")
    public ResponseEntity<PictogramaConCategorias> getPictogramaById(@PathVariable Long id) {
        String correo = getCorreoAutenticado();
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

        return ResponseEntity.ok(pictogramaService.obtenerPictogramaConCategorias(id, usuarioId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PictogramaConCategorias> updatePictograma(
            @PathVariable Long id,
            @RequestBody PictogramaConCategoriasInput input) {
        
        String correo = getCorreoAutenticado();
        Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);

        PictogramaConCategorias actualizado = pictogramaService.actualizarDesdeInput(idAutenticado,id, input);
        return ResponseEntity.ok(actualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePictograma(@PathVariable Long id) {
        boolean eliminado = pictogramaService.eliminarPictograma(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarios/{id}")
    public ResponseEntity<PictogramaConCategorias> crearPictogramaUsuario(
            @PathVariable Long id,
            @RequestBody PictogramaConCategoriasInput input
    ) {
        try {
        	System.out.println("Aquí?");
            PictogramaConCategorias creado = pictogramaService.crearPictogramaUsuario(id, input);
            return ResponseEntity.ok(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}/con-categorias")
    public ResponseEntity<PictogramaConCategorias> getPictogramaConCategorias(@PathVariable Long id) {
        String correo = getCorreoAutenticado();
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

        PictogramaConCategorias dto = pictogramaService.obtenerPictogramaConCategorias(id, usuarioId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/por-ids")
    public ResponseEntity<List<PictogramaSimple>> obtenerPictogramasPorIds(@RequestBody List<Long> ids) {
        List<PictogramaSimple> pictogramas = pictogramaService.obtenerPictogramasPorIds(ids);
        return ResponseEntity.ok(pictogramas);
    }

    @GetMapping
    public ResponseEntity<List<PictogramaSimple>> getPictogramasVisibles() {
        try {
        	System.out.println("No esta ni siquiera entrando");
            String correo = getCorreoAutenticado();
            Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
            System.out.println("Parece que no se llega a ejecutar nunca");
            List<PictogramaSimple> pictogramas = pictogramaService.obtenerPictogramasVisibles(usuarioId);
            if (pictogramas.isEmpty()) {
            	System.out.println("Estoy vacioooooo");
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pictogramas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/por-categoria/{categoriaId}")
    public ResponseEntity<List<PictogramaSimple>> getPictogramasPorCategoria(
        @PathVariable Long categoriaId) {

        String correo = getCorreoAutenticado();
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

        List<PictogramaSimple> pictogramas = pictogramaService.obtenerPictogramasPorCategoria(categoriaId, usuarioId);
        
        return ResponseEntity.ok(pictogramas);
    }
    
    


}
