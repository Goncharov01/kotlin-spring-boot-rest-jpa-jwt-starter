package osahner.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity @Table(name = "product")
data class Product(
    var name: String = "",

 @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    var company: Company? = null,

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 var id: Long = 0
){
 override fun toString(): String{
  return "{name: ${name}, company: ${company?.name}}"
 }
}
