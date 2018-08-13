package osahner.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import osahner.domain.Product
import osahner.domain.User
import osahner.service.ProductRepositoru
import osahner.service.UserRepository
import java.util.*

@RestController
@RequestMapping("/api")
class IndexController {
  @Autowired
  lateinit var productRepository: ProductRepositoru
@Autowired
lateinit var userRepository: UserRepository
  @GetMapping(value = ["", "/", "/test"])
  fun helloWorld() = "Pong!"

  @GetMapping(value = ["/required"])
  fun helloRequiredWorld(@RequestParam(value = "msg", required = true) msg: String) = "Echo \"$msg\"!"

  @GetMapping(value = ["/restricted"])
  @PreAuthorize("hasAuthority('STANDARD_USER')")
  fun helloRestrictedWorld() = "Pong!"

  @GetMapping (value= "/autorize")
 fun autorize(): Authentication? {
    val authentication = SecurityContextHolder.getContext().authentication
    val autorize = ArrayList<String>()
    authentication.authorities.forEach { role -> autorize.add(role.authority.toString()) }
    return authentication



  }
@GetMapping (value="/username")
//дабавляем метод гет маппинг
fun username ():String {
  //дабавляем функцию username типа стринг
 val authentication= SecurityContextHolder.getContext().authentication
   //дабавляем переменную аутефикацион который ровняется SecurityContextHolder.getContext
  return authentication.name
    //просим написать аутификацион наме

}
@GetMapping (value="/roles")
//дабавляем метод гет маппинг
fun roles ():ArrayList<String>{
  //дабавляем функцию релес и пустой лист типа стринг
  val authentication= SecurityContextHolder.getContext().authentication
    //дабавляем функцию аутификацион которая ронвна SecurityContextHolder.getContext
  val list=ArrayList<String>()
    //дабавляем переменную лист которая сосздают пустой лист типа стринг
  authentication.authorities.forEach{grantetautority -> list.add(grantetautority.authority.toString())}
    //
  return list
    //просим написать list
}
@GetMapping (value="/product")
//дабавляем метод гет маппинг
fun product (): ArrayList<Product> {
  //дабавляем функцию продукт и пустой лист продукт
val authentication=SecurityContextHolder.getContext().authentication
  //дабавляем переменную аутификацион которая ровно authentication=SecurityContextHolder.getContext
 val user: Optional<User> = userRepository.findByUsername(authentication.name)
   //дабавляем переменну юзер которая ровнв оптинал юзер .....
val productlist=ArrayList<Product>()
  //дабавляем переменную продукт которая равна пустому листу продукт
//  user.get().products.forEach{product->productlist.add(product)}
    //
  return productlist
    //просим написать продукт
}

}
