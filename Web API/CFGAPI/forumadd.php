<?php
include 'connect.php';
if(isset($_POST["quest"]) && !empty($_POST["quest"]) && isset($_POST["detail"]) && !empty($_POST["detail"]))
{
  $quest=$_POST["quest"];
  $detail=$_POST["detail"];
  $sql = "INSERT INTO forum_question VALUES('','".$quest."','".$detail."','Prathamesh Mhapsekar','prathmesh36@yahoo.com','10/07/18 05:05:50',1,0,0,'perl',0)";
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