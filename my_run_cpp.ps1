
$param2 = $args[0] # No of threads int type or "seq" keyword if we have a sequential running

$outputFilePath = $args[1]

$tipMatrice = $args[2]

$suma = 0

for ($i = 0; $i -lt 10; $i++){
    $a = .\main $param2 "$($outputFilePath)_$($i).txt" # rulare class java
    $suma += $a
}
$media = $suma / $i


# Creare fisier .csv
if (!(Test-Path outJ.csv)){
    New-Item outJ.csv -ItemType File
    #Scrie date in csv
    Set-Content outJ.csv 'Tip Matrice,Nr threads,Timp executie'
}

# Append
Add-Content outJ.csv "$($tipMatrice),$($param2),$($media)"