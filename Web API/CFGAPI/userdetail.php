<?php
include 'connect.php';
$id=$_POST["id"];

$sql = "SELECT * FROM user WHERE u_id='".$id."'";
$results=mysqli_query($conn,$sql);
$row[]=mysqli_fetch_assoc($results);
$row["status"]=0;

if(mysqli_num_rows($results)==1)
{
  $row["status"]=1;
  echo json_encode($row);   
}else {
  echo json_encode($row);
}
?>
