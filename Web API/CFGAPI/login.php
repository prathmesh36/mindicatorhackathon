<?php
include 'connect.php';
$username=$_POST["username"];
$password=$_POST["password"];


$sql = "SELECT u_id, name, gender, age, username FROM user WHERE username='".$username."' AND password='".md5($password)."' ";
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