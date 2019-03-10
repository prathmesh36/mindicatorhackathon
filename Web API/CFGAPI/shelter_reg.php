<?php
include 'connect.php';
$mobile=$_POST["mobile"];
$name=$_POST["name"];
$facility=$_POST["facility"];
$lati=$_POST["lati"];
$longi=$_POST["longi"];


$sql = "INSERT INTO temp_shelter(name,facility,lati,longi,contact) VALUES('".$name."','".$facility."','".$lati."','".$longi."','".$mobile."')";
$arr = array("status" => "0");
if(mysqli_query($conn,$sql))
{
  $arr["status"]=1;
  echo json_encode($arr);
}else {
  echo json_encode($arr);
}
?>