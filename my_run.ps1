$param1 = $args[0] # path Main.class java
#Write-Host $param1

$param2 = $args[1] # No of threads int type or "seq" keyword if we have a sequential running
#Write-Host $param2

$param3 = $args[2] # No of runs
#Write-Host $param2
$outputFilePath = $args[3]

$tipMatrice = $args[4]

$suma = 0

for ($i = 0; $i -lt $param3; $i++){
    $a = java -cp $param1 Main $param2 "$($outputFilePath)_$($i).txt" # rulare class java
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