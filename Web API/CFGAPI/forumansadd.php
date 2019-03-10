<?php
include 'connect.php';
if(isset($_POST["id"]) && !empty($_POST["id"]) && isset($_POST["comment"]) && !empty($_POST["comment"]))
{
  $id=$_POST["id"];
  $comment=$_POST["comment"];
  $sql = "INSERT INTO forum_answer VALUES(".$id.",1,'Prathamesh Mhapsekar','prathmesh36@yahoo.com','".$comment."','10/07/18 05:05:50',1)";
  //echo $sql;
  $rows = "";
  if($conn->query($sql))
  {
    $rows["status"] = "1";
  }
  else{
    $rows["status"] = "0";
  }

print json_encode($rows);
}
//echo $sql;


?>