<?php
 if($_SESSION["error_auth"]){
  unset($_SESSION["error_auth"]);
  $alert="Неверные e-mail и/или пароль!";
  include "alert.php";
 }
?>
<form name="auth" action="auth.php" method="post">
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