<div class="article">
 <h2><?php echo $title; ?><h2>
 <table> 
  <tr>
   <td>
    <p class="articles_img">
     <img src="images/article_<?php echo $id; ?>.jpg" alt="<?php echo $title; ?>">
    </p>
   </td>
   <td>
    <?php echo $intro_text; ?>
    <p>
     <a href="articles.php?id=<?php echo $id ?>">Читать дальше</a>
    </p>
   </td>
  </tr>
 </table>
</div>
<hr>