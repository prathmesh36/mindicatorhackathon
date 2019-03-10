<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "dbupload";
// Create connection
$conn = new mysqli($servername, $username, $password,$dbname);

// Check connection
if ($conn) {
  $image=$_POST["image"];
  $name=$_POST["name"];
  $sql="INSERT INTO imageinfo(name) values('$name')";
  $upload_path="CFGApp/$name.jpg";
  if(mysqli_query($conn,$sql))
  {
    file_put_contents($upload_path,base64_decode($image));
    echo json_encode(array('response'=>'Image Uploaded Successfully'));
  }else
  {
    echo json_encode(array('response'=>'Image Upload Failed'));
  }
}else {
  echo json_encode(array('response'=>'Image Upload Failed'));
}
mysqli_close($conn);

?>
