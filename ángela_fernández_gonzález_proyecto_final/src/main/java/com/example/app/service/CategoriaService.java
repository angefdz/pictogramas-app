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
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {
	
	@Autowired
	private PictogramaCategoriaRepository pictogramaCategoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Transactional
    public CategoriaConPictogramas crearDesdeInput(CategoriaConPictogramasInput input) {
        Categoria categoria = new Categoria();
        categoria.setNombre(input.getNombre());
        categoria.setImagen(input.getImagen());
        categoria.setUsuario(null);

        Categoria guardada = categoriaRepository.save(categoria);

        if (input.getPictogramas() != null && !input.getPictogramas().isEmpty()) {
            for (Long pictogramaId : input.getPictogramas()) {
                PictogramaCategoria relacion = new PictogramaCategoria();
                relacion.setCategoria(guardada);
                relacion.setPictograma(pictogramaRepository.buscarPorId(pictogramaId));
                relacion.setUsuario(null); 
                pictogramaCategoriaRepository.save(relacion);
            }
        }

        List<Pictograma> pictosFiltrados = obtenerPictogramasCategoriaParaUsuario(guardada.getId(), null);
        return convertirADTOConPictogramasFiltrados(guardada, pictosFiltrados);
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

    @Transactional
    public Optional<CategoriaConPictogramas> actualizarDesdeInput(Long id, CategoriaConPictogramasInput input) {
        return categoriaRepository.findById(id).map(categoria -> {
            categoria.setNombre(input.getNombre());
            categoria.setImagen(input.getImagen());
            Categoria actualizada = categoriaRepository.save(categoria);

            Long usuarioId = categoria.getUsuario() != null ? categoria.getUsuario().getId() : null;

            pictogramaCategoriaRepository.eliminarRelacionesPorCategoriaYUsuario(id, usuarioId);
            if (input.getPictogramas() != null && !input.getPictogramas().isEmpty()) {
                for (Long pictogramaId : input.getPictogramas()) {
                    PictogramaCategoria relacion = new PictogramaCategoria();
                    relacion.setCategoria(actualizada);
                    relacion.setPictograma(pictogramaRepository.buscarPorId(pictogramaId));
                    relacion.setUsuario(categoria.getUsuario());
                    pictogramaCategoriaRepository.save(relacion);
                }
            }

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
        return pictogramaCategoriaRepository.obtenerPictogramasDeCategoriaPorUsuario(categoriaId, usuarioId);
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
            for (Long pictoId : input.getPictogramas()) {
                Pictograma pictograma = pictogramaRepository.findById(pictoId)
                    .orElseThrow(() -> new RuntimeException("Pictograma no encontrado: " + pictoId));

                PictogramaCategoria relacion = new PictogramaCategoria();
                relacion.setCategoria(guardada);
                relacion.setPictograma(pictograma);
                relacion.setUsuario(usuario); // siempre asigna el usuario

                pictogramaCategoriaRepository.save(relacion);
            }
        }

        // 3. Recuperar pictogramas filtrados por usuario
        List<Pictograma> pictosFiltrados = obtenerPictogramasCategoriaParaUsuario(guardada.getId(), input.getUsuarioId());
        return convertirADTOConPictogramasFiltrados(guardada, pictosFiltrados);
    }


    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public List<Categoria> obtenerCategoriasPorPictograma(Long pictogramaId, Long usuarioId) {
        return pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(pictogramaId, usuarioId);
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
        List<Categoria> categorias = pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(pictogramaId, usuarioId);
        return categorias.stream()
            .map(c -> {
                Long usuarioIdCat = (c.getUsuario() != null) ? c.getUsuario().getId() : null;
                return new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCat);
            })
            .toList();
    }

    @Transactional
    public void actualizarPictogramasRelacionados(Long categoriaId, List<Long> pictogramaIds, Long usuarioId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Eliminar relaciones actuales del usuario con esta categoría
        pictogramaCategoriaRepository.eliminarRelacionesPorCategoriaYUsuario(categoriaId, usuarioId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        for (Long pictogramaId : pictogramaIds) {
            Pictograma pictograma = pictogramaRepository.buscarPorId(pictogramaId);

            PictogramaCategoria nuevaRelacion = new PictogramaCategoria();
            nuevaRelacion.setCategoria(categoria);
            nuevaRelacion.setPictograma(pictograma);
            nuevaRelacion.setUsuario(usuario);

            pictogramaCategoriaRepository.save(nuevaRelacion);
        }
    }


    public List<CategoriaConPictogramas> obtenerCategoriasConPictogramasVisibles(Long usuarioId) {
        System.out.println("PRIMER PASO");
        
        List<Categoria> categorias = categoriaRepository
            .findCategoriasVisiblesParaUsuario(usuarioId);

        System.out.println("Segundo");

        List<CategoriaConPictogramas> resultado = new ArrayList<>();

        for (Categoria categoria : categorias) {
            try {
                List<PictogramaCategoria> relaciones = pictogramaCategoriaRepository
                    .findByCategoriaIdAndUsuarioId(categoria.getId(), usuarioId);
                

                // Aquí puedes añadir el filtrado con Set si quieres (opcional)
                List<PictogramaSimple> pictos = new ArrayList<>();
                for (PictogramaCategoria rel : relaciones) {
                    Pictograma p = rel.getPictograma();
                    PictogramaSimple dto = new PictogramaSimple(
                        p.getId(),
                        p.getNombre(),
                        p.getImagen(),
                        p.getTipo()
                    );
                    pictos.add(dto);
                }
                Long usuarioCatId = (categoria.getUsuario() != null)
                    ? categoria.getUsuario().getId()
                    : null;

                CategoriaConPictogramas dto = new CategoriaConPictogramas(
                    categoria.getId(),
                    categoria.getNombre(),
                    categoria.getImagen(),
                    pictos,
                    usuarioCatId
                );

                resultado.add(dto);
            } catch (Exception e) {
                System.out.println("❌ Error al procesar categoría con ID: " + categoria.getId());
                e.printStackTrace();
            }
        }

        return resultado;
    }


}
