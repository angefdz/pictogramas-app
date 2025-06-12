package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;

public interface PictogramaRepository extends JpaRepository<Pictograma, Long> {

    // Pictogramas generales (los que no tienen usuario asociado)
    @Query("SELECT p FROM Pictograma p WHERE p.usuario IS NULL")
    List<Pictograma> findAllGenerales();

    // Pictogramas personalizados de un usuario concreto
    @Query("SELECT p FROM Pictograma p WHERE p.usuario.id = :usuarioId")
    List<Pictograma> findAllPersonalizados(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Pictograma p WHERE p.id IN :ids")
    List<Pictograma> buscarPorIds(@Param("ids") List<Long> ids);
    
    @Query("SELECT p FROM Pictograma p WHERE p.id = :id")
    Pictograma buscarPorId(@Param("id") Long id);
    
    @Modifying
    @Query(value = "INSERT INTO pictograma_categoria (pictograma_id, categoria_id) VALUES (:pictogramaId, :categoriaId)", nativeQuery = true)
    void insertarRelacion(@Param("pictogramaId") Long pictogramaId, @Param("categoriaId") Long categoriaId);

    @Query(value = """
    	    SELECT p.* FROM pictogramas p
    	    JOIN pictograma_categoria pc ON p.id = pc.pictograma_id
    	    WHERE pc.categoria_id = :categoriaId
    	    AND (p.usuario_id IS NULL OR p.usuario_id = :usuarioId)
    	""", nativeQuery = true)
    	List<Pictograma> obtenerPictogramasDeCategoriaPorUsuario(
    	    @Param("categoriaId") Long categoriaId,
    	    @Param("usuarioId") Long usuarioId
    	);

    @Query("""
    	    SELECT p FROM Pictograma p
    	    WHERE p.usuario IS NULL OR p.usuario.id = :usuarioId
    	""")
    	List<Pictograma> findPictogramasVisiblesParaUsuario(@Param("usuarioId") Long usuarioId);

    
    
 

}
