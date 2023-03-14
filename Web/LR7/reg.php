<html>
<head>
 <title>Поликлиника</title>
 <meta http-equiv="Content-type" content="text/html; charset=utf-8">
 <link rel="stylesheet" type="text/css" href="styles/main.css">
</head>
<body link="black" alink="blue" vlink="black">
 <table id="main">
  <?php require_once "blocks/top.php"; ?>
  <tr> <!-- Линия -->
   <td colspan="2"><hr>
  </tr>
  <tr>
   <td colspan="2">
	<table id="content">
	 <tr>
	  <td>
	   <div class="articles">
		<h1> Регистрация </h1>
		<form action="reg.php" name="reg" method="post">
		 <table id="articles_tab">
		  <tr><td>E-mail:</td></tr>
		  <tr><td><input type="text" name="email"></td></tr>
		  <tr><td>Логин:</td></tr>
		  <tr><td><input type="text" name="login"></td></tr>
		  <tr><td>Пароль:</td></tr>
		  <tr><td><input type="password" name="password"></td></tr>
		  <tr><td>Подтверждение пароля:</td></tr>
		  <tr><td><input type="password" name="password_conf"></td></tr>
		  <tr><td><input id="button" type="submit" name="button_reg" value="Зарегистрироваться"></td></tr>
		 </table>
		</form>
	   </div>
	  </td>
	  <td id="banners"> <!-- Баннеры -->
	   <?php require_once "blocks/banners.php"; ?>
	  </td>
	 </tr>
	</table>
   </td>
  </tr>
  <tr>
   <td colspan="2"><hr></td>
  </tr>
  <tr>
   <?php require_once "blocks/footer.php"; ?>
  </tr>
 </table>
</body>
</html>