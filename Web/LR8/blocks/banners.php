<h3 align="center">Реклама</h3>
<?php
 $banners = getAllBanners();
 for($i=0; $i<count($banners); $i++){
  $code = $banners[$i]["code"];
  include "banner.php";
 }
?>