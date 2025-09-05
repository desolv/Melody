Write-Output "`Deploying Melody..."

Write-Output "Waiting $cooldown seconds before deploying..."
Start-Sleep -Seconds $cooldown

$deployPath = $env:TARGET.Trim()
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildPath = Join-Path $scriptDir "target"

if (-not (Test-Path $deployPath)) {
    Write-Output "Deploy folder not found. Creating: $deployPath"
    exit 0
}

Write-Output "Cleaning deploy folder..."
$oldJars = Get-ChildItem "$deployPath\melody-*.jar" -ErrorAction SilentlyContinue
if ($oldJars) {
    $oldJars | ForEach-Object {
        # Write-Output " - Removing: $($_.Name)"
        Remove-Item $_.FullName -Force
    }
} else {
    Write-Output "No old jars found."
}

Write-Output "Searching for built jars..."
$newJar = Get-ChildItem "$buildPath\melody-*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if ($null -eq $newJar) {
    Write-Output "No jar found in $buildPath"
    exit 0
}

# Write-Output "Found jar: $($newJar.FullName)"

$destPath = Join-Path $deployPath $newJar.Name
Write-Output "Deploying..."
Copy-Item $newJar.FullName -Destination $destPath -Force
Write-Output "Deployed $($newJar.Name) -> $deployPath"
exit 0