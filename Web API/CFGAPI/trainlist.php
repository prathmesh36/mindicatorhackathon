<?php
include 'connect.php';
if(isset($_GET["tstation"]))
{
    $tstation=$_GET["tstation"];
    $sql = "SELECT * FROM train WHERE (TIMEDIFF(time,CURRENT_TIME())>TIME('00:01:00') AND TIMEDIFF(time,CURRENT_TIME())<TIME('00:59:00')) OR (TIMEDIFF(time,CURRENT_TIME())<TIME('-00:01:00') AND TIMEDIFF(time,CURRENT_TIME())>TIME('-00:59:00')) AND tstation='".$tstation."' ";
    $results=mysqli_query($conn,$sql);
    $rows = array();
    while($r = mysqli_fetch_assoc($results)) {
        $rows[] = $r;
    }
    print json_encode($rows);
}
?>
