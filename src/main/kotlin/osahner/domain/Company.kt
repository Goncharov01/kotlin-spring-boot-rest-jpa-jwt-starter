package osahner.domain

import javax.persistence.*


@Entity @Table(name = "company")
data class Company(
  var name: String = "",

  @OneToMany(mappedBy = "company", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
  var products: List<Product> = emptyList(),


  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "company_id")
  var user: User? = null

  ){

  override fun toString(): String{
    return "{name: ${this.name}, products: ${products.map { it->it.name }},user: ${this.user?.username}}";
  }
}

