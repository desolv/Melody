Write-Output "`Deploying Melody..."

$cooldown = $env:COOLDOWN
Write-Output "Waiting $cooldown seconds before deploying..."
Start-Sleep -Seconds $cooldown

$deployPath = $env:TARGET.Trim()
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildPath = Join-Path $scriptDir "target"

if (-not (Test-Path $deployPath)) {
    Write-Output "Deploy folder not found. Creating: $deployPath"
    exit 0
}

Write-Output "Searching for built jars..."
$newJar = Get-ChildItem "$buildPath\melody-*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if ($null -eq $newJar) {
    Write-Output "No jar found in $buildPath"
    exit 0
}

$destPath = Join-Path $deployPath "melody.jar"

Write-Output "Deploying..."
Copy-Item $newJar.FullName -Destination $destPath -Force
Write-Output "Deployed as melody.jar -> $deployPath"
exit 0
