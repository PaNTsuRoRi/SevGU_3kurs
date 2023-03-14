<h1 align="center">Гостевая книга</h1>
<h2>Добавить запись<h2>
<form name="guestmess" action="" method="post">
<table>
 <tr><td>Имя:</td></tr>
 <tr><td><input type="text" name="name"></td></tr>
 <tr><td>Комментарий:</td></tr>
 <tr><td><input type="text" name="comment"></td></tr>
 <tr><td><input id="button" type="submit" name="button_guestbook" value="Добавить"></td></tr>
</table>
</form>
<h2>Записи в гостевой книге</h2>
<div>
 <?php
  if(!empty($_POST["button_guestbook"])){
   $name = htmlspecialchars($_POST["name"]);
   $comment = htmlspecialchars($_POST["comment"]);
   if((strlen($name)<3)||(strlen($comment)<3)) $success = false;
   else $success = addGuestBookComment($name, $comment);
   if(!$success){
	$alert = "Ошибка при добавлении новой записи!";
	include "alert.php";
   }
  }
  $comments = getGuestBookComments();
  for ($i=0; $i<count($comments); $i++){
   $name = $comments[$i]["name"];
   $comment = $comments[$i]["comment"];
   include "guestbook_comment.php";
  }
 ?>
</div>