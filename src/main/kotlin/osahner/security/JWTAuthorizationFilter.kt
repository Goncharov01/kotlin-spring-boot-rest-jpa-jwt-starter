package osahner.security

import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import osahner.config.SecurityConstants.HEADER_STRING
import osahner.config.SecurityConstants.SECRET
import osahner.config.SecurityConstants.TOKEN_PREFIX
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {
  private var log = LoggerFactory.getLogger(JWTAuthorizationFilter::class.java)

  @Throws(IOException::class, ServletException::class)
  override fun doFilterInternal(
    req: HttpServletRequest,
    res: HttpServletResponse,
    chain: FilterChain
  ) {
    //достали из hedr со значением аутаризейшен
    val header = req.getHeader(HEADER_STRING)
      // Проверели что он не пустой и что начинается со слова Beare
    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      // В случаи успеха.пустили по цепочке дальше
      chain.doFilter(req, res)
      //выйти из цикла
      return
    }
      //по данным из запроса получили объект аутификации
    val authentication = getAuthentication(req)
    //передали контекс сикюрити аутификацию
    SecurityContextHolder.getContext().authentication = authentication
    //передали дальше по цепочке
    chain.doFilter(req, res)
  }
  //метод принемает запрос а выдаёт токен
  private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
    //получаем токен из строчки хедера аутаризатион
    val token = request.getHeader(HEADER_STRING)
      //если токен не пустой
    return if (token != null) {
      //с помощью библеотеки начинаем разбирать токен на части
      val claims = Jwts.parser()
        //убираем секрет
        .setSigningKey(SECRET)
        //убираем токен префикс биар
        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
        //переменная user будет равна значению полю суб из токена
      val user = claims
        .body
        .subject
        //создаем пустой список для ролей
      val authorities = ArrayList<GrantedAuthority>()
        //берем поле ауф итапретируем как MutableList берем из этого листа роль и переписываем ее в список ауторитис
      (claims.body["auth"] as MutableList<*>).forEach { role -> authorities.add(SimpleGrantedAuthority(role.toString())) }
//если юзер не пустой
      if (user != null) {
        //создаем экземпляр класса куда мы перемещаем имя и список ролей
        UsernamePasswordAuthenticationToken(user, null, authorities)
      } else null
    } else null
  }
}
