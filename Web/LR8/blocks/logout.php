<?php
 session_start();
 unset($_SESSION["email"]);
 unset($_SESSION["password"]);
 header("Location: ".$_SERVER["HTTP_REFERER"]);
 exit;
?>