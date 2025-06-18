package com.example.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.dto.CategoriaSimple;
import com.example.app.dto.PictogramaConCategorias;
import com.example.app.dto.PictogramaConCategoriasInput;
import com.example.app.dto.PictogramaSimple;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class PictogramaService {


    @Autowired
    private PictogramaCategoriaRepository pictogramaCategoriaRepository;
    
    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PictogramaConCategorias crearDesdeInputDTO(PictogramaConCategoriasInput input, Long usuarioId) {
        Pictograma pictograma = new Pictograma();
        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());

        if (usuarioId != null) {
            usuarioRepository.buscarPorId(usuarioId).ifPresent(pictograma::setUsuario);
        } else {
            pictograma.setUsuario(null);
        }

        Pictograma guardado = pictogramaRepository.save(pictograma);
        return convertirADTO(guardado, usuarioId);
    }

    public PictogramaConCategorias actualizarDesdeInput(Long usuarioid, Long id, PictogramaConCategoriasInput input) {
        Pictograma pictograma = pictogramaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());

        Pictograma actualizado = pictogramaRepository.save(pictograma);

        // ✅ Actualizar relación con categorías
        actualizarCategoriasDePictograma(usuarioid,actualizado.getId(), input.getCategorias());

        return convertirADTO(actualizado,usuarioid);
    }


    public PictogramaConCategorias obtenerPictogramaConCategorias(Long id,Long usuarioId) {
        Pictograma pictograma = pictogramaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        return convertirADTO(pictograma,usuarioId);
    }

    public List<PictogramaConCategorias> obtenerTodosConCategorias() {
        List<Pictograma> pictogramas = pictogramaRepository.findAllGenerales();
        return convertirListaADTO(pictogramas,null);
    }


    public List<PictogramaConCategorias> obtenerPictogramasDeUsuarioConCategorias(Long usuarioId) {
        List<Pictograma> pictogramas = pictogramaRepository.findAllPersonalizados(usuarioId);
        return convertirListaADTO(pictogramas,usuarioId);
    }

    public boolean eliminarPictograma(Long id) {
        Optional<Pictograma> pictogramaOpt = pictogramaRepository.findById(id);
        if (pictogramaOpt.isEmpty()) {
            return false;
        }

        Pictograma pictograma = pictogramaOpt.get();

        // Solo permitir eliminar si es personalizado (tiene usuario asociado)
        if (pictograma.getUsuario() == null) {
            return false;
        }

        pictogramaRepository.delete(pictograma);
        return true;
    }


    // Helpers

    private List<PictogramaConCategorias> convertirListaADTO(List<Pictograma> lista, Long usuarioId) {
        List<PictogramaConCategorias> resultado = new ArrayList<>();
        for (Pictograma p : lista) {
            resultado.add(convertirADTO(p,usuarioId));
        }
        return resultado;
    }

    private PictogramaConCategorias convertirADTO(Pictograma p, Long usuarioIdParaFiltrarRelaciones) {
        List<CategoriaSimple> categoriasDTO = new ArrayList<>();

        // Aunque el pictograma sea general, el usuario sí tiene relaciones propias con categorías
        List<Categoria> categorias = pictogramaCategoriaRepository
            .buscarCategoriasDePictogramaPorUsuario(p.getId(), usuarioIdParaFiltrarRelaciones);

        for (Categoria c : categorias) {
            Long usuarioIdCategoria = (c.getUsuario() != null) ? c.getUsuario().getId() : null;
            categoriasDTO.add(new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCategoria));
        }

        // Este usuario es el creador del pictograma, puede ser null (si es general)
        Long usuarioIdPictograma = (p.getUsuario() != null) ? p.getUsuario().getId() : null;

        return new PictogramaConCategorias(
            p.getId(),
            p.getNombre(),
            p.getImagen(),
            p.getTipo(),
            usuarioIdPictograma,
            categoriasDTO
        );
    }



    public List<PictogramaSimple> obtenerPictogramasPorIds(List<Long> ids) {
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Long id : ids) {
            Pictograma p = pictogramaRepository.buscarPorId(id);
            if (p != null) {
                resultado.add(convertirASimple(p));
            }
        }

        return resultado;
    }


    private PictogramaSimple convertirASimple(Pictograma p) {
        PictogramaSimple dto = new PictogramaSimple();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setImagen(p.getImagen());
        dto.setTipo(p.getTipo());
        return dto;
    }
    
    @Transactional
    public PictogramaConCategorias crearPictogramaUsuario(Long usuarioId, PictogramaConCategoriasInput input) {
        Pictograma pictograma = new Pictograma();
        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        pictograma.setUsuario(usuario);

        pictograma = pictogramaRepository.save(pictograma);

        if (input.getCategorias() != null && !input.getCategorias().isEmpty()) {
            for (Long categoriaId : input.getCategorias()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

                PictogramaCategoria relacion = new PictogramaCategoria();
                relacion.setPictograma(pictograma);
                relacion.setCategoria(categoria);
                relacion.setUsuario(usuario);

                pictogramaCategoriaRepository.save(relacion);
            }
        }

        Long idUsuario = (usuario != null) ? usuario.getId() : null;
        return convertirADTO(pictograma, idUsuario);
    }


    @Transactional
    public void actualizarCategoriasDePictograma(Long usuarioId, Long pictogramaId, List<Long> nuevasCategoriaIds) {
        // Verificamos que el pictograma exista
        Pictograma pictograma = pictogramaRepository.findById(pictogramaId)
            .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar relaciones anteriores del usuario con ese pictograma
        pictogramaCategoriaRepository.eliminarPorUsuarioYPictograma(usuarioId, pictogramaId);

        // Crear nuevas relaciones
        for (Long categoriaId : nuevasCategoriaIds) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            PictogramaCategoria relacion = new PictogramaCategoria();
            relacion.setUsuario(usuario);
            relacion.setPictograma(pictograma);
            relacion.setCategoria(categoria);

            pictogramaCategoriaRepository.save(relacion);
        }
    }


    public List<PictogramaSimple> obtenerPictogramasVisibles(Long usuarioId) {
        List<Pictograma> pictogramas = pictogramaRepository.findPictogramasVisiblesParaUsuario(usuarioId);
        if (pictogramas.isEmpty()) {
        	System.out.println("no he encontrado nada de nada");
        }else {
        	System.out.println("No se de donde coño viene el error");
        }
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Pictograma pictograma : pictogramas) {
            resultado.add(convertirASimple(pictograma));
        }

        return resultado;
    }

    public List<PictogramaSimple> obtenerPictogramasPorCategoria(Long categoriaId, Long usuarioId) {
        List<Pictograma> pictos = pictogramaCategoriaRepository.obtenerPictogramasDeCategoriaPorUsuario(categoriaId, usuarioId);
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Pictograma pictograma : pictos) {
            resultado.add(convertirASimple(pictograma));
        }

        return resultado;
    }

    public List<String> obtenerNombresPictogramasGenerales() {
        return pictogramaRepository.findAllGenerales()
            .stream()
            .map(Pictograma::getNombre)
            .toList();
    }

}
