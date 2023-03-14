<?php require_once "start.php"; ?>
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
	  <td> <!-- Информация об учреждении -->
	   <?php require_once "blocks/main_article.php"; ?>
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