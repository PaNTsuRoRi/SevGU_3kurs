<?php
 $articles = getAllArticles();
 for ($i=0; $i<count($articles); $i++){
  $id = $articles[$i]["id"];
  $title = $articles[$i]["title"];
  $intro_text = $articles[$i]["intro_text"];
  include "intro_article.php";
 }
?>