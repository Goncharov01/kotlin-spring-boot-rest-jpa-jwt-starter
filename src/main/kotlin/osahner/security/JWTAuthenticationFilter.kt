package osahner.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import osahner.config.SecurityConstants.EXPIRATION_TIME
import osahner.config.SecurityConstants.HEADER_STRING
import osahner.config.SecurityConstants.SECRET
import osahner.config.SecurityConstants.TOKEN_PREFIX
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(private val _authenticationManager: AuthenticationManager) :
  UsernamePasswordAuthenticationFilter() {
  //логирование введение журнала собыйтий которые происходят с классом JWTAuthenticationFilter
  private var log = LoggerFactory.getLogger(JWTAuthenticationFilter::class.java)
//выбросить исключение ошибка аутификации
  @Throws(AuthenticationException::class)
  //переопределение метода, попытка  аутификации
  override fun attemptAuthentication(
  // req-запрос
    req: HttpServletRequest,
    //res-ответ
    res: HttpServletResponse?
//выводим объект класса аутификацион внутри которого поле user
  ): Authentication {
  // try попробовать
    return try {
      //переменная creds/ ObjectMapper Преобразует одни объекты в другие
      val creds = ObjectMapper()
        // статический метод принимает в себя запрос и преобразует входящий потом inputStream в класс user
        .readValue(req.inputStream, osahner.domain.User::class.java)
        //метод aurhetication принимает в себя username, password и autihecationToken
      _authenticationManager.authenticate(
        //создаём экземпляр класса UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken(
          //объект user из objectMapera достаем поле username
          creds.username,
          //достаем поле password из объекта
          creds.password,
          //пустой список ролей
          ArrayList<GrantedAuthority>()
        )
      )
      // в случаи не улачи выбросить исключение
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }
//в случаи неудачи выбрасываются два типа исключения IORxception and ServletException
  @Throws(IOException::class, ServletException::class)
  override fun successfulAuthentication(
  //req-запрос
    req: HttpServletRequest,
    //res-ответ
    res: HttpServletResponse,
    //цепочка фильтров
    chain: FilterChain?,
    //объект Authentication
    auth: Authentication
  ) {
  //изменяемый список
    val claims: MutableList<String> = mutableListOf()
      //дабавляем в изменяемый список все роли которые есть у данного объекта
    auth.authorities!!.forEach { a -> claims.add(a.toString()) }
      // создаем токен с помощью библеотеки Jwts
    val token = Jwts.builder()
      // заголовок-поле sub записывается имя Usera
        .setSubject((auth.principal as User).username)
      //в поле auth записываем список ролей
      .claim("auth", claims)
      // к текущему времени прибовляем срок действие. именно столько будет действовать токен
      .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
      //электронная подпись указываем алгоритм шифрование HS512 который будет шифровать наш текст
      .signWith(SignatureAlgorithm.HS512, SECRET)
      // убрать пробелы переносы и.т.д
      .compact()
  //дабавить в заголовок autharization + token
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token)
  }
}
