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
	 <a href="article.php">Статьи</a>
	</td>
	<td>
	 <a href="guestbook.php">Гостевая книга</a>
	</td>
   </tr>
  </table>
 </td> 
 <td id="login"> <!-- Авторизация -->
  <?php
   if(checkUser($_SESSION["email"],$_SESSION["password"]))
	require_once "blocks/user_panel.php";
   else require_once "blocks/auth_form.php";
  ?>
 </td>
</tr>