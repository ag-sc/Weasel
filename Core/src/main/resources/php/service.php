<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 

<?php
$message = "Enter text here...";
if(isset($_POST['inputArea'])){
	$message = $_POST['inputArea'];
}	
?>

<html>
	<div align="center"> 
    <head>
        <title>Veasel Webservice</title>
    </head>
    <body>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<h2>Veasel webservice test</h2>
		
		
        <form action="service.php" method="POST">
            <textarea rows="10" cols="80" name="inputArea"><?= $message ?></textarea>
			<br>
			<input type="submit">
        </form>
		
    </body>
</html>

<?php
	// Validate input
	$result = "";
	$timeout = 6000;
	if(!isset($_POST['inputArea'])){
		echo "Timeout is currently set to " . $timeout . " seconds.";
		exit(-1);
	}

	$input = $_POST['inputArea'];

	$url = 'http://localhost:8080/formatted-output';
	$data = array('input' => $input);

	// use key 'http' even if you send the request to https://...
	$options = array(
		'http' => array(
			'header'  => "Content-type: application/x-www-form-urlencoded",
			'method'  => 'POST',
			'content' => http_build_query($data),
			'timeout' => $timeout
		),
	);
	$context  = stream_context_create($options);
	$result = file_get_contents($url, false, $context);

	echo(nl2br($result));
?>



















