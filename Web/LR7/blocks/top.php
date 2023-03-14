<tr> <!-- Голова -->
 <td colspan="2" id="header">
  <h1>Поликлиника</h1>
  <p>
   <img id="h_image" src="images/header.jpg" alt="Шапка сайта">
  </p>
 </td>
</tr>
<tr> <!-- Линия -->
 <td colspan="2"><hr></td>
</tr>
<tr>
 <td> <!-- Меню -->
  <table id="topmenu">
   <tr>
	<td>
	 <a href="index.php">Главная</a>
	</td>
	<td>
	 <a href="reg.php">Регистрация</a>
	</td>
	<td>
	 <a href="articles.php">Статьи</a>
	</td>
	<td>
	 <a href="guestbook.php">Гостевая книга</a>
	</td>
   </tr>
  </table>
 </td> 
 <td id="login"> <!-- Авторизация -->
  <form action="auth.php" name="auth" method="post">
   <table align="right">
	<tr>
	 <td>E-mail:</td>
	 <td>
	  <input type="text" name="email">
	 </td>
	 <td>Пароль:</td>
	 <td>
	  <input type="password" name="password">
	 </td>
	</tr>
	<tr>
	 <td colspan="4" class="rigth_td">
	  <input type="submit" name="button_auth" value="Войти">
	 </td>
	</tr>
   </table>
  </form>
 </td>
</tr>