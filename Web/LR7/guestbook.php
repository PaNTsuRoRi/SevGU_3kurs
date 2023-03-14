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
	   <div class="article">
		<h1> Гостевая книга </h1>
		<table>
		 <tr><td>Здесь вы можете оставить сообшение.</td></tr>
		 <tr>
		  <td>
		   <form action="guestmess.php" name="guestmess" method="post">
		    <table>
		     <tr><td>Логин:</td></tr>
		     <tr><td><input type="text" name="login"></td></tr>
		     <tr><td>Сообщение:</td></tr>
		     <tr><td><input class="message-box" type="text" name="message"></td></tr>
		     <tr><td><input id="button" type="submit" name="button_mess" value="Сохранить"></td></tr>
		    </table>
		   </form>
		  </td>
		 </tr>
		</table>
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