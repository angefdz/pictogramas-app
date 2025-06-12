package com.example.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.dto.CategoriaConPictogramas;
import com.example.app.dto.CategoriaConPictogramasInput;
import com.example.app.dto.CategoriaSimple;
import com.example.app.dto.CategoriaUsuarioInput;
import com.example.app.dto.PictogramaSimple;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    public CategoriaConPictogramas crearDesdeInput(CategoriaConPictogramasInput input) {
        Categoria categoria = new Categoria();
        categoria.setNombre(input.getNombre());
        categoria.setImagen(input.getImagen());
        categoria.setUsuario(null);

        if (input.getPictogramas() != null && !input.getPictogramas().isEmpty()) {
            List<Pictograma> pictos = pictogramaRepository.findAllById(input.getPictogramas());
            categoria.setPictogramas(pictos);
        }

        Categoria guardada = categoriaRepository.save(categoria);
        return convertirADTOConPictogramasFiltrados(guardada, obtenerPictogramasCategoriaParaUsuario(guardada.getId(), null));
    }

    public List<CategoriaConPictogramas> obtenerCategoriasGeneralesConPictogramas() {
        List<Categoria> categorias = categoriaRepository.findAllGenerales();
        List<CategoriaConPictogramas> resultado = new ArrayList<>();
        for (Categoria c : categorias) {
            List<Pictograma> pictos = obtenerPictogramasCategoriaParaUsuario(c.getId(), null);
            resultado.add(convertirADTOConPictogramasFiltrados(c, pictos));
        }
        return resultado;
    }

    public List<CategoriaConPictogramas> obtenerCategoriasPersonalizadasConPictogramas(Long usuarioId) {
        List<Categoria> categorias = categoriaRepository.findByUsuario_Id(usuarioId);
        List<CategoriaConPictogramas> resultado = new ArrayList<>();
        for (Categoria c : categorias) {
            List<Pictograma> pictos = obtenerPictogramasCategoriaParaUsuario(c.getId(), usuarioId);
            resultado.add(convertirADTOConPictogramasFiltrados(c, pictos));
        }
        return resultado;
    }

    public Optional<CategoriaConPictogramas> obtenerCategoriaConPictogramasOpt(Long id, Long usuarioId) {
        return categoriaRepository.findById(id)
            .map(categoria -> {
                List<Pictograma> pictos = obtenerPictogramasCategoriaParaUsuario(id, usuarioId);
                return convertirADTOConPictogramasFiltrados(categoria, pictos);
            });
    }

    public CategoriaConPictogramas obtenerCategoriaConPictogramas(Long id, Long usuarioId) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        List<Pictograma> pictos = obtenerPictogramasCategoriaParaUsuario(id, usuarioId);
        return convertirADTOConPictogramasFiltrados(categoria, pictos);
    }

    public Optional<CategoriaConPictogramas> actualizarDesdeInput(Long id, CategoriaConPictogramasInput input) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(input.getNombre());
            c.setImagen(input.getImagen());

            if (input.getPictogramas() != null) {
                List<Pictograma> pictos = pictogramaRepository.findAllById(input.getPictogramas());
                c.setPictogramas(pictos);
            } else {
                c.setPictogramas(new ArrayList<>());
            }

            Categoria actualizada = categoriaRepository.save(c);
            Long usuarioId = c.getUsuario() != null ? c.getUsuario().getId() : null;
            List<Pictograma> pictosFiltrados = obtenerPictogramasCategoriaParaUsuario(id, usuarioId);

            return convertirADTOConPictogramasFiltrados(actualizada, pictosFiltrados);
        });
    }

    public boolean eliminarCategoria(Long id) {
        return categoriaRepository.findById(id).map(c -> {
            categoriaRepository.delete(c);
            return true;
        }).orElse(false);
    }

    // ============ MÉTODOS AUXILIARES DTO ============

    private CategoriaConPictogramas convertirADTOConPictogramasFiltrados(Categoria categoria, List<Pictograma> pictogramasFiltrados) {
        List<PictogramaSimple> pictosDTO = pictogramasFiltrados.stream()
            .map(p -> new PictogramaSimple(p.getId(), p.getNombre(), p.getImagen(), p.getTipo()))
            .toList();

        Long usuarioId = null;
        if (categoria.getUsuario() != null) {
            usuarioId = categoria.getUsuario().getId();
        }

        return new CategoriaConPictogramas(
            categoria.getId(),
            categoria.getNombre(),
            categoria.getImagen(),
            pictosDTO,
            usuarioId
        );
    }

    public List<Pictograma> obtenerPictogramasCategoriaParaUsuario(Long categoriaId, Long usuarioId) {
        return pictogramaRepository.obtenerPictogramasDeCategoriaPorUsuario(categoriaId, usuarioId);
    }

    public CategoriaConPictogramas crearCategoriaDeUsuario(CategoriaUsuarioInput input) {
        Categoria categoria = new Categoria();
        categoria.setNombre(input.getNombre());
        categoria.setImagen(input.getImagen());

        Usuario usuario = usuarioRepository.buscarPorId(input.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        categoria.setUsuario(usuario);

        Categoria guardada = categoriaRepository.save(categoria);

        if (input.getPictogramas() != null && !input.getPictogramas().isEmpty()) {
            List<Pictograma> pictos = pictogramaRepository.findAllById(input.getPictogramas());
            guardada.setPictogramas(pictos);
            guardada = categoriaRepository.save(guardada);
        }

        List<Pictograma> pictosFiltrados = obtenerPictogramasCategoriaParaUsuario(guardada.getId(), input.getUsuarioId());
        return convertirADTOConPictogramasFiltrados(guardada, pictosFiltrados);
    }

    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public List<Categoria> obtenerCategoriasPorPictograma(Long pictogramaId) {
        return categoriaRepository.findByPictogramaId(pictogramaId);
    }

    public List<CategoriaSimple> obtenerCategoriasPorIds(List<Long> ids) {
        List<Categoria> entidades = categoriaRepository.findAllById(ids);
        return entidades.stream()
            .map(c -> {
                Long usuarioId = null;
                if (c.getUsuario() != null) {
                    usuarioId = c.getUsuario().getId();
                }
                return new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioId);
            })
            .toList();
    }
    
    public List<CategoriaSimple> obtenerCategoriasVisiblesParaUsuario(Long usuarioId) {
        List<Categoria> categorias = categoriaRepository.findCategoriasVisiblesParaUsuario(usuarioId);
        return categorias.stream()
            .map(c -> {
                Long usuarioIdCat = null;
                if (c.getUsuario() != null) {
                    usuarioIdCat = c.getUsuario().getId();
                }
                return new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCat);
            })
            .toList();
    }

    public List<CategoriaSimple> obtenerCategoriasDePictogramaParaUsuario(Long pictogramaId, Long usuarioId) {
        List<Categoria> categorias = categoriaRepository.findCategoriasPorPictogramaYUsuario(pictogramaId, usuarioId);
        return categorias.stream()
            .map(c -> {
                Long usuarioIdCat = null;
                if (c.getUsuario() != null) {
                    usuarioIdCat = c.getUsuario().getId();
                }
                return new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCat);
            })
            .toList();
    }
}
