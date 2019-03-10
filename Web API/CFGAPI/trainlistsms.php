<?php
include 'connect.php';
if(isset($_GET["tstation"]))
{
    $tstation=$_GET["tstation"];
    $sql = "SELECT src,dest,time FROM train WHERE time>=CURRENT_TIME() AND tstation='".$tstation."' LIMIT 1";
    $results=mysqli_query($conn,$sql);
    $rows;
    while($r = mysqli_fetch_assoc($results)) {
        $rows = $r;
    }
    print json_encode($rows);
}
?>
