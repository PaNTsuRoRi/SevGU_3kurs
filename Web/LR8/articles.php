<?php 
 require_once "start.php";
 $article = getArticle($_GET["id"]);
 $id = $article["id"];
 $title = $article["title"];
 $full_text = $article["full_text"];
?>
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
	   <h1 align="center">Статьи</h1>
	   <?php require_once "blocks/full_article.php"; ?>
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