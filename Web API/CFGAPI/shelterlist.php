<?php
include 'connect.php';

    $sql = "SELECT * FROM temp_shelter";
    $results=mysqli_query($conn,$sql);
    
    $rows = array();
    while($r = mysqli_fetch_assoc($results)) {
        $rows[] = $r;
    }
    print json_encode($rows);
?>
