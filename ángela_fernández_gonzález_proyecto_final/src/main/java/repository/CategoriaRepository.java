package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria,Integer>{

	// Obtener categorias generales (los que no tienen usuario asociado)
    @Query("SELECT c FROM categorias c WHERE c.usuario_id IS NULL")
    List<Categoria> findAllGenerales();

    // Obtener categorias personalizadas de un usuario concreto
    @Query("SELECT c FROM categorias c WHERE c.usuario.id = :usuarioId")
    List<Categoria> findAllPersonalizados(@Param("usuarioId") Long usuarioId);

}
