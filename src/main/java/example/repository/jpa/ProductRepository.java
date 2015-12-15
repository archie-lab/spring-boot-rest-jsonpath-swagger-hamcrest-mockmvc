package example.repository.jpa;

import example.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    Product findProductByName(String name);

    Page<Product> findAll(Pageable pageable);

}
