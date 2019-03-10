<?php
include 'connect.php';
if(isset($_GET["uid"]) && !empty($_GET["uid"]) && isset($_GET["rush"]) && !empty($_GET["rush"]) && isset($_GET["curr_loc_stat"]) && !empty($_GET["curr_loc_stat"]) && isset($_GET["id"]) && !empty($_GET["id"]))
{
  $rush=$_GET["rush"];
  $currlocstat=$_GET["curr_loc_stat"];
  $id=$_GET["id"];
  $uid=$_GET["uid"];
  date_default_timezone_set('Asia/Calcutta'); 
  $date = date('d/m/Y h:i:s a', time());
  $sql = "UPDATE train SET rush".$rush."=rush".$rush."+1, currstation='".$currlocstat."',updatetime='".$date."' WHERE id=".$id;
  //echo $sql;
  $rows = "";
  if($conn->query($sql))
  {
    @$rows["status"] = "1";
    $sql = "UPDATE user SET rating=rating+1 WHERE u_id=".$uid;
    if($conn->query($sql))
    {
      @$rows["status"] = "1";
    }
  }
  else{
    @$rows["status"] = "0";
  }

print json_encode($rows);
}
//echo $sql;

?>