package osahner.service

import org.springframework.data.repository.CrudRepository
import osahner.domain.Product

interface ProductRepositoru:CrudRepository<Product,Long> {
}
