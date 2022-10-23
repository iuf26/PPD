$test = 1;
Get-ChildItem -Path "D:/FACULTATE-AN3-SEM1/PPD/Lab1-output" |
Foreach-Object {

#Do something with $_.FullName
   
 $original = "D:/FACULTATE-AN3-SEM1/PPD/Lab1-output/Test$($test)/$($test).txt"
 
Get-ChildItem -Path $_.FullName | Foreach-Object {
  
    if($_.Name -ne "$($test).txt" ) {

            Get-ChildItem -Path $_.FullName | Foreach-Object{
                 Write-Host "$($original) diff  $($_.FullName)"
                    diff (cat $original) (cat $_.FullName)
                   
            }   

    }


}

    $test = $test + 1
}