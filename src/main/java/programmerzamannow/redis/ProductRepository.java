package programmerzamannow.redis;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends KeyValueRepository<Product, String> {
}
