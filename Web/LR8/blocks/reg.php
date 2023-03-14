<?php
 if(!empty($_POST["button_reg"])){
  $email = htmlspecialchars($_POST["email"]);
  $password_1 = htmlspecialchars($_POST["password_1"]);
  $password_2 = htmlspecialchars($_POST["password_2"]);
  if(strlen($email)<3) $success = false;
  elseif (strlen($password_1)<3) $success = false;
  elseif ($password_1 != $password_2) $success = false;
  else $success = addUser($email, md5($password_1));
  if(!$success) $alert = "Ошибка при регистрации пользователя!";
  else $alert = "Вы успешно зарегистрировались!";
  include "alert.php";
 }
?>
<h1 align="center">Регистрация</h1>
<form name="reg" action="" method="post">
 <table>
  <tr><td>E-mail:</td></tr>
  <tr><td><input type="text" name="email"></td></tr>
  <tr><td>Пароль:</td></tr>
  <tr><td><input type="password" name="password_1"></td></tr>
  <tr><td>Подтверждение пароля:</td></tr>
  <tr><td><input type="password" name="password_2"></td></tr>
  <tr><td><input id="button" type="submit" name="button_reg" value="Зарегистрироваться"></td></tr>
 </table>
</form>