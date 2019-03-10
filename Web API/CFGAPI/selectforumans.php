<?php
include 'connect.php';
if(isset($_GET["qid"]) && !empty($_GET["qid"]))
{
  $ques_id=$_GET["qid"];
  $sql = "SELECT * FROM forum_answer WHERE question_id='".$ques_id."'";
  $results=$conn->query($sql);
  $rows = array();
  while($r = mysqli_fetch_assoc($results)) {
      $rows[] = array("id"=>$r["a_id"],"name"=>$r["a_name"],"date"=>$r["a_datetime"],"text"=>$r["a_answer"]);
  }

print json_encode($rows);
}
//echo $sql;


?>
