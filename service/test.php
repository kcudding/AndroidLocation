#!/usr/bin/php-cgi
<html>
<title>Location Data</title>
<body>

<?php 

// https://code-boxx.com/php-url-parts/
// https://www.geeksforgeeks.org/php-parse_str-function/

$serverdate = date("Y-m-d")."T".date("H:i:s");

parse_str($_SERVER['QUERY_STRING']); 
$txt = $serverdate.",".$capturedate.",".$id.",".$imei.",".$mei.",".$lat.",".$long.",".$alt;
echo $txt;

// https://www.w3schools.com/php/php_file_create.asp
// https://stackoverflow.com/questions/24972424/create-or-write-append-in-text-file

$filename = $id.".txt";
if (!file_exists ($filename)) {
	$myfile = file_put_contents($filename, "#serverdate,capturedate,id,imei,mei,lat,long,alt".PHP_EOL , FILE_APPEND | LOCK_EX);
}

$myfile = file_put_contents($filename, $txt.PHP_EOL , FILE_APPEND | LOCK_EX);
fclose($myfile);


?>

</body>
</html>