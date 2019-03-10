<?php
include 'connect.php';
if(isset($_GET["tstation"]))
{
    $tstation=$_GET["tstation"];
    $sql = "SELECT * FROM train WHERE time>=CURRENT_TIME() AND tstation='".$tstation."' ";
    $results=mysqli_query($conn,$sql);
    $rows = array();
    while($r = mysqli_fetch_assoc($results)) {
        $rows[] = $r;
    }
    $sql = "SELECT * FROM train WHERE time<CURRENT_TIME() AND tstation='".$tstation."' ";
    $results=mysqli_query($conn,$sql);
    //$rows = array();
    while($r = mysqli_fetch_assoc($results)) {
        $rows[] = $r;
    }
    print json_encode($rows);
}
?>