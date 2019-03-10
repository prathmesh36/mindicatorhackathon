<?php
include 'connect.php';
if(isset($_GET["uid"]) && !empty($_GET["uid"]))
{
  $user_id=$_GET["uid"];
  $sql = "SELECT * FROM forum_question WHERE id='".$user_id."' ORDER BY reply DESC";
  $results=$conn->query($sql);
  $rows = array();
  while($r = mysqli_fetch_assoc($results)) {
      $rows[] = array("id"=>$r["topic_id"],"name"=>$r["topic"],"date"=>$r["datetime"],"reply"=>$r["reply"],"view"=>$r["view"]);
  }
}
else{
  $sql = "SELECT * FROM forum_question ORDER BY reply DESC";
  $results=$conn->query($sql);
  $rows = array();
  while($r = mysqli_fetch_assoc($results)) {
      $rows[] = array("id"=>$r["topic_id"],"name"=>$r["topic"],"date"=>$r["datetime"],"reply"=>$r["reply"],"view"=>$r["view"],"uname"=>$r["name"],"text"=>$r["detail"]);
  }
}
//echo $sql;

print json_encode($rows);

?>
