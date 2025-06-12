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
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

@Service
public class PictogramaService {

    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PictogramaConCategorias crearDesdeInputDTO(PictogramaConCategoriasInput input) {
        Pictograma pictograma = new Pictograma();
        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());
        pictograma.setUsuario(null);
        

        // ✅ Guardamos el pictograma sin necesidad de asociar categorías directamente
        Pictograma guardado = pictogramaRepository.save(pictograma);

        // ✅ Luego se podrían asociar desde el lado de Categoria si hicieras edición en esas entidades
        return convertirADTO(guardado);
    }

    public PictogramaConCategorias actualizarDesdeInput(Long id, PictogramaConCategoriasInput input) {
        Pictograma pictograma = pictogramaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());

        Pictograma actualizado = pictogramaRepository.save(pictograma);

        // ✅ Actualizar relación con categorías
        actualizarCategoriasDePictograma(actualizado.getId(), input.getCategorias());

        return convertirADTO(actualizado);
    }


    public PictogramaConCategorias obtenerPictogramaConCategorias(Long id) {
        Pictograma pictograma = pictogramaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        return convertirADTO(pictograma);
    }

    public List<PictogramaConCategorias> obtenerTodosConCategorias() {
        List<Pictograma> pictogramas = pictogramaRepository.findAllGenerales();
        return convertirListaADTO(pictogramas);
    }


    public List<PictogramaConCategorias> obtenerPictogramasDeUsuarioConCategorias(Long usuarioId) {
        List<Pictograma> pictogramas = pictogramaRepository.findAllPersonalizados(usuarioId);
        return convertirListaADTO(pictogramas);
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

    private List<PictogramaConCategorias> convertirListaADTO(List<Pictograma> lista) {
        List<PictogramaConCategorias> resultado = new ArrayList<>();
        for (Pictograma p : lista) {
            resultado.add(convertirADTO(p));
        }
        return resultado;
    }

    private PictogramaConCategorias convertirADTO(Pictograma p) {
        List<CategoriaSimple> categoriasDTO = new ArrayList<>();

        List<Categoria> categorias = categoriaRepository.findByPictogramaId(p.getId());
        for (Categoria c : categorias) {
            Long usuarioIdCategoria = null;
            if (c.getUsuario() != null) {
                usuarioIdCategoria = c.getUsuario().getId();
            }
            categoriasDTO.add(new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCategoria));
        }

        Long usuarioId = (p.getUsuario() != null) ? p.getUsuario().getId() : null;

        return new PictogramaConCategorias(
            p.getId(),
            p.getNombre(),
            p.getImagen(),
            p.getTipo(),
            usuarioId,
            categoriasDTO
        );
    }

    public List<PictogramaSimple> obtenerPictogramasPorIds(List<Long> ids) {
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Long id : ids) {
            pictogramaRepository.findById(id).ifPresent(p -> {
                resultado.add(convertirASimple(p));
            });
        }

        return resultado;
    }

    private PictogramaSimple convertirASimple(Pictograma p) {
        PictogramaSimple dto = new PictogramaSimple();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setImagen(p.getImagen());
        return dto;
    }
    
    public PictogramaConCategorias crearPictogramaUsuario(Long usuarioId, PictogramaConCategoriasInput input) {
        Pictograma pictograma = new Pictograma();
        pictograma.setNombre(input.getNombre());
        pictograma.setTipo(input.getTipo());
        pictograma.setImagen(input.getImagen());

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        pictograma.setUsuario(usuario);
        pictograma = pictogramaRepository.save(pictograma);

        if (input.getCategorias() != null && !input.getCategorias().isEmpty()) {
            List<Categoria> categorias = categoriaRepository.findAllById(input.getCategorias());
            for (Categoria categoria : categorias) {
                categoria.getPictogramas().add(pictograma);
            }
            categoriaRepository.saveAll(categorias);
        }

        return convertirADTO(pictograma);
    }


    public void actualizarCategoriasDePictograma(Long pictogramaId, List<Long> nuevasCategoriaIds) {
        // 1. Buscar el pictograma
        Pictograma pictograma = pictogramaRepository.findById(pictogramaId)
            .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        // 2. Eliminar el pictograma de todas las categorías actuales
        List<Categoria> categoriasActuales = categoriaRepository.findByPictogramaId(pictogramaId);
        for (Categoria categoria : categoriasActuales) {
            categoria.getPictogramas().removeIf(p -> p.getId().equals(pictogramaId));
            categoriaRepository.save(categoria);
        }

        // 3. Añadir el pictograma a las nuevas categorías
        for (Long nuevaId : nuevasCategoriaIds) {
            categoriaRepository.findById(nuevaId).ifPresent(cat -> {
                if (!cat.getPictogramas().contains(pictograma)) {
                    cat.getPictogramas().add(pictograma);
                    categoriaRepository.save(cat);
                }
            });
        }
    }

    public List<PictogramaSimple> obtenerPictogramasVisibles(Long usuarioId) {
        List<Pictograma> pictogramas = pictogramaRepository.findPictogramasVisiblesParaUsuario(usuarioId);
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Pictograma pictograma : pictogramas) {
            resultado.add(convertirASimple(pictograma));
        }

        return resultado;
    }

    public List<PictogramaSimple> obtenerPictogramasPorCategoria(Long categoriaId, Long usuarioId) {
        List<Pictograma> pictos = pictogramaRepository.obtenerPictogramasDeCategoriaPorUsuario(categoriaId, usuarioId);
        List<PictogramaSimple> resultado = new ArrayList<>();

        for (Pictograma pictograma : pictos) {
            resultado.add(convertirASimple(pictograma));
        }

        return resultado;
    }
}
