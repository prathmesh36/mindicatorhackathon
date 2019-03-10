<?php

if(isset($_GET["lati"]) && !empty($_GET["lati"]) && isset($_GET["longi"]) && !empty($_GET["longi"]))
{
    $curl = curl_init();
    $lat=$_GET["lati"];
    $long=$_GET["longi"];
    curl_setopt_array($curl, array(
    CURLOPT_URL => "https://transit.api.here.com/v3/stations/by_geocoord.json?app_id=PUT_YOUR_APPID_HERE&center=".$lat.",".$long."&radius=350&max=1",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => "",
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => "GET",
    CURLOPT_POSTFIELDS => "",
    CURLOPT_HTTPHEADER => array(
        "Postman-Token: 1839c2ba-6b51-4424-b480-8636ef120014",
        "cache-control: no-cache"
    ),
    ));

    $response = curl_exec($curl);
    $err = curl_error($curl);

    curl_close($curl);

    if ($err) {
    echo "cURL Error #:" . $err;
    } else {

    //echo $response;
    $response=json_decode($response,true);
    $busstops= $response["Res"]["Stations"]["Stn"];
    foreach($busstops as $busstop)
    {
        $rows = array("name"=>$busstop["name"],"lat"=>$busstop["x"],"long"=>$busstop["y"],"busno"=>$busstop["Transports"]["Transport"]);      
    }
    print json_encode($rows);
    //$station=$response["Stations"]["Stn"];
    //echo $station;
    }
}