<?php
include 'connect.php';
$sql = "SELECT u_id,name, profile_pic_url,rating FROM user ORDER BY rating DESC";
$results=mysqli_query($conn,$sql);
$rows = array();
while($r = mysqli_fetch_assoc($results)) {
    $rows[] = $r;
  }
  print json_encode($rows);
?>
