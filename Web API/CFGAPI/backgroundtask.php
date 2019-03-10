<?php
include 'connect.php';



if(isset($_POST["lat"]) && !empty($_POST["lat"]) && isset($_POST["long"]) && !empty($_POST["long"]))
{
  $lat=(float)$_POST["lat"];
  $long=(float)$_POST["long"];
  $sql = "SELECT * from station";
  //echo $sql;
  $rows = "";
  $results=$conn->query($sql);
  $rows["status"] = "0";
  while($r = mysqli_fetch_assoc($results)) {
      $slat=(float)$r["lati"];
      $slong=(float)$r["longi"];
      $latdif=$slat-$lat;
      $longdif=$slong-$long;
      $innereuclid=pow($latdif,2)+pow($longdif,2);
      $euclid=sqrt($innereuclid);
      if($euclid<0.0025)
      {
        //echo $euclid."\n";
        $rows["status"] = $r["station"];        
      }
  }
print json_encode($rows);

}
?>