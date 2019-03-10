<?php
include 'connect.php';
$username=$_POST["username"];
$password=$_POST["password"];
$email=$_POST["email"];
$name=$_POST["name"];
$age=$_POST["age"];
$sql = "INSERT INTO user(username,password,email,name,age) VALUES('".$username."','".md5($password)."','".$email."','".$name."','".$age."')";

if(mysqli_query($conn,$sql))
{
  $sql = "SELECT u_id, name FROM user WHERE username='".$username."' ";
  $results=mysqli_query($conn,$sql);
  $arr[]=mysqli_fetch_assoc($results);
  $arr["status"]=1;
  echo json_encode($arr);
}else {
  $arr = array("status" => "0");
  echo json_encode($arr);
}
?>
