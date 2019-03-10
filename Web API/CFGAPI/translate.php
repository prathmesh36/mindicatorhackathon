<?php

if(isset($_GET["query"]) && !empty($_GET["query"])) 
{
    $query=$_GET["query"];
        //echo $squery."<br>";
        $curl = curl_init();
        curl_setopt_array($curl, array(
        CURLOPT_URL => "https://translate.yandex.net/api/v1.5/tr.json/translate?key=PUTYOURKEYHERE&text=".$query."&lang=en-mr",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_ENCODING => "",
        CURLOPT_MAXREDIRS => 10,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_HTTPHEADER => array(
            "cache-control: no-cache",
            "postman-token: b300a7e4-b0a0-ce26-1f2d-e92827ca11a5"
        ),
        ));

        $response = curl_exec($curl);
        //print $response;
        $err = curl_error($curl);

        curl_close($curl);
        if ($err) {
        echo "cURL Error #:" . $err;
        } else {
        echo $response;
        }
}